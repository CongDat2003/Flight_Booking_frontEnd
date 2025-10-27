package com.prm.flightbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chaos.view.PinView;
import com.prm.flightbooking.R;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.api.RetrofitClient;
import com.prm.flightbooking.dto.user.VerifyOtpDto;

import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationDialogFragment extends DialogFragment {

    private static final long OTP_RESEND_COOLDOWN = 60000; // 60 giây

    private PinView pinViewOtp;
    private Button btnVerifyOtp, btnResendOtp;
    private TextView tvResendTimer;

    private String email;
    private AuthApiEndpoint authApi;
    private CountDownTimer countDownTimer;

    // Listener để giao tiếp với Activity gọi nó
    public interface OtpVerificationListener {
        void onOtpVerified(String email, String otpCode);
        void onOtpVerificationFailed();
        void onResendOtpRequested(String email);
    }

    private OtpVerificationListener listener;

    public void setOtpVerificationListener(OtpVerificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }
        authApi = ApiServiceProvider.getAuthApi();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp_verification, null);

        bindingView(view);
        bindingAction();

        startResendCooldownTimer(); // Bắt đầu đếm ngược ngay khi dialog hiển thị

        builder.setView(view);
        return builder.create();
    }

    private void bindingView(View view) {
        pinViewOtp = view.findViewById(R.id.pin_view_otp);
        btnVerifyOtp = view.findViewById(R.id.btn_verify_otp);
        btnResendOtp = view.findViewById(R.id.btn_resend_otp);
        tvResendTimer = view.findViewById(R.id.tv_resend_timer);
    }

    private void bindingAction() {
        btnVerifyOtp.setOnClickListener(this::onBtnVerifyOtpClick);
        btnResendOtp.setOnClickListener(this::onBtnResendOtpClick);

        // Tự động gọi verifyOtp khi đủ 6 số
        pinViewOtp.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    onBtnVerifyOtpClick(btnVerifyOtp);
                }
            }

            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    // Xử lý sự kiện khi nhấn nút xác nhận OTP
    private void onBtnVerifyOtpClick(View view) {
        String otpCode = pinViewOtp.getText() != null ? pinViewOtp.getText().toString() : "";

        if (TextUtils.isEmpty(otpCode) || otpCode.length() < 6) {
            // Hiển thị lỗi ngay trên PinView và focus lại
            pinViewOtp.setError("Vui lòng nhập đủ 6 chữ số OTP");
            pinViewOtp.requestFocus();
            return;
        }

        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Đang xác thực...");

        VerifyOtpDto verifyOtpDto = new VerifyOtpDto(email, otpCode);
        authApi.verifyOtp(verifyOtpDto).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("XÁC NHẬN OTP");

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onOtpVerified(email, otpCode);
                    }
                    dismiss(); // Đóng dialog
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("XÁC NHẬN OTP");
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onOtpVerificationFailed();
                }
            }
        });
    }

    // Xử lý sự kiện khi nhấn nút gửi lại mã OTP
    private void onBtnResendOtpClick(View view) {
        if (listener != null) {
            listener.onResendOtpRequested(email); // Yêu cầu Activity gửi lại OTP
            Toast.makeText(getContext(), "Đang gửi lại mã OTP...", Toast.LENGTH_SHORT).show();
            startResendCooldownTimer(); // Bắt đầu đếm ngược lại
        }
    }

    // Bắt đầu đếm ngược thời gian chờ gửi lại OTP
    private void startResendCooldownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        btnResendOtp.setEnabled(false);
        tvResendTimer.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(OTP_RESEND_COOLDOWN, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResendTimer.setText(String.format(Locale.getDefault(), "Gửi lại sau: %d giây", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvResendTimer.setVisibility(View.GONE);
                btnResendOtp.setEnabled(true);
                btnResendOtp.setText("Gửi lại mã");
            }
        }.start();
    }

    // Xử lý lỗi trả về khi xác thực OTP không thành công
    private void handleErrorResponse(Response<Map<String, String>> response) {
        String errorMessage = "Mã OTP không hợp lệ hoặc đã hết hạn.";
        try {
            if (response.errorBody() != null) {
                Map<String, String> errorMap = RetrofitClient.getGson().fromJson(response.errorBody().charStream(), Map.class);
                if (errorMap != null && errorMap.containsKey("message")) {
                    errorMessage = errorMap.get("message");
                }
            }
        } catch (Exception e) {
            Log.e("OtpVerification", "Lỗi khi phân tích cú pháp nội dung lỗi: " + e.getMessage());
            errorMessage = "Đã có lỗi xảy ra";
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onOtpVerificationFailed();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
