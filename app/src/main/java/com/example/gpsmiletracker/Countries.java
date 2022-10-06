package com.example.gpsmiletracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class Countries extends RecyclerView.Adapter<Countries.viewhold> {

    ArrayList<postal_code_object> codelist;

    Context cts;
    Intent intent;
    private PaginationCallBack callback;
    private AdapterCallbacky adapterCallback;


    public class viewhold extends RecyclerView.ViewHolder {

        TextView countryview, postcodeview;

        LinearLayout info;

        public viewhold(View itemView) {
            super(itemView);

            countryview = itemView.findViewById(R.id.country);

            postcodeview = itemView.findViewById(R.id.postcode);

            info = itemView.findViewById(R.id.infolinear);

        }

    }

    public Countries(Context cts, Intent intent, ArrayList<postal_code_object> codelist){

        this.cts = cts;
        this.intent = intent;
        this.codelist = codelist;

        try {
            adapterCallback = ((AdapterCallbacky) cts);
        } catch (ClassCastException e) {

        }


    }



    @Override
    public viewhold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //  Toast.makeText(cts, "banz", Toast.LENGTH_SHORT).show();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postcodes, parent, false);


        return new viewhold(view);

    }







    @Override
    public void onBindViewHolder(@NonNull final viewhold holder, int pos) {

        int position = holder.getAdapterPosition();

        if(position < codelist.size()) {

            holder.countryview.setText(codelist.get(position).countryname+"");

            holder.postcodeview.setText(codelist.get(position).postalcode+"");


            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //  Toast.makeText(cts, "zzxx "+" "+popula.get(position).us+"", Toast.LENGTH_SHORT).show();


                    adapterCallback.onMethodCallbackc(position, codelist.get(position).countryname, codelist.get(position).postalcode);

                }
            });




        }


        }






    public  interface AdapterCallbacky {

        void onMethodCallbackc(int position, String urlz, String id);

    }





    @Override
    public int getItemCount() {
        return codelist.size();
    }





}
