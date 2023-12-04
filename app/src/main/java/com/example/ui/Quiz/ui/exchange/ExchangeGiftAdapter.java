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
import com.example.ui.R;
import com.example.ui.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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

    public void addExchangedGift(GiftModel giftModel) {
//        ExchangedGiftModel exchangedGiftModel = new ExchangedGiftModel(
//                "",
//                FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                giftModel.getName(),
//                giftModel.getPrice(),
//                giftModel.getImage_path(),
//                "Đã đổi"
//        );
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.put("name", giftModel.getName());
        data.put("price", giftModel.getPrice());
        data.put("image_path", giftModel.getImage_path());
        data.put("status", "Đã đổi");
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
                                "Bạn đã đổi quà thành công!\n"
                                        + "Khi checkout, bạn đưa cho nhân viên xem để nhận quà nhé"
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
