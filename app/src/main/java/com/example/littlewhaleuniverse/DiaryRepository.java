package com.example.littlewhaleuniverse;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

class DiaryRepository {

    private DiaryDao mDiaryDao;
    private LiveData<List<DiaryEntry>> mAllDiaries;

    // 构造函数
    DiaryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDiaryDao = db.diaryDao();
        mAllDiaries = mDiaryDao.getAllDiaries();
    }

    // 为ViewModel提供获取所有日记的方法
    LiveData<List<DiaryEntry>> getAllDiaries() {
        return mAllDiaries;
    }

    // 在后台线程中插入日记
    void insert(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDiaryDao.insert(diaryEntry);
        });
    }

    // 在后台线程中更新日记
    void update(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDiaryDao.update(diaryEntry);
        });
    }

    // 在后台线程中删除日记
    void delete(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDiaryDao.delete(diaryEntry);
        });
    }
}