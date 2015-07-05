package io.hackathon.edelivery;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;

import io.hackathon.edelivery.DeliveryService.Order;
import io.hackathon.edelivery.myPack.APIClient;

public class ViewOrders extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mApiClient;
    boolean work = false;
    Order order;
    APIClient apiClient = new APIClient();
    String orderId = "";
    Marker now;

    public interface OrderListener {
        void onFailure();

        void onSuccess(Order order);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);
        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_orders2, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        work = true;
        mApiClient.connect();

        Intent intent = getIntent();

        this.orderId = intent.getStringExtra(Welcome.EXTRA_MESSAGE);

        this.runService();


    }

    Runnable reDrawOrderLocation = new Runnable() {

        @Override
        public void run() {
            if (now != null) {
                now.remove();
            }
            LatLng latLng = order.getLocation();
            now = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_icon)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        }
    };

    public void runService() {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    apiClient.getOrder(orderId, new ViewOrders.OrderListener() {
                        @Override
                        public void onFailure() {
                        }

                        @Override
                        public void onSuccess(final Order order_) {
                            order = order_;
                            runOnUiThread(reDrawOrderLocation);
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                if (work)
                    handler.postDelayed(this, 5000);


            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        work = false;
        if (mApiClient.isConnected()) {
            mApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (mLastLocation != null) {
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onClickChange(View view) {
        EditText orderIdTxt = (EditText) findViewById(R.id.txtOrderId);
        String ordIDTxtString = orderIdTxt.getText().toString();
        if(ordIDTxtString.equals("")==false) {
            this.orderId = ordIDTxtString;
            orderIdTxt.setText("");
        }
    }


}
