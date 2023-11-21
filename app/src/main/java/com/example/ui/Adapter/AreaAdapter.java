package com.example.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.ui.Helper.AreaHelper;
import com.example.ui.Model.AreaModel;
import com.example.ui.Model.LocalAreaModel;
import com.example.ui.R;

import java.util.HashMap;
import java.util.List;

public class AreaAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<AreaModel> listDataHeader; // Header titles
    private HashMap<AreaModel, List<LocalAreaModel>> listDataChild; // Child data
    private AreaHelper areaHelper;

    public AreaAdapter(Context context) {
        this.context = context;
        this.listDataChild = new HashMap<>();
        this.areaHelper = new AreaHelper();
        fetchData();
    }

    private void fetchData() {
        areaHelper.getAllAreasWithLocalAreas(new AreaHelper.OnAreasRetrievedListener() {
            @Override
            public void onAreasRetrieved(List<AreaModel> areas) {
                listDataHeader = areas;

                // Populate listDataChild with LocalAreaModel data
                for (AreaModel area : areas) {
                    fetchLocalAreaModels(area);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
            }
        });
    }

    private void fetchLocalAreaModels(final AreaModel areaModel) {
        areaHelper.getLocalAreaModels(areaModel, new AreaHelper.OnLocalAreasRetrievedListener() {
            @Override
            public void onLocalAreasRetrieved(List<LocalAreaModel> localAreas) {
                listDataChild.put(areaModel, localAreas);

                // Notify the adapter that the data has changed
                notifyDataSetChanged();
            }

            @Override
            public void onLocalAreasNotFound() {
                // Handle the case when no local areas are found for the given area
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
            }
        });
    }

    @Override
    public int getGroupCount() {
        if (listDataHeader != null) {
            return listDataHeader.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (listDataHeader != null && listDataChild != null) {
            AreaModel groupItem = listDataHeader.get(groupPosition);
            if (groupItem != null && listDataChild.containsKey(groupItem)) {
                List<LocalAreaModel> childItems = listDataChild.get(groupItem);
                if (childItems != null) {
                    return childItems.size();
                }
            }
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (listDataHeader != null) {
            return listDataHeader.get(groupPosition);
        } else {
            return null;
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (listDataHeader != null && listDataChild != null) {
            AreaModel groupItem = listDataHeader.get(groupPosition);
            if (groupItem != null && listDataChild.containsKey(groupItem)) {
                List<LocalAreaModel> childItems = listDataChild.get(groupItem);
                if (childItems != null) {
                    return childItems.get(childPosition);
                }
            }
        }
        return null;
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
        if (listDataHeader != null) {
            String headerTitle = ((AreaModel) getGroup(groupPosition)).getName();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_name, null);
            }

            TextView groupTextView = convertView.findViewById(R.id.category_name);
            groupTextView.setText(headerTitle);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (listDataHeader != null && listDataChild != null) {
            String childText = ((LocalAreaModel) getChild(groupPosition, childPosition)).getName();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_artifact_item, null);
            }

            TextView childTextView = convertView.findViewById(R.id.category_item_name);
            childTextView.setText(childText);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
