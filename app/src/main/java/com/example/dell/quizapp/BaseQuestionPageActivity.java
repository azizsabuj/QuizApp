package com.example.dell.quizapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.quizapp.quiz.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BaseQuestionPageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BaseQuestionPage";

    private FirebaseFirestore db;
    private CollectionReference questionsRef;

    private boolean questionsLoaded = false;
    private int nowOnQuestionNumberAt = 0;
    private boolean bookmarked = false;

    private ViewGroup questionView;
    private ProgressBar progressBar;

    private TextView questionText;
    private TextView optionAText;
    private TextView optionBText;
    private TextView optionCText;
    private TextView optionDText;

    private ViewGroup previousButton;
    private ViewGroup gotoButton;
    private ViewGroup bookmarkButton;
    private ViewGroup nextButton;

    private ImageView bookmarkIcon;

    private AlertDialog gotoDialog;

    private EditText gotoEditText;

    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_question_page);

        db = FirebaseFirestore.getInstance();
        questionsRef = db.collection("Questions");

        questions = new ArrayList<>();

        initialize();

        progressBar.setVisibility(View.VISIBLE);

        loadQuestions();
    }

    private void initialize() {
        questionText = findViewById(R.id.question_text);
        optionAText = findViewById(R.id.option_a_text);
        optionBText = findViewById(R.id.option_b_text);
        optionCText = findViewById(R.id.option_c_text);
        optionDText = findViewById(R.id.option_d_text);

        questionView = findViewById(R.id.questionView);
        questionView.setVisibility(View.GONE);

        progressBar = findViewById(R.id.questionLoadProgressbar);

        previousButton = findViewById(R.id.previous_button);
        previousButton.setOnClickListener(this);

        gotoButton = findViewById(R.id.goto_button);
        gotoButton.setOnClickListener(this);

        bookmarkButton = findViewById(R.id.bookmark_button);
        bookmarkButton.setOnClickListener(this);

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        bookmarkIcon = findViewById(R.id.bookmark_icon);
    }

    private void setQuestion(int i) {
        Question question = questions.get(i);

        questionText.setText(question.getQuestion());
        optionAText.setText(question.getOption_a());
        optionBText.setText(question.getOption_b());
        optionCText.setText(question.getOption_c());
        optionDText.setText(question.getOption_d());

        questionView.setVisibility(View.VISIBLE);
    }

    private void startPractice() {
        setQuestion(nowOnQuestionNumberAt);
    }

    private void loadQuestions() {
        questionsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                Question question = documentSnapshot.toObject(Question.class);
                                questions.add(question);

                                Log.d("question", question.getQuestion());
                                Log.d("option a", question.getOption_a());
                                Log.d("option b", question.getOption_b());
                                Log.d("option c", question.getOption_c());
                                Log.d("option d", question.getOption_d());
                                Log.d("answer", question.getAnswer());
                            }
                            questionsLoaded = true;
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "Successfully loaded questions");

                            startPractice();

                        } else {
                            questionsLoaded = false;
                            Toast.makeText(BaseQuestionPageActivity.this, "Failed loading questions", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Failed loading questions");
                        }

                    }
                });
    }

    private void goNext() {
        if (nowOnQuestionNumberAt == questions.size() - 1) {
            nowOnQuestionNumberAt = 0;
        } else {
            nowOnQuestionNumberAt++;
        }
        setQuestion(nowOnQuestionNumberAt);
    }

    private void goPrevious() {
        if (nowOnQuestionNumberAt == 0) {
            nowOnQuestionNumberAt = questions.size() - 1;
        } else {
            nowOnQuestionNumberAt--;
        }
        setQuestion(nowOnQuestionNumberAt);
    }

    private void bookmark() {
        if (bookmarked) {
            bookmarked = false;
            bookmarkIcon.setImageResource(R.drawable.ic_bookmark);
        } else {
            bookmarked = true;
            bookmarkIcon.setImageResource(R.drawable.ic_bookmarked);
        }
    }

    private void gotoAt() {
        if (gotoDialog == null) {
            View dialogView = this.getLayoutInflater().inflate(R.layout.goto_dialog_layout, null);
            gotoEditText = dialogView.findViewById(R.id.goto_number);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setView(dialogView)
                    .setTitle("Go to")
                    .setPositiveButton(getString(R.string.go), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String numberString = gotoEditText.getText().toString();
                            if (numberString.isEmpty()) {
                                Toast.makeText(BaseQuestionPageActivity.this, "Did not enter a number.", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (Integer.parseInt(numberString) <= 0 || Integer.parseInt(numberString) > questions.size()) {
                                Toast.makeText(BaseQuestionPageActivity.this, "Number must between 1-" + (questions.size()), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            setQuestion(Integer.parseInt(numberString) - 1);

                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gotoDialog.hide();
                        }
                    });
            gotoDialog = dialogBuilder.create();
        }
        gotoDialog.show();

    }

    @Override
    public void onClick(View view) {
        if (questionsLoaded) {
            switch (view.getId()) {
                case R.id.previous_button:
                    goPrevious();
                    break;
                case R.id.next_button:
                    goNext();
                    break;
                case R.id.bookmark_button:
                    bookmark();
                    break;
                case R.id.goto_button:
                    gotoAt();
                    break;
                default:
                    break;
            }
        }
    }
}