package com.shopsmart.shopsmart;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    String userEmail;
    String userPass;
    int index;
    FirstFragment first;
    SecondFragment second;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                           String userEmail, String userPass, int index) {
        super(fragmentManager, lifecycle);
        this.userEmail = userEmail;
        this.userPass = userPass;
        this.index = index;
        first = FirstFragment.newInstance(userEmail, userPass, index);
        second = SecondFragment.newInstance(userEmail, userPass, index);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return first;
        } else {
            return second;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
