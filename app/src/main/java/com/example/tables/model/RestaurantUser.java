package com.example.tables.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantUser extends User implements Serializable {

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String uri;

    private String Uid;

    public RestaurantUser(){}

    public RestaurantUser(String userName, String uid) {
        super(userName);
        this.Uid = uid;
    }


    public void setAddress(String address, Context context) {
        this.address = address;
        convertAddressToLatLng(address, context);
    }

    private void convertAddressToLatLng(String address, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressResult = addresses.get(0);
                latitude = addressResult.getLatitude();
                longitude = addressResult.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
