package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.NotificationApiEndpoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUsername;
    private ImageView ivAvatar;
    // Legacy views (may be null in redesigned layout)
    private Button btnBannerBook;
    private View menuBookFlight, menuMyTrips, menuNotifications, menuProfile, menuAIPlan, menuAIChat;
    private TextView tvNotificationsBadge;
    private BottomNavigationView bottomNavigation;

    // API và SharedPreferences
    private NotificationApiEndpoint notificationApi;
    private SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Khởi tạo API và SharedPreferences
        notificationApi = ApiServiceProvider.getNotificationApi();
        userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        bindingView();
        bindingAction();
        loadUserInfo();
        setupBackPressHandler();
        startAIChatIconAnimation();
    }

    private void bindingView() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUsername = findViewById(R.id.tv_username);
        ivAvatar = findViewById(R.id.iv_avatar);
        // Legacy ids (may be absent)
        btnBannerBook = findViewById(R.id.btn_banner_book);
        menuBookFlight = findViewById(R.id.menu_book_flight);
        menuMyTrips = findViewById(R.id.menu_my_trips);
        menuNotifications = findViewById(R.id.menu_notifications);
        menuProfile = findViewById(R.id.menu_profile);
        tvNotificationsBadge = findViewById(R.id.tv_notifications_badge);
        menuAIPlan = findViewById((R.id.menu_ai_plan));
        menuAIChat = findViewById(R.id.menu_ai_chat);

        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void bindingAction() {
        if (btnBannerBook != null) btnBannerBook.setOnClickListener(this::onBannerBookClick);
        if (menuBookFlight != null) menuBookFlight.setOnClickListener(this::onBookFlightClick);
        if (menuMyTrips != null) menuMyTrips.setOnClickListener(this::onMyTripsClick);
        if (menuNotifications != null) menuNotifications.setOnClickListener(this::onNotificationsClick);
        if (menuProfile != null) menuProfile.setOnClickListener(this::onProfileClick);
        if (ivAvatar != null) ivAvatar.setOnClickListener(this::onAvatarClick);
        if (menuAIPlan != null) menuAIPlan.setOnClickListener(this::OnAITripPlan);
        if (menuAIChat != null) menuAIChat.setOnClickListener(this::onAIChatClick);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            handleBottomNavigation(itemId);
            return true;
        });
    }

    private void OnAITripPlan(View view) {
        navigateToActivity(AIPlannerActivity.class);
    }

    // Xử lý click menu AI Chatbot
    private void onAIChatClick(View view) {
        navigateToActivity(AIChatActivity.class);
    }

    // Start animation cho AI Chatbot icon
    private void startAIChatIconAnimation() {
        if (menuAIChat != null) {
            // Tìm ImageView với id iv_ai_chat_icon hoặc tìm trong child views
            ImageView iconView = menuAIChat.findViewById(R.id.iv_ai_chat_icon);
            if (iconView == null) {
                // Fallback: tìm ImageView trong child views
                if (menuAIChat instanceof android.view.ViewGroup) {
                    android.view.ViewGroup group = (android.view.ViewGroup) menuAIChat;
                    for (int i = 0; i < group.getChildCount(); i++) {
                        View child = group.getChildAt(i);
                        if (child instanceof ImageView) {
                            iconView = (ImageView) child;
                            break;
                        } else if (child instanceof android.view.ViewGroup) {
                            // Tìm trong ViewGroup (LinearLayout)
                            android.view.ViewGroup subGroup = (android.view.ViewGroup) child;
                            for (int j = 0; j < subGroup.getChildCount(); j++) {
                                View subChild = subGroup.getChildAt(j);
                                if (subChild instanceof ImageView) {
                                    iconView = (ImageView) subChild;
                                    break;
                                }
                            }
                            if (iconView != null) break;
                        }
                    }
                }
            }
            
            if (iconView != null) {
                Drawable drawable = iconView.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        }
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    // Xử lý click banner đặt vé
    private void onBannerBookClick(View view) {
        navigateToActivity(BookingActivity.class);
    }

    // Xử lý click menu đặt chuyến bay
    private void onBookFlightClick(View view) {
        navigateToActivity(BookingActivity.class);
    }

    // Xử lý click menu chuyến đi của tôi
    private void onMyTripsClick(View view) {
        navigateToActivity(BookingHistoryActivity.class);
    }

    // Xử lý click menu thông báo
    private void onNotificationsClick(View view) {
        navigateToActivity(NotificationActivity.class);
    }

    // Xử lý click menu hồ sơ
    private void onProfileClick(View view) {
        navigateToActivity(Profile.class);
    }

    // Xử lý click avatar
    private void onAvatarClick(View view) {
        navigateToActivity(Profile.class);
    }

    // Xử lý navigation dưới cùng
    private void handleBottomNavigation(int itemId) {
      /*  if (itemId == R.id.nav_home) {
            // Trang chủ - không làm gì
        } else*/
            if (itemId == R.id.nav_search) {
            navigateToActivity(SearchFlightActivity.class);
        } else if (itemId == R.id.nav_ai_plan) {
            navigateToActivity(AIPlannerActivity.class);
        } else if (itemId == R.id.nav_trips) {
            navigateToActivity(BookingHistoryActivity.class);
        } else if (itemId == R.id.nav_flights) {
            navigateToActivity(BookingActivity.class);
        } else if (itemId == R.id.nav_more) {
            showMoreOptionsDialog();
        }
    }

    // Tải thông tin người dùng từ SharedPreferences
    private void loadUserInfo() {
        String fullName = userPrefs.getString("user_name", "");
        String username = userPrefs.getString("username", "Người dùng");

        // Hiển thị tên người dùng
        if (!fullName.isEmpty()) {
            tvWelcome.setText("Chào mừng trở lại,");
            tvUsername.setText(fullName);
        } else {
            tvWelcome.setText("Chào mừng,");
            tvUsername.setText(username);
        }
    }

    // Tải số lượng thông báo chưa đọc từ API
    private void loadUnreadNotificationCount() {
        int userId = userPrefs.getInt("user_id", -1);

        if (userId <= 0) {
            Log.e("MainMenuActivity", "ID người dùng không hợp lệ: " + userId);
            return;
        }

        Call<Integer> call = notificationApi.getUnreadCount(userId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateNotificationsBadge(response.body());
                } else {
                    handleNotificationError(response);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("MainMenuActivity", "Lỗi khi tải thông báo: " + t.getMessage());
            }
        });
    }

    // Cập nhật badge thông báo
    private void updateNotificationsBadge(int unreadCount) {
        if (tvNotificationsBadge == null) return;
        if (unreadCount > 0) {
            tvNotificationsBadge.setVisibility(View.VISIBLE);
            if (unreadCount > 99) {
                tvNotificationsBadge.setText("99+");
            } else {
                tvNotificationsBadge.setText(String.valueOf(unreadCount));
            }
        } else {
            tvNotificationsBadge.setVisibility(View.GONE);
        }
    }

    // Xử lý lỗi khi tải thông báo
    private void handleNotificationError(Response<Integer> response) {
        String errorMessage = "Không thể tải thông báo";

        if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn";
            performLogout();
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }

        Log.e("MainMenuActivity", errorMessage + " - Mã lỗi: " + response.code());
    }

    // Hiển thị dialog tùy chọn thêm
    private void showMoreOptionsDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_more_options, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Các tùy chọn trong dialog
        LinearLayout optionProfile = bottomSheetView.findViewById(R.id.option_profile);
        LinearLayout optionNotifications = bottomSheetView.findViewById(R.id.option_notifications);
        LinearLayout optionSupport = bottomSheetView.findViewById(R.id.option_support);
        LinearLayout optionLogout = bottomSheetView.findViewById(R.id.option_logout);

        // Xử lý click các tùy chọn
        optionProfile.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            navigateToActivity(Profile.class);
        });

        optionNotifications.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            navigateToActivity(NotificationActivity.class);
        });

        optionSupport.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            navigateToActivity(ChatActivity.class);
        });

        optionLogout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showLogoutDialog();
        });

        bottomSheetDialog.show();
    }

    // Hiển thị dialog xác nhận đăng xuất
    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Thực hiện đăng xuất
    private void performLogout() {
        // Xóa thông tin đăng nhập
        userPrefs.edit().clear().apply();

        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Chuyển về màn hình đăng nhập
        redirectToLogin();
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isLoggedIn() {
        return userPrefs.getBoolean("is_logged_in", false);
    }

    // Chuyển đến màn hình đăng nhập
    private void redirectToLogin() {
        Intent intent = new Intent(MainMenuActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Điều hướng đến activity khác
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra trạng thái đăng nhập khi quay lại màn hình
        if (isLoggedIn()) {
            loadUserInfo();
            loadUnreadNotificationCount();
            //bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            redirectToLogin();
        }
    }

    private void showExitDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thoát")
                .setMessage("Bạn có chắc chắn muốn thoát?")
                .setPositiveButton("Thoát", (dialog, which) -> finish())
                .setNegativeButton("Hủy", null)
                .show();
    }
}