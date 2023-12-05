package com.example.ui.MainActivityPackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.ui.LoginActivity;
import com.example.ui.MainActivity;
import com.example.ui.Quiz.GiftActivity;
import com.example.ui.Model.UserModel;
import com.example.ui.R;
import com.example.ui.SettingPackage.CheckPriorityActivity;
import com.example.ui.SettingPackage.EditInfoActivity;
import com.example.ui.SettingPackage.InstructionActivity;
import com.example.ui.SettingPackage.LanguageActivity;
import com.example.ui.SettingPackage.PrivacyActivity;
import com.example.ui.TicketHandler.BoughtTicketActivity;
import com.example.ui.databinding.FragmentSettingBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    SweetAlertDialog sweetAlertDialog;

    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        MainActivity.profilePicture = result;
                        Glide.with(requireActivity())
                                .load(MainActivity.profilePicture)
                                .into(binding.navHeader.imageProfile);
                        FirebaseStorage.getInstance().getReference().child("images/" + MainActivity.currentUser.getId())
                                .putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("UI", "Upload profile picture successfully!");
                                        sweetAlertDialog.dismiss();
                                        Toast.makeText(requireActivity(), "Update successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("UI", "Upload profile picture failed!");
                                        sweetAlertDialog.dismiss();
                                        Toast.makeText(requireActivity(), "Upload failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.d("UI", "Get image failed!");
                        Toast.makeText(requireActivity(), "Upload failed!", Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.dismiss();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        sweetAlertDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);

        getUser();

        if (MainActivity.profilePicture != null) {
            if (this.getContext() != null) {
                Glide.with(this.getContext())
                        .load(MainActivity.profilePicture)
                        .into(binding.navHeader.imageProfile);
            }
        }

        ((MainActivity) requireActivity()).getSupportActionBar().hide();

        binding.navHeader.username.setText(MainActivity.currentUser.getName());
        binding.navHeader.email.setText(MainActivity.currentUser.getEmail());

        binding.navHeader.editInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditInfoActivity.class));
            }
        });
        binding.bookedTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BoughtTicketActivity.class));
            }
        });
        binding.exchangedGiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), GiftActivity.class).putExtra("type", "exchanged"));
            }
        });
        binding.voucherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CheckPriorityActivity.class));
            }
        });

        sharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);
//        nightMode = false;
        binding.nightModeSwitch.setChecked(nightMode);
        binding.nightModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean newNightMode = !nightMode; // Toggle the night mode

                AppCompatDelegate.setDefaultNightMode(
                        newNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

                // Save the night mode state in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("nightMode", newNightMode);
                editor.apply();

                // Recreate the fragment manager to apply the changes immediately
                if (getActivity() != null) {
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, SettingFragment.this);
                    transaction.commit();
                }
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        gsc = GoogleSignIn.getClient(this.requireContext(), gso);

        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("User").document(MainActivity.currentUser.getId())
                        .update("signIn", false)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                gsc.signOut();
                                FirebaseAuth.getInstance().signOut();
                                MainActivity.profilePicture = null;
                                MainActivity.currentUser = null;
                                requireActivity().finishAffinity();
                                requireActivity().finishAndRemoveTask();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                requireActivity().finish();
                            }
                        });
            }
        });

        binding.navHeader.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.show();
                getImage.launch("image/*");

            }
        });

        binding.fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tuanduc.nguyen1210/")));
            }
        });

        binding.instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/hhhlsciuuu/")));
            }
        });

        binding.twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/")));
            }
        });

        binding.instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), InstructionActivity.class));
            }
        });

        binding.deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Are you sure?")
                        .setContentText("You won't be able to recover this account!")
                        .setConfirmText("Yes, delete it!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                FirebaseFirestore.getInstance().collection("User").document(MainActivity.currentUser.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete();
                                                gsc.signOut();
                                                FirebaseAuth.getInstance().signOut();
                                                MainActivity.profilePicture = null;
                                                MainActivity.currentUser = null;
                                                requireActivity().finishAffinity();
                                                requireActivity().finishAndRemoveTask();
                                                sweetAlertDialog.dismiss();
                                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                                requireActivity().finish();
                                            }
                                        });
                            }
                        })
                        .showCancelButton(true)
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setCancelButtonBackgroundColor(Color.parseColor("#FFA5A5A5"))
                        .setConfirmButtonBackgroundColor(R.color.red)
                        .show();
            }
        });

        binding.securityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrivacyActivity.class));
            }
        });

        binding.languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LanguageActivity.class));
            }
        });

        return binding.getRoot();
    }

    protected void getUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            Comment this to fix bug of current user not loaded when from edit info to setting fragment
//            currentUser = new UserModel(Uid, null, null, null);
            FirebaseFirestore.getInstance().collection("User").document(Uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            MainActivity.currentUser = documentSnapshot.toObject(UserModel.class);
                            assert MainActivity.currentUser != null;
                            MainActivity.currentUser.setId(Uid);
                            getProfilePicture();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "User Failed!", Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismiss();
                        }
                    });
        }
    }

    public void getProfilePicture() {
        FirebaseStorage.getInstance().getReference().child("images/" + MainActivity.currentUser.getId()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        MainActivity.profilePicture = uri;
                        sweetAlertDialog.dismiss();
                        Log.d("UI", "Get profile picture");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sweetAlertDialog.dismiss();
                        Log.d("UI", e.getMessage());
                    }
                });
    }
//    private void applyDayNight(boolean isNightMode) {
//        int nightModeFlag = isNightMode ?
//                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
//
//        AppCompatDelegate.setDefaultNightMode(nightModeFlag);
//        getDelegate().applyDayNight();
//    }
}