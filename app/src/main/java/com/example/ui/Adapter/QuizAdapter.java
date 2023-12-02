package com.example.ui.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.ui.Model.QuizModel;
import com.example.ui.QuizActivity;
import com.example.ui.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    public List<QuizModel> quizModelList;
    public List<Integer> choseAnswerList;

    public QuizAdapter() {
        this.quizModelList = new ArrayList<>();
        this.choseAnswerList = new ArrayList<>();
    }

    public QuizAdapter(List<QuizModel> quizModelList, List<Integer> choseAnswer) {
        this.quizModelList = quizModelList;
        this.choseAnswerList = choseAnswer;
    }

    public void setQuizModelList(List<QuizModel> quizModelList) {
        this.quizModelList = quizModelList;
        notifyDataSetChanged();
    }

//    public void setSubmittedAnswerList(List<Boolean> submittedAnswerList) {
//        this.submittedAnswerList = submittedAnswerList;
//        notifyDataSetChanged();
//    }

//    public void setSubmittedAnswer(int position) {
//        this.submittedAnswerList.set(position, true);
//        notifyDataSetChanged();
//    }

    public List<QuizModel> getQuizModelList() {
        return this.quizModelList;
    }

    public void setChoseAnswerList(List<Integer> choseAnswerList) {
        this.choseAnswerList = choseAnswerList;
        notifyDataSetChanged();
    }

    public void setChoseAnswer(int position, int choseAnswer) {
        if (position < this.choseAnswerList.size()) {
            this.choseAnswerList.set(position, choseAnswer);
            notifyDataSetChanged();
        } else {
            Log.e("choose answer", "choseAnswer exceeds size");
        }
    }

    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_content_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder viewHolder, int position) {
        Log.d("position", Integer.toString(position));
        Log.e("quizList", Integer.toString(QuizActivity.choseAnswerList.size()));
        QuizModel quizModel = this.quizModelList.get(position);
        List<String> answers = quizModel.getAnswer();

        Log.d("adapterPostion", Integer.toString(viewHolder.getAdapterPosition()));
        int choseAnswer = choseAnswerList.get(position);
        Log.d("choseAnswer", Integer.toString(choseAnswer));
        boolean submittedAnswer = QuizActivity.submittedAnswerList.get(position);
        viewHolder.quizContent.setText(quizModel.getQuestion());
        Log.d("quizAnswers", Integer.toString(viewHolder.quizAnswers.size()));
        if (submittedAnswer) {
            showResult(viewHolder.getAdapterPosition());
        } else {
            for (int i = 0; i < viewHolder.quizAnswers.size(); ++i) {
                if (choseAnswer - 1 == i) {
                    viewHolder.rlAnswers.get(i)
                            .setBackgroundResource(R.drawable.bg_choose_answer_item);
                } else {
                    viewHolder.rlAnswers.get(i)
                            .setBackgroundResource(R.drawable.bg_answer_item);
                }
            }
        }

        for (int i = 0; i < viewHolder.quizAnswers.size(); ++i) {
            viewHolder.quizAnswers.get(i).setText(answers.get(i));

            int finalI = i;
            viewHolder.rlAnswers.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d("submitted", Integer.toString(QuizActivity.choseAnswerList.size()));
                    if (QuizActivity.submittedAnswerList.get(viewHolder.getAdapterPosition()) == false) {
                        Log.d("onClickChoseAnswer", Integer.toString(choseAnswer));
                        Log.d("onClickFinalI", Integer.toString(finalI));
                        if (choseAnswer == 0) {
                            viewHolder.rlAnswers.get(finalI).
                                    setBackgroundResource(R.drawable.bg_choose_answer_item);
                            setChoseAnswer(viewHolder.getAdapterPosition(), finalI + 1);
                        } else if (choseAnswer != finalI + 1) {
                            viewHolder.rlAnswers.get(choseAnswer - 1)
                                    .setBackgroundResource(R.drawable.bg_answer_item);
                            viewHolder.rlAnswers.get(finalI).
                                    setBackgroundResource(R.drawable.bg_choose_answer_item);
                            setChoseAnswer(viewHolder.getAdapterPosition(), finalI + 1);
                        }
                    }
                }
            });
        }
    }

    public boolean checkAnswer(int position) {
        int answer = choseAnswerList.get(position) - 1;
        Log.d("answer", Integer.toString(answer));
//        Log.d("positionTrueAnswer", quizModelList.get(position).getTrue_answer());
        if (quizModelList.get(position) == null) {
            Log.d("quizNull", "null");
        } else {
            if (quizModelList.get(position).getTrue_answer() == null) {
                Log.d("True answer", "null");
            } else {
                Log.d("True answer", quizModelList.get(position).getTrue_answer());
            }
        }
        Log.d("positionAnswer", quizModelList.get(position).getAnswer().get(answer));
        if (quizModelList.get(position).getTrue_answer()
                .equals(quizModelList.get(position).getAnswer().get(answer))) {
            return true;
        }
        return false;
    }

    public void showResult(int position) {
        int answer = choseAnswerList.get(position) - 1;
        Log.e("choseAnswer", Integer.toString(answer + 1));
        if (checkAnswer(position)) {
            QuizViewHolder.rlAnswers.get(answer)
                    .setBackgroundResource(R.drawable.bg_true_answer_item);
        } else {
            QuizViewHolder.rlAnswers.get(answer)
                    .setBackgroundResource(R.drawable.bg_false_answer_item);
        }
    }

    @Override
    public int getItemCount() {
        if (this.quizModelList != null) {
            return this.quizModelList.size();
        }
        return 0;
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        public TextView quizContent;
//                        quizAnswerA, quizAnswerB, quizAnswerC, quizAnswerD;
//        public RelativeLayout rlAnswerA, rlAnswerB, rlAnswerC, rlAnswerD;
        public static List<RelativeLayout> rlAnswers;
        public static List<TextView> quizAnswers;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);

            quizContent = (TextView) itemView.findViewById(R.id.quiz_content);

            rlAnswers = new ArrayList<>();
            quizAnswers = new ArrayList<>();

            rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_a));
            rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_b));
            rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_c));
            rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_d));

            quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_a_content));
            quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_b_content));
            quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_c_content));
            quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_d_content));
        }
    }

}

//public class QuizAdapter extends PagerAdapter {
//    private Context context;
//    public List<QuizModel> quizModelList;
//    public List<Integer> choseAnswerList;
//    private List<RelativeLayout> rlAnswers;
//    private List<TextView> quizAnswers;
//    private TextView quizContent;
//    public QuizAdapter() {
//
//    }
//
//    public QuizAdapter(Context context) {
//        this.context = context;
//        this.quizModelList = new ArrayList<>();
//        this.choseAnswerList = new ArrayList<>();
//    }
//
//    public QuizAdapter(Context context, List<QuizModel> quizModelList, List<Integer> choseAnswerList) {
//        this.context = context;
//        this.quizModelList = quizModelList;
//        this.choseAnswerList = choseAnswerList;
//    }
//
//    public void setQuizModelList(List<QuizModel> quizModelList) {
//        this.quizModelList = quizModelList;
//        notifyDataSetChanged();
//    }
//
////    public void setSubmittedAnswerList(List<Boolean> submittedAnswerList) {
////        this.submittedAnswerList = submittedAnswerList;
////        notifyDataSetChanged();
////    }
//
////    public void setSubmittedAnswer(int position) {
////        this.submittedAnswerList.set(position, true);
////        notifyDataSetChanged();
////    }
//
//    public List<QuizModel> getQuizModelList() {
//        return this.quizModelList;
//    }
//
//    public void setChoseAnswerList(List<Integer> choseAnswerList) {
//        this.choseAnswerList = choseAnswerList;
//        notifyDataSetChanged();
//    }
//
//    public void setChoseAnswer(int position, int choseAnswer) {
//        if (position < this.choseAnswerList.size()) {
//            this.choseAnswerList.set(position, choseAnswer);
//            notifyDataSetChanged();
//        } else {
//            Log.e("choose answer", "choseAnswer exceeds size");
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return this.quizModelList.size();
//    }
//
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view == (LinearLayout) object;
//    }
//
//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.quiz_content_item, container, false);
//        initView(view, position);
//        container.addView(view);
//        return view;
//    }
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        container.removeView((LinearLayout) object);
//    }
//
//    public boolean checkAnswer(int position) {
//        int answer = choseAnswerList.get(position) - 1;
//        Log.d("answer", Integer.toString(answer));
////        Log.d("positionTrueAnswer", quizModelList.get(position).getTrue_answer());
//        if (quizModelList.get(position) == null) {
//            Log.d("quizNull", "null");
//        } else {
//            if (quizModelList.get(position).getTrue_answer() == null) {
//                Log.d("True answer", "null");
//            } else {
//                Log.d("True answer", quizModelList.get(position).getTrue_answer());
//            }
//        }
//        Log.d("positionAnswer", quizModelList.get(position).getAnswer().get(answer));
//        if (quizModelList.get(position).getTrue_answer()
//                .equals(quizModelList.get(position).getAnswer().get(answer))) {
//            return true;
//        }
//        return false;
//    }
//
//    public void showResult(int position) {
//        int answer = choseAnswerList.get(position) - 1;
//        Log.e("choseAnswer", Integer.toString(answer + 1));
//        if (checkAnswer(position)) {
//            rlAnswers.get(answer)
//                    .setBackgroundResource(R.drawable.bg_true_answer_item);
//        } else {
//            rlAnswers.get(answer)
//                    .setBackgroundResource(R.drawable.bg_false_answer_item);
//        }
//    }
//
//    public void initView(View itemView, int position) {
//        Log.d("quizModelInitView", Integer.toString(quizModelList.size()));
//        Log.d("choseAnswerInitView", Integer.toString(choseAnswerList.size()));
//        quizContent = (TextView) itemView.findViewById(R.id.quiz_content);
//
//        rlAnswers = new ArrayList<>();
//        quizAnswers = new ArrayList<>();
//
//        rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_a));
//        rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_b));
//        rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_c));
//        rlAnswers.add((RelativeLayout) itemView.findViewById(R.id.rl_answer_d));
//
//        quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_a_content));
//        quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_b_content));
//        quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_c_content));
//        quizAnswers.add((TextView) itemView.findViewById(R.id.quiz_answer_d_content));
//
//        Log.d("position", Integer.toString(position));
//        Log.e("quizList", Integer.toString(QuizActivity.choseAnswerList.size()));
//        QuizModel quizModel = this.quizModelList.get(position);
//        List<String> answers = quizModel.getAnswer();
//
////        Log.d("adapterPostion", Integer.toString(viewHolder.getAdapterPosition()));
//        Log.d("adapterPostion", Integer.toString(position));
//        int choseAnswer = choseAnswerList.get(position);
//        Log.d("choseAnswer", Integer.toString(choseAnswer));
//        boolean submittedAnswer = QuizActivity.submittedAnswerList.get(position);
////        viewHolder.quizContent.setText(quizModel.getQuestion());
//        quizContent.setText(quizModel.getQuestion());
//        Log.d("quizAnswers", Integer.toString(quizAnswers.size()));
//        if (submittedAnswer) {
//            showResult(position);
//        } else {
//            for (int i = 0; i < quizAnswers.size(); ++i) {
//                if (choseAnswer - 1 == i) {
//                    rlAnswers.get(i)
//                            .setBackgroundResource(R.drawable.bg_choose_answer_item);
//                } else {
//                    rlAnswers.get(i)
//                            .setBackgroundResource(R.drawable.bg_answer_item);
//                }
//            }
//        }
//
//        for (int i = 0; i < quizAnswers.size(); ++i) {
//            quizAnswers.get(i).setText(answers.get(i));
//
//            int finalI = i;
//            rlAnswers.get(i).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Log.d("submitted", Integer.toString(QuizActivity.choseAnswerList.size()));
//                    if (QuizActivity.submittedAnswerList.get(position) == false) {
//                        Log.d("onClickChoseAnswer", Integer.toString(choseAnswer));
//                        Log.d("onClickFinalI", Integer.toString(finalI));
//                        if (choseAnswer == 0) {
//                            rlAnswers.get(finalI).
//                                    setBackgroundResource(R.drawable.bg_choose_answer_item);
//                            setChoseAnswer(position, finalI + 1);
//                        } else if (choseAnswer != finalI + 1) {
//                            rlAnswers.get(choseAnswer - 1)
//                                    .setBackgroundResource(R.drawable.bg_answer_item);
//                            rlAnswers.get(finalI).
//                                    setBackgroundResource(R.drawable.bg_choose_answer_item);
//                            setChoseAnswer(position, finalI + 1);
//                        }
//                    }
//                }
//            });
//        }
//    }
//}
