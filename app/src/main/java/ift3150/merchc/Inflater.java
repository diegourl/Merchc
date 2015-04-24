package ift3150.merchc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Diego
 */

//@TODO remove provisional saving devices like map and such once db works
/*
this class is called by LoadingActivity (or Loader) and is given basic information on objects pulled from the xml which it then instantiates
and writes into the db. It needs the filename and containername as these are used as keys to access contained objects. Methods in this class
do not call each other; they are always called by the LoadingActivity. Hence the need for abstract containterName, as when called, the methods
don't know if the object is on a boat or on an island.

Note that there are two separate inflater objects created by LA: one for the boat and one for the islands.
*/
public class Inflater {
    private static final String TAG = "Inflater";


    //private File file; // not right. or maybe.
    private String saveName;// = DbHelper.filename;//provisional until we settle on a way to write a save name IF we use several savefiles
    private String containerName;
    private Map<String,Island> map;
    private Boat boat;
    private SQLiteDatabase db;

    public Inflater(String saveName){
        this.saveName = saveName;
        this.map = new HashMap<>();
        db = Globals.dbHelper.getWritableDatabase();
    }

    public  void inflateIsland(String name, float x, float y, String industry){
        Island island = new Island(name,x,y,industry);
        //DbHelper.write(saveName, name, x, y, industry);

        //ContentValues is a name-value pair data structure which we then shove into the db
        ContentValues values = new ContentValues();
        values.clear();
        values.put(DbHelper.C_FILENAME,saveName);
        values.put(DbHelper.C_NAME, name);
        values.put(DbHelper.C_X, x);
        values.put(DbHelper.C_Y, y);
        values.put(DbHelper.C_INDUSTRY, industry);
        db.insertOrThrow(DbHelper.T_ISLANDS, null, values);

        //Provisional save into map
        map.put(name,island);

    }

    public void inflateBoat(String name, String type, String starting) {
        boat = new Boat(name,type);
        //DbHelper.write(...
        //ContentValues is a name-value pair data structure which we then shove into the db
        ContentValues values = new ContentValues();
        values.clear();
        values.put(DbHelper.C_FILENAME,saveName);
        values.put(DbHelper.C_NAME, boat.getName());
        values.put(DbHelper.C_CURRENTISLAND, starting);
        values.put(DbHelper.C_TYPE, boat.getType());
        values.put(DbHelper.C_REPAIR, boat.getRepair());
        db.insertOrThrow(DbHelper.T_BOAT, null, values);

    }

    public  void inflatePassenger(String type, String destination) {
        //Passenger passenger = new Passenger(type);  //If destination is not set by the xml, which i don't think it should but hey
        //who am i?
        Passenger passenger = new Passenger(type, destination);
        //actual inflating of the d-bag
        //DbHelper.write(
        ContentValues values = new ContentValues();
        values.clear();
        values.put(DbHelper.C_FILENAME,saveName);
        values.put(DbHelper.C_CONTAINER, containerName);
        values.put(DbHelper.C_NAME, passenger.getName());
        values.put(DbHelper.C_WEIGHT, passenger.getWeight());
        values.put(DbHelper.C_VOLUME, passenger.getVolume());
        values.put(DbHelper.C_TYPE, type);
        values.put(DbHelper.C_DESTINATION, passenger.getDestination());
        values.put(DbHelper.C_FEE, passenger.getFee());
        values.put(DbHelper.C_DAYSLEFT, passenger.getDaysLeft());
        db.insertOrThrow(DbHelper.T_PASSENGERS, null, values);
        //provisional save
        Island island = map.get(containerName);
        if(island != null)
            island.addPassenger(passenger);
        else
            boat.addPassenger(passenger);
    }

    public  void inflateResource(String type, int amount) {
        Resource resource = new Resource(type,amount);
        //DbHelper.write...
        ContentValues values = new ContentValues();
        values.clear();
        values.put(DbHelper.C_FILENAME,saveName);
        values.put(DbHelper.C_CONTAINER,containerName);
        values.put(DbHelper.C_TYPE, resource.getType());
        values.put(DbHelper.C_AMOUNT, resource.getAmount());
        db.insertOrThrow(DbHelper.T_RESOURCES, null, values);

        //provisional save
        Island island = map.get(containerName);
        if(island != null)
            island.addResource(resource);
        else
            boat.addResource(resource);
    }

    public  void inflateEquipment(String type, int amount) {
        Equipment equipment = new Equipment(type,amount);
        //DbHelper.write...
        ContentValues values = new ContentValues();
        values.clear();
        values.put(DbHelper.C_FILENAME,saveName);
        values.put(DbHelper.C_CONTAINER,containerName);
        values.put(DbHelper.C_TYPE, equipment.getType());
        values.put(DbHelper.C_AMOUNT, equipment.getAmount());
        db.insertOrThrow(DbHelper.T_EQUIPMENT, null, values);
        //provisional save
        Island island = map.get(containerName);
        if(island != null)
            island.addEquipment(equipment);
        else
            boat.addEquipment(equipment);
    }

    public  void inflateCrew(String type, int amount) {
        Crew crew = new Crew(type);
        //DbHelper.write.....
        ContentValues values = new ContentValues();
        values.clear();
        values.put(DbHelper.C_FILENAME,saveName);
        values.put(DbHelper.C_CONTAINER, containerName);
        values.put(DbHelper.C_NAME, crew.getName());
        values.put(DbHelper.C_WEIGHT, crew.getWeight());
        values.put(DbHelper.C_VOLUME, crew.getVolume());
        values.put(DbHelper.C_TYPE, crew.getType());
        values.put(DbHelper.C_SALARY, crew.getSalary());
        values.put(DbHelper.C_UPKEEP, crew.getUpkeep());
        db.insertOrThrow(DbHelper.T_CREW, null, values);
        //provisional save
        Island island = map.get(containerName);
        if(island != null)
            island.addCrew(crew);
        else
            boat.addCrew(crew);

    }
    //asdlfkjasdlkjasdlfkj
    //@TODO redo
    public void inflateTrajectory(int days, String to, String from){
        ContentValues values = new ContentValues();
        values.clear();
        values.put(DbHelper.C_FILENAME,saveName);
        values.put(DbHelper.C_DAYS, days);
        values.put(DbHelper.C_FROM, from);
        values.put(DbHelper.C_TO,to);
        db.insertOrThrow(DbHelper.T_TRAJECTORIES, null, values);

    }

    public void setContainerName(String containerName){
        this.containerName = containerName;
    }

    public Map<String, Island> getMap() {return map;}

    public Boat getBoat() { return boat; }

    public void closeDB() { db.close();   }
}
