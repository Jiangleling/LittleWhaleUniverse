package com.example.littlewhaleuniverse;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DiaryDao {

    @Insert
    void insert(DiaryEntry diaryEntry);

    @Update
    void update(DiaryEntry diaryEntry);

    @Delete
    void delete(DiaryEntry diaryEntry);

    @Query("SELECT * FROM diaries ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> getAllDiaries();

    @Query("SELECT * FROM diaries WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> getRecentDiaries(long sinceTimestamp);
}
