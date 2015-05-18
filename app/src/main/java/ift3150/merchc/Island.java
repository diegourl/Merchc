package ift3150.merchc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Island extends Container  {
    private static final String TAG = "Island";
    ///base////////
    private int xCoord;
    private int yCoord;
    private String industry;
    ///////////////////

    //ArrayList<Trajectory> trajectories= new ArrayList<Trajectory>();
    //float[] buy={};
    //float[] sell={};
    //float[] equip={};
    //Market market= new Market(buy, sell, equip);

    public Island(String name, int x, int y, String industry){

        xCoord=x;
        yCoord=y;
        this.name=name;
        this.industry = industry;
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
    public void deliver() {
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        String selection = DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ? and "+DbHelper.C_DESTINATION+" = ?";
        String [] selectArgs = {Globals.saveName,Globals.boat.getName(),name};
        String [] columns = {DbHelper.C_FEE,DbHelper.C_DAYSLEFT};
        Cursor c = db.query(DbHelper.T_PASSENGERS,columns,selection,selectArgs,null,null,null);
        if(!c.moveToFirst()){c.close();db.close();return;}
        do{
            int columnIndex = c.getColumnIndex(DbHelper.C_FEE);
            int fee = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_DAYSLEFT);
            int daysLeft = c.getInt(columnIndex);
            Globals.boat.addMoney(daysLeft>=0?fee:(int)(Passenger.LATE_PENALTY*daysLeft*fee));
        }while(c.moveToNext());

        db.delete(DbHelper.T_PASSENGERS,selection,selectArgs);
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
