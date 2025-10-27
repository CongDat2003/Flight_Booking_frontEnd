package com.prm.flightbooking;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.NotificationApiEndpoint;
import com.prm.flightbooking.dto.notify.NotificationDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView tvUnreadCount;
    private ImageButton btnBack;

    private NotificationAdapter adapter;
    private NotificationApiEndpoint notificationApi;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationApi = ApiServiceProvider.getNotificationApi();

        bindingView();
        bindingAction();

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // RecyclerView để hiển thị danh sách thông báo
        setupRecyclerView();

        // Danh sách thông báo
        loadNotifications();

        // Số lượng thông báo chưa đọc để hiển thị
        loadUnreadCount();
    }

    private void bindingView() {
        rvNotifications = findViewById(R.id.rvNotifications);
        tvUnreadCount = findViewById(R.id.tvUnreadCount);
        btnBack = findViewById(R.id.btn_back);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(new ArrayList<>(), this::onNotificationClicked);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    // Gọi API để lấy danh sách thông báo của người dùng
    private void loadNotifications() {
        notificationApi.getUserNotifications(userId, 1, 20).enqueue(new Callback<List<NotificationDto>>() {
            @Override
            public void onResponse(Call<List<NotificationDto>> call, Response<List<NotificationDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật dữ liệu cho adapter để hiển thị lên màn hình
                    adapter.updateData(response.body());
                } else {
                    Toast.makeText(NotificationActivity.this, "Không tải được thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationDto>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Gọi API để lấy số lượng thông báo chưa đọc
    private void loadUnreadCount() {
        notificationApi.getUnreadCount(userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body();
                    // Hiển thị số lượng thông báo chưa đọc lên màn hình
                    tvUnreadCount.setText("Chưa đọc: " + count);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                // Thông báo lỗi
            }
        });
    }

    // Xử lý khi người dùng nhấn vào một thông báo
    private void onNotificationClicked(NotificationDto notification) {
        // Nếu thông báo chưa đọc thì gọi API đánh dấu đã đọc trước khi hiển thị chi tiết
        if ("UNREAD".equalsIgnoreCase(notification.getStatus())) {
            markNotificationAsRead(notification);
        } else {
            showNotificationDetail(notification);
        }
    }

    // Gọi API đánh dấu thông báo đã đọc
    private void markNotificationAsRead(NotificationDto notification) {
        notificationApi.markAsRead(notification.getNotificationId(), userId).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    // Cập nhật trạng thái thông báo trong danh sách thành "đã đọc"
                    notification.setStatus("READ");
                    adapter.notifyDataSetChanged();
                    // Cập nhật lại số lượng thông báo chưa đọc trên màn hình
                    loadUnreadCount();
                    showNotificationDetail(notification);
                } else {
                    Toast.makeText(NotificationActivity.this, "Không thể đánh dấu đã đọc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Không thể đánh dấu đã đọc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hiển thị chi tiết thông báo bằng hộp thoại
    private void showNotificationDetail(NotificationDto notification) {
        new AlertDialog.Builder(this)
                .setTitle(notification.getTitle())
                .setMessage(notification.getMessage())
                .setPositiveButton("Đóng", null)
                .show();
    }

    // Adapter cho RecyclerView
    public static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
        // Giao diện xử lý sự kiện khi nhấn vào một thông báo
        public interface OnItemClickListener {
            void onItemClick(NotificationDto notification);
        }

        private final List<NotificationDto> notifications;
        private final OnItemClickListener listener;

        public NotificationAdapter(List<NotificationDto> notifications, OnItemClickListener listener) {
            this.notifications = notifications;
            this.listener = listener;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Tạo view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            // Gán dữ liệu cho từng item trong danh sách
            NotificationDto notification = notifications.get(position);
            holder.bind(notification, listener);
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        // Cập nhật dữ liệu mới cho danh sách thông báo
        public void updateData(List<NotificationDto> newNotifications) {
            notifications.clear();
            notifications.addAll(newNotifications);
            notifyDataSetChanged();
        }

        // ViewHolder chứa các thành phần giao diện
        static class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvMessage, tvCreatedAt;
            View viewUnreadIndicator;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvMessage = itemView.findViewById(R.id.tvMessage);
                tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
                viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
            }

            // Gán dữ liệu cho các thành phần
            public void bind(NotificationDto notification, OnItemClickListener listener) {
                tvTitle.setText(notification.getTitle());
                tvMessage.setText(notification.getMessage());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String dateStr = notification.getCreatedAt() != null ? sdf.format(notification.getCreatedAt()) : "";
                tvCreatedAt.setText(dateStr);

                // Nếu thông báo chưa đọc thì hiển thị dấu hiệu và chữ đậm
                if ("UNREAD".equalsIgnoreCase(notification.getStatus())) {
                    viewUnreadIndicator.setVisibility(View.VISIBLE);
                    tvTitle.setTypeface(null, Typeface.BOLD);
                } else {
                    // Nếu đã đọc thì ẩn dấu hiệu và chữ bình thường
                    viewUnreadIndicator.setVisibility(View.GONE);
                    tvTitle.setTypeface(null, Typeface.NORMAL);
                }

                // Xử lý sự kiện khi người dùng nhấn vào item thông báo
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(notification);
                    }
                });
            }
        }
    }
}
