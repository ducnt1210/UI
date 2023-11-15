package com.example.ui.Adapter;

import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<String> content;
    public NotificationAdapter(List<String> content) {
        this.content = content;
    }

    public void setContent(List<String> content) {
        this.content = content;
        notifyDataSetChanged();
    }

    public void addContent(String description) {
        this.content.add(description);
        notifyDataSetChanged();
    }

    public void clearContent() {
        this.content.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_content_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        String notificationContent = content.get(position);
        holder.bind(notificationContent);
    }

    @Override
    public int getItemCount() {
        if (content != null) {
            return content.size();
        }
        return 0;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public NotificationViewHolder(@NonNull View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.notification_content_text);
            imageView = (ImageView) view.findViewById(R.id.notification_content_image);
            imageView.setImageResource(R.drawable.loading);
        }

        public void bind(String notificationContent) {
            if (notificationContent != null && !notificationContent.isEmpty()) {
                if (notificationContent.startsWith("$imgs$")) {
                    String imageName = notificationContent.substring("$imgs$".length());
                    StorageReference imageRef = FirebaseStorage.getInstance("gs://ui-123456.appspot.com").getReference().child("notification_images").child(imageName);
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                Glide.with(itemView.getContext()).load(uri).into(imageView);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firebase Storage", "Error downloading image: " + e.getMessage());
                        }
                    });
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    if (notificationContent.startsWith("$heading$")) {
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        textView.setText(notificationContent.substring("$heading$".length()));
                        textView.setTextSize(20);
                        textView.setTextColor(itemView.getResources().getColor(R.color.main_green));
                        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
                        textView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey_bold));
                    } else if (notificationContent.startsWith("$note$")) {
                        textView.setText(notificationContent.substring("$note$".length()));
                        textView.setTextSize(11);
                        textView.setTextColor(itemView.getResources().getColor(R.color.black));
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey));
                        textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
                        textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
                    } else {
                        textView.setText("\t\t\t" + notificationContent);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        textView.setTextColor(itemView.getResources().getColor(R.color.black));
                        textView.setTextSize(14);
                        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
                        textView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey));
                    }
                }
            } else {
                textView.setText("");
            }
        }
    }
}
