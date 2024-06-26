package com.example.tables;

import android.os.Bundle;

import com.example.tables.model.ReservationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tables.databinding.ActivityNavigationBarBinding;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationBarActivity extends AppCompatActivity {

    private ActivityNavigationBarBinding binding;
    private ReservationManager reservationManager;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reservationManager = new ReservationManager(this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        binding = ActivityNavigationBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation_bar);
        getUserProfile(userId);
        NavigationUI.setupWithNavController(navView, navController);


    }

    private void getUserProfile(String userId) {
        reservationManager.checkUserType(userId, new ReservationManager.OnUserTypeRetrievedListener() {
            @Override
            public void onUserTypeRetrieved(String userType) {
                if (userType.equals("CustomerUser")) {

                } else if (userType.equals("RestaurantUser")) {
                    navView.getMenu().findItem(R.id.navigation_search).setEnabled(false);
                } else {
                    // Handle unknown user type
                }
            }
        });
    }

}