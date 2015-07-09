package ift3150.merchc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Island extends Container  {
    private static final String TAG = "Island";
    public static final int ISLAND_IMAGES = 21;
    ///base////////
    private int xCoord;
    private int yCoord;
    private String industry;
    private String image;
    ///////////////////

    //ArrayList<Trajectory> trajectories= new ArrayList<Trajectory>();
    //float[] buy={};
    //float[] sell={};
    //float[] equip={};
    //Market market= new Market(buy, sell, equip);

    public Island(String name, int x, int y, String industry, String image){

        xCoord=x;
        yCoord=y;
        this.name=name;
        this.industry = industry;
        this.image = image;
    }


    public String getName(){
        return this.name;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public String getIndustry() {
        return industry;
    }

    public void tick() {
        float[] probs=Industry.valueOf(getIndustry().trim().toUpperCase()).value;
        //Resource newResources= new Resource();
        for(int i=0; i<probs.length; i++){
            //Log.d(TAG, "TOCK"+probs[i]);
            Random r= new Random();
            float t= r.nextFloat();
            //dammit is this really how we add PROBLEM HERE?
            if (probs[i]>= t) {
                //hope we are getting the right resource name
                //update rtype enum in resource
                bump(Resource.rType.values()[i].name(),this.getName());
                //addResource(new Resource(Resource.rType.values()[i].name().toLowerCase()));
                Log.d(TAG, "TOCK " + t + " >=" + probs[i] + " " + Resource.rType.values()[i].name() + " added to" + this.getName());
            }
        }
    }
    //ups the amount of a resource in the db by 1 unit
    private void bump(String resourceID, String islandID){
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        String selection = DbHelper.C_CONTAINER+" = ? AND "+ DbHelper.C_TYPE+" = ? AND "+ DbHelper.C_FILENAME+" = ?";//+DbHelper.C_ARCHIPELAGO ....
        String [] selectArgs = {islandID, resourceID, Globals.saveName};
        Cursor c = db.query(DbHelper.T_RESOURCES,null,selection,selectArgs,null,null,null);
        c.moveToFirst();
        if(c!=null && c.getCount()>0) {
            int i = c.getColumnIndex(DbHelper.C_AMOUNT);
            String amount = c.getString(i);
            int j = c.getColumnIndex(DbHelper.C_TYPE);
            String typeJ = c.getString(j);
            int k = c.getColumnIndex(DbHelper.C_CONTAINER);
            String containerK = c.getString(k);
            //DatabaseCreator dbcreator=new DatabaseCreator(context);
            //SQLiteDatabase sqdb=DbHelper.getWritableDatabase();
            int newAmount = Integer.parseInt(amount) + 1;

            ContentValues values = new ContentValues();
            values.put(DbHelper.C_AMOUNT, newAmount);

            String selection2 = DbHelper.C_CONTAINER + " = ? and   "+DbHelper.C_TYPE + " = ?";
            String [] selectArgs2 = new String [] {islandID,resourceID};
            db.update(DbHelper.T_RESOURCES, values, selection2, selectArgs2);
            Log.d(TAG, "DOCK " +typeJ+" "+containerK+" "+amount+" "+resourceID+ " added to" + this.getName());


        }
        //if that resource is not in that container creates it and sets it to 1
        else {
            ContentValues values = new ContentValues();

            values.put(DbHelper.C_FILENAME, Globals.saveName);
            values.put(DbHelper.C_CONTAINER, this.getName());
            values.put(DbHelper.C_TYPE, resourceID);
            values.put(DbHelper.C_AMOUNT, 1);

            db.insert(DbHelper.T_RESOURCES, null, values);
            Log.d(TAG, "CROCK " + resourceID + " added to" + this.getName());
        }
        c.close();
    }

    public ArrayList<String> deliver() {
        ArrayList<String> delivered;
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        String selection = DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ? and "+DbHelper.C_DESTINATION+" = ?";
        String [] selectArgs = {Globals.saveName,Globals.boat.getName(),name};
        Cursor c = db.query(DbHelper.T_PASSENGERS,null,selection,selectArgs,null,null,null);
        if(!c.moveToFirst()){c.close();db.close();return null;}
        delivered = new ArrayList<>(c.getCount());
        do{
            int columnIndex = c.getColumnIndex(DbHelper.C_FEE);
            int fee = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_DAYSLEFT);
            int daysLeft = c.getInt(columnIndex);
            Globals.boat.addMoney(daysLeft>=0?fee:(int)(Passenger.LATE_PENALTY*daysLeft*fee));

            columnIndex = c.getColumnIndex(DbHelper.C_WEIGHT);
            int weight = c.getInt(columnIndex);
            Globals.boat.addWeight(-weight);
            columnIndex = c.getColumnIndex(DbHelper.C_VOLUME);
            int volume = c.getInt(columnIndex);
            Globals.boat.addVolume(-volume);

            columnIndex = c.getColumnIndex(DbHelper.C_NAME);
            delivered.add(c.getString(columnIndex));
        }while(c.moveToNext());

        db.delete(DbHelper.T_PASSENGERS,selection,selectArgs);

        c.close();
        db.close();

        return delivered;
    }

    public String getImage() {
        return image;
    }

    //@TODO improve
    public int getRepairCost(int damage) {
        return damage*10;
    }


    public enum Industry{
        //creates the probability matrix for all Cargo generated on islands. For now it is only an
        // array for resources but it will in the future be an array for all cargo types. Modify
        //constructor as needed.
        //Current resource indexes:
        // (0)"metal" (1)"fish" (2)"coconut" (3)"water" (4)"wood" (5)"booze" (6)"tobacco"
        //would be ideal if each array summed to 1 so we assume on average each island would
        // generate resources at the same speed

        //@TODO "food" is a category not a resource there was some kind of mess up it will be changed
        // to something else but for now no island generate food

        BANKING(new float[]{0,0.5f,0,0.5f,0,0,0}), MERCHANT(new float[]{0,0,0,0,0,0,1}),
        PIRATE(new float[]{0,0,0,0,0,1,0}), MINING(new float[]{1,0,0,0,0,0,0}),
        FARMING(new float[]{0,0,1,0,0,0,0}), LUMBERING(new float[]{0,0,0,0,1,0,0});
        private float[] value;
        private Industry(float[] value){
            this.value= value;
        }
    }

}
