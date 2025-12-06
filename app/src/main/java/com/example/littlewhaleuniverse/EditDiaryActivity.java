package com.example.littlewhaleuniverse;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class EditDiaryActivity extends AppCompatActivity {

    private EditText contentView;
    private DiaryViewModel diaryViewModel;
    private int currentDiaryId = -1;
    private String selectedImageUri;
    private int selectedMood = 4; 

    private FrameLayout imagePreviewContainer;
    private ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        ImageButton backBtn = findViewById(R.id.button_close);
        View saveBtn = findViewById(R.id.button_save);
        TextView dateText = findViewById(R.id.text_month);
        contentView = findViewById(R.id.edittext_content);
        
        imagePreviewContainer = findViewById(R.id.image_preview_container);
        imagePreview = findViewById(R.id.image_preview);
        ImageButton removeImageBtn = findViewById(R.id.button_remove_image);

        ImageButton pickImageBtn = findViewById(R.id.button_pick_image);
        ImageButton boldBtn = findViewById(R.id.button_bold);
        ImageButton listBtn = findViewById(R.id.button_list);

        ImageView moodImageView = findViewById(R.id.mood_image);
        TextView moodTextView = findViewById(R.id.mood_text);
        updateMoodViews(moodImageView, moodTextView); 

        View.OnClickListener openMoodPickerListener = v -> showMoodPicker(moodImageView, moodTextView);
        moodImageView.setOnClickListener(openMoodPickerListener);
        moodTextView.setOnClickListener(openMoodPickerListener);

        Intent intent = getIntent();
        long ts = System.currentTimeMillis();
        if (intent.hasExtra("diary_id")) {
            currentDiaryId = intent.getIntExtra("diary_id", -1);
            String content = intent.getStringExtra("diary_content");
            selectedImageUri = intent.getStringExtra("diary_image_uri");
            selectedMood = intent.getIntExtra("diary_mood", 3);

            if (intent.hasExtra("diary_timestamp")) ts = intent.getLongExtra("diary_timestamp", ts);
            if (content != null) contentView.setText(content);
            if (selectedImageUri != null) {
                showImage(Uri.parse(selectedImageUri));
            }
            updateMoodViews(moodImageView, moodTextView);
        }
        dateText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(ts)));
        TextView dayNumView = findViewById(R.id.textview_diary_time);
        TextView dayOfWeekView = findViewById(R.id.textview_day_of_week);
        String dayNum = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date(ts));
        String dow = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date(ts));
        dayNumView.setText(dayNum);
        dayOfWeekView.setText(dow);

        pickImageBtn.setOnClickListener(v -> openImagePicker());
        imagePreview.setOnClickListener(v -> openImagePicker()); 

        removeImageBtn.setOnClickListener(v -> {
            selectedImageUri = null;
            imagePreviewContainer.setVisibility(View.GONE);
        });

        boldBtn.setOnClickListener(v -> {
            int s = contentView.getSelectionStart();
            int e = contentView.getSelectionEnd();
            if (s >= 0 && e > s) {
                SpannableStringBuilder sb = new SpannableStringBuilder(contentView.getText());
                sb.setSpan(new StyleSpan(Typeface.BOLD), s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                contentView.setText(sb);
                contentView.setSelection(e);
            }
        });

        listBtn.setOnClickListener(v -> {
            int start = contentView.getSelectionStart();
            String text = contentView.getText().toString();
            int lineStart = text.lastIndexOf('\n', start > 0 ? start - 1 : 0);
            if (lineStart < 0) lineStart = 0;
            else lineStart++; 
            String newText = text.substring(0, lineStart) + "• " + text.substring(lineStart);
            contentView.setText(newText);
            contentView.setSelection(start + 2);
        });

        saveBtn.setOnClickListener(v -> {
            String content = contentView.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(getApplicationContext(), "Diary content cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }
            
            String[] lines = content.split("\n");
            String title = lines.length > 0 ? lines[0] : "Untitled diary";

            long timestamp = System.currentTimeMillis();

            if (currentDiaryId != -1) {
                DiaryEntry updated = new DiaryEntry(title, content, timestamp);
                updated.setId(currentDiaryId);
                updated.setMood(selectedMood);
                updated.setImageUri(selectedImageUri); // Directly set the image URI
                diaryViewModel.update(updated);
            } else {
                DiaryEntry created = new DiaryEntry(title, content, timestamp);
                created.setMood(selectedMood);
                created.setImageUri(selectedImageUri); // Also set for new entries
                diaryViewModel.insert(created);
            }
            finish();
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i, 1001);
    }

    private void showMoodPicker(ImageView moodImageView, TextView moodTextView) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_mood_picker, null, false);
        AlertDialog dlg = new AlertDialog.Builder(this).setView(view).create();
        
        View.OnClickListener moodButtonListener = v -> {
            int id = v.getId();
            if (id == R.id.pick_mood_1) selectedMood = 1;
            else if (id == R.id.pick_mood_2) selectedMood = 2;
            else if (id == R.id.pick_mood_3) selectedMood = 3;
            else if (id == R.id.pick_mood_4) selectedMood = 4;
            else if (id == R.id.pick_mood_5) selectedMood = 5;
            
            updateMoodViews(moodImageView, moodTextView);
            dlg.dismiss();
        };
        
        view.findViewById(R.id.pick_mood_1).setOnClickListener(moodButtonListener);
        view.findViewById(R.id.pick_mood_2).setOnClickListener(moodButtonListener);
        view.findViewById(R.id.pick_mood_3).setOnClickListener(moodButtonListener);
        view.findViewById(R.id.pick_mood_4).setOnClickListener(moodButtonListener);
        view.findViewById(R.id.pick_mood_5).setOnClickListener(moodButtonListener);
        
        dlg.show();
    }

    private void updateMoodViews(ImageView moodImageView, TextView moodTextView) {
        moodTextView.setText(moodText(selectedMood));
        moodImageView.setImageResource(getMoodDrawable(selectedMood));
    }

    private int getMoodDrawable(int mood) {
        switch (mood) {
            case 1: return R.drawable.ic_mood_1;
            case 2: return R.drawable.ic_mood_2;
            case 3: return R.drawable.ic_mood_3;
            case 4: return R.drawable.ic_mood_4;
            case 5: return R.drawable.ic_mood_5;
            default: return R.drawable.ic_mood_3;
        }
    }

    private String moodText(int m) {
        switch (m) {
            case 1: return "Very bad";
            case 2: return "Bad";
            case 3: return "Neutral";
            case 4: return "Good";
            case 5: return "Very good";
        }
        return "Neutral";
    }

    private void showImage(Uri uri) {
        imagePreviewContainer.setVisibility(View.VISIBLE);
        imagePreview.setImageURI(uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    selectedImageUri = uri.toString();
                    showImage(uri);
                } catch (SecurityException e) {
                    Toast.makeText(this, "Cannot get image permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
