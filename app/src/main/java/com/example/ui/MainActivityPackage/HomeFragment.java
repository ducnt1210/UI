package com.example.ui.MainActivityPackage;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.ui.Helper.NewsHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.ui.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class HomeFragment extends Fragment {

    private NewsHelper newsHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        newsHelper = new NewsHelper();
        initNews();
        return view;
    }

    public void initNews() {
        newsHelper.fetchLatestNews(new NewsHelper.NewsDataCallback() {
            @Override
            public void onDataLoaded(List<DocumentSnapshot> newsList) {
                System.out.println(newsList.size());
                updateCardViews(newsList);
            }
        });
    }

    private void updateCardViews(List<DocumentSnapshot> newsList) {
        // Iterate through the list of news and update CardViews
        for (int i = 0; i < Math.min(newsList.size(), 6); i++) {
            DocumentSnapshot document = newsList.get(i);
            Log.d("abc", document.getString("title"));

            // Update your CardView with Firestore data
            updateCardView(document, i + 1); // Assuming i + 1 is the correct index for your CardViews
        }
    }

    private void updateCardView(DocumentSnapshot document, int cardIndex) {
        // Update your CardView with Firestore data
        View view = getView();
        if (view != null) {
            CardView cardView = view.findViewById(getCardViewId(cardIndex));
            ImageView img = cardView.findViewById(getImageId(cardIndex));
            TextView text = cardView.findViewById(getTextId(cardIndex));

            text.setText(document.getString("title"));
            String imageUrl = document.getString("image_path");

            // Sử dụng Glide để tải và hiển thị ảnh
            Glide.with(this)
                    .load(imageUrl)
                    .into(img);
        }
    }

    // Helper methods to get IDs based on card index
    private int getCardViewId(int index) {
        return getResources().getIdentifier("new_cardview" + index, "id", requireActivity().getPackageName());
    }

    private int getImageId(int index) {
        return getResources().getIdentifier("new_img" + index, "id", requireActivity().getPackageName());
    }

    private int getTextId(int index) {
        return getResources().getIdentifier("new_text" + index, "id", requireActivity().getPackageName());
    }
}
