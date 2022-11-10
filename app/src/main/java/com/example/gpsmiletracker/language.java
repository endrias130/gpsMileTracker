package com.example.gpsmiletracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

public class language extends AppCompatActivity  implements languagesAdapter.AdapterCallbacky{

    DatabaseReference databaseReference;

    EditText editText;
    RecyclerView languagesRecycleView;

    language_object languagess;

    ArrayList<language_object> languagelist, updatelanguagelist, descriptionList;

    ArrayList<String> languageCheck, langCheck, initCheck;

    languagesAdapter lang_adapt;

    String text;

    private int limit = 0;

    private int initmethodSelection = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.language_codes);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        editText = (EditText) findViewById(R.id.searchcodes);


        languagesRecycleView = (RecyclerView) findViewById(R.id.listofcodez);

        languagesRecycleView.setFocusableInTouchMode(false);
        languagesRecycleView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplication());

        languagesRecycleView.setLayoutManager(mLayoutManager);

        languagesRecycleView.setNestedScrollingEnabled(false);

        languageCheck = new ArrayList<String>();
        descriptionList = new ArrayList<language_object>();
        languagelist = new ArrayList<language_object>();
        initCheck = new ArrayList<String>();
        updatelanguagelist = new ArrayList<language_object>();

        langCheck = new ArrayList<String>();


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

                    limit = 1;

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }



    private void processsearch(String languageText) {

        if(!languageText.equals("") || !languageText.isEmpty()) {

            String upperString = languageText.substring(0, 1).toUpperCase() + languageText.substring(1).toLowerCase();


            databaseReference.child("ListOfLanguages").orderByChild("description").startAt(upperString+"").endAt(upperString+"\uf8ff").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                descriptionList.clear();

                                for(DataSnapshot d : dataSnapshot.getChildren()) {

                                    if(d.child("language").exists() && d.child("description").exists()) {

                                        if(!langCheck.contains(d.child("language").getValue().toString())) {


                                            //langCheck.add(d.child("language").getValue().toString());

                                        }

                                        language_object code = new language_object(d.child("language").getValue().toString(), d.child("description").getValue().toString());

                                        descriptionList.add(code);

                                    }


                                }




                            }

                            else {


                                descriptionList.clear();

                            }





                            //data will be available on dataSnapshot.getValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



            databaseReference.child("ListOfLanguages").orderByChild("language").startAt(upperString+"").endAt(upperString+"\uf8ff").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                languagelist.clear();

                                for(DataSnapshot d : dataSnapshot.getChildren()) {

                                    if(d.child("language").exists() && d.child("description").exists()) {

                                        if(!langCheck.contains(d.child("language").getValue().toString())) {

                                            language_object code = new language_object(d.child("language").getValue().toString(), d.child("description").getValue().toString());

                                            languagelist.add(code);

                                            langCheck.add(d.child("language").getValue().toString());
                                        }

                                    }


                                }


                                for(int index = 0; index < descriptionList.size(); index++) {

                                    languagelist.add(descriptionList.get(index));

                                }


                                Intent t = new Intent();

                                lang_adapt = new languagesAdapter(language.this, t, languagelist);

                                lang_adapt.notifyDataSetChanged();

                                languagesRecycleView.setAdapter(lang_adapt);


                            }

                            else {

                                if(descriptionList.size() > 0) {

                                    languagelist.clear();

                                    languagesRecycleView.removeAllViews();

                                    for(int index = 0; index < descriptionList.size(); index++) {

                                        languagelist.add(descriptionList.get(index));

                                    }


                                    Intent t = new Intent();

                                    lang_adapt = new languagesAdapter(language.this, t, languagelist);

                                    lang_adapt.notifyDataSetChanged();

                                    languagesRecycleView.setAdapter(lang_adapt);


                                }

                                else {

                                    languagelist.clear();

                                    languagesRecycleView.removeAllViews();


                                }

                            }





                            //data will be available on dataSnapshot.getValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            initmethodSelection = 5;


        }

        else {

            initmethodSelection = 0;

            initMethod();

        }



    }




    private void initMethod() {

        initmethodSelection = 0;

        languagelist.clear();

        initCheck.clear();

        pullUpTopThirteen();

        
        databaseReference.child("ListOfLanguages").orderByChild("language").limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {


                    for(DataSnapshot d : dataSnapshot.getChildren()) {

                        if(d.child("language").exists() && d.child("description").exists()) {

                            Intent t = new Intent();

                            language_object code = new language_object(d.child("language").getValue().toString(), d.child("description").getValue().toString());

                            if(!initCheck.contains(d.child("language").getValue().toString())) {

                                languagelist.add(code);

                                lang_adapt = new languagesAdapter(language.this, t, languagelist);

                                lang_adapt.notifyDataSetChanged();

                                languagesRecycleView.setAdapter(lang_adapt);


                            }


                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void pullUpTopThirteen() {

        getInfo( "58");
        getInfo( "2");
        getInfo( "0");
        getInfo( "1");
        getInfo( "5");
        getInfo( "16");
        getInfo( "4");
        getInfo( "3");
        getInfo( "27");
        getInfo( "10");
        getInfo( "8");
        getInfo( "33");
        getInfo( "6");

    }

    private void getInfo(String id) {

        databaseReference.child("ListOfLanguages").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot d) {

                if(d.exists()) {

                    if(d.child("language").exists() && d.child("description").exists()) {

                        Intent t = new Intent();

                        language_object code = new language_object(d.child("language").getValue().toString(), d.child("description").getValue().toString());

                        languagelist.add(code);

                        initCheck.add(d.child("language").getValue().toString());
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    @Override
    public void onMethodCallbackc(int position, String language, String desc) {


        SharedPreferences databaseLocation = getSharedPreferences("shared_preferences", MODE_PRIVATE);

        SharedPreferences.Editor ed;

        ed = databaseLocation.edit();

        ed.putString("Language", desc+"");

        ed.putString("TranslatedLanguage", language+"");

        ed.commit();


        finish();

    }



    @Override
    public void updateList(int position, String language, String desc) {

        if(position != 0) {

            if(initmethodSelection == 0) {

                databaseReference.child("ListOfLanguages").orderByChild("language").startAfter(language+"").limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {

                            updatelanguagelist.clear();

                            for(DataSnapshot d : dataSnapshot.getChildren()) {

                                if(d.child("language").exists() && d.child("description").exists()) {

                                    Intent t = new Intent();

                                    language_object code = new language_object(d.child("language").getValue().toString(), d.child("description").getValue().toString());

                                    if(!languageCheck.contains(d.child("language").getValue().toString())) {

                                        updatelanguagelist.add(code);

                                        languageCheck.add(d.child("language").getValue().toString());

                                    }


                                }

                            }



                            lang_adapt.updateData(updatelanguagelist);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        }

    }
}
