package com.prm.flightbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.api.RetrofitClient;
import com.prm.flightbooking.dto.user.ForgotPasswordRequestDto;
import com.prm.flightbooking.fragments.OtpVerificationDialogFragment;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnSendResetLink;
    private ImageButton btnBack;
    private TextView tvBackToLogin;
    private AuthApiEndpoint authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo API service
        authApi = ApiServiceProvider.getAuthApi();

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        etEmail = findViewById(R.id.et_email);
        btnSendResetLink = findViewById(R.id.btn_send_reset_link);
        btnBack = findViewById(R.id.btn_back);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
    }

    private void bindingAction() {
        btnSendResetLink.setOnClickListener(this::onBtnSendResetLinkClick);
        btnBack.setOnClickListener(this::onBtnBackClick);
        tvBackToLogin.setOnClickListener(this::onTvBackToLoginClick);
    }

    // Quay lại màn hình trước
    private void onBtnBackClick(View view) {
        finish();
    }

    // Chuyển về màn hình đăng nhập
    private void onTvBackToLoginClick(View view) {
        Intent intent = new Intent(ForgotPasswordActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Gửi mã xác thực về email
    private void onBtnSendResetLinkClick(View view) {
        performSendResetLink();
    }

    /*
     - Kiểm tra dữ liệu nhập vào
     - Gọi API gửi mã xác thực
     - Xử lý phản hồi và hiển thị dialog nhập OTP
     */
    private void performSendResetLink() {
        String email = etEmail.getText().toString().trim();

        // Kiểm tra dữ liệu nhập vào
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email của bạn");
            etEmail.requestFocus();
            return;
        }

        // Kiểm tra định dạng email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        // Vô hiệu hóa nút để tránh nhấn nhiều lần
        btnSendResetLink.setEnabled(false);
        btnSendResetLink.setText("Đang gửi...");

        // Tạo request DTO
        ForgotPasswordRequestDto requestDto = new ForgotPasswordRequestDto(email);

        // Gọi API gửi mã xác thực
        Call<Map<String, String>> call = authApi.forgotPassword(requestDto);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                // Kích hoạt lại nút sau khi nhận phản hồi
                btnSendResetLink.setEnabled(true);
                btnSendResetLink.setText("Gửi mã xác thực");

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(ForgotPasswordActivity.this, message != null ? message : "Mã xác thực đã được gửi đến email của bạn", Toast.LENGTH_LONG).show();

                    // Hiển thị dialog nhập mã OTP
                    showOtpVerificationDialog(email);

                } else {
                    // Xử lý lỗi từ server
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                // Kích hoạt lại nút khi có lỗi mạng
                btnSendResetLink.setEnabled(true);
                btnSendResetLink.setText("Gửi mã xác thực");

                // Xử lý lỗi mạng
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý các lỗi phản hồi từ server
    private void handleErrorResponse(Response<Map<String, String>> response) {
        try {
            String errorMessage = "Gửi yêu cầu thất bại";

            if (response.code() == 400) {
                errorMessage = "Thông tin email không hợp lệ";
            } else if (response.code() == 404) {
                errorMessage = "Email không tồn tại trong hệ thống";
            } else if (response.code() == 429) {
                errorMessage = "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau";
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
                    Log.e("ForgotPasswordActivity", "Error parsing error body: " + e.getMessage());
                }
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            Log.e("ForgotPasswordActivity", "Error handling response: " + e.getMessage());
        }
    }

    // Hiển thị dialog nhập mã OTP
    private void showOtpVerificationDialog(String email) {
        OtpVerificationDialogFragment dialogFragment = new OtpVerificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        dialogFragment.setArguments(args);

        // Thiết lập listener để nhận kết quả từ dialog
        dialogFragment.setOtpVerificationListener(new OtpVerificationDialogFragment.OtpVerificationListener() {
            @Override
            public void onOtpVerified(String email, String otpCode) {
                // OTP đã được xác thực thành công
                Toast.makeText(ForgotPasswordActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();

                // Chuyển sang màn hình đặt lại mật khẩu
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otpCode", otpCode);
                startActivity(intent);
                finish();
            }

            @Override
            public void onOtpVerificationFailed() {
                // Xác thực OTP thất bại
                Toast.makeText(ForgotPasswordActivity.this, "Xác thực OTP thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResendOtpRequested(String email) {
                // Gửi lại mã OTP
                performSendResetLink();
            }
        });

        dialogFragment.show(getSupportFragmentManager(), "otp_verification_dialog");
    }

    // Điều hướng đến activity khác
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }
}