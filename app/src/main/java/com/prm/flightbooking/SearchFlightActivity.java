package com.prm.flightbooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.prm.flightbooking.api.AdvancedSearchApiEndpoint;
import com.prm.flightbooking.api.AirportApiEndpoint;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.dto.advancedsearch.AdvancedFlightSearchDto;
import com.prm.flightbooking.dto.advancedsearch.FlightSearchResultDto;
import com.prm.flightbooking.dto.airport.AirportDto;
import com.prm.flightbooking.models.SeatClass;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFlightActivity extends AppCompatActivity {

    // UI
    private TextView tvUserName, tvAdultCount, tvDepartureDate, tvReturnDate, tvClass;
    private AutoCompleteTextView actvFrom, actvTo;
    private ImageButton btnMinusAdult, btnPlusAdult;
    private Button btnSearchFlights;
    private ImageButton btnBack;
    // API
    private AdvancedSearchApiEndpoint searchApi;
    private AirportApiEndpoint airportApi;

    // Data
    private int adultCount = 2;
    private List<AirportDto> airportList = new ArrayList<>();
    private List<SeatClass> seatClassesList;
    private String[] seatClassNames;
    private int selectedClassIndex = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.forLanguageTag("vi-VN"));
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flight);

        // Khởi tạo API services
        searchApi = ApiServiceProvider.getAdvancedSearchApi();
        airportApi = ApiServiceProvider.getAirportApi();

        // Khởi tạo dữ liệu ban đầu
        initializeSeatClasses();

        bindingView();
        bindingAction();

        // Tải thông tin người dùng và dữ liệu ban đầu
        loadUserInfo();
        loadAirports();
        setupBackPressHandler();
    }

    private void bindingView() {
        tvUserName = findViewById(R.id.user_name);
        actvFrom = findViewById(R.id.tv_from);
        actvTo = findViewById(R.id.tv_to);
        tvAdultCount = findViewById(R.id.tv_adult_count);
        tvDepartureDate = findViewById(R.id.tv_departure_date);
        tvReturnDate = findViewById(R.id.tv_return_date);
        tvClass = findViewById(R.id.tv_class);
        btnMinusAdult = findViewById(R.id.btn_minus_adult);
        btnPlusAdult = findViewById(R.id.btn_plus_adult);
        btnSearchFlights = findViewById(R.id.btn_search_flights);
        btnBack = findViewById(R.id.btn_back);
        // Cấu hình AutoCompleteTextView
        setupAutoCompleteTextViews();
    }

    private void bindingAction() {
        btnMinusAdult.setOnClickListener(this::onBtnMinusAdultClick);
        btnPlusAdult.setOnClickListener(this::onBtnPlusAdultClick);
        tvDepartureDate.setOnClickListener(this::onTvDepartureDateClick);
        tvReturnDate.setOnClickListener(this::onTvReturnDateClick);
        tvClass.setOnClickListener(this::onTvClassClick);
        btnSearchFlights.setOnClickListener(this::onBtnSearchFlightsClick);
        actvFrom.setOnClickListener(v -> actvFrom.showDropDown());
        actvTo.setOnClickListener(v -> actvTo.showDropDown());
        btnBack.setOnClickListener(v -> finish());
    }

    // Khởi tạo dữ liệu hạng ghế
    private void initializeSeatClasses() {
        seatClassesList = getMockSeatClasses();
        seatClassNames = new String[seatClassesList.size()];
        for (int i = 0; i < seatClassesList.size(); i++) {
            seatClassNames[i] = seatClassesList.get(i).getClassDescription();
        }
    }

    // Cấu hình AutoCompleteTextView
    private void setupAutoCompleteTextViews() {
        actvFrom.setThreshold(1);
        actvTo.setThreshold(1);
        actvFrom.setDropDownHeight(400);
        actvTo.setDropDownHeight(400);
    }

    // Xử lý nút back của hệ thống
    private void setupBackPressHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (btnSearchFlights.isEnabled()) {
                    setEnabled(false);
                    SearchFlightActivity.super.onBackPressed();
                } else {
                    Log.d("SearchFlightActivity", "Back press ignored during search");
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // Tải thông tin người dùng từ SharedPreferences
    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Khách");
        tvUserName.setText(userName);
    }

    // Tải danh sách sân bay từ API
    private void loadAirports() {
        Call<List<AirportDto>> call = airportApi.getAllAirports();
        call.enqueue(new Callback<List<AirportDto>>() {
            @Override
            public void onResponse(Call<List<AirportDto>> call, Response<List<AirportDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    airportList = response.body();
                    setupAirportDropdown();
                    setupInitialValues();
                } else {
                    handleAirportLoadError(response);
                }
            }

            @Override
            public void onFailure(Call<List<AirportDto>> call, Throwable t) {
                Toast.makeText(SearchFlightActivity.this,"Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thiết lập dropdown cho sân bay
    private void setupAirportDropdown() {
        List<String> airportNames = new ArrayList<>();
        for (AirportDto airport : airportList) {
            if (airport.getAirportCode() != null && airport.getCity() != null) {
                airportNames.add(airport.getAirportCode() + " - " + airport.getCity());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, airportNames);
        actvFrom.setAdapter(adapter);
        actvTo.setAdapter(adapter);

        // Thiết lập giá trị mặc định
        if (airportList.size() > 0) {
            actvFrom.setText(airportList.get(0).getAirportCode() + " - " + airportList.get(0).getCity(), false);
        }
        if (airportList.size() > 1) {
            actvTo.setText(airportList.get(1).getAirportCode() + " - " + airportList.get(1).getCity(), false);
        }
    }

    // Xử lý lỗi khi tải sân bay
    private void handleAirportLoadError(Response<List<AirportDto>> response) {
        String errorMessage = "Không thể tải danh sách sân bay";
        if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }
        Log.e("SearchFlightActivity", "Lỗi tải sân bay: HTTP " + response.code());
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Thiết lập giá trị ban đầu
    private void setupInitialValues() {
        tvAdultCount.setText(String.valueOf(adultCount));
        tvClass.setText(seatClassNames[0]);

        // Thiết lập ngày khởi hành và ngày về
        Calendar calendar = Calendar.getInstance();
        tvDepartureDate.setText(dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        tvReturnDate.setText(dateFormat.format(calendar.getTime()));
    }

    // Giảm số lượng người lớn
    private void onBtnMinusAdultClick(View view) {
        updatePassengerCount(false);
    }

    // Tăng số lượng người lớn
    private void onBtnPlusAdultClick(View view) {
        updatePassengerCount(true);
    }

    // Chọn ngày khởi hành
    private void onTvDepartureDateClick(View view) {
        showDatePicker(true);
    }

    // Chọn ngày về
    private void onTvReturnDateClick(View view) {
        showDatePicker(false);
    }

    // Chọn hạng ghế
    private void onTvClassClick(View view) {
        showClassSelection();
    }

    // Thực hiện tìm kiếm
    private void onBtnSearchFlightsClick(View view) {
        performSearch();
    }

    // Cập nhật số lượng hành khách
    private void updatePassengerCount(boolean isIncrement) {
        if (isIncrement && adultCount < 9) {
            adultCount++;
        } else if (!isIncrement && adultCount > 1) {
            adultCount--;
        }
        tvAdultCount.setText(String.valueOf(adultCount));
    }

    // Hiển thị dialog chọn ngày
    private void showDatePicker(boolean isDeparture) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    String dateStr = dateFormat.format(selectedDate.getTime());
                    if (isDeparture) {
                        tvDepartureDate.setText(dateStr);
                    } else {
                        tvReturnDate.setText(dateStr);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    // Hiển thị dialog chọn hạng ghế
    private void showClassSelection() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn hạng ghế");
        builder.setItems(seatClassNames, (dialog, which) -> {
            selectedClassIndex = which;
            tvClass.setText(seatClassNames[which]);
        });
        builder.show();
    }

    // Thực hiện tìm kiếm chuyến bay
    private void performSearch() {
        String from = actvFrom.getText().toString().trim();
        String to = actvTo.getText().toString().trim();
        String departureDateStr = tvDepartureDate.getText().toString().trim();
        String returnDateStr = tvReturnDate.getText().toString().trim();
        String seatClass = tvClass.getText().toString().trim();

        // Validate dữ liệu nhập
        if (!validateSearchInput(from, to, departureDateStr, seatClass)) {
            return;
        }

        // Lấy mã sân bay
        String departureAirportCode, arrivalAirportCode;
        try {
            departureAirportCode = from.split(" - ")[0];
            arrivalAirportCode = to.split(" - ")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "Lựa chọn sân bay không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button trong khi tìm kiếm
        btnSearchFlights.setEnabled(false);
        btnSearchFlights.setText("Đang tìm kiếm...");

        try {
            // Tạo DTO tìm kiếm
            AdvancedFlightSearchDto searchDto = createSearchDto(
                    departureAirportCode, arrivalAirportCode,
                    departureDateStr, returnDateStr, seatClass);

            // Gọi API tìm kiếm
            Call<FlightSearchResultDto> call = searchApi.advancedSearch(searchDto);
            call.enqueue(new Callback<FlightSearchResultDto>() {
                @Override
                public void onResponse(Call<FlightSearchResultDto> call, Response<FlightSearchResultDto> response) {
                    // Enable lại button
                    btnSearchFlights.setEnabled(true);
                    btnSearchFlights.setText("Tìm chuyến bay");

                    if (response.isSuccessful() && response.body() != null) {
                        handleSearchSuccess(response.body());
                    } else {
                        handleSearchError(response);
                    }
                }

                @Override
                public void onFailure(Call<FlightSearchResultDto> call, Throwable t) {
                    btnSearchFlights.setEnabled(true);
                    btnSearchFlights.setText("Tìm chuyến bay");
                    Toast.makeText(SearchFlightActivity.this,"Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (ParseException e) {
            btnSearchFlights.setEnabled(true);
            btnSearchFlights.setText("Tìm chuyến bay");
            Toast.makeText(this, "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    // Validate dữ liệu đầu vào
    private boolean validateSearchInput(String from, String to, String departureDateStr, String seatClass) {
        if (from.isEmpty() || from.equals("Chọn sân bay khởi hành")) {
            Toast.makeText(this, "Vui lòng chọn sân bay khởi hành", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (to.isEmpty() || to.equals("Chọn sân bay đến")) {
            Toast.makeText(this, "Vui lòng chọn sân bay đến", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (departureDateStr.isEmpty() || departureDateStr.equals("Chọn ngày")) {
            Toast.makeText(this, "Vui lòng chọn ngày khởi hành", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (seatClass.isEmpty() || seatClass.equals("Chọn hạng ghế")) {
            Toast.makeText(this, "Vui lòng chọn hạng ghế", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Tạo DTO tìm kiếm
    private AdvancedFlightSearchDto createSearchDto(String departureCode, String arrivalCode,
                                                    String departureDateStr, String returnDateStr, String seatClass) throws ParseException {
        Date departureDate = dateFormat.parse(departureDateStr);
        Date returnDate = null;
        if (!returnDateStr.isEmpty() && !returnDateStr.equals("Chọn ngày")) {
            returnDate = dateFormat.parse(returnDateStr);
        }

        AdvancedFlightSearchDto searchDto = new AdvancedFlightSearchDto();
        searchDto.setDepartureAirportCode(departureCode);
        searchDto.setArrivalAirportCode(arrivalCode);
        searchDto.setDepartureDate(departureDate);
        searchDto.setReturnDate(returnDate);
        searchDto.setPassengers(adultCount);
        searchDto.setSeatClass(seatClass);

        return searchDto;
    }

    // Xử lý kết quả tìm kiếm thành công
    private void handleSearchSuccess(FlightSearchResultDto result) {
        String message = "Tìm thấy " + result.getOutboundFlights().size() + " chuyến bay đi";
        if (result.getReturnFlights() != null && !result.getReturnFlights().isEmpty()) {
            message += " và " + result.getReturnFlights().size() + " chuyến bay về";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        try {
            // Chuyển đến màn hình kết quả
            Gson gson = new Gson();
            String resultJson = gson.toJson(result);
            Intent intent = new Intent(this, FlightResultsActivity.class);
            intent.putExtra("search_results_json", resultJson);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("SearchFlightActivity", "Lỗi khi chuyển dữ liệu: ", e);
            Toast.makeText(this, "Lỗi hiển thị kết quả: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Xử lý lỗi tìm kiếm
    private void handleSearchError(Response<FlightSearchResultDto> response) {
        String errorMessage = "Tìm kiếm thất bại";
        if (response.code() == 400) {
            errorMessage = "Thông tin tìm kiếm không hợp lệ. Vui lòng kiểm tra lại.";
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay phù hợp.";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau.";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    // Tạo danh sách hạng ghế mẫu
    private List<SeatClass> getMockSeatClasses() {
        List<SeatClass> seatClasses = new ArrayList<>();
        seatClasses.add(new SeatClass(1, "ECONOMY", "Hạng phổ thông", new BigDecimal("1.00"), null));
        seatClasses.add(new SeatClass(2, "BUSINESS", "Hạng thương gia", new BigDecimal("2.50"), null));
        seatClasses.add(new SeatClass(3, "FIRST_CLASS", "Hạng nhất", new BigDecimal("4.00"), null));
        return seatClasses;
    }
}