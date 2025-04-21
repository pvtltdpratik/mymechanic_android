package com.mba.my_mechanic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mba.my_mechanic.R;
import com.mba.my_mechanic.fragments.admin.EditGarageDialogFragment;
import com.mba.my_mechanic.models.Garage;

import java.util.List;
public class GarageAdapter extends ArrayAdapter<Garage> {
    private final Context context;
    private final List<Garage> garageList;

    public interface OnGarageEditClickListener {
        void onGarageEdit(Garage garage);
    }

    private final OnGarageEditClickListener listener;

    public GarageAdapter(@NonNull Context context, List<Garage> garageList, OnGarageEditClickListener listener) {
        super(context, 0, garageList);
        this.context = context;
        this.garageList = garageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Garage garage = garageList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.garage_list_item, parent, false);
        }

        TextView nameTV = convertView.findViewById(R.id.garageNameTextView);
        TextView addressTV = convertView.findViewById(R.id.garageAddressTextView);
        ImageButton editButton = convertView.findViewById(R.id.editGarageButton);

        editButton.setOnClickListener(v -> {
            EditGarageDialogFragment dialog = new EditGarageDialogFragment();
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "EditGarageDialog");
        });


        nameTV.setText(garage.getGarageName());
        addressTV.setText(garage.getGarageAddress());

        editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGarageEdit(garage);
            }
        });

        return convertView;
    }
}
