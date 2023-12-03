package com.example.ui.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ui.Adapter.HScrollManager;
import com.example.ui.Adapter.NoScrollRecyclerView;
import com.example.ui.Adapter.QuizAdapter;
import com.example.ui.MainActivity;
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

    private int quizCount = 1;
    private int quizTotal = 0;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume", "resume");
    }

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
        textViewScoreValue.setText("" + MainActivity.scoreModel.getScore());

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

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewQuiz.setLayoutManager(layoutManager);

//        layoutManager = new HScrollManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerViewQuiz.setLayoutManager(layoutManager);
//        layoutManager.setScrollingEnabled(false);


        Bundle bundle = getIntent().getExtras();
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
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                skip.setText("Bỏ qua");
                submit.setVisibility(View.VISIBLE);
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
                     recyclerViewQuiz.smoothScrollToPosition(quizCount);
                 } else {
                     FirebaseFirestore.getInstance().collection("Score")
                             .document(MainActivity.scoreModel.getId())
                             .set(MainActivity.scoreModel)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {

                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Log.e("Update score failed", MainActivity.scoreModel.getId());
                                 }
                             });
                     onBackPressed();
                 }
             }
         });

         submit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.e("choseAnswer", Integer.toString(quizAdapter.choseAnswerList.get(quizCount - 1)));
                 Log.d("quizList", Integer.toString(quizAdapter.quizModelList.size()));
                 Log.d("subbmittedAnswerList", Integer.toString(submittedAnswerList.size()));
                 Log.e("quizModelList", Integer.toString(quizModelList.size()));
                 if (submittedAnswerList.get(quizCount - 1) == false) {
                     if (quizAdapter.choseAnswerList.get(quizCount - 1) > 0) {
                         submittedAnswerList.set(quizCount - 1, true);
                         final Handler handler = new Handler();
                         handler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 // Do something after 5s = 5000ms
                                 quizAdapter.showResult(quizCount - 1);
                                 submit.setVisibility(View.GONE);
                                 textViewScoreValue.setText("" + MainActivity.scoreModel.getScore());
                                 final Handler handlerDialog = new Handler();
                                 handler.postDelayed(new Runnable() {
                                     @Override
                                     public void run() {
                                         openDialog(Gravity.CENTER);
                                     }
                                 }, 300);
                             }
                         }, 500);
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
        db.collection("Quiz")
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
                                    exhibit_id,
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

    public void openDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detail_answer);

        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        ImageView imageViewDetailedAnswer = (ImageView) dialog.findViewById(R.id.imageViewDetailedAnswer);
        TextView textViewCheckCorrect = (TextView) dialog.findViewById(R.id.check_correct);
        TextView textViewShowCorrectAnswer = (TextView) dialog.findViewById(R.id.show_correct_answer);
        TextView textViewDetailedAnswer = (TextView) dialog.findViewById(R.id.detailed_answer);
        TextView textViewSkip = (TextView) dialog.findViewById(R.id.dialog_skip);

        QuizModel quizModel = quizAdapter.quizModelList.get(quizCount - 1);
        if (correctAnswer) {
            imageViewDetailedAnswer.setImageResource(R.drawable.correct_img);
            textViewCheckCorrect.setText("Câu trả lời chính xác");
        } else {
            imageViewDetailedAnswer.setImageResource(R.drawable.incorrect_img);
            textViewCheckCorrect.setText("Câu trả lời không chính xác");
        }
        textViewShowCorrectAnswer.setText("Đáp án đúng là: " + quizModel.getTrue_answer());
        textViewDetailedAnswer.setText(quizModel.getDetailed_answer());
        if (quizCount < quizTotal) {
            textViewSkip.setText("Tiếp tục");
            skip.setText("Tiếp tục");
        } else {
            textViewSkip.setText("Hoàn thành");
            skip.setText("Hoàn thành");
        }
        textViewSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    skip.callOnClick();
                }
            }
        });

        dialog.show();
    }
}