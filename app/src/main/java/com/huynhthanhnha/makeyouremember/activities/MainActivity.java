package com.huynhthanhnha.makeyouremember.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.adapters.NotesAdapter;
import com.huynhthanhnha.makeyouremember.adapters.NotesAdapter2;
import com.huynhthanhnha.makeyouremember.database.NotesDatabase;
import com.huynhthanhnha.makeyouremember.models.Note;
import com.huynhthanhnha.makeyouremember.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompat {

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());


    private ViewPager2 notesContainer;
    private ImageView btnChangeLayout;
    private RecyclerView notesContainerLayout2;
    private ImageView btnCreateNote;
    private CardView itemNoteNothing;
    private ImageView btnSetting;
    private EditText textSearch;
    private ImageView btnQuiz;
    
    private List<Note> notes = new ArrayList<>();
    private List<Note> notesHoldData = new ArrayList<>();
    private NotesAdapter notesAdapter;
    private NotesAdapter2 notesAdapter2;

    private boolean isCard = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChangeLayout = findViewById(R.id.btnChangeLayout);
        notesContainer = findViewById(R.id.notesContainer);
        notesContainerLayout2 = findViewById(R.id.notesContainerLayout2);
        btnCreateNote = findViewById(R.id.btnAdd);
        itemNoteNothing = findViewById(R.id.itemNoteNothing);
        btnSetting = findViewById(R.id.btnSettings);
        textSearch = findViewById(R.id.textSearch);
        btnQuiz = findViewById(R.id.btnQuiz);

        notesContainer.setFocusableInTouchMode(true);
        notesContainer.setFocusable(true);

//        notes = List.of(
//                new Note("Associate with", "Liên quan với", "Từ tiếng anh", "#FCA484"),
//                new Note("Associate with", "Liên quan với", "Từ tiếng anh", "#ffffff"),
//                new Note("Associate with", "Liên quan với", "Từ tiếng anh", "#92F8B0"),
//                new Note("Associate something with somebody", "Liên kết cái gì đó với ai đó", "là gì?", "#A6D2F9"),
//                new Note("Associate with", "Liên quan với", "Từ tiếng anh", "#ffffff"),
//                new Note("Associate with", "Liên quan với", "Từ tiếng anh", "#ffd947")
//        );

        notesAdapter = new NotesAdapter(notes, this);
        notesAdapter2 = new NotesAdapter2(notes, this);

        notesContainer.setAdapter(notesAdapter);
        notesContainerLayout2.setAdapter(notesAdapter2);
        notesContainerLayout2.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        setNotesContainerViewPager2();

        loadNotes();

        btnChangeLayout.setOnClickListener(v -> {
            if(isCard) {
                btnChangeLayout.setImageResource(R.drawable.card);
                notesContainerLayout2.setVisibility(View.VISIBLE);
                notesContainer.setVisibility(View.GONE);
                isCard = false;
            } else {
                btnChangeLayout.setImageResource(R.drawable.grid);
                notesContainerLayout2.setVisibility(View.GONE);
                notesContainer.setVisibility(View.VISIBLE);
                isCard = true;
            }
        });

        btnCreateNote.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
            startActivity(intent);
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        });

        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString();
                notes.clear();
                for(Note note : notesHoldData) {
                    if((note.getKey() + note.getValue()).toLowerCase().contains(key.toLowerCase())) {
                        notes.add(note);
                    }
                }
                notesAdapter.notifyDataSetChanged();
                notesAdapter2.notifyDataSetChanged();
            }
        });

        btnQuiz.setOnClickListener(v -> {
            if(notesHoldData.size() >= 4) {
                Intent intent = new Intent(this, QuizActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Need 4 notes to create a quiz!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onRestart() {
        super.onRestart();
        loadNotes();
        notesContainer.setFocusableInTouchMode(true);
        notesContainer.setFocusable(true);
        recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setNotesContainerViewPager2() {
        notesContainer.setClipToPadding(false);
        notesContainer.setClipChildren(false);
        notesContainer.setOffscreenPageLimit(3);
        notesContainer.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        notesContainer.setPageTransformer(compositePageTransformer);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged")
    private void loadNotes() {
        executor.execute(() -> {
            List<Note> notesDb = NotesDatabase.getDatabase(getApplicationContext()).getNoteDao().getNotes();
            List<Note> notesDbIsNotDelete = notesDb.stream().filter(m -> !m.isDelete()).collect(Collectors.toList());
            handler.post(() -> {
                notes.clear();
                notes.addAll(notesDbIsNotDelete);
                notesHoldData.addAll(notes);
                notesAdapter.notifyDataSetChanged();
                notesAdapter2.notifyDataSetChanged();
                if(notes.size() > 0) {
                    itemNoteNothing.setVisibility(View.GONE);
                } else {
                    itemNoteNothing.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    public void updateFavorite(int noteId) {
        executor.execute(() -> {
            NotesDatabase.getDatabase(getApplicationContext()).getNoteDao().toggleFavorite(noteId);
        });
    }


}