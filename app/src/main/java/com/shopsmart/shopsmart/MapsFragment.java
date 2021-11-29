package com.shopsmart.shopsmart;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsFragment extends Fragment implements View.OnClickListener {
    private FragmentMapsListener listener;
    private TextView locationView;
    private Button viewBtn;
    private GoogleMap map;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            LatLng sydney = new LatLng(60, -90);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };
    private String[] shopIds;
    private String[] shopNames;
    private String[] shopPCodes;
    private String userAddress;
    private Integer numShops;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SearchView searchView = view.findViewById(R.id.searchInput);
        Button homeBtn = (Button) view.findViewById(R.id.buttonHome);
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        locationView = (TextView) view.findViewById(R.id.textLocation2);
        viewBtn = (Button) view.findViewById(R.id.buttonView2);
        viewBtn.setEnabled(false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            numShops = bundle.getInt("NUMSHOPS");
            shopIds = new String[numShops];
            shopIds = bundle.getStringArray("SHOPID");
            shopNames = new String[numShops];
            shopNames = bundle.getStringArray("SHOPNAMES");
            shopPCodes = new String[numShops];
            shopPCodes = bundle.getStringArray("SHOPPCODES");
            userAddress = bundle.getString("USERADDRESS");
        } else {
            numShops = 0;
            homeBtn.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            viewBtn.setVisibility(View.GONE);
            locationView.setVisibility(View.GONE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                boolean shopName = false;
                int idx = -1;
                for (int x = 0; x < numShops && !shopName; x++) {
                    if (shopNames[x].toUpperCase(Locale.ROOT).contains(location.toUpperCase(Locale.ROOT))) {
                        location = shopPCodes[x].replaceAll("\\s+", "");
                        idx = x;
                        shopName = true;
                    }
                }
                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                    List<Address> addressList = null;
                    boolean place;
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        place = addressList != null && addressList.size() > 0;
                        searchView.setQuery("", false);
                    }
                    TextView textLocation = view.findViewById(R.id.textLocation);
                    if (place) {
                        textLocation.setText(" ");
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        if (shopName) {
                            map.clear();
                            getLocation();
                            Marker marker = map.addMarker(new MarkerOptions().position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                    .title(shopNames[idx]));
                            marker.showInfoWindow();
                            locationView.setText("Location: " + shopNames[idx]);
                            listener.onInputMapSent(shopIds[idx]);

                            viewBtn.setEnabled(true);
                        }
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    } else {
                        textLocation.setText("Shop/Location not found");
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                textLocation.setText(" ");
                            }
                        }, 1500);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        viewBtn.setOnClickListener(view1 -> {
            int idx = -1;
            if (!locationView.getText().toString().equals("Location: Home")) {
                for (int x = 0; x < numShops; x++) {
                    if (locationView.getText().toString().equals("Location: " + shopNames[x])) {
                        idx = x;
                    }
                }
                listener.onViewMapSent(idx);
            }
        });

        homeBtn.setOnClickListener(v -> {
            map.clear();
            viewBtn = (Button) view.findViewById(R.id.buttonView2);
            viewBtn.setEnabled(false);
            getLocation();
            listener.onInputMapSent("Home");
        });

        supportMapFragment.getMapAsync(googleMap -> {
            googleMap.setOnMapClickListener(latLng -> {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 10
                ));
            });
            googleMap.setOnMarkerClickListener(marker -> {
                locationView = (TextView) view.findViewById(R.id.textLocation2);
                locationView.setText("Location: " + marker.getTitle());
                viewBtn = (Button) view.findViewById(R.id.buttonView2);
                viewBtn.setEnabled(!marker.getTitle().equals("Home"));
                String shopId = null;
                if (!locationView.getText().toString().equals("Location: Home")) {
                    for (int x = 0; x < numShops; x++) {
                        if (locationView.getText().toString().equals("Location: " + shopNames[x])) {
                            shopId = shopIds[x];
                        }
                    }
                } else shopId = "HOME";
                if (shopId != null) listener.onInputMapSent(shopId);
                return false;
            });
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void getLocation() {
        String location = userAddress;
        String check = userAddress.substring(0, 2);
        List<Address> addressList = null;
        Address address;
        boolean place;
        location = location.replaceAll("\\s+", "");
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            place = addressList != null && addressList.size() > 0;
        }
        if (addressList != null) {
            if (place) {
                address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                locationView.setText("Location: Home");
                Marker markerHome = map.addMarker(new MarkerOptions().position(latLng).title("Home"));
                markerHome.showInfoWindow();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }
        }
        for (int x = 0; x < numShops; x++) {
            addressList = null;
            boolean checkAddress = false;

            if (shopPCodes[x].contains(check)) {
                checkAddress = true;
            }
            location = shopPCodes[x].replaceAll("\\s+", "");

            geocoder = new Geocoder(getActivity().getApplicationContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                place = addressList != null && addressList.size() > 0;
            }
            if (addressList != null && checkAddress) {
                if (place) {
                    address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(shopNames[x]));
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentMapsListener) {
            listener = (FragmentMapsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " implement FragmentMapsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface FragmentMapsListener {
        void onInputMapSent(CharSequence input);

        void onViewMapSent(int idx);
    }
}