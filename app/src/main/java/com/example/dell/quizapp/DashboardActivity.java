package com.example.dell.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    public static String INTENT_TITLE_TAG = "title";

    private FirebaseAuth mAuth;

    private CardView studyCardView;
    private CardView practiceCardView;
    private CardView examCardView;
    private CardView questionBankCardView;
    private CardView scoreCardView;
    private CardView forumCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initialize();
    }

    private void checkLogin() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        checkLogin();

        studyCardView = findViewById(R.id.cardview_study);
        studyCardView.setOnClickListener(this);

        practiceCardView = findViewById(R.id.cardview_practice);
        practiceCardView.setOnClickListener(this);

        examCardView = findViewById(R.id.cardview_exam);
        examCardView.setOnClickListener(this);

        questionBankCardView = findViewById(R.id.cardview_questionbank);
        questionBankCardView.setOnClickListener(this);

        scoreCardView = findViewById(R.id.cardview_score);
        scoreCardView.setOnClickListener(this);

        forumCardView = findViewById(R.id.cardview_forum);
        forumCardView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardview_study:
                Intent studyIntent = new Intent(DashboardActivity.this, SubjectsActivity.class);
                studyIntent.putExtra(INTENT_TITLE_TAG, "Study");
                studyIntent.putExtra(BaseQuestionPageActivity.QUESTION_TYPE_KEY, BaseQuestionPageActivity.QUESTION_TYPE_STUDY);
                startActivity(studyIntent);
                break;
            case R.id.cardview_practice:
                Intent practiceIntent = new Intent(DashboardActivity.this, SubjectsActivity.class);
                practiceIntent.putExtra(INTENT_TITLE_TAG, "Practice");
                practiceIntent.putExtra(BaseQuestionPageActivity.QUESTION_TYPE_KEY, BaseQuestionPageActivity.QUESTION_TYPE_PRACTICE);
                startActivity(practiceIntent);
                break;
            case R.id.cardview_exam:
                Intent examIntent = new Intent(this, BaseQuestionPageActivity.class);
                examIntent.putExtra(BaseQuestionPageActivity.QUESTION_TYPE_KEY, BaseQuestionPageActivity.QUESTION_TYPE_EXAM);
                startActivity(examIntent);
                break;
            case R.id.cardview_questionbank:
                break;
            case R.id.cardview_score:
                break;
            case R.id.cardview_forum:
                break;
            default:
                break;
        }
    }

}