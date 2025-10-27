package com.prm.flightbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.api.RetrofitClient;
import com.prm.flightbooking.dto.user.ResetPasswordDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private ImageButton btnBack;
    private String email;
    private String otpCode;
    private AuthApiEndpoint authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Khởi tạo API service
        authApi = ApiServiceProvider.getAuthApi();

        // Nhận dữ liệu từ Intent
        retrieveIntentData();

        bindingView();
        bindingAction();
        setupBackPressHandler();
    }

    private void bindingView() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        btnBack = findViewById(R.id.btn_back);
    }

    private void bindingAction() {
        btnResetPassword.setOnClickListener(this::onBtnResetPasswordClick);
        btnBack.setOnClickListener(this::onBtnBackClick);
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    // Lấy dữ liệu từ Intent
    private void retrieveIntentData() {
        if (getIntent() != null) {
            email = getIntent().getStringExtra("email");
            otpCode = getIntent().getStringExtra("otpCode");

            // Kiểm tra dữ liệu bắt buộc
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(otpCode)) {
                Toast.makeText(this, "Thông tin không hợp lệ. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
    }

    // Quay lại màn hình trước
    private void onBtnBackClick(View view) {
        showExitDialog();
    }

    // Đặt lại mật khẩu
    private void onBtnResetPasswordClick(View view) {
        performResetPassword();
    }

    private void showExitDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thoát")
                .setMessage("Bạn có chắc chắn muốn thoát? Thông tin đã nhập sẽ bị mất.")
                .setPositiveButton("Thoát", (dialog, which) -> finish())
                .setNegativeButton("Hủy", null)
                .show();
    }

    /*
     - Kiểm tra dữ liệu nhập vào
     - Gọi API đặt lại mật khẩu
     - Xử lý phản hồi và chuyển về màn hình đăng nhập
     */
    private void performResetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu nhập vào
        if (!validateInput(newPassword, confirmPassword)) {
            return;
        }

        // Vô hiệu hóa nút để tránh nhấn nhiều lần
        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Đang đặt lại...");

        // Tạo ResetPasswordDto
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto(email, otpCode, newPassword);

        // Gọi API đặt lại mật khẩu
        Call<Map<String, String>> call = authApi.resetPassword(resetPasswordDto);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                // Kích hoạt lại nút sau khi nhận phản hồi
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(ResetPasswordActivity.this, message != null ? message : "Đặt lại mật khẩu thành công", Toast.LENGTH_LONG).show();

                    // Chuyển về màn hình đăng nhập
                    navigateToLogin();

                } else {
                    // Xử lý lỗi từ server
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                // Kích hoạt lại nút khi có lỗi mạng
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");

                // Xử lý lỗi mạng
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Kiểm tra tính hợp lệ của dữ liệu nhập vào
    private boolean validateInput(String newPassword, String confirmPassword) {
        // Kiểm tra mật khẩu mới
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return false;
        }

        // Kiểm tra xác nhận mật khẩu
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Kiểm tra độ dài mật khẩu
        if (newPassword.length() < 8) {
            etNewPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            etNewPassword.requestFocus();
            return false;
        }

        // Kiểm tra độ mạnh mật khẩu
        if (!isPasswordStrong(newPassword)) {
            etNewPassword.setError("Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số");
            etNewPassword.requestFocus();
            return false;
        }

        // Kiểm tra mật khẩu khớp
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    // Kiểm tra độ mạnh mật khẩu
    private boolean isPasswordStrong(String password) {
        // Kiểm tra có ít nhất 1 chữ hoa, 1 chữ thường và 1 số
        boolean hasUpper = false, hasLower = false, hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }

    // Xử lý các lỗi phản hồi từ server
    private void handleErrorResponse(Response<Map<String, String>> response) {
        try {
            String errorMessage = "Đặt lại mật khẩu thất bại";

            if (response.code() == 400) {
                errorMessage = "Thông tin không hợp lệ. Vui lòng kiểm tra lại";
            } else if (response.code() == 401) {
                errorMessage = "Mã xác thực không hợp lệ hoặc đã hết hạn";
            } else if (response.code() == 404) {
                errorMessage = "Không tìm thấy thông tin người dùng";
            } else if (response.code() == 422) {
                errorMessage = "Mật khẩu không đáp ứng yêu cầu bảo mật";
            } else if (response.code() >= 500) {
                errorMessage = "Lỗi server, vui lòng thử lại sau";
            }

            // Thử lấy thông báo lỗi từ response body
            if (response.errorBody() != null) {
                try {
                    Map<String, String> errorMap = RetrofitClient.getGson()
                            .fromJson(response.errorBody().charStream(), Map.class);
                    if (errorMap != null && errorMap.containsKey("message")) {
                        errorMessage = errorMap.get("message");
                    }
                } catch (Exception e) {
                    Log.e("ResetPasswordActivity", "Error parsing error body: " + e.getMessage());
                }
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            Log.e("ResetPasswordActivity", "Error handling response: " + e.getMessage());
        }
    }

    // Chuyển về màn hình đăng nhập
    private void navigateToLogin() {
        Intent intent = new Intent(ResetPasswordActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Điều hướng đến activity khác
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }
}