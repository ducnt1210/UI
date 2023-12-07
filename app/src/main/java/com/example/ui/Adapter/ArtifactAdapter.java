package com.example.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Model.ExhibitModel;
import com.example.ui.R;
import com.example.ui.ShowExhibitActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ArtifactAdapter extends ListAdapter<ExhibitModel, ArtifactAdapter.ArtifactViewHolder> {
    private Context context;

    public ArtifactAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item, parent, false);
        return new ArtifactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        ExhibitModel exhibitModel = getItem(position);
        holder.bind(exhibitModel);
    }

    static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;




        public ArtifactViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.area_img);
            nameTextView = itemView.findViewById(R.id.area_text);


        }

        public void bind(ExhibitModel exhibitModel) {
            getImage(exhibitModel.getImage_path(), imageView);


            nameTextView.setText(exhibitModel.getName());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(imageView.getContext(), ShowExhibitActivity.class);
                    intent.putExtra("exhibit", exhibitModel);
                    imageView.getContext().startActivity(intent);
                }
            });
        }
        public void getImage(String image_path, ImageView imageView) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(image_path);
            System.out.println(3333);

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uri != null) {
                        System.out.println(imageView);
                        // Uri tải xuống thành công, sử dụng Glide để hiển thị ảnh
                        Glide.with(imageView.getContext())
                                .load(uri)
                                .into(imageView);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý khi không thể tải xuống ảnh
                    Log.e("Firebase Storage", "Error downloading image: " + e.getMessage());
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<ExhibitModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ExhibitModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ExhibitModel oldItem, @NonNull ExhibitModel newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ExhibitModel oldItem, @NonNull ExhibitModel newItem) {
            // Implement your own equality check if needed
            return oldItem.equals(newItem);
        }
    };


}
