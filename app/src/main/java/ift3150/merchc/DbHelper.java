package ift3150.merchc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Diego on 2015-03-18.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    static final String DB_NAME = "gamestate.db";

    //increment this every time u alter the db. Can't decrement.
    static final int DB_VERSION = 9;

    //meaningless in our case but required
    static final String C_ID  = BaseColumns._ID;

    static final String filename = "filee";

    //all the column and table names. They are put here to avoid runtime typos in queries. When querying, use these.
    static final String T_ISLANDS= "islands";
    static final String C_FILENAME = "filename";
    static final String C_NAME = "name";
    static final String C_X = "xCoord";
    static final String C_Y = "yCoord";
    static final String C_INDUSTRY = "industry";

    static final String T_BOAT = "boat";
    static final String C_CURRENTISLAND = "currentIsland";
    static final String C_TYPE = "type";
    static final String C_REPAIR = "repair";

    static final String T_RESOURCES = "resources";
    static final String C_CONTAINER = "container";
    static final String C_AMOUNT = "amount";

    static final String T_EQUIPMENT = "equipment";

    static final String T_PASSENGERS = "passengers";
    static final String C_WEIGHT = "weight";
    static final String C_VOLUME = "volume";
    static final String C_DESTINATION = "destination";
    static final String C_FEE = "fee";
    static final String C_DAYSLEFT = "daysleft";

    static final String T_CREW = "crew";
    static final String C_SALARY = "salary";
    static final String C_UPKEEP = "upkeep";



    Context context;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    //called once, first time the db is created
    //when adding elements to a create table statement, be careful to space out the name and the type (e.g. "  text, "
    //so as to avoid creating false columns
    //don't forget to change the version number. U can only go up from the last number.
    //IMPORTANT: autoincrement only works with integer, not int, for some reason.
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + T_ISLANDS + " (" + C_ID + " integer primary key autoincrement, "
                + C_FILENAME + " text, " + C_NAME + " text, "  + C_Y + " real, "+ C_X + " real, " + C_INDUSTRY + " text, unique(" + C_FILENAME + " , " + C_NAME + " ) on conflict replace)";
        db.execSQL(sql);
        Log.d(TAG,"onCreated sql: " + sql);

        sql = "create table " + T_BOAT + " (" + C_ID + " integer primary key autoincrement, "
                + C_FILENAME + " text, " + C_NAME + " text, " + C_CURRENTISLAND + " text, " + C_TYPE + " text, " + C_REPAIR + " int, unique(" + C_FILENAME + " , " + C_NAME + " ) on conflict replace)";
        db.execSQL(sql);
        Log.d(TAG,"onCreated sql: " + sql);

        sql = "create table " + T_RESOURCES + " (" + C_ID + " integer primary key autoincrement, "
                + C_FILENAME + " text, " + C_CONTAINER + " text, "+ C_TYPE + " text, " + C_AMOUNT + " int, unique(" + C_FILENAME + " , " + C_CONTAINER + " , " + C_TYPE + " ) on conflict replace)";
        db.execSQL(sql);
        Log.d(TAG,"onCreated sql: " + sql);

        sql = "create table " + T_EQUIPMENT + " (" + C_ID + " integer primary key autoincrement, "
                + C_FILENAME + " text, " + C_CONTAINER + " text, "+ C_TYPE + " text, " + C_AMOUNT + " int, unique(" + C_FILENAME + " , " + C_CONTAINER + " , " + C_TYPE + " ) on conflict replace)";
        db.execSQL(sql);
        Log.d(TAG,"onCreated sql: " + sql);

        sql = "create table " + T_PASSENGERS + " (" + C_ID + " integer primary key autoincrement, "
                + C_FILENAME + " text, " + C_CONTAINER + " text, "+ C_NAME + " text, " + C_WEIGHT + " real, " + C_VOLUME + " real, " + C_TYPE + " text," + C_DESTINATION + " text," + C_FEE + " int," + C_DAYSLEFT + " int, unique(" + C_FILENAME + " , " + C_CONTAINER + " , " + C_NAME +  " , " + C_TYPE +" ) on conflict replace)";
        db.execSQL(sql);
        Log.d(TAG,"onCreated sql: " + sql);

        sql = "create table " + T_CREW + " (" + C_ID + " integer primary key autoincrement, "
                + C_FILENAME + " text, " + C_CONTAINER + " text, "+ C_NAME + " text, " + C_WEIGHT + " real, " + C_VOLUME + " real, " + C_TYPE + " text," + C_SALARY + " real," + C_UPKEEP + " real, unique(" + C_FILENAME + " , " + C_CONTAINER + " , " + C_NAME +  " , " + C_TYPE +" ) on conflict replace)";
        db.execSQL(sql);
        Log.d(TAG,"onCreated sql: " + sql);

    }

    @Override
    // @TODO use alter table statements instead of dropping all the tables
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + T_ISLANDS);
        db.execSQL("drop table if exists " + T_BOAT);
        db.execSQL("drop table if exists " + T_RESOURCES);
        db.execSQL("drop table if exists " + T_EQUIPMENT);
        db.execSQL("drop table if exists " + T_PASSENGERS);
        db.execSQL("drop table if exists " + T_CREW);

        Log.d(TAG,"onUpdated");

        onCreate(db);


    }
}
