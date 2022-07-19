package com.huynhthanhnha.makeyouremember.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.activities.MainActivity;
import com.huynhthanhnha.makeyouremember.activities.NotesActivity;
import com.huynhthanhnha.makeyouremember.models.Note;

import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> implements TextToSpeech.OnInitListener {

    private final List<Note> notes;
    private final MainActivity context;
    private final TextToSpeech tts;

    public NotesAdapter(List<Note> notes, MainActivity context) {
        this.notes = notes;
        this.context = context;
        tts = new TextToSpeech(context, this);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bindItem(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.UK);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                Log.e("TTS", "The Language specified is not supported!");
        }

    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        CardView itemNote;
        TextView textKey;
        TextView textValue;
        ImageView btnFavorite;
        ImageView btnSpeak;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNote = itemView.findViewById(R.id.itemNote);
            textKey = itemView.findViewById(R.id.textKey);
            textValue = itemView.findViewById(R.id.textValue);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnSpeak = itemView.findViewById(R.id.btnSpeak);
        }

        void bindItem(Note note) {
            textKey.setText(note.getKey());
            textValue.setText(note.getValue());

            if(note.isFavourite()) {
                btnFavorite.setColorFilter(ContextCompat.getColor(context, R.color.color_note_bg_1), PorterDuff.Mode.SRC_IN);
            }

            btnFavorite.setOnClickListener(v -> {
                context.updateFavorite(note.getId());
                toggleBtnFavorite();
            });

            itemNote.setOnClickListener(v -> {
                Intent intent = new Intent(context, NotesActivity.class);
                intent.putExtra("note", note);
                intent.putExtra("isUpdated", true);
                context.startActivity(intent);
            });

            btnSpeak.setOnClickListener(v -> {
                tts.speak(note.getKey(), TextToSpeech.QUEUE_FLUSH, null, "");
            });
        }

        private void toggleBtnFavorite() {
            if(btnFavorite.getColorFilter() == null) {
                btnFavorite.setColorFilter(ContextCompat.getColor(context, R.color.color_note_bg_1), PorterDuff.Mode.SRC_IN);
            } else {
                btnFavorite.setColorFilter(null);
            }
        }



    }
}
