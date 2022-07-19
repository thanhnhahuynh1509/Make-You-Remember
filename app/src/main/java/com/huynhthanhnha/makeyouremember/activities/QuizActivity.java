package com.huynhthanhnha.makeyouremember.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.dao.NoteDao;
import com.huynhthanhnha.makeyouremember.database.NotesDatabase;
import com.huynhthanhnha.makeyouremember.models.Note;
import com.huynhthanhnha.makeyouremember.models.Quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QuizActivity extends AppCompat {

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    private TextView textQuestion;
    private TextView textA1;
    private TextView textA2;
    private TextView textA3;
    private TextView textA4;

    private LinearLayout btnA1;
    private LinearLayout btnA2;
    private LinearLayout btnA3;
    private LinearLayout btnA4;
    private LinearLayout btnFinish;
    private LinearLayout btnContinue;

    private ImageView imgQuestion;

    private Quiz quiz;
    private Note note;

    private boolean isCompleted = false;

    private List<Note> notes = new ArrayList<>();
    private int correctPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textQuestion = findViewById(R.id.textQuestion);
        textA1 = findViewById(R.id.textAnswer1);
        textA2 = findViewById(R.id.textAnswer2);
        textA3 = findViewById(R.id.textAnswer3);
        textA4 = findViewById(R.id.textAnswer4);
        btnA1 = findViewById(R.id.btnAnswer1);
        btnA2 = findViewById(R.id.btnAnswer2);
        btnA3 = findViewById(R.id.btnAnswer3);
        btnA4 = findViewById(R.id.btnAnswer4);
        btnFinish = findViewById(R.id.btnFinish);
        btnContinue = findViewById(R.id.btnContinue);
        imgQuestion = findViewById(R.id.imgQuestion);

        btnFinish.setOnClickListener(v -> {
            if (isCompleted) {
                onBackPressed();
                finish();
            }
        });

        btnContinue.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });

        setQuestion();

        btnA1.setOnClickListener(v -> {
            handleClick(textA1, btnA1);
        });

        btnA2.setOnClickListener(v -> {
            handleClick(textA2, btnA2);
        });

        btnA3.setOnClickListener(v -> {
            handleClick(textA3, btnA3);
        });

        btnA4.setOnClickListener(v -> {
            handleClick(textA4, btnA4);
        });
    }

    private void handleClick(TextView textA, LinearLayout btnA) {
        if(!isCompleted) {
            String answer = textA.getText().toString();
            if(answer.equals(quiz.getCorrectAnswer())) {
                btnA.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_quiz_correct));
                updateCorrectTime(note.getId(), note.getCorrectTimes() + 1);
            } else {
                btnA.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_quiz_incorrect));
                getCorrectAnswer().setBackground(ContextCompat.getDrawable(this, R.drawable.bg_quiz_correct));
            }
            btnFinish.setVisibility(View.VISIBLE);
            btnFinish.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_finish_active));
            btnContinue.setVisibility(View.VISIBLE);
            isCompleted = true;
        }
    }

    private void setQuestion() {
        executor.execute(() -> {
            notes = NotesDatabase.getDatabase(getApplicationContext()).getNoteDao().getNotes();
            int length = notes.size();
            int currentQuiz = new Random().nextInt(length);
            handler.post(() -> {
                if(notes.size() < 4)
                    finish();

                quiz = new Quiz();
                note = notes.get(currentQuiz);
                if(note.getImage() != null && !note.getImage().isEmpty()) {
                    imgQuestion.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
                } else {
                    setImageRandom();
                }
                imgQuestion.setVisibility(View.VISIBLE);
                quiz.setQuestion(note.getKey() + "?");
                quiz.setCorrectAnswer(note.getValue());
                int i = 0;
                String[] answers = new String[4];
                List<Integer> randomPositions = new ArrayList<>();

                while(i < 4) {
                    int randomAnswers = new Random().nextInt(length);
                    if(randomAnswers == currentQuiz || randomPositions.contains(randomAnswers))
                        continue;
                    answers[i++] = notes.get(randomAnswers).getValue();
                    randomPositions.add(randomAnswers);
                }
                correctPosition = new Random().nextInt(4);
                answers[correctPosition] = note.getValue();

                quiz.setAnswer1(answers[0]);
                quiz.setAnswer2(answers[1]);
                quiz.setAnswer3(answers[2]);
                quiz.setAnswer4(answers[3]);

                textQuestion.setText(quiz.getQuestion());
                textA1.setText(quiz.getAnswer1());
                textA2.setText(quiz.getAnswer2());
                textA3.setText(quiz.getAnswer3());
                textA4.setText(quiz.getAnswer4());
            });
        });

    }

    private LinearLayout getCorrectAnswer() {
        switch (correctPosition) {
            case 0: return btnA1;
            case 1: return btnA2;
            case 2: return btnA3;
            default: return btnA4;
        }
    }

    private void updateCorrectTime(int noteId, int correctTime) {
        executor.execute(() -> {
            NotesDatabase.getDatabase(this).getNoteDao().updateCorrectTime(noteId, correctTime);
        });
    }

    private void setImageRandom() {
        int random = new Random().nextInt(2);
        if(random == 0) {
            imgQuestion.setImageResource(R.drawable.problem);
        } else {
            imgQuestion.setImageResource(R.drawable.problem);
        }
    }

    @Override
    public void onBackPressed() {
        
    }
}