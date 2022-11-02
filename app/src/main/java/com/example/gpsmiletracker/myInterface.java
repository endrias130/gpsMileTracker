package com.example.gpsmiletracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

                setLocation();

            }
        });


    }


    public void stopLocationUpdates() {
        requestingLocationUpdates = false;
        LocationServices.getFusedLocationProviderClient(getApplication())
                .removeLocationUpdates(locationCallback);
    }



    private void setLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

            String json_servers = "{\"AD\":\"west_1_european\",\"AE\":\"west_asia\",\"AF\":\"west_asia\",\"AG\":\"east_us\",\"AI\":\"east_us\",\"AL\":\"east_european\",\"AM\":\"east_european\",\"AN\":\"east_us\",\"AO\":\"africa\",\"AQ\":\"africa\",\"AR\":\"south_america\",\"AS\":\"south_east_asia\",\"AT\":\"east_european\",\"AU\":\"south_east_asia\",\"AW\":\"east_us\",\"AZ\":\"east_european\",\"BA\":\"east_european\",\"BB\":\"east_us\",\"BD\":\"west_asia\",\"BE\":\"west_2_european\",\"BF\":\"africa\",\"BG\":\"east_european\",\"BH\":\"south_east_asia\",\"BI\":\"africa\",\"BJ\":\"africa\",\"BM\":\"east_us\",\"BN\":\"south_east_asia\",\"BO\":\"south_america\",\"BR\":\"south_america\",\"BS\":\"east_us\",\"BT\":\"south_east_asia\",\"BW\":\"africa\",\"BY\":\"east_european\",\"BZ\":\"south_america\",\"CA\":\"east_us\",\"CC\":\"south_east_asia\",\"CD\":\"africa\",\"CF\":\"africa\",\"CG\":\"africa\",\"CH\":\"west_1_european\",\"CI\":\"africa\",\"CK\":\"south_east_asia\",\"CL\":\"south_america\",\"CM\":\"africa\",\"CN\":\"north_east_asia\",\"CO\":\"south_america\",\"CR\":\"east_us\",\"CU\":\"east_us\",\"CV\":\"africa\",\"CX\":\"south_east_asia\",\"CY\":\"east_european\",\"CZ\":\"east_european\",\"DE\":\"west_2_european\",\"DJ\":\"africa\",\"DK\":\"west_2_european\",\"DM\":\"east_us\",\"DO\":\"east_us\",\"DZ\":\"africa\",\"EC\":\"south_america\",\"EE\":\"east_european\",\"EG\":\"africa\",\"EH\":\"africa\",\"ER\":\"africa\",\"ES\":\"west_1_european\",\"ET\":\"africa\",\"FI\":\"west_2_european\",\"FJ\":\"south_east_asia\",\"FK\":\"south_america\",\"FM\":\"south_east_asia\",\"FO\":\"west_2_european\",\"FR\":\"west_1_european\",\"GA\":\"africa\",\"GB\":\"west_1_european\",\"GD\":\"east_us\",\"GE\":\"east_european\",\"GF\":\"south_america\",\"GG\":\"west_1_european\",\"GH\":\"africa\",\"GI\":\"west_1_european\",\"GL\":\"west_2_european\",\"GM\":\"africa\",\"GN\":\"africa\",\"GP\":\"east_us\",\"GQ\":\"africa\",\"GR\":\"east_european\",\"GS\":\"east_us\",\"GT\":\"south_america\",\"GU\":\"south_east_asia\",\"GW\":\"africa\",\"GY\":\"south_america\",\"HK\":\"north_east_asia\",\"HN\":\"south_america\",\"HR\":\"east_european\",\"HT\":\"east_us\",\"HU\":\"east_european\",\"ID\":\"south_east_asia\",\"IE\":\"west_1_european\",\"IL\":\"east_european\",\"IM\":\"west_1_european\",\"IN\":\"west_asia\",\"IO\":\"west_asia\",\"IQ\":\"east_european\",\"IR\":\"east_european\",\"IS\":\"west_1_european\",\"IT\":\"west_1_european\",\"JE\":\"west_1_european\",\"JM\":\"east_us\",\"JO\":\"east_european\",\"JP\":\"north_east_asia\",\"KE\":\"africa\",\"KG\":\"north_east_asia\",\"KH\":\"west_asia\",\"KI\":\"south_east_asia\",\"KM\":\"africa\",\"KN\":\"east_us\",\"KP\":\"north_east_asia\",\"KR\":\"north_east_asia\",\"KW\":\"west_asia\",\"KY\":\"east_us\",\"KZ\":\"north_east_asia\",\"LA\":\"south_east_asia\",\"LB\":\"east_european\",\"LC\":\"east_us\",\"LI\":\"west_2_european\",\"LK\":\"south_east_asia\",\"LR\":\"africa\",\"LS\":\"africa\",\"LT\":\"east_european\",\"LU\":\"west_2_european\",\"LV\":\"east_european\",\"LY\":\"africa\",\"MA\":\"africa\",\"MC\":\"west_1_european\",\"MD\":\"east_european\",\"ME\":\"east_european\",\"MG\":\"africa\",\"MH\":\"south_east_asia\",\"MK\":\"east_european\",\"ML\":\"africa\",\"MM\":\"west_asia\",\"MN\":\"north_east_asia\",\"MO\":\"north_east_asia\",\"MP\":\"south_east_asia\",\"MQ\":\"east_us\",\"MR\":\"africa\",\"MS\":\"east_us\",\"MT\":\"west_1_european\",\"MU\":\"africa\",\"MV\":\"west_asia\",\"MW\":\"africa\",\"MX\":\"south_america\",\"MY\":\"south_east_asia\",\"MZ\":\"africa\",\"NA\":\"africa\",\"NC\":\"south_east_asia\",\"NE\":\"africa\",\"NF\":\"south_east_asia\",\"NG\":\"africa\",\"NI\":\"south_america\",\"NL\":\"west_2_european\",\"NO\":\"west_2_european\",\"NP\":\"west_asia\",\"NR\":\"south_east_asia\",\"NU\":\"south_east_asia\",\"NZ\":\"south_east_asia\",\"OM\":\"east_european\",\"PA\":\"south_america\",\"PE\":\"south_america\",\"PF\":\"south_east_asia\",\"PG\":\"south_east_asia\",\"PH\":\"south_east_asia\",\"PK\":\"west_asia\",\"PL\":\"east_european\",\"PM\":\"west_us\",\"PN\":\"south_east_asia\",\"PR\":\"east_us\",\"PS\":\"east_european\",\"PT\":\"west_1_european\",\"PW\":\"south_east_asia\",\"PY\":\"south_america\",\"QA\":\"east_european\",\"RE\":\"africa\",\"RO\":\"east_european\",\"RS\":\"east_european\",\"RU\":\"east_european\",\"RW\":\"africa\",\"SA\":\"east_european\",\"SB\":\"south_east_asia\",\"SC\":\"africa\",\"SD\":\"africa\",\"SE\":\"west_2_european\",\"SG\":\"south_east_asia\",\"SH\":\"africa\",\"SI\":\"east_european\",\"SJ\":\"west_1_european\",\"SK\":\"east_european\",\"SL\":\"africa\",\"SM\":\"west_1_european\",\"SN\":\"africa\",\"SO\":\"africa\",\"SR\":\"south_america\",\"ST\":\"africa\",\"SV\":\"south_america\",\"SY\":\"east_european\",\"SZ\":\"africa\",\"TC\":\"east_us\",\"TD\":\"africa\",\"TF\":\"south_east_asia\",\"TG\":\"africa\",\"TH\":\"west_asia\",\"TJ\":\"north_east_asia\",\"TK\":\"south_east_asia\",\"TM\":\"east_european\",\"TN\":\"africa\",\"TO\":\"south_east_asia\",\"TR\":\"south_east_asia\",\"TT\":\"east_us\",\"TV\":\"south_east_asia\",\"TW\":\"south_east_asia\",\"TZ\":\"africa\",\"UA\":\"east_european\",\"UG\":\"africa\",\"US\":\"east_us\",\"UY\":\"south_america\",\"UZ\":\"east_european\",\"VC\":\"east_us\",\"VE\":\"south_america\",\"VG\":\"east_us\",\"VI\":\"east_us\",\"VN\":\"west_asia\",\"VU\":\"south_east_asia\",\"WF\":\"south_east_asia\",\"WS\":\"south_east_asia\",\"YE\":\"east_european\",\"YT\":\"africa\",\"ZA\":\"africa\",\"ZM\":\"africa\",\"ZW\":\"africa\"}";

            //String json_continents = "{\"AD\":\"Europe\",\"AE\":\"Asia\",\"AF\":\"Asia\",\"AG\":\"North America\",\"AI\":\"North America\",\"AL\":\"Europe\",\"AM\":\"Asia\",\"AN\":\"North America\",\"AO\":\"Africa\",\"AQ\":\"Antarctica\",\"AR\":\"South America\",\"AS\":\"Australia\",\"AT\":\"Europe\",\"AU\":\"Australia\",\"AW\":\"North America\",\"AZ\":\"Asia\",\"BA\":\"Europe\",\"BB\":\"North America\",\"BD\":\"Asia\",\"BE\":\"Europe\",\"BF\":\"Africa\",\"BG\":\"Europe\",\"BH\":\"Asia\",\"BI\":\"Africa\",\"BJ\":\"Africa\",\"BM\":\"North America\",\"BN\":\"Asia\",\"BO\":\"South America\",\"BR\":\"South America\",\"BS\":\"North America\",\"BT\":\"Asia\",\"BW\":\"Africa\",\"BY\":\"Europe\",\"BZ\":\"North America\",\"CA\":\"North America\",\"CC\":\"Asia\",\"CD\":\"Africa\",\"CF\":\"Africa\",\"CG\":\"Africa\",\"CH\":\"Europe\",\"CI\":\"Africa\",\"CK\":\"Australia\",\"CL\":\"South America\",\"CM\":\"Africa\",\"CN\":\"Asia\",\"CO\":\"South America\",\"CR\":\"North America\",\"CU\":\"North America\",\"CV\":\"Africa\",\"CX\":\"Asia\",\"CY\":\"Asia\",\"CZ\":\"Europe\",\"DE\":\"Europe\",\"DJ\":\"Africa\",\"DK\":\"Europe\",\"DM\":\"North America\",\"DO\":\"North America\",\"DZ\":\"Africa\",\"EC\":\"South America\",\"EE\":\"Europe\",\"EG\":\"Africa\",\"EH\":\"Africa\",\"ER\":\"Africa\",\"ES\":\"Europe\",\"ET\":\"Africa\",\"FI\":\"Europe\",\"FJ\":\"Australia\",\"FK\":\"South America\",\"FM\":\"Australia\",\"FO\":\"Europe\",\"FR\":\"Europe\",\"GA\":\"Africa\",\"GB\":\"Europe\",\"GD\":\"North America\",\"GE\":\"Asia\",\"GF\":\"South America\",\"GG\":\"Europe\",\"GH\":\"Africa\",\"GI\":\"Europe\",\"GL\":\"North America\",\"GM\":\"Africa\",\"GN\":\"Africa\",\"GP\":\"North America\",\"GQ\":\"Africa\",\"GR\":\"Europe\",\"GS\":\"Antarctica\",\"GT\":\"North America\",\"GU\":\"Australia\",\"GW\":\"Africa\",\"GY\":\"South America\",\"HK\":\"Asia\",\"HN\":\"North America\",\"HR\":\"Europe\",\"HT\":\"North America\",\"HU\":\"Europe\",\"ID\":\"Asia\",\"IE\":\"Europe\",\"IL\":\"Asia\",\"IM\":\"Europe\",\"IN\":\"Asia\",\"IO\":\"Asia\",\"IQ\":\"Asia\",\"IR\":\"Asia\",\"IS\":\"Europe\",\"IT\":\"Europe\",\"JE\":\"Europe\",\"JM\":\"North America\",\"JO\":\"Asia\",\"JP\":\"Asia\",\"KE\":\"Africa\",\"KG\":\"Asia\",\"KH\":\"Asia\",\"KI\":\"Australia\",\"KM\":\"Africa\",\"KN\":\"North America\",\"KP\":\"Asia\",\"KR\":\"Asia\",\"KW\":\"Asia\",\"KY\":\"North America\",\"KZ\":\"Asia\",\"LA\":\"Asia\",\"LB\":\"Asia\",\"LC\":\"North America\",\"LI\":\"Europe\",\"LK\":\"Asia\",\"LR\":\"Africa\",\"LS\":\"Africa\",\"LT\":\"Europe\",\"LU\":\"Europe\",\"LV\":\"Europe\",\"LY\":\"Africa\",\"MA\":\"Africa\",\"MC\":\"Europe\",\"MD\":\"Europe\",\"ME\":\"Europe\",\"MG\":\"Africa\",\"MH\":\"Australia\",\"MK\":\"Europe\",\"ML\":\"Africa\",\"MM\":\"Asia\",\"MN\":\"Asia\",\"MO\":\"Asia\",\"MP\":\"Australia\",\"MQ\":\"North America\",\"MR\":\"Africa\",\"MS\":\"North America\",\"MT\":\"Europe\",\"MU\":\"Africa\",\"MV\":\"Asia\",\"MW\":\"Africa\",\"MX\":\"North America\",\"MY\":\"Asia\",\"MZ\":\"Africa\",\"NA\":\"Africa\",\"NC\":\"Australia\",\"NE\":\"Africa\",\"NF\":\"Australia\",\"NG\":\"Africa\",\"NI\":\"North America\",\"NL\":\"Europe\",\"NO\":\"Europe\",\"NP\":\"Asia\",\"NR\":\"Australia\",\"NU\":\"Australia\",\"NZ\":\"Australia\",\"OM\":\"Asia\",\"PA\":\"North America\",\"PE\":\"South America\",\"PF\":\"Australia\",\"PG\":\"Australia\",\"PH\":\"Asia\",\"PK\":\"Asia\",\"PL\":\"Europe\",\"PM\":\"North America\",\"PN\":\"Australia\",\"PR\":\"North America\",\"PS\":\"Asia\",\"PT\":\"Europe\",\"PW\":\"Australia\",\"PY\":\"South America\",\"QA\":\"Asia\",\"RE\":\"Africa\",\"RO\":\"Europe\",\"RS\":\"Europe\",\"RU\":\"Europe\",\"RW\":\"Africa\",\"SA\":\"Asia\",\"SB\":\"Australia\",\"SC\":\"Africa\",\"SD\":\"Africa\",\"SE\":\"Europe\",\"SG\":\"Asia\",\"SH\":\"Africa\",\"SI\":\"Europe\",\"SJ\":\"Europe\",\"SK\":\"Europe\",\"SL\":\"Africa\",\"SM\":\"Europe\",\"SN\":\"Africa\",\"SO\":\"Africa\",\"SR\":\"South America\",\"ST\":\"Africa\",\"SV\":\"North America\",\"SY\":\"Asia\",\"SZ\":\"Africa\",\"TC\":\"North America\",\"TD\":\"Africa\",\"TF\":\"Antarctica\",\"TG\":\"Africa\",\"TH\":\"Asia\",\"TJ\":\"Asia\",\"TK\":\"Australia\",\"TM\":\"Asia\",\"TN\":\"Africa\",\"TO\":\"Australia\",\"TR\":\"Asia\",\"TT\":\"North America\",\"TV\":\"Australia\",\"TW\":\"Asia\",\"TZ\":\"Africa\",\"UA\":\"Europe\",\"UG\":\"Africa\",\"US\":\"North America\",\"UY\":\"South America\",\"UZ\":\"Asia\",\"VC\":\"North America\",\"VE\":\"South America\",\"VG\":\"North America\",\"VI\":\"North America\",\"VN\":\"Asia\",\"VU\":\"Australia\",\"WF\":\"Australia\",\"WS\":\"Australia\",\"YE\":\"Asia\",\"YT\":\"Africa\",\"ZA\":\"Africa\",\"ZM\":\"Africa\",\"ZW\":\"Africa\"}";


            try {

                jsonObject = new JSONObject(json_servers);

                Geocoder geocoder = new Geocoder(myInterface.this, Locale.getDefault());

                List<Address> addresses = null;

                try {


                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);


                    if (addresses != null && addresses.size() > 0) {

                        userCountry = addresses.get(0).getCountryName();
                        userAddress = addresses.get(0).getAddressLine(0);

                        String city = addresses.get(0).getLocality();

                        String state = addresses.get(0).getAdminArea();


                        Address fetchedAddress = addresses.get(0);

                        // getCountryCode from Address
                        String countryCode = fetchedAddress.getCountryCode();

                        String serverName = jsonObject.getString(countryCode);

                        

                    }


                } catch (IOException e) {


                    //  e.printStackTrace();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }



            // use this current location
        });

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                          //  Toast.makeText(myInterface.this, location.getLongitude()+" "+location.getLatitude()+"", Toast.LENGTH_SHORT).show();

                        }
                    }
                });



/*
        ActivityCompat.requestPermissions(myInterface.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



        try {


            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Toast.makeText(this, gps_loc+"  "+network_loc+"", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longitude = 0.0;
        }

*/

       // Toast.makeText(myInterface.this, latitude+" "+longitude+"", Toast.LENGTH_SHORT).show();


        /*

            try {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(myInterface.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(myInterface.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(myInterface.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                return;
            }


            try {

                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gps_loc != null) {
                final_loc = gps_loc;
                latitude = final_loc.getLatitude();
                longitude = final_loc.getLongitude();
            }
            else if (network_loc != null) {
                final_loc = network_loc;
                latitude = final_loc.getLatitude();
                longitude = final_loc.getLongitude();
            }
            else {
                latitude = 0.0;
                longitude = 0.0;
            }


            Toast.makeText(locationTrack, latitude+" "+longitude+"", Toast.LENGTH_SHORT).show();


            Geocoder geocoder = new Geocoder(myInterface.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {

                userCountry = addresses.get(0).getCountryName();
                userAddress = addresses.get(0).getAddressLine(0);

                String city = addresses.get(0).getLocality();

                String state = addresses.get(0).getAdminArea();


                Address fetchedAddress = addresses.get(0);

                // getCountryCode from Address
                String countryCode = fetchedAddress.getCountryCode();




            }


            else {

            }

        }


        catch (Exception e) {

            ref.child("AMO").setValue(e.getLocalizedMessage()+"");

         //   Toast.makeText(myInterface.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }


         */

    }





    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(myInterface.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

}
