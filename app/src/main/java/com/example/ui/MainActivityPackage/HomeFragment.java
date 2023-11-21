package com.example.ui.MainActivityPackage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ui.Helper.NewsHelper;
import com.example.ui.IntroContentActivity;
import com.example.ui.MapActivity;
import com.example.ui.R;
import com.example.ui.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;

    private NewsHelper newsHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        newsHelper = new NewsHelper();
        initNews();
        binding = FragmentHomeBinding.bind(rootView);
        getIntroContentView();

        return rootView;
    }

    protected void getIntroContentView() {
        binding.homeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        binding.homeIntroMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "");
                startActivity(intent);
            }
        });
        binding.introCardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Chienluoc");
                startActivity(intent);
            }
        });
        binding.introCardview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Lichsu");
                startActivity(intent);
            }
        });
        binding.introCardview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Khonggian");
                startActivity(intent);
            }
        });
        binding.introCardview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Nhansu");
                startActivity(intent);
            }
        });
        binding.introCardview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Hoptac");
                startActivity(intent);
            }
        });
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
