package ch.berufsbildungscenter.mapdemo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by nblaku on 28.04.2015.
 * Helper Class to work with the DB
 */
public class MapSQLiteHelper extends SQLiteOpenHelper {

    //Default Settings to work with the DB
    public static final String TABLE_WAYPOINTS = "waypoints";
    //Collumns of the DB
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";

    //DB Version
    private static final int DATABASE_VERSION = 2;

    //SQL Query to create a Table in the DB
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + TABLE_WAYPOINTS + " ("
                    +COLUMN_ID + " integer primary key autoincrement, "
                    +COLUMN_LONGITUDE + " TEXT, "
                    +COLUMN_LATITUDE + " TEXT);";

    //Default Constructor which calls the real SQP Helper (with super(...))
    MapSQLiteHelper(Context context) {
        super(context, "mapdemo", null, DATABASE_VERSION);
    }

    /*
    If the DB must be created
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DICTIONARY_TABLE_CREATE);
    }

    /*
    If the DB needs an upgrade (called when the DATABASE_VERSION ist changed)
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
