package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.FlightApiEndpoint;
import com.prm.flightbooking.dto.booking.PassengerInfoDto;
import com.prm.flightbooking.dto.seat.SeatMapDto;
import com.prm.flightbooking.models.SeatClass;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseSeatsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnConfirmSeats;
    private Spinner spinnerSeatClass;
    private TextInputEditText etPassengerCount;
    private LinearLayout llPassengerDetails;
    private ProgressBar progressBar;
    private TextView tvFlightInfo, tvSeatClassPrice;
    private FlightApiEndpoint flightApi;
    private SharedPreferences sharedPreferences;
    private int flightId;
    private int userId;
    private List<PassengerInfoDto> passengerDetails;
    private List<SeatClass> seatClasses;
    private BigDecimal basePrice; // Store the flight's base price
    private NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seats);

        flightApi = ApiServiceProvider.getFlightApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        passengerDetails = new ArrayList<>();
        seatClasses = new ArrayList<>();

        // Retrieve basePrice from Intent safely
        try {
            String basePriceStr = getIntent().getStringExtra("basePrice");
            basePrice = (basePriceStr != null && !basePriceStr.isEmpty()) ? new BigDecimal(basePriceStr) : null;
        } catch (NumberFormatException e) {
            basePrice = null;
        }

        if (!validateUserAndFlight()) {
            return;
        }

        bindingView();
        bindingAction();
        setupBackPressHandler();
        fetchFlightInfo();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        btnConfirmSeats = findViewById(R.id.btn_confirm_seats);
        spinnerSeatClass = findViewById(R.id.spinner_seat_class);
        etPassengerCount = findViewById(R.id.et_passenger_count);
        llPassengerDetails = findViewById(R.id.ll_passenger_details);
        progressBar = findViewById(R.id.progress_bar);
        tvFlightInfo = findViewById(R.id.tv_flight_info);
        tvSeatClassPrice = findViewById(R.id.tv_seat_class_price);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> showExitDialog());
        btnConfirmSeats.setOnClickListener(v -> onConfirmSeatsClick());
        etPassengerCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updatePassengerFields();
                updateSeatClassPrice(spinnerSeatClass.getSelectedItemPosition());
            }
        });
        spinnerSeatClass.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateSeatClassPrice(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    private boolean validateUserAndFlight() {
        flightId = getIntent().getIntExtra("flightId", -1);
        if (flightId == -1) {
            Toast.makeText(this, "Mã chuyến bay không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        sharedPreferences.edit().putInt("flightId", flightId).apply();
        userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return false;
        }

        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(this, "Giá cơ bản của chuyến bay không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        return true;
    }

    private void setupSeatClassSpinner() {
        String[] seatClassNames = new String[seatClasses.size()];
        for (int i = 0; i < seatClasses.size(); i++) {
            seatClassNames[i] = seatClasses.get(i).getClassDescription();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seatClassNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeatClass.setAdapter(adapter);
        updateSeatClassPrice(0); // Set initial price
    }

    private void fetchFlightInfo() {
        progressBar.setVisibility(View.VISIBLE);
        Call<SeatMapDto> call = flightApi.getSeatMap(flightId, userId);
        call.enqueue(new Callback<SeatMapDto>() {
            @Override
            public void onResponse(Call<SeatMapDto> call, Response<SeatMapDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    SeatMapDto seatMap = response.body();
                    tvFlightInfo.setText("Chuyến bay: " + seatMap.getFlightNumber() + " (" + seatMap.getAircraftModel() + ")");
                    seatClasses = getMockSeatClasses();
                    setupSeatClassSpinner();
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<SeatMapDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChooseSeatsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSeatClassPrice(int position) {
        String countStr = etPassengerCount.getText().toString().trim();
        int passengerCount;
        try {
            passengerCount = countStr.isEmpty() ? 1 : Integer.parseInt(countStr);
            if (passengerCount <= 0 || passengerCount > 10) {
                passengerCount = 1; // Default to 1 if invalid
            }
        } catch (NumberFormatException e) {
            passengerCount = 1; // Default to 1 if parsing fails
        }

        if (position >= 0 && position < seatClasses.size() && basePrice != null && basePrice.compareTo(BigDecimal.ZERO) > 0) {
            SeatClass selectedSeatClass = seatClasses.get(position);
            BigDecimal finalPrice = basePrice.multiply(selectedSeatClass.getPriceMultiplier());
            BigDecimal totalPrice = finalPrice.multiply(new BigDecimal(passengerCount));
            tvSeatClassPrice.setText(String.format("Tổng giá (%d vé): %s VND", passengerCount, currencyFormat.format(totalPrice)));
        } else {
            tvSeatClassPrice.setText("Tổng giá: N/A");
        }
    }

    private void updatePassengerFields() {
        llPassengerDetails.removeAllViews();
        passengerDetails.clear();

        String countStr = etPassengerCount.getText().toString().trim();
        if (countStr.isEmpty()) return;

        int count;
        try {
            count = Integer.parseInt(countStr);
            if (count <= 0 || count > 10) {
                Toast.makeText(this, "Số lượng hành khách phải từ 1 đến 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng hành khách không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            for (int i = 0; i < count; i++) {
                LinearLayout passengerLayout = new LinearLayout(this);
                passengerLayout.setOrientation(LinearLayout.VERTICAL);
                passengerLayout.setPadding(16, 8, 16, 8);

                TextView tvPassenger = new TextView(this);
                tvPassenger.setText("Hành khách " + (i + 1));
                tvPassenger.setTextSize(16);
                tvPassenger.setTextColor(getResources().getColor(android.R.color.black));
                passengerLayout.addView(tvPassenger);

                TextInputLayout tilName = new TextInputLayout(this);
                tilName.setHint("Tên hành khách");
                tilName.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                tilName.setBoxStrokeColor(getResources().getColor(R.color.travel_orange));
                TextInputEditText etName = new TextInputEditText(this);
                etName.setId(View.generateViewId());
                tilName.addView(etName);
                passengerLayout.addView(tilName);

                TextInputLayout tilIdNumber = new TextInputLayout(this);
                tilIdNumber.setHint("Số CMND/CCCD (9-12 số, tùy chọn)");
                tilIdNumber.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                tilIdNumber.setBoxStrokeColor(getResources().getColor(R.color.travel_orange));
                TextInputEditText etIdNumber = new TextInputEditText(this);
                etIdNumber.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                etIdNumber.setId(View.generateViewId());
                tilIdNumber.addView(etIdNumber);
                passengerLayout.addView(tilIdNumber);

                llPassengerDetails.addView(passengerLayout);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tạo giao diện hành khách: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void onConfirmSeatsClick() {
        String countStr = etPassengerCount.getText().toString().trim();
        if (countStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số lượng hành khách", Toast.LENGTH_SHORT).show();
            return;
        }

        int passengerCount;
        try {
            passengerCount = Integer.parseInt(countStr);
            if (passengerCount <= 0 || passengerCount > 10) {
                Toast.makeText(this, "Số lượng hành khách phải từ 1 đến 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng hành khách không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (llPassengerDetails.getChildCount() == 0) {
            Toast.makeText(this, "Vui lòng cập nhật thông tin hành khách", Toast.LENGTH_SHORT).show();
            updatePassengerFields();
            return;
        }

        if (llPassengerDetails.getChildCount() != passengerCount) {
            Toast.makeText(this, "Số lượng hành khách không khớp, vui lòng cập nhật lại", Toast.LENGTH_SHORT).show();
            updatePassengerFields();
            return;
        }

        passengerDetails.clear();
        for (int i = 0; i < llPassengerDetails.getChildCount(); i++) {
            LinearLayout passengerLayout = (LinearLayout) llPassengerDetails.getChildAt(i);
            TextInputLayout tilName = (TextInputLayout) passengerLayout.getChildAt(1);
            TextInputLayout tilIdNumber = (TextInputLayout) passengerLayout.getChildAt(2);
            TextInputEditText etName = (TextInputEditText) tilName.getEditText();
            TextInputEditText etIdNumber = (TextInputEditText) tilIdNumber.getEditText();

            if (etName == null || etIdNumber == null) {
                Toast.makeText(this, "Lỗi giao diện hành khách " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etName.getText().toString().trim();
            String idNumber = etIdNumber.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Tên hành khách " + (i + 1) + " không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!idNumber.isEmpty() && !idNumber.matches("\\d{9,12}")) {
                Toast.makeText(this, "Số CMND/CCCD của hành khách " + (i + 1) + " phải có 9-12 số", Toast.LENGTH_SHORT).show();
                return;
            }

            passengerDetails.add(new PassengerInfoDto(name, idNumber.isEmpty() ? null : idNumber));
        }

        // Validate duplicate CCCD/CMND when booking for 2 or more passengers
        if (passengerDetails.size() >= 2) {
            Set<String> seenIds = new HashSet<>();
            for (int i = 0; i < passengerDetails.size(); i++) {
                String id = passengerDetails.get(i).getPassengerIdNumber();
                if (id == null || id.trim().isEmpty()) continue; // optional field, skip empties
                if (!seenIds.add(id)) {
                    Toast.makeText(this, "Số CMND/CCCD của các hành khách không được trùng nhau", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        int seatClassId = getSeatClassId(spinnerSeatClass.getSelectedItem().toString());
        BigDecimal selectedSeatClassPrice = basePrice.multiply(seatClasses.get(spinnerSeatClass.getSelectedItemPosition()).getPriceMultiplier());
        // Navigate to ServiceSelectionActivity instead of directly to BookingFormActivity
        Intent intent = new Intent(this, ServiceSelectionActivity.class);
        intent.putExtra("flightId", flightId);
        intent.putExtra("seatClassId", seatClassId);
        intent.putExtra("passengerCount", passengerCount);
        intent.putExtra("passengerDetails", new ArrayList<>(passengerDetails));
        intent.putExtra("seatClassPrice", selectedSeatClassPrice);
        startActivity(intent);
    }

    private int getSeatClassId(String seatClassName) {
        switch (seatClassName) {
            case "Hạng Phổ Thông":
                return 1;
            case "Hạng Thương Gia":
                return 2;
            case "Hạng Nhất":
                return 3;
            default:
                return 1; // Default to Economy
        }
    }

    private void handleErrorResponse(Response<SeatMapDto> response) {
        String errorMessage = "Không thể tải thông tin chuyến bay";
        if (response.code() == 400) {
            errorMessage = "Yêu cầu không hợp lệ";
        } else if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn";
            redirectToLogin();
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thoát")
                .setMessage("Bạn có muốn thoát không? Thông tin chưa lưu sẽ bị mất.")
                .setPositiveButton("Có", (dialog, which) -> finish())
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private List<SeatClass> getMockSeatClasses() {
        List<SeatClass> seatClasses = new ArrayList<>();
        seatClasses.add(new SeatClass(1, "ECONOMY", "Hạng Phổ Thông", new BigDecimal("1.0"), null));
        seatClasses.add(new SeatClass(2, "BUSINESS", "Hạng Thương Gia", new BigDecimal("2.5"), null));
        seatClasses.add(new SeatClass(3, "FIRST_CLASS", "Hạng Nhất", new BigDecimal("4.0"), null));
        return seatClasses;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sharedPreferences.getBoolean("is_logged_in", false)) {
            redirectToLogin();
        }
    }
}
