package com.example.littlewhaleuniverse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class DiaryListAdapter extends ListAdapter<DiaryEntry, DiaryListAdapter.DiaryViewHolder> {

    public DiaryListAdapter(@NonNull DiffUtil.ItemCallback<DiaryEntry> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_diary, parent, false);
        return new DiaryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry current = getItem(position);
        // --- ↓↓↓ 我们在这里传入了整个DiaryEntry对象 ↓↓↓ ---
        holder.bind(current);
    }

    // ViewHolder类，持有列表项的UI控件
    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView diaryTitleView;
        private final TextView diaryPreviewView;

        private DiaryViewHolder(View itemView) {
            super(itemView);
            diaryTitleView = itemView.findViewById(R.id.textview_diary_title);
            diaryPreviewView = itemView.findViewById(R.id.textview_diary_preview);
        }

        // --- ↓↓↓ bind方法被修改以接收整个对象，并设置点击事件 ↓↓↓ ---
        public void bind(DiaryEntry diary) {
            diaryTitleView.setText(diary.getTitle());
            diaryPreviewView.setText(diary.getContent());

            // 为整个列表项（itemView）设置点击监听器
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    // 创建一个意图来启动EditDiaryActivity
                    Intent intent = new Intent(context, EditDiaryActivity.class);
                    // 将日记的数据放入意图中，以便下一个Activity可以接收
                    intent.putExtra("diary_id", diary.getId());
                    intent.putExtra("diary_title", diary.getTitle());
                    intent.putExtra("diary_content", diary.getContent());
                    // 启动Activity
                    context.startActivity(intent);
                }
            });
        }
    }

    // DiffUtil.ItemCallback，用于高效更新列表 (这部分保持不变)
    public static class DiaryDiff extends DiffUtil.ItemCallback<DiaryEntry> {
        @Override
        public boolean areItemsTheSame(@NonNull DiaryEntry oldItem, @NonNull DiaryEntry newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DiaryEntry oldItem, @NonNull DiaryEntry newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getContent().equals(newItem.getContent());
        }
    }
}