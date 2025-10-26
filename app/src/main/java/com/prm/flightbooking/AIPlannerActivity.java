package com.prm.flightbooking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Collections;

import io.noties.markwon.Markwon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIPlannerActivity extends AppCompatActivity {

    private EditText etDestination, etDuration, etInterests;
    private Button btnGeneratePlan;
    private ProgressBar progressBar;
    private TextView tvResult, tvError;
    private ImageButton btnBack;
    private OkHttpClient client;
    private Gson gson;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyDEWRFkEX5k-dFx_nppoLdwH5ge5LxGaHQ";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_planner);

        etDestination = findViewById(R.id.etDestination);
        etDuration = findViewById(R.id.etDuration);
        etInterests = findViewById(R.id.etInterests);
        btnGeneratePlan = findViewById(R.id.btnGeneratePlan);
        progressBar = findViewById(R.id.progressBar);
        tvResult = findViewById(R.id.tvResult);
        tvError = findViewById(R.id.tvError);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)  // timeout kết nối
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)     // timeout đọc dữ liệu
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)    // timeout ghi dữ liệu
                .build();

        gson = new Gson();

        btnGeneratePlan.setOnClickListener(v -> {
            String destination = etDestination.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();
            String interests = etInterests.getText().toString().trim();

            if (TextUtils.isEmpty(destination) || TextUtils.isEmpty(duration)) {
                Toast.makeText(AIPlannerActivity.this, "Vui lòng nhập Điểm đến và Số ngày.", Toast.LENGTH_SHORT).show();
                return;
            }

            generatePlan(destination, duration, interests);
        });
    }

    private void generatePlan(String destination, String duration, String interests) {
        progressBar.setVisibility(View.VISIBLE);
        tvResult.setText("");
        tvError.setVisibility(View.GONE);
        btnGeneratePlan.setEnabled(false);

        String prompt = String.format(
                "Tạo một lịch trình du lịch chi tiết cho chuyến đi %s ngày tại %s. Người đi có sở thích về %s. Vui lòng trả lời bằng tiếng Việt, sử dụng định dạng Markdown với tiêu đề cho mỗi ngày và gạch đầu dòng cho các hoạt động cụ thể.",
                duration,
                destination,
                interests.isEmpty() ? "khám phá và trải nghiệm văn hóa địa phương" : interests
        );

        ChatMessage userMessage = new ChatMessage("user", Collections.singletonList(new MessagePart(prompt)));
        ChatRequest requestObj = new ChatRequest(Collections.singletonList(userMessage));

        String jsonRequest = gson.toJson(requestObj);

        RequestBody body = RequestBody.create(jsonRequest, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                // Nếu cần header Authorization thì thêm ở đây
                //.addHeader("Authorization", "Bearer YOUR_ACCESS_TOKEN")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnGeneratePlan.setEnabled(true);
                    showError("Lỗi kết nối: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnGeneratePlan.setEnabled(true);
                });

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> showError("Lỗi phản hồi từ server: " + response.code()));
                    return;
                }

                String responseBodyStr = response.body().string();

                ChatResponse chatResponse = gson.fromJson(responseBodyStr, ChatResponse.class);

                if (chatResponse != null && chatResponse.candidates != null && !chatResponse.candidates.isEmpty()
                        && chatResponse.candidates.get(0).content != null
                        && chatResponse.candidates.get(0).content.parts != null
                        && !chatResponse.candidates.get(0).content.parts.isEmpty()) {

                    String text = chatResponse.candidates.get(0).content.parts.get(0).text;

                    runOnUiThread(() -> {
                        Markwon markwon = Markwon.create(AIPlannerActivity.this);
                        markwon.setMarkdown(tvResult, text);
                        tvError.setVisibility(View.GONE);
                    });
                } else {
                    runOnUiThread(() -> showError("Không nhận được nội dung hợp lệ từ AI."));
                }
            }
        });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    // Model classes cho request và response
    public static class ChatRequest {
        @SerializedName("contents")
        public final java.util.List<ChatMessage> contents;

        public ChatRequest(java.util.List<ChatMessage> contents) {
            this.contents = contents;
        }
    }

    public static class ChatMessage {
        @SerializedName("role")
        public final String role;

        @SerializedName("parts")
        public final java.util.List<MessagePart> parts;

        public ChatMessage(String role, java.util.List<MessagePart> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    public static class MessagePart {
        @SerializedName("text")
        public final String text;

        public MessagePart(String text) {
            this.text = text;
        }
    }

    public static class ChatResponse {
        @SerializedName("candidates")
        public java.util.List<Candidate> candidates;
    }

    public static class Candidate {
        @SerializedName("content")
        public Content content;
    }

    public static class Content {
        @SerializedName("parts")
        public java.util.List<MessagePart> parts;
    }
}
