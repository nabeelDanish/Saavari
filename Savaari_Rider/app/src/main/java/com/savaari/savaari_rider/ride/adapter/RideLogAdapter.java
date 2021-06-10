package com.savaari.savaari_rider.ride.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savaari.savaari_rider.R;
import com.savaari.savaari_rider.ride.entity.Ride;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RideLogAdapter extends RecyclerView.Adapter<RideLogAdapter.ItemViewHolder> {

    private ArrayList<Ride> mItemList;
    private LogViewClickListener listener;

    public RideLogAdapter(ArrayList<Ride> itemList, LogViewClickListener listener) {
        mItemList = itemList;
        this.listener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private WeakReference<LogViewClickListener> listenerRef;
        private TextView driverName;
        private TextView numberPlate;
        private TextView date;
        private TextView duration;
        private TextView fare;

        public ItemViewHolder(@NonNull View itemView, final LogViewClickListener listener) {
            super(itemView);

            driverName = itemView.findViewById(R.id.driver_name);
            numberPlate = itemView.findViewById(R.id.number_plate);
            date = itemView.findViewById(R.id.date);
            duration = itemView.findViewById(R.id.duration);
            fare = itemView.findViewById(R.id.fare);

            listenerRef = new WeakReference<>(listener);

            Log.d("KAMINEY", "Is driver null: " + (driverName == null));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onRideItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RideLogAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_entry, parent, false);
        return new RideLogAdapter.ItemViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RideLogAdapter.ItemViewHolder holder, int position) {

        Ride currentItem = mItemList.get(position);

        holder.driverName.setText(currentItem.getRideParameters().getDriver().getFirstName() + " "
        + currentItem.getRideParameters().getDriver().getLastName());

        holder.numberPlate.setText(currentItem.getRideParameters().getDriver().getActiveVehicle().getNumberPlate());

        holder.duration.setText("Duration: 18:15 - 18:35");
        holder.date.setText("Date: 12th July");
        holder.fare.setText("Fare: " + Double.toString(currentItem.getFare()));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
