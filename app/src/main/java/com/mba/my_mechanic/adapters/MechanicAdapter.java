package com.mba.my_mechanic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


import com.mba.my_mechanic.R;
import com.mba.my_mechanic.models.Mechanic;

import java.util.List;

public class MechanicAdapter extends ArrayAdapter<Mechanic> {

    private final Context context;
    private final List<Mechanic> mechanicList;

    public MechanicAdapter(@NonNull Context context, List<Mechanic> mechanicList) {
        super(context, 0, mechanicList);
        this.context = context;
        this.mechanicList = mechanicList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Mechanic mechanic = mechanicList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mechanic_list_item, parent, false);
        }

        TextView nameTV = convertView.findViewById(R.id.nameTextView);
        TextView garageTV = convertView.findViewById(R.id.garageTextView);
        TextView statusTV = convertView.findViewById(R.id.statusTextView);

        nameTV.setText(mechanic.name);
        garageTV.setText("Garage: " + mechanic.garage_name);
        statusTV.setText(mechanic.available ? "Available" : "Not Available");
        statusTV.setTextColor(ContextCompat.getColor(context,
                mechanic.available ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

        return convertView;
    }
}
