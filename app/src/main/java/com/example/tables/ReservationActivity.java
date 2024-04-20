package com.example.tables;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tables.model.ReservationManager;
import com.example.tables.model.RestaurantUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.TimeZone;

public class ReservationActivity extends AppCompatActivity {
    private RestaurantUser restaurant;
    private TextView restaurantNameTextView;
    private NumberPicker numberOfPeoplePicker;
    private MaterialButton dateTimeButton;
    private MaterialButton reservationButton;

    private ReservationManager manager;
    private FirebaseAuth mAuth;

    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        restaurant = (RestaurantUser) getIntent().getSerializableExtra("restaurant");
        manager = new ReservationManager(this);
        mAuth = FirebaseAuth.getInstance();

        restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        numberOfPeoplePicker = findViewById(R.id.numberOfPeoplePicker);
        dateTimeButton = findViewById(R.id.dateTimeButton);
        reservationButton = findViewById(R.id.reservationButton);

        // Set the restaurant name
        restaurantNameTextView.setText(restaurant.getName());

        // Set up the number picker
        numberOfPeoplePicker.setMinValue(1);
        numberOfPeoplePicker.setMaxValue(10);
        numberOfPeoplePicker.setWrapSelectorWheel(false);

        // Set up the date and time button click listener
        dateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        // Set up the reservation button click listener
        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeReservation();
            }
        });
    }

    private void showDateTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        minDate.set(Calendar.MINUTE, 0);
        minDate.set(Calendar.SECOND, 0);
        minDate.set(Calendar.MILLISECOND, 0);

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(currentDate.getTimeInMillis())
                .setCalendarConstraints(constraints)
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                selectedDateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                selectedDateTime.setTimeInMillis(selection);

                showTimePicker();
            }
        });

        datePicker.show(getSupportFragmentManager(), "date_picker");
    }

    private void showTimePicker() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedHour = timePicker.getHour();
                int selectedMinute = timePicker.getMinute();

                selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedDateTime.set(Calendar.MINUTE, selectedMinute);

                String dateTimeString = DateFormat.format("dd/MM/yyyy hh:mm a", selectedDateTime).toString();
                dateTimeButton.setText(dateTimeString);
            }
        });

        timePicker.show(getSupportFragmentManager(), "time_picker");
    }

    private void makeReservation() {
        int numOfPeople = numberOfPeoplePicker.getValue();

        if (selectedDateTime == null) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        long startTime = selectedDateTime.getTimeInMillis();
        long endTime = startTime + (120 * 60 * 1000); // Add 1 hour to the start time
        String restaurantUid = restaurant.getUid();
        String customerUid = mAuth.getCurrentUser().getUid();
        manager.makeReservation(customerUid, restaurantUid, numOfPeople, startTime, endTime);

    }
}