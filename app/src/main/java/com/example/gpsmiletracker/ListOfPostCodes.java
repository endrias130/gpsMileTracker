package com.example.gpsmiletracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListOfPostCodes extends AppCompatActivity implements Countries.AdapterCallbacky{

    DatabaseReference ref;

    EditText editText;
    RecyclerView listofcodes;

    Countries countries;

    ArrayList<postal_code_object> codelist;

    String text;

    private int limit = 0;

    long delay = 300; // 1/2 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();

    ArrayList<String> postCodeCheck;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_codes);

        FirebaseOptions default_serv = new FirebaseOptions.Builder()
                .setApplicationId("1:1045809070251:android:c8f2fb2e0e18f6b3a2f7ee") // Required for Analytics.
                .setApiKey("AIzaSyAfPzBHCElEenpWnp4z2LRRJMOrbW5_bVk") // Required for Auth.
                .setDatabaseUrl("https://bust-nutz-44e29-default-rtdb.firebaseio.com/") // Required for RTDB.
                .build();


        try {

            FirebaseApp.initializeApp(this /* Context */, default_serv, "default_serv");

        }

        catch(Exception e) {


        }


        FirebaseApp default_server = FirebaseApp.getInstance("default_serv");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(default_server);

        ref = firebaseDatabase.getReference();


        editText = (EditText) findViewById(R.id.searchcodes);

        listofcodes = (RecyclerView) findViewById(R.id.listofcodez);
        listofcodes.setFocusableInTouchMode(false);
        listofcodes.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplication());
        listofcodes.setLayoutManager(mLayoutManager);
        listofcodes.setNestedScrollingEnabled(false);


        // focusing the edit text
        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        codelist = new ArrayList<postal_code_object>();
        postCodeCheck = new ArrayList<String>();


        Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {

                    processsearch(text);

                }
            }
        };



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                text = charSequence.toString();

                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                last_text_edit = System.currentTimeMillis();

                handler.postDelayed(input_finish_checker, delay);

            }
        });


        initMethod();



    }



    private void processsearch(String countryText) {

        if(!countryText.equals("") || !countryText.isEmpty()) {

            String upperString = countryText.substring(0, 1).toUpperCase() + countryText.substring(1);

            ref.child("Postal Codes").orderByChild("country").startAt(upperString+"").endAt(upperString+"\uf8ff").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                codelist.clear();

                                postCodeCheck.clear();

                                for(DataSnapshot d : dataSnapshot.getChildren()) {

                                    if(d.child("country").exists() && d.child("code").exists()) {

                                        if(!postCodeCheck.contains(d.child("country").getValue().toString())) {

                                            postal_code_object code = new postal_code_object(d.child("code").getValue().toString(), d.child("country").getValue().toString());

                                            codelist.add(code);

                                            postCodeCheck.add(d.child("country").getValue().toString());

                                        }

                                    }


                                }

                                // Toast.makeText(ListOfPostCodes.this, upperString+"", Toast.LENGTH_SHORT).show();


                                Intent t = new Intent();

                                countries = new Countries(ListOfPostCodes.this, t, codelist);

                                countries.notifyDataSetChanged();

                                listofcodes.setAdapter(countries);

                            }

                            else {


                                codelist.clear();

                                postCodeCheck.clear();

                                listofcodes.removeAllViews();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

          }

        else {

            initMethod();

        }

    }


    private void initMethod() {

        codelist.clear();

        postCodeCheck.clear();

        pullUpTopTwenty();

    }


    private void pullUpTopTwenty() {

        Intent t = new Intent();


        postal_code_object code = new postal_code_object("+1", "United States");

        codelist.add(code);


        code = new postal_code_object("+1", "Canada");

        codelist.add(code);


        code = new postal_code_object("+44", "United Kingdom");

        codelist.add(code);


        code = new postal_code_object("+61", "Australia");

        codelist.add(code);


        code = new postal_code_object("+91", "India");

        codelist.add(code);


        code = new postal_code_object("+33", "France");

        codelist.add(code);


        code = new postal_code_object("+49", "Germany");

        codelist.add(code);


        code = new postal_code_object("+81", "Japan");

        codelist.add(code);


        code = new postal_code_object("+82", "South Korea");

        codelist.add(code);


        code = new postal_code_object("+52", "Mexico");

        codelist.add(code);


        code = new postal_code_object("+55", "Brazil");

        codelist.add(code);



        code = new postal_code_object("+86", "China");

        codelist.add(code);



        code = new postal_code_object("+7", "Russia");

        codelist.add(code);



        code = new postal_code_object("+34", "Spain");

        codelist.add(code);



        code = new postal_code_object("+39", "Italy");

        codelist.add(code);



        code = new postal_code_object("+84", "Vietnam");

        codelist.add(code);



        code = new postal_code_object("+62", "Indonesia");

        codelist.add(code);



        code = new postal_code_object("+31", "Netherlands");

        codelist.add(code);



        code = new postal_code_object("+47", "Norway");

        codelist.add(code);



        code = new postal_code_object("+351", "Portugal");

        codelist.add(code);


        code = new postal_code_object("+32", "Belgium");

        codelist.add(code);


        code = new postal_code_object("+268", "Sweden");

        codelist.add(code);



        code = new postal_code_object("+41", "Switzerland");

        codelist.add(code);



        code = new postal_code_object("Find your country", "Let's Explore");

        codelist.add(code);



        countries = new Countries(ListOfPostCodes.this, t, codelist);

        countries.notifyDataSetChanged();

        listofcodes.setAdapter(countries);


    }

    @Override
    public void onMethodCallbackc(int position, String country, String postcode) {

       // ref.child("Postal Code").child("PostCode").setValue(postcode+"");

        SharedPreferences phoneinfo = getSharedPreferences("shared_preferences", MODE_PRIVATE);

        SharedPreferences.Editor ed;

        ed = phoneinfo.edit();

        ed.putString("Country", country+"");

        ed.putString("PostCode", postcode+"");

        ed.commit();


        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        finish();


    }

}
