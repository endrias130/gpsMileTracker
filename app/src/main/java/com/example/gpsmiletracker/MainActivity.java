package com.example.gpsmiletracker;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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


    @Override
    protected void onStart() {
        super.onStart();


        SharedPreferences languagePreference = getSharedPreferences("shared_preferences", MODE_PRIVATE);

        if(languagePreference.contains("Language")) {

            String lang = languagePreference.getString("Language", "");

            if(!prefernceLang.equals(lang)) {


                translateTextViews(lang);

                prefernceLang = lang;
            }

        }

    }


    private void translateTextViews(String lang) {

        if(lang.equals("Bengali/Bangla")) {

            lang = "BENGALI";

        }

        else if(lang.equals("Chinese (Simplified)")) {

            lang = "CHINESE_SIMPLIFIED";

        }

        else if(lang.equals("Chinese (Traditional)")) {

            lang = "CHINESE_TRADITIONAL";

        }

        else {

            lang = lang.toUpperCase();

        }


        if(!lang.equals("ENGLISH")) {

            databaseReference.child("Languages").child("FrontPage").child(lang+"").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()) {

                        for(DataSnapshot translations : snapshot.getChildren()) {

                            switch(translations.getKey().toString()) {

                                case "0":

                                    bust = translations.getValue().toString();

                                    break;

                                case "1":

                                    String bustem = bust+" "+translations.getValue().toString();

                                    TextView title = (TextView) findViewById(R.id.titlez);

                                    title.setText(bustem);


                                    break;

                                case "2":

                                    String loginemail = translations.getValue().toString();

                                    TextView email = (TextView) findViewById(R.id.email_google);

                                    email.setText(loginemail);

                                    break;

                                case "3":

                                    String phoneNum = translations.getValue().toString();

                                    TextView phone = (TextView) findViewById(R.id.phone_num);

                                    phone.setText(phoneNum);


                                    break;

                                case "4":

                                    String termsString = translations.getValue().toString();

                                    TextView terms = (TextView) findViewById(R.id.termz);

                                    terms.setText(termsString);

                                    break;

                            }

                        }



                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        else {

            TextView title = (TextView) findViewById(R.id.titlez);

            title.setText("Bust Nutz");

            TextView email = (TextView) findViewById(R.id.email_google);

            email.setText("Log In With Google");

            TextView phone = (TextView) findViewById(R.id.phone_num);

            phone.setText("Log In With Phone Num");

            TextView terms = (TextView) findViewById(R.id.termz);

            terms.setText("By signing up or loging in, you agree with our Terms. Feel free to see how we use your data in our Privacy Policy.");


        }

    }



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

                setUp();

            }
        });


        phone_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(MainActivity.this, PhoneVerify.class));

            }
        });


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



        askPermission();


    }




    private void askPermission() {


        try {


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

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

           // Intent toThenNext = new Intent(MainActivity.this, .class);
           // toThenNext.putExtra("type", "emailVerification");

           // startActivity(toThenNext);


        }


        catch (Exception e) {

            e.printStackTrace();

        }


    }



}