package com.example.littlewhaleuniverse;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class EditDiaryActivity extends AppCompatActivity {

    private EditText mEditTitleView;
    private EditText mEditContentView;
    private DiaryViewModel mDiaryViewModel;

    // --- ↓↓↓ 新增一个成员变量来存储当前日记的ID ↓↓↓ ---
    private int currentDiaryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDiaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        mEditTitleView = findViewById(R.id.edittext_title);
        mEditContentView = findViewById(R.id.edittext_content);
        final Button button = findViewById(R.id.button_save);

        // --- ↓↓↓ 新增代码：接收来自Intent的数据 ↓↓↓ ---
        Intent intent = getIntent();
        // 检查Intent中是否包含diary_id，-1是默认值，表示没找到
        if (intent.hasExtra("diary_id")) {
            // 如果有ID，说明是编辑模式
            currentDiaryId = intent.getIntExtra("diary_id", -1);
            String title = intent.getStringExtra("diary_title");
            String content = intent.getStringExtra("diary_content");
            // 将接收到的数据显示在输入框中
            mEditTitleView.setText(title);
            mEditContentView.setText(content);
        }
        // 如果没有ID，currentDiaryId会保持-1，说明是新建模式

        // --- ↓↓↓ 修改保存按钮的逻辑 ↓↓↓ ---
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title = mEditTitleView.getText().toString().trim();
                String content = mEditContentView.getText().toString().trim();

                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getApplicationContext(), "日记内容不能为空哦", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(title)) {
                    title = "无标题日记";
                }

                long timestamp = System.currentTimeMillis();

                // 判断是更新还是插入
                if (currentDiaryId != -1) {
                    // 如果ID存在，说明是编辑模式，执行更新
                    DiaryEntry updatedDiary = new DiaryEntry(title, content, timestamp);
                    updatedDiary.setId(currentDiaryId); // 关键！必须设置ID，Room才能找到要更新的记录
                    mDiaryViewModel.update(updatedDiary);
                } else {
                    // 如果ID不存在，说明是新建模式，执行插入
                    DiaryEntry newDiary = new DiaryEntry(title, content, timestamp);
                    mDiaryViewModel.insert(newDiary);
                }

                finish(); // 操作完成后关闭页面
            }
        });
    }
}