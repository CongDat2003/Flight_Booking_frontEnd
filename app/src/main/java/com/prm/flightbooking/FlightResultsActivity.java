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

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.prm.flightbooking.dto.advancedsearch.FlightSearchResultDto;
import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FlightResultsActivity extends AppCompatActivity {

    private RecyclerView rvFlights;
    private TextView tvHeader;
    private ImageButton btnBack;
    private TabLayout tabLayout;
    private FlightAdapter flightAdapter;
    private SharedPreferences sharedPreferences;
    private List<FlightResponseDto> allFlights; // Tất cả chuyến bay
    private Map<String, List<FlightResponseDto>> flightsByStatus; // Chuyến bay phân loại theo status
    private String currentFilter = "ALL"; // Filter hiện tại

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
        setupTabLayout();

        // Xử lý kết quả tìm kiếm
        processSearchResults();
    }

    private void bindingView() {
        rvFlights = findViewById(R.id.rv_flights);
        tvHeader = findViewById(R.id.tv_header);
        btnBack = findViewById(R.id.btn_back);
        tabLayout = findViewById(R.id.tab_layout);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(view -> finish());
    }

    // Thiết lập RecyclerView
    private void setupRecyclerView() {
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        allFlights = new ArrayList<>();
        flightsByStatus = new HashMap<>();

        // Khởi tạo adapter với listener để chọn chuyến bay
        flightAdapter = new FlightAdapter(allFlights, this::onFlightSelected);
        rvFlights.setAdapter(flightAdapter);
    }

    // Thiết lập TabLayout để phân loại chuyến bay
    private void setupTabLayout() {
        // Thêm các tab - chỉ 4 tab theo yêu cầu
        tabLayout.addTab(tabLayout.newTab().setText("Đã lên lịch"));
        tabLayout.addTab(tabLayout.newTab().setText("Bị hoãn"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hoàn thành"));

        // Xử lý khi chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        currentFilter = "SCHEDULED";
                        break;
                    case 1:
                        currentFilter = "DELAYED";
                        break;
                    case 2:
                        currentFilter = "CANCELLED";
                        break;
                    case 3:
                        currentFilter = "COMPLETED";
                        break;
                }
                filterFlightsByStatus();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    // Phân loại chuyến bay theo trạng thái
    private void categorizeFlights(List<FlightResponseDto> flights) {
        flightsByStatus.clear();
        flightsByStatus.put("SCHEDULED", new ArrayList<>());
        flightsByStatus.put("DELAYED", new ArrayList<>());
        flightsByStatus.put("COMPLETED", new ArrayList<>());
        flightsByStatus.put("CANCELLED", new ArrayList<>());

        for (FlightResponseDto flight : flights) {
            String status = flight.getStatus() != null ? flight.getStatus().toUpperCase(Locale.ROOT) : "UNKNOWN";
            
            // Phân loại vào tab tương ứng
            if ("SCHEDULED".equals(status) || "PREPARING".equals(status) || "DEPARTED".equals(status)) {
                flightsByStatus.get("SCHEDULED").add(flight);
            } else if (flightsByStatus.containsKey(status)) {
                flightsByStatus.get(status).add(flight);
            }
        }
    }

    // Lọc chuyến bay theo trạng thái được chọn
    private void filterFlightsByStatus() {
        List<FlightResponseDto> filteredFlights = flightsByStatus.getOrDefault(currentFilter, new ArrayList<>());
        flightAdapter.setFlights(filteredFlights);
        
        // Cập nhật header
        updateHeader(filteredFlights.size());
    }

    // Cập nhật header với số lượng chuyến bay
    private void updateHeader(int count) {
        String statusText = getStatusText(currentFilter);
        String headerMessage = String.format("Tìm thấy %d chuyến bay %s", count, statusText);
        tvHeader.setText(headerMessage);
    }

    // Lấy text hiển thị cho trạng thái
    private String getStatusText(String status) {
        switch (status) {
            case "SCHEDULED":
                return "đã lên lịch";
            case "DELAYED":
                return "bị hoãn";
            case "COMPLETED":
                return "đã hoàn thành";
            case "CANCELLED":
                return "đã hủy";
            default:
                return "";
        }
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

        // Lưu tất cả chuyến bay
        allFlights.clear();
        allFlights.addAll(outboundFlights);
        if (returnFlights != null) {
            allFlights.addAll(returnFlights);
        }

        // Phân loại chuyến bay theo trạng thái
        categorizeFlights(allFlights);

        // Hiển thị tab "Đã lên lịch" ban đầu (tab đầu tiên)
        currentFilter = "SCHEDULED";
        tabLayout.getTabAt(0).select(); // Chọn tab đầu tiên
        filterFlightsByStatus();

        Toast.makeText(this, "Tìm thấy " + allFlights.size() + " chuyến bay", Toast.LENGTH_SHORT).show();
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