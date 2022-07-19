package com.huynhthanhnha.makeyouremember.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.huynhthanhnha.makeyouremember.models.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Transaction
    @Query("UPDATE notes SET favourite = not favourite WHERE id = :noteId")
    void toggleFavorite(int noteId);

    @Transaction
    @Query("UPDATE notes SET is_delete = not is_delete WHERE id = :noteId")
    void toggleDelete(int noteId);

    @Transaction
    @Query("UPDATE notes SET correct_times = :correctTime WHERE id = :noteId")
    void updateCorrectTime(int noteId, int correctTime);

    @Delete
    void delete(Note note);

}
