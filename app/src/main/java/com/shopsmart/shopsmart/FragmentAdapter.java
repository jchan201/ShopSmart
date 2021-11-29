package com.shopsmart.shopsmart;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    FirstFragment first;
    SecondFragment second;
    int index;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                           int index) {
        super(fragmentManager, lifecycle);
        this.index = index;
        first = FirstFragment.newInstance(index);
        second = SecondFragment.newInstance(index);
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
