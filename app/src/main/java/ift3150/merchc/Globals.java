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
    public static final int TILES = 5;
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
        String selection = DbHelper.C_FILENAME + "= '" + saveName + "'";
        Cursor c = db.query(DbHelper.T_BOAT,null,selection,null,null,null,null);
        Boat b;
        String name;
        String type;
        String islandName;
        int repair;
        Island i;
        int money;
        int totalWeight;
        int totalVolume;
        int food;
        int index;
        if(!c.moveToFirst()){db.close(); c.close();return null;}
        else {

            index = c.getColumnIndex(DbHelper.C_NAME);
            name = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_TYPE);
            type = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_CURRENTISLAND);
            islandName = c.getString(index);
            index = c.getColumnIndex(DbHelper.C_REPAIR);
            repair = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_MONEY);
            money = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_TOTAL_WEIGHT);
            totalWeight = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_TOTAL_VOLUME);
            totalVolume = c.getInt(index);
            index = c.getColumnIndex(DbHelper.C_FOOD);
            food = c.getInt(index);

            archipelago = loadArchipelago("");
            i = archipelago.get(islandName);
            b = new Boat(name,type,repair,i,money,totalWeight,totalVolume,food);
            db.close();
            c.close();

            return b;
        }
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

        return 0 < db.update(DbHelper.T_BOAT,values,selection,selectArgs);
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

                i = new Island(islandName,x,y,industry);
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

            Island i = new Island(name,x,y,industry);
            archipelago.put(name,i);

        }while(c.moveToNext());
        return archipelago;
    }


    public static boolean sail(int days, int toX, int toY){

        if (!canSail(days)){
            Log.d(TAG, "inside check");
            return false;

        }
        //we can do it so
        //check for events
        Log.d(TAG, "to (x,y):"+ toX+","+toY);
        rollEventDice((int)boat.getCurrentIsland().getxCoord(), (int)boat.getCurrentIsland().getyCoord(), toX, toY);
        //reset the current island to the destiation island
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_X + " = ? AND "+ DbHelper.C_Y+" = ?";
        String [] selectionArgs = {Float.toString(toX), Float.toString( toY)};//goddammit it needs to be INTEGERS
        Cursor islandCursor = db.query(DbHelper.T_ISLANDS,null,selection,selectionArgs,null,null,null);
        if(islandCursor.moveToFirst()) { //maybe?
            int columnIndex = islandCursor.getColumnIndex(DbHelper.C_NAME);
            String destination = islandCursor.getString(columnIndex);
            columnIndex = islandCursor.getColumnIndex(DbHelper.C_INDUSTRY);
            String industry = islandCursor.getString(columnIndex);
            Island curr = new Island(destination, toX, toY, industry);
            boat.setCurrentIsland(curr);
            // its time to pay the price!!!
            int foodUnits = getDailyFood() * days;
            int money = getDailyDollars() * days;
            boat.addMoney(-money); //remove the salaries
            //remove the food
            feed(foodUnits);
        }

        else return false;

        return true;
    }
    //@TODO upsate as more resources are added
    public static void feed(int units){
        int feedfish;
        int feedcoconuts;
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_CONTAINER + " = ? AND "+ DbHelper.C_TYPE+" = ? ";
        String [] selectionArgs = {boat.getName(), "coconut"};
        Cursor resCursor = db.query(DbHelper.T_RESOURCES,null,selection,selectionArgs,null,null,null);
        resCursor.moveToFirst(); //maybe?
        int columnIndex=resCursor.getColumnIndex(DbHelper.C_AMOUNT);
        int coconut= resCursor.getInt(columnIndex);

        selection = DbHelper.C_CONTAINER + " = ? AND "+ DbHelper.C_TYPE + "=?";
        selectionArgs = new String[] {boat.getName(), "fish"};
        resCursor = db.query(DbHelper.T_RESOURCES,null,selection,selectionArgs,null,null,null);
        resCursor.moveToFirst(); //maybe?
        columnIndex=resCursor.getColumnIndex(DbHelper.C_AMOUNT);
        int fish= resCursor.getInt(columnIndex);

        int feedFish, feedCoconut;
        //remove a unit from each..wait no just coconuts and THEN fish but someone said they'd
        //do it better
        //we have already checked the total food is sufficient
        for(int i=0; i<units; i++){
            if(coconut>0){
                coconut--;
            }
            else fish--;

        }


        ContentValues cv = new ContentValues();
        cv.put(DbHelper.C_AMOUNT,coconut); //These Fields should be your String values of actual column names
        db.update(DbHelper.T_RESOURCES, cv, dbHelper.C_CONTAINER+" = '"+boat.getName()+"' AND "+
                dbHelper.C_TYPE+"='coconut'", null);
        Log.d(TAG, "coconuts : " + (coconut));
        cv = new ContentValues();
        cv.put(DbHelper.C_AMOUNT, fish); //These Fields should be your String values of actual column names
        db.update(DbHelper.T_RESOURCES, cv, dbHelper.C_CONTAINER+" = '"+boat.getName()+"' AND "+
                dbHelper.C_TYPE+"='fish'", null);
        Log.d(TAG, "fish : " + (fish));
    }

    public static void rollEventDice(int fromX, int fromY, int toX, int toY){
        //@TODO lookup events aassociated with the trajectory and apply
     //if the event is selected, apply consequences
    }

    public static boolean canSail(int days){

        int totalBFood = getBoatFood();
        int dailyFood = getDailyFood();
        int  dailyDollars= getDailyDollars();
        boolean enoughFood=false;
        if(totalBFood >= dailyFood*days)enoughFood=true;
        boolean enoughMoney=false;
        if(Globals.boat.getMoney()>=dailyDollars*days) enoughMoney=true;
        if(enoughFood && enoughMoney) return true;
        Log.d(TAG, "canSail money"+Globals.boat.getMoney());
        return false;
    }

    public static int getBoatFood(){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String  selection = DbHelper.C_CONTAINER + " = ? AND "+ DbHelper.C_TYPE + "=? OR +?";
        String [] selectionArgs = {Globals.boat.name, "coconut", "fish"};
        int food = 0;
        Cursor resourceCursor = db.query(DbHelper.T_RESOURCES,null,selection,selectionArgs,null,null,null);
        while(resourceCursor.moveToNext()) {
            //resourceCursor.moveToFirst();
            int columnIndex = resourceCursor.getColumnIndex(DbHelper.C_AMOUNT);
            food += resourceCursor.getInt(columnIndex);
        }
        Log.d(TAG, "Boat food  : " + food);
        return food;
    }

    public static int getDailyFood(){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String  selection = DbHelper.C_CONTAINER + " = ?";
        String [] selectionArgs = {Globals.boat.name};
        int food = 0;
        Cursor resourceCursor = db.query(DbHelper.T_CREW,null,selection,selectionArgs,null,null,null);
        while(resourceCursor.moveToNext()) {
            int columnIndex = resourceCursor.getColumnIndex(DbHelper.C_UPKEEP);
            food += resourceCursor.getInt(columnIndex);
        }
        Log.d(TAG, "Daily food  : " + food);
        return food;
    }

    public static int getDailyDollars(){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String  selection = DbHelper.C_CONTAINER + " = ?";
        String [] selectionArgs = {Globals.boat.name};
        int salary = 0;
        Cursor resourceCursor = db.query(DbHelper.T_CREW,null,selection,selectionArgs,null,null,null);
        while(resourceCursor.moveToNext()) {
            int columnIndex = resourceCursor.getColumnIndex(DbHelper.C_SALARY);
            salary += resourceCursor.getInt(columnIndex);
        }
        Log.d(TAG, "Total Daily Salaries  : " + salary);
        return salary;
    }

    //THESE ARE ALL DEPRECATED
    //probably not the best place to put these...
    //@TODO remove
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

    //@TODO remove
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
        int weight;
        int volume;
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
                weight = c.getInt(index);
                index = c.getColumnIndex(DbHelper.C_VOLUME);
                volume = c.getInt(index);
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
        int weight;
        int volume;
        String name;
        int upkeep;
        int salary;
        ArrayList<Crew> crew = new ArrayList<>();
        int index;
        if(!c.moveToFirst()){db.close(); return null;}
        else {
            do{
                index = c.getColumnIndex(DbHelper.C_TYPE);
                type = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_WEIGHT);
                weight = c.getInt(index);
                index = c.getColumnIndex(DbHelper.C_VOLUME);
                volume = c.getInt(index);
                index = c.getColumnIndex(DbHelper.C_NAME);
                name = c.getString(index);
                index = c.getColumnIndex(DbHelper.C_UPKEEP);
                upkeep = c.getInt(index);
                index = c.getColumnIndex(DbHelper.C_SALARY);
                salary = c.getInt(index);
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
