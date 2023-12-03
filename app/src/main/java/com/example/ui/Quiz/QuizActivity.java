package com.example.ui.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ui.Adapter.HScrollManager;
import com.example.ui.Adapter.QuizAdapter;
import com.example.ui.Model.QuizModel;
import com.example.ui.R;
import com.example.ui.databinding.ActivityQuizBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuizActivity extends AppCompatActivity {
    ActivityQuizBinding binding;
    private FirebaseFirestore db;
    private QuizAdapter quizAdapter;
    private ViewPager viewPagerQuiz;
    private List<QuizModel> quizModelList;
    public static List<Integer> choseAnswerList;
    public static List<Boolean> submittedAnswerList;
    private RecyclerView recyclerViewQuiz;
    public static HScrollManager layoutManager;
    private TextView quizHeaderCount,
            quizTime, textViewScoreValue,
            skip, submit;

    private int quizCount = 1;
    private int quizTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_quiz);

        db = FirebaseFirestore.getInstance();
        quizModelList = new ArrayList<>();
        choseAnswerList = new ArrayList<>();
        submittedAnswerList = new ArrayList<>();

        recyclerViewQuiz = (RecyclerView) findViewById(R.id.recyclerViewQuiz);
//        viewPagerQuiz = (ViewPager) findViewById(R.id.viewPagerQuiz);
        quizHeaderCount = (TextView) findViewById(R.id.quiz_header_count);
        quizTime = (TextView) findViewById(R.id.quiz_time);
        textViewScoreValue = (TextView) findViewById(R.id.coin_layout).findViewById(R.id.coin);
        skip = (TextView) findViewById(R.id.quiz_skip);
        submit = (TextView) findViewById(R.id.quiz_submit);

        getSupportActionBar().hide();

        quizAdapter = new QuizAdapter(quizModelList, choseAnswerList);
        recyclerViewQuiz.setAdapter(quizAdapter);
//        viewPagerQuiz.setAdapter(quizAdapter);
//        viewPagerQuiz.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                quizCount = getViewPagerItem(0) + 1;
//                quizHeaderCount.setText("Câu hỏi: "
//                        + Integer.toString(quizCount) + "/" + Integer.toString(quizTotal));
//            }
//        });

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerViewQuiz.setLayoutManager(layoutManager);
        layoutManager = new HScrollManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewQuiz.setLayoutManager(layoutManager);
        layoutManager.setScrollingEnabled(false);


        Bundle bundle = getIntent().getExtras();
        bundle = null;
//        if (bundle != null) {
//            String exhibit_id = bundle.getString("id");
            String exhibit_id = "7MHOSWzGQIRyF8Hd47w6";
            assert exhibit_id != "";
            setSnapHelper();
            getQuizList(exhibit_id);
            startTimer();
            setClickListeners();
//        }
        Drawable leftDrawable = binding.coinLayout.coin.getCompoundDrawablesRelative()[0];
        binding.coinLayout.coin.setCompoundDrawablesRelative(leftDrawable, null, null, null);
    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewQuiz);
        recyclerViewQuiz.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quizCount = recyclerView.getLayoutManager().getPosition(view) + 1;
                Log.d("quizCountScroll", Integer.toString(quizCount));

                quizHeaderCount.setText("Câu hỏi: "
                        + Integer.toString(quizCount) + "/" + Integer.toString(quizTotal));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                quizAdapter.onBindViewHolder(QuizAdapter.QuizViewHolder , );
                layoutManager.setScrollingEnabled(false);
            }


        });
    }

    public int getViewPagerItem(int i) {
        return viewPagerQuiz.getCurrentItem() + i;
    }


    private long setTimeForQuizList() {

        return 30 * 1000 * quizTotal;
    }

    private void setClickListeners() {
         skip.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.d("quizCount", Integer.toString(quizCount));
                 Log.d("quizTotal", Integer.toString(quizTotal));
                 if (quizCount < quizTotal) {
                     layoutManager.setScrollingEnabled(true);
                     recyclerViewQuiz.smoothScrollToPosition(quizCount);
                 }
//                 layoutManager.setScrollingEnabled(false);
             }
         });
//        skip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (quizCount < quizTotal) {
//                    viewPagerQuiz.setCurrentItem(getViewPagerItem(1), true);
//                }
//            }
//        });

         submit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.e("choseAnswer", Integer.toString(quizAdapter.choseAnswerList.get(quizCount - 1)));
                 Log.d("quizList", Integer.toString(quizAdapter.quizModelList.size()));
                 Log.d("subbmittedAnswerList", Integer.toString(submittedAnswerList.size()));
                 if (submittedAnswerList.get(quizCount - 1) == false) {
                     if (quizAdapter.choseAnswerList.get(quizCount - 1) > 0) {
                         submittedAnswerList.set(quizCount - 1, true);
//                         quizAdapter.showResult(quizCount - 1);
                     } else {
                         Toast.makeText(QuizActivity.this, "Vui lòng chọn đáp án trước khi nộp", Toast.LENGTH_SHORT).show();
                     }
                 } else {
                     Toast.makeText(QuizActivity.this, "Câu hỏi đã trả lời không thể nộp", Toast.LENGTH_SHORT).show();
                 }
             }
         });
    }

    private void startTimer() {
        long totalTime = setTimeForQuizList();
        CountDownTimer timer = new CountDownTimer(totalTime + 1000, 1000) {
            @Override
            public void onTick(long l) {
                String time = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))
                );
                quizTime.setText("⏱  " + time);
            }

            @Override
            public void onFinish() {

            }
        };

        timer.start();
    }

    private void getQuizList(String exhibit_id) {
        quizModelList.clear();
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
                            choseAnswerList.add(0);
                            submittedAnswerList.add(false);
                            Collections.shuffle(quizModelList);
                        }
                        quizAdapter.setQuizModelList(quizModelList);
                        quizAdapter.setChoseAnswerList(choseAnswerList);
//                        quizAdapter.setSubmittedAnswerList(submittedAnswerList);
                        quizTotal = quizModelList.size();
                        quizHeaderCount.setText("Câu hỏi: "
                                + Integer.toString(quizCount) + "/" + Integer.toString(quizTotal));
//                        setClickListeners();
                        startTimer();
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