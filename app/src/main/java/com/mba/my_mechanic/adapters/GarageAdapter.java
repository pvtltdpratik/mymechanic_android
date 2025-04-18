package com.mba.my_mechanic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.mba.my_mechanic.R;
import com.mba.my_mechanic.models.Garage;

import java.util.List;

public class GarageAdapter extends ArrayAdapter<Garage> {

    private final Context context;
    private final List<Garage> garageList;

    public GarageAdapter(@NonNull Context context, List<Garage> garageList) {
        super(context, 0, garageList);
        this.context = context;
        this.garageList = garageList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Garage garage = garageList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.garage_list_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.garageNameTextView);
        TextView addressTextView = convertView.findViewById(R.id.garageAddressTextView);
        ImageView logoImageView = convertView.findViewById(R.id.garageLogoImageView);

        nameTextView.setText(garage.garage_name);
        addressTextView.setText(garage.garage_address);

        // Load image using Glide
        Glide.with(context)
                .load(garage.logo_url) // Assuming garage.logo_url is a String
                .placeholder(R.drawable.cogwheel) // fallback if loading fails
                .error(R.drawable.fender_bender) // optional error placeholder
                .into(logoImageView);

        return convertView;
    }
}