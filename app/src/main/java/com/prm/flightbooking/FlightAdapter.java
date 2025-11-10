package com.prm.flightbooking;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.prm.flightbooking.models.FlightInfo;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    // Danh sách các chuyến bay cần hiển thị
    private List<? extends FlightInfo> flightList;

    // Listener để xử lý sự kiện click vào từng chuyến bay
    private OnFlightClickListener listener;

    // Định dạng thời gian hiển thị giờ phút
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    // Định dạng số tiền (giá vé)
    private NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);

    // Interface để truyền sự kiện click ra bên ngoài
    public interface OnFlightClickListener {
        void onFlightClick(int flightId);
    }

    // Constructor nhận danh sách chuyến bay và listener
    public FlightAdapter(List<? extends FlightInfo> flightList, OnFlightClickListener listener) {
        this.flightList = flightList;
        this.listener = listener;
    }

    // Cập nhật danh sách chuyến bay mới và refresh RecyclerView
    public void setFlights(List<? extends FlightInfo> flights) {
        this.flightList = flights;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_flight cho mỗi item trong danh sách
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        // Lấy dữ liệu chuyến bay tại vị trí hiện tại
        FlightInfo flight = flightList.get(position);

        // Format Route: "Điểm đi (Mã) đến Điểm đến (Mã)" - Style ảnh 2
        String departureAirport = flight.getDepartureAirport();
        String arrivalAirport = flight.getArrivalAirport();
        
        // Extract airport code and city name
        String departureCity = extractCityName(departureAirport);
        String departureCode = extractAirportCode(departureAirport);
        String arrivalCity = extractCityName(arrivalAirport);
        String arrivalCode = extractAirportCode(arrivalAirport);
        
        String route = String.format("%s (%s) đến %s (%s)", 
            departureCity, departureCode, arrivalCity, arrivalCode);
        holder.tvRoute.setText(route);

        // Format Departure Date: "d Tháng M, yyyy" - Giữ format từ ảnh 1
        holder.tvDepartureDate.setText(dateFormat.format(flight.getDepartureTime()));

        // Giữ nguyên thông tin từ ảnh 1
        holder.tvFlightNumber.setText(flight.getFlightNumber());
        holder.tvAirline.setText(flight.getAirlineName());
        holder.tvDepartureTime.setText(timeFormat.format(flight.getDepartureTime()));
        holder.tvArrivalTime.setText(timeFormat.format(flight.getArrivalTime()));

        // Lấy mã sân bay từ chuỗi mô tả
        String departureCodeOnly = extractAirportCode(departureAirport);
        String arrivalCodeOnly = extractAirportCode(arrivalAirport);

        holder.tvDepartureAirport.setText(departureCodeOnly);
        holder.tvArrivalAirport.setText(arrivalCodeOnly);

        // Format Price: "XXX,XXX VND*" - Style ảnh 2
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = numberFormat.format(flight.getBasePrice());
        holder.tvPrice.setText(String.format("%s VND*", formattedPrice));
        
        // Status - chỉ hiển thị "Đã lên lịch" vì đã lọc ở BookingActivity
        if (holder.chipStatus != null) {
            holder.chipStatus.setText("Đã lên lịch");
            // Màu xanh lá cho trạng thái "Đã lên lịch"
            int chipBgColor = Color.parseColor("#E8F5E8"); // Light Green
            int chipStrokeColor = Color.parseColor("#4CAF50"); // Green
            int chipTextColor = Color.parseColor("#4CAF50");
            
            holder.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(chipBgColor));
            holder.chipStatus.setChipStrokeColor(android.content.res.ColorStateList.valueOf(chipStrokeColor));
            holder.chipStatus.setChipStrokeWidth(1.5f);
            holder.chipStatus.setTextColor(chipTextColor);
            holder.chipStatus.setChipMinHeight(24);
        }

        // Gán sự kiện click cho nút "Đặt ngay" - Giữ nguyên logic
        if (listener != null) {
            final int flightId = flight.getFlightId();
            holder.btnBookNow.setOnClickListener(v -> listener.onFlightClick(flightId));
        } else {
            holder.btnBookNow.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return flightList != null ? flightList.size() : 0;
    }

    // Định dạng ngày tháng năm
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d 'Tháng' M, yyyy", new Locale("vi", "VN"));

    // Helper method to extract city name from airport string
    private String extractCityName(String airportString) {
        if (airportString == null || airportString.isEmpty()) {
            return "N/A";
        }
        // Format: "Sân bay Nội Bài (HAN)" -> "Nội Bài"
        // or "HAN" -> "HAN"
        if (airportString.contains("(")) {
            String[] parts = airportString.split("\\(");
            if (parts.length > 0) {
                String cityPart = parts[0].trim();
                // Remove "Sân bay" prefix if exists
                if (cityPart.startsWith("Sân bay")) {
                    cityPart = cityPart.substring("Sân bay".length()).trim();
                }
                return cityPart;
            }
        }
        return airportString;
    }

    // Helper method to extract airport code
    private String extractAirportCode(String airportString) {
        if (airportString == null || airportString.isEmpty()) {
            return "N/A";
        }
        // Format: "Sân bay Nội Bài (HAN)" -> "HAN"
        if (airportString.contains("(") && airportString.contains(")")) {
            int start = airportString.indexOf("(");
            int end = airportString.indexOf(")");
            if (start < end) {
                return airportString.substring(start + 1, end).trim();
            }
        }
        // If no parentheses, assume the whole string is the code
        return airportString.length() <= 3 ? airportString : "N/A";
    }

    // ViewHolder chứa các view trong item_flight
    static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDepartureDate, tvFlightNumber, tvAirline;
        TextView tvDepartureTime, tvArrivalTime, tvDepartureAirport, tvArrivalAirport;
        TextView tvPrice;
        Chip chipStatus;
        Button btnBookNow;

        FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tv_route);
            tvDepartureDate = itemView.findViewById(R.id.tv_departure_date);
            tvFlightNumber = itemView.findViewById(R.id.tv_flight_number);
            tvAirline = itemView.findViewById(R.id.tv_airline);
            tvDepartureTime = itemView.findViewById(R.id.tv_departure_time);
            tvArrivalTime = itemView.findViewById(R.id.tv_arrival_time);
            tvDepartureAirport = itemView.findViewById(R.id.tv_departure_airport);
            tvArrivalAirport = itemView.findViewById(R.id.tv_arrival_airport);
            tvPrice = itemView.findViewById(R.id.tv_price);
            chipStatus = itemView.findViewById(R.id.chip_status);
            btnBookNow = itemView.findViewById(R.id.btn_book_now);
        }
    }

    private String convertStatusToVietnamese(String status) {
        if (status == null) return "";

        switch (status.toUpperCase(Locale.ROOT)) {
            case "SCHEDULED":
                return "Đã lên lịch";
            case "CONFIRMED":
                return "Đã xác nhận";
            case "CANCELLED":
                return "Đã hủy";
            case "DELAYED":
                return "Bị hoãn";
            case "COMPLETED":
                return "Đã hoàn thành";
            case "PREPARING":
                return "Chuẩn bị khởi hành";
            case "DEPARTED":
                return "Đã khởi hành";
            default:
                return status; // Trả về nguyên trạng nếu không có mapping
        }
    }

    // Trả về thông báo hợp lý cho từng trạng thái không thể đặt vé
    private String getStatusMessage(String status) {
        if (status == null) return "Chuyến bay này không thể đặt vé.";

        switch (status.toUpperCase(Locale.ROOT)) {
            case "CANCELLED":
                return "Chuyến bay này đã bị hủy. Vui lòng chọn chuyến bay khác hoặc liên hệ với chúng tôi để được hỗ trợ.";
            case "DELAYED":
                return "Chuyến bay này đang bị hoãn. Vui lòng chọn chuyến bay khác hoặc liên hệ với chúng tôi để biết thông tin cập nhật.";
            case "COMPLETED":
                return "Chuyến bay này đã hoàn thành. Vui lòng chọn chuyến bay khác.";
            case "PREPARING":
                return "Chuyến bay này đang chuẩn bị khởi hành và không thể đặt vé thêm. Vui lòng chọn chuyến bay khác.";
            case "DEPARTED":
                return "Chuyến bay này đã khởi hành. Vui lòng chọn chuyến bay khác.";
            default:
                return "Chuyến bay này hiện không thể đặt vé. Vui lòng chọn chuyến bay khác.";
        }
    }
}
