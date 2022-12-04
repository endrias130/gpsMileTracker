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
import android.widget.ImageView;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    LinearLayout startTrip, endTrip;

    ImageView settingz;

    FirebaseAuth auth;

    ArrayList<String> totalWeeklyData;

    String calculatedMiles = "", weekly_calculatedMiles = "";

    DatabaseReference ref;

    private BroadcastReceiver broadcastReceiver;

    TextView dailyTxtView, weeklyTxtView;

    private int cnt = 0;

    double dailyMiles = 0;

    double weeklyMiles = 0;

    double totalWeekMiles = 0;

    ArrayList<myLocationPoints> dailyList, weeklyList;


    @Override
    protected void onResume()  {
        super.onResume();

        if(broadcastReceiver == null)  {

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    sendToFirebase(intent.getExtras().get("longitude").toString(), intent.getExtras().get("latitude").toString(), intent.getExtras().get("currentDate").toString(), cnt);

                    TextView cityVal = (TextView) findViewById(R.id.cityVal);

                    cityVal.setText(intent.getExtras().get("city").toString());

                    cnt = cnt + 1;

                    getDailyMiles();

                    getWeeklyMiles();

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

        dailyList = new ArrayList<myLocationPoints>();

        totalWeeklyData = new ArrayList<String>();

        weeklyList = new ArrayList<myLocationPoints>();

        startTrip = (LinearLayout) findViewById(R.id.startTrip);

        dailyTxtView = (TextView) findViewById(R.id.xVal);

        weeklyTxtView = (TextView) findViewById(R.id.yVa);

        settingz = (ImageView) findViewById(R.id.settingzIcon);

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



        getDailyMiles();

        getWeeklyMiles();


        settingz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(myInterface.this, settingzpage.class));

            }
        });


        ref.child("User").child(auth.getUid()+"").child("Username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    TextView myTitlez = (TextView) findViewById(R.id.myTitlez);

                    myTitlez.setText(snapshot.getValue().toString()+"'s Miles");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getDailyMiles() {

        Date todayDate = Calendar.getInstance().getTime();

        SimpleDateFormat month = new SimpleDateFormat("MM");
        String monthString = month.format(todayDate);

        SimpleDateFormat day = new SimpleDateFormat("dd");
        String dayString = day.format(todayDate);

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        String yearString = year.format(todayDate);


        String myCurrentDate = monthString+"_"+dayString+"_"+yearString+"";

        retrieveDailyData(myCurrentDate);

    }

    private void retrieveDailyData(String myCurrentDate) {

        ref.child("User").child(auth.getUid()+"").child("Locations").child(myCurrentDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    for(DataSnapshot mySnapshot : snapshot.getChildren()) {

                        if(mySnapshot.child("x").exists() && mySnapshot.child("y").exists() && mySnapshot.child("time").exists()){

                            myLocationPoints newPoint = new myLocationPoints(mySnapshot.child("x").getValue().toString(), mySnapshot.child("y").getValue().toString(), Long.parseLong(mySnapshot.child("time").getValue().toString()));

                            dailyList.add(newPoint);

                            if(dailyList.size() == 2) {

                                dailyMiles = dailyMiles + findMiles(dailyList.get(0), dailyList.get(1));

                                dailyList.clear();

                            }


                        }

                    }


                    String finalMiles = dailyMiles+"";

                    String rounded = finalMiles.substring(0, 3);

                    calculatedMiles = rounded;

                    int roundedNum = (int) Double.parseDouble(calculatedMiles);

                    dailyTxtView.setText(roundedNum+" mil");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private double findMiles(myLocationPoints newPoint, myLocationPoints newPoint2) {

        double lat1 = Double.parseDouble(newPoint.latitude);
        double long1 = Double.parseDouble(newPoint.longitude);

        double lat2 = Double.parseDouble(newPoint2.latitude);
        double long2 = Double.parseDouble(newPoint2.longitude);


        double newDistance = distance(lat1, long1, lat2, long2);

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(long1);


        Location endPoint=new Location("locationB");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(long2);


        double accuracy = .72 * newDistance;

        return accuracy;

    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        int Radius = 6371;// radius of earth in Km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        return Radius * c;
    }


    private void getWeeklyMiles() {


        ArrayList<String> allWeekz = new ArrayList<String>();

        for(int daysIndx = 1; daysIndx < 8; daysIndx++) {

            Calendar lastSeven = Calendar.getInstance();

            lastSeven.add(Calendar.DAY_OF_YEAR, -daysIndx);

            Date newDate = lastSeven.getTime();


            SimpleDateFormat month = new SimpleDateFormat("MM");
            String monthString = month.format(newDate);


            SimpleDateFormat dayz = new SimpleDateFormat("dd");

            String dayStringz = dayz.format(newDate);


            SimpleDateFormat year = new SimpleDateFormat("yyyy");

            String yearString = year.format(newDate);


            String updatedDate = monthString+"_"+dayStringz+"_"+yearString+"";

            allWeekz.add(updatedDate);

        }

        totalWeeklyData = allWeekz;

        retrieveWeeklyData();

    }




    private void retrieveWeeklyData() {

        ref.child("User").child(auth.getUid()+"").child("Locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    DataSnapshot mySnap = snapshot;

                    for(int mypos = 0; mypos < totalWeeklyData.size(); mypos++) {

                        if(mySnap.child(totalWeeklyData.get(mypos)).exists()) {

                            for (DataSnapshot mysubSnap : mySnap.child(totalWeeklyData.get(mypos)).getChildren()) {

                                if (mysubSnap.child("x").exists() && mysubSnap.child("y").exists() && mysubSnap.child("time").exists()) {

                                    myLocationPoints newPoint = new myLocationPoints(mysubSnap.child("x").getValue().toString(), mysubSnap.child("y").getValue().toString(), Long.parseLong(mysubSnap.child("time").getValue().toString()));

                                    weeklyList.add(newPoint);

                                    if (weeklyList.size() == 2) {

                                        weeklyMiles = weeklyMiles + findMiles(weeklyList.get(0), weeklyList.get(1));

                                        weeklyList.clear();

                                    }

                                }

                            }


                            String finalMiles = weeklyMiles+"";

                            String rounded = finalMiles.substring(0, 3);

                            double finalNum = Double.parseDouble(rounded);

                            totalWeekMiles = totalWeekMiles+finalNum;

                            weekly_calculatedMiles = totalWeekMiles+"";

                        }

                    }


                    weeklyTxtView.setText(weekly_calculatedMiles+" mil");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    

    @Override
    protected void onDestroy() {
       
        super.onDestroy();

        Toast.makeText(this, "destroyed..", Toast.LENGTH_SHORT).show();
        
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }

    }
    
    
}
