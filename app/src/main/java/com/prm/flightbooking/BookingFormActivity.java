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
import com.prm.flightbooking.api.PaymentApiEndpoint;
import com.prm.flightbooking.api.ServiceApiEndpoint;
import com.prm.flightbooking.dto.service.AddServiceToBookingDto;
import com.prm.flightbooking.dto.booking.BookingResponseDto;
import com.prm.flightbooking.dto.booking.CreateBookingDto;
import com.prm.flightbooking.dto.booking.PassengerInfoDto;
import com.prm.flightbooking.dto.payment.CreatePaymentDto;
import com.prm.flightbooking.dto.payment.PaymentResponseDto;
import com.prm.flightbooking.dialogs.PaymentMethodSelectionDialog;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

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
    private PaymentApiEndpoint paymentApi;
    private ServiceApiEndpoint serviceApi;
    private SharedPreferences sharedPreferences;
    private int flightId, userId, seatClassId, passengerCount;
    private List<PassengerInfoDto> passengerDetails;
    private BigDecimal seatClassPrice;
    private BigDecimal totalServicesPrice = BigDecimal.ZERO;
    private int[] selectedMeals, selectedLuggages, selectedInsurances;
    private int notificationId = 1000;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int REQ_VNPAY = 1001;
    private static final String TAG = "BookingFormActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        bookingApi = ApiServiceProvider.getBookingApi();
        paymentApi = ApiServiceProvider.getPaymentApi();
        serviceApi = ApiServiceProvider.getServiceApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Retrieve seatClassPrice from Intent
        try {
            seatClassPrice = (BigDecimal) getIntent().getSerializableExtra("seatClassPrice");
        } catch (Exception e) {
            seatClassPrice = null;
            Log.e(TAG, "Error retrieving seatClassPrice: " + e.getMessage());
        }
        
        // Retrieve services data from Intent
        try {
            totalServicesPrice = (BigDecimal) getIntent().getSerializableExtra("totalServicesPrice");
            if (totalServicesPrice == null) totalServicesPrice = BigDecimal.ZERO;
            selectedMeals = getIntent().getIntArrayExtra("selectedMeals");
            selectedLuggages = getIntent().getIntArrayExtra("selectedLuggages");
            selectedInsurances = getIntent().getIntArrayExtra("selectedInsurances");
        } catch (Exception e) {
            totalServicesPrice = BigDecimal.ZERO;
            Log.e(TAG, "Error retrieving services data: " + e.getMessage());
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
        
        // Add services summary
        if (totalServicesPrice.compareTo(BigDecimal.ZERO) > 0) {
            summary.append("\n-----------------------------------------------------\n");
            summary.append("Dịch vụ đã chọn:\n");
            if (selectedMeals != null && selectedMeals.length > 0) {
                summary.append("   - Bữa ăn: ").append(selectedMeals.length / 2).append(" món\n");
            }
            if (selectedLuggages != null && selectedLuggages.length > 0) {
                summary.append("   - Hành lý: ").append(selectedLuggages.length / 2).append(" loại\n");
            }
            if (selectedInsurances != null && selectedInsurances.length > 0) {
                summary.append("   - Bảo hiểm: ").append(selectedInsurances.length / 2).append(" gói\n");
            }
            summary.append("   - Tổng dịch vụ: ").append(formatCurrency(totalServicesPrice)).append("\n");
        }

        tvBookingSummary.setText(summary.toString());

        BigDecimal totalPrice = calculateTotalPrice();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(currencyFormat.format(totalPrice));
    }
    
    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount) + " VND";
    }

    private BigDecimal calculateTotalPrice() {
        BigDecimal seatTotal = seatClassPrice.multiply(new BigDecimal(passengerCount));
        return seatTotal.add(totalServicesPrice);
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
        
        // Hiện dialog chọn phương thức thanh toán TRƯỚC
        PaymentMethodSelectionDialog.show(this, new PaymentMethodSelectionDialog.PaymentMethodListener() {
            @Override
            public void onPaymentMethodSelected(String paymentMethod) {
                // User đã chọn phương thức thanh toán → Tạo booking rồi tạo payment
                proceedWithBookingAndPayment(paymentMethod);
            }

            @Override
            public void onQRCodePayment() {
                // QR Code payment - tạo booking nhưng không tạo payment online
                proceedWithBookingOnly();
            }
        });
    }
    
    private void proceedWithBookingAndPayment(String paymentMethod) {
        setBookingInProgress(true);
        CreateBookingDto bookingDto = createBookingData();
        Call<BookingResponseDto> call = bookingApi.createBooking(bookingDto);
        call.enqueue(new Callback<BookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingResponseDto> call, Response<BookingResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Booking thành công → Tạo payment và mở thanh toán
                    handleBookingSuccessForPayment(response.body(), paymentMethod);
                } else {
                    setBookingInProgress(false);
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
    
    private void proceedWithBookingOnly() {
        setBookingInProgress(true);
        CreateBookingDto bookingDto = createBookingData();
        Call<BookingResponseDto> call = bookingApi.createBooking(bookingDto);
        call.enqueue(new Callback<BookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingResponseDto> call, Response<BookingResponseDto> response) {
                setBookingInProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Booking thành công nhưng QR Code → Chỉ hiện thông báo
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
        
        // Add services to booking
        addServicesToBooking(bookingId, () -> navigateToPayment(bookingId));
    }
    
    private void handleBookingSuccessForPayment(BookingResponseDto bookingResponse, String paymentMethod) {
        String bookingReference = bookingResponse.getBookingReference();
        int bookingId = bookingResponse.getBookingId();
        String successMessage = "Đặt vé thành công! Mã tham chiếu: " + bookingReference;

        Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
        sendBookingSuccessNotification(bookingReference, bookingId);
        
        // Add services to booking
        addServicesToBooking(bookingId, () -> createPaymentAndOpenWebView(bookingId, paymentMethod));
    }
    
    private void addServicesToBooking(int bookingId, Runnable onComplete) {
        List<AddServiceToBookingDto> pendingRequests = new ArrayList<>();

        // Prepare meal services
        if (selectedMeals != null && selectedMeals.length > 0) {
            for (int i = 0; i < selectedMeals.length; i += 2) {
                int mealId = selectedMeals[i];
                int quantity = selectedMeals[i + 1];
                AddServiceToBookingDto dto = new AddServiceToBookingDto();
                dto.setBookingId(bookingId);
                dto.setServiceType("MEAL");
                dto.setMealId(mealId);
                dto.setQuantity(quantity);
                pendingRequests.add(dto);
            }
        }

        // Prepare luggage services
        if (selectedLuggages != null && selectedLuggages.length > 0) {
            for (int i = 0; i < selectedLuggages.length; i += 2) {
                int luggageId = selectedLuggages[i];
                int quantity = selectedLuggages[i + 1];
                AddServiceToBookingDto dto = new AddServiceToBookingDto();
                dto.setBookingId(bookingId);
                dto.setServiceType("LUGGAGE");
                dto.setLuggageId(luggageId);
                dto.setQuantity(quantity);
                pendingRequests.add(dto);
            }
        }

        // Prepare insurance services
        if (selectedInsurances != null && selectedInsurances.length > 0) {
            for (int i = 0; i < selectedInsurances.length; i += 2) {
                int insuranceId = selectedInsurances[i];
                int quantity = selectedInsurances[i + 1];
                AddServiceToBookingDto dto = new AddServiceToBookingDto();
                dto.setBookingId(bookingId);
                dto.setServiceType("INSURANCE");
                dto.setInsuranceId(insuranceId);
                dto.setQuantity(quantity);
                pendingRequests.add(dto);
            }
        }

        if (pendingRequests.isEmpty()) {
            if (onComplete != null) {
                runOnUiThread(onComplete);
            }
            return;
        }

        AtomicInteger remaining = new AtomicInteger(pendingRequests.size());

        Callback<com.prm.flightbooking.dto.service.BookingServiceDto> callback =
                new Callback<com.prm.flightbooking.dto.service.BookingServiceDto>() {
                    private void markComplete() {
                        if (remaining.decrementAndGet() == 0 && onComplete != null) {
                            runOnUiThread(onComplete);
                        }
                    }

                    @Override
                    public void onResponse(Call<com.prm.flightbooking.dto.service.BookingServiceDto> call,
                                           Response<com.prm.flightbooking.dto.service.BookingServiceDto> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "Failed to add service. Code: " + response.code());
                        }
                        markComplete();
                    }

                    @Override
                    public void onFailure(Call<com.prm.flightbooking.dto.service.BookingServiceDto> call, Throwable t) {
                        Log.e(TAG, "Error adding service: " + t.getMessage());
                        markComplete();
            }
                };

        for (AddServiceToBookingDto request : pendingRequests) {
            serviceApi.addServiceToBooking(request).enqueue(callback);
        }
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
        // Navigate to PayActivity to complete payment (dùng cho QR Code hoặc thanh toán lại)
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("bookingId", bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void createPaymentAndOpenWebView(int bookingId, String paymentMethod) {
        CreatePaymentDto paymentDto = new CreatePaymentDto();
        paymentDto.setBookingId(bookingId);
        paymentDto.setPaymentMethod(paymentMethod);
        paymentDto.setReturnUrl("flightbooking://payment/return");
        paymentDto.setCancelUrl("flightbooking://payment/cancel");
        
        // VNPay: KHÔNG set BankCode - để VNPay sandbox hiển thị TẤT CẢ phương thức thanh toán
        if (!"VNPAY".equalsIgnoreCase(paymentMethod)) {
            paymentDto.setBankCode(null);
        }
        
        Log.d(TAG, "Creating payment - BookingId: " + bookingId + ", Method: " + paymentMethod);
        
        Call<PaymentResponseDto> call = paymentApi.createPayment(paymentDto);
        call.enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                setBookingInProgress(false);
                
                Log.d(TAG, "Payment API Response - Code: " + response.code() + ", Success: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponseDto paymentResponse = response.body();
                    
                    Log.d(TAG, "Payment Response Details:");
                    Log.d(TAG, "  - BookingId: " + paymentResponse.getBookingId());
                    Log.d(TAG, "  - PaymentId: " + paymentResponse.getPaymentId());
                    Log.d(TAG, "  - TransactionId: " + paymentResponse.getTransactionId());
                    Log.d(TAG, "  - PaymentMethod: " + paymentResponse.getPaymentMethod());
                    Log.d(TAG, "  - PaymentUrl: " + paymentResponse.getPaymentUrl());
                    Log.d(TAG, "  - Status: " + paymentResponse.getStatus());
                    Log.d(TAG, "  - Amount: " + paymentResponse.getAmount());
                    
                    String paymentUrl = paymentResponse.getPaymentUrl();
                    
                    if (paymentUrl != null && !paymentUrl.trim().isEmpty()) {
                        Log.d(TAG, "Payment URL received successfully: " + paymentUrl);
                        openPaymentWebView(paymentUrl, bookingId);
                    } else {
                        Log.e(TAG, "Payment URL is null or empty!");
                        Toast.makeText(BookingFormActivity.this, "Không nhận được URL thanh toán từ server. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                        // Fallback: Navigate to PayActivity
                        navigateToPayment(bookingId);
                    }
                } else {
                    String errorMsg = "Không thể tạo thanh toán";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Payment API Error - Code: " + response.code());
                            Log.e(TAG, "Error Body: " + errorBody);
                            errorMsg += ": " + errorBody;
                        } else {
                            Log.e(TAG, "Payment API Error - Code: " + response.code() + ", Message: " + response.message());
                            errorMsg += ": " + response.message() + " (Code: " + response.code() + ")";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                        errorMsg += ": " + response.message() + " (Code: " + response.code() + ")";
                    }
                    
                    Toast.makeText(BookingFormActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    // Fallback: Navigate to PayActivity
                    navigateToPayment(bookingId);
                }
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                setBookingInProgress(false);
                Log.e(TAG, "Payment API call failed", t);
                Toast.makeText(BookingFormActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Fallback: Navigate to PayActivity
                navigateToPayment(bookingId);
            }
        });
    }
    
    private void openPaymentWebView(String paymentUrl, int bookingId) {
        Intent intent = new Intent(this, com.prm.flightbooking.webview.WebViewPaymentActivity.class);
        intent.putExtra(com.prm.flightbooking.webview.WebViewPaymentActivity.EXTRA_URL, paymentUrl);
        intent.putExtra("bookingId", bookingId); // Pass bookingId để xử lý result
        startActivityForResult(intent, REQ_VNPAY);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_VNPAY && resultCode == RESULT_OK) {
            if (data != null) {
                String status = data.getStringExtra("status");
                String message = data.getStringExtra("message");
                String transactionId = data.getStringExtra("transactionId");
                String responseCode = data.getStringExtra("responseCode");
                int bookingId = data.getIntExtra("bookingId", -1);
                
                Log.d(TAG, "Payment result - Status: " + status + ", Message: " + message);
                
                if ("success".equalsIgnoreCase(status)) {
                    // Navigate to success result page
                    Intent resultIntent = new Intent(this, PaymentResultActivity.class);
                    resultIntent.putExtra(PaymentResultActivity.EXTRA_STATUS, "success");
                    resultIntent.putExtra(PaymentResultActivity.EXTRA_MESSAGE, message != null ? message : "Thanh toán VNPay thành công");
                    resultIntent.putExtra(PaymentResultActivity.EXTRA_BOOKING_ID, bookingId);
                    if (transactionId != null) {
                        resultIntent.putExtra("transactionId", transactionId);
                    }
                    startActivity(resultIntent);
                    finish();
                } else {
                    // Navigate to failed result page
                    Intent resultIntent = new Intent(this, PaymentResultActivity.class);
                    resultIntent.putExtra(PaymentResultActivity.EXTRA_STATUS, "failed");
                    resultIntent.putExtra(PaymentResultActivity.EXTRA_MESSAGE, message != null ? message : "Thanh toán VNPay thất bại");
                    resultIntent.putExtra(PaymentResultActivity.EXTRA_BOOKING_ID, bookingId);
                    if (transactionId != null) {
                        resultIntent.putExtra("transactionId", transactionId);
                    }
                    if (responseCode != null) {
                        resultIntent.putExtra("responseCode", responseCode);
                    }
                    startActivity(resultIntent);
                    finish();
                }
            } else {
                // Không có data, có thể user đã đóng WebView
                Toast.makeText(this, "Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQ_VNPAY && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
        }
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