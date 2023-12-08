package com.example.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.ui.Helper.AreaHelper;
import com.example.ui.IntroContentActivity;
import com.example.ui.Model.LocalAreaModel;
import com.example.ui.Model.ExhibitModel;
import com.example.ui.R;
import com.example.ui.ShowExhibitActivity;
import com.example.ui.ShowLocalAreaActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubAreaAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<LocalAreaModel> listDataHeader; // Header titles
    private HashMap<LocalAreaModel, List<ExhibitModel>> listDataChild; // Child data
    private AreaHelper areaHelper;

    public SubAreaAdapter(Context context, AreaHelper areaHelper, List<LocalAreaModel> listDataHeader) {
        this.context = context;
        this.areaHelper = areaHelper;
        this.listDataHeader = listDataHeader;
        this.listDataChild = new HashMap<>();

        for (LocalAreaModel localAreaModel : listDataHeader) {
            fetchExhibitModels(localAreaModel);
        }
    }

    private void fetchExhibitModels(final LocalAreaModel localAreaModel) {
        areaHelper.getExhibitModels(localAreaModel, new AreaHelper.OnExhibitModelsRetrievedListener() {
            @Override
            public void onExhibitsRetrieved(List<ExhibitModel> exhibitModels) {
                listDataChild.put(localAreaModel, exhibitModels);
                System.out.println(exhibitModels.get(0).getName());
            }

            @Override
            public void onExhibitsNotFound() {
                // Handle the case when no exhibits are found for the given local area
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
            }
        });
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<ExhibitModel> exhibitModels = listDataChild.get(listDataHeader.get(groupPosition));
        return exhibitModels.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_artifact_item, null);
        }

        TextView groupTextView = convertView.findViewById(R.id.category_item_name);
        groupTextView.setText(listDataHeader.get(groupPosition).getName());
        TextView categoryDetailTextView = convertView.findViewById(R.id.category_detail);
        categoryDetailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý khi nút "Chi tiết" được bấm
                // Chuyển sang ShowLocalAreaActivity
                Intent intent = new Intent(context, ShowLocalAreaActivity.class);
                intent.putExtra("localArea",(LocalAreaModel) getGroup(groupPosition));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sub_sub_category_item, null);
        }

        if (listDataChild.containsKey(listDataHeader.get(groupPosition))) {
            List<ExhibitModel> exhibitModels = listDataChild.get(listDataHeader.get(groupPosition));

            // Check if exhibitModels is not null and not empty
            if (exhibitModels != null && !exhibitModels.isEmpty() && exhibitModels.size() > childPosition) {
                // Continue processing only if the childPosition is within bounds
                TextView childTextView = convertView.findViewById(R.id.heading_sub_item);
                childTextView.setText(exhibitModels.get(childPosition).getName());

                TextView contentText = convertView.findViewById(R.id.txt_noti_item_time);
                contentText.setText(exhibitModels.get(childPosition).getDescription());

                ImageView imageView = convertView.findViewById(R.id.img_sub_item);
//                if(exhibitModels.get(childPosition).getImage() != null) {
//                    Glide.with(context)
//                            .load(exhibitModels.get(childPosition).getImage())
//                            .into(imageView);
//                }


            }
        }

        // Add OnClickListener for child item
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the event when the user clicks on a child item
                handleChildItemClick((ExhibitModel) getChild(groupPosition, childPosition));
            }
        });

        return convertView;
    }


    void handleChildItemClick(ExhibitModel exhibitModel) {
        System.out.println(exhibitModel.getName());
        Intent intent = new Intent(context, ShowExhibitActivity.class);
        intent.putExtra("exhibit", exhibitModel);
        context.startActivity(intent);

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void getImage(String image_path, ImageView imageView) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(image_path);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    System.out.println(imageView);
                    // Uri tải xuống thành công, sử dụng Glide để hiển thị ảnh
                    Glide.with(context)
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