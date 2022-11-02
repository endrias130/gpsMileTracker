package com.example.gpsmiletracker;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LinearLayout google_signin, phone_signin;


    FirebaseAuth.AuthStateListener statt;

    DatabaseReference databaseReference;

    FirebaseAuth auth;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userCountry, userAddress;
    private String location = "North America";

    String prefernceLang = "";

    String bust = "";

    int PERMISSIONS_REQUEST = 111;

    String button_selection = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        google_signin = (LinearLayout) findViewById(R.id.google_signin);
        phone_signin = (LinearLayout) findViewById(R.id.phone_signin);

        auth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                button_selection = "google";

                setGPS_Permissions();

            }
        });


        phone_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                button_selection = "phone";

                setGPS_Permissions();

            }
        });


    }


    private void setGPS_Permissions() {


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        }



    }


    private void setUp() {


        statt = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser su = firebaseAuth.getCurrentUser();


                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());


                // Create and launch sign-in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build();
                signInLauncher.launch(signInIntent);


            }
        };


        auth.addAuthStateListener(statt);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(button_selection.equals("phone")) {

                    startActivity(new Intent(MainActivity.this, PhoneVerify.class));

                }

                else {

                    setUp();

                }


                // Permission ok. Do work.
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );



    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {

        IdpResponse response = result.getIdpResponse();



        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String emmail = user.getEmail();

            int index = emmail.indexOf("@");



            keepGoing();


            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }


    }




    private void keepGoing() {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String emmail = user.getEmail();

        int index = emmail.indexOf("@");

        String email_username = emmail.substring(0, index);


        Toast.makeText(this, "Your User ID is:  "+auth.getUid().toString(), Toast.LENGTH_SHORT).show();


        databaseReference.child("User").child(auth.getUid().toString()).child("id").setValue(auth.getUid()+"");

        databaseReference.child("User").child(auth.getUid().toString()).child("email_address").setValue(user.getEmail()+"");

        databaseReference.child("User").child(auth.getUid().toString()).child("login_type").setValue("email");


        startActivity(new Intent(MainActivity.this, myInterface.class));


    }




/*
    private void askPermission() {


        try {


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

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


            Toast.makeText(this, latitude+"", Toast.LENGTH_SHORT).show();

            if(button_selection.equals("google")) {

             //   setUp();

            }

            else  {

              //  startActivity(new Intent(MainActivity.this, PhoneVerify.class));

            }

           // Intent toThenNext = new Intent(MainActivity.this, .class);
           // toThenNext.putExtra("type", "emailVerification");

           // startActivity(toThenNext);


        }


        catch (Exception e) {

            Toast.makeText(this, e.getLocalizedMessage()+" ", Toast.LENGTH_SHORT).show();

            e.printStackTrace();

        }


    }
*/


}