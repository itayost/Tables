package com.example.tables;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tables.model.CustomerUser;
import com.example.tables.model.Reservation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CustomerSignUpActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText;
    private Button confirmButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();

                if (!firstName.isEmpty() && !lastName.isEmpty()) {
                    saveCustomerDetails(firstName, lastName);
                } else {
                    Toast.makeText(CustomerSignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveCustomerDetails(String firstName, String lastName) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("customers").child(userId);
        CustomerUser customerUser = new CustomerUser(mAuth.getCurrentUser().getEmail(), userId);
        customerUser.setFirstName(firstName);
        customerUser.setLastName(lastName);

        userRef.setValue(customerUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CustomerSignUpActivity.this, "Customer details saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CustomerSignUpActivity.this, NavigationBarActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CustomerSignUpActivity.this, "Failed to save customer details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}