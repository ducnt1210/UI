package com.example.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.Model.UserModel;
import com.example.ui.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignupActivity extends AppCompatActivity {

    String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
    SweetAlertDialog sweetAlertDialog;
    ActivitySignupBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);

        binding.signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String fullName = binding.fullName.getText().toString();
                String number = binding.mobileNumber.getText().toString();
                String email = binding.email.getText().toString().trim();
                String password = binding.password.getText().toString();
                String confirmPassword = binding.confirmPassword.getText().toString();

                sweetAlertDialog = new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                if (!password.equals("") && !email.equals("") && !confirmPassword.equals("") && !fullName.equals("") && !number.equals("")) {
                    if (number.matches(allCountryRegex)) {
                        if (password.equals(confirmPassword)) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            Toast.makeText(SignupActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                            Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                                            loginIntent.putExtra("checkUser", "newUser");
                                            startActivity(loginIntent);
                                            sweetAlertDialog.dismissWithAnimation();

                                            firebaseFirestore.collection("User")
                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                    .set(new UserModel(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), fullName, number, email));
                                            Uri defaultImage = Uri.parse("android.resource://com.example.ui/" + R.drawable.default_profile_picture);
                                            StorageReference reference = storage.getReference().child("images/" + Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                            reference.putFile(defaultImage);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {

                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    });
                        } else {
                            sweetAlertDialog.dismissWithAnimation();
                            Toast.makeText(SignupActivity.this, "Reconfirm your password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        sweetAlertDialog.dismissWithAnimation();
                        Toast.makeText(SignupActivity.this, "The phone number is badly formatted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                    Toast.makeText(SignupActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.toLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });
    }
}