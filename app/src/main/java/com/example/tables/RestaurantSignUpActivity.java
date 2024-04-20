package com.example.tables;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import com.example.tables.model.RestaurantUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RestaurantSignUpActivity extends AppCompatActivity {

    private EditText restaurantNameEditText, streetEditText, numberEditText, cityEditText, stateEditText;
    private Button confirmButton, uploadPhoto;
    private ImageView photoImageView;
    Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean hasPic = false;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        findView();
        setClicks();
    }

    private void findView(){
        restaurantNameEditText = findViewById(R.id.restaurantNameEditText);
        streetEditText = findViewById(R.id.streetEditText);
        numberEditText = findViewById(R.id.numberEditText);
        cityEditText = findViewById(R.id.cityEditText);
        stateEditText = findViewById(R.id.stateEditText);
        confirmButton = findViewById(R.id.confirmButton);
        photoImageView = findViewById(R.id.restaurantImageView);
        uploadPhoto = findViewById(R.id.uploadPhotoButton);
    }

    private void setClicks(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restaurantName = restaurantNameEditText.getText().toString().trim();
                String street = streetEditText.getText().toString().trim();
                String number = numberEditText.getText().toString().trim();
                String city = cityEditText.getText().toString().trim();
                String state = stateEditText.getText().toString().trim();

                if (hasPic && !restaurantName.isEmpty() && !street.isEmpty() && !number.isEmpty() && !city.isEmpty() && !state.isEmpty()) {
                    String address = street + " " + number + ", " + city + ", " + state;
                    saveRestaurantDetails(restaurantName, address);
                } else {
                    Toast.makeText(RestaurantSignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RestaurantSignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(RestaurantSignUpActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    // Permission is already granted, open the photo picker
                    openPhotoPicker();
                }
            }
        });
    }

    private void openPhotoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, open the photo picker
                openPhotoPicker();
            } else {
                // Permission is denied, show a message to the user
                Toast.makeText(RestaurantSignUpActivity.this, "Permission denied to access photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            photoImageView.setImageURI(imageUri);
            hasPic = true;
        }
    }


    private void saveRestaurantDetails(String restaurantName, String address) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("restaurants").child(userId);

        RestaurantUser restaurantUser = new RestaurantUser(mAuth.getCurrentUser().getEmail(), userId);
        restaurantUser.setName(restaurantName);
        restaurantUser.setAddress(address, this);
        restaurantUser.setUri(imageUri.toString());

        userRef.setValue(restaurantUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RestaurantSignUpActivity.this, "Restaurant details saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RestaurantSignUpActivity.this, NavigationBarActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RestaurantSignUpActivity.this, "Failed to save restaurant details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}