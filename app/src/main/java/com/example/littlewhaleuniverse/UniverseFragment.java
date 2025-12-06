package com.example.littlewhaleuniverse;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class UniverseFragment extends Fragment {

    private DiaryViewModel diaryViewModel;
    private MaterialCalendarView calendarView;
    private OkHttpClient client;

    private static final String DEEPSEEK_API_KEY = "sk-62559d1854ff4d5ba9921b757ac28709";
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_universe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        diaryViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(DiaryViewModel.class);
        calendarView = view.findViewById(R.id.calendar_view);
        Button aiReviewButton = view.findViewById(R.id.button_ai_review);

        setupHttpClient();
        observeDiariesForCalendar();

        aiReviewButton.setOnClickListener(v -> performAiReview());
    }

    private void setupHttpClient() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build();
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private void observeDiariesForCalendar() {
        diaryViewModel.getAllDiaries().observe(getViewLifecycleOwner(), diaries -> {
            if (diaries == null || diaries.isEmpty()) {
                calendarView.removeDecorators();
                return;
            }

            List<CalendarDay> mood1Days = new ArrayList<>();
            List<CalendarDay> mood2Days = new ArrayList<>();
            List<CalendarDay> mood3Days = new ArrayList<>();
            List<CalendarDay> mood4Days = new ArrayList<>();
            List<CalendarDay> mood5Days = new ArrayList<>();

            for (DiaryEntry entry : diaries) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(entry.getTimestamp());
                LocalDate localDate = LocalDate.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
                );
                CalendarDay day = CalendarDay.from(localDate);

                switch (entry.getMood()) {
                    case 1: mood1Days.add(day); break;
                    case 2: mood2Days.add(day); break;
                    case 3: mood3Days.add(day); break;
                    case 4: mood4Days.add(day); break;
                    case 5: mood5Days.add(day); break;
                }
            }

            calendarView.removeDecorators();
            calendarView.addDecorator(new EventDecorator(Color.parseColor("#e57373"), mood1Days));
            calendarView.addDecorator(new EventDecorator(Color.parseColor("#ffb74d"), mood2Days));
            calendarView.addDecorator(new EventDecorator(Color.parseColor("#90a4ae"), mood3Days));
            calendarView.addDecorator(new EventDecorator(Color.parseColor("#a5d6a7"), mood4Days));
            calendarView.addDecorator(new EventDecorator(Color.parseColor("#81c784"), mood5Days));
        });
    }

    private void performAiReview() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        long sevenDaysAgo = calendar.getTimeInMillis();

        AlertDialog loadingDialog = new AlertDialog.Builder(getContext())
            .setView(new ProgressBar(getContext()))
            .setCancelable(false)
            .create();
        loadingDialog.show();

        diaryViewModel.getRecentDiaries(sevenDaysAgo).observe(getViewLifecycleOwner(), diaries -> {
            if (diaries == null || diaries.isEmpty()) {
                loadingDialog.dismiss();
                Toast.makeText(getContext(), "No diaries in the last 7 days.", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder diaryText = new StringBuilder();
            for (DiaryEntry entry : diaries) {
                diaryText.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(entry.getTimestamp()))).append("\n");
                diaryText.append("Content: ").append(entry.getContent()).append("\n\n");
            }
            callDeepSeekForReview(diaryText.toString(), loadingDialog);
        });
    }

    private void callDeepSeekForReview(String diaryContent, AlertDialog loadingDialog) {
        String systemPrompt = "You are a professional psychological analyst and life coach. Provide a compassionate weekly review. Respond strictly in English. Include: 1) Emotion trend, 2) Key themes, 3) Behavior insights, 4) 1-2 open questions, 5) One actionable suggestion.";
        String userPrompt = "Here are my last 7 days of diaries. Please review them:\n\n" + diaryContent;

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            messages.put(new JSONObject().put("role", "user").put("content", userPrompt));

            jsonBody.put("model", "deepseek-chat");
            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(DEEPSEEK_API_URL).header("Authorization", "Bearer " + DEEPSEEK_API_KEY).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), "AI review failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (getActivity() == null) return;
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String aiMessage = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                            getActivity().runOnUiThread(() -> {
                                loadingDialog.dismiss();
                                new AlertDialog.Builder(getContext())
                                    .setTitle("AI Review Report")
                                    .setMessage(aiMessage)
                                    .setPositiveButton("OK", null)
                                    .show();
                            });
                        } catch (Exception e) {
                            getActivity().runOnUiThread(() -> {
                                loadingDialog.dismiss();
                                Toast.makeText(getContext(), "Failed to parse AI response", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                         getActivity().runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(), "AI review error: " + response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            loadingDialog.dismiss();
            Toast.makeText(getContext(), "Error building AI request", Toast.LENGTH_SHORT).show();
        }
    }
}
