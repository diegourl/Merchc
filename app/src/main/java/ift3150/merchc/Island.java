package ift3150.merchc;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Island extends Container {
    private static final String TAG = "Island";
    ///base////////
    float xCoord;
    float yCoord;
    String industry;
    ///////////////////

    //ArrayList<Trajectory> trajectories= new ArrayList<Trajectory>();
    //float[] buy={};
    //float[] sell={};
    //float[] equip={};
    //Market market= new Market(buy, sell, equip);

    public Island(String name, float x, float y, String industry){

        xCoord=x;
        yCoord=y;
        this.name=name;
        this.industry = industry;
    }


    public String getName(){
        return this.name;
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
