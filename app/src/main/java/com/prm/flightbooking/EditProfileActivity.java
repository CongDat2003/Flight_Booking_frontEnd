package com.prm.flightbooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.dto.user.DeleteAccountDto;
import com.prm.flightbooking.dto.user.UpdateProfileDto;
import com.prm.flightbooking.dto.user.UserProfileDto;

import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etFirstName, etLastName, etEmail, etPhone;
    private TextView tvDateOfBirth;
    private Button btnSave;
    private LinearLayout btnDatePicker, btnChangePassword, btnDeleteAccount;
    private Switch switchEmailNotifications, switchPushNotifications, switchSmsNotifications;
    private ImageView ivProfilePhoto;
    private Spinner spinnerGender;
    private AuthApiEndpoint authApi;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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
        setupGenderSpinner();

        // Tải thông tin hồ sơ hiện tại
        loadCurrentProfileData();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        tvDateOfBirth = findViewById(R.id.tv_date_of_birth);
        btnSave = findViewById(R.id.btn_save);
        btnDatePicker = findViewById(R.id.btn_date_picker);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);
        switchEmailNotifications = findViewById(R.id.switch_email_notifications);
        switchPushNotifications = findViewById(R.id.switch_push_notifications);
        switchSmsNotifications = findViewById(R.id.switch_sms_notifications);
        ivProfilePhoto = findViewById(R.id.iv_profile_photo);
        spinnerGender = findViewById(R.id.spinner_gender);

        // Khóa trường email không cho chỉnh sửa
        etEmail.setEnabled(false);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBtnBackClick);
        btnSave.setOnClickListener(this::onBtnSaveClick);
        btnDatePicker.setOnClickListener(this::onBtnDatePickerClick);
        btnChangePassword.setOnClickListener(this::onBtnChangePasswordClick);
        btnDeleteAccount.setOnClickListener(this::onBtnDeleteAccountClick);

        // Thiết lập sự kiện cho các công tắc thông báo
        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                showComingSoonToast("Thông báo email"));
        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                showComingSoonToast("Thông báo đẩy"));
        switchSmsNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                showComingSoonToast("Thông báo SMS"));
    }

    // Nút quay lại
    private void onBtnBackClick(View view) {
        finish();
    }

    // Nút lưu thông tin
    private void onBtnSaveClick(View view) {
        performUpdateProfile();
    }

    // Nút chọn ngày sinh
    private void onBtnDatePickerClick(View view) {
        showDatePickerDialog();
    }

    // Nút đổi mật khẩu
    private void onBtnChangePasswordClick(View view) {
        navigateToActivity(ChangePasswordActivity.class);
    }

    // Nút xóa tài khoản
    private void onBtnDeleteAccountClick(View view) {
        showDeleteAccountConfirmDialog();
    }

    // Thiết lập danh sách giới tính
    private void setupGenderSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Chọn giới tính", "Nam", "Nữ"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    // Hiển thị hộp thoại chọn ngày
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    tvDateOfBirth.setText(selectedDate);
                },
                year, month, day);

        // Giới hạn ngày tối đa là hôm nay
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    // Hiển thị hộp thoại xác nhận xóa tài khoản
    private void showDeleteAccountConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản? Hành động này sẽ vô hiệu hóa tài khoản của bạn.")
                .setPositiveButton("Xóa", (dialog, which) -> showPasswordInputDialog())
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Hiển thị hộp thoại nhập mật khẩu để xác nhận xóa tài khoản
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận mật khẩu");

        // Tạo ô nhập mật khẩu
        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setPadding(16, 16, 16, 16);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setBoxBackgroundColor(getResources().getColor(android.R.color.transparent));
        textInputLayout.setHint("Nhập mật khẩu");

        TextInputEditText passwordInput = new TextInputEditText(this);
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        textInputLayout.addView(passwordInput);

        builder.setView(textInputLayout);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                textInputLayout.setError("Vui lòng nhập mật khẩu");
            } else {
                performDeleteAccount(password);
            }
        });
        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Chỉ cho phép nhấn nút xác nhận khi đã nhập mật khẩu
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        passwordInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                textInputLayout.setError(null);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }
        });
    }

    // Tải thông tin hồ sơ hiện tại từ API
    private void loadCurrentProfileData() {
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
                Toast.makeText(EditProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật giao diện với thông tin người dùng
    private void updateProfileUI(UserProfileDto user) {
        // Tách tên đầy đủ thành họ và tên
        String fullName = user.getFullName() != null ? user.getFullName() : "";
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        etFirstName.setText(firstName);
        etLastName.setText(lastName);
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        tvDateOfBirth.setText(user.getDateOfBirth() != null && !user.getDateOfBirth().isEmpty()? user.getDateOfBirth() : "Chọn ngày sinh");

        // Thiết lập giới tính
        String gender = translateGender(user.getGender());
        int genderPosition = getGenderPosition(gender);
        spinnerGender.setSelection(genderPosition);

        // Thiết lập trạng thái thông báo (tạm thời để false)
        switchEmailNotifications.setChecked(false);
        switchPushNotifications.setChecked(false);
        switchSmsNotifications.setChecked(false);

        Log.d("EditProfileActivity", "Loaded user profile - " + user.toString());
    }

    // Thực hiện cập nhật hồ sơ
    /*
     - Validate thông tin đầu vào
     - Gọi API cập nhật
     - Xử lý phản hồi và lưu thông tin
     */
    private void performUpdateProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String phone = etPhone.getText().toString().trim();
        String dateOfBirth = tvDateOfBirth.getText().toString();
        String gender = spinnerGender.getSelectedItem().toString();
        int userId = sharedPreferences.getInt("user_id", -1);

        // Kiểm tra tính hợp lệ của thông tin
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Vui lòng nhập họ");
            etFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Vui lòng nhập tên");
            etLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (dateOfBirth.equals("Chọn ngày sinh") || !dateOfBirth.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh hợp lệ", Toast.LENGTH_SHORT).show();
            btnDatePicker.requestFocus();
            return;
        }

        if (gender.equals("Chọn giới tính")) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            spinnerGender.requestFocus();
            return;
        }

        // Vô hiệu hóa nút lưu để tránh nhấn nhiều lần
        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        // Tạo đối tượng cập nhật hồ sơ
        UpdateProfileDto updateProfileDto = new UpdateProfileDto(fullName, phone, dateOfBirth, gender);

        // Gọi API cập nhật
        Call<UserProfileDto> call = authApi.updateProfile(userId, updateProfileDto);
        call.enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(Call<UserProfileDto> call, Response<UserProfileDto> response) {
                // Kích hoạt lại nút lưu
                btnSave.setEnabled(true);
                btnSave.setText("LƯU");

                if (response.isSuccessful() && response.body() != null) {
                    UserProfileDto updatedUser = response.body();

                    // Lưu thông tin mới vào SharedPreferences
                    saveUserInfo(updatedUser);

                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                // Kích hoạt lại nút lưu khi có lỗi
                btnSave.setEnabled(true);
                btnSave.setText("LƯU");

                Toast.makeText(EditProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thực hiện xóa tài khoản
    private void performDeleteAccount(String password) {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        DeleteAccountDto deleteDto = new DeleteAccountDto(password);
        Call<Map<String, String>> call = authApi.deleteAccount(userId, deleteDto);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> result = response.body();
                    Toast.makeText(EditProfileActivity.this, result.get("message"), Toast.LENGTH_LONG).show();

                    if ("Account deleted successfully".equals(result.get("message"))) {
                        // Xóa toàn bộ thông tin người dùng
                        sharedPreferences.edit().clear().apply();
                        redirectToLogin();
                    }
                } else {
                    handleDeleteAccountError(response);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Xử lý lỗi khi xóa tài khoản
    private void handleDeleteAccountError(Response<Map<String, String>> response) {
        String errorMessage = "Lỗi khi xóa tài khoản";

        try {
            if (response.code() == 401) {
                errorMessage = "Mật khẩu không đúng";
            } else if (response.code() == 404) {
                errorMessage = "Không tìm thấy người dùng";
                redirectToLogin();
            } else if (response.code() == 400) {
                String responseBody = response.errorBody().string();
                if (responseBody.contains("active bookings")) {
                    errorMessage = "Không thể xóa tài khoản do có vé đang hoạt động";
                }
            } else if (response.code() >= 500) {
                errorMessage = "Lỗi máy chủ, vui lòng thử lại sau";
            }
        } catch (Exception e) {
            Log.e("EditProfileActivity", "Error parsing delete account response", e);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    // Xử lý các lỗi phản hồi từ server
    private void handleErrorResponse(Response<?> response) {
        String errorMessage = "Không thể cập nhật hồ sơ";

        if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
            performLogout();
        } else if (response.code() == 400) {
            errorMessage = "Thông tin hồ sơ không hợp lệ";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi máy chủ, vui lòng thử lại sau";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Lưu thông tin người dùng vào SharedPreferences
    private void saveUserInfo(UserProfileDto user) {
        int userIdToSave = user.getUserId();

        // Kiểm tra tính hợp lệ của userId
        if (userIdToSave <= 0) {
            Toast.makeText(this, "Không thể lưu thông tin: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu thông tin vào SharedPreferences
        sharedPreferences.edit()
                .putInt("user_id", user.getUserId())
                .putString("username", user.getUsername())
                .putString("user_name", user.getFullName())
                .putString("user_email", user.getEmail())
                .putString("user_phone", user.getPhone())
                .putString("date_of_birth", user.getDateOfBirth())
                .putString("gender", user.getGender())
                .putInt("total_bookings", user.getTotalBookings())
                .putBoolean("is_logged_in", true)
                .apply();
    }

    // Dịch giới tính sang tiếng Việt
    private String translateGender(String gender) {
        if (gender == null || gender.isEmpty()) return "Chọn giới tính";

        switch (gender.toLowerCase()) {
            case "male":
            case "nam":
                return "Nam";
            case "female":
            case "nữ":
                return "Nữ";
            default:
                return "Chọn giới tính";
        }
    }

    // Lấy vị trí giới tính trong spinner
    private int getGenderPosition(String gender) {
        if (gender.equals("Nam")) {
            return 1;
        } else if (gender.equals("Nữ")) {
            return 2;
        }
        return 0;
    }

    // Hiển thị thông báo tính năng sắp ra mắt
    private void showComingSoonToast(String feature) {
        Toast.makeText(this, feature + ": Sắp ra mắt", Toast.LENGTH_SHORT).show();
    }

    // Thực hiện đăng xuất
    private void performLogout() {
        sharedPreferences.edit().clear().apply();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    // Chuyển hướng đến màn hình đăng nhập
    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Chuyển đến activity khác
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra lại trạng thái đăng nhập khi quay lại màn hình
        if (!isLoggedIn()) {
            redirectToLogin();
        } else {
            loadCurrentProfileData();
        }
    }
}