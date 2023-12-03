package com.example.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ScaleGestureDetector;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ui.databinding.ActivityMapBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MapActivity extends AppCompatActivity {
    ActivityMapBinding binding;
    ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private float lastX, lastY;
    private boolean isPanning = false;
    private int originalHeight;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(R.string.map_instruction);

        binding.mapLayout.setOnTouchListener((view, motionEvent) -> {
            onTouchEvent(motionEvent);
            return true;
        });

        binding.mapLayout.post(() -> {
            originalHeight = binding.mapLayout.getHeight();
        });

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);

        zoomButtonConstructure();

        numberButtonConstructure();

        directionButtonConstructure();
    }

    private void directionButtonConstructure() {
        binding.directionButton.setOnClickListener(view -> {
            mapDirectionConstructure();
        });
    }

    private Paint paintColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(2);
        return paint;
    }

    private void mapDirectionConstructure() {
        sweetAlertDialog.show();
        Bitmap originalBitmap = ((BitmapDrawable) binding.mapImgView.getDrawable()).getBitmap();
        Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);

        canvas.drawBitmap(originalBitmap, 0, 0, null);

        int roadColor = Color.parseColor("#964B00");
        int greenColor = Color.parseColor("#0ED145");
        int darkGreenColor = Color.parseColor("#044117");
        int redColor = Color.parseColor("#EC1C24");
        int orangeColor = Color.parseColor("#FF7F27");
        int blueColor = Color.parseColor("#8CFFFB");
        int blackColor = Color.parseColor("#000000");
        int resColor = Color.parseColor("#C0C0C0");

        Log.d("width", Integer.toString(originalBitmap.getWidth()));
        Log.d("height", Integer.toString(originalBitmap.getHeight()));
//      Vẽ lại bản đồ bằng pixel
        for (int x = 0; x < originalBitmap.getWidth(); x += 2) {
            for (int y = 0; y < originalBitmap.getHeight(); y += 2) {
                int pixelColor = originalBitmap.getPixel(x, y);

                if (isSimilarColor(pixelColor, roadColor)) {
                    canvas.drawPoint(x, y, paintColor(roadColor));
                }
                if (isSimilarColor(pixelColor, greenColor)) {
                    canvas.drawPoint(x, y, paintColor(greenColor));
                }
                if (isSimilarColor(pixelColor, darkGreenColor)) {
                    canvas.drawPoint(x, y, paintColor(darkGreenColor));
                }
                if (isSimilarColor(pixelColor, redColor)) {
                    canvas.drawPoint(x, y, paintColor(redColor));
                }
                if (isSimilarColor(pixelColor, orangeColor)) {
                    canvas.drawPoint(x, y, paintColor(orangeColor));
                }
                if (isSimilarColor(pixelColor, blackColor)) {
                    canvas.drawPoint(x, y, paintColor(blackColor));
                }
                if (isSimilarColor(pixelColor, blueColor)) {
                    canvas.drawPoint(x, y, paintColor(blueColor));
                }
            }
            if (x == originalBitmap.getWidth() - 2) x--;
        }

        for (int x = 530; x < 610; x += 1) {
            for (int y = 210; y < 290; y += 1) {
                if (isSimilarColor(originalBitmap.getPixel(x, y), roadColor)) {
                    canvas.drawPoint(x, y, paintColor(resColor));
                }
            }
        }

        sweetAlertDialog.dismissWithAnimation();

        binding.mapImgView.setImageBitmap(newBitmap);
    }

    // Kiểm tra xem một màu có gần một màu khác không
    private boolean isSimilarColor(int color1, int color2) {
        int tolerance = 50; // Độ chênh lệch tối đa cho mỗi thành phần màu (R, G, B)

        int red1 = Color.red(color1);
        int green1 = Color.green(color1);
        int blue1 = Color.blue(color1);

        int red2 = Color.red(color2);
        int green2 = Color.green(color2);
        int blue2 = Color.blue(color2);

        return Math.abs(red1 - red2) <= tolerance && Math.abs(green1 - green2) <= tolerance && Math.abs(blue1 - blue2) <= tolerance;
    }


    private void numberButtonConstructure() {
        binding.buttonchinh.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 00);
        });
        binding.buttoncanhdieu.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 101);
        });
        binding.button1.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 1);
        });
        binding.button2.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 2);
        });
        binding.button3.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 3);
        });
        binding.button4.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 4);
        });
        binding.button5.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 5);
        });
        binding.button6.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 6);
        });
        binding.button7.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 7);
        });
        binding.button8.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 8);
        });
        binding.button9.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 9);
        });
        binding.button10.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 10);
        });
        binding.button11.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 11);
        });
        binding.button12.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 12);
        });
        binding.button13.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 13);
        });
        binding.button14.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 14);
        });
        binding.button15.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 15);
        });
        binding.button16.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 16);
        });
        binding.button17.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 17);
        });
        binding.button18.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 18);
        });
        binding.button19.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 19);
        });
        binding.button20.setOnClickListener(view -> {
            openDialog(Gravity.CENTER, 20);
        });
    }

    private void openDialoghehe() {
        final Dialog dialoghehe = new Dialog(this);
        dialoghehe.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoghehe.setContentView(R.layout.dialog_future_planheheee);

        Window windowhehe = dialoghehe.getWindow();
        if (windowhehe == null) return;

        windowhehe.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowhehe.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributeshehe = windowhehe.getAttributes();
        windowAttributeshehe.gravity = Gravity.CENTER;
        windowhehe.setAttributes(windowAttributeshehe);

        dialoghehe.show();
    }

    private void openDialog(int gravity, int type) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map);

        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        TextView txtTitleDialog = dialog.findViewById(R.id.locationName);
        TextView direcMap = dialog.findViewById(R.id.textViewDirection);
        TextView detailMap = dialog.findViewById(R.id.textViewDetail);
        ImageView imgDialog = dialog.findViewById(R.id.imageViewMap);
        TextView txtDirection = dialog.findViewById(R.id.textViewDirection);
        TextView txtDetail = dialog.findViewById(R.id.textViewDetail);

        direcMap.setOnClickListener(view -> {
            openDialoghehe();
        });

        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("map_imgs");
        switch (type) {
            case 00:
                txtTitleDialog.setText("Tòa Trống đồng");
                imgRef.child("toatrongdong.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 101:
                txtTitleDialog.setText("Tòa Cánh diều");
                imgRef.child("toacanhdieu.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 1:
                txtTitleDialog.setText("1. Nhà Chăm");
                imgRef.child("nhacham1.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 2:
                txtTitleDialog.setText("2. Ghe Ngo Khơ me");
                imgRef.child("ghengo2.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 3:
                txtTitleDialog.setText("3. Thủy Đình");
                imgRef.child("thuydinh3.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 4:
                txtTitleDialog.setText("4. Nhà Việt");
                imgRef.child("nhaviet4.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 5:
                txtTitleDialog.setText("5. Nhà thuyền");
                imgRef.child("nhathuyen5.jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 6:
                txtTitleDialog.setText("6. Nhà rông Banana");
                imgRef.child("nharong6.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 7:
                txtTitleDialog.setText("7. Nhà Êđê");
                imgRef.child("nhaede7.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 8:
                txtTitleDialog.setText("8. Nhà mồ Giarai");
                imgRef.child("nhamo8.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 9:
                txtTitleDialog.setText("9. Nhà mồ Cơtu");
                imgRef.child("nhamocotu9.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 10:
                txtTitleDialog.setText("10. Lò rèn Nùng");
                imgRef.child("loren10.jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 11:
                txtTitleDialog.setText("11. Nhà Tày");
                imgRef.child("nhatay11.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 12:
                txtTitleDialog.setText("12. Nhà Dao");
                imgRef.child("nhadao12.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 13:
                txtTitleDialog.setText("13. Nhà Hmông");
                imgRef.child("nhahmong13.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 14:
                txtTitleDialog.setText("14. Nhà Hà Nhì");
                imgRef.child("hanhi14.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 15:
                txtTitleDialog.setText("15. Nhà thực nghiệm");
                imgRef.child("thucnghiem15.jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 16:
                txtTitleDialog.setText("16. Nhà hàng");
                imgRef.child("nhahang16.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 17:
                txtTitleDialog.setText("17. Cửa hàng sách");
                imgRef.child("nhahang16.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 18:
                txtTitleDialog.setText("18. Cửa hàng lưu niệm");
                imgRef.child("luuniem18.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 19:
                txtTitleDialog.setText("19. Café");
                imgRef.child("cf19.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
            case 20:
                txtTitleDialog.setText("20. Coffee & Cake");
                imgRef.child("cfvabanhngot20.jpeg").getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(imgDialog);
                });
                break;
        }

        dialog.show();
    }

    private void zoomButtonConstructure() {
        binding.zoomInButton.setOnClickListener(view -> {
            if (scaleFactor < 3.0f) {
                scaleFactor += 0.25f;
                binding.mapLayout.setScaleX(scaleFactor);
                binding.mapLayout.setScaleY(scaleFactor);
            }
        });

        binding.zoomOutButton.setOnClickListener(view -> {
            scaleFactor -= 0.25f;
            if (scaleFactor >= 0.5f) {
                binding.mapLayout.setScaleX(scaleFactor);
                binding.mapLayout.setScaleY(scaleFactor);
            }
        });

        binding.zoomRatio.setOnClickListener(view -> {
            scaleFactor = 1.0f;
            binding.mapLayout.setScaleX(scaleFactor);
            binding.mapLayout.setScaleY(scaleFactor);
            binding.mapLayout.setTranslationX(0);
            binding.mapLayout.setTranslationY(0);
        });
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case android.view.MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                isPanning = false;
                break;
            case android.view.MotionEvent.ACTION_MOVE:
                if (scaleFactor > 1.0f) {
                    float deltaX = x - lastX;
                    float deltaY = y - lastY;
                    if (!isPanning && (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1)) {
                        isPanning = true;
                    }

                    if (isPanning) {
                        binding.mapLayout.setTranslationX(binding.mapLayout.getTranslationX() + deltaX);
                        binding.mapLayout.setTranslationY(binding.mapLayout.getTranslationY() + deltaY);
                    }


                    lastX = x;
                    lastY = y;
                }
                break;
            case android.view.MotionEvent.ACTION_UP:
                isPanning = false;
                break;
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float previousScaleFactor = scaleFactor;
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 5.0f)); // Giới hạn tỷ lệ phóng to/thu nhỏ
            if (previousScaleFactor != 1.0f && scaleFactor == 1.0f) {
                binding.mapLayout.setTranslationX(0);
                binding.mapLayout.setTranslationY(0);
            }
            binding.mapLayout.setScaleX(scaleFactor);
            binding.mapLayout.setScaleY(scaleFactor);

            return true;
        }
    }
}