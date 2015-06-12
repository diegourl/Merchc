package ift3150.merchc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego on 2015-03-14.
 */

//temporary substitute for DB
public class Globals {
    private static final String TAG = "Globals";
    public static final int TILES = 10;
    public static final int LEAGUES_IN_A_TILE = 5;

    //public static Map<String,Island> map;//to be deprecated
    public static Map<String,Island> archipelago;
    public static Boat boat;
    //public static Island currentIsland;
    public static DbHelper dbHelper;//this is the only DbHelper object u should use
    public static String saveName;



///TODO replace total weight,vol and food columns by cross-table query;
    public static Boat loadBoat(String saveName){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME + "= ?";
        String [] selectArgs = {saveName};
        Cursor c = db.query(DbHelper.T_BOAT,null,selection,selectArgs,null,null,null);
        Boat b;
        if(!c.moveToFirst()){db.close(); c.close();return null;}
        else {

            int index = c.getColumnIndex(DbHelper.C_NAME);
            String name = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_TYPE);
            String type = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_CURRENTISLAND);
            String islandName = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_REPAIR);
            int repair = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_MONEY);
            int money = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_TOTAL_WEIGHT);
            int totalWeight = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_TOTAL_VOLUME);
            int totalVolume = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_FOOD);
            int food = c.getInt(index);

            Log.d(TAG,"total weight: "+totalWeight);
            Log.d(TAG,"total volume: "+totalVolume);
            Log.d(TAG,"total food: "+food);


            archipelago = loadArchipelago("");
            Island i = archipelago.get(islandName);
            b = new Boat(name, type, repair, i, money);
        }


        b = censusPassengers(b);
        b = censusCrew(b);
        b = censusResources(b);
        b = censusEquipment(b);



        db.close();
        c.close();

        return b;
    }

    public static boolean saveBoat(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbHelper.C_NAME,boat.getName());
        values.put(DbHelper.C_TYPE,boat.getType());
        values.put(DbHelper.C_REPAIR,boat.getRepair());
        values.put(DbHelper.C_CURRENTISLAND,boat.getCurrentIsland().getName());
        values.put(DbHelper.C_MONEY,boat.getMoney());
        values.put(DbHelper.C_TOTAL_WEIGHT,boat.getTotalWeight());
        values.put(DbHelper.C_TOTAL_VOLUME,boat.getTotalVolume());
        values.put(DbHelper.C_FOOD,boat.getFood());

        String selection = DbHelper.C_FILENAME + " = ? and "+DbHelper.C_NAME+" = ?";
        String [] selectArgs = {saveName,boat.getName()};

        int rows =  db.update(DbHelper.T_BOAT,values,selection,selectArgs);
        db.close();
        return rows>0;
    }

    //called by loadboat upon loading game
    //should be called by mapactivity
    public static Island loadIsland(String islandName) {
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_NAME + "= '" + islandName + "'";
        Cursor c = db.query(DbHelper.T_ISLANDS,null,selection,null,null,null,null);
        Island i;
        int x;
        int y;
        String industry;
        int index;
        if(!c.moveToFirst()){db.close();c.close(); return null;}
        else {

                index = c.getColumnIndex(DbHelper.C_X);
                x = c.getInt(index);
                index = c.getColumnIndex(DbHelper.C_Y);
                y = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_INDUSTRY);
            industry = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_IMAGE);
            String image = c.getString(index);

                i = new Island(islandName,x,y,industry,image);
            db.close();
            c.close();

            return i;
        }

    }

    public static Map<String,Island> loadArchipelago(String archipelagoName){
        Map<String,Island> archipelago = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME+" = ?";//+DbHelper.C_ARCHIPELAGO ....
        String [] selectArgs = {saveName};
        Cursor c = db.query(DbHelper.T_ISLANDS,null,selection,selectArgs,null,null,null);
        int columnIndex;
        if(!c.moveToFirst()){db.close();c.close(); return null;}
        do{
            columnIndex = c.getColumnIndex(DbHelper.C_NAME);
            String name = c.getString(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_X);
            int x = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_Y);
            int y = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_INDUSTRY);
            String industry = c.getString(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_IMAGE);
            String image = c.getString(columnIndex);

            Island i = new Island(name,x,y,industry,image);
            archipelago.put(name,i);

        }while(c.moveToNext());
        return archipelago;
    }


    private static Boat censusPassengers(Boat b) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "select sum("+DbHelper.C_WEIGHT+") as "+DbHelper.C_WEIGHT+", sum("+DbHelper.C_VOLUME+") as "+DbHelper.C_VOLUME+" from "+DbHelper.T_PASSENGERS+" where "+DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgs = new String[]{saveName, b.getName()};
        Cursor c = db.rawQuery(query,selectArgs);
        if(!c.moveToFirst()) return b;
        int columnIndex = c.getColumnIndex(DbHelper.C_WEIGHT);
        int weight = c.getInt(columnIndex);
        columnIndex = c.getColumnIndex(DbHelper.C_VOLUME);
        int volume = c.getInt(columnIndex);

        b.addWeight(weight);
        b.addVolume(volume);

        return b;
    }

    private static Boat censusCrew(Boat b) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "select sum("+DbHelper.C_WEIGHT+") as "+DbHelper.C_WEIGHT+", sum("+DbHelper.C_VOLUME+") as "+DbHelper.C_VOLUME+" from "+DbHelper.T_CREW+" where "+DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgs = new String[]{saveName, b.getName()};
        Cursor c = db.rawQuery(query,selectArgs);
        if(!c.moveToFirst()) return b;
        int columnIndex = c.getColumnIndex(DbHelper.C_WEIGHT);
        int weight = c.getInt(columnIndex);
        columnIndex = c.getColumnIndex(DbHelper.C_VOLUME);
        int volume = c.getInt(columnIndex);

        b.addWeight(weight);
        b.addVolume(volume);

        return b;
    }


    private static Boat censusResources(Boat b) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgs = new String[]{saveName,b.getName()};
        Cursor c = db.query(DbHelper.T_RESOURCES,null,selection,selectArgs,null,null,null);
        if(!c.moveToFirst()) return b;
        do {
            int columnIndex = c.getColumnIndex(DbHelper.C_TYPE);
            String type = c.getString(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_AMOUNT);
            int amount = c.getInt(columnIndex);
            Resource r = new Resource(type,amount);
            b.addWeight(r.getWeight());
            b.addVolume(r.getVolume());
            b.addFood(r.getFoodValue());
        }while(c.moveToNext());
        return b;
    }

    private static Boat censusEquipment(Boat b) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgs = new String[]{saveName,b.getName()};
        Cursor c = db.query(DbHelper.T_EQUIPMENT,null,selection,selectArgs,null,null,null);
        if(!c.moveToFirst()) return b;
        do {
            int columnIndex = c.getColumnIndex(DbHelper.C_TYPE);
            String type = c.getString(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_AMOUNT);
            int amount = c.getInt(columnIndex);
            Equipment e = new Equipment(type,amount);
            b.addWeight(e.getWeight());
            b.addVolume(e.getVolume());
        }while(c.moveToNext());
        return b;
    }








}
