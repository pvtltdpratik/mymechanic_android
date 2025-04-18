package com.mba.my_mechanic.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mba.my_mechanic.R;
import com.mba.my_mechanic.adapters.MechanicAdapter;
import com.mba.my_mechanic.models.Mechanic;

import java.util.ArrayList;

public class AdminMechanicList extends Fragment {

    private ArrayList<Mechanic> mechanicList = new ArrayList<>();
    private MechanicAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_mechanic_list, container, false);
        ListView listView = view.findViewById(R.id.mechanicListView);

        adapter = new MechanicAdapter(requireContext(), mechanicList);
        listView.setAdapter(adapter);

        loadMechanics();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Mechanic selectedMechanic = mechanicList.get(position);
            showMechanicDetailsDialog(selectedMechanic);
        });

        return view;
    }

    private void loadMechanics() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("role", "mechanic")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mechanicList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String fname = doc.getString("first_name");
                        String lname = doc.getString("last_name");
                        String name = fname + lname;
                        boolean available = doc.getBoolean("available") != null && doc.getBoolean("available");
                        String email = doc.getString("email");
                        String phone = doc.getString("phone");
                        String garage_name = doc.getString("garage_name");
                        Long jobs_completedLong = doc.getLong("jobs_completed");
                        String jobs_completed = String.valueOf(jobs_completedLong != null ? jobs_completedLong : 0);
                        String specialization = doc.getString("specialization");

                        mechanicList.add(new Mechanic(name, email, phone, garage_name, jobs_completed, specialization, available));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load mechanics", Toast.LENGTH_SHORT).show());
    }

    private void showMechanicDetailsDialog(Mechanic mechanic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Mechanic Details");
        String status = mechanic.available ? "Available" : "Not Available";
        builder.setMessage("Name: " + mechanic.name + "\nGarage: " + mechanic.garage_name + "\nStatus: " + status +
                "\nEmail: " + mechanic.email + "\nPhone: " + mechanic.phone + "\nJobs Completed: " + mechanic.jobs_completed +
                "\nSpecialization: " + mechanic.specialization);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
