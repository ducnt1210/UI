package com.example.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ui.Model.UserModel;
import com.example.ui.databinding.ActivityEditInfoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditInfoActivity extends AppCompatActivity {
    ActivityEditInfoBinding binding;
    SweetAlertDialog sweetAlertDialog;
    UserModel tempUser;
    FirebaseStorage storage;
    String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
    Uri tempImage = null;
    ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        tempImage = result;
                        Glide.with(EditInfoActivity.this)
                                .load(tempImage)
                                .into(binding.imageProfile);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditInfoBinding.inflate(getLayoutInflater());
        setTitle("Edit Info");

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);

        storage = FirebaseStorage.getInstance();

        tempUser = MainActivity.currentUser;
        binding.userName.setText(tempUser.getName());
        binding.userNumber.setText(tempUser.getNumber());
        binding.userEmail.setText(tempUser.getEmail());

        if (MainActivity.profilePicture != null) {
            Glide.with(this)
                    .load(MainActivity.profilePicture)
                    .into(binding.imageProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_profile_picture)
                    .into(binding.imageProfile);
        }

        binding.imageProfile.setOnClickListener(v -> {
            getImage.launch("image/*");
        });

        binding.updateInformationButton.setOnClickListener(v -> {
            sweetAlertDialog.show();
            updateInformation();
//            Quick fix, should let the MainActivity load before changing to SettingFragment
            MainActivity.currentUser = tempUser;
//            MainActivity.profilePicture = tempImage;
        });

        binding.changePasswordButton.setOnClickListener(v -> {
            sweetAlertDialog.show();
            updatePassword();
        });


        setContentView(binding.getRoot());
    }

    private void updatePassword() {
        String password = binding.changedPassword.getText().toString();
        String cf_password = binding.changedPasswordCf.getText().toString();
        if (password.length() == 0) {
            sweetAlertDialog.dismiss();
            binding.changedPassword.setError("Empty");
            return;
        }
        if (cf_password.length() == 0) {
            sweetAlertDialog.dismiss();
            binding.changedPasswordCf.setError("Empty");
            return;
        }
        if (!password.equals(cf_password)) {
            sweetAlertDialog.dismiss();
            binding.changedPasswordCf.setError("Password does not match!");
            return;
        }
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(password)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        sweetAlertDialog.dismiss();
                        Toast.makeText(EditInfoActivity.this, "Password update!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditInfoActivity.this, MainActivity.class).putExtra("FragmentID", "SettingFragment"));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sweetAlertDialog.dismiss();
                        Toast.makeText(EditInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditInfoActivity.this, MainActivity.class).putExtra("FragmentID", "SettingFragment"));
                        finish();
                    }
                });
    }

    private void updateInformation() {
        if (binding.userName.getText().toString().isEmpty()) {
            binding.userName.setError("Name cannot be empty");
            sweetAlertDialog.dismiss();
            return;
        }
        if (binding.userNumber.getText().toString().isEmpty()) {
            binding.userNumber.setError("Number cannot be empty");
            sweetAlertDialog.dismiss();
            return;
        }
        if (!binding.userNumber.getText().toString().matches(allCountryRegex)) {
            binding.userNumber.setError("Invalid number");
            sweetAlertDialog.dismiss();
            return;
        }

        tempUser.setName(binding.userName.getText().toString());
        tempUser.setNumber(binding.userNumber.getText().toString());

        FirebaseFirestore.getInstance().collection("User").document(tempUser.getId())
                .set(tempUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (tempImage != null) {
                            MainActivity.profilePicture = tempImage;
                            StorageReference reference = storage.getReference().child("images/" + tempUser.getId());
                            reference.putFile(tempImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("VMUSEUM", "Upload profile picture successfully!");
                                    Toast.makeText(EditInfoActivity.this, "Update successfully!", Toast.LENGTH_SHORT).show();
                                    sweetAlertDialog.dismiss();
                                    startActivity(new Intent(EditInfoActivity.this, MainActivity.class).putExtra("FragmentID", "SettingFragment"));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    TODO: set update but not yet upload profile picture
                                    Log.d("VMUSEUM", "Upload profile picture failed!");
                                    Toast.makeText(EditInfoActivity.this, "Upload profile picture failed! Please try again.", Toast.LENGTH_SHORT).show();
                                    sweetAlertDialog.dismiss();
                                    startActivity(new Intent(EditInfoActivity.this, MainActivity.class).putExtra("FragmentID", "SettingFragment"));
                                    finish();
                                }
                            });
                        } else {
                            sweetAlertDialog.dismiss();
                            Toast.makeText(EditInfoActivity.this, "Update successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditInfoActivity.this, MainActivity.class).putExtra("FragmentID", "SettingFragment"));
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sweetAlertDialog.dismiss();
                        Toast.makeText(EditInfoActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditInfoActivity.this, MainActivity.class).putExtra("FragmentID", "SettingFragment"));
                        finish();
                    }
                });

    }
}