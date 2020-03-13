package com.example.easyride.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyride.R;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class RideRequestListAdapter extends RecyclerView.Adapter<RideRequestListAdapter.ViewHolder> {

    driver_home DriverActivity;
    public List<RideRequest> rideRequestsList;

    public RideRequestListAdapter(driver_home driverActivity, List<RideRequest> rideRequestsList) {
        this.DriverActivity = driverActivity;
        this.rideRequestsList = rideRequestsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_content_display,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.riderName.setText(rideRequestsList.get(position).getRiderUserName());
        holder.pickupLocation.setText(rideRequestsList.get(position).getPickupPoint());
        holder.destination.setText(rideRequestsList.get(position).getTargetPoint());
        holder.fee.setText(rideRequestsList.get(position).getCost());

    }

    @Override
    public int getItemCount() {
        return rideRequestsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView riderName, pickupLocation, destination, fee;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            riderName = mView.findViewById(R.id.riderName);
            pickupLocation = mView.findViewById(R.id.pickUp);
            destination = mView.findViewById(R.id.target);
            fee = mView.findViewById(R.id.cost);
        }
    }
}
