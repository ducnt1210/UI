package com.example.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.ArtifactAdapter;
import com.example.ui.Adapter.LocalAreaAdapter;
import com.example.ui.Helper.AreaHelper;
import com.example.ui.Model.ExhibitModel;
import com.example.ui.Model.LocalAreaModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ShowLocalAreaActivity extends AppCompatActivity {
    private LocalAreaModel localArea;
    private AreaHelper areaHelper;
    private FirebaseFirestore firestore;
    private TextView name;
    private ArtifactAdapter artifactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_local_area);
        firestore = FirebaseFirestore.getInstance();


        localArea = (LocalAreaModel) getIntent().getSerializableExtra("localArea");
        name = findViewById(R.id.area_name);
        name.setText(localArea.getName());

        System.out.println(localArea.getName());
        // Assuming you have an adapter for the RecyclerViews
        artifactAdapter = new ArtifactAdapter(this); // replace with your adapter
        LocalAreaAdapter contentAdapter = new LocalAreaAdapter(localArea.getDescription()); // replace with your adapter


        // Set adapters and layout managers
        RecyclerView artifactRecyclerView = findViewById(R.id.artifact_recycler_view);
        artifactRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        artifactRecyclerView.setAdapter(artifactAdapter);

        RecyclerView contentRecyclerView = findViewById(R.id.content_recycler_view);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentRecyclerView.setAdapter(contentAdapter);
        loadArtifactData();

    }

    // You can replace the following functions with your own data retrieval logic
    private void loadArtifactData() {
        ArrayList<ExhibitModel> exhibitModels = new ArrayList<>();
        CollectionReference localAreasCollection = firestore.collection("Exhibit");
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (String exhibitId : localArea.getExhibits()) {
            DocumentReference localAreaDocRef = localAreasCollection.document(exhibitId);
            Task<DocumentSnapshot> task = localAreaDocRef.get();
            tasks.add(task);

            task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Convert the document to a LocalAreaModel
                            ExhibitModel exhibitModel = new ExhibitModel(document.getId(),
                                    document.getString("name"),
                                    document.getString("description"),
                                    document.getString("video"),
                                    (ArrayList<String>) document.get("content"),
                                    document.getString("image_path"));

                            exhibitModels.add(exhibitModel);

                            // Update UI if all tasks are complete
                            if (exhibitModels.size() == localArea.getExhibits().size()) {
                                artifactAdapter.submitList(exhibitModels);
                            }
                        }
                    } else {
                        // Handle errors here
                        System.out.println("lỗi 12345");
                    }
                }
            });
        }
    }
}
