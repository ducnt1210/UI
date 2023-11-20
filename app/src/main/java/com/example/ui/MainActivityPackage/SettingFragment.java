package com.example.ui.MainActivityPackage;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ui.EditInfoActivity;
import com.example.ui.LoginActivity;
import com.example.ui.MainActivity;
import com.example.ui.R;
import com.example.ui.databinding.FragmentSettingBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
                                        Log.d("FinancialApp", "Upload profile picture successfully!");
                                        Toast.makeText(requireActivity(), "Update successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("FinancialApp", "Upload profile picture failed!");
                                        Toast.makeText(requireActivity(), "Update successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        if (MainActivity.profilePicture != null) {
            if (this.getContext() != null) {
                Glide.with(this.getContext())
                        .load(MainActivity.profilePicture)
                        .into(binding.navHeader.imageProfile);
            }
        }

        binding.navHeader.username.setText(MainActivity.currentUser.getName());
        binding.navHeader.email.setText(MainActivity.currentUser.getEmail());

        binding.navHeader.editInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditInfoActivity.class));
            }
        });

        sweetAlertDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);

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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/")));
            }
        });

        binding.twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/")));
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

        return binding.getRoot();
    }
}