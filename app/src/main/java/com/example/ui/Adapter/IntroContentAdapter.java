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

public class IntroContentAdapter extends RecyclerView.Adapter<IntroContentAdapter.IntroContentViewHolder> {

    private final List<String> introContentList;

    public IntroContentAdapter(List<String> introContentList) {
        this.introContentList = introContentList;
    }

    @NonNull
    @Override
    public IntroContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.intro_content_item, parent, false);
        return new IntroContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroContentViewHolder holder, int position) {
        String introContent = introContentList.get(position);
        holder.bind(introContent);
    }

    @Override
    public int getItemCount() {
        return introContentList.size();
    }

    public class IntroContentViewHolder extends RecyclerView.ViewHolder {
        private final TextView introContentTextView;
        ImageView imageView;

        public IntroContentViewHolder(@NonNull View itemView) {
            super(itemView);
            introContentTextView = itemView.findViewById(R.id.intro_content_item_text);
            imageView = itemView.findViewById(R.id.img_intro_item);
        }

        public void bind(String introContent) {
            if (introContent != null && !introContent.isEmpty()) {
                if (introContent.startsWith("$imgs$")) {
                    String imageName = introContent.substring("$imgs$".length());
                    StorageReference imageRef = FirebaseStorage.getInstance("gs://ui-123456.appspot.com").getReference().child("intro_imgs").child(imageName);
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
                    introContentTextView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    introContentTextView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    if (introContent.startsWith("$heading$")) {
                        introContentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        introContentTextView.setText(introContent.substring("$heading$".length()));
                        introContentTextView.setTextSize(20);
                        introContentTextView.setTextColor(itemView.getResources().getColor(R.color.main_green));
                        introContentTextView.setTypeface(introContentTextView.getTypeface(), Typeface.NORMAL);
                        introContentTextView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey_bold));
                    } else if (introContent.startsWith("$note$")) {
                        introContentTextView.setText(introContent.substring("$note$".length()));
                        introContentTextView.setTextSize(11);
                        introContentTextView.setTextColor(itemView.getResources().getColor(R.color.black));
                        introContentTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        introContentTextView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey));
                        introContentTextView.setTypeface(introContentTextView.getTypeface(), Typeface.ITALIC);
                        introContentTextView.setTypeface(introContentTextView.getTypeface(), Typeface.ITALIC);
                    } else {
                        introContentTextView.setText("\t\t\t" + introContent);
                        introContentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        introContentTextView.setTextColor(itemView.getResources().getColor(R.color.black));
                        introContentTextView.setTextSize(14);
                        introContentTextView.setTypeface(introContentTextView.getTypeface(), Typeface.NORMAL);
                        introContentTextView.setTypeface(itemView.getContext().getResources().getFont(R.font.alegrey));
                    }
                }
            } else {
                introContentTextView.setText("");
            }
        }
    }
}
