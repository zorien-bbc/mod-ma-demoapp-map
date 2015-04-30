package ch.berufsbildungscenter.mapdemo.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nblaku on 28.04.2015.
 * This is a WayPoint Object
 */
public class WayPoint {

    //Here are the Values which can be set.
    private long id;
    private double longitude;
    private double latitude;

    //Defalut Constructor
    public WayPoint(){
    }

    //Customized Constructor if it's needed to create a WayPoint with Values
    public WayPoint(LatLng latLong){
        setLatitude(latLong.latitude);
        setLongitude(latLong.longitude);
    }


    //Getter and Setters for all Values
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    //Returns a nice view of the Values, if you want to show it in a e.g. List
    @Override
    public String toString() {
        return longitude+", "+latitude;
    }
}

