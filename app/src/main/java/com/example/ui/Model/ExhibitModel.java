package com.example.ui.Model;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;

public class ExhibitModel implements Serializable {
    private String id;
    private String parentId;
    private String name;
    private String description;
    private String video;
    private ArrayList<String> content;
    private String image_path;
    public ExhibitModel(String id, String parentId, String name, String description) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
    }

    private String image;

    public ExhibitModel() {}

    public ExhibitModel(String id, String parentId, String name, String description, String video, ArrayList<String> content, String image_path) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
        this.video = video;
        this.content = content;
        this.image_path = image_path;
        this.getImage(this.image_path);
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void getImage(String image_path) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(image_path);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    setImage(uri.toString());
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
