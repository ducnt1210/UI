package com.example.ui.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ui.Adapter.NoScrollRecyclerView;
import com.example.ui.Adapter.QuizAdapter;
import com.example.ui.MainActivityPackage.HomeFragment;
import com.example.ui.Model.QuizModel;
import com.example.ui.R;
import com.example.ui.Utils;
import com.example.ui.databinding.ActivityQuizBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    ActivityQuizBinding binding;
    private FirebaseFirestore db;
    private QuizAdapter quizAdapter;
    public static List<QuizModel> quizModelList;
    public static List<Integer> choseAnswerList;
    public static List<Boolean> submittedAnswerList;
    public static boolean correctAnswer = false;
    private NoScrollRecyclerView recyclerViewQuiz;
//    public static HScrollManager layoutManager;
    public static LinearLayoutManager layoutManager;
    public SnapHelper snapHelper;
    private TextView quizHeaderCount,
            quizTime, textViewScoreValue,
            skip, submit;
    public static int countCorrectAnswer = 0;
    private int quizCount = 1;
    private int quizTotal = 0;
    private long remainingTime = 0;
    private CountDownTimer timer;
    private boolean completedQuiz = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_quiz);

        db = FirebaseFirestore.getInstance();
        quizModelList = new ArrayList<>();
        choseAnswerList = new ArrayList<>();
        submittedAnswerList = new ArrayList<>();

        recyclerViewQuiz = (NoScrollRecyclerView) findViewById(R.id.recyclerViewQuiz);
//        viewPagerQuiz = (ViewPager) findViewById(R.id.viewPagerQuiz);
        quizHeaderCount = (TextView) findViewById(R.id.quiz_header_count);
        quizTime = (TextView) findViewById(R.id.quiz_time);
        textViewScoreValue = (TextView) findViewById(R.id.coin_layout).findViewById(R.id.coin);
        skip = (TextView) findViewById(R.id.quiz_skip);
        submit = (TextView) findViewById(R.id.quiz_submit);

        getSupportActionBar().hide();
        textViewScoreValue.setText("" + HomeFragment.scoreModel.getScore());

        quizAdapter = new QuizAdapter(quizModelList, choseAnswerList);
        recyclerViewQuiz.setAdapter(quizAdapter);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewQuiz.setLayoutManager(layoutManager);

        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            String exhibit_id = bundle.getString("id");
            String exhibit_id = "7MHOSWzGQIRyF8Hd47w6";
            assert exhibit_id != "";
            setSnapHelper();
            getQuizList(exhibit_id);
            setClickListeners();
//        }
        Drawable leftDrawable = binding.coinLayout.coin.getCompoundDrawablesRelative()[0];
        binding.coinLayout.coin.setCompoundDrawablesRelative(leftDrawable, null, null, null);
    }

    private void setSnapHelper() {
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewQuiz);
        recyclerViewQuiz.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quizCount = recyclerView.getLayoutManager().getPosition(view) + 1;
                Log.d("quizCountScroll", Integer.toString(quizCount));

                quizHeaderCount.setText("Câu hỏi "
                        + Integer.toString(quizCount) + "/" + Integer.toString(quizTotal));

                if (quizCount < quizTotal) {
                    skip.setText("Bỏ qua");
                } else {
                    skip.setText("Hoàn thành");
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("quizCountScroll", Integer.toString(quizCount));
                submit.setVisibility(View.VISIBLE);
                resumeTimer();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (completedQuiz) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuizActivity.this);
            // Setting Alert Dialog Title
            alertDialogBuilder.setTitle("Xác nhận thoát khỏi trò chơi!");
            // Icon Of Alert Dialog
            alertDialogBuilder.setIcon(R.drawable.tzuki_question);
            // Setting Alert Dialog Message
            alertDialogBuilder.setMessage(
                    "Do chưa hoàn thành trò chơi nên bạn sẽ không được cộng xu!\n"
                            + "Bạn chắc chắn muốn thoát khỏi trò chơi chứ?");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    QuizActivity.super.onBackPressed();
                }
            });

            alertDialogBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private long setTimeForQuizList() {
        return 5 * 1000 * quizTotal;
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void createTimer(long timeInMilliseconds) {
        pauseTimer();
        timer = new CountDownTimer(timeInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                remainingTime = l;
                quizTime.setText("⏱  " + Utils.formatTime(remainingTime));
            }

            @Override
            public void onFinish() {
                completedQuiz = true;
                openCompletedDialog(Gravity.CENTER);
            }
        };
        timer.start();
    }

    private void startTimer() {
        Log.d("startTimer", "startTimer");
        remainingTime = setTimeForQuizList() + 1000;
        createTimer(remainingTime);
    }

    private void resumeTimer() {
        Log.d("resumeTimer", "resumeTimer");
        createTimer(remainingTime);
    }

    private int setCoinForCorrectAnswer(int numberOfCorrectAnswers) {
        return numberOfCorrectAnswers * 10;
    }

    private void updateScore() {
        HomeFragment.scoreModel.setScore(
                HomeFragment.scoreModel.getScore()
                        + setCoinForCorrectAnswer(countCorrectAnswer)
        );
        Utils.updateScore(HomeFragment.scoreModel);
//                     onBackPressed();
    }

    private void setClickListeners() {
         skip.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.d("quizCount", Integer.toString(quizCount));
                 Log.d("quizTotal", Integer.toString(quizTotal));
                 if (quizCount < quizTotal) {
                     if (submittedAnswerList.get(quizCount - 1)) {
                         recyclerViewQuiz.smoothScrollToPosition(quizCount);
                     } else {
                         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuizActivity.this);
                         // Setting Alert Dialog Title
                         alertDialogBuilder.setTitle("Bạn chưa trả lời câu hỏi này!");
                         // Icon Of Alert Dialog
                         alertDialogBuilder.setIcon(R.drawable.tzuki_question);
                         // Setting Alert Dialog Message
                         alertDialogBuilder.setMessage(
                                 "Câu hỏi đã bỏ qua không thể quay lại\n"
                                         + "Bạn chắc chắn muốn bỏ qua chứ?");
                         alertDialogBuilder.setCancelable(true);
                         alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {

                             @Override
                             public void onClick(DialogInterface arg0, int arg1) {
                                 recyclerViewQuiz.smoothScrollToPosition(quizCount);
                             }
                         });

                         alertDialogBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {

                             }
                         });

                         AlertDialog alertDialog = alertDialogBuilder.create();
                         alertDialog.show();
                     }
                 } else {
                     AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuizActivity.this);
                     // Setting Alert Dialog Title
                     alertDialogBuilder.setTitle("Xác nhận hoàn thành");
                     // Icon Of Alert Dialog
                     alertDialogBuilder.setIcon(R.drawable.tzuki_question);
                     // Setting Alert Dialog Message
                     alertDialogBuilder.setMessage("Bạn chắc chắn muốn hoàn thành?");
                     alertDialogBuilder.setCancelable(true);
                     alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {

                         @Override
                         public void onClick(DialogInterface arg0, int arg1) {
                             completedQuiz = true;
                             pauseTimer();
                             updateScore();
                             openCompletedDialog(Gravity.CENTER);
                         }
                     });
