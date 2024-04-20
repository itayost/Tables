package com.example.tables.ui.home;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tables.R;
import com.example.tables.model.Reservation;
import com.example.tables.ui.home.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.tables.databinding.FragmentReservationBinding;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyReservationRecyclerViewAdapter extends RecyclerView.Adapter<MyReservationRecyclerViewAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private OnReservationClickListener onReservationClickListener;


    public MyReservationRecyclerViewAdapter(List<Reservation> items) {
        reservations = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_reservation, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation);
        holder.buttonCancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onReservationClickListener != null) {
                    String reservationUid = reservation.getReservationUid();
                    onReservationClickListener.onCancelReservationClick(reservationUid);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (reservations != null) {
            return reservations.size();
        }
        return 0;
    }
    public void setOnReservationClickListener(OnReservationClickListener listener) {
        onReservationClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewRestaurantName;
        public final TextView textViewDateTime;
        public final TextView textViewCount;
        public final TextView textViewCustomerName;
        public final MaterialButton buttonCancelReservation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRestaurantName = itemView.findViewById(com.example.tables.R.id.Restaurant_name);
            textViewDateTime = itemView.findViewById(R.id.start_time);
            textViewCustomerName = itemView.findViewById(R.id.Customer_name);
            textViewCount = itemView.findViewById(R.id.count);
            buttonCancelReservation = itemView.findViewById(R.id.cancel_reservation_button);
        }

        public void bind(Reservation reservation) {
            textViewRestaurantName.setText(String.format("Restaurant: %s", reservation.getRestaurantName()));
            textViewDateTime.setText(reservation.getTimeToString(reservation.getStartTimestamp()));
            textViewCount.setText(String.format("Table for: %s", String.valueOf(reservation.getNumOfPeople())));
            textViewCustomerName.setText(String.format("Customer: %s", reservation.getCustomerName()));
        }
    }

    public interface OnReservationClickListener {
        void onCancelReservationClick(String reservationUid);
    }
}