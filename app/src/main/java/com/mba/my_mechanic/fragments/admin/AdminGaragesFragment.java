package com.mba.my_mechanic.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mba.my_mechanic.R;
import com.mba.my_mechanic.adapters.GarageAdapter;
import com.mba.my_mechanic.models.Garage;

import java.util.ArrayList;

public class AdminGaragesFragment extends Fragment {

    private ArrayList<Garage> garageList = new ArrayList<>();
    private GarageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_garages, container, false);
        ListView listView = view.findViewById(R.id.garageListView);

        adapter = new GarageAdapter(requireContext(), garageList);
        listView.setAdapter(adapter);

        loadGarages();

        return view;
    }

    private void loadGarages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("garages")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    garageList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("garage_name");
                        String address = doc.getString("garage_address");
                        double latitude = doc.getDouble("garage_latitude") != null ? doc.getDouble("garage_latitude") : 0.0;
                        double longitude = doc.getDouble("garage_longitude") != null ? doc.getDouble("garage_longitude") : 0.0;
                        String phone = doc.getString("garage_phone_number");
                        double ratings = doc.getDouble("garage_ratings") != null ? doc.getDouble("garage_ratings") : 0.0;
                        String logo_url = doc.getString("logo");

                        garageList.add(new Garage(name, address, latitude, longitude, phone, ratings, logo_url));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load garages", Toast.LENGTH_SHORT).show());
    }
}