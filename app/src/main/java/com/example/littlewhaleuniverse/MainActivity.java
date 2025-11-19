package com.example.littlewhaleuniverse;

// --- 导入所有需要的类 ---
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DiaryViewModel mDiaryViewModel;
    private DiaryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 直接设置布局，移除了 EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. 初始化RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview_diaries);

        // 2. 初始化Adapter
        // 我们传入之前创建的DiaryDiff作为参数，用于列表的高效更新
        adapter = new DiaryListAdapter(new DiaryListAdapter.DiaryDiff());

        // 3. 将Adapter设置给RecyclerView
        recyclerView.setAdapter(adapter);

        // 4. 设置布局管理器 (LayoutManager)
        // LayoutManager负责决定列表项如何排列（线性、网格等）
        // LinearLayoutManager表示一个垂直滚动的列表
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 5. 初始化ViewModel
        // 使用ViewModelProvider来获取ViewModel实例，这样可以确保ViewModel在屏幕旋转等配置变化后仍然存活
        mDiaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // 6. 观察ViewModel中的LiveData
        mDiaryViewModel.getAllDiaries().observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(List<DiaryEntry> diaryEntries) {
                // 当数据发生变化时，这个onChanged方法会被自动调用
                // 我们将最新的数据列表提交给Adapter
                adapter.submitList(diaryEntries);
            }
        });

        // 移除了 ViewCompat.setOnApplyWindowInsetsListener(...) 的代码块
    }
}