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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.net.Uri;
import android.widget.ImageView;
import java.io.InputStream;

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
        holder.bind(current);
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView diaryTitleView;
        private final TextView diaryPreviewView;
        private final TextView diaryTimeView;
        private final ImageView imageThumbView;
        private final TextView moodView;

        private DiaryViewHolder(View itemView) {
            super(itemView);
            diaryTitleView = itemView.findViewById(R.id.textview_diary_title);
            diaryPreviewView = itemView.findViewById(R.id.textview_diary_preview);
            diaryTimeView = itemView.findViewById(R.id.textview_diary_time);
            imageThumbView = itemView.findViewById(R.id.image_thumb);
            moodView = itemView.findViewById(R.id.textview_mood);
        }

        public void bind(DiaryEntry diary) {
            diaryTitleView.setText(diary.getTitle());
            diaryPreviewView.setText(diary.getContent());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            diaryTimeView.setText(sdf.format(new Date(diary.getTimestamp())));
            String uri = diary.getImageUri();
            if (uri != null) {
                int p = uri.indexOf('|');
                if (p >= 0) uri = uri.substring(0, p);
            }
            if (uri != null && !uri.isEmpty()) {
                try {
                    if (uri.startsWith("file://")) {
                        java.io.File f = new java.io.File(Uri.parse(uri).getPath());
                        if (f.exists()) {
                            imageThumbView.setVisibility(View.VISIBLE);
                            imageThumbView.setImageURI(Uri.parse(uri));
                        } else {
                            imageThumbView.setVisibility(View.GONE);
                            imageThumbView.setImageDrawable(null);
                        }
                    } else {
                        Context ctx = itemView.getContext();
                        try {
                            InputStream is = ctx.getContentResolver().openInputStream(Uri.parse(uri));
                            if (is != null) {
                                android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeStream(is);
                                is.close();
                                if (bmp != null) {
                                    imageThumbView.setVisibility(View.VISIBLE);
                                    imageThumbView.setImageBitmap(bmp);
                                } else {
                                    imageThumbView.setVisibility(View.GONE);
                                    imageThumbView.setImageDrawable(null);
                                }
                            } else {
                                imageThumbView.setVisibility(View.GONE);
                                imageThumbView.setImageDrawable(null);
                            }
                        } catch (SecurityException se) {
                            imageThumbView.setVisibility(View.GONE);
                            imageThumbView.setImageDrawable(null);
                        }
                    }
                } catch (Exception e) {
                    imageThumbView.setVisibility(View.GONE);
                    imageThumbView.setImageDrawable(null);
                }
            } else {
                imageThumbView.setVisibility(View.GONE);
            }
            int m = diary.getMood();
            String moodText = "";
            if (m <= 1) moodText = "😞"; else if (m == 2) moodText = "☹️"; else if (m == 3) moodText = "😐"; else if (m == 4) moodText = "🙂"; else moodText = "😄";
            moodView.setText(moodText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EditDiaryActivity.class);
                    intent.putExtra("diary_id", diary.getId());
                    intent.putExtra("diary_title", diary.getTitle());
                    intent.putExtra("diary_content", diary.getContent());
                    intent.putExtra("diary_timestamp", diary.getTimestamp());
                    intent.putExtra("diary_image_uri", diary.getImageUri());
                    intent.putExtra("diary_mood", diary.getMood());
                    context.startActivity(intent);
                }
            });
        }
    }

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
