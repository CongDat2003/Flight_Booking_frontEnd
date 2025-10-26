package com.prm.flightbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private List<FlightResponseDto> flights;

    public RecommendationAdapter(List<FlightResponseDto> flights) {
        this.flights = flights;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlightResponseDto flight = flights.get(position);
        holder.tvFlightNumber.setText(flight.getFlightNumber());
        holder.tvAirline.setText(flight.getAirlineName());
        holder.tvRoute.setText(flight.getDepartureAirport() + " → " + flight.getArrivalAirport());
        holder.tvPrice.setText("$" + flight.getBasePrice());
    }

    @Override
    public int getItemCount() {
        return flights != null ? flights.size() : 0;
    }

    public void updateFlights(List<FlightResponseDto> newFlights) {
        this.flights = newFlights;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlightNumber, tvAirline, tvRoute, tvPrice;

        ViewHolder(View itemView) {
            super(itemView);
            tvFlightNumber = itemView.findViewById(R.id.tv_flight_number);
            tvAirline = itemView.findViewById(R.id.tv_airline);
            tvRoute = itemView.findViewById(R.id.tv_route);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}