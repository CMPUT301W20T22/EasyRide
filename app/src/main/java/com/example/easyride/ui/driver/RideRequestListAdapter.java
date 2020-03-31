package com.example.easyride.ui.driver;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyride.R;

import java.util.List;

/**
 * Custom Adapter to RecylerView.
 * Handle and display all the data to View from the Database.
 * @author T22
 * @version 1.0
 */
public class RideRequestListAdapter extends RecyclerView.Adapter<RideRequestListAdapter.ViewHolder> {

    private List<RideRequest> rideRequestsList;
    private OnItemClickListener mListener;

    // https://stackoverflow.com/a/28304164
    // Author: https://stackoverflow.com/users/1633609/sir-nikolay-cesar-the-first
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Handle onClick Event for each item in the list.
     * @param listener
     */
    public void setOnClickLisnter(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Constructor for the Custom Adapter.
     * @param rideRequestsList
     */
    public RideRequestListAdapter(List<RideRequest> rideRequestsList) {
        this.rideRequestsList = rideRequestsList;
    }

    /**
     * Set up the holder for the View in RecyclerView.
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_content_display,parent,false);

        return new ViewHolder(view, mListener);
    }

    /**
     * Set up the data to View.
     * @param holder
     * @param position
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.riderName.setText("Rider: " + rideRequestsList.get(position).getRiderUserName());
        holder.pickupLocation.setText("Pick Up Location: " + rideRequestsList.get(position).getPickupPoint());
        holder.destination.setText("Destination: " + rideRequestsList.get(position).getTargetPoint());
        holder.fee.setText("Fare: " + rideRequestsList.get(position).getCost());

    }

    /**
     * Return the size of the list.
     * @return int
     */
    @Override
    public int getItemCount() {
        return rideRequestsList.size();
    }

    /**
     * Set up a holder for the views in the RecyclerView
     */
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
