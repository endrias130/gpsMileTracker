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


public class languagesAdapter extends RecyclerView.Adapter<languagesAdapter.viewhold> {

    ArrayList<language_object> languageList;

    Context cts;
    Intent intent;
    private PaginationCallBack callback;
    private AdapterCallbacky adapterCallback;

    int pos = 0;


    public class viewhold extends RecyclerView.ViewHolder {

        TextView languageview, descriptionview;

        LinearLayout info;

        public viewhold(View itemView) {
            super(itemView);

            languageview = itemView.findViewById(R.id.language);

            descriptionview = itemView.findViewById(R.id.description);

            info = itemView.findViewById(R.id.infolinear);

        }

    }

    public languagesAdapter(Context cts, Intent intent, ArrayList<language_object> languageList){

        this.cts = cts;
        this.intent = intent;
        this.languageList = languageList;

        try {
            adapterCallback = ((AdapterCallbacky) cts);
        } catch (ClassCastException e) {

        }


    }



    @Override
    public viewhold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //  Toast.makeText(cts, "banz", Toast.LENGTH_SHORT).show();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language, parent, false);

        return new viewhold(view);

    }





    @Override
    public void onBindViewHolder(@NonNull final viewhold holder, int pos) {

        int position = holder.getAdapterPosition();

        pos = languageList.size();

        if(position < languageList.size()) {

            if(position+1 == languageList.size()) {

                if(position != 0) {

                    adapterCallback.updateList(position, languageList.get(position).language, languageList.get(position).description);

                }

            }
        }






        if(position < languageList.size()) {

            holder.languageview.setText(languageList.get(position).language+"");

            holder.descriptionview.setText(languageList.get(position).description+"");


            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //  Toast.makeText(cts, "zzxx "+" "+popula.get(position).us+"", Toast.LENGTH_SHORT).show();

                    adapterCallback.onMethodCallbackc(position, languageList.get(position).language, languageList.get(position).description);

                }
            });

        }


    }






    public  interface AdapterCallbacky {

        void onMethodCallbackc(int position, String language, String desc);

        void updateList(int position, String language, String desc);

    }




    public void updateData(ArrayList<language_object> updatelanguageList) {

        int ss = languageList.size()-1;

        if(updatelanguageList != null) {

            this.languageList.addAll(updatelanguageList);


        }


        //notifyDataSetChanged();
        notifyItemInserted(languageList.size()-1);

//notifyItemRangeInserted(ss, languageList.size());

    }



    @Override
    public int getItemCount() {
        return languageList.size();
    }





}
