package com.example.easyride.ui.rider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.easyride.R;

import java.util.ArrayList;

public class CustomListForRider extends ArrayAdapter<Ride> {


    // Hold the Measurement attributes I want to display, and the activity context.
    private ArrayList<Ride> rides;
    private Context context;

    // Implementation of constructor from the parent class.
    public CustomListForRider(Context context, ArrayList<Ride> rides) {
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
        String ride_cost = ride.getCost();
        String ride_cost_short;
        int index = ride_cost.indexOf('.');
        if (index == -1){
            ride_cost_short = ride_cost;
        }else {
            if (ride_cost.length() > 4 && index == 3) {
                ride_cost_short = ride_cost.substring(0, 3);
            } else if(ride_cost.length() > 4) {
                ride_cost_short = ride_cost.substring(0, 4);
            }else {
                ride_cost_short = ride_cost;
            }
        }

        cost.setText("$" + ride_cost_short);

        if(ride.isRideAccepted() && ride.isRideConfirmAccepted()) {
            status.setText("Accepted");
            status.setTextColor(Color.GREEN);
        }else if (ride.isRideAccepted() && !ride.isRideConfirmAccepted()){
            status.setText("Action Required");
            status.setTextColor(Color.RED);
        }else if (!ride.isRidePaid() && ride.isRideCompleted()){
            status.setText("Payment Required");
            status.setTextColor(Color.RED);
        }else{
            status.setText("Not Accepted");
        }

        return view;
    }

}

