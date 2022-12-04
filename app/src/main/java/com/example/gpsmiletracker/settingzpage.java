package com.example.gpsmiletracker;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class settingzpage extends AppCompatActivity {

    EditText myEditText;

    FirebaseAuth auth;

    DatabaseReference ref;

    LinearLayout submitBut;

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) myEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);

        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingz);

        myEditText = (EditText) findViewById(R.id.myEdit);

        auth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();

        submitBut = (LinearLayout) findViewById(R.id.submitBut);


        ref.child("User").child(auth.getUid()+"").child("Username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    myEditText.setHint(snapshot.getValue().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        setFocused();

        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ref.child("User").child(auth.getUid()+"").child("Username").setValue(myEditText.getText().toString());

            }
        });

    }



    private void setFocused() {

        myEditText.setFocusable(true);
        myEditText.setFocusableInTouchMode(true);
        myEditText.requestFocus();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }


}
