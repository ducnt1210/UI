package com.example.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Model.ExhibitModel;
import com.example.ui.R;
import com.example.ui.ShowExhibitActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    private List<ExhibitModel> dataList;
    private List<ExhibitModel> dataListFull; // Dữ liệu đầy đủ để lọc
    private Context context;

    public SearchAdapter(Context context, List<ExhibitModel> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.dataListFull = new ArrayList<>(dataList); // Sử dụng new ArrayList<>(dataList) để sao chép dữ liệu
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sub_sub_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExhibitModel item = dataList.get(position);

        // Bind data to views
        holder.titleTextView.setText(item.getName());
        holder.descriptionTextView.setText(item.getDescription());
        // Set image (you need to handle image loading here)

        // Add click listener if needed
        holder.itemView.setOnClickListener(v -> {
            handleChildItemClick(item);
        });
        if(item.getImage() != null) {
            Glide.with(context)
                    .load(Uri.parse(item.getImage()))
                    .into(holder.imageView);
        }


    }

    void handleChildItemClick(ExhibitModel exhibitModel) {
        System.out.println(exhibitModel.getName());
        Intent intent = new Intent(context, ShowExhibitActivity.class);
        intent.putExtra("exhibit", exhibitModel);
        context.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        System.out.println(dataList.size());
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_sub_item);
            titleTextView = itemView.findViewById(R.id.heading_sub_item);
            descriptionTextView = itemView.findViewById(R.id.txt_noti_item_time);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ExhibitModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // Nếu không có chuỗi tìm kiếm, sử dụng toàn bộ danh sách
                filteredList.addAll(dataListFull);
            } else {
                // Lọc danh sách theo chuỗi tìm kiếm
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ExhibitModel item : dataListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataList.clear();
            dataList.addAll((List<ExhibitModel>) results.values);
            notifyDataSetChanged();
        }
    };
}