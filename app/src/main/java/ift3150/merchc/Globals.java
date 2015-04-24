package ift3150.merchc;

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

    public static Map<String,Island> map;//to be deprecated
    public static Boat boat;
    public static Island currentIsland;
    public static DbHelper dbHelper;//this is the only DbHelper object u should use
    public static String saveName;




    public static Boat loadBoat(String saveName){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME + "= '" + saveName + "'";
        Cursor c = db.query(DbHelper.T_BOAT,null,selection,null,null,null,null);
        Boat b;
        String name;
        String type;
        String islandName;
        int repair;
        Island i;
        int index;
        if(!c.moveToFirst()){db.close(); return null;}
        else {

            index = c.getColumnIndex(DbHelper.C_NAME);
            name = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_TYPE);
            type = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_CURRENTISLAND);
            islandName = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_REPAIR);
            repair = c.getInt(index);

            i = loadIsland(islandName);
            b = new Boat(name,type,repair,i);
            db.close();

            return b;
        }
    }

    public static Island loadIsland(String islandName) {
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_NAME + "= '" + islandName + "'";
        Cursor c = db.query(DbHelper.T_ISLANDS,null,selection,null,null,null,null);
        Island i;
        float x;
        float y;
        String industry;
        int index;
        if(!c.moveToFirst()){db.close(); return null;}
        else {

                index = c.getColumnIndex(DbHelper.C_X);
                x = c.getFloat(index);
                index = c.getColumnIndex(DbHelper.C_Y);
                y = c.getFloat(index);
            index = c.getColumnIndex(DbHelper.C_INDUSTRY);
            industry = c.getString(index);

                i = new Island(islandName,x,y,industry);
            db.close();

            return i;
        }

    }
    //probably not the best place to put these...
    //@TODO add resource unification
    public static ArrayList<Resource> loadResources(String  containerName) {
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_CONTAINER + "= '" + containerName + "'";
        Cursor c = db.query(DbHelper.T_RESOURCES,null,selection,null,null,null,null);
        Resource r;
        String type;
        int amount;
        ArrayList<Resource> resources = new ArrayList<>();
        int index;
        if(!c.moveToFirst()){db.close(); return null;}
        else {
            do{
                index = c.getColumnIndex(DbHelper.C_TYPE);
                type = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_AMOUNT);
                amount = c.getInt(index);

                r = new Resource(type,amount);
                resources.add(r);
            }while(c.moveToNext());
            db.close();
            return resources;
        }

    }

    //@TODO add equipment unification
    public static ArrayList<Equipment> loadEquipment(String  containerName) {
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_CONTAINER + "= '" + containerName + "'";
        Cursor c = db.query(DbHelper.T_EQUIPMENT,null,selection,null,null,null,null);
        Equipment e;
        String type;
        int amount;
        ArrayList<Equipment> equipment = new ArrayList<>();
        int index;
        if(!c.moveToFirst()){db.close(); return null;}
        else {
            do{
                index = c.getColumnIndex(DbHelper.C_TYPE);
                type = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_AMOUNT);
                amount = c.getInt(index);

                e = new Equipment(type,amount);
                equipment.add(e);
            }while(c.moveToNext());
            db.close();
            return equipment;
        }

    }

    public static ArrayList<Passenger> loadPassengers(String containerName) {
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_CONTAINER + "= '" + containerName + "'";
        Cursor c = db.query(DbHelper.T_PASSENGERS,null,selection,null,null,null,null);
        Passenger p;
        String type;
        float weight;
        float volume;
        String name;
        String destination;
        int fee;
        int daysLeft;
        ArrayList<Passenger> passengers = new ArrayList<>();
        int index;
        if(!c.moveToFirst()){db.close(); return null;}
        else {
            do{
                index = c.getColumnIndex(DbHelper.C_TYPE);
                type = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_WEIGHT);
                weight = c.getFloat(index);
                index = c.getColumnIndex(DbHelper.C_VOLUME);
                volume = c.getFloat(index);
                index = c.getColumnIndex(DbHelper.C_NAME);
                name = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_DESTINATION);
                destination = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_FEE);
                fee = c.getInt(index);
                index = c.getColumnIndex(DbHelper.C_DAYSLEFT);
                daysLeft = c.getInt(index);
                p = new Passenger(type, weight, volume, name, destination, fee, daysLeft);
                passengers.add(p);
            }while(c.moveToNext());
            db.close();
            return passengers;
        }

    }



    public static ArrayList<Crew> loadCrew(String  containerName) {
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_CONTAINER + "= '" + containerName + "'";
        Cursor c = db.query(DbHelper.T_CREW,null,selection,null,null,null,null);
        Crew cr;
        String type;
        float weight;
        float volume;
        String name;
        float upkeep;
        float salary;
        ArrayList<Crew> crew = new ArrayList<>();
        int index;
        if(!c.moveToFirst()){db.close(); return null;}
        else {
            do{
                index = c.getColumnIndex(DbHelper.C_TYPE);
                type = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_WEIGHT);
                weight = c.getFloat(index);
                index = c.getColumnIndex(DbHelper.C_VOLUME);
                volume = c.getFloat(index);
                index = c.getColumnIndex(DbHelper.C_NAME);
                name = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_UPKEEP);
                upkeep = c.getFloat(index);
                index = c.getColumnIndex(DbHelper.C_SALARY);
                salary = c.getFloat(index);
                cr = new Crew(type, weight, volume, name, upkeep, salary);
                crew.add(cr);
            }while(c.moveToNext());
            db.close();
            return crew;
        }

    }

    public ArrayList<Map<String,String>> resourceMaps(ArrayList<Resource> boatResources,ArrayList<Resource> islandResources){
        ArrayList<Map<String,String>> resources = new ArrayList<>();
        for(Resource r : islandResources){
            Map<String,String> m = new HashMap<>();
            m.put(DbHelper.C_TYPE,r.getType());
            m.put(DbHelper.C_WEIGHT, r.getWeight()+"");
            m.put(DbHelper.C_VOLUME, r.getVolume()+"");
            m.put(DbHelper.PRICE, Resource.getPrice(r.getType(),r.getAmount())+"");
            m.put("island", r.getAmount()+"");
            for(Resource b : boatResources){
                if(r.getType().equals(b.getType())){
                    m.put("boat", b.getAmount()+"");
                    boatResources.remove(b);
                    break;
                }
            }
            resources.add(m);
        }
        for(Resource r : boatResources){
            Map<String,String> m = new HashMap<>();
            m.put(DbHelper.C_TYPE,r.getType());
            m.put(DbHelper.C_WEIGHT, r.getWeight()+"");
            m.put(DbHelper.C_VOLUME, r.getVolume()+"");
            m.put(DbHelper.PRICE, Resource.getPrice(r.getType(),r.getAmount())+"");
            m.put("island", "0");
            m.put("boat", r.getAmount()+"");
            resources.add(m);
        }
        return resources;
    }



}
