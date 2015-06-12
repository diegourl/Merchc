package ift3150.merchc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    }

    //@TODO free up names
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





/*
    public boolean addTrajectory(Island island){
        Trajectory traj=new Trajectory(island, this.getDistance(island));
        return trajectories.add(traj);
    }

    public float getDistance(Island island){
        //System.out.println("From ("+this.xCoord+","+this.yCoord+") To ("+island.xCoord+","+island.yCoord+")");
        double a= Math.abs(this.xCoord-island.xCoord);
        double b= Math.abs(this.yCoord-island.yCoord);
        double square=a*a+b*b;
        return (float) Math.sqrt(square);
    }


    public void setMarket(Market market){
        this.market=market;

    }


    public float getResourcePrice(int type){
        return Market.getBasePrices()[type]/resources.getAmount(type);

    }



    public String marketString(){
        String result = "1.metals: " + getResourcePrice(0)+ "$(" + resources.getAmount(0)+")";
        result += " 2.fish: " + getResourcePrice(1)+ "$(" + resources.getAmount(1)+")";
        result += " 3.wood: " + getResourcePrice(2)+ "$(" + resources.getAmount(2)+")";
        result += " 4.water: " + getResourcePrice(3)+ "$(" + resources.getAmount(3)+")";
        result += " 5.booze: " + getResourcePrice(4)+ "$(" + resources.getAmount(4)+")";
        result += " 6.coconuts: " + getResourcePrice(5)+ "$(" + resources.getAmount(5)+")";
        result += " 7.tobacco: " + getResourcePrice(6)+ "$(" + resources.getAmount(6)+")";
        return result;
    }
*/

}
