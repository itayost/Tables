package com.example.tables.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Reservation implements Serializable, Comparable<Reservation> {
    private long startTimestamp;
    private long endTimestamp;
    private String customerName;
    private String restaurantName;

    private String reservationUid;
    private String restaurantUid;
    private String customerUid;
    private int numOfPeople;

    public Reservation() {
    }

    public Reservation(String reservationUid, long startTimestamp, long endTimestamp, String restaurantName, int numOfPeople, String customerName, String customerUid, String restaurantUid) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.restaurantName = restaurantName;
        this.customerName = customerName;
        this.numOfPeople = numOfPeople;
        this.customerUid = customerUid;
        this.restaurantUid = restaurantUid;
        this.reservationUid = reservationUid;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantUid() {
        return restaurantUid;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public String getTimeToString(long Timestamp){

        return Instant.ofEpochMilli(Timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yy HH:mm"));
    }

    public String getReservationUid() {
        return reservationUid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return startTimestamp == that.startTimestamp &&
                endTimestamp == that.endTimestamp &&
                numOfPeople == that.numOfPeople &&
                Objects.equals(restaurantName, that.restaurantName)&&
                Objects.equals(customerName, that.customerName);

    }

    @Override
    public int hashCode() {
        return Objects.hash(startTimestamp, endTimestamp, restaurantName, numOfPeople, customerName);
    }

    @Override
    public int compareTo(Reservation other){
        return Long.compare(this.getStartTimestamp(), other.getStartTimestamp());
    }
}