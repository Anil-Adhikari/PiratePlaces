package edu.ecu.cs.pirateplaces;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class PirateMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    List<PiratePlace> piratePlaceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pirate_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        piratePlaceList = PirateBase.getPirateBase(this).getPiratePlaces();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                for(PiratePlace place: piratePlaceList){
                    LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker()));
                    LatLngBounds bounds = new LatLngBounds.Builder().include(location).build();
                    int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
                    CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
                    mMap.animateCamera(update);
                }

            }
        });
    }
}
