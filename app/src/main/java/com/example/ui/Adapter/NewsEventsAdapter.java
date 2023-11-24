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
import com.example.ui.NewsEventsActivity;
import com.example.ui.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class NewsEventsAdapter extends RecyclerView.Adapter<NewsEventsAdapter.NewsEventsViewHolder> {
    private List<String> content;
    public NewsEventsAdapter(List<String> content) {
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
    public NewsEventsAdapter.NewsEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_events_content_item, parent, false);
        return new NewsEventsAdapter.NewsEventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsEventsAdapter.NewsEventsViewHolder holder, int position) {
        String newsEventsContent = content.get(position);
        holder.bind(newsEventsContent);
    }

    @Override
    public int getItemCount() {
        if (content != null) {
            return content.size();
        }
        return 0;
    }

    public class NewsEventsViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public NewsEventsViewHolder(@NonNull View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.news_events_content_text);
            imageView = (ImageView) view.findViewById(R.id.news_events_content_image);

            imageView.setImageResource(R.drawable.loading);

        }

        public void bind(String newsEventsContent) {
            if (newsEventsContent != null && !newsEventsContent.isEmpty()) {
                if (newsEventsContent.startsWith("$imgs$")) {
                    String imageName = newsEventsContent.substring("$imgs$".length());
                    StorageReference imageRef = FirebaseStorage.getInstance("gs://ui-123456.appspot.com").getReference().child("newsevents_images").child(imageName);
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
                    if (newsEventsContent.startsWith("$heading$")) {
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        textView.setText(newsEventsContent.substring("$heading$".length()));
                        textView.setTextSize(20);
                        textView.setTextColor(itemView.getResources().getColor(R.color.main_green));
                        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
                        textView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey_bold));
                    } else if (newsEventsContent.startsWith("$note$")) {
                        textView.setText(newsEventsContent.substring("$note$".length()));
                        textView.setTextSize(11);
                        textView.setTextColor(itemView.getResources().getColor(R.color.black));
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey));
                        textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
                        textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
                    } else {
                        textView.setText("\t\t\t" + newsEventsContent);
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
