package com.huynhthanhnha.makeyouremember.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.adapters.NotesAdapter2;
import com.huynhthanhnha.makeyouremember.adapters.NotesAdapterRemembered;
import com.huynhthanhnha.makeyouremember.database.NotesDatabase;
import com.huynhthanhnha.makeyouremember.models.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OptionalActivity extends AppCompat {

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    private ImageView btnBack;
    private ImageView btnReTrash;
    private RecyclerView notesContainerOption;
    private TextView textTitle;

    private List<Note> notes = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private String option = "remember";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional);

        btnBack = findViewById(R.id.btnBack);
        notesContainerOption = findViewById(R.id.notesContainerOption);
        textTitle = findViewById(R.id.textTitle);

        option = getIntent().getStringExtra("option");
        textTitle.setText(getTextTitle());

        notesContainerOption.setLayoutManager(new LinearLayoutManager(this));
        adapter = getAdapter();
        notesContainerOption.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
            finish();

        });

        loadNotes();
    }

    private RecyclerView.Adapter getAdapter() {
        switch (option) {
            default:
                return new NotesAdapterRemembered(notes, option, this);
        }

    }

    private String getTextTitle() {
        switch (option) {
            case "favorite":
                return getResources().getString(R.string.favorite_title);
            case "trash":
                return getResources().getString(R.string.trash_title);
            default:
                return getResources().getString(R.string.you_remembered);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadNotes() {
        if(option.equals("remembered")) {
            executor.execute(() -> {
                List<Note> notesDb = loadNotesWithPredicate(m -> m.getCorrectTimes() >= 400);
                handler.post(() -> {
                    notes.addAll(notesDb);
                    adapter.notifyDataSetChanged();
                });
            });
        } else if(option.equals("favorite")) {
            executor.execute(() -> {
                List<Note> notesDb = loadNotesWithPredicate(Note::isFavourite);

                handler.post(() -> {
                    notes.addAll(notesDb);
                    adapter.notifyDataSetChanged();
                });
            });
        } else if(option.equals("trash")) {
            executor.execute(() -> {
                List<Note> notesDb = loadNotesWithPredicate(Note::isDelete);
                handler.post(() -> {
                    notes.addAll(notesDb);
                    adapter.notifyDataSetChanged();
                });
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Note> loadNotesWithPredicate(Predicate<Note> condition) {
        return NotesDatabase.getDatabase(getApplicationContext()).getNoteDao().getNotes()
                .stream().filter(condition).collect(Collectors.toList());
    }

    public void unFavorite(int id, View view) {
        executor.execute(() -> {
            NotesDatabase.getDatabase(this).getNoteDao().toggleFavorite(id);
            handler.post(() -> {
                view.setVisibility(View.GONE);
            });
        });
    }

    public void deleteNote(Note note, View view) {
        executor.execute(() -> {
            NotesDatabase.getDatabase(this).getNoteDao().delete(note);
            handler.post(() -> {
                view.setVisibility(View.GONE);
            });
        });
    }

    public void reDeleteNote(Note note, View view) {
        executor.execute(() -> {
            NotesDatabase.getDatabase(this).getNoteDao().toggleDelete(note.getId());
            handler.post(() -> {
                view.setVisibility(View.GONE);
            });
        });
    }
}