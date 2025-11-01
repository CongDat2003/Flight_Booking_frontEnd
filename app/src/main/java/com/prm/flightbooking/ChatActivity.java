package com.prm.flightbooking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.flightbooking.adapters.MessageAdapter;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.ChatApiEndpoint;
import com.prm.flightbooking.dto.chat.ChatConversationDto;
import com.prm.flightbooking.dto.chat.CreateMessageDto;
import com.prm.flightbooking.dto.chat.MessageDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private ImageButton btnSend, btnBack;
    private ProgressBar progressBar;
    private TextView tvChatTitle;

    private MessageAdapter adapter;
    private ChatApiEndpoint chatApi;
    private SharedPreferences sharedPreferences;

    private int userId;
    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private static final long REFRESH_INTERVAL = 3000; // Refresh every 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatApi = ApiServiceProvider.getChatApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Lấy userId từ SharedPreferences
        userId = sharedPreferences.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindingView();
        bindingAction();
        setupRecyclerView();
        loadConversation();

        // Setup auto-refresh
        setupAutoRefresh();
    }

    private void bindingView() {
        rvMessages = findViewById(R.id.rv_messages);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progress_bar);
        tvChatTitle = findViewById(R.id.tv_chat_title);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendMessage());

        // Gửi tin nhắn khi nhấn Enter
        etMessageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void setupRecyclerView() {
        adapter = new MessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Scroll to bottom
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);
    }

    private void sendMessage() {
        String content = etMessageInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            return;
        }

        // Clear input
        etMessageInput.setText("");

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etMessageInput.getWindowToken(), 0);
        }

        // Create message DTO
        CreateMessageDto messageDto = new CreateMessageDto(userId, content);

        // Show loading
        progressBar.setVisibility(View.VISIBLE);

        // Send message
        Call<MessageDto> call = chatApi.sendMessage(messageDto);
        call.enqueue(new Callback<MessageDto>() {
            @Override
            public void onResponse(@NonNull Call<MessageDto> call, @NonNull Response<MessageDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // Add message to adapter
                    adapter.addMessage(response.body());
                    // Scroll to bottom
                    rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                } else {
                    Toast.makeText(ChatActivity.this, "Không thể gửi tin nhắn: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageDto> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChatActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadConversation() {
        progressBar.setVisibility(View.VISIBLE);

        // Đảm bảo userId hợp lệ
        if (userId <= 0) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        android.util.Log.d("ChatActivity", "Loading conversation for userId: " + userId);
        
        Call<ChatConversationDto> call = chatApi.getConversation(userId);
        call.enqueue(new Callback<ChatConversationDto>() {
            @Override
            public void onResponse(@NonNull Call<ChatConversationDto> call, @NonNull Response<ChatConversationDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ChatConversationDto conversation = response.body();
                    List<MessageDto> messages = conversation.getMessages() != null ? conversation.getMessages() : new ArrayList<>();
                    android.util.Log.d("ChatActivity", "Loaded " + messages.size() + " messages");
                    adapter.updateData(messages);
                    
                    // Scroll to bottom
                    if (messages.size() > 0) {
                        rvMessages.post(() -> rvMessages.smoothScrollToPosition(messages.size() - 1));
                    }
                } else {
                    String errorMsg = "Không thể tải tin nhắn";
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("ChatActivity", "Error loading conversation: " + errorBody);
                            errorMsg += ": " + errorBody;
                        } catch (Exception e) {
                            android.util.Log.e("ChatActivity", "Error reading error body", e);
                            errorMsg += ": " + response.message() + " (Code: " + response.code() + ")";
                        }
                    } else {
                        errorMsg += ": " + response.message() + " (Code: " + response.code() + ")";
                    }
                    android.util.Log.e("ChatActivity", errorMsg);
                    Toast.makeText(ChatActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatConversationDto> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                android.util.Log.e("ChatActivity", "Network error loading conversation", t);
                Toast.makeText(ChatActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupAutoRefresh() {
        refreshHandler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                refreshMessages();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void refreshMessages() {
        // Đảm bảo userId hợp lệ
        if (userId <= 0) {
            return;
        }
        
        Call<ChatConversationDto> call = chatApi.getConversation(userId);
        call.enqueue(new Callback<ChatConversationDto>() {
            @Override
            public void onResponse(@NonNull Call<ChatConversationDto> call, @NonNull Response<ChatConversationDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatConversationDto conversation = response.body();
                    List<MessageDto> messages = conversation.getMessages() != null ? conversation.getMessages() : new ArrayList<>();
                    
                    // Only update if messages changed
                    if (messages.size() != adapter.getItemCount()) {
                        adapter.updateData(messages);
                        rvMessages.post(() -> rvMessages.smoothScrollToPosition(messages.size() - 1));
                    }
                } else {
                    android.util.Log.w("ChatActivity", "Failed to refresh messages. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatConversationDto> call, @NonNull Throwable t) {
                // Silent fail for auto-refresh to avoid spamming logs
                android.util.Log.w("ChatActivity", "Auto-refresh failed silently", t);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
        }
    }
}







