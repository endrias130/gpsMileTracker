package com.example.gpsmiletracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class myInterface extends AppCompatActivity {

    LocationTrack locationTrack;

    LinearLayout startTrip, endTrip;
    JSONObject jsonObject;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;

    String userCountry, userAddress;

    String location = "";

    FirebaseAuth auth;

    DatabaseReference ref;

    int PERMISSIONS_REQUEST = 111;

    FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;

    public boolean requestingLocationUpdates;

    private BroadcastReceiver broadcastReceiver;
    private int cnt = 0;


    @Override
    protected void onResume() {
        super.onResume();

        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    cnt = cnt + 1;
                    
                    System.out.println("count: "+cnt+" Location\nlongitude point = "+intent.getExtras().get("longitude").toString()+"\nlatitude point = "+intent.getExtras().get("latitude").toString()+"\nCity/State = "+intent.getExtras().get("city").toString()+" "+intent.getExtras().get("state").toString()+"");

                    sendToFirebase(intent.getExtras().get("longitude").toString(), intent.getExtras().get("latitude").toString(), intent.getExtras().get("currentDate").toString(), cnt);

                    TextView xVal = (TextView) findViewById(R.id.xVal);
                    TextView yVal = (TextView) findViewById(R.id.yVa);
                    TextView cityVal = (TextView) findViewById(R.id.cityVal);

                    xVal.setText(intent.getExtras().get("longitude").toString());

                    yVal.setText(intent.getExtras().get("latitude").toString());

                    cityVal.setText(intent.getExtras().get("city").toString());

                }
            };
        }

        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));

    }


    private void sendToFirebase(String longitude, String latitude, String currentDate, int cnt) {

        //Will send to the Firebase Database under the User's ID

        ref.child("User").child(auth.getUid()+"").child("Locations").child(currentDate+"").child(cnt+"").child("x").setValue(longitude+"");

        ref.child("User").child(auth.getUid()+"").child("Locations").child(currentDate+"").child(cnt+"").child("y").setValue(latitude+"");

        Long currentTime = System.currentTimeMillis();

        ref.child("User").child(auth.getUid()+"").child("Locations").child(currentDate+"").child(cnt+"").child("time").setValue(currentTime+"");


    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinterface);

        auth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();


        startTrip = (LinearLayout) findViewById(R.id.startTrip);

        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEndButton();

                //tracking the user's geolocation data

                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);
            }
        });



        endTrip = (LinearLayout) findViewById(R.id.endTrip);

        endTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showStartButton();

                //no longer tracking the user's geolocation data

                if(broadcastReceiver != null) {

                    unregisterReceiver(broadcastReceiver);

                    Toast.makeText(myInterface.this, "Your trip has come to an end..", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    private void showStartButton() {

        endTrip.setVisibility(View.GONE);

        startTrip.setVisibility(View.VISIBLE);

    }


    private void showEndButton() {

        startTrip.setVisibility(View.GONE);

        endTrip.setVisibility(View.VISIBLE);

    }
    


/*
    private void setLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            return;
        }

        //Here we are collecting the user's location with the fusedLocationClient.
        // This is where the lattiude and longtide points are coming from

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

            Geocoder geocoder = new Geocoder(myInterface.this, Locale.getDefault());

            List<Address> addresses = null;

            try {


                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);


                if (addresses != null && addresses.size() > 0) {

                    //Here is the user's country
                    userCountry = addresses.get(0).getCountryName();

                    userAddress = addresses.get(0).getAddressLine(0);

                    //Here is the user's city
                    String city = addresses.get(0).getLocality();

                    //Here is the user's state
                    String state = addresses.get(0).getAdminArea();


                    Address fetchedAddress = addresses.get(0);

                    // getCountryCode from Address
                    String countryCode = fetchedAddress.getCountryCode();





                }


            } catch (IOException e) {


                //  e.printStackTrace();
            }


            // use this current location
        });

    }

*/

    
    @Override
    protected void onDestroy() {
       
        super.onDestroy();

        Toast.makeText(this, "destroyed..", Toast.LENGTH_SHORT).show();
        
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }

    }
    
    
}
