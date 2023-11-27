package com.example.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ui.Adapter.QuizAdapter;
import com.example.ui.Model.QuizModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private QuizAdapter quizAdapter;
    public static List<QuizModel> quizModelList = new ArrayList<>();
    private RecyclerView recyclerViewQuiz;
    private TextView quizHeaderCount,
            quizTime, textViewScoreValue,
            skip, submit;

    private int quizCount = 0;
    private int quizTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        db = FirebaseFirestore.getInstance();
//        quizModelList = new ArrayList<>();

        recyclerViewQuiz = (RecyclerView) findViewById(R.id.recyclerViewQuiz);
        quizHeaderCount = (TextView) findViewById(R.id.quiz_header_count);
        quizTime = (TextView) findViewById(R.id.quiz_time);
        textViewScoreValue = (TextView) findViewById(R.id.textViewScoreValue);
        skip = (TextView) findViewById(R.id.quiz_skip);
        submit = (TextView) findViewById(R.id.quiz_skip);



        quizAdapter = new QuizAdapter(quizModelList, new ArrayList<>());
        recyclerViewQuiz.setAdapter(quizAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewQuiz.setLayoutManager(layoutManager);

        Bundle bundle = getIntent().getExtras();
        bundle = null;
//        if (bundle != null) {
//            String exhibit_id = bundle.getString("id");
            String exhibit_id = "7MHOSWzGQIRyF8Hd47w6";
            assert exhibit_id != "";
            getQuizList(exhibit_id);
//        }
        Log.d("list quiz", Integer.toString(quizAdapter.getItemCount()));
//        quizTotal = quizAdapter.getItemCount();
//        quizModelList = quizAdapter.getQuizModelList();
        Log.d("quizModelList", Integer.toString(quizModelList.size()));

        setSnapHelper();
    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewQuiz);
        recyclerViewQuiz.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quizCount = recyclerView.getLayoutManager().getPosition(view);

                quizHeaderCount.setText("Câu hỏi: "
                        + Integer.toString(quizCount + 1) + "/" + Integer.toString(quizTotal));
            }
        });
    }

    private void getQuizList(String exhibit_id) {
        Task<QuerySnapshot> task = db.collection("Quiz")
                .whereEqualTo("exhibit_id", exhibit_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc: queryDocumentSnapshots) {
                            List<String> answers = (List<String>) doc.get("answer");
                            Collections.shuffle(answers);
                            QuizModel quizModel = new QuizModel(
                                    doc.getId(),
                                    doc.getString("question"),
                                    answers,
                                    doc.getString("true_answer"),
                                    doc.getString("detailed_answer")
                            );
                            quizModelList.add(quizModel);
                            Collections.shuffle(quizModelList);
                        }
                        quizAdapter.setQuizModelList(quizModelList);
                        quizTotal = quizModelList.size();
                        quizHeaderCount.setText("Câu hỏi: "
                                + Integer.toString(quizCount + 1) + "/" + Integer.toString(quizTotal));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Failed Getting Quiz", exhibit_id);
                    }
                });

    }
}