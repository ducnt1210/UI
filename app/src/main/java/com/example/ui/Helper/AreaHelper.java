package com.example.ui.Helper;

import com.example.ui.Model.AreaModel;
import com.example.ui.Model.ExhibitModel;
import com.example.ui.Model.LocalAreaModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AreaHelper {

    private FirebaseFirestore firestore;

    public AreaHelper() {
        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();
    }

    public void getAllAreasWithLocalAreas(final OnAreasRetrievedListener onAreasRetrievedListener) {
        // Reference to the "areas" collection
        CollectionReference areasCollection = firestore.collection("Area");

        areasCollection.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
            @Override
            public void onComplete(Task<com.google.firebase.firestore.QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<AreaModel> areas = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Convert each document to an AreaModel
                        AreaModel area = new AreaModel(document.getId(),
                                document.getString("name"),
                                (ArrayList<String>) document.get("localArea"),
                                (ArrayList<String>) document.get("description")
                        );
                        System.out.println(area);

                        // Add the area to the list
                        areas.add(area);
                    }

                    // Callback with the list of AreaModels
                    onAreasRetrievedListener.onAreasRetrieved(areas);
                } else {
                    // Handle errors here
                    System.out.println("lỗi cmnr");
                    onAreasRetrievedListener.onError(task.getException().getMessage());
                }
            }
        });
    }

    public void getLocalAreaModels(final AreaModel areaModel, final OnLocalAreasRetrievedListener onLocalAreasRetrievedListener) {
        // Reference to the "localAreas" collection
        CollectionReference localAreasCollection = firestore.collection("LocalArea");

        // Get the localAreaIds from the AreaModel
        List<String> localAreaIds = areaModel.getLocalAreaIds();

        final List<LocalAreaModel> localAreas = new ArrayList<>();

        // Fetch each LocalAreaModel based on its ID
        if(localAreaIds.size() > 0) {
            for (String localAreaId : localAreaIds) {
                DocumentReference localAreaDocRef = localAreasCollection.document(localAreaId);

                localAreaDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Convert the document to a LocalAreaModel
                                LocalAreaModel localAreaModel = new LocalAreaModel(document.getId(),
                                        document.getString("name"),
                                        (ArrayList<String>) document.get("exhibits"),
                                        (ArrayList<String>) document.get("description"));

                                // Add the LocalAreaModel to the list
                                localAreas.add(localAreaModel);

                                // Check if all LocalAreaModels have been retrieved
                                if (localAreas.size() == areaModel.getLocalAreaIds().size()) {
                                    // Callback with the list of LocalAreaModels
                                    onLocalAreasRetrievedListener.onLocalAreasRetrieved(localAreas);
                                }
                            }
                        } else {
                            // Handle errors here
                            System.out.println("lỗi 12345");
                            onLocalAreasRetrievedListener.onError(task.getException().getMessage());
                        }
                    }
                });
            }
        }
    }

    public void getExhibitModels(final LocalAreaModel localAreaModel, final OnExhibitModelsRetrievedListener onExhibitModelsRetrievedListener) {
        // Reference to the "localAreas" collection
        CollectionReference localAreasCollection = firestore.collection("Exhibit");

        // Get the localAreaIds from the AreaModel
        List<String> exhibitIds = localAreaModel.getExhibits();

        final List<ExhibitModel> exhibitModels = new ArrayList<>();

        // Fetch each LocalAreaModel based on its ID
        if(exhibitIds.size() > 0) {
            for (String exhibitId : exhibitIds) {
                DocumentReference localAreaDocRef = localAreasCollection.document(exhibitId);

                localAreaDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Convert the document to a LocalAreaModel
                                ExhibitModel exhibitModel = new ExhibitModel(document.getId(),
                                        document.getString("name"),
                                        document.getString("description"),
                                        document.getString("video"),
                                        (ArrayList<String>) document.get("content"),
                                        document.getString("image_path"));

                                // Add the LocalAreaModel to the list
                                exhibitModels.add(exhibitModel);

                                // Check if all LocalAreaModels have been retrieved
                                if (exhibitModels.size() == localAreaModel.getExhibits().size()) {
                                    // Callback with the list of LocalAreaModels
                                    onExhibitModelsRetrievedListener.onExhibitsRetrieved(exhibitModels);
                                }
                            }
                        } else {
                            // Handle errors here
                            System.out.println("lỗi 12345");
                            onExhibitModelsRetrievedListener.onError(task.getException().getMessage());
                        }
                    }
                });
            }
        }
    }

    // Interface to define callback methods for retrieving AreaModels
    public interface OnAreasRetrievedListener {
        void onAreasRetrieved(List<AreaModel> areas);

        void onError(String errorMessage);
    }

    // Interface to define callback methods for retrieving LocalAreaModels
    public interface OnLocalAreasRetrievedListener {
        void onLocalAreasRetrieved(List<LocalAreaModel> localAreas);

        void onLocalAreasNotFound();

        void onError(String errorMessage);
    }

    public interface OnExhibitModelsRetrievedListener {
        void onExhibitsRetrieved(List<ExhibitModel> exhibitModels);
        void onExhibitsNotFound();
        void onError(String errorMessage);

    }
}