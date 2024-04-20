package com.example.tables.ui.search;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tables.R;
import com.example.tables.model.RestaurantUser;
import com.example.tables.placeholder.PlaceholderContent.PlaceholderItem;
import com.google.android.material.button.MaterialButton;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<MyRestaurantRecyclerViewAdapter.ViewHolder> {

    private final List<RestaurantUser> restaurants;
    private OnReservationClickListener reservationClickListener;

    public MyRestaurantRecyclerViewAdapter(List<RestaurantUser> items) {
        restaurants = items;
    }

    @NonNull
    @Override
    public MyRestaurantRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restaurant, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyRestaurantRecyclerViewAdapter.ViewHolder holder, int position) {
        if (restaurants != null && position >= 0 && position < restaurants.size()) {
            RestaurantUser restaurant = restaurants.get(position);
            holder.bind(restaurant);
            holder.reservationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reservationClickListener != null) {
                        reservationClickListener.onReservationClick(restaurant);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void updateData(List<RestaurantUser> searchResults) {
        restaurants.clear();
        restaurants.addAll(searchResults);
        notifyDataSetChanged();
    }
    public void setOnReservationClickListener(OnReservationClickListener listener) {
        reservationClickListener = listener;
    }

    public interface OnReservationClickListener {
        void onReservationClick(RestaurantUser restaurant);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView imageViewLogo;
        public final TextView textViewRestaurantName;
        public final TextView textViewAddress;
        public final MaterialButton reservationButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRestaurantName = itemView.findViewById(com.example.tables.R.id.Restaurant_name);
            textViewAddress = itemView.findViewById(R.id.Restaurant_address);
            imageViewLogo = itemView.findViewById(R.id.header_image);
            reservationButton = itemView.findViewById(R.id.Reservation_button);

        }

        public void bind(@NonNull RestaurantUser restaurant) {
            textViewRestaurantName.setText(restaurant.getName());
            textViewAddress.setText(restaurant.getAddress());
            imageViewLogo.setImageURI(Uri.parse(restaurant.getUri()));
        }
    }
}