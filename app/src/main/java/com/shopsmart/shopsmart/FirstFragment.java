package com.shopsmart.shopsmart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopsmart.shopsmart.databinding.FragmentSecondBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SHOP_ADDRESS1 = "param1";
    private static final String SHOP_ADDRESS2 = "param2";
    private static final String PCODE = "param3";
    private static final String DAYSOPEN = "param4";
    private static final String DAYSCLOSED = "param5";
    private static final String EXTRA_EMAIL = "param6";
    private static final String EXTRA_PASS = "param7";

    // TODO: Rename and change types of parameters
    private String shopAddress1;
    private String shopAddress2;
    private String pCode;

    private String userEmail;
    private String userPass;

//    private String daysOpen;
//    private String daysClosed;

    private String[] shopDaysOpen = new String[7];
    private String[] shopDaysClosed = new String[7];

    private TextView sAddress1;
    private TextView sAddress2;
    private TextView pCodeView;
    private TextView sCountry;
    private TextView[] tvDaysOpen = new TextView[7];

    private FragmentSecondBinding binding;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param address1 Parameter 1.
     * @param address2 Parameter 2.
     * @param pCode Parameter 3.
     * @param daysOpen Parameter 4.
     * @param daysClosed Parameter5.
     * @param user Parameter6.
     * @param pass Parameter7.
     * @return A new instance of fragment First_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String address1, String address2, String pCode, String daysOpen[], String daysClosed[], String user, String pass) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();

        args.putString(SHOP_ADDRESS1, address1);
        args.putString(SHOP_ADDRESS2, address2);
        args.putString(PCODE, pCode);
        args.putStringArray(DAYSOPEN, daysOpen);
        args.putStringArray(DAYSCLOSED, daysClosed);
        args.putString(EXTRA_EMAIL, user);
        args.putString(EXTRA_PASS, pass);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        binding = FragmentSecondBinding.inflate(getLayoutInflater());
        if (getArguments() != null) {
//            shopAddress1 = getArguments().getString(SHOP_ADDRESS1);
//            shopAddress2 = getArguments().getString(SHOP_ADDRESS2);
//            pCode = getArguments().getString(PCODE);
        }
//        binding.queryShopAddress1.setText(shopAddress1);
//        binding.queryShopAddress2.setText(shopAddress2);
//        binding.queryShopPCode.setText(pCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_first, container, false);
        
        sAddress1 = v.findViewById(R.id.queryShopAddress1);
        sAddress2 = v.findViewById(R.id.queryShopAddress2);
        pCodeView = v.findViewById(R.id.queryShopPCode);
        sCountry = v.findViewById(R.id.queryShopCountry);

        tvDaysOpen[0] = v.findViewById(R.id.queryDay1Hours);
        tvDaysOpen[1] = v.findViewById(R.id.queryDay2Hours);
        tvDaysOpen[2] = v.findViewById(R.id.queryDay3Hours);
        tvDaysOpen[3] = v.findViewById(R.id.queryDay4Hours);
        tvDaysOpen[4] = v.findViewById(R.id.queryDay5Hours);
        tvDaysOpen[5] = v.findViewById(R.id.queryDay6Hours);
        tvDaysOpen[6] = v.findViewById(R.id.queryDay7Hours);

        Bundle bundle = this.getArguments();

        if(bundle != null){
            shopAddress1 = bundle.getString("SHOP_ADDRESS1");
            shopAddress2 = bundle.getString("SHOP_ADDRESS2");
            pCode = bundle.getString("PCODE");
            shopDaysOpen = bundle.getStringArray("DAYSOPEN");
            shopDaysClosed = bundle.getStringArray("DAYSCLOSED");
        }

        sAddress1.setText(shopAddress1);
        sAddress2.setText(shopAddress2);
        pCodeView.setText(pCode);
        sCountry.setText("Canada");

        for(int x = 0; x < 7; x++){
            tvDaysOpen[x].setText(shopDaysOpen[x] + shopDaysClosed[x]);
        }

//        for(int x = 0; x < 7; x++){
//            daysOpen[x] = bundle.getString("DAYSOPEN" + x);
//            daysClosed[x] = bundle.getString("DAYSCLOSED" + x);
//        }

        // Inflate the layout for this fragment
        return v;
    }
}