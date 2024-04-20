package com.example.tables.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tables.R;
import com.example.tables.model.CustomerUser;
import com.example.tables.model.Reservation;
import com.example.tables.model.ReservationManager;
import com.example.tables.model.RestaurantUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeFragment extends Fragment implements MyReservationRecyclerViewAdapter.OnReservationClickListener{
    private RecyclerView recyclerViewReservations;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    ReservationManager reservationManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewReservations = view.findViewById(R.id.recyclerViewReservations);
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(getActivity()));
        reservationManager = new ReservationManager(getContext());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchReservations();
    }

    private void fetchReservations() {
        String userId = mAuth.getCurrentUser().getUid();

        reservationManager.checkUserType(userId, new ReservationManager.OnUserTypeRetrievedListener() {
            @Override
            public void onUserTypeRetrieved(String userType) {
                if (userType.equals("CustomerUser")) {
                    fetchCustomerReservations(userId);
                } else if (userType.equals("RestaurantUser")) {
                    fetchRestaurantReservations(userId);
                } else {
                    // Handle the case when the user type is unknown
                    //showErrorMessage("Unknown user type");
                }
            }
        });


    }


    private void fetchCustomerReservations(String customerId){
        DatabaseReference customerRef = mDatabase.getReference("customers").child(customerId);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CustomerUser customer = dataSnapshot.getValue(CustomerUser.class);
                    if (customer != null) {

                        reservationManager.getCustomerReservations(customerId, new ReservationManager.OnReservationsRetrievedListener() {
                            @Override
                            public void onReservationsRetrieved(List<Reservation> reservations) {
                                if (reservations != null) {
                                    displayReservations(reservations);
                                } else {
                                    // Handle the case when no reservations are found or an error occurred
                                    //showErrorMessage("Failed to fetch reservations");
                                }
                            }
                        });
                    }
                } else {
                    // Customer not found
                    //showErrorMessage("Customer not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                //showErrorMessage("Failed to fetch customer: " + databaseError.getMessage());
            }
        });
    }

    private void fetchRestaurantReservations(String restaurantId){
        DatabaseReference restaurantRef = mDatabase.getReference("restaurants").child(restaurantId);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    RestaurantUser restaurant = dataSnapshot.getValue(RestaurantUser.class);
                    if (restaurant != null) {

                        reservationManager.getRestaurantReservations(restaurantId, new ReservationManager.OnReservationsRetrievedListener() {
                            @Override
                            public void onReservationsRetrieved(List<Reservation> reservations) {
                                if (reservations != null) {
                                    displayReservations(reservations);
                                } else {
                                    // Handle the case when no reservations are found or an error occurred
                                    //showErrorMessage("Failed to fetch reservations");
                                }
                            }
                        });
                    }
                } else {
                    // Customer not found
                    //showErrorMessage("Customer not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                //showErrorMessage("Failed to fetch customer: " + databaseError.getMessage());
            }
        });
    }

    private void displayReservations(List<com.example.tables.model.Reservation> reservations) {
        MyReservationRecyclerViewAdapter adapter = new MyReservationRecyclerViewAdapter(reservations);
        adapter.setOnReservationClickListener(this);
        recyclerViewReservations.setAdapter(adapter);
    }

    @Override
    public void onCancelReservationClick(String reservationUid) {
        reservationManager.cancelReservation(reservationUid, new ReservationManager.OnReservationCancelledListener() {
            @Override
            public void onReservationCancelled() {
                fetchReservations();
            }
        });
    }
}