package com.example.littlewhaleuniverse;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


// @Dao注解，表明这是一个数据访问对象
@Dao
public interface DiaryDao {

    // 插入一篇日记
    @Insert
    void insert(DiaryEntry diaryEntry);

    // 更新一篇日记
    @Update
    void update(DiaryEntry diaryEntry);

    // 删除一篇日记
    @Delete
    void delete(DiaryEntry diaryEntry);


    // 查询所有日记，并按时间戳降序排列
    // 返回LiveData，这样当数据变化时，UI可以自动更新
    @Query("SELECT * FROM diaries ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> getAllDiaries();
}