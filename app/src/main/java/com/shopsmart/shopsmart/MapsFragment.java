package com.shopsmart.shopsmart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class MapsFragment extends Fragment implements View.OnClickListener {
    private FragmentMapsListener listener;

    private final String PARTITION = "ShopSmart";
    private static final String TAG = "";
    private static final String USERNAME = "param1";
    private static final String USERPASS = "param2";
    private static final String NUMSHOPS = "param3";
    private static final String SHOPID = "param4";
    private static final String SHOPNAMES = "param5";
    private static final String SHOPPCODES = "param6";
    private static final String HOMEADDRESS = "param7";

    private String userEmail;
    private String userPass;
    private Integer numShops = 0;
    private String[] shopIds;
    private String[] shopNames;
    private String[] shopPCodes;
    String userAddress;

    Button homeBtn;
    Button viewBtn;

    TextView locationView;

    CharSequence shopAddress;
    String shopId;

    AppUser user;
    ArrayList<Shop> shops;
    Shop shop;
    Address address;

    boolean clickable = true;

    private App app;
    private Realm realm;

    GoogleMap map;
    SupportMapFragment supportMapFragment;
    SearchView searchView;

    /**
     *
     * @param userName Parameter 1.
     * @param userPass Parameter 2.
     * @param num Parameter 3.
     * @param shopId Parameter 4.
     * @param shopName Parameter 5.
     * @param shopPCode Parameter 6.
     * @param homeAddress Parameter 7.
     * @return
     */

    public static MapsFragment newInstance(String userName, String userPass, Integer num, String shopId[], String shopName[], String shopPCode[], String homeAddress){
        MapsFragment mapsFragment = new MapsFragment();
        Bundle args = new Bundle();

        args.putString(USERNAME, userName);
        args.putString(USERPASS, userPass);
        args.putInt(NUMSHOPS, num);
        args.putStringArray(SHOPID, shopId);
        args.putStringArray(SHOPNAMES, shopName);
        args.putStringArray(SHOPPCODES, shopPCode);
        args.putString(HOMEADDRESS, homeAddress);

        mapsFragment.setArguments(args);

        return mapsFragment;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         location = shopPCodes[x].replaceAll("\\s+", "");
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            LatLng sydney = new LatLng(60, -90);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
    };
    private Bundle savedInstanceState;

    private void init(){
//        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                || actionId == EditorInfo.IME_ACTION_DONE
//                || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
//                    geoLocate();
//                }
//                return false;
//            }
//        });
    }

    private void geoLocate(){
//        String searchString = searchText.getText().toString();
//
//        Geocoder geocoder = new Geocoder(MapsFragment.this);
//        List<Address> list = new ArrayList<>();
//        try{
//            list = geocoder.getFromLocationName(searchString, 1);
//        }catch(IOException e){
//            Log.e(TAG, "Error");
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        Bundle bundle = this.getArguments();

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        RelativeLayout mapLayout = (RelativeLayout) view.findViewById(R.id.rlMaps);

        searchView = view.findViewById(R.id.searchInput);

        viewBtn = (Button) view.findViewById(R.id.buttonView2);
        homeBtn = (Button) view.findViewById(R.id.buttonHome);

        viewBtn = (Button) view.findViewById(R.id.buttonView2);
        viewBtn.setClickable(false);
        viewBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.gray));

        locationView = (TextView) view.findViewById(R.id.textLocation2);

        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                map.clear();
                viewBtn = (Button) view.findViewById(R.id.buttonView2);
                viewBtn.setClickable(false);
                viewBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.gray));
                getLocation();
                listener.onInputMapSent("Home");
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = -1;
                if(!locationView.getText().toString().equals("Location: Home")) {
                    for (int x = 0; x < numShops; x++) {
                        if (locationView.getText().toString().equals("Location: " + shopNames[x])) {//shopNames[x].equals("Location: " + locationView.getText().toString())){
                            Log.i("HELLO", shopIds[x]);
                            idx = x;
                        }
                    }

                    listener.onViewMapSent(idx);
                }
            }
        });

        if(bundle != null){
            userEmail = bundle.getString("USERNAME");
            userPass = bundle.getString("USERPASS");
            numShops = bundle.getInt("NUMSHOPS");
            shopIds = new String[numShops];
            shopIds = bundle.getStringArray("SHOPID");
            shopNames = new String[numShops];
            shopNames = bundle.getStringArray("SHOPNAMES");
            shopPCodes = new String[numShops];
            shopPCodes = bundle.getStringArray("SHOPPCODES");
            userAddress = bundle.getString("USERADDRESS");



//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

//            if(userEmail != null) {
//
//                Credentials credentials = Credentials.emailPassword(userEmail, userPass);
//                app.loginAsync(credentials, result -> {
//                    if (result.isSuccess()) {
//                        Log.v("LOGIN", "Successfully authenticated using email and password.");
//
//                        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
//                        realm = Realm.getInstance(config);
//
//                        RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
//                        for (AppUser u : users) {
//                            if (u.getEmail().equals(userEmail)) {
//                                user = u;
//                            }
//                        }
////                        RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
//////                        shopIds = user.getShops();
////                        shops = new ArrayList<>();
////                        for (Shop s : allShops) {
//////                    for (ObjectId o : shopIds) {
//////                        if (s.getId().equals(o))
////                            shops.add(s);
//////                    }
////                        }
//
////                total = shops.size();
////
////                if(index >= 0){
////                    shop = shops.get(index);
////                    displayShopInfo();
////                }
//                    } else {
//                        Log.v("LOGIN", "Failed to authenticate using email and password.");
//                    }
//                });
//            }
//            searchView.setBackgroundColor(0);
            for(int x = 0; x < numShops; x++){
                Log.i("HELLO", shopPCodes[x]);
            }
            Log.i("HELLO USERADDRESS", userAddress);
        }else{
            homeBtn.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            viewBtn.setVisibility(View.GONE);
            locationView.setVisibility(View.GONE);
//            searchView.setBackgroundColor(0);
//            mapLayout.setVisibility(View.GONE);

        }


//
//        view.findViewById(R.id.home).setOnClickListener(view1 -> {
//            Log.i("HELLO", "BUTTON WORKS");
//        });



        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("HELLO USERADDRESS", userAddress);
                String location = searchView.getQuery().toString();
                boolean shopName = false;
                int idx = -1;
                Log.i("HELLO SEARCH", location);

                for(int x = 0; x < numShops && !shopName; x++){
                    Log.i("HELLO SHOP", shopNames[x]);
//                    if(location.toUpperCase(Locale.ROOT).contains(shopNames[x].toUpperCase(Locale.ROOT))){
//                    if(shopNames[x].toUpperCase(Locale.ROOT).equals(location.toUpperCase(Locale.ROOT))){
//                        location = shopPCodes[x].replaceAll("\\s+", "");
//                        idx = x;
//                        shopName = true;
//                    }
                    if(shopNames[x].toUpperCase(Locale.ROOT).contains(location.toUpperCase(Locale.ROOT))){
                        location = shopPCodes[x].replaceAll("\\s+", "");
                        idx = x;
                        shopName = true;
                    }
                }
                Log.i("HELLO SEARCH 2", location);

                List<Address> addressList = null;
                boolean place = true;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                    try{
                        addressList = geocoder.getFromLocationName(location, 1);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        place = false;
                        if(addressList != null && addressList.size() > 0){
                            place = true;
                        }
                        searchView.setQuery("", false);
                    }
                    if(place) {
                        TextView textLocation = view.findViewById(R.id.textLocation);
                        textLocation.setText(" ");
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    map.addMarker(new MarkerOptions().position(latLng).title(location));
                        if(shopName){
                            map.clear();
                            getLocation();
//                            map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(shopNames[idx]));
                            Marker marker = map.addMarker(new MarkerOptions().position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                    .title(shopNames[idx]));
                            marker.showInfoWindow();
                            locationView.setText("Location: " + shopNames[idx]);
                            listener.onInputMapSent(shopIds[idx]);

                            viewBtn.setClickable(true);
                            viewBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.teal));
                        }
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                    }
                    else{
                        TextView textLocation = view.findViewById(R.id.textLocation);
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



        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
                    @Override
                    public void onMapClick(LatLng latLng){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
//                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        boolean shopNameGet = false;

                        locationView = (TextView) view.findViewById(R.id.textLocation2);
                        locationView.setText("Location: " + marker.getTitle());
                        viewBtn = (Button) view.findViewById(R.id.buttonView2);
                        if(marker.getTitle().toString().equals("Home")){
                            viewBtn.setClickable(false);
                            viewBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.gray));
                            Log.i("HELLO", "Button False");
                        }
                        else{
                            viewBtn.setClickable(true);
                            viewBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.teal));
                            Log.i("HELLO", "Button True");

//                            for(int x = 0; x < numShops && !shopNameGet; x++){
//                                if(locationView.getText().toString().equals("Location: " + shopNames[x])){//shopNames[x].equals("Location: " + locationView.getText().toString())){
//                                    Log.i("HELLO", shopIds[x]);
//                                    shopId = shopIds[x];
//                                }
//                            }
                        }
                if(!locationView.getText().toString().equals("Location: Home")) {
                    for (int x = 0; x < numShops; x++) {
                        if (locationView.getText().toString().equals("Location: " + shopNames[x])) {//shopNames[x].equals("Location: " + locationView.getText().toString())){
                            Log.i("HELLO", shopIds[x]);
                            shopId = shopIds[x];
                        }
                    }

                }
                else{
                    shopId = "HOME";
                }
                        listener.onInputMapSent(shopId);

                        return false;
                    }
                });
            }
        });

        return view;
    }

    public boolean getLocationByName(String place){
        Boolean check = false;

        return check;
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

    public void getLocation(){
        String homeAddress = userAddress;
        String location = userAddress;
        String check = userAddress.substring(0, 2);
        Log.i("HELLO BUTTON", homeAddress);
        List<Address> addressList = null;
        Address address = null;
        boolean place = true;
        location = location.replaceAll("\\s+", "");
        Log.i("HELLO BUTTON", location);
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());

        try{
            addressList = geocoder.getFromLocationName(location, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            place = false;
            if(addressList != null && addressList.size() > 0){
                place = true;
            }
        }
        if(addressList != null) {
            if(place) {
                address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions marker = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Home");

//            marker.icon();
                locationView.setText("Location: Home");
                Marker markerHome = map.addMarker(new MarkerOptions().position(latLng).title("Home"));
                markerHome.showInfoWindow();
//            map.addMarker(marker);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                address = null;
                addressList = null;
            }
        }

        for(int x = 0; x < numShops; x++){
            //List<Address>
            addressList = null;
            boolean checkAddress = false;

            if(shopPCodes[x].contains(check)){
                checkAddress = true;
            }
            location = shopPCodes[x].replaceAll("\\s+", "");

            place = true;

            Log.i("HELLO BUTTON", location);
//            //Geocoder
            geocoder = new Geocoder(getActivity().getApplicationContext());
            try{
                addressList = geocoder.getFromLocationName(location, 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                place = false;
                if(addressList != null && addressList.size() > 0){
                    place = true;
                }
            }
            if(addressList != null && checkAddress) {
                if(place) {
                    address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(shopNames[x]));
                }
            }
        }

    }

    public void updateEditText(CharSequence newText){
        //editText.setText(newText);
    }

    public interface FragmentMapsListener{
        void onInputMapSent(CharSequence input);
        void onViewMapSent(int idx);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentMapsListener){
            listener = (FragmentMapsListener) context;
        }
        else{
            throw new RuntimeException(context.toString()
            + " implement FragmentMapsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}