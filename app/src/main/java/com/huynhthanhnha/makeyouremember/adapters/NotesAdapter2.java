package com.huynhthanhnha.makeyouremember.adapters;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.activities.MainActivity;
import com.huynhthanhnha.makeyouremember.activities.NotesActivity;
import com.huynhthanhnha.makeyouremember.models.Note;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class NotesAdapter2 extends RecyclerView.Adapter<NotesAdapter2.NoteViewHolder> {

    private List<Note> notes;
    private MainActivity context;

    public NotesAdapter2(List<Note> notes, MainActivity context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_layout, parent, false)
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

    class NoteViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutNote;
        TextView textKey, textValue, textDateTime;
        RoundedImageView imageNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutNote = itemView.findViewById(R.id.layoutNote);
            textKey = itemView.findViewById(R.id.textKey);
            textValue = itemView.findViewById(R.id.textValue);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        void bindItem(Note note) {
            textKey.setText(note.getKey());
            textValue.setText(note.getValue());
            textDateTime.setText(note.getDate());

            if(note.getColor() != null && !note.getColor().isEmpty()) {
                GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }

            if(note.getImage() != null && !note.getImage().isEmpty()) {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
                imageNote.setVisibility(View.VISIBLE);
            } else {
                imageNote.setVisibility(View.GONE);
            }

            layoutNote.setOnClickListener(v -> {
                Intent intent = new Intent(context, NotesActivity.class);
                intent.putExtra("note", note);
                intent.putExtra("isUpdated", true);
                context.startActivity(intent);
            });
        }
    }
}