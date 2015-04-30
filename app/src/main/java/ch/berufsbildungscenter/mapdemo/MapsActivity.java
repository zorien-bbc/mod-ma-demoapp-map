package ch.berufsbildungscenter.mapdemo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import ch.berufsbildungscenter.mapdemo.model.WayPoint;
import ch.berufsbildungscenter.mapdemo.model.WayPointDataSource;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager locationManager;
    PolylineOptions myWay;
    private WayPointDataSource datasource;
    public static String TAG = "MapsActivity";
    private LatLng lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the view to the MAP activity
        setContentView(R.layout.activity_maps);
        //Sets the MAP up if needed (if there is no MAP)
        setUpMapIfNeeded();

        //Get the Button from the GUI
        Button clear = (Button) findViewById(R.id.clearButton);
        //Add a clicklistener to the Button
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasource.removeAllWayPoints();
                myWay = new PolylineOptions();
                mMap.clear();
                //Shows a little message in order that the user knows the data were cleared
                Toast.makeText(MapsActivity.this,getString(R.string.cleared,2),Toast.LENGTH_SHORT).show();
            }
        });

        //Create a new Poliline (List of WayPoints)
        myWay = new PolylineOptions();
        //Create a new DB connection
        datasource = new WayPointDataSource(this);
        try {
            //Try to open the DB connection
            datasource.open();
        } catch (SQLException e) {
            Log.v(TAG,e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Setup the map if needed
        setUpMapIfNeeded();
        //Get all WayPoints from the Database
        List<WayPoint> values = datasource.getAllWayPoints();
        //Add every Waypoint from the database to the myWay List
        Iterator i = values.iterator();
        for( ;i.hasNext();){
            WayPoint w = (WayPoint) i.next();
            LatLng l = new LatLng(w.getLatitude(),w.getLongitude());
            myWay.add(l);
            lastLocation = l;
        }
        //Add a Poliline with all Waypoints
        mMap.addPolyline(myWay);

        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = LocationManager.GPS_PROVIDER;
        //String provider = LocationManager.NETWORK_PROVIDER;
        //String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 0, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove the listener you previously added
        locationManager.removeUpdates(this);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Create a LatLng object for the init location
        LatLng latLng = new LatLng(0,0);
        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(1));
    }

    @Override
    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        makeUseOfNewLocation(location);
    }

    private void makeUseOfNewLocation(Location location) {
        // Get latitude of the current location
        double latitude = location.getLatitude();
        // Get longitude of the current location
        double longitude = location.getLongitude();
        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Add the current location to the myWay list
        myWay.add(latLng);
        // Add the WayPoints in myWay onto the map
        mMap.addPolyline(myWay);
        // Create a new WayPoint
        WayPoint w = new WayPoint(latLng);
        // Add the WayPoint to the Database
        datasource.createWayPoint(w);
        // Calculate the Cameraorientation
        Double l = calculateOrientation(latLng);
        // Move the camera to the new position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(5)                   // Sets the zoom
                .bearing(l.floatValue())                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    /*
    Calculates the orientation of the camera
     */
    private Double calculateOrientation(LatLng location){
        Double res = 0.0;
        if(null != lastLocation){
            double x = lastLocation.latitude - location.latitude;
            double y = lastLocation.longitude - location.longitude;
            Double angle = Math.atan2(x, y);
            res =  (angle > 0 ? angle : (2*Math.PI + angle)) * 360 / (2*Math.PI);
        }
        lastLocation = location;
        return res;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
