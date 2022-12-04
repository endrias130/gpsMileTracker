package com.example.gpsmiletracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GPS_Service extends Service {

    FusedLocationProviderClient fusedLocationClient;

    ArrayList<mylocation_Obj> myLocation;

    long appTime = 900000000;

    Runnable myrunnable;

    Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        myLocation = new ArrayList<mylocation_Obj>();

        if (fusedLocationClient != null) {

            new CountDownTimer(appTime, 15000) {

                public void onTick(long millisUntilFinished) {

                    findLocation();
                }

                public void onFinish() {

                }

            }.start();

        }

    }


    private void findLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnSuccessListener(location -> {

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            List<Address> addresses = null;

            try {

                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);


                if (addresses != null && addresses.size() > 0) {

                    //Here is the user's country
                    String userCountry = addresses.get(0).getCountryName();

                    //Here is the user's city
                    String city = addresses.get(0).getLocality();

                    //Here is the user's state
                    String state = addresses.get(0).getAdminArea();

                    Address fetchedAddress = addresses.get(0);

                    submitData(location, city, state);

                }


            } catch (IOException e) {

            }

        });


    }


    private void submitData(Location location, String city, String state) {

        Date todayDate = Calendar.getInstance().getTime();

        SimpleDateFormat month = new SimpleDateFormat("MM");
        String monthString = month.format(todayDate);

        SimpleDateFormat day = new SimpleDateFormat("dd");
        String dayString = day.format(todayDate);

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        String yearString = year.format(todayDate);

        String myCurrentDate = monthString+"_"+dayString+"_"+yearString+"";

        mylocation_Obj myObj = new mylocation_Obj(location.getLongitude(), location.getLatitude(), myCurrentDate);

        myLocation.add(0, myObj);

        Intent i = new Intent("location_update");
        i.putExtra("coordinates", location.getLongitude() + " " + location.getLatitude());
        i.putExtra("longitude", location.getLongitude()+"");
        i.putExtra("latitude", location.getLatitude()+"");
        i.putExtra("pushData", "true");
        i.putExtra("currentDate", myCurrentDate+"");
        i.putExtra("city", city);
        i.putExtra("state", state);

        sendBroadcast(i);

    }


    @Override
    public void onDestroy()  {

        super.onDestroy();

        if (fusedLocationClient != null) {

            fusedLocationClient = null;

            handler.removeCallbacks(myrunnable);

        }

    }



}
