package com.example.tables.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerUser extends User implements Serializable {
    private List<Reservation> reservations;
    private String firstName;
    private String lastName;
    private String Uid;

    public CustomerUser() {
        // Empty constructor required for Firebase deserialization
    }
    public CustomerUser(String email, String uid) {
        super(email);
        this.reservations = new ArrayList<>();
        this.Uid = uid;
    }

    public String getUid(){return Uid;}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        return getFirstName() + " " + getLastName();
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }


}