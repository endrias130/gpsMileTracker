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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    LinearLayout submit;
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

                    System.out.println("==============location=======================");

                    System.out.println("count: "+cnt+"\n" +intent.getExtras().get("coordinates")+"\n");

                    sendToFirebase(intent.getExtras().get("longitude").toString(), intent.getExtras().get("latitude").toString());

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }


    private void sendToFirebase(String longitude, String latitude) {

        //Will send to the Firebase Database under the User's ID


    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinterface);

        auth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();


        submit = (LinearLayout) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);
               
                // setLocation();
            }
        });


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

        
        //locationTrack.stopListener();

    }
    
    
}
