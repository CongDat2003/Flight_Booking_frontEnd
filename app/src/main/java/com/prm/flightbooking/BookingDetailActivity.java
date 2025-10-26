package com.prm.flightbooking;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingDetailDto;
import com.prm.flightbooking.dto.booking.FlightDetailDto;
import com.prm.flightbooking.dto.booking.PassengerSeatDto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {

    // Khai báo các view components
    private TextView tvBookingReference, tvStatus, tvPaymentStatus, tvPrice, tvBookingDate;
    private TextView tvFlightNumber, tvAirline, tvAircraftModel, tvDepartureAirport, tvArrivalAirport;
    private TextView tvDepartureTime, tvArrivalTime, tvGate, tvNotes;
    private LinearLayout passengerContainer, seatSummaryContainer;
    private ProgressBar progressBar;
    private Button btnCancelBooking;
    private ImageButton btnBack, btnDownload;

    // API service và dữ liệu
    private BookingApiEndpoint bookingApi;
    private SharedPreferences sharedPreferences;
    private int userId;
    private int bookingId;
    private BookingDetailDto currentBookingDetail; // Lưu booking detail để export
    
    // Permission constants
    private static final int PERMISSION_REQUEST_CODE = 1001;

    // Format hiển thị
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, dd 'Th'MM 'năm' yyyy, 'lúc' HH:mm", new Locale("vi", "VN"));
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // Khởi tạo API service và SharedPreferences
        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (!checkLoginStatus()) {
            redirectToLogin();
            return;
        }

        // Lấy booking ID từ intent
        bookingId = getIntent().getIntExtra("bookingId", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Không tìm thấy mã đặt vé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindingView();
        bindingAction();
        fetchBookingDetail();
    }

    // Liên kết các view từ layout
    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        tvBookingReference = findViewById(R.id.tv_booking_reference);
        tvStatus = findViewById(R.id.tv_status);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tvPrice = findViewById(R.id.tv_price);
        tvBookingDate = findViewById(R.id.tv_booking_date);
        tvFlightNumber = findViewById(R.id.tv_flight_number);
        tvAirline = findViewById(R.id.tv_airline);
        tvAircraftModel = findViewById(R.id.tv_aircraft_model);
        tvDepartureAirport = findViewById(R.id.tv_departure_airport);
        tvArrivalAirport = findViewById(R.id.tv_arrival_airport);
        tvDepartureTime = findViewById(R.id.tv_departure_time);
        tvArrivalTime = findViewById(R.id.tv_arrival_time);
        tvGate = findViewById(R.id.tv_gate);
        tvNotes = findViewById(R.id.tv_notes);
        passengerContainer = findViewById(R.id.passenger_container);
        seatSummaryContainer = findViewById(R.id.seat_summary_container);
        progressBar = findViewById(R.id.progress_bar);
        btnCancelBooking = findViewById(R.id.btn_cancel_booking);
        btnDownload = findViewById(R.id.btn_download);
    }

    // Liên kết các sự kiện click
    private void bindingAction() {
        btnBack.setOnClickListener(this::onBackClick);
        btnCancelBooking.setOnClickListener(this::onCancelBookingClick);
        btnDownload.setOnClickListener(this::onDownloadTicketClick);

        // Ẩn nút hủy vé mặc định
        btnCancelBooking.setVisibility(View.GONE);
    }

    // Xử lý sự kiện click nút quay lại
    private void onBackClick(View view) {
        finish();
    }

    // Xử lý sự kiện click nút hủy vé
    private void onCancelBookingClick(View view) {
        showCancelConfirmationDialog();
    }

    private void onDownloadTicketClick(View view) {
        if (currentBookingDetail == null) {
            Toast.makeText(this, "Không có thông tin để xuất file", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Android 10+ không cần quyền cho app-specific directory
        // Xuất trực tiếp vào thư mục app
        exportToExcel();
    }
    
    // Xuất dữ liệu ra file Excel
    private void exportToExcel() {
        try {
            // Tạo workbook mới
            Workbook workbook = new XSSFWorkbook();
            
            // Tạo sheet
            Sheet sheet = workbook.createSheet("Chi tiết đặt vé");
            
            // Tạo các row
            int rowNum = 0;
            
            // Header - Thông tin đặt vé
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("CHI TIẾT ĐẶT VÉ MÁY BAY");
            
            // Thông tin booking
            rowNum++; // Dòng trống
            Row bookingRefRow = sheet.createRow(rowNum++);
            bookingRefRow.createCell(0).setCellValue("Mã đặt vé:");
            bookingRefRow.createCell(1).setCellValue(currentBookingDetail.getBookingReference());
            
            Row statusRow = sheet.createRow(rowNum++);
            statusRow.createCell(0).setCellValue("Trạng thái:");
            statusRow.createCell(1).setCellValue(formatBookingStatus(currentBookingDetail.getBookingStatus()));
            
            Row paymentRow = sheet.createRow(rowNum++);
            paymentRow.createCell(0).setCellValue("Trạng thái thanh toán:");
            paymentRow.createCell(1).setCellValue(formatPaymentStatus(currentBookingDetail.getPaymentStatus()));
            
            Row priceRow = sheet.createRow(rowNum++);
            priceRow.createCell(0).setCellValue("Tổng tiền:");
            BigDecimal totalAmount = currentBookingDetail.getTotalAmount();
            priceRow.createCell(1).setCellValue(totalAmount != null ? currencyFormat.format(totalAmount) + " VNĐ" : "N/A");
            
            if (currentBookingDetail.getBookingDate() != null) {
                Row dateRow = sheet.createRow(rowNum++);
                dateRow.createCell(0).setCellValue("Ngày đặt vé:");
                dateRow.createCell(1).setCellValue(dateTimeFormat.format(currentBookingDetail.getBookingDate()));
            }
            
            // Thông tin chuyến bay
            rowNum++; // Dòng trống
            Row flightHeader = sheet.createRow(rowNum++);
            flightHeader.createCell(0).setCellValue("THÔNG TIN CHUYẾN BAY");
            
            if (currentBookingDetail.getFlight() != null) {
                FlightDetailDto flight = currentBookingDetail.getFlight();
                
                Row flightNumberRow = sheet.createRow(rowNum++);
                flightNumberRow.createCell(0).setCellValue("Số chuyến bay:");
                flightNumberRow.createCell(1).setCellValue(flight.getFlightNumber());
                
                Row airlineRow = sheet.createRow(rowNum++);
                airlineRow.createCell(0).setCellValue("Hãng bay:");
                airlineRow.createCell(1).setCellValue(flight.getAirlineName() != null ? flight.getAirlineName() : "N/A");
                
                Row aircraftRow = sheet.createRow(rowNum++);
                aircraftRow.createCell(0).setCellValue("Loại máy bay:");
                aircraftRow.createCell(1).setCellValue(flight.getAircraftModel() != null ? flight.getAircraftModel() : "N/A");
                
                Row departureRow = sheet.createRow(rowNum++);
                departureRow.createCell(0).setCellValue("Sân bay đi:");
                departureRow.createCell(1).setCellValue(flight.getDepartureAirport());
                
                if (flight.getDepartureTime() != null) {
                    Row departureTimeRow = sheet.createRow(rowNum++);
                    departureTimeRow.createCell(0).setCellValue("Thời gian đi:");
                    departureTimeRow.createCell(1).setCellValue(dateTimeFormat.format(flight.getDepartureTime()));
                }
                
                Row arrivalRow = sheet.createRow(rowNum++);
                arrivalRow.createCell(0).setCellValue("Sân bay đến:");
                arrivalRow.createCell(1).setCellValue(flight.getArrivalAirport());
                
                if (flight.getArrivalTime() != null) {
                    Row arrivalTimeRow = sheet.createRow(rowNum++);
                    arrivalTimeRow.createCell(0).setCellValue("Thời gian đến:");
                    arrivalTimeRow.createCell(1).setCellValue(dateTimeFormat.format(flight.getArrivalTime()));
                }
                
                if (flight.getGate() != null && !flight.getGate().isEmpty()) {
                    Row gateRow = sheet.createRow(rowNum++);
                    gateRow.createCell(0).setCellValue("Cổng:");
                    gateRow.createCell(1).setCellValue(flight.getGate());
                }
            }
            
            // Thông tin hành khách
            rowNum++; // Dòng trống
            Row passengerHeader = sheet.createRow(rowNum++);
            passengerHeader.createCell(0).setCellValue("THÔNG TIN HÀNH KHÁCH");
            
            if (currentBookingDetail.getPassengers() != null && !currentBookingDetail.getPassengers().isEmpty()) {
                // Header row cho bảng hành khách
                Row tableHeader = sheet.createRow(rowNum++);
                tableHeader.createCell(0).setCellValue("Họ tên");
                tableHeader.createCell(1).setCellValue("Ghế");
                tableHeader.createCell(2).setCellValue("Hạng ghế");
                tableHeader.createCell(3).setCellValue("Loại ghế");
                tableHeader.createCell(4).setCellValue("Giá vé");
                
                // Dữ liệu hành khách
                for (PassengerSeatDto passenger : currentBookingDetail.getPassengers()) {
                    Row passengerRow = sheet.createRow(rowNum++);
                    passengerRow.createCell(0).setCellValue(passenger.getPassengerName());
                    passengerRow.createCell(1).setCellValue(passenger.getSeatNumber());
                    passengerRow.createCell(2).setCellValue(passenger.getSeatClass());
                    passengerRow.createCell(3).setCellValue(formatSeatType(passenger));
                    
                    BigDecimal seatPrice = passenger.getSeatPrice();
                    passengerRow.createCell(4).setCellValue(seatPrice != null ? currencyFormat.format(seatPrice) + " VNĐ" : "N/A");
                }
            }
            
            // Ghi chú
            if (currentBookingDetail.getNotes() != null && !currentBookingDetail.getNotes().isEmpty()) {
                rowNum++; // Dòng trống
                Row notesRow = sheet.createRow(rowNum++);
                notesRow.createCell(0).setCellValue("Ghi chú:");
                notesRow.createCell(1).setCellValue(currentBookingDetail.getNotes());
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Lưu file vào thư mục Downloads công khai
            String fileName = "Booking_" + currentBookingDetail.getBookingReference() + ".xlsx";
            File downloadsDir;
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Android 10+ - dùng app-specific Downloads folder
                downloadsDir = new File(getExternalFilesDir(null), "Downloads");
            } else {
                // Android < 10 - dùng Downloads công khai
                downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            }
            
            downloadsDir.mkdirs();
            File file = new File(downloadsDir, fileName);
            
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();
            
            // Hiển thị thông báo thành công và vị trí file
            Toast.makeText(this, 
                "✅ Đã lưu file Excel!\n" + "Vị trí: Downloads/" + fileName + 
                "\n(Mở bằng File Manager để xem)", 
                Toast.LENGTH_LONG).show();
            
        } catch (IOException e) {
            Log.e("BookingDetailActivity", "Lỗi xuất file Excel: " + e.getMessage());
            Toast.makeText(this, "Lỗi xuất file Excel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean checkLoginStatus() {
        userId = sharedPreferences.getInt("user_id", -1);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (userId <= 0 || !isLoggedIn) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem chi tiết đặt vé", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Chuyển hướng về màn hình đăng nhập
    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Gọi API lấy chi tiết đặt vé
    private void fetchBookingDetail() {
        progressBar.setVisibility(View.VISIBLE);

        Call<BookingDetailDto> call = bookingApi.getBookingDetail(userId, bookingId);
        Log.d("BookingDetailActivity", "Đang tải chi tiết đặt vé với ID: " + bookingId);

        call.enqueue(new Callback<BookingDetailDto>() {
            @Override
            public void onResponse(Call<BookingDetailDto> call, Response<BookingDetailDto> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    BookingDetailDto bookingDetail = response.body();
                    Log.d("BookingDetailActivity", "Tải chi tiết đặt vé thành công - " + bookingDetail.toString());
                    updateBookingDetailUI(bookingDetail);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<BookingDetailDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật giao diện với thông tin chi tiết đặt vé
    private void updateBookingDetailUI(BookingDetailDto bookingDetail) {
        // Lưu booking detail để export
        currentBookingDetail = bookingDetail;
        
        // Hiển thị thông tin đặt vé cơ bản
        displayBookingInfo(bookingDetail);

        // Hiển thị thông tin chuyến bay
        displayFlightInfo(bookingDetail.getFlight());

        // Hiển thị danh sách hành khách
        displayPassengerInfo(bookingDetail);

        // Hiển thị tóm tắt ghế
        displaySeatSummary(bookingDetail);

        // Hiển thị nút hủy vé nếu có thể hủy
        updateCancelButton(bookingDetail.getBookingStatus());
    }

    // Hiển thị thông tin đặt vé cơ bản
    private void displayBookingInfo(BookingDetailDto bookingDetail) {
        tvBookingReference.setText("Mã đặt vé: " + bookingDetail.getBookingReference());
        tvStatus.setText(formatBookingStatus(bookingDetail.getBookingStatus()));
        tvPaymentStatus.setText(formatPaymentStatus(bookingDetail.getPaymentStatus()));

        // Hiển thị giá tiền
        BigDecimal totalAmount = bookingDetail.getTotalAmount();
        if (totalAmount != null) {
            tvPrice.setText(currencyFormat.format(totalAmount) + " VNĐ");
        } else {
            tvPrice.setText("Chưa có thông tin giá");
        }

        // Hiển thị ngày đặt vé
        if (bookingDetail.getBookingDate() != null) {
            tvBookingDate.setText(dateTimeFormat.format(bookingDetail.getBookingDate()));
        } else {
            tvBookingDate.setText("Chưa có thông tin ngày");
        }

        // Hiển thị ghi chú
        String notes = bookingDetail.getNotes();
        tvNotes.setText(notes != null && !notes.isEmpty() ? notes : "Không có ghi chú");
    }

    // Hiển thị thông tin chuyến bay
    private void displayFlightInfo(FlightDetailDto flight) {
        if (flight == null) {
            tvFlightNumber.setText("Không có thông tin chuyến bay");
            return;
        }

        String departureAirport = flight.getDepartureAirport();
        TextView tvDepartureAirportName = findViewById(R.id.tv_departure_airport_name);
        String arrivalAirport = flight.getArrivalAirport();
        TextView tvArrivalAirportName = findViewById(R.id.tv_arrival_airport_name);

        tvFlightNumber.setText(flight.getFlightNumber());
        tvAirline.setText("Hãng bay: " + (flight.getAirlineName() != null ? flight.getAirlineName() : "Chưa có thông tin"));
        tvAircraftModel.setText("Loại máy bay: " + (flight.getAircraftModel() != null ? flight.getAircraftModel() : "Chưa có thông tin"));
        tvDepartureAirport.setText(getAirportCode(departureAirport));
        tvDepartureAirportName.setText(getAirportName(departureAirport));
        tvArrivalAirport.setText(getAirportCode(arrivalAirport));
        tvArrivalAirportName.setText(getAirportName(arrivalAirport));

        // Hiển thị thời gian khởi hành và đến
        if (flight.getDepartureTime() != null) {
            tvDepartureTime.setText(formatTime(flight.getDepartureTime()));
            TextView tvDepartureDate = findViewById(R.id.tv_departure_date);
            tvDepartureDate.setText(formatDate(flight.getDepartureTime()));
        } else {
            tvDepartureTime.setText("Chưa có thông tin");
            TextView tvDepartureDate = findViewById(R.id.tv_departure_date);
            tvDepartureDate.setText("");
        }

        if (flight.getArrivalTime() != null) {
            tvArrivalTime.setText(formatTime(flight.getArrivalTime()));
            TextView tvArrivalDate = findViewById(R.id.tv_arrival_date);
            tvArrivalDate.setText(formatDate(flight.getArrivalTime()));
        } else {
            tvArrivalTime.setText("Chưa có thông tin");
            TextView tvArrivalDate = findViewById(R.id.tv_arrival_date);
            tvArrivalDate.setText("");
        }

        // Hiển thị cổng
        String gate = flight.getGate();
        tvGate.setText(gate != null && !gate.isEmpty() ? "Cổng: " + gate : "Chưa có thông tin cổng");
    }

    // Hiển thị thông tin hành khách
    private void displayPassengerInfo(BookingDetailDto bookingDetail) {
        passengerContainer.removeAllViews();

        if (bookingDetail.getPassengers() == null || bookingDetail.getPassengers().isEmpty()) {
            TextView noPassenger = new TextView(this);
            noPassenger.setText("Không có thông tin hành khách");
            noPassenger.setTextColor(getResources().getColor(android.R.color.darker_gray));
            passengerContainer.addView(noPassenger);
            return;
        }

        for (PassengerSeatDto passenger : bookingDetail.getPassengers()) {
            View passengerView = getLayoutInflater().inflate(R.layout.item_passenger_detail, passengerContainer, false);

            TextView tvPassengerName = passengerView.findViewById(R.id.tv_passenger_name);
            TextView tvSeatNumber = passengerView.findViewById(R.id.tv_seat_number);
            TextView tvSeatClass = passengerView.findViewById(R.id.tv_seat_class);
            TextView tvSeatPrice = passengerView.findViewById(R.id.tv_seat_price);
            TextView tvSeatType = passengerView.findViewById(R.id.tv_seat_type);

            // Cập nhật thông tin hành khách
            tvPassengerName.setText(passenger.getPassengerName());
            tvSeatNumber.setText(passenger.getSeatNumber());
            tvSeatClass.setText(passenger.getSeatClass());

            // Hiển thị giá ghế
            BigDecimal seatPrice = passenger.getSeatPrice();
            if (seatPrice != null) {
                tvSeatPrice.setText(currencyFormat.format(seatPrice) + " VNĐ");
            } else {
                tvSeatPrice.setText("Chưa có thông tin giá");
            }

            // Hiển thị loại ghế
            tvSeatType.setText(formatSeatType(passenger));

            // Xử lý nút mở rộng thông tin
            setupPassengerExpandButton(passengerView);

            passengerContainer.addView(passengerView);
        }
    }

    // Thiết lập nút mở rộng thông tin hành khách
    private void setupPassengerExpandButton(View passengerView) {
        ImageButton btnOptions = passengerView.findViewById(R.id.btn_passenger_options);
        LinearLayout detailLayout = passengerView.findViewById(R.id.layout_passenger_detail);

        // Mặc định hiển thị thông tin chi tiết
        detailLayout.setVisibility(View.VISIBLE);
        btnOptions.setRotation(180);

        btnOptions.setOnClickListener(v -> {
            if (detailLayout.getVisibility() == View.VISIBLE) {
                detailLayout.setVisibility(View.GONE);
                btnOptions.setRotation(0);
            } else {
                detailLayout.setVisibility(View.VISIBLE);
                btnOptions.setRotation(180);
            }
        });
    }

    // Hiển thị tóm tắt ghế
    private void displaySeatSummary(BookingDetailDto bookingDetail) {
        seatSummaryContainer.removeAllViews();

        if (bookingDetail.getPassengers() == null || bookingDetail.getPassengers().isEmpty()) {
            TextView noSeatSummary = new TextView(this);
            noSeatSummary.setText("Không có thông tin ghế");
            noSeatSummary.setTextColor(getResources().getColor(android.R.color.darker_gray));
            seatSummaryContainer.addView(noSeatSummary);
            return;
        }

        for (PassengerSeatDto passenger : bookingDetail.getPassengers()) {
            TextView tvSummary = new TextView(this);
            tvSummary.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvSummary.setTextSize(14f);
            tvSummary.setTextColor(getResources().getColor(android.R.color.black));
            tvSummary.setPadding(0, 8, 0, 8);

            String seatInfo = String.format("%s - Ghế %s - %s / %s",
                    passenger.getPassengerName(),
                    passenger.getSeatNumber(),
                    passenger.getSeatClass(),
                    formatSeatType(passenger));

            tvSummary.setText(seatInfo);
            seatSummaryContainer.addView(tvSummary);
        }
    }

    // Cập nhật nút hủy vé
    private void updateCancelButton(String bookingStatus) {
        if ("CONFIRMED".equalsIgnoreCase(bookingStatus)) {
            btnCancelBooking.setVisibility(View.VISIBLE);
            btnCancelBooking.setText("Hủy vé");
        } else {
            btnCancelBooking.setVisibility(View.GONE);
        }
    }

    // Format trạng thái đặt vé
    private String formatBookingStatus(String status) {
        if (status == null || status.isEmpty()) return "Chưa có thông tin";

        switch (status.toUpperCase()) {
            case "CONFIRMED":
                return "✅ Đã xác nhận";
            case "CANCELLED":
                return "❌ Đã hủy";
            case "PENDING":
                return "⏳ Đang chờ xử lý";
            default:
                return status;
        }
    }

    // Format trạng thái thanh toán
    private String formatPaymentStatus(String paymentStatus) {
        if (paymentStatus == null || paymentStatus.isEmpty()) return "Chưa có thông tin";

        switch (paymentStatus.toUpperCase()) {
            case "PAID":
                return "💳 Đã thanh toán";
            case "PENDING":
                return "⏳ Chưa thanh toán";
            case "REFUNDED":
                return "💰 Đã hoàn tiền";
            default:
                return paymentStatus;
        }
    }

    // Format loại ghế
    private String formatSeatType(PassengerSeatDto passenger) {
        if (passenger.isWindow()) {
            return "Ghế cửa sổ";
        } else if (passenger.isAisle()) {
            return "Ghế lối đi";
        } else {
            return "Ghế giữa";
        }
    }

    // Hiển thị dialog xác nhận hủy vé
    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy vé")
                .setMessage("Bạn có chắc chắn muốn hủy vé này không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Hủy vé", (dialog, which) -> performCancelBooking())
                .setNegativeButton("Không", null)
                .show();
    }

    // Thực hiện hủy vé
    private void performCancelBooking() {
        progressBar.setVisibility(View.VISIBLE);
        btnCancelBooking.setEnabled(false);
        btnCancelBooking.setText("Đang hủy vé...");

        Call<Void> call = bookingApi.cancelBookingUser(userId, bookingId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                btnCancelBooking.setEnabled(true);
                btnCancelBooking.setText("Hủy vé");

                if (response.isSuccessful()) {
                    Toast.makeText(BookingDetailActivity.this, "Hủy vé thành công", Toast.LENGTH_SHORT).show();
                    fetchBookingDetail(); // Làm mới thông tin đặt vé
                } else {
                    handleCancelErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnCancelBooking.setEnabled(true);
                btnCancelBooking.setText("Hủy vé");
                Toast.makeText(BookingDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý lỗi khi tải chi tiết đặt vé
    private void handleErrorResponse(Response<BookingDetailDto> response) {
        String errorMessage = "Không thể tải chi tiết đặt vé";

        if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
            redirectToLogin();
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy thông tin đặt vé";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Xử lý lỗi khi hủy vé
    private void handleCancelErrorResponse(Response<Void> response) {
        String errorMessage = "Không thể hủy vé";

        if (response.code() == 400) {
            errorMessage = "Vé này không thể hủy đặt chỗ trước ít hơn 24 giờ trước khi khởi hành";
        } else if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
            redirectToLogin();
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy thông tin đặt vé";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    /* Tách chuỗi */
    private String getAirportName(String airportStr) {
        if (airportStr == null) return "Chưa có thông tin";
        int idx = airportStr.lastIndexOf(" (");
        if (idx > 0) {
            return airportStr.substring(0, idx);
        } else {
            return airportStr;
        }
    }

    private String getAirportCode(String airportStr) {
        if (airportStr == null) return "";
        int start = airportStr.lastIndexOf("(");
        int end = airportStr.lastIndexOf(")");
        if (start >= 0 && end > start) {
            return airportStr.substring(start + 1, end);
        } else {
            return airportStr;
        }
    }

    // Hàm tách giờ phút
    private String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(date);
    }

    // Hàm tách ngày tháng năm theo định dạng "dd ThMM, yyyy"
    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'Th'MM, yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
}