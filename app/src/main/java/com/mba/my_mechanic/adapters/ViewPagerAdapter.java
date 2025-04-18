package com.mba.my_mechanic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mba.my_mechanic.fragments.HomeFragment;
import com.mba.my_mechanic.fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ProfileFragment();
        }
        return new HomeFragment(); // Default
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}
