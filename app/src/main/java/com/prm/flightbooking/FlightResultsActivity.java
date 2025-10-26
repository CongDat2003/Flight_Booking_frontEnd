package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.prm.flightbooking.dto.advancedsearch.FlightSearchResultDto;
import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.util.ArrayList;
import java.util.List;

public class FlightResultsActivity extends AppCompatActivity {

    private RecyclerView rvFlights;
    private TextView tvHeader;
    private ImageButton btnBack;
    private FlightAdapter flightAdapter;
    private SharedPreferences sharedPreferences;
    private List<FlightResponseDto> flightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_results);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        bindingView();
        bindingAction();
        setupRecyclerView();

        // Xử lý kết quả tìm kiếm
        processSearchResults();
    }

    private void bindingView() {
        rvFlights = findViewById(R.id.rv_flights);
        tvHeader = findViewById(R.id.tv_header);
        btnBack = findViewById(R.id.btn_back);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(view -> finish());
    }

    // Thiết lập RecyclerView
    private void setupRecyclerView() {
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        flightList = new ArrayList<>();

        // Khởi tạo adapter với listener để chọn chuyến bay
        flightAdapter = new FlightAdapter(flightList, this::onFlightSelected);
        rvFlights.setAdapter(flightAdapter);
    }

    // Xử lý khi người dùng chọn chuyến bay
    private void onFlightSelected(int flightId) {
        Intent intent = new Intent(FlightResultsActivity.this, ChooseSeatsActivity.class);
        intent.putExtra("flightId", flightId);
        startActivity(intent);
    }

    // Xử lý kết quả tìm kiếm từ Intent
    private void processSearchResults() {
        String resultJson = getIntent().getStringExtra("search_results_json");

        if (resultJson != null && !resultJson.isEmpty()) {
            parseSearchResults(resultJson);
        } else {
            showNoResultsMessage();
        }
    }

    // Phân tích dữ liệu JSON kết quả tìm kiếm
    private void parseSearchResults(String resultJson) {
        try {
            Gson gson = new Gson();
            FlightSearchResultDto result = gson.fromJson(resultJson, FlightSearchResultDto.class);

            if (result != null && result.getOutboundFlights() != null && !result.getOutboundFlights().isEmpty()) {
                updateUIWithResults(result);
            } else {
                showNoResultsMessage();
            }
        } catch (Exception e) {
            handleParseError(e);
        }
    }

    // Cập nhật giao diện với kết quả tìm kiếm
    private void updateUIWithResults(FlightSearchResultDto result) {
        List<FlightResponseDto> outboundFlights = result.getOutboundFlights();
        List<FlightResponseDto> returnFlights = result.getReturnFlights();

        // Tạo thông báo header
        String headerMessage = createHeaderMessage(outboundFlights.size(),
                returnFlights != null ? returnFlights.size() : 0);
        tvHeader.setText(headerMessage);

        // Cập nhật danh sách chuyến bay
        flightAdapter.setFlights(outboundFlights);

        Toast.makeText(this, "Tìm thấy " + outboundFlights.size() + " chuyến bay", Toast.LENGTH_SHORT).show();
    }

    // Tạo thông báo header
    private String createHeaderMessage(int outboundCount, int returnCount) {
        String message = "Tìm thấy " + outboundCount + " chuyến bay đi";

        if (returnCount > 0) {
            message += " và " + returnCount + " chuyến bay về";
        }

        return message;
    }

    // Hiển thị thông báo không có kết quả
    private void showNoResultsMessage() {
        String noResultsMessage = "Không tìm thấy chuyến bay nào";
        tvHeader.setText(noResultsMessage);
        Toast.makeText(this, noResultsMessage, Toast.LENGTH_SHORT).show();
    }

    // Xử lý lỗi phân tích dữ liệu
    private void handleParseError(Exception e) {
        String errorMessage = "Lỗi xử lý dữ liệu tìm kiếm";
        tvHeader.setText(errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false) &&
                sharedPreferences.getInt("user_id", -1) > 0;
    }

    // Chuyển hướng đến màn hình đăng nhập
    private void redirectToLogin() {
        Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra lại trạng thái đăng nhập khi quay lại activity
        if (!isLoggedIn()) {
            redirectToLogin();
        }
    }
}