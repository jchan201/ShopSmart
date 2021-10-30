package com.shopsmart.shopsmart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopsmart.shopsmart.databinding.FragmentFirstBinding;
import com.shopsmart.shopsmart.databinding.FragmentSecondBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SHOP_ADDRESS1 = "param1";
    private static final String SHOP_ADDRESS2 = "param2";
    private static final String PCODE = "param3";

    // TODO: Rename and change types of parameters
    private String shopAddress1;
    private String shopAddress2;
    private String pCode;

    private TextView sAddress1;
    private TextView sAddress2;
    private TextView pCodeView;

    private FragmentSecondBinding binding;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param address1 Parameter 1.
     * @param address2 Parameter 2.
     * @param pCode Parameter 3.
     * @return A new instance of fragment First_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String address1, String address2, String pCode) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(SHOP_ADDRESS1, address1);
        args.putString(SHOP_ADDRESS2, address2);
        args.putString(PCODE, pCode);
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

        View v = inflater.inflate(R.layout.fragment_second, container, false);
        sAddress1 = v.findViewById(R.id.queryShopAddress1);

        Bundle bundle = getArguments();

        if(bundle != null){
            shopAddress1 = bundle.getString("SHOP_ADDRESS1");
            shopAddress2 = bundle.getString("SHOP_ADDRESS2");
            pCode = bundle.getString("PCODE");
        }

        sAddress1.setText(shopAddress1);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }
}