package com.example.tables.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tables.R;
import com.example.tables.ReservationActivity;
import com.example.tables.model.RestaurantUser;
import androidx.appcompat.widget.SearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */

    public class SearchFragment extends Fragment implements MyRestaurantRecyclerViewAdapter.OnReservationClickListener {
        private RecyclerView recyclerViewSearchResults;
        private SearchView searchView;
        private MyRestaurantRecyclerViewAdapter searchAdapter;
        private List<RestaurantUser> allRestaurants;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_search, container, false);
            recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults);
            searchView = view.findViewById(R.id.searchView);
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Initialize the RecyclerView and adapter
            recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getActivity()));
            searchAdapter = new MyRestaurantRecyclerViewAdapter(new ArrayList<>());
            searchAdapter.setOnReservationClickListener(this);
            recyclerViewSearchResults.setAdapter(searchAdapter);

            // Get all restaurants from Firebase
            fetchAllRestaurants();

            // Set up search functionality
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchRestaurants(newText);
                    return true;
                }
            });
        }

    private void fetchAllRestaurants() {
        allRestaurants = new ArrayList<>();
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference("restaurants");
        restaurantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                    RestaurantUser restaurant = restaurantSnapshot.getValue(RestaurantUser.class);
                    allRestaurants.add(restaurant);
                }
                searchAdapter.updateData(allRestaurants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    private void searchRestaurants(String query) {
        List<RestaurantUser> searchResults = new ArrayList<>();

        if (allRestaurants != null && !allRestaurants.isEmpty()) {
            for (RestaurantUser restaurant : allRestaurants) {
                if (restaurant.getName().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(restaurant);
                }
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            searchAdapter.updateData(searchResults);
        }, 300);
    }

    @Override
    public void onReservationClick(RestaurantUser restaurant) {
        Intent intent = new Intent(getActivity(), ReservationActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }
}