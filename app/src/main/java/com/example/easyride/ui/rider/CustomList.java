package com.example.easyride.ui.rider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easyride.R;
import com.example.easyride.ui.rider.Ride;

import java.util.ArrayList;

import io.opencensus.stats.Measurement;


// Displays the ride information of active ride requests to the Rider home page.
public class CustomList extends ArrayAdapter<Ride> {


    // Hold the ride attributes I want to display, and the activity context.
    private ArrayList<Ride> rides;
    private Context context;

    // Implementation of constructor from the parent class.
    public CustomList(Context context, ArrayList<Ride> rides) {
        super(context,0,rides);
        this.rides = rides;
        this.context = context;
    }

    // getView() allows you to set the values for the views in your listView. Use autocomplete.
    // We want get to reference the TextViews in the content.xml layout file and fill them with
    // values (From, To).
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // The convertView object is a way of recycling old views inside the ListView.
        // Increases performance.
        View view = convertView;

        // If convertView holds nothing then we inflate the 'content.xml'.
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.ride_content_display,parent,false);
        }

        // This next step extracts the information from the DataList and
        // then sets the appropriate attributes to each TextView.
        Ride ride = rides.get(position);

        TextView from = view.findViewById(R.id.from_text);
        TextView to = view.findViewById(R.id.to_text);


        from.setText(ride.getFrom());
        to.setText(ride.getTo());


        return view;
    }

}
