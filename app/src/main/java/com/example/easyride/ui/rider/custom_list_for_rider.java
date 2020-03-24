package com.example.easyride.ui.rider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easyride.R;

import java.util.ArrayList;

public class custom_list_for_rider extends ArrayAdapter<Ride> {


    // Hold the Measurement attributes I want to display, and the activity context.
    private ArrayList<Ride> rides;
    private Context context;

    // Implementation of constructor from the parent class.
    public custom_list_for_rider (Context context, ArrayList<Ride> rides) {
        super(context,0,rides);
        this.rides = rides;
        this.context = context;
    }

    // getView() allows you to set the values for the views in your listView. Use autocomplete.
    // We want get to reference the TextViews in the content.xml layout file and fill them with
    // values (Date, Systolic, Diastolic, and Heart Rate).
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // The convertView object is a way of recycling old views inside the ListView.
        // Increases performance.
        View view = convertView;

        // If convertView holds nothing then we inflate the 'content.xml'.
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.ride_content_display_for_ride,parent,false);
        }

        // This next step extracts the information from the DataList and
        // then sets the appropriate attributes to each TextView.
        Ride ride = rides.get(position);

        TextView from = view.findViewById(R.id.from_text);
        TextView to = view.findViewById(R.id.to_text);
        TextView cost = view.findViewById(R.id.display_cost);
        TextView status = view.findViewById(R.id.ride_status);

        from.setText(ride.getFrom());
        to.setText(ride.getTo());
        if (ride.getCost().length() > 4) {
            cost.setText("$" + ride.getCost().substring(0, 4));
        }else{
            cost.setText("$" + ride.getCost());
        }
        if(ride.isRideAccepted()) {
            status.setText("Accepted");
        }else{
            status.setText("Not Accepted");
        }

        return view;
    }

}

