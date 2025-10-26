package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.dto.user.ChangePasswordDto;

import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton btnBack, btnHelp;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private Button btnChangePassword;
    private TextView tvPasswordStrength;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;
    private ImageView checkLength, checkUppercase, checkLowercase, checkNumber, checkSpecial;
    private AuthApiEndpoint authApi;
    private SharedPreferences sharedPreferences;

    // Các pattern kiểm tra mật khẩu
    private static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[!@#$%^*]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo API
        authApi = ApiServiceProvider.getAuthApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        btnHelp = findViewById(R.id.btn_help);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tilCurrentPassword = findViewById(R.id.til_current_password);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        tvPasswordStrength = findViewById(R.id.tv_password_strength);
        strengthBar1 = findViewById(R.id.strength_bar_1);
        strengthBar2 = findViewById(R.id.strength_bar_2);
        strengthBar3 = findViewById(R.id.strength_bar_3);
        strengthBar4 = findViewById(R.id.strength_bar_4);
        checkLength = findViewById(R.id.check_length);
        checkUppercase = findViewById(R.id.check_uppercase);
        checkLowercase = findViewById(R.id.check_lowercase);
        checkNumber = findViewById(R.id.check_number);
        checkSpecial = findViewById(R.id.check_special);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBackClick);
        btnHelp.setOnClickListener(this::onHelpClick);
        btnChangePassword.setOnClickListener(this::onChangePasswordClick);

        // Theo dõi thay đổi mật khẩu mới
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword(s.toString());
                updateChangeButtonState();
            }
        });

        // Theo dõi thay đổi xác nhận mật khẩu
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword();
                updateChangeButtonState();
            }
        });

        // Theo dõi thay đổi mật khẩu hiện tại
        etCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateChangeButtonState();
            }
        });
    }

    // Nút quay lại
    private void onBackClick(View view) {
        finish();
    }

    // Nút trợ giúp
    private void onHelpClick(View view) {
        Toast.makeText(this, "Tính năng trợ giúp sẽ có sớm", Toast.LENGTH_SHORT).show();
    }

    // Nút đổi mật khẩu
    private void onChangePasswordClick(View view) {
        performChangePassword();
    }

    // Kiểm tra độ mạnh mật khẩu
    private void validatePassword(String password) {
        int strength = 0;
        boolean hasLength = LENGTH_PATTERN.matcher(password).matches();
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasNumber = NUMBER_PATTERN.matcher(password).find();
        boolean hasSpecial = SPECIAL_PATTERN.matcher(password).find();

        // Cập nhật các biểu tượng yêu cầu
        checkLength.setImageResource(hasLength ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkUppercase.setImageResource(hasUppercase ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkLowercase.setImageResource(hasLowercase ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkNumber.setImageResource(hasNumber ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkSpecial.setImageResource(hasSpecial ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);

        // Tính toán độ mạnh mật khẩu
        if (!hasLength) {
            strength = 0; // Không đủ 8 ký tự thì strength = 0
        } else {
            // Tính strength dựa trên 4 điều kiện còn lại
            if (hasUppercase) strength++;
            if (hasLowercase) strength++;
            if (hasNumber) strength++;
            if (hasSpecial) strength++;
        }

        // Cập nhật thanh độ mạnh và văn bản
        updateStrengthBars(strength);
        updateStrengthText(strength);
    }

    // Cập nhật thanh hiển thị độ mạnh mật khẩu
    private void updateStrengthBars(int strength) {
        int colorWeak = getResources().getColor(R.color.weak_password);
        int colorMedium = getResources().getColor(R.color.medium_password);
        int colorStrong = getResources().getColor(R.color.strong_password);
        int colorDefault = getResources().getColor(R.color.grey);

        strengthBar1.setBackgroundColor(strength >= 1 ? colorWeak : colorDefault);
        strengthBar2.setBackgroundColor(strength >= 2 ? colorMedium : colorDefault);
        strengthBar3.setBackgroundColor(strength >= 3 ? colorMedium : colorDefault);
        strengthBar4.setBackgroundColor(strength >= 4 ? colorStrong : colorDefault);
    }

    // Cập nhật văn bản hiển thị độ mạnh mật khẩu
    private void updateStrengthText(int strength) {
        if (strength == 0) {
            tvPasswordStrength.setText("Nhập mật khẩu để kiểm tra độ mạnh");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.grey));
        } else if (strength <= 2) {
            tvPasswordStrength.setText("Yếu");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.weak_password));
        } else if (strength <= 3) {
            tvPasswordStrength.setText("Trung bình");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.medium_password));
        } else {
            tvPasswordStrength.setText("Mạnh");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.strong_password));
        }
    }

    // Kiểm tra xác nhận mật khẩu
    private void validateConfirmPassword() {
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (!confirmPassword.isEmpty() && !confirmPassword.equals(newPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
        } else {
            tilConfirmPassword.setError(null);
        }
    }

    // Cập nhật trạng thái nút đổi mật khẩu
    private void updateChangeButtonState() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        /*  boolean isValidPassword = LENGTH_PATTERN.matcher(newPassword).matches() &&
                UPPERCASE_PATTERN.matcher(newPassword).matches() &&
                LOWERCASE_PATTERN.matcher(newPassword).matches() &&
                NUMBER_PATTERN.matcher(newPassword).matches() &&
                SPECIAL_PATTERN.matcher(newPassword).matches();*/

        // Chỉ cần đủ 8 ký tự, không cần các điều kiện khác
        boolean isValidPassword = LENGTH_PATTERN.matcher(newPassword).matches();

        boolean isEnabled = !currentPassword.isEmpty() &&
                !newPassword.isEmpty() &&
                !confirmPassword.isEmpty() &&
                newPassword.equals(confirmPassword) &&
                isValidPassword;

        btnChangePassword.setEnabled(isEnabled);
        btnChangePassword.setAlpha(isEnabled ? 1.0f : 0.5f);
    }

    // Thực hiện đổi mật khẩu
    private void performChangePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        int userId = sharedPreferences.getInt("user_id", -1);

        // Kiểm tra các trường nhập liệu
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        if (currentPassword.isEmpty()) {
            tilCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            etCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // Kiểm tra độ mạnh mật khẩu
        int strength = calculatePasswordStrength(newPassword);

        if (!LENGTH_PATTERN.matcher(newPassword).matches()) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 8 ký tự", Toast.LENGTH_LONG).show();
            return;
        }

        // Hiển thị cảnh báo nếu mật khẩu yếu
        if (strength <= 3) {
            showWeakPasswordDialog(userId, currentPassword, newPassword);
        } else {
            callChangePasswordApi(userId, currentPassword, newPassword);
        }
    }

    // Tính toán độ mạnh mật khẩu
    private int calculatePasswordStrength(String password) {
        int strength = 0;
        boolean hasLength = LENGTH_PATTERN.matcher(password).matches();
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasNumber = NUMBER_PATTERN.matcher(password).find();
        boolean hasSpecial = SPECIAL_PATTERN.matcher(password).find();

        if (hasLength) {
            if (hasUppercase) strength++;
            if (hasLowercase) strength++;
            if (hasNumber) strength++;
            if (hasSpecial) strength++;
        }

        return strength;
    }

    // Hiển thị dialog cảnh báo mật khẩu yếu
    private void showWeakPasswordDialog(int userId, String currentPassword, String newPassword) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cảnh báo mật khẩu yếu")
                .setMessage("Mật khẩu bạn nhập khá yếu. Bạn có chắc chắn muốn tiếp tục đổi mật khẩu không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    callChangePasswordApi(userId, currentPassword, newPassword);
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    // Gọi API đổi mật khẩu
    private void callChangePasswordApi(int userId, String currentPassword, String newPassword) {
        // Disable button để tránh click nhiều lần
        btnChangePassword.setEnabled(false);
        btnChangePassword.setText("Đang cập nhật...");

        // Tạo DTO và gọi API
        ChangePasswordDto changePasswordDto = new ChangePasswordDto(currentPassword, newPassword);
        Call<Map<String, String>> call = authApi.changePassword(userId, changePasswordDto);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                // Enable lại button sau khi nhận response
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("CẬP NHẬT MẬT KHẨU");

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> result = response.body();
                    String message = result.get("message");

                    // Hiển thị thông báo thành công
                    Toast.makeText(ChangePasswordActivity.this,
                            "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();

                    // Đăng xuất và chuyển về màn hình login
                    performLogout();
                } else {
                    // Xử lý lỗi từ server
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                // Enable lại button khi có lỗi network
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("CẬP NHẬT MẬT KHẨU");

                // Xử lý lỗi network
                Toast.makeText(ChangePasswordActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Xử lý các lỗi response từ server
    private void handleErrorResponse(Response<Map<String, String>> response) {
        String errorMessage = "Đổi mật khẩu thất bại";

        try {
            if (response.code() == 401) {
                errorMessage = "Mật khẩu hiện tại không đúng";
                tilCurrentPassword.setError(errorMessage);
                etCurrentPassword.requestFocus();
            } else if (response.code() == 400) {
                errorMessage = "Thông tin mật khẩu không hợp lệ";
                tilNewPassword.setError(errorMessage);
                etNewPassword.requestFocus();
            } else if (response.code() == 404) {
                errorMessage = "Không tìm thấy người dùng";
                redirectToLogin();
            } else if (response.code() >= 500) {
                errorMessage = "Lỗi server, vui lòng thử lại sau";
            }
        } catch (Exception e) {
            errorMessage = "Đã có lỗi xảy ra";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    // Chuyển về màn hình đăng nhập
    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Thực hiện đăng xuất
    private void performLogout() {
        sharedPreferences.edit().clear().apply();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }
}