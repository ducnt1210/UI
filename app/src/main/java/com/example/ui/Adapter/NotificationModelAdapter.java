
package com.example.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Model.NotificationModel;
import com.example.ui.NotificationActivity;
import com.example.ui.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationModelAdapter extends RecyclerView.Adapter<NotificationModelAdapter.NotificationModelViewHolder> {
    private Context context;
    public List<NotificationModel> notificationModelList;

    private int count = 0;

    public NotificationModelAdapter(Context context) {
        this.context = context;
        this.notificationModelList = new ArrayList<>();
    }

    public NotificationModelAdapter(Context context,
                                    List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    public List<NotificationModel> getNotificationModelList() {
        return this.notificationModelList;
    }

    public void setNotificationModelList(List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList;
        notifyDataSetChanged();
    }

    public void addNotificationModelList(NotificationModel data) {
        this.notificationModelList.add(data);
        notifyDataSetChanged();
    }

    public void clearNotificationModelList() {
        this.notificationModelList.clear();
        notifyDataSetChanged();
    }

   @Override
   public NotificationModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_item, parent, false);
       return new NotificationModelViewHolder(view);
   }

    @Override
    public void onBindViewHolder(@NonNull NotificationModelViewHolder holder, int position) {
        NotificationModel item = this.notificationModelList.get(position);
        if (item.getId() != "") {
            holder.dateTextView.setVisibility(View.GONE);

            if (item.getSeen()) {
                holder.dotStatus.setVisibility(View.INVISIBLE);
                holder.item.setBackgroundResource(R.drawable.bg_read_noti);
            } else {
                holder.dotStatus.setVisibility(View.VISIBLE);
            }

            StorageReference imageRef = FirebaseStorage.getInstance("gs://ui-123456.appspot.com").getReference().child("notification_images").child(item.getImage_path());
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(context).load(uri.toString()).into(holder.imageItem);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase Storage", "Error downloading image: " + e.getMessage());
                    }
                });

//        holder.time.setText(convertTimeFormat(item.getTime()));
            holder.content.setText(item.heading());
            holder.time.setText(item.formatDate());

            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.item.animate().alpha(0.5f).setDuration(100).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            holder.item.animate().alpha(1).setDuration(100);
                        }
                    });
                    if (item.getSeen() == false) {
                        item.setSeen(true);
                        updateData(item);
                        notifyDataSetChanged();
//                        holder.dotStatus.setVisibility(View.INVISIBLE);
                    }
                    goToDetailedNotification(item);
                }
            });
        } else {
            holder.item.setVisibility(View.GONE);
            Log.d("type", "null");
            Log.d("count", Integer.toString(count));
            if (count == 0){
                ++count;
                holder.dateTextView.setText(R.string.today);
            } else {
                count = 0;
                holder.dateTextView.setText(R.string.before);
            }
        }
    }

    private void goToDetailedNotification(NotificationModel item) {
        Intent intent = new Intent(this.context, NotificationActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putString("id", item.getId());
        bundle.putStringArrayList("description", (ArrayList<String>) item.getDescription());
        bundle.putString("time", item.formatDate());
        bundle.putBoolean("update", false);
        bundle.putString("id", item.getId());
        intent.putExtras(bundle);
        this.context.startActivity(intent);
    }

    private void updateData(NotificationModel item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notification")
                .document(item.getId())
                .set(item)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Failed seen update", item.getId());
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (this.notificationModelList != null) {
            return notificationModelList.size();
        }
        return 0;
    }

    public class NotificationModelViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout item;
        public ImageView dotStatus;
        public CircleImageView imageItem;
        public TextView time, content, dateTextView;

        public NotificationModelViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);

            item = itemView.findViewById(R.id.noti_item);
            dotStatus = itemView.findViewById(R.id.noti_item_dot_status);
            imageItem = itemView.findViewById(R.id.img_noti_item);
            time = itemView.findViewById(R.id.txt_noti_item_time);
            content = itemView.findViewById(R.id.txt_noti_item_content);
        }
    }
}
