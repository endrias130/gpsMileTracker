package com.example.gpsmiletracker;

public class mylocation_Obj {

    double longtiude, latitude;

    String country, state ,city;

    public mylocation_Obj(double longtiude, double latitude, String country, String city, String state) {
        this.longtiude = longtiude;
        this.latitude = latitude;
        this.country = country;
        this.city = city;
        this.state = state;

    }


    public double getLongtiude() {
        return longtiude;
    }

    public void setLongtiude(double longtiude) {
        this.longtiude = longtiude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
