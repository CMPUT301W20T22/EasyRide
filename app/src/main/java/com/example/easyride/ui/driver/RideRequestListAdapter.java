package com.example.easyride.ui.driver;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyride.R;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class RideRequestListAdapter extends RecyclerView.Adapter<RideRequestListAdapter.ViewHolder> {

    private List<RideRequest> rideRequestsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickLisnter(OnItemClickListener listener) {
        mListener = listener;
    }

    public RideRequestListAdapter(List<RideRequest> rideRequestsList) {
        this.rideRequestsList = rideRequestsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_content_display,parent,false);


        return new ViewHolder(view, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.riderName.setText("Rider: " + rideRequestsList.get(position).getRiderUserName());
        holder.pickupLocation.setText("Pick Up Location: " + rideRequestsList.get(position).getPickupPoint());
        holder.destination.setText("Destination: " + rideRequestsList.get(position).getTargetPoint());
        holder.fee.setText("Fare: " + rideRequestsList.get(position).getCost().toString());

    }

    @Override
    public int getItemCount() {
        return rideRequestsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView riderName, pickupLocation, destination, fee;


        public ViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);

            mView = itemView;

            riderName = mView.findViewById(R.id.riderName);
            pickupLocation = mView.findViewById(R.id.pickUp);
            destination = mView.findViewById(R.id.target);
            fee = mView.findViewById(R.id.cost);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)  {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }


    }


}
