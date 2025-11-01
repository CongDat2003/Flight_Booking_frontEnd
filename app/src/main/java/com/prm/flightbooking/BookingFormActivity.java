package com.prm.flightbooking;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingResponseDto;
import com.prm.flightbooking.dto.booking.CreateBookingDto;
import com.prm.flightbooking.dto.booking.PassengerInfoDto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFormActivity extends AppCompatActivity {

    private TextInputEditText etNotes;
    private TextView tvBookingSummary, tvTotalPrice;
    private Button btnBook;
    private CheckBox cbTerms;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private BookingApiEndpoint bookingApi;
    private SharedPreferences sharedPreferences;
    private int flightId, userId, seatClassId, passengerCount;
    private List<PassengerInfoDto> passengerDetails;
    private BigDecimal seatClassPrice;
    private int notificationId = 1000;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final String TAG = "BookingFormActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Retrieve seatClassPrice from Intent
        try {
            seatClassPrice = (BigDecimal) getIntent().getSerializableExtra("seatClassPrice");
        } catch (Exception e) {
            seatClassPrice = null;
            Log.e(TAG, "Error retrieving seatClassPrice: " + e.getMessage());
        }

        requestStoragePermission();
        if (!validateSessionData()) {
            return;
        }

        bindingView();
        bindingAction();
        displayBookingSummary();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, STORAGE_PERMISSION_CODE);
            }
        }
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        etNotes = findViewById(R.id.et_notes);
        tvBookingSummary = findViewById(R.id.tv_booking_summary);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnBook = findViewById(R.id.btn_book);
        cbTerms = findViewById(R.id.cb_terms);
        progressBar = findViewById(R.id.progress_bar);
        btnBook.setEnabled(false);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnBook.setOnClickListener(this::onBtnBookClick);
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Enable/disable booking button based on terms checkbox
            btnBook.setEnabled(isChecked);
        });
    }

    private boolean validateSessionData() {
        flightId = getIntent().getIntExtra("flightId", -1);
        seatClassId = getIntent().getIntExtra("seatClassId", -1);
        passengerCount = getIntent().getIntExtra("passengerCount", 0);
        passengerDetails = (List<PassengerInfoDto>) getIntent().getSerializableExtra("passengerDetails");
        userId = sharedPreferences.getInt("user_id", -1);

        if (flightId == -1) {
            Toast.makeText(this, "Mã chuyến bay không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (userId <= 0) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (seatClassId == -1) {
            Toast.makeText(this, "Hạng ghế không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (passengerCount <= 0) {
            Toast.makeText(this, "Số lượng hành khách không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (passengerDetails == null || passengerDetails.size() != passengerCount) {
            Toast.makeText(this, "Dữ liệu hành khách không hợp lệ: " + (passengerDetails == null ? "null" : passengerDetails.size()), Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (seatClassPrice == null || seatClassPrice.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(this, "Giá vé không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        return true;
    }

    private void displayBookingSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Chi tiết đặt vé:\n\n");
        summary.append("Mã chuyến bay: ").append(flightId).append("\n");
        summary.append("Hạng ghế: ").append(getSeatClassName(seatClassId)).append("\n");
        summary.append("Số lượng hành khách: ").append(passengerCount).append("\n\n");

        for (int i = 0; i < passengerDetails.size(); i++) {
            PassengerInfoDto passenger = passengerDetails.get(i);
            summary.append("Hành khách ").append(i + 1).append(":\n");
            summary.append("   - Tên: ").append(passenger.getPassengerName()).append("\n");
            summary.append("   - CMND/CCCD: ").append(passenger.getPassengerIdNumber() != null ? passenger.getPassengerIdNumber() : "N/A").append("\n");
            if (i < passengerDetails.size() - 1) {
                summary.append("-----------------------------------------------------\n");
            }
        }

        tvBookingSummary.setText(summary.toString());

        BigDecimal totalPrice = calculateTotalPrice();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(currencyFormat.format(totalPrice));
    }

    private BigDecimal calculateTotalPrice() {
        return seatClassPrice.multiply(new BigDecimal(passengerCount));
    }

    private String getSeatClassName(int seatClassId) {
        switch (seatClassId) {
            case 1:
                return "Hạng Phổ Thông";
            case 2:
                return "Hạng Thương Gia";
            case 3:
                return "Hạng Nhất";
            default:
                return "Không xác định";
        }
    }

    private void onBtnBookClick(View view) {
        if (!validateBookingInput()) {
            return;
        }
        setBookingInProgress(true);
        CreateBookingDto bookingDto = createBookingData();
        Call<BookingResponseDto> call = bookingApi.createBooking(bookingDto);
        call.enqueue(new Callback<BookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingResponseDto> call, Response<BookingResponseDto> response) {
                setBookingInProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    handleBookingSuccess(response.body());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<BookingResponseDto> call, Throwable t) {
                setBookingInProgress(false);
                Toast.makeText(BookingFormActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateBookingInput() {
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản và điều kiện", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private CreateBookingDto createBookingData() {
        String notes = etNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            notes = "Không có yêu cầu đặc biệt";
        }
        return new CreateBookingDto(userId, flightId, seatClassId, passengerCount, passengerDetails, notes);
    }

    private void setBookingInProgress(boolean inProgress) {
        findViewById(R.id.progress_overlay).setVisibility(inProgress ? View.VISIBLE : View.GONE);
        btnBook.setEnabled(!inProgress);
        btnBook.setText(inProgress ? "Đang xử lý..." : "🎉 XÁC NHẬN & ĐẶT VÉ NGAY");
    }

    private void handleBookingSuccess(BookingResponseDto bookingResponse) {
        String bookingReference = bookingResponse.getBookingReference();
        int bookingId = bookingResponse.getBookingId();
        String successMessage = "Đặt vé thành công! Mã tham chiếu: " + bookingReference;

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
        sendBookingSuccessNotification(bookingReference, bookingId);
        
        // Navigate to PayActivity to complete payment
        navigateToPayment(bookingId);
    }

    private void handleErrorResponse(Response<BookingResponseDto> response) {
        String errorMessage = "Đặt vé thất bại";
        if (response.code() == 400) {
            errorMessage = "Thông tin đặt vé không hợp lệ";
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay hoặc hạng ghế";
        } else if (response.code() == 409) {
            errorMessage = "Không đủ ghế trống";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void sendBookingSuccessNotification(String bookingReference, int bookingId) {
        String channelId = "BookingChannelId";
        String channelName = "Thông báo đặt vé";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, BookingDetailActivity.class);
        intent.putExtra("bookingId", bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                bookingId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Đặt vé thành công")
                .setContentText("Mã đặt chỗ: " + bookingReference)
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Mã đặt chỗ: " + bookingReference))
                .build();

        notificationManager.notify(notificationId++, notification);
    }

    private void navigateToPayment(int bookingId) {
        // Navigate to PayActivity to complete payment
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("bookingId", bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để lưu và đọc file Excel", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}