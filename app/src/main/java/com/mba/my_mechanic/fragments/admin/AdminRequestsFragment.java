package com.mba.my_mechanic.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mba.my_mechanic.R;
import com.mba.my_mechanic.adapters.ServiceRequestAdapter;
import com.mba.my_mechanic.models.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

public class AdminRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ServiceRequest> requestList = new ArrayList<>();
    private ServiceRequestAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_requests, container, false);

        recyclerView = view.findViewById(R.id.requestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceRequestAdapter(getContext(), requestList);
        recyclerView.setAdapter(adapter);

        loadRequests();

        return view;
    }

    private void loadRequests() {
        FirebaseFirestore.getInstance().collection("service_requests")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    requestList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String id = doc.getId();
                        String mechanicId = doc.contains("mechanic_id") && doc.get("mechanic_id") != null
                                ? doc.getDocumentReference("mechanic_id").getId() : null;

                        Object userIdObj = doc.get("user_id");
                        String userId = null;

                        if (userIdObj instanceof com.google.firebase.firestore.DocumentReference) {
                            userId = ((com.google.firebase.firestore.DocumentReference) userIdObj).getId();
                        } else if (userIdObj instanceof String) {
                            userId = (String) userIdObj;
                        }

                        Object garageIdObj = doc.get("garage_id");
                        String garageId = null;
                        if (garageIdObj instanceof com.google.firebase.firestore.DocumentReference) {
                            garageId = ((com.google.firebase.firestore.DocumentReference) garageIdObj).getId();
                        } else if (garageIdObj instanceof String) {
                            garageId = (String) garageIdObj;
                        }



                        requestList.add(new ServiceRequest(
                                id,
                                garageId,
                                mechanicId,
                                doc.getString("problem_description"),
                                doc.getString("status"),
                                userId,
                                doc.getString("user_location"),
                                doc.getString("user_phone"),
                                doc.getString("username"),
                                doc.getString("vehicle_type")
                        ));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load requests", Toast.LENGTH_SHORT).show());
    }
}