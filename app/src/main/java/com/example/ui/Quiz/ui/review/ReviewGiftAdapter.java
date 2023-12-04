package com.example.ui.Quiz.ui.review;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Adapter.NotificationAdapter;
import com.example.ui.MainActivity;
import com.example.ui.Model.ExchangedGiftModel;
import com.example.ui.Model.GiftModel;
import com.example.ui.Quiz.QuizActivity;
import com.example.ui.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ReviewGiftAdapter extends RecyclerView.Adapter<ReviewGiftAdapter.ReviewGiftViewHolder> {
    private Context context;
    private List<ExchangedGiftModel> giftModelList;

    public ReviewGiftAdapter(Context context) {
        this.context = context;
        this.giftModelList = new ArrayList<>();
    }
    public ReviewGiftAdapter(Context context, List<ExchangedGiftModel> giftModelList) {
        this.context = context;
        this.giftModelList = giftModelList;
    }

    public void setGiftModelList(List<ExchangedGiftModel> giftModelList) {
        this.giftModelList = giftModelList;
        notifyDataSetChanged();
    }

    public void addGiftModel(ExchangedGiftModel giftModel) {
        this.giftModelList.add(giftModel);
        notifyDataSetChanged();
    }

    public void clearGiftModelList() {
        this.giftModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewGiftAdapter.ReviewGiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_review_item, parent, false);
        return new ReviewGiftAdapter.ReviewGiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewGiftAdapter.ReviewGiftViewHolder holder, int position) {
        ExchangedGiftModel exchangedGiftModel = giftModelList.get(position);

        StorageReference imageRef = FirebaseStorage.getInstance("gs://ui-123456.appspot.com").getReference().child("gift_images").child(exchangedGiftModel.getImage_path());
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

        holder.giftName.setText(exchangedGiftModel.getName());
        holder.giftValue.setText(Integer.toString(exchangedGiftModel.getPrice()));
        holder.giftStatus.setText(exchangedGiftModel.getStatus());
    }

    @Override
    public int getItemCount() {
        if (this.giftModelList != null) {
            return this.giftModelList.size();
        }
        return 0;
    }

    public class ReviewGiftViewHolder extends RecyclerView.ViewHolder {
        public ImageView giftImage;
        public TextView giftName;
        public TextView giftValue;
        public TextView giftStatus;
        public ReviewGiftViewHolder(@NonNull View view) {
            super(view);

            giftImage = (ImageView) view.findViewById(R.id.img_gift);
            giftName = (TextView) view.findViewById(R.id.txt_gift_name);
            giftValue = (TextView) view.findViewById(R.id.txt_gift_point);
            giftStatus = (TextView) view.findViewById(R.id.txt_gift_status);
        }
    }
}
