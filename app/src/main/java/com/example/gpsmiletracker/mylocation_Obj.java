package com.example.gpsmiletracker;

public class mylocation_Obj {

    double longtiude, latitude;

    String currentDate;

    public mylocation_Obj(double longtiude, double latitude, String currentDate) {
        this.longtiude = longtiude;
        this.latitude = latitude;

        this.currentDate = currentDate;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
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

}
