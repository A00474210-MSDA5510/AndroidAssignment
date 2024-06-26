package com.example.hotelreservationsystem_java;

public class HotelListData {

    String hotel_name;
    String price;
    String availability;
    Integer id;

    public HotelListData(String hotel_name, String price, String availability, Integer id) {
        this.hotel_name = hotel_name;
        this.price = price;
        this.availability = availability;
        this.id = id;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setHotel_id() {this.id = id;}

    public Integer getHotel_id() {return id;}

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}