package ch.berufsbildungscenter.mapdemo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nblaku on 28.04.2015.
 */
public class WayPointDataSource {
    private SQLiteDatabase database;
    private MapSQLiteHelper dbHelper;
    private String[] allColumns = {
            MapSQLiteHelper.COLUMN_ID,
            MapSQLiteHelper.COLUMN_LATITUDE,
            MapSQLiteHelper.COLUMN_LONGITUDE};

    public WayPointDataSource(Context context){
        dbHelper = new MapSQLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    /*
    Adds a WayPoint to the Database
     */
    public WayPoint createWayPoint(WayPoint wayPoint){
        //Create a VlalueSet in order to add them to the DB
        ContentValues values = new ContentValues();
        values.put(MapSQLiteHelper.COLUMN_LATITUDE, wayPoint.getLatitude());
        values.put(MapSQLiteHelper.COLUMN_LONGITUDE, wayPoint.getLongitude());
        //Insert the datas into the DB
        long insertId = database.insert(MapSQLiteHelper.TABLE_WAYPOINTS, null, values);
        //Get the WayPoint with ID xy from the DB
        Cursor cursor = database.query(MapSQLiteHelper.TABLE_WAYPOINTS, allColumns, MapSQLiteHelper.COLUMN_ID + " = " + insertId, null,null,null,null);
        //Get the first element from the DB response
        cursor.moveToFirst();
        //Convert the DB Values to a real WayPoint Object
        WayPoint point = (WayPoint) cursorToWayPoint(cursor);
        //close the DB
        cursor.close();
        return point;
    }

    /*
    Deletes the WayPoint from the DB
     */
    public void deleteWayPoint(WayPoint wayPoint) {
        long id = wayPoint.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MapSQLiteHelper.TABLE_WAYPOINTS, MapSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /*
    Retrive all WayPoints from the DB as a List of WayPoints
     */
    public List<WayPoint> getAllWayPoints() {
        List<WayPoint> wayPoints = new ArrayList<WayPoint>();
        //Get all WayPoints from the Table waypoints (Value is defined in the Helper Class)
        Cursor cursor = database.query(MapSQLiteHelper.TABLE_WAYPOINTS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        //Get all WayPoint entries an convert them to WayPoints Objects
        while (!cursor.isAfterLast()) {
            WayPoint wayPoint = cursorToWayPoint(cursor);
            wayPoints.add(wayPoint);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        //returns all WayPoints as a list of WayPoints
        return wayPoints;
    }

    public void removeAllWayPoints(){
        //Flush the whole DB-Table with the WayPoints
        database.delete(MapSQLiteHelper.TABLE_WAYPOINTS,null,null);

    }

    /*
    Converts a DB Value to a real WayPoint Object
     */
    private WayPoint cursorToWayPoint(Cursor cursor) {
        //Create an empty WayPoint Object
        WayPoint point = new WayPoint();
        //Set all Values which were get from the DB
        point.setId(cursor.getLong(0));
        point.setLongitude(cursor.getDouble(1));
        point.setLatitude(cursor.getDouble(2));
        //Return the WayPoint as a real WayPoint Object
        return point;
    }
}

