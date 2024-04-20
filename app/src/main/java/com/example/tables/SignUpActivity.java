package com.example.tables;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tables.model.CustomerUser;
import com.example.tables.model.RestaurantUser;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView userName, password;
    private RadioGroup userType;
    private MaterialButton signUp;

    private String email, pswd;
    private int userTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        findView();
        setClicks();
    }

    private void findView(){
        signUp = findViewById(R.id.buttonSignUp);
        userName = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        userType = findViewById(R.id.radioGroupUserType);
    }

    private void setClicks() {
        signUp.setOnClickListener(v -> {
            if (isFormValid()) {
                email = userName.getText().toString().trim();
                pswd = password.getText().toString().trim();
                userTypeId = userType.getCheckedRadioButtonId();
                registerUser(email, pswd);

            }
        });
    }

    private void WriteUserToDataBase(String userId){
        if (userTypeId == R.id.radioButtonCustomer) {
            CustomerUser customerUser = new CustomerUser(email, mAuth.getCurrentUser().getUid());
            mDatabase.child("customers").child(userId).setValue(customerUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Customer registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, CustomerSignUpActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else if (userTypeId == R.id.radioButtonRestaurant) {
            RestaurantUser restaurantUser = new RestaurantUser(email, mAuth.getCurrentUser().getUid());
            mDatabase.child("restaurants").child(userId).setValue(restaurantUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Restaurant registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, RestaurantSignUpActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        WriteUserToDataBase(userId);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isFormValid() {
        boolean isValid = true;

        if (userName.getText().toString().trim().isEmpty()) {
            userName.setError(getString(R.string.error_username_required));
            isValid = false;
        }

        if (password.getText().toString().trim().isEmpty()) {
            password.setError(getString(R.string.error_password_required));
            isValid = false;
        }

        if (userType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.error_user_type_required, Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private String getUserTypeName(int userTypeId) {
        if (userTypeId == R.id.radioButtonCustomer) {
            return "customer";
        } else if (userTypeId == R.id.radioButtonRestaurant) {
            return "restaurant";
        }
        return null;
    }
}

