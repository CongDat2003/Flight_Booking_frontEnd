package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.prm.flightbooking.dto.advancedsearch.FlightSearchResultDto;
import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FlightResultsActivity extends AppCompatActivity {

    private RecyclerView rvFlights;
    private MaterialButton btnBack;
    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private TabLayout tabLayout;
    private Chip chipSortPrice, chipSortTime, chipSortDuration;
    private android.view.View llEmptyState;
    private FlightAdapter flightAdapter;
    private SharedPreferences sharedPreferences;
    private List<FlightResponseDto> allFlights;
    private Map<String, List<FlightResponseDto>> flightsByStatus;
    private String currentFilter = "ALL";
    private String currentSort = "NONE"; // NONE, PRICE_ASC, PRICE_DESC, TIME_ASC, TIME_DESC, DURATION_ASC, DURATION_DESC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_results);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        bindingView();
        bindingAction();
        setupRecyclerView();
        setupTabs();
        setupSortChips();

        processSearchResults();
    }

    private void bindingView() {
        rvFlights = findViewById(R.id.rv_flights);
        btnBack = findViewById(R.id.btn_back);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvHeaderSubtitle = findViewById(R.id.tv_header_subtitle);
        tabLayout = findViewById(R.id.tab_layout);
        chipSortPrice = findViewById(R.id.chip_sort_price);
        chipSortTime = findViewById(R.id.chip_sort_time);
        chipSortDuration = findViewById(R.id.chip_sort_duration);
        llEmptyState = findViewById(R.id.ll_empty_state);
    }

    private void bindingAction() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        allFlights = new ArrayList<>();
        flightsByStatus = new HashMap<>();

        flightAdapter = new FlightAdapter(allFlights, this::onFlightSelected);
        rvFlights.setAdapter(flightAdapter);
    }

    private void setupTabs() {
        if (tabLayout == null) return;

        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã lên lịch"));
        tabLayout.addTab(tabLayout.newTab().setText("Bị hoãn"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"));

        tabLayout.selectTab(tabLayout.getTabAt(0));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        currentFilter = "ALL";
                        break;
                    case 1:
                        currentFilter = "SCHEDULED";
                        break;
                    case 2:
                        currentFilter = "DELAYED";
                        break;
                    case 3:
                        currentFilter = "CANCELLED";
                        break;
                }
                applyFiltersAndSort();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
    }

    private void setupSortChips() {
        if (chipSortPrice == null || chipSortTime == null || chipSortDuration == null) return;

        chipSortPrice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherSortChips(chipSortPrice);
                // Toggle between ascending and descending
                if (currentSort.equals("PRICE_ASC")) {
                    currentSort = "PRICE_DESC";
                } else {
                    currentSort = "PRICE_ASC";
                }
                applyFiltersAndSort();
            } else {
                currentSort = "NONE";
                applyFiltersAndSort();
            }
        });

        chipSortTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherSortChips(chipSortTime);
                if (currentSort.equals("TIME_ASC")) {
                    currentSort = "TIME_DESC";
                } else {
                    currentSort = "TIME_ASC";
                }
                applyFiltersAndSort();
            } else {
                currentSort = "NONE";
                applyFiltersAndSort();
            }
        });

        chipSortDuration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherSortChips(chipSortDuration);
                if (currentSort.equals("DURATION_ASC")) {
                    currentSort = "DURATION_DESC";
                } else {
                    currentSort = "DURATION_ASC";
                }
                applyFiltersAndSort();
            } else {
                currentSort = "NONE";
                applyFiltersAndSort();
            }
        });
    }

    private void uncheckOtherSortChips(Chip selectedChip) {
        if (selectedChip != chipSortPrice) chipSortPrice.setChecked(false);
        if (selectedChip != chipSortTime) chipSortTime.setChecked(false);
        if (selectedChip != chipSortDuration) chipSortDuration.setChecked(false);
    }

    private void categorizeFlights(List<FlightResponseDto> flights) {
        flightsByStatus.clear();
        flightsByStatus.put("SCHEDULED", new ArrayList<>());
        flightsByStatus.put("DELAYED", new ArrayList<>());
        flightsByStatus.put("CANCELLED", new ArrayList<>());

        for (FlightResponseDto flight : flights) {
            String status = flight.getStatus() != null ? flight.getStatus().toUpperCase(Locale.ROOT) : "UNKNOWN";
            
            if ("SCHEDULED".equals(status) || "CONFIRMED".equals(status) || "PREPARING".equals(status) || "DEPARTED".equals(status)) {
                flightsByStatus.get("SCHEDULED").add(flight);
            } else if ("DELAYED".equals(status) || "POSTPONED".equals(status)) {
                flightsByStatus.get("DELAYED").add(flight);
            } else if ("CANCELLED".equals(status) || "CANCELED".equals(status)) {
                flightsByStatus.get("CANCELLED").add(flight);
            }
        }
    }

    private void applyFiltersAndSort() {
        List<FlightResponseDto> filteredFlights;
        
        // Filter by status
        if ("ALL".equals(currentFilter)) {
            filteredFlights = new ArrayList<>(allFlights);
        } else {
            filteredFlights = new ArrayList<>(flightsByStatus.getOrDefault(currentFilter, new ArrayList<>()));
        }

        // Sort
        if (!"NONE".equals(currentSort)) {
            sortFlights(filteredFlights);
        }

        flightAdapter.setFlights(filteredFlights);
        updateHeader(filteredFlights.size());
        updateEmptyState(filteredFlights.isEmpty());
    }

    private void sortFlights(List<FlightResponseDto> flights) {
        switch (currentSort) {
            case "PRICE_ASC":
                Collections.sort(flights, (f1, f2) -> 
                    BigDecimal.valueOf(f1.getBasePrice()).compareTo(BigDecimal.valueOf(f2.getBasePrice())));
                break;
            case "PRICE_DESC":
                Collections.sort(flights, (f1, f2) -> 
                    BigDecimal.valueOf(f2.getBasePrice()).compareTo(BigDecimal.valueOf(f1.getBasePrice())));
                break;
            case "TIME_ASC":
                Collections.sort(flights, (f1, f2) -> 
                    f1.getDepartureTime().compareTo(f2.getDepartureTime()));
                break;
            case "TIME_DESC":
                Collections.sort(flights, (f1, f2) -> 
                    f2.getDepartureTime().compareTo(f1.getDepartureTime()));
                break;
            case "DURATION_ASC":
                Collections.sort(flights, (f1, f2) -> {
                    long d1 = f1.getArrivalTime().getTime() - f1.getDepartureTime().getTime();
                    long d2 = f2.getArrivalTime().getTime() - f2.getDepartureTime().getTime();
                    return Long.compare(d1, d2);
                });
                break;
            case "DURATION_DESC":
                Collections.sort(flights, (f1, f2) -> {
                    long d1 = f1.getArrivalTime().getTime() - f1.getDepartureTime().getTime();
                    long d2 = f2.getArrivalTime().getTime() - f2.getDepartureTime().getTime();
                    return Long.compare(d2, d1);
                });
                break;
        }
    }
    
    private void updateEmptyState(boolean isEmpty) {
        if (llEmptyState != null) {
            llEmptyState.setVisibility(isEmpty ? android.view.View.VISIBLE : android.view.View.GONE);
        }
        if (rvFlights != null) {
            rvFlights.setVisibility(isEmpty ? android.view.View.GONE : android.view.View.VISIBLE);
        }
    }

    private void updateHeader(int count) {
        String headerMessage = String.format("Tìm thấy %d chuyến bay", count);
        if (tvHeaderSubtitle != null) {
            tvHeaderSubtitle.setText(headerMessage);
        }
    }

    private void onFlightSelected(int flightId) {
        Intent intent = new Intent(FlightResultsActivity.this, ChooseSeatsActivity.class);
        intent.putExtra("flightId", flightId);
        startActivity(intent);
    }

    private void processSearchResults() {
        String resultJson = getIntent().getStringExtra("search_results_json");

        if (resultJson != null && !resultJson.isEmpty()) {
            parseSearchResults(resultJson);
        } else {
            showNoResultsMessage();
        }
    }

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

    private void updateUIWithResults(FlightSearchResultDto result) {
        List<FlightResponseDto> outboundFlights = result.getOutboundFlights();
        List<FlightResponseDto> returnFlights = result.getReturnFlights();

        allFlights.clear();
        allFlights.addAll(outboundFlights);
        if (returnFlights != null) {
            allFlights.addAll(returnFlights);
        }

        categorizeFlights(allFlights);

        currentFilter = "ALL";
        currentSort = "NONE";
        if (tabLayout != null && tabLayout.getTabCount() > 0) {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        }
        // Uncheck all sort chips
        if (chipSortPrice != null) chipSortPrice.setChecked(false);
        if (chipSortTime != null) chipSortTime.setChecked(false);
        if (chipSortDuration != null) chipSortDuration.setChecked(false);
        
        applyFiltersAndSort();

        Toast.makeText(this, "Tìm thấy " + allFlights.size() + " chuyến bay", Toast.LENGTH_SHORT).show();
    }

    private void showNoResultsMessage() {
        String noResultsMessage = "Không tìm thấy chuyến bay nào";
        if (tvHeaderSubtitle != null) {
            tvHeaderSubtitle.setText(noResultsMessage);
        }
        updateEmptyState(true);
        Toast.makeText(this, noResultsMessage, Toast.LENGTH_SHORT).show();
    }

    private void handleParseError(Exception e) {
        String errorMessage = "Lỗi xử lý dữ liệu tìm kiếm";
        if (tvHeaderSubtitle != null) {
            tvHeaderSubtitle.setText(errorMessage);
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false) &&
                sharedPreferences.getInt("user_id", -1) > 0;
    }

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
        if (!isLoggedIn()) {
            redirectToLogin();
        }
    }
}



































