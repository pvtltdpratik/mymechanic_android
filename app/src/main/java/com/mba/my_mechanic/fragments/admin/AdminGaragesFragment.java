package com.mba.my_mechanic.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu resource into the menu
        inflater.inflate(R.menu.menu_admin_garages, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_garage) {
            openAddGarageForm(); // Your method to open add garage dialog
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddGarageForm() {
        // Open the Add Garage Dialog or Fragment here
        Toast.makeText(getContext(), "Add Garage clicked", Toast.LENGTH_SHORT).show();

        // You can create a new dialog or fragment to add garage details
        AddGarageDialogFragment addGarageDialogFragment = new AddGarageDialogFragment();
        addGarageDialogFragment.show(getChildFragmentManager(), "AddGarageDialog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_garages, container, false);
        ListView listView = view.findViewById(R.id.garageListView);
        setHasOptionsMenu(true);
        adapter = new GarageAdapter(requireContext(), garageList,  garage -> {
            // Handle edit click
            showEditGarageDialog(garage);
        });
        listView.setAdapter(adapter);

        loadGarages();

        return view;
    }

    // Method to reload the garage list
    public void reloadGarages() {
        loadGarages(); // Call loadGarages to refresh the data
    }

    private void showEditGarageDialog(Garage garage) {
        EditGarageDialogFragment dialog = EditGarageDialogFragment.newInstance(garage, garage.getDocId());
        dialog.setTargetFragment(this, 1); // Optional if you want a result callback
        dialog.show(getParentFragmentManager(), "EditGarageDialog");
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
                        String state = doc.getString("state") != null ? doc.getString("state") : "";
                        String docId = doc.getId();

                        garageList.add(new Garage(name, address, latitude, longitude, phone, ratings, logo_url, state, docId));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load garages", Toast.LENGTH_SHORT).show());
    }
}