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
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                           String userEmail, String userPass, int index) {
        super(fragmentManager, lifecycle);
        this.userEmail = userEmail;
        this.userPass = userPass;
        this.index = index;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 1:
                return SecondFragment.newInstance(userEmail, userPass, index);
            default:
                return FirstFragment.newInstance(userEmail, userPass, index);
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}
