package com.example.littlewhaleuniverse;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class DiaryViewModel extends AndroidViewModel {

    private DiaryRepository mRepository;
    private final LiveData<List<DiaryEntry>> mAllDiaries;

    public DiaryViewModel (Application application) {
        super(application);
        mRepository = new DiaryRepository(application);
        mAllDiaries = mRepository.getAllDiaries();
    }

    // 向UI暴露LiveData，以便UI可以观察数据变化
    public LiveData<List<DiaryEntry>> getAllDiaries() {
        return mAllDiaries;
    }

    // 向UI暴露插入方法，ViewModel将调用Repository的相应方法
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