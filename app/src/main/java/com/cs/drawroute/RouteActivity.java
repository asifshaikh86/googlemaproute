package com.cs.drawroute;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cs.googlemaproute.DrawRoute;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback,DrawRoute.onDrawRoute {

    GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        /*DrawRoute.getInstance(RouteActivity.this).setFromLatLong(24.905954,67.0803505)
                .setToLatLong(24.9053485,67.079119).setGmapAndKey("AIzaSyBdV2PVvDNMlswG59ZwXIKJS1g7D_2PgYE",gMap)
                .run();*/
        DrawRoute.getInstance(this,RouteActivity.this).run();

    }

    @Override
    public void afterDraw(String result) {
    }
}
