package com.example.littlewhaleuniverse;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class DiaryViewModel extends AndroidViewModel {

    private DiaryRepository mRepository;
    private final LiveData<List<DiaryEntry>> mAllDiaries;

    public DiaryViewModel(Application application) {
        super(application);
        mRepository = new DiaryRepository(application);
        mAllDiaries = mRepository.getAllDiaries();
    }

    LiveData<List<DiaryEntry>> getAllDiaries() {
        return mAllDiaries;
    }

    public LiveData<List<DiaryEntry>> getRecentDiaries(long sinceTimestamp) {
        return mRepository.getRecentDiaries(sinceTimestamp);
    }

    public void insert(DiaryEntry diaryEntry) {
        mRepository.insert(diaryEntry);
    }

    public void update(DiaryEntry diaryEntry) {
        mRepository.update(diaryEntry);
    }

    public void delete(DiaryEntry diaryEntry) {
        mRepository.delete(diaryEntry);
    }
}