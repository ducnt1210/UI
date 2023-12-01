package com.example.ui;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ui.Model.UserModel;
import com.example.ui.SettingPackage.LanguageActivity;
import com.example.ui.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends LocaleAwareCompatActivity {

    SweetAlertDialog sweetAlertDialog;
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UI);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);

        storage = FirebaseStorage.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        binding.ggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ggSignin();
            }
        });

//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.signOut();
//        gsc.signOut();
//        TODO: comment 2 above to keep user sign in

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            String checkUser = getIntent().getStringExtra("checkUser");
            if (checkUser != null && checkUser.contains("newUser")) {
                Intent intent = new Intent(LoginActivity.this, LanguageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("checkUser", "newUser");
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
        }

        binding.login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString().trim();
                sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                if (!password.equals("") && !email.equals("")) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    checkUserSignIn();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    Toast.makeText(LoginActivity.this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                    Toast.makeText(LoginActivity.this, "Fill in email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.email.getText().toString();


                sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setTitle("Sending email");
                sweetAlertDialog.show();
                if (!email.equals("")) {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    Toast.makeText(LoginActivity.this, "Password reset send to email", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                    Toast.makeText(LoginActivity.this, "Fill in email to send verification", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.toSignupActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finishAffinity();
            }
        });
    }

    private void ggSignin() {
        sweetAlertDialog.show();
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                sweetAlertDialog.dismiss();
                Toast.makeText(this, "Login with Google failed!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error" + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            boolean newUser = Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                            if (newUser) {
                                assert user != null;
                                String name = null, phoneNumber = null, email = null;
                                Uri profileImage = Uri.parse("android.resource://com.example.financialapp/" + R.drawable.default_profile_picture);
                                for (UserInfo profile : user.getProviderData()) {
                                    name = profile.getDisplayName();
                                    phoneNumber = profile.getPhoneNumber();
                                    email = profile.getEmail();
                                    if (profile.getPhotoUrl() != null)
                                        profileImage = profile.getPhotoUrl();
                                }
                                final Uri defaultImage = profileImage;
                                UserModel ggUser = new UserModel(user.getUid(), name, phoneNumber, email);
//                                ggUser.setSignIn(true);
                                //                            TODO: return or fix setSignIn when have signOut
                                FirebaseFirestore.getInstance().collection("User").document(ggUser.getId()).set(ggUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
//                                                Uri defaultImage = Uri.parse("android.resource://com.example.ui/" + R.drawable.default_profile_picture);
                                                StorageReference reference = storage.getReference().child("images/" + ggUser.getId());
                                                reference.putFile(defaultImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                        String checkUser = getIntent().getStringExtra("checkUser");
                                                        if (checkUser != null && checkUser.contains("newUser")) {
                                                            Intent intent = new Intent(LoginActivity.this, LanguageActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent.putExtra("checkUser", "newUser");
                                                            startActivity(intent);
                                                        } else {
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                        }
                                                        sweetAlertDialog.dismiss();
                                                        finish();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                sweetAlertDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                FirebaseFirestore.getInstance().collection("User").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        UserModel tempGGUser = documentSnapshot.toObject(UserModel.class);
                                        if (tempGGUser.isSignIn()) {
                                            Toast.makeText(LoginActivity.this, "This account already been signed in!", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            sweetAlertDialog.dismiss();
                                            gsc.signOut();
                                            LoginActivity.this.recreate();
                                        } else {
//                                            tempGGUser.setSignIn(true);
                                            //                            TODO: return or fix setSignIn when have signOut
                                            FirebaseFirestore.getInstance().collection("User").document(tempGGUser.getId()).set(tempGGUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    sweetAlertDialog.dismiss();
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            sweetAlertDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserSignIn() {
        FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        assert user != null;
                        if (user.isSignIn()) {
                            Toast.makeText(LoginActivity.this, "This account already been signed in!", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            sweetAlertDialog.dismissWithAnimation();
                        } else {
//                            user.setSignIn(true);
//                            TODO: return or fix setSignIn when have signOut
                            FirebaseFirestore.getInstance().collection("User").document(user.getId()).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        }
                                    });
                        }
                    }
                });
    }
}