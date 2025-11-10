package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.FlightApiEndpoint;
import com.prm.flightbooking.dto.flight.AdminFlightResponseDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private RecyclerView rvFlights;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private FlightApiEndpoint flightApi;
    private FlightAdapter flightAdapter;
    private List<AdminFlightResponseDto> flightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Khởi tạo API service
        flightApi = ApiServiceProvider.getFlightApi();

        bindingView();
        bindingAction();

        // Kiểm tra trạng thái đăng nhập và gọi API lấy danh sách chuyến bay
        checkLoginAndFetchFlights();
    }

    // Bind các view từ layout
    private void bindingView() {
        rvFlights = findViewById(R.id.rv_flights);
        progressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.btn_back);
    }

    /*
     - Thiết lập adapter, layout manager và các sự kiện cho RecyclerView
     - Khởi tạo danh sách chuyến bay rỗng
     */
    private void bindingAction() {
        // Khởi tạo danh sách chuyến bay và adapter
        flightList = new ArrayList<>();
        flightAdapter = new FlightAdapter(flightList, this::onFlightItemClick);

        // Thiết lập layout và adapter cho RecyclerView
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        rvFlights.setAdapter(flightAdapter);
        btnBack.setOnClickListener(v -> finish());
    }

    /*
    - Kiểm tra trạng thái đăng nhập, nếu chưa đăng nhập thì chuyển sang màn hình Login
    - Nếu đã đăng nhập thì gọi API lấy danh sách chuyến bay
    */
    private void checkLoginAndFetchFlights() {
        // Lấy userId từ SharedPreferences để kiểm tra đăng nhập
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId <= 0) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        } else {
            performFetchFlights();
        }
    }

    // Xử lý khi click vào một chuyến bay trong danh sách
    private void onFlightItemClick(int flightId) {
        // Tìm chuyến bay được chọn
        AdminFlightResponseDto selectedFlight = flightList.stream()
                .filter(flight -> flight.getFlightId() == flightId)
                .findFirst()
                .orElse(null);

        if (selectedFlight != null && selectedFlight.getBasePrice() > 0) {
            // Khi click vào chuyến bay, chuyển sang màn hình chọn ghế và truyền basePrice
            Intent intent = new Intent(BookingActivity.this, ChooseSeatsActivity.class);
            intent.putExtra("flightId", flightId);
            intent.putExtra("basePrice", String.valueOf(selectedFlight.getBasePrice()));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Giá cơ bản không hợp lệ hoặc không tìm thấy chuyến bay", Toast.LENGTH_SHORT).show();
        }
    }

    // Gọi API lấy danh sách chuyến bay
    private void performFetchFlights() {
        // Hiển thị ProgressBar khi đang tải dữ liệu
        progressBar.setVisibility(View.VISIBLE);

        // Gọi API
        Call<List<AdminFlightResponseDto>> call = flightApi.getAllFlights(1, 20);
        call.enqueue(new Callback<List<AdminFlightResponseDto>>() {
            @Override
            public void onResponse(Call<List<AdminFlightResponseDto>> call, Response<List<AdminFlightResponseDto>> response) {
                // Ẩn ProgressBar sau khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);

                // Kiểm tra xem API có trả về dữ liệu thành công không
                if (response.isSuccessful() && response.body() != null) {
                    // Lọc chỉ lấy các chuyến bay có trạng thái SCHEDULED (Đã lên lịch)
                    List<AdminFlightResponseDto> allFlights = response.body();
                    List<AdminFlightResponseDto> scheduledFlights = new ArrayList<>();
                    
                    for (AdminFlightResponseDto flight : allFlights) {
                        String status = flight.getStatus();
                        if (status != null && status.equalsIgnoreCase("SCHEDULED")) {
                            scheduledFlights.add(flight);
                        }
                    }
                    
                    // Xóa danh sách cũ và cập nhật danh sách mới (chỉ SCHEDULED)
                    flightList.clear();
                    flightList.addAll(scheduledFlights);
                    // Cập nhật adapter để hiển thị danh sách mới
                    flightAdapter.setFlights(flightList);
                    
                    if (scheduledFlights.isEmpty()) {
                        Toast.makeText(BookingActivity.this, "Không có chuyến bay nào đã lên lịch", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookingActivity.this, "Tải danh sách chuyến bay thành công!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý lỗi nếu API trả về không thành công
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<AdminFlightResponseDto>> call, Throwable t) {
                // Ẩn ProgressBar khi có lỗi mạng
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý các lỗi từ phản hồi của server
    private void handleErrorResponse(Response<List<AdminFlightResponseDto>> response) {
        String errorMessage = "Không thể tải danh sách chuyến bay";
        // Kiểm tra mã lỗi từ server
        if (response.code() == 400) {
            errorMessage = "Yêu cầu không hợp lệ";
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay nào";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Chuyển sang màn hình đăng nhập và kết thúc activity hiện tại
    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}