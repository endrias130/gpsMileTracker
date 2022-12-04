package com.example.gpsmiletracker;

public class myLocationPoints {

    String latitude, longitude;

    long time;


    public myLocationPoints(String latitude, String longitude, long time) {

        this.latitude = latitude;

        this.longitude = longitude;

        this.time = time;

    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
