package com.example.littlewhaleuniverse;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

class DiaryRepository {

    private DiaryDao mDiaryDao;
    private LiveData<List<DiaryEntry>> mAllDiaries;

    DiaryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDiaryDao = db.diaryDao();
        mAllDiaries = mDiaryDao.getAllDiaries();
    }

    LiveData<List<DiaryEntry>> getAllDiaries() {
        return mAllDiaries;
    }

    LiveData<List<DiaryEntry>> getRecentDiaries(long sinceTimestamp) {
        return mDiaryDao.getRecentDiaries(sinceTimestamp);
    }

    void insert(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDiaryDao.insert(diaryEntry);
        });
    }

    void update(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDiaryDao.update(diaryEntry);
        });
    }

    void delete(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDiaryDao.delete(diaryEntry);
        });
    }
}