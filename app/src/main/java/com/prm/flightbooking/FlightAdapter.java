package com.prm.flightbooking;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        // Hiển thị thông tin chuyến bay lên các TextView
        holder.tvFlightNumber.setText(flight.getFlightNumber());
        holder.tvAirline.setText(flight.getAirlineName());
        holder.tvDepartureTime.setText(timeFormat.format(flight.getDepartureTime()));
        holder.tvArrivalTime.setText(timeFormat.format(flight.getArrivalTime()));

        // Hiển thị ngày tháng năm bay
        holder.tvFlightDate.setText(dateFormat.format(flight.getDepartureTime()));

        // Lấy mã sân bay từ chuỗi mô tả (ví dụ: "Sân bay Nội Bài (HAN)" -> "HAN")
        String departureAirport = flight.getDepartureAirport().contains("(")
                ? flight.getDepartureAirport().split(" \\(")[1].replace(")", "")
                : flight.getDepartureAirport();
        String arrivalAirport = flight.getArrivalAirport().contains("(")
                ? flight.getArrivalAirport().split(" \\(")[1].replace(")", "")
                : flight.getArrivalAirport();

        holder.tvDepartureAirport.setText(departureAirport);
        holder.tvArrivalAirport.setText(arrivalAirport);

        // Hiển thị giá vé với định dạng tiền tệ
        holder.tvPrice.setText(String.format("%s VND", currencyFormat.format(flight.getBasePrice())));

        // Hiển thị trạng thái chuyến bay với màu sắc phù hợp
        String status = flight.getStatus();
        String statusText = convertStatusToVietnamese(status);
        holder.tvStatus.setText(statusText);
        
        // Đặt màu cho status badge
        int textColor = Color.WHITE;
        int bgColor;
        if ("CANCELLED".equalsIgnoreCase(status)) {
            bgColor = Color.parseColor("#F44336"); // Red
        } else if ("DELAYED".equalsIgnoreCase(status)) {
            bgColor = Color.parseColor("#FF9800"); // Orange
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            bgColor = Color.parseColor("#757575"); // Gray
        } else {
            // SCHEDULED, PREPARING, DEPARTED
            bgColor = Color.parseColor("#4CAF50"); // Green
        }
        holder.tvStatus.setTextColor(textColor);
        holder.tvStatus.setBackgroundColor(bgColor);

        // Gán sự kiện click cho item nếu listener không null
        if (listener != null) {
            // Chặn đặt vé cho các chuyến bay: DELAYED, COMPLETED, CANCELLED, PREPARING, DEPARTED
            if ("COMPLETED".equalsIgnoreCase(status)
                    || "DELAYED".equalsIgnoreCase(status)
                    || "CANCELLED".equalsIgnoreCase(status)
                    || "PREPARING".equalsIgnoreCase(status)
                    || "DEPARTED".equalsIgnoreCase(status)) {
                // Hiển thị thông báo hợp lý tùy theo trạng thái
                holder.itemView.setOnClickListener(v -> {
                    String message = getStatusMessage(status);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Không thể đặt vé");
                    builder.setMessage(message);
                    builder.setPositiveButton("Đã hiểu", null);
                    builder.show();
                });
                // Làm mờ item để thể hiện không thể đặt
                holder.itemView.setAlpha(0.6f);
            } else {
                // Cho phép click bình thường cho SCHEDULED
                holder.itemView.setOnClickListener(v -> listener.onFlightClick(flight.getFlightId()));
                holder.itemView.setAlpha(1.0f);
            }
        } else {
            holder.itemView.setOnClickListener(null); // Tránh lỗi nếu listener null
        }
    }

    @Override
    public int getItemCount() {
        return flightList != null ? flightList.size() : 0;
    }

    // Định dạng ngày tháng năm
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d 'Tháng' M, yyyy", new Locale("vi", "VN"));

    // ViewHolder chứa các view trong item_flight
    static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlightNumber, tvAirline, tvDepartureTime, tvArrivalTime, tvDepartureAirport, tvArrivalAirport, tvPrice, tvStatus, tvFlightDate;

        FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFlightNumber = itemView.findViewById(R.id.tv_flight_number);
            tvAirline = itemView.findViewById(R.id.tv_airline);
            tvDepartureTime = itemView.findViewById(R.id.tv_departure_time);
            tvArrivalTime = itemView.findViewById(R.id.tv_arrival_time);
            tvDepartureAirport = itemView.findViewById(R.id.tv_departure_airport);
            tvArrivalAirport = itemView.findViewById(R.id.tv_arrival_airport);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvFlightDate = itemView.findViewById(R.id.tv_flight_date);
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
