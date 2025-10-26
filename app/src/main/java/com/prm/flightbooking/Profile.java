package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.dto.user.UserProfileDto;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    private ImageButton btnBack, btnEditProfile;
    private ImageView ivProfileAvatar;
    private TextView tvUserName, tvUserEmail, tvUsername, tvPhone, tvGender, tvDateOfBirth, tvMemberSince;
    private TextView tvFlightsCount, tvCountriesCount, tvMilesCount;
    private LinearLayout menuPersonalInfo, menuTravelPreferences, menuMyBookings, menuTripHistory;
    private LinearLayout menuNotifications, menuLanguage, menuHelp, menuLogout;
    private Switch switchNotifications;
    private AuthApiEndpoint authApi;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authApi = ApiServiceProvider.getAuthApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        bindingView();
        bindingAction();

        // Lấy và hiển thị hồ sơ người dùng
        fetchUserProfile();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        ivProfileAvatar = findViewById(R.id.iv_profile_avatar);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUsername = findViewById(R.id.tv_username);
        tvPhone = findViewById(R.id.tv_phone);
        tvGender = findViewById(R.id.tv_gender);
        tvDateOfBirth = findViewById(R.id.tv_date_of_birth);
        tvMemberSince = findViewById(R.id.tv_member_since);
        tvFlightsCount = findViewById(R.id.tv_flights_count);
        tvCountriesCount = findViewById(R.id.tv_countries_count);
        tvMilesCount = findViewById(R.id.tv_miles_count);
        menuPersonalInfo = findViewById(R.id.menu_personal_info);
        menuTravelPreferences = findViewById(R.id.menu_travel_preferences);
        menuMyBookings = findViewById(R.id.menu_my_bookings);
        menuTripHistory = findViewById(R.id.menu_trip_history);
        menuNotifications = findViewById(R.id.menu_notifications);
        switchNotifications = findViewById(R.id.switch_notifications);
        menuLanguage = findViewById(R.id.menu_language);
        menuHelp = findViewById(R.id.menu_help);
        menuLogout = findViewById(R.id.menu_logout);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBackClick);
        btnEditProfile.setOnClickListener(this::onEditProfileClick);
        menuPersonalInfo.setOnClickListener(this::onPersonalInfoClick);
        menuTravelPreferences.setOnClickListener(this::onTravelPreferencesClick);
        menuMyBookings.setOnClickListener(this::onMyBookingsClick);
        menuTripHistory.setOnClickListener(this::onTripHistoryClick);
        switchNotifications.setOnClickListener(this::onNotificationsSwitchClick);
        menuLanguage.setOnClickListener(this::onLanguageClick);
        menuHelp.setOnClickListener(this::onHelpClick);
        menuLogout.setOnClickListener(this::onLogoutClick);
    }

    // Button back
    private void onBackClick(View view) {
        finish();
    }

    // Button edit profile
    private void onEditProfileClick(View view) {
        navigateToActivity(EditProfileActivity.class);
    }

    // Button edit profile
    private void onPersonalInfoClick(View view) {
        navigateToActivity(EditProfileActivity.class);
    }

    /*Menu click*/
    private void onTravelPreferencesClick(View view) {
        Toast.makeText(this, "Travel Preferences coming soon", Toast.LENGTH_SHORT).show();
    }

    private void onMyBookingsClick(View view) {
        Toast.makeText(this, "My Bookings coming soon", Toast.LENGTH_SHORT).show();
    }

    private void onTripHistoryClick(View view) {
        navigateToActivity(BookingHistoryActivity.class);
    }

    private void onNotificationsSwitchClick(View view) {
        boolean isChecked = switchNotifications.isChecked();
        Toast.makeText(this, "Notifications coming soon " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
    }

    private void onLanguageClick(View view) {
        Toast.makeText(this, "Language selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void onHelpClick(View view) {
        Toast.makeText(this, "Help & Support coming soon", Toast.LENGTH_SHORT).show();
    }

    // Logout
    private void onLogoutClick(View view) {
        showLogoutDialog();
    }

    // Lấy hồ sơ người dùng từ API
    private void fetchUserProfile() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        Call<UserProfileDto> call = authApi.getProfile(userId);
        call.enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(Call<UserProfileDto> call, Response<UserProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateProfileUI(response.body());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                Toast.makeText(Profile.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật giao diện người dùng với user data
    private void updateProfileUI(UserProfileDto user) {
        tvUserName.setText(user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : "User");
        tvUserEmail.setText(user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : "Chưa có email");

        if (tvUsername != null) {
            tvUsername.setText(user.getUsername() != null && !user.getUsername().isEmpty() ? "@" + user.getUsername() : "@user");
        }

        if (tvPhone != null) {
            tvPhone.setText(user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Chưa cập nhật");
        }

        if (tvGender != null) {
            tvGender.setText(translateGender(user.getGender()));
        }

        if (tvDateOfBirth != null) {
            tvDateOfBirth.setText(formatDateOfBirth(user.getDateOfBirth()));
        }

        if (tvMemberSince != null) {
            tvMemberSince.setText(formatMemberSince(user.getCreatedAt()));
        }

        tvFlightsCount.setText(String.valueOf(user.getTotalBookings()));
        tvCountriesCount.setText("12"); // Fix cứng
        tvMilesCount.setText("45K"); // Fix cứng

        Log.d("ProfileActivity", "UserProfileDto - " + user.toString());
    }

    // Định dạng số điện thoại
/*    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return "Chưa cập nhật";

        // Loại bỏ các ký tự không phải số
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Định dạng số điện thoại theo chuẩn Việt Nam: (+84) XXX XXX XXX
        if (cleanNumber.startsWith("0")) {
            cleanNumber = "+84" + cleanNumber.substring(1);
        } else if (!cleanNumber.startsWith("+84")) {
            cleanNumber = "+84" + cleanNumber;
        }

        // Giả sử số điện thoại có ít nhất 9 số (sau +84)
        if (cleanNumber.length() >= 12) {
            return String.format("(%s) %s %s %s",
                    cleanNumber.substring(0, 3),
                    cleanNumber.substring(3, 6),
                    cleanNumber.substring(6, 9),
                    cleanNumber.substring(9));
        }
        return cleanNumber;
    }*/

    // Dịch giới tính sang tiếng Việt
    private String translateGender(String gender) {
        if (gender == null || gender.isEmpty()) return "Chưa cập nhật";
        switch (gender.toLowerCase()) {
            case "male":
            case "nam":
                return "Nam";
            case "female":
            case "nữ":
                return "Nữ";
            case "other":
            case "khác":
                return "Khác";
            default:
                return "Chưa cập nhật";
        }
    }

    // Format ngày sinh
    private String formatDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) return "Chưa cập nhật";
        try {
            String[] parts = dateOfBirth.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
            return dateOfBirth;
        } catch (Exception e) {
            return "Chưa cập nhật";
        }
    }

    // Format ngày tham gia
    private String formatMemberSince(Date createdAt) {
        if (createdAt == null) return "Chưa xác định";
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(createdAt);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            return String.format("Ngày %d tháng %02d năm %d", day, month, year);
        } catch (Exception e) {
            return "Chưa xác định";
        }
    }

    // Xử lý phản hồi lỗi API
    private void handleErrorResponse(Response<UserProfileDto> response) {
        String errorMessage = "Không thể tải hồ sơ";
        if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
            performLogout();
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy hồ sơ người dùng";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Show logout confirm dialog
    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Thực hiện thao tác đăng xuất
    private void performLogout() {
        sharedPreferences.edit().clear().apply();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    // Kiểm tra xem người dùng đã đăng nhập chưa
    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoggedIn()) {
            fetchUserProfile();
        } else {
            redirectToLogin();
        }
    }
}