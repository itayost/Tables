package com.example.tables.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tables.LoginActivity;
import com.example.tables.R;
import com.example.tables.WelcomeActivity;
import com.example.tables.databinding.FragmentUserProfileBinding;
import com.example.tables.model.CustomerUser;
import com.example.tables.model.ReservationManager;
import com.example.tables.model.RestaurantUser;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private ReservationManager reservationManager;
    private CustomerUser customer;
    private RestaurantUser restaurant;
    private MaterialButton logOut;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the user profile layout
        View profileView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return profileView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reservationManager = new ReservationManager(getContext());
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        logOut = view.findViewById(R.id.btn_log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(requireContext(), "Sign out succeed.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), WelcomeActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Retrieve the user profile based on user type
        getUserProfile(userId);
    }

    private void getUserProfile(String userId) {
        reservationManager.checkUserType(userId, new ReservationManager.OnUserTypeRetrievedListener() {
            @Override
            public void onUserTypeRetrieved(String userType) {
                if (userType.equals("CustomerUser")) {
                    retrieveCustomerProfile(userId);
                } else if (userType.equals("RestaurantUser")) {
                    retrieveRestaurantProfile(userId);
                } else {
                    // Handle unknown user type
                    showErrorMessage("Unknown user type.");
                }
            }
        });
    }

    private void retrieveCustomerProfile(String userId) {
        reservationManager.getCustomerDetails(userId, new ReservationManager.OnCustomerRetrievedListener() {
            @Override
            public void onCustomerRetrieved(CustomerUser retrievedCustomer) {
                if (retrievedCustomer != null) {
                    customer = retrievedCustomer;
                    updateCustomerProfileUI();
                } else {
                    // Handle the case when customer details are not found
                    showErrorMessage("Failed to retrieve customer profile.");
                }
            }
        });
    }

    private void retrieveRestaurantProfile(String userId) {
        reservationManager.getRestaurantDetails(userId, new ReservationManager.OnRestaurantRetrievedListener() {
            @Override
            public void onRestaurantRetrieved(RestaurantUser retrievedRestaurant) {
                if (retrievedRestaurant != null) {
                    restaurant = retrievedRestaurant;
                    updateRestaurantProfileUI();
                } else {
                    // Handle the case when restaurant details are not found
                    showErrorMessage("Failed to retrieve restaurant profile.");
                }
            }
        });
    }

    private void updateCustomerProfileUI() {
        TextView textFirstName = getView().findViewById(R.id.text_first_name);
        TextView textLastName = getView().findViewById(R.id.text_last_name);

        // Update the UI with customer details
        textFirstName.setText(String.format("First Name: %s", customer.getFirstName()));
        textLastName.setText(String.format("Last Name: %s", customer.getLastName()));
    }

    private void updateRestaurantProfileUI() {
        TextView textRestaurantName = getView().findViewById(R.id.text_first_name);
        TextView textRestaurantAddress = getView().findViewById(R.id.text_last_name);

        // Update the UI with restaurant details
        textRestaurantName.setText(String.format("Restaurant Name: %s", restaurant.getName()));
        textRestaurantAddress.setText(String.format("Address: %s", restaurant.getAddress()));
    }

    private void showErrorMessage(String message) {
        // Display an error message to the user
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
