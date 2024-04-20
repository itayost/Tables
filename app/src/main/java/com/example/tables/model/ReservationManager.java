package com.example.tables.model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tables.ReservationActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ReservationManager {
    private DatabaseReference restaurantsRef;
    private DatabaseReference customersRef;

    private DatabaseReference reservationsRef;

    private Context context;

    public ReservationManager(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        restaurantsRef = database.getReference("restaurants");
        customersRef = database.getReference("customers");
        reservationsRef = database.getReference("reservations");
        this.context = context;
    }

    public void makeReservation(String customerUid, String restaurantUid, int numOfPeople, long start, long end) {
        DatabaseReference customerRef = customersRef.child(customerUid);
        DatabaseReference restaurantRef = restaurantsRef.child(restaurantUid);

        customerRef.get().addOnSuccessListener(customerSnapshot -> {
            restaurantRef.get().addOnSuccessListener(restaurantSnapshot -> {
                if (customerSnapshot.exists() && restaurantSnapshot.exists()) {
                        CustomerUser customer = customerSnapshot.getValue(CustomerUser.class);
                        RestaurantUser restaurant = restaurantSnapshot.getValue(RestaurantUser.class);

                        assert customer != null;
                        assert restaurant != null;
                        String restaurantName = restaurant.getName();
                        String customerName = customer.getFullName();
                        String reservationKey = reservationsRef.push().getKey();
                        Reservation reservation = new Reservation(reservationKey, start, end, restaurantName, numOfPeople, customerName, customerUid, restaurantUid);
                        reservationsRef.child(reservationKey).setValue(reservation)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Reservation successful", Toast.LENGTH_SHORT).show();
                                    if (context instanceof ReservationActivity) {
                                        ((ReservationActivity) context).finish();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Reservation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                }
                else {
                    Toast.makeText(context, "Restaurant or Customer not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void cancelReservation(String reservationUid, OnReservationCancelledListener listener) {
        DatabaseReference reservationRef = reservationsRef.child(reservationUid);
        reservationRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onReservationCancelled();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onReservationCancelled();
                    }
                });
    }

    public void getCustomerReservations(String customerUid, OnReservationsRetrievedListener listener) {
        reservationsRef.orderByChild("customerUid").equalTo(customerUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Reservation> reservations = new ArrayList<>();
                        for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                            Reservation reservation = reservationSnapshot.getValue(Reservation.class);
                            reservations.add(reservation);
                        }
                        Collections.sort(reservations);
                        listener.onReservationsRetrieved(reservations);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onReservationsRetrieved(null);
                    }
                });
    }
    public void getRestaurantReservations(String restaurantUid, OnReservationsRetrievedListener listener) {
        reservationsRef.orderByChild("restaurantUid").equalTo(restaurantUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Reservation> reservations = new ArrayList<>();
                        for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                            Reservation reservation = reservationSnapshot.getValue(Reservation.class);
                            reservations.add(reservation);
                        }
                        Collections.sort(reservations);
                        listener.onReservationsRetrieved(reservations);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onReservationsRetrieved(null);
                    }
                });
    }
    public void checkUserType(String userId, OnUserTypeRetrievedListener listener) {

        customersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onUserTypeRetrieved("CustomerUser");
                } else {
                    restaurantsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                listener.onUserTypeRetrieved("RestaurantUser");
                            } else {
                                listener.onUserTypeRetrieved("Unknown");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            listener.onUserTypeRetrieved("Unknown");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onUserTypeRetrieved("Unknown");
            }
        });
    }

    public void getCustomerDetails(String userId, OnCustomerRetrievedListener listener) {
        DatabaseReference customerRef = customersRef.child(userId);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CustomerUser customer = dataSnapshot.getValue(CustomerUser.class);
                    listener.onCustomerRetrieved(customer);
                } else {
                    listener.onCustomerRetrieved(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onCustomerRetrieved(null);
            }
        });
    }

    public void getRestaurantDetails(String userId, OnRestaurantRetrievedListener listener) {
        DatabaseReference restaurantRef = restaurantsRef.child(userId);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    RestaurantUser restaurant = dataSnapshot.getValue(RestaurantUser.class);
                    listener.onRestaurantRetrieved(restaurant);
                } else {
                    listener.onRestaurantRetrieved(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onRestaurantRetrieved(null);
            }
        });
    }

    public interface OnCustomerRetrievedListener {
        void onCustomerRetrieved(CustomerUser customer);
    }

    public interface OnRestaurantRetrievedListener {
        void onRestaurantRetrieved(RestaurantUser restaurant);
    }

    public interface OnUserTypeRetrievedListener {
        void onUserTypeRetrieved(String userType);
    }

    public interface OnReservationsRetrievedListener {
        void onReservationsRetrieved(List<Reservation> reservations);
    }

    public interface OnReservationCancelledListener {
        void onReservationCancelled();
    }
}