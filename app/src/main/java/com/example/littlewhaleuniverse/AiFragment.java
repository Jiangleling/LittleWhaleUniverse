package com.example.littlewhaleuniverse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
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

public class AiFragment extends Fragment {

    private EditText input;
    private TextView conversation;
    private OkHttpClient client;

    private static final String DEEPSEEK_API_KEY = "sk-62559d1854ff4d5ba9921b757ac28709";
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        input = view.findViewById(R.id.edit_message);
        conversation = view.findViewById(R.id.text_conversation);

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build();
        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        view.findViewById(R.id.button_send).setOnClickListener(v -> {
            String msg = input.getText().toString().trim();
            if (msg.isEmpty()) return;
            append(conversation, "You: " + msg);
            input.setText("");
            callDeepSeekApi(msg);
        });
    }

    private void callDeepSeekApi(String userMessage) {
        append(conversation, "Assistant: Thinking...");

        String systemPrompt = "You are an empathetic counselor and life coach. Be warm, encouraging, and constructive. Respond strictly in English.";

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray messages = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            JSONObject userMsgJson = new JSONObject();
            userMsgJson.put("role", "user");
            userMsgJson.put("content", userMessage);
            messages.put(systemMessage);
            messages.put(userMsgJson);

            jsonBody.put("model", "deepseek-chat");
            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(DEEPSEEK_API_URL)
                    .header("Authorization", "Bearer " + DEEPSEEK_API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "API request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            removeLastLine();
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (getActivity() != null) {
                        if (response.isSuccessful()) {
                            try {
                                String responseBody = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String aiMessage = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                                
                                getActivity().runOnUiThread(() -> {
                                    removeLastLine();
                                    append(conversation, "Assistant: " + aiMessage);
                                });

                            } catch (Exception e) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Failed to parse API response", Toast.LENGTH_SHORT).show();
                                    removeLastLine();
                                });
                            }
                        } else {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "API error: " + response.code(), Toast.LENGTH_SHORT).show();
                                removeLastLine();
                            });
                        }
                    }
                }
            });

        } catch (Exception e) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error building API request", Toast.LENGTH_SHORT).show();
                    removeLastLine();
                });
            }
        }
    }
    
    private void append(TextView tv, String s) {
        String prev = tv.getText().toString();
        tv.setText(prev.isEmpty() ? s : prev + "\n" + s);
    }
    
    private void removeLastLine() {
        String text = conversation.getText().toString();
        int lastNewline = text.lastIndexOf('\n');
        if (lastNewline != -1) {
            conversation.setText(text.substring(0, lastNewline));
        } else {
            conversation.setText("");
        }
    }
}
