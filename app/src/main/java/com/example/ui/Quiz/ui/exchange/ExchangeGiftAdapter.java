package com.example.ui.Quiz.ui.exchange;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.MainActivity;
import com.example.ui.MainActivityPackage.HomeFragment;
import com.example.ui.Model.ExchangedGiftModel;
import com.example.ui.Model.GiftModel;
import com.example.ui.Model.NotificationModel;
import com.example.ui.R;
import com.example.ui.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeGiftAdapter extends RecyclerView.Adapter<ExchangeGiftAdapter.ExchangeGiftViewHolder>{
    private Context context;
    private List<GiftModel> giftModelList;

    public ExchangeGiftAdapter(Context context) {
        this.context = context;
        this.giftModelList = new ArrayList<>();
    }

    public ExchangeGiftAdapter(Context context, List<GiftModel> giftModelList) {
        this.context = context;
        this.giftModelList = giftModelList;
    }

    public void setGiftModelList(List<GiftModel> giftModelList) {
        this.giftModelList = giftModelList;
        notifyDataSetChanged();
    }

    public void addGiftModel(GiftModel giftModel) {
        this.giftModelList.add(giftModel);
        notifyDataSetChanged();
    }

    public void clearGiftModelList(GiftModel giftModel) {
        this.giftModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExchangeGiftAdapter.ExchangeGiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_item_layout, parent, false);
        return new ExchangeGiftAdapter.ExchangeGiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeGiftAdapter.ExchangeGiftViewHolder holder, int position) {
        GiftModel giftModel = giftModelList.get(position);

        StorageReference imageRef = FirebaseStorage.getInstance("gs://ui-123456.appspot.com").getReference().child("gift_images").child(giftModel.getImage_path());
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Glide.with(context).load(uri.toString()).into(holder.giftImage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase Storage", "Error downloading image: " + e.getMessage());
            }
        });

        holder.giftName.setText(giftModel.getName());
        holder.giftValue.setText(Integer.toString(giftModel.getPrice()));

        holder.giftValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeFragment.scoreModel.getScore() < giftModel.getPrice()) {
                    Toast.makeText(context, "Bạn không đủ số xu để thực hiện đổi quà", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // Setting Alert Dialog Title
                    alertDialogBuilder.setTitle("Xác nhận đổi quà!");
                    // Icon Of Alert Dialog
                    alertDialogBuilder.setIcon(R.drawable.tzuki_question);
                    // Setting Alert Dialog Message
                    alertDialogBuilder.setMessage(
                            "Bạn sẽ mất " + Integer.toString(giftModel.getPrice())
                                    + " xu để đổi món quà này!\n"
                                    + "Bạn chắc chắn muốn đổi quà?"
                    );
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String id = generateFileId();
                            String image_path = "gift.png";
                            boolean seen = false;
                            boolean sentNotification = false;
                            String user_id = MainActivity.currentUser.getId();
                            Timestamp time = Timestamp.now();

                            List<String> description = new ArrayList<>();
                            description.add("$heading$Đổi quà thành công");
                            description.add("Quà đã đổi: " + giftModel.getName());
                            description.add("Số xu đã dùng: " + giftModel.getPrice());
                            description.add("Bạn đã đổi quà thành công. Vào mục \"Quà đã đổi\" để xem các quà đã đổi của bạn!");
                            description.add("Quà đã đổi chỉ có thể được nhận khi bạn checkout tại quầy vé trong ngày đổi quà.");
                            NotificationModel notificationModel = new NotificationModel(id, image_path, description, user_id, seen, sentNotification, time);
                            FirebaseFirestore.getInstance().collection("Notification").document(id).set(notificationModel);

                            HomeFragment.scoreModel.setScore(
                                    HomeFragment.scoreModel.getScore() - giftModel.getPrice()
                            );
                            Utils.updateScore(HomeFragment.scoreModel);
                            addExchangedGift(giftModel);
                            TextView coin =
                                    (TextView) ((AppCompatActivity) context)
                                            .getSupportActionBar().getCustomView().findViewById(R.id.coin);
                            coin.setText(Integer.toString(HomeFragment.scoreModel.getScore()));
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }
    private String generateFileId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public void addExchangedGift(GiftModel giftModel) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.put("name", giftModel.getName());
        data.put("price", giftModel.getPrice());
        data.put("image_path", giftModel.getImage_path());
        data.put("status", "Đã đổi");
        data.put("time", new Timestamp(new Date()));
        FirebaseFirestore.getInstance()
                .collection("ExchangedGift")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        // Setting Alert Dialog Title
                        alertDialogBuilder.setTitle("Đổi quà thành công!");
                        // Icon Of Alert Dialog
                        alertDialogBuilder.setIcon(R.drawable.tzuki_success);
                        // Setting Alert Dialog Message
                        alertDialogBuilder.setMessage(
                                "Vào mục \"Quà đã đổi\" để xem các quà đã đổi của bạn!\n"
                                        + "Khi checkout, bạn đưa cho nhân viên xem để nhận quà nhé!"
                        );
                        alertDialogBuilder.setCancelable(true);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Update Exchanged Data failed", giftModel.getId());
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        // Setting Alert Dialog Title
                        alertDialogBuilder.setTitle("Đổi quà không thành công!");
                        // Icon Of Alert Dialog
                        alertDialogBuilder.setIcon(R.drawable.heheee);
                        // Setting Alert Dialog Message
                        alertDialogBuilder.setMessage(
                                "Bạn đã đổi quà không thành công!\n"
                                        + "Đã có lỗi xảy ra với hệ thống."
                        );
                        alertDialogBuilder.setCancelable(true);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (this.giftModelList != null) {
            return this.giftModelList.size();
        }
        return 0;
    }

    public class ExchangeGiftViewHolder extends RecyclerView.ViewHolder {
        public ImageView giftImage;
        public TextView giftName;
        public TextView giftValue;

        public ExchangeGiftViewHolder(@NonNull View view) {
            super(view);

            giftImage = (ImageView) view.findViewById(R.id.gift_image);
            giftName = (TextView) view.findViewById(R.id.gift_name);
            giftValue = (TextView) view.findViewById(R.id.gift_value);
        }
    }
}
