/*
package com.prm.flightbooking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingResponseDto;
import com.prm.flightbooking.dto.booking.BookingSeatDto;
import com.prm.flightbooking.dto.booking.CreateBookingDto;
import com.prm.flightbooking.dto.seat.SelectedSeatInfo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
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
    private List<SelectedSeatInfo> selectedSeatsList;
    private int flightId, userId;
    private int notificationId = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        // Khởi tạo API và SharedPreferences
        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra dữ liệu hợp lệ
        if (!validateSessionData()) {
            return;
        }

        bindingView();
        bindingAction();
        displayBookingSummary();
    }

    // Liên kết các view trong layout
    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        etNotes = findViewById(R.id.et_notes);
        tvBookingSummary = findViewById(R.id.tv_booking_summary);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnBook = findViewById(R.id.btn_book);
        cbTerms = findViewById(R.id.cb_terms);
        progressBar = findViewById(R.id.progress_bar);
    }

    // Gán sự kiện cho các view
    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnBook.setOnClickListener(this::onBtnBookClick);
    }

    // Xử lý khi nhấn nút đặt vé
    private void onBtnBookClick(View view) {
        performBooking();
    }

    // Kiểm tra dữ liệu phiên làm việc
    private boolean validateSessionData() {
        selectedSeatsList = (List<SelectedSeatInfo>) getIntent().getSerializableExtra("selectedSeatsList");
        flightId = sharedPreferences.getInt("flightId", -1);
        userId = sharedPreferences.getInt("user_id", -1);

        if (flightId == -1 || userId <= 0 || selectedSeatsList == null || selectedSeatsList.isEmpty()) {
            Toast.makeText(this, "Dữ liệu không hợp lệ hoặc chưa chọn ghế", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    // Hiển thị tóm tắt thông tin đặt vé
    private void displayBookingSummary() {
        StringBuilder summary = new StringBuilder();
        BigDecimal overallTotalPrice = BigDecimal.ZERO;
        summary.append("Thông tin đặt vé:\n\n");

        // Hiển thị chi tiết từng ghế
        for (int i = 0; i < selectedSeatsList.size(); i++) {
            SelectedSeatInfo seat = selectedSeatsList.get(i);
            summary.append("🔵 Ghế số: ")
                    .append(seat.getSeatNumber())
                    .append(" (").append(seat.getSeatClassName()).append(")\n");
            summary.append("   - Hành khách: ").append(seat.getPassengerName()).append("\n");
            summary.append("   - CMND/CCCD: ").append(seat.getPassengerIdNumber()).append("\n");

            if (seat.getTotalPrice() != null) {
                String seatFormattedPrice = formatCurrency(seat.getTotalPrice());
                summary.append("   - Giá ghế: ").append(seatFormattedPrice).append("\n"); // Hiển thị giá từng ghế
                overallTotalPrice = overallTotalPrice.add(seat.getTotalPrice()); // Vẫn cộng vào tổng để hiển thị ở tvTotalPrice
            } else {
                summary.append("   - Giá ghế: N/A\n"); // Xử lý trường hợp giá không có
            }

            // Thêm dấu phân cách giữa các ghế nếu không phải ghế cuối cùng
            if (i < selectedSeatsList.size() - 1) {
                summary.append("-----------------------------------------------------\n");
            }
        }

        summary.append("\n📊 Tổng số ghế đã chọn: ")
                .append(selectedSeatsList.size()).append(" ghế");

        String overallFormattedPrice = formatCurrency(overallTotalPrice);

        tvBookingSummary.setText(summary.toString());
        tvTotalPrice.setText(overallFormattedPrice);
    }

    // Định dạng tiền tệ Việt Nam
    private String formatCurrency(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount) + " VNĐ";
    }

    // Thực hiện đặt vé
    private void performBooking() {
        if (!validateBookingInput()) {
            return;
        }

        // Hiển thị trạng thái đang xử lý
        setBookingInProgress(true);

        // Tạo dữ liệu đặt vé
        CreateBookingDto bookingDto = createBookingData();

        // Gọi API đặt vé
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

    // Kiểm tra dữ liệu đầu vào
    private boolean validateBookingInput() {
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản và điều kiện", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Tạo dữ liệu đặt vé
    private CreateBookingDto createBookingData() {
        String notes = etNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            notes = "Không có yêu cầu đặc biệt";
        }

        List<BookingSeatDto> seats = new ArrayList<>();
        for (SelectedSeatInfo info : selectedSeatsList) {
            BookingSeatDto seatDto = new BookingSeatDto(
                    info.getSeatId(),
                    info.getPassengerName(),
                    info.getPassengerIdNumber()
            );
            seats.add(seatDto);
        }

        return new CreateBookingDto(userId, flightId, seats, notes);
    }

    // Thiết lập trạng thái đang xử lý
    private void setBookingInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        btnBook.setEnabled(!inProgress);
        btnBook.setText(inProgress ? "Đang xử lý..." : "XÁC NHẬN ĐẶT VÉ");
    }

    // Xử lý khi đặt vé thành công
    private void handleBookingSuccess(BookingResponseDto bookingResponse) {
        String bookingReference = bookingResponse.getBookingReference();
        int bookingId = bookingResponse.getBookingId();
        String successMessage = "Đặt vé thành công! Mã tham chiếu: " + bookingReference;

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // Gửi thông báo
        sendBookingSuccessNotification(bookingReference, bookingId);

        // Chuyển về màn hình chính
        navigateToMainMenu();
    }

    // Xử lý lỗi từ server
    private void handleErrorResponse(Response<BookingResponseDto> response) {
        String errorMessage = "Đặt vé thất bại";

        if (response.code() == 400) {
            errorMessage = "Thông tin đặt vé không hợp lệ";
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay hoặc ghế";
        } else if (response.code() == 409) {
            errorMessage = "Ghế đã được đặt bởi người khác";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Gửi thông báo đặt vé thành công
    private void sendBookingSuccessNotification(String bookingReference, int bookingId) {
        String channelId = "BookingChannelId";
        String channelName = "Thông báo đặt vé";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Tạo kênh thông báo cho Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo intent khi nhấn vào thông báo
        Intent intent = new Intent(this, BookingDetailActivity.class);
        intent.putExtra("bookingId", bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                bookingId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        // Xây dựng thông báo
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Đặt vé thành công")
                .setContentText("Mã đặt chỗ: " + bookingReference)
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Mã đặt chỗ: " + bookingReference))
                .build();

        notificationManager.notify(notificationId++, notification);
    }

    // Chuyển về màn hình chính
    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}*//*
*/
/*


package com.prm.flightbooking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingResponseDto;
import com.prm.flightbooking.dto.booking.CreateBookingDto;
import com.prm.flightbooking.dto.booking.PassengerInfoDto;

import java.util.ArrayList;
import java.util.List;

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
    private int notificationId = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        if (!validateSessionData()) {
            return;
        }

        bindingView();
        bindingAction();
        displayBookingSummary();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        etNotes = findViewById(R.id.et_notes);
        tvBookingSummary = findViewById(R.id.tv_booking_summary);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnBook = findViewById(R.id.btn_book);
        cbTerms = findViewById(R.id.cb_terms);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnBook.setOnClickListener(this::onBtnBookClick);
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
        tvTotalPrice.setText("Tổng tiền: Được tính khi xác nhận đặt vé");
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
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        btnBook.setEnabled(!inProgress);
        btnBook.setText(inProgress ? "Đang xử lý..." : "🎉 XÁC NHẬN & ĐẶT VÉ NGAY");
    }

    private void handleBookingSuccess(BookingResponseDto bookingResponse) {
        String bookingReference = bookingResponse.getBookingReference();
        int bookingId = bookingResponse.getBookingId();
        String successMessage = "Đặt vé thành công! Mã tham chiếu: " + bookingReference;

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
        sendBookingSuccessNotification(bookingReference, bookingId);
        navigateToMainMenu();
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

    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}*//*



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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
    private int notificationId = 1000;
    private String transactionId;
    private boolean isPaymentVerified = false;
    private Handler paymentCheckHandler;
    private Runnable paymentCheckRunnable;
    private AlertDialog paymentDialog; // Lưu tham chiếu đến dialog
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final String TAG = "BookingFormActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        paymentCheckHandler = new Handler(Looper.getMainLooper());

        requestStoragePermission();
        if (!validateSessionData()) {
            return;
        }

        bindingView();
        bindingAction();
        displayBookingSummary();
        generateTransactionId();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
        btnBook.setEnabled(false); // Initially disabled until payment is verified
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnBook.setOnClickListener(this::onBtnBookClick);
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showPaymentQRCode();
            } else {
                stopPaymentCheck();
                // Chỉ vô hiệu hóa nút nếu thanh toán chưa được xác nhận
                if (!isPaymentVerified) {
                    btnBook.setEnabled(false);
                }
            }
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

        // Calculate and display total price
        long totalPrice = calculateTotalPrice();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText("Tổng tiền: " + currencyFormat.format(totalPrice));
    }

    private long calculateTotalPrice() {
        long pricePerPassenger;
        switch (seatClassId) {
            case 1: // Economy
                pricePerPassenger = 1_000_000;
                break;
            case 2: // Business
                pricePerPassenger = 2_000_000;
                break;
            case 3: // First
                pricePerPassenger = 3_000_000;
                break;
            default:
                pricePerPassenger = 1_000_000;
        }
        return pricePerPassenger * passengerCount;
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

    private void generateTransactionId() {
        transactionId = UUID.randomUUID().toString();
        Log.d(TAG, "Generated transactionId: " + transactionId);
    }

    private void showPaymentQRCode() {
        // Load QR code from drawable
        Bitmap qrCodeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_default);
        if (qrCodeBitmap == null) {
            Log.e(TAG, "Error loading QR code from drawable/qrcode_default.jpg");
            Toast.makeText(this, "Lỗi tải mã QR ngân hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total price for display
        long totalPrice = calculateTotalPrice();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(totalPrice);

        // Create dialog with QR code
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quét mã QR để thanh toán");
        ImageView qrCodeView = new ImageView(this);
        qrCodeView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Đảm bảo mã QR không bị cắt
        qrCodeView.setPadding(20, 20, 20, 20); // Thêm padding cho rõ nét
        qrCodeView.setImageBitmap(qrCodeBitmap);
        builder.setView(qrCodeView);
        builder.setMessage("Vui lòng quét mã QR để chuyển khoản ngân hàng.\n\n" +
                "Số tiền: " + formattedPrice + "\n" +
                "Nội dung chuyển khoản: " + transactionId + "\n\n" +
                "Sau khi chuyển khoản thành công, cập nhật file Excel (payments.xlsx) với TransactionID: " + transactionId + " và IsPaid = TRUE.");
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            stopPaymentCheck();
            dialog.dismiss();
        });
        paymentDialog = builder.create(); // Lưu dialog để đóng sau
        paymentDialog.show();

        // Start polling for payment status in background
        startPaymentCheck();
    }

    private void startPaymentCheck() {
        paymentCheckRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Checking payment for transactionId: " + transactionId);
                new Thread(() -> {
                    boolean isPaid = checkPaymentStatusFromExcel(transactionId);
                    runOnUiThread(() -> {
                        if (isPaid) {
                            isPaymentVerified = true;
                            btnBook.setEnabled(true);
                            Toast.makeText(BookingFormActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                            stopPaymentCheck();
                            if (paymentDialog != null && paymentDialog.isShowing()) {
                                paymentDialog.dismiss(); // Đóng dialog khi thanh toán thành công
                            }
                        } else {
                            // Continue polling
                            paymentCheckHandler.postDelayed(this, 5000); // Check every 5 seconds
                        }
                    });
                }).start();
            }
        };
        paymentCheckHandler.post(paymentCheckRunnable);
    }

    private boolean checkPaymentStatusFromExcel(String transactionId) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "payments.xlsx");
            Log.d(TAG, "Checking Excel file at: " + file.getAbsolutePath());
            if (!file.exists()) {
                Log.e(TAG, "Excel file not found at: " + file.getAbsolutePath());
                runOnUiThread(() -> Toast.makeText(this, "Không tìm thấy file payments.xlsx trong thư mục Download", Toast.LENGTH_SHORT).show());
                return false;
            }

            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            boolean isFirstRow = true; // Bỏ qua hàng tiêu đề
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Bỏ qua hàng đầu tiên (tiêu đề)
                }

                String id = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                Cell isPaidCell = row.getCell(1);
                boolean isPaid = false;

                if (isPaidCell != null) {
                    switch (isPaidCell.getCellType()) {
                        case BOOLEAN:
                            isPaid = isPaidCell.getBooleanCellValue();
                            break;
                        case STRING:
                            String cellValue = isPaidCell.getStringCellValue().trim().toLowerCase();
                            isPaid = cellValue.equals("true");
                            break;
                        default:
                            Log.w(TAG, "Unsupported cell type for IsPaid: " + isPaidCell.getCellType());
                            continue;
                    }
                }

                Log.d(TAG, "Row: TransactionID=" + id + ", IsPaid=" + isPaid);
                if (id.equals(transactionId) && isPaid) {
                    workbook.close();
                    fis.close();
                    return true;
                }
            }

            workbook.close();
            fis.close();
        } catch (Exception e) {
            Log.e(TAG, "Error reading Excel file: ", e);
            runOnUiThread(() -> Toast.makeText(this, "Lỗi đọc file Excel: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        return false;
    }

    private void stopPaymentCheck() {
        if (paymentCheckRunnable != null) {
            paymentCheckHandler.removeCallbacks(paymentCheckRunnable);
        }
    }

    private void onBtnBookClick(View view) {
        if (!validateBookingInput()) {
            return;
        }
        if (!isPaymentVerified) {
            Toast.makeText(this, "Vui lòng hoàn tất thanh toán trước khi đặt vé", Toast.LENGTH_SHORT).show();
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
        navigateToMainMenu();
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

    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPaymentCheck();
        if (paymentDialog != null && paymentDialog.isShowing()) {
            paymentDialog.dismiss(); // Đóng dialog khi activity tạm dừng
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPaymentCheck();
        if (paymentDialog != null && paymentDialog.isShowing()) {
            paymentDialog.dismiss(); // Đóng dialog khi activity bị hủy
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để đọc file Excel", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}*/

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.UUID;

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
    private BigDecimal seatClassPrice; // Price per ticket (basePrice × multiplier)
    private int notificationId = 1000;
    private String transactionId;
    private AlertDialog paymentDialog;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final String TAG = "BookingFormActivity";
    private static final String BANK_ACCOUNT_NUMBER = "555508122003";

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
        generateTransactionId();
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
            if (isChecked) {
                showPaymentQRCode();
            } else {
                btnBook.setEnabled(false);
            }
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

    private void generateTransactionId() {
        transactionId = UUID.randomUUID().toString();
        Log.d(TAG, "Generated transactionId: " + transactionId);
    }

    private void showPaymentQRCode() {
        // Load QR code from drawable
        Bitmap qrCodeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_default);
        if (qrCodeBitmap == null) {
            Log.e(TAG, "Error loading QR code from drawable/qrcode_default.jpg");
            Toast.makeText(this, "Lỗi tải mã QR ngân hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total price for display
        BigDecimal totalPrice = calculateTotalPrice();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(totalPrice);

        // Create dialog with QR code
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quét mã QR để thanh toán");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        ImageView qrCodeView = new ImageView(this);
        qrCodeView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        qrCodeView.setImageBitmap(qrCodeBitmap);
        layout.addView(qrCodeView);

        TextView instructions = new TextView(this);
        instructions.setText("Vui lòng quét mã QR để chuyển khoản vào số tài khoản:\n\n" +
                "Số tài khoản: " + BANK_ACCOUNT_NUMBER + "\n" +
                "Số tiền: " + formattedPrice + "\n" +
                "Nội dung chuyển khoản: " + transactionId + "\n\n" +
                "Sau khi chuyển khoản, nhấn 'Xác nhận' để nhập mã OTP.");
        instructions.setPadding(0, 20, 0, 20);
        layout.addView(instructions);

        builder.setView(layout);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> showOtpDialog());
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            btnBook.setEnabled(false);
            dialog.dismiss();
        });

        paymentDialog = builder.create();
        paymentDialog.show();
    }

    private void showOtpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mã OTP");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        EditText etOtp = new EditText(this);
        etOtp.setHint("Nhập mã OTP (mặc định: 111)");
        etOtp.setPadding(10, 10, 10, 10);
        layout.addView(etOtp);

        builder.setView(layout);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String otp = etOtp.getText().toString().trim();
            if ("111".equals(otp)) {
                btnBook.setEnabled(true);
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                if (paymentDialog != null && paymentDialog.isShowing()) {
                    paymentDialog.dismiss();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Mã OTP không đúng. Vui lòng nhập 111.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog otpDialog = builder.create();
        otpDialog.show();
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
        navigateToMainMenu();
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

    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (paymentDialog != null && paymentDialog.isShowing()) {
            paymentDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (paymentDialog != null && paymentDialog.isShowing()) {
            paymentDialog.dismiss();
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