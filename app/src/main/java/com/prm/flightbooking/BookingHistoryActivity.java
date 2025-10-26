package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.UserBookingHistoryDto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookingHistory;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private TextView tvNoBookings;
    private ImageButton btnBack, btnFilter;
    private TextInputEditText etSearch;
    private CardView chipAll, chipUpcoming, chipCompleted, chipCancelled;
    private LinearLayout searchFilterContainer;
    private Button btnBookFlight;

    private BookingApiEndpoint bookingApi;
    private BookingHistoryAdapter adapter;
    private List<UserBookingHistoryDto> bookingList, filteredList;
    private SharedPreferences sharedPreferences;
    private int userId;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private String currentFilter = "Tất cả";
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        // Khởi tạo API và SharedPreferences
        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra đăng nhập và lấy userId
        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        bindingView();
        bindingAction();
        setupRecyclerView();
        if (bookingList.isEmpty() && !isFetching) {
            fetchBookingHistory();
        }
    }

    private void bindingView() {
        rvBookingHistory = findViewById(R.id.rv_booking_history);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        tvNoBookings = findViewById(R.id.tv_no_bookings);
        btnBack = findViewById(R.id.btn_back);
        btnFilter = findViewById(R.id.btn_filter);
        etSearch = findViewById(R.id.et_search);
        chipAll = findViewById(R.id.chip_all);
        chipUpcoming = findViewById(R.id.chip_upcoming);
        chipCompleted = findViewById(R.id.chip_completed);
        chipCancelled = findViewById(R.id.chip_cancelled);
        searchFilterContainer = findViewById(R.id.search_filter_container);
        btnBookFlight = findViewById(R.id.btn_book_flight);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBtnBackClick);
        btnFilter.setOnClickListener(this::onBtnFilterClick);
        btnBookFlight.setOnClickListener(this::onBtnBookFlightClick);
        chipAll.setOnClickListener(this::onChipAllClick);
        chipUpcoming.setOnClickListener(this::onChipUpcomingClick);
        chipCompleted.setOnClickListener(this::onChipCompletedClick);
        chipCancelled.setOnClickListener(this::onChipCancelledClick);

        // Tìm kiếm theo thời gian thực
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterBookings(s.toString());
            }
        });
    }

    // Quay lại màn hình trước
    private void onBtnBackClick(View view) {
        finish();
    }

    // Hiển thị hoặc ẩn bộ lọc
    private void onBtnFilterClick(View view) {
        toggleSearchFilter();
    }

    // Chuyển sang màn hình đặt vé mới
    private void onBtnBookFlightClick(View view) {
        Intent intent = new Intent(this, BookingActivity.class);
        startActivity(intent);
    }

    // Lọc tất cả đặt vé
    private void onChipAllClick(View view) {
        setFilter("Tất cả");
    }

    // Lọc vé sắp bay
    private void onChipUpcomingClick(View view) {
        setFilter("Sắp tới");
    }

    // Lọc vé đã bay
    private void onChipCompletedClick(View view) {
        setFilter("Đã hoàn thành");
    }

    // Lọc vé đã hủy
    private void onChipCancelledClick(View view) {
        setFilter("Đã hủy");
    }

    // Hiển thị hoặc ẩn thanh tìm kiếm
    private void toggleSearchFilter() {
        if (searchFilterContainer.getVisibility() == View.VISIBLE) {
            searchFilterContainer.setVisibility(View.GONE);
        } else {
            searchFilterContainer.setVisibility(View.VISIBLE);
        }
    }

    // Thiết lập danh sách hiển thị
    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new BookingHistoryAdapter(filteredList);
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);
    }

    // Thiết lập bộ lọc theo trạng thái
    private void setFilter(String filter) {
        currentFilter = filter;
        updateChipStyles();
        filterBookings(etSearch.getText().toString());
    }

    // Cập nhật giao diện cho các nút lọc
    private void updateChipStyles() {
        int activeColor = getResources().getColor(R.color.blue_500);
        int inactiveColor = getResources().getColor(R.color.grey_200);
        int activeTextColor = getResources().getColor(R.color.white);
        int inactiveTextColor = getResources().getColor(R.color.grey_600);

        TextView tvAll = chipAll.findViewById(R.id.tv_chip_all);
        TextView tvUpcoming = chipUpcoming.findViewById(R.id.tv_chip_upcoming);
        TextView tvCompleted = chipCompleted.findViewById(R.id.tv_chip_completed);
        TextView tvCancelled = chipCancelled.findViewById(R.id.tv_chip_cancelled);

        // Cập nhật màu nền cho các chip
        chipAll.setCardBackgroundColor(currentFilter.equals("Tất cả") ? activeColor : inactiveColor);
        chipUpcoming.setCardBackgroundColor(currentFilter.equals("Sắp tới") ? activeColor : inactiveColor);
        chipCompleted.setCardBackgroundColor(currentFilter.equals("Đã hoàn thành") ? activeColor : inactiveColor);
        chipCancelled.setCardBackgroundColor(currentFilter.equals("Đã hủy") ? activeColor : inactiveColor);

        // Cập nhật màu chữ cho các chip
        if (tvAll != null) tvAll.setTextColor(currentFilter.equals("Tất cả") ? activeTextColor : inactiveTextColor);
        if (tvUpcoming != null) tvUpcoming.setTextColor(currentFilter.equals("Sắp tới") ? activeTextColor : inactiveTextColor);
        if (tvCompleted != null) tvCompleted.setTextColor(currentFilter.equals("Đã hoàn thành") ? activeTextColor : inactiveTextColor);
        if (tvCancelled != null) tvCancelled.setTextColor(currentFilter.equals("Đã hủy") ? activeTextColor : inactiveTextColor);
    }

    // Lọc danh sách đặt vé theo điều kiện
    private void filterBookings(String query) {
        filteredList.clear();

        for (UserBookingHistoryDto booking : bookingList) {
            // Kiểm tra trạng thái đặt vé
            boolean matchesFilter = currentFilter.equals("Tất cả") ||
                    (currentFilter.equals("Sắp tới") && booking.getBookingStatus().equalsIgnoreCase("Confirmed")) ||
                    (currentFilter.equals("Đã hoàn thành") && booking.getBookingStatus().equalsIgnoreCase("Completed")) ||
                    (currentFilter.equals("Đã hủy") && booking.getBookingStatus().equalsIgnoreCase("Cancelled"));

            // Kiểm tra từ khóa tìm kiếm
            boolean matchesQuery = query.isEmpty() ||
                    booking.getRoute().toLowerCase().contains(query.toLowerCase()) ||
                    booking.getBookingReference().toLowerCase().contains(query.toLowerCase()) ||
                    booking.getBookingDate().toString().toLowerCase().contains(query.toLowerCase());

            if (matchesFilter && matchesQuery) {
                filteredList.add(booking);
            }
        }

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    // Gọi API để lấy lịch sử đặt vé
    private void fetchBookingHistory() {
        if (isFetching) {
            Log.d("BookingHistory", "Fetch already in progress, skipping");
            return;
        }
        isFetching = true;

        userId = sharedPreferences.getInt("user_id", -1);

        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            isFetching = false;
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        // Clear lists to prevent duplicates
        bookingList.clear();
        filteredList.clear();
        adapter.notifyDataSetChanged();

        Log.d("BookingHistory", "Before fetch: bookingList size = " + bookingList.size());
        Call<List<UserBookingHistoryDto>> call = bookingApi.getBookingHistory(userId, currentPage, PAGE_SIZE);
        call.enqueue(new Callback<List<UserBookingHistoryDto>>() {
            @Override
            public void onResponse(Call<List<UserBookingHistoryDto>> call, Response<List<UserBookingHistoryDto>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<UserBookingHistoryDto> bookings = response.body();
                    Log.d("BookingHistory", "Fetched bookings size = " + bookings.size());
                    for (UserBookingHistoryDto booking : bookings) {
                        Log.d("BookingHistory", "Booking ID: " + booking.getBookingId() + ", Reference: " + booking.getBookingReference());
                    }
                    bookingList.addAll(bookings);
                    Log.d("BookingHistory", "After fetch: bookingList size = " + bookingList.size());
                    filterBookings(etSearch.getText().toString());
                    Log.d("BookingHistory", "After filter: filteredList size = " + filteredList.size());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<UserBookingHistoryDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingHistoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("BookingHistory", "Lỗi gọi API: " + t.getMessage());
            }
        });
    }

    // Xử lý lỗi từ server
    private void handleErrorResponse(Response<List<UserBookingHistoryDto>> response) {
        try {
            String errorMessage = "Không thể tải lịch sử đặt vé";

            if (response.code() == 401) {
                errorMessage = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                performLogout();
            } else if (response.code() == 400) {
                errorMessage = "Yêu cầu không hợp lệ";
            } else if (response.code() == 404) {
                errorMessage = "Không tìm thấy lịch sử đặt vé";
            } else if (response.code() >= 500) {
                errorMessage = "Lỗi server, vui lòng thử lại sau";
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị thông báo khi không có dữ liệu
    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvBookingHistory.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvBookingHistory.setVisibility(View.VISIBLE);
        }
    }

    // Đăng xuất người dùng
    private void performLogout() {
        sharedPreferences.edit().clear().apply();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        redirectToLogin();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLoggedIn()) {
            redirectToLogin();
        } else if (bookingList.isEmpty() && !isFetching) {
            fetchBookingHistory();
        }
    }

    // Adapter cho RecyclerView hiển thị lịch sử đặt vé
    private static class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

        private final List<UserBookingHistoryDto> bookings;
        private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy, HH:mm", new Locale("vi", "VN"));
        private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("vi", "VN"));
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        BookingHistoryAdapter(List<UserBookingHistoryDto> bookings) {
            this.bookings = bookings;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_booking_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserBookingHistoryDto booking = bookings.get(position);

            // Hiển thị trạng thái đặt vé
            String status = booking.getBookingStatus();
            holder.tvStatus.setText(getStatusText(status));
            holder.statusLayout.setBackgroundColor(getStatusColor(holder.itemView.getContext(), status));
            holder.tvBookingId.setText("Mã đặt vé: " + booking.getBookingReference());

            // Thông tin chuyến bay
            holder.tvAirline.setText(booking.getAirlineName());
            holder.tvFlightDetails.setText("Chuyến bay " + booking.getFlightNumber());

            // Hiển thị giá vé
            BigDecimal totalAmount = booking.getTotalAmount();
            String formattedPrice = currencyFormat.format(totalAmount);
            holder.tvPrice.setText(formattedPrice);

            // Thông tin tuyến bay
            String route = booking.getRoute().replace("→", " đến ").trim();
            String[] routeParts = route.split(" đến ");
            holder.tvFromCity.setText(routeParts.length > 0 ? routeParts[0].trim() : "Chưa xác định");
            holder.tvToCity.setText(routeParts.length > 1 ? routeParts[1].trim() : "Chưa xác định");

            // Mã sân bay
            holder.tvFromCode.setText(routeParts.length > 0 ? routeParts[0].substring(0, 3).toUpperCase() : "---");
            holder.tvToCode.setText(routeParts.length > 1 ? routeParts[1].substring(0, 3).toUpperCase() : "---");

            // Thời gian bay
            holder.tvFromTime.setText(timeFormat.format(booking.getDepartureTime()));
            holder.tvToTime.setText(timeFormat.format(booking.getArrivalTime()));
            holder.tvDate.setText(dateTimeFormat.format(booking.getBookingDate()));

            // Thời gian bay
            long durationMillis = booking.getArrivalTime().getTime() - booking.getDepartureTime().getTime();
            long hours = durationMillis / (1000 * 60 * 60);
            long minutes = (durationMillis / (1000 * 60)) % 60;
            holder.tvDuration.setText(String.format(new Locale("vi", "VN"), "%d giờ %d phút", hours, minutes));

            // Nút hành động
            holder.btnViewDetail.setText("Xem chi tiết");

            if (status.equalsIgnoreCase("Confirmed")) {
                holder.btnCheckin.setText("Check-in");
                holder.btnCheckin.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase("Completed")) {
                holder.btnCheckin.setText("Đặt lại");
                holder.btnCheckin.setVisibility(View.VISIBLE);
            } else {
                holder.btnCheckin.setVisibility(View.GONE);
            }

            // Sự kiện click cho nút xem chi tiết
            holder.btnViewDetail.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), BookingDetailActivity.class);
                intent.putExtra("bookingId", booking.getBookingId());
                holder.itemView.getContext().startActivity(intent);
            });

            // Sự kiện click cho nút thứ hai
            holder.btnCheckin.setOnClickListener(v -> {
                if (status.equalsIgnoreCase("Confirmed")) {
                    // Chuyển sang màn hình thanh toán
                    Intent intent = new Intent(holder.itemView.getContext(), PayActivity.class);
                    intent.putExtra("bookingId", booking.getBookingId());
                    intent.putExtra("bookingReference", booking.getBookingReference());
                    intent.putExtra("totalAmount", booking.getTotalAmount().doubleValue());
                    intent.putExtra("flightNumber", booking.getFlightNumber());
                    intent.putExtra("route", booking.getRoute());
                    holder.itemView.getContext().startActivity(intent);
                } else if (status.equalsIgnoreCase("Completed")) {
                    // Chuyển sang màn hình đặt vé mới
                    Intent intent = new Intent(holder.itemView.getContext(), BookingActivity.class);
                    holder.itemView.getContext().startActivity(intent);
                }
            });

            // Làm mờ các vé đã hủy
            holder.itemView.setAlpha(status.equalsIgnoreCase("Cancelled") ? 0.7f : 1.0f);
        }

        // Chuyển đổi trạng thái sang tiếng Việt
        private String getStatusText(String status) {
            switch (status.toLowerCase()) {
                case "confirmed":
                    return "✅ Đã xác nhận";
                case "completed":
                    return "✈️ Đã hoàn thành";
                case "cancelled":
                    return "❌ Đã hủy";
                default:
                    return status;
            }
        }

        // Lấy màu theo trạng thái
        private int getStatusColor(android.content.Context context, String status) {
            switch (status.toLowerCase()) {
                case "confirmed":
                    return context.getResources().getColor(R.color.green_100);
                case "completed":
                    return context.getResources().getColor(R.color.blue_100);
                case "cancelled":
                    return context.getResources().getColor(R.color.red_100);
                default:
                    return context.getResources().getColor(R.color.grey_100);
            }
        }

        @Override
        public int getItemCount() {
            return bookings.size();
        }

        // ViewHolder cho từng item trong danh sách
        static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout statusLayout;
            TextView tvStatus, tvBookingId, tvAirline, tvFlightDetails, tvPrice;
            TextView tvFromCity, tvFromCode, tvFromTime, tvToCity, tvToCode, tvToTime;
            TextView tvDate, tvDuration;
            Button btnViewDetail, btnCheckin;

            ViewHolder(View itemView) {
                super(itemView);
                statusLayout = itemView.findViewById(R.id.status_layout);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvBookingId = itemView.findViewById(R.id.tv_booking_id);
                tvAirline = itemView.findViewById(R.id.tv_airline);
                tvFlightDetails = itemView.findViewById(R.id.tv_flight_details);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvFromCity = itemView.findViewById(R.id.tv_from_city);
                tvFromCode = itemView.findViewById(R.id.tv_from_code);
                tvFromTime = itemView.findViewById(R.id.tv_from_time);
                tvToCity = itemView.findViewById(R.id.tv_to_city);
                tvToCode = itemView.findViewById(R.id.tv_to_code);
                tvToTime = itemView.findViewById(R.id.tv_to_time);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
                btnCheckin = itemView.findViewById(R.id.btn_check_in);
            }
        }
    }
}