package com.huynhthanhnha.makeyouremember.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.activities.OptionalActivity;
import com.huynhthanhnha.makeyouremember.database.NotesDatabase;
import com.huynhthanhnha.makeyouremember.models.Note;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotesAdapterRemembered extends RecyclerView.Adapter<NotesAdapterRemembered.ViewHolder> {


    private final List<Note> notes;
    private final String option;
    private final OptionalActivity optionalActivity;

    public NotesAdapterRemembered(List<Note> notes, String option, OptionalActivity optionalActivity) {
        this.notes = notes;
        this.option = option;
        this.optionalActivity = optionalActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_remembered, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindingData(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textKey;
        private TextView textValue;
        private TextView textDateTime;
        private ImageView btnDelete;
        private ImageView btnUnFavorite;
        private ImageView btnReTrash;
        private ConstraintLayout layoutNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textKey = itemView.findViewById(R.id.textKey);
            textValue = itemView.findViewById(R.id.textValue);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUnFavorite = itemView.findViewById(R.id.btnUnFavorite);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            btnReTrash = itemView.findViewById(R.id.btnReTrash);

        }

        public void bindingData(Note note) {
            textKey.setText(note.getKey());
            textValue.setText(note.getValue());
            textDateTime.setText(note.getDate());

            if(option.equals("favorite")) {
                btnUnFavorite.setVisibility(View.VISIBLE);
            } else if(option.equals("trash")) {
                btnDelete.setVisibility(View.VISIBLE);
                btnReTrash.setVisibility(View.VISIBLE);
            }

            btnUnFavorite.setOnClickListener(v -> {
                optionalActivity.unFavorite(note.getId(), layoutNote);
            });

            btnDelete.setOnClickListener(v -> {
                optionalActivity.deleteNote(note, layoutNote);
            });

            btnReTrash.setOnClickListener(v -> {
                optionalActivity.reDeleteNote(note, layoutNote);
            });
        }


    }

}
