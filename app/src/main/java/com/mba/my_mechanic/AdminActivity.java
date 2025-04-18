package com.mba.my_mechanic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mba.my_mechanic.adapters.AdminViewPagerAdapter;
import com.mba.my_mechanic.fragments.admin.AdminGaragesFragment;
import com.mba.my_mechanic.fragments.admin.AdminMechanicList;
import com.mba.my_mechanic.fragments.admin.AdminNewRequestsFragment;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private Button addGarageButton, assignRoleButton;
    private FirebaseFirestore db;

    private TabLayout tableLayout;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();

        tableLayout = findViewById(R.id.admin_tablayout);
        viewPager = findViewById(R.id.admin_viewpager);

        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this); // pass FragmentActivity to constructor
        adapter.addFragment(new AdminGaragesFragment(), "Garages");
        adapter.addFragment(new AdminNewRequestsFragment(), "New Requests");
        adapter.addFragment(new AdminMechanicList(), "Mechanics");

        viewPager.setAdapter(adapter);

        // This is the correct way for ViewPager2
        new TabLayoutMediator(tableLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getTitle(position))
        ).attach();
    }


    private void assignRole(String userId, String role) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("role", role);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(AdminActivity.this, "Role Updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Failed to Update Role", Toast.LENGTH_SHORT).show());
    }

    private void addGarageToFirestore(String name, double lat, double lng) {
        Map<String, Object> garage = new HashMap<>();
        garage.put("name", name);
        garage.put("latitude", lat);
        garage.put("longitude", lng);

        db.collection("garages").add(garage)
                .addOnSuccessListener(documentReference -> Toast.makeText(AdminActivity.this, "Garage Added!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Error Adding Garage", Toast.LENGTH_SHORT).show());
    }

}