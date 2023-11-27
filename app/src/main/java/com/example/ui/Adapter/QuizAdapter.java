package com.example.ui.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Model.QuizModel;
import com.example.ui.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuizModel> quizModelList;
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
        QuizModel quizModel = this.quizModelList.get(position);
        List<String> answers = quizModel.getAnswer();

//        int true_answer = 0;
//        for (int i = 0; i < answers.size(); ++i) {
//            if (answers.get(i) == quizModel.getTrue_answer()) {
//                true_answer = i;
//                break;
//            }
//        }

        viewHolder.quizContent.setText(quizModel.getQuestion());
        for (int i = 0; i < viewHolder.quizAnswers.size(); ++i) {
            viewHolder.quizAnswers.get(i).setText(answers.get(i));
            int finalI = i;
            viewHolder.rlAnswers.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int choseAnswer = choseAnswerList.get(finalI);
                    if (choseAnswer == 0) {
                        viewHolder.rlAnswers.get(finalI).
                                setBackgroundResource(R.drawable.bg_choose_answer_item);
                        setChoseAnswer(viewHolder.getAdapterPosition(), finalI);
                    }
                }
            });
        }
    }

    public boolean checkAnswer(QuizModel quizModel, int choseAnswer) {
        if (quizModel.getAnswer().get(choseAnswer) == quizModel.getTrue_answer()) {
            return true;
        }
        return false;
    }

    public void showResult(int position, int choseAnswer) {
        QuizModel quizModel = quizModelList.get(position);
        if (checkAnswer(quizModel, choseAnswer)) {

        }
    }

    @Override
    public int getItemCount() {
        if (this.quizModelList != null) {
            return this.quizModelList.size();
        }
        return 0;
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        public TextView quizContent;
//                        quizAnswerA, quizAnswerB, quizAnswerC, quizAnswerD;
//        public RelativeLayout rlAnswerA, rlAnswerB, rlAnswerC, rlAnswerD;
        public List<RelativeLayout> rlAnswers;
        public List<TextView> quizAnswers;

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
