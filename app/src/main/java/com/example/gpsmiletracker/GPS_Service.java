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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GPS_Service extends Service {

    FusedLocationProviderClient fusedLocationClient;

    ArrayList<mylocation_Obj> myLocation;


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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            return;
        }

        //Here we are collecting the user's location with the fusedLocationClient.
        // This is where the lattiude and longtide points are coming from

        if(fusedLocationClient != null) {

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

                        String userAddress = addresses.get(0).getAddressLine(0);

                        //Here is the user's city
                        String city = addresses.get(0).getLocality();

                        //Here is the user's state
                        String state = addresses.get(0).getAdminArea();


                        Address fetchedAddress = addresses.get(0);

                        // getCountryCode from Address
                        String countryCode = fetchedAddress.getCountryCode();


                        if(myLocation.isEmpty())  {

                            mylocation_Obj myObj = new mylocation_Obj(location.getLongitude(), location.getLatitude(), userCountry, state, city);

                            myLocation.add(0, myObj);

                        }

                        else {

                            if(farEnough(location.getLongitude(), location.getLatitude()) == true) {

                                myLocation.clear();

                                mylocation_Obj myObj = new mylocation_Obj(location.getLongitude(), location.getLatitude(), userCountry, state, city);

                                myLocation.add(0, myObj);

                                System.out.println("==============location=======================");
                                Log.i("location ",location.toString());
                                System.out.println(location.toString());
                                Intent i = new Intent("location_update");
                                i.putExtra("coordinates", location.getLongitude() + " " + location.getLatitude());

                                sendBroadcast(i);

                            }

                        }



                        System.out.println("==============location=======================");
                        Log.i("location ",location.toString());
                        System.out.println(location.toString());
                        Intent i = new Intent("location_update");
                        i.putExtra("coordinates", location.getLongitude() + " " + location.getLatitude());
                        i.putExtra("longitude", location.getLongitude()+"");
                        i.putExtra("latitude", location.getLatitude()+"");


                        new CountDownTimer(10000, 500) {

                            public void onTick(long millisUntilFinished) {

                                sendBroadcast(i);

                            }

                            public void onFinish() {

                            }

                        }.start();




                    }


                } catch (IOException e) {

                }

            });


        }

    }

    private boolean farEnough(double longitude, double latitude) {

        //if the user is at least 200 yards away from their original spot return true.


        return false;

    }

    @Override
    public void onDestroy()  {

        super.onDestroy();

        if (fusedLocationClient != null) {

            fusedLocationClient = null;

        }

    }


}
