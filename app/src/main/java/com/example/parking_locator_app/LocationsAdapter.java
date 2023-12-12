package com.example.parking_locator_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {

    private final List<SavedLocation> locationList;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Setter method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public LocationsAdapter(List<SavedLocation> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        SavedLocation location = locationList.get(position);
        holder.locationName.setText(location.getName());
        holder.locationDetails.setText("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
        holder.locationId.setText(String.valueOf(location.getId()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(location.getTimestamp()));
        holder.locationTimestamp.setText(formattedDate);
        holder.locationNotes.setText(location.getNotes());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView locationName;
        TextView locationDetails;
        TextView locationId;

        TextView locationTimestamp;
        TextView locationNotes;


        LocationViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_name);
            locationDetails = itemView.findViewById(R.id.location_details);
            locationId = itemView.findViewById(R.id.location_id);
            locationTimestamp = itemView.findViewById(R.id.location_timestamp);
            locationNotes = itemView.findViewById(R.id.location_notes);
        }
    }
}
