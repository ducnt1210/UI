package com.example.ui.SettingPackage;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.ui.MainActivity;
import com.example.ui.Model.NotificationModel;
import com.example.ui.Model.PriorityModel;
import com.example.ui.R;
import com.example.ui.databinding.ActivityCheckPriorityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CheckPriorityActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    private static final int MANAGE_EXTERNAL_STORAGE_REQUEST_CODE = 102;
    private static final int PICKFILE_REQUEST_CODE = 1;
    public PriorityModel priorityField;
    ActivityCheckPriorityBinding binding;
    Uri tempFile;
    String path, fileName, fileId;
    String priority = "";
    String uid = MainActivity.currentUser.getId();
    ActivityResultLauncher<String> getFile = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                fileId = generateFileId();
                tempFile = result;
                fileName = fileId + ".jpg";
                Toast toast = Toast.makeText(CheckPriorityActivity.this, "Tải lên tệp: " + fileName, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckPriorityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (MainActivity.profilePicture != null) {
            Glide.with(this).load(MainActivity.profilePicture).into(binding.imgAvatar);
        }

        binding.txtName.setText(MainActivity.currentUser.getName());

        binding.vertifyButton.setOnClickListener(v -> openDialog());

        setDisplayVerifyButton();

    }


    private void setDisplayVerifyButton() {
        FirebaseFirestore.getInstance().collection("Priority").document(uid).get().addOnSuccessListener(new OnSuccessListener<com.google.firebase.firestore.DocumentSnapshot>() {
            @Override
            public void onSuccess(com.google.firebase.firestore.DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    priorityField = documentSnapshot.toObject(PriorityModel.class);
                    if (priorityField.getPriority() != null) {
                        binding.vertifyButton.setEnabled(false);
                        binding.vertifyButton.setBackgroundResource(R.drawable.bg_review_gift);

                        if (!priorityField.isVerified()) {
                            binding.vertifyButton.setText("Đang chờ");
                            binding.txtInstruction.setVisibility(View.VISIBLE);
                        } else {
                            binding.vertifyButton.setText("Đã xác minh");
                            binding.txtInstruction.setVisibility(View.GONE);
                            binding.txtPriority.setText(priorityField.getPriority());
                        }
                    } else {
                        binding.vertifyButton.setEnabled(true);
                        binding.vertifyButton.setBackgroundResource(R.drawable.bg_exchange_button);
                        binding.vertifyButton.setText("Xác minh");
                        binding.txtInstruction.setVisibility(View.GONE);

                    }
                }
            }
        });
    }

    private void requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_CODE);
            } catch (Exception e) {
                // Open the application's settings to grant the permission manually
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MANAGE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // User has granted access to read and manage external storage
                } else {
                    // User has denied access to manage external storage
                }
            }
        } else if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            path = uri.getPath();

            if (path == null) {
                Toast.makeText(CheckPriorityActivity.this, "Không tìm thấy tệp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            openDialog();
        } else {
            path = null;
        }
    }

    private void openDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                requestManageExternalStoragePermission();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICKFILE_REQUEST_CODE);
            }
        }
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_check_priority);

        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributeshehe = window.getAttributes();
        windowAttributeshehe.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributeshehe);


        MaterialButton btnChooseFile = dialog.findViewById(R.id.choose_file_button);
        btnChooseFile.setOnClickListener(v -> {
            getFile.launch("image/*");
        });

        RadioButton r1 = dialog.findViewById(R.id.radioButton1);
        RadioButton r2 = dialog.findViewById(R.id.radioButton2);
        RadioButton r3 = dialog.findViewById(R.id.radioButton3);
        RadioButton r4 = dialog.findViewById(R.id.radioButton4);
        RadioButton r5 = dialog.findViewById(R.id.radioButton5);


        MaterialButton btnSubmit = dialog.findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(view -> {
            if (r1.isChecked()) priority = "Sinh viên";
            else if (r2.isChecked()) priority = "Học sinh";
            else if (r3.isChecked()) priority = "Người cao tuổi";
            else if (r4.isChecked()) priority = "Dân tộc thiểu số";
            else if (r5.isChecked()) priority = "Người khuyết tật nặng";
            else priority = "";
            if (priority.equals("")) {
                Toast.makeText(CheckPriorityActivity.this, "Vui lòng chọn đối tượng ưu tiên!", Toast.LENGTH_SHORT).show();
            } else if (tempFile == null) {
                Toast.makeText(CheckPriorityActivity.this, "Vui lòng tải lên tệp minh chứng!", Toast.LENGTH_SHORT).show();
            } else {
                Calendar calendar = Calendar.getInstance();
                String date = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
                String userName = MainActivity.currentUser.getName();


                priorityField = new PriorityModel(uid, priority, userName, date, "", false, true);
                FirebaseFirestore.getInstance().collection("Priority").document(uid).set(priorityField);


                StorageReference referencee = FirebaseStorage.getInstance().getReference().child("priority_files/" + date).child(fileName);
                referencee.putFile(tempFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileURL = uri.toString();
                                FirebaseFirestore.getInstance().collection("Priority").document(uid).update("filePath", fileURL);
                            }
                        });
                        Toast.makeText(CheckPriorityActivity.this, "Gửi thành công!", Toast.LENGTH_SHORT).show();
                        Intent intentBefore = getIntent();
                        Intent intent = new Intent(CheckPriorityActivity.this, CheckPriorityActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckPriorityActivity.this, "Tải tệp thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();

                FirebaseStorage.getInstance().getReference().child("notification_images/" + fileName).putFile(tempFile);
                String id = generateFileId();
                String image_path = "vertify.jpg";
                boolean seen = false;
                boolean sentNotification = false;
                String user_id = uid;
                Timestamp time = Timestamp.now();


                List<String> description = new ArrayList<>();
                description.add("$heading$Đã gửi thông tin xác minh đối tượng ưu tiên");
                description.add("Loại đối tượng: " + priority);
                description.add("$imgs$" + fileName);
                description.add("$note$Minh chứng xét đối tượng ưu tiên");
                description.add("Nếu có bất kỳ sai sót gì, vui lòng liên hệ theo thông tin liên hệ ở mục cài đặt để được hỗ trợ giải quyết.");
                NotificationModel notificationModel = new NotificationModel(id, image_path, description, user_id, seen, sentNotification, time);
                FirebaseFirestore.getInstance().collection("Notification").document(id).set(notificationModel);

                List<String> description_en = new ArrayList<>();
                description_en.add("$heading$Sent priority verification information");
                description_en.add("Priority type: " + priority);
                description_en.add("$imgs$" + fileName);
                description_en.add("$note$Priority verification evidence");
                description_en.add("If there are any errors, please contact the contact information in the settings section for assistance.");
                FirebaseFirestore.getInstance().collection("Notification").document(id).update("description_en", description_en);

            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    private String generateFileId() {
        return String.valueOf(System.currentTimeMillis());
    }
}