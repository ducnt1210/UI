package com.example.ui.Adapters;
//
//import android.app.Activity;
//import android.content.Context;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.example.ui.Model.NotificationModel;
//import com.example.ui.R;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class NotificationModelAdapter extends BaseAdapter {
//    private Context context;
//    private List<NotificationModel> notificationModelList;
//
//    public NotificationModelAdapter(Context context, List<NotificationModel> notificationModelList) {
//        this.context = context;
//        this.notificationModelList = notificationModelList;
//    }
//
//    @Override
//    public int getCount() {
//        return notificationModelList.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return notificationModelList.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    public static class NotificationViewHolder {
//        public RelativeLayout item;
//        public ImageView dotStatus;
//        public CircleImageView imageItem;
//        public TextView time, content;
//    }
//
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        NotificationViewHolder viewHolder  = null;
//        if (view == null) {
//            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            view = inflater.inflate(R.layout.noti_item, null);
//            viewHolder = new NotificationViewHolder();
//            viewHolder.item = view.findViewById(R.id.noti_item);
//            viewHolder.dotStatus = view.findViewById(R.id.noti_item_dot_status);
//            viewHolder.imageItem = view.findViewById(R.id.img_noti_item);
//            viewHolder.time = view.findViewById(R.id.txt_noti_item_time);
//            viewHolder.content = view.findViewById(R.id.txt_noti_item_content);
//        } else {
//            viewHolder = ((NotificationViewHolder) view.getTag());
//        }
////        viewHolder.dotStatus.setImageResource();
//        NotificationModel item = notificationModelList.get(i);
//        if (item.getSeen()) {
//            viewHolder.dotStatus.setVisibility(View.INVISIBLE);
//        } else {
//            viewHolder.dotStatus.setVisibility(view.VISIBLE);
//        }
//        StorageReference storageReference =
//                FirebaseStorage.getInstance().getReference();
//        View finalView = view;
//        NotificationViewHolder finalViewHolder = viewHolder;
//        storageReference.child(item.getImage_path()).
//                getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Glide.with(finalView).load(uri.toString()).into(finalViewHolder.imageItem);
//                    }
//                });
//
//        viewHolder.time.setText(item.getTime().toString());
//        viewHolder.content.setText(item.getDescription());
//
//        return view;
//    }
//}

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
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
import com.example.ui.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.errorprone.annotations.ForOverride;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.ProtocolFamily;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationModelAdapter extends RecyclerView.Adapter<NotificationModelAdapter.NotificationViewHolder> {
    private Context context;
    public List<NotificationModel> notificationModelList;

    public NotificationModelAdapter(Context context) {
        this.context = context;
        this.notificationModelList = new ArrayList<>();
    }

    public NotificationModelAdapter(Context context, List<NotificationModel> notificationModelList) {
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

    public void addData(NotificationModel data) {
        this.notificationModelList.add(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.notificationModelList.clear();
        notifyDataSetChanged();
    }

   @Override
   public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_item, parent, false);
       return new NotificationViewHolder(view);
   }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel item = this.notificationModelList.get(position);
        Log.d("count", "2 two times");
        Log.d("description", item.getDescription());
        Log.d("time", item.getTime().toDate().toString());
        Log.d("seen", String.valueOf(item.getSeen()));
        if (item == null) return;

        if (item.getSeen()) {
            holder.dotStatus.setVisibility(View.INVISIBLE);
        } else {
            holder.dotStatus.setVisibility(View.VISIBLE);
        }
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference();
        NotificationViewHolder finalHolder = holder;
        Context finalContext = this.context;
//        storageReference.child(item.getImage_path()).
//                getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        if (uri != null) {
//                            Glide.with(finalContext).load(uri.toString()).into(finalHolder.imageItem);
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Firebase Storage", "Error downloading image: " + e.getMessage());
//                    }
//                });

//        holder.time.setText(convertTimeFormat(item.getTime()));
        holder.content.setText(item.getDescription());
        holder.time.setText(item.getTime().toDate().toString());
    }

    private String convertTimeFormat(Timestamp time) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String formatted = sfd.format(new Date(String.valueOf(time)));
        return formatted;
    }

    @Override
    public int getItemCount() {
        if (this.notificationModelList != null) {
            return notificationModelList.size();
        }
        return 0;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout item;
        public ImageView dotStatus;
        public CircleImageView imageItem;
        public TextView time, content;

        public NotificationViewHolder(View itemView) {
            super(itemView);

//            item = itemView.findViewById(R.id.noti_item);
            dotStatus = itemView.findViewById(R.id.noti_item_dot_status);
//            imageItem = itemView.findViewById(R.id.img_noti_item);
            time = itemView.findViewById(R.id.txt_noti_item_time);
            content = itemView.findViewById(R.id.txt_noti_item_content);
        }
    }
}
