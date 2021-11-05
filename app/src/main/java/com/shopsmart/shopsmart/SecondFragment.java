package com.shopsmart.shopsmart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopsmart.shopsmart.databinding.FragmentFirstBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_USER = "param1";
    private static final String EXTRA_PASS = "param2";

    // TODO: Rename and change types of parameters
    private String userEmail;
    private String userPass;
    //
    private FragmentFirstBinding binding;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userEmail Parameter 1.
     * @param userPass Parameter 2.
     * @return A new instance of fragment First_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String userEmail, String userPass) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER, userEmail);
        args.putString(EXTRA_PASS, userPass);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_second, container, false);

        // Inflate the layout for this fragment
        return v;
    }
}