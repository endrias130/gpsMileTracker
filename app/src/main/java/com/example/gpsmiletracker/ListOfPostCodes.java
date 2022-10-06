package com.example.gpsmiletracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_codes);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:1045809070251:android:c8f2fb2e0e18f6b3a2f7ee") // Required for Analytics.
                .setApiKey("AIzaSyAfPzBHCElEenpWnp4z2LRRJMOrbW5_bVk") // Required for Auth.
                .setDatabaseUrl("https://bust-nutz-44e29-default-rtdb.firebaseio.com/") // Required for RTDB.
                // .setDatabaseUrl("https://jj-j-5961e-fca1c.asia-southeast1.firebasedatabase.app/") // Required for RTDB.
                .build();


        try {

            FirebaseApp.initializeApp(ListOfPostCodes.this /* Context */, options, "secondary");

        }

        catch(Exception e) {



        }


        FirebaseApp appr = FirebaseApp.getInstance("secondary");
        FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(appr);


        DatabaseReference secondary = secondaryDatabase.getReference();

        ref = secondary;


        editText = (EditText) findViewById(R.id.searchcodes);

        listofcodes = (RecyclerView) findViewById(R.id.listofcodez);

        listofcodes.setFocusableInTouchMode(false);
        listofcodes.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplication());

        //   mLayoutManager.setReverseLayout(true);
        //   mLayoutManager.setStackFromEnd(true);
        // mLayoutManager.setStackFromEnd(true);
        listofcodes.setLayoutManager(mLayoutManager);

        listofcodes.setNestedScrollingEnabled(false);


        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        codelist = new ArrayList<>();

        initMethod();



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                text = charSequence.toString();


                if(text.length() > 0 && text.length() <= 200) {

                    processsearch(text);

                }

                else {


                    if(text.length() == 0) {

                        initMethod();

                    }


                }


                if(text.length() > 200) {

                    if(limit != 1) {

                        Toast.makeText(ListOfPostCodes.this, "HOLD ON BIG FELLA!\nToo many characters.", Toast.LENGTH_SHORT).show();
                    }

                    limit = 1;

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




    }



    private void processsearch(String countryText) {

        if(!countryText.equals("") || !countryText.isEmpty()) {

            String upperString = countryText.substring(0, 1).toUpperCase() + countryText.substring(1).toLowerCase();


            ref.child("Postal Codes").orderByChild("country").startAt(upperString+"").endAt(upperString+"\uf8ff").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                codelist.clear();

                                for(DataSnapshot d : dataSnapshot.getChildren()) {

                                    if(d.child("country").exists() && d.child("code").exists()) {

                                        Intent t = new Intent();

                                        postal_code_object code = new postal_code_object(d.child("code").getValue().toString(), d.child("country").getValue().toString());

                                        codelist.add(code);

                                        countries = new Countries(ListOfPostCodes.this, t, codelist);

                                        countries.notifyDataSetChanged();

                                        listofcodes.setAdapter(countries);


                                    }


                                }

                            }

                            else {

                                codelist.clear();

                                listofcodes.removeAllViews();

                            }



                            //data will be available on dataSnapshot.getValue();
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


        ref.child("Postal Codes").orderByChild("country").limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for(DataSnapshot d : dataSnapshot.getChildren()) {

                        if(d.child("country").exists() && d.child("code").exists()) {

                            Intent t = new Intent();

                            postal_code_object code = new postal_code_object(d.child("code").getValue().toString(), d.child("country").getValue().toString());

                            codelist.add(code);

                            countries = new Countries(ListOfPostCodes.this, t, codelist);

                            countries.notifyDataSetChanged();

                            listofcodes.setAdapter(countries);


                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
