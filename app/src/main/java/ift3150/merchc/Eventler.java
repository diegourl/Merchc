package ift3150.merchc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego on 2015-05-16.
 */
public class Eventler {
    private static final String TAG = "Eventler";
    public static final int LEAGUES_IN_A_TILE = Globals.LEAGUES_IN_A_TILE;
    private Map<String,Event> events;
    private static final int FEASIBILITY_LIMIT = 25;
    //private Map<String,Double> events;
    private Wind wind;

    public Eventler(){
        events = new HashMap<>();
        wind = new Wind();
        events.put(Event.PIRATE_ATTACK, new Event(Event.PIRATE_ATTACK));
        events.put(Event.STORM, new Event(Event.STORM));
    }


    public Trajectory getTrajectory(Island from, Island to){
        Trajectory trajectory = new Trajectory(from,to);
        double distance = LEAGUES_IN_A_TILE*Math.sqrt(Math.pow(to.getxCoord()-from.getxCoord(),2)+Math.pow(to.getyCoord()-from.getyCoord(),2));
        Log.d(TAG,"distance: "+distance);
        trajectory.setFeasible(distance<FEASIBILITY_LIMIT);

        double speed = Globals.boat.getSpeed(wind.getSpeed(from.getxCoord(),from.getyCoord(),to.getxCoord(),to.getyCoord()))+1;
        Log.d(TAG, "actual speed: "+speed);
        int duration = (int)(distance/speed +1);
        trajectory.setDuration(duration);
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();

        String query = "select sum("+DbHelper.C_SALARY+") as "+DbHelper.C_SALARY+", sum("+DbHelper.C_UPKEEP+") as "+DbHelper.C_UPKEEP+" from "+DbHelper.T_CREW+" where "+DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgs = {Globals.saveName,Globals.boat.getName()};
        Cursor c = db.rawQuery(query, selectArgs);

        int money = 0;
        int food = 0;
        if(c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(DbHelper.C_SALARY);
            money = c.getInt(columnIndex);
            money *= duration;
            columnIndex = c.getColumnIndex(DbHelper.C_UPKEEP);
            food = c.getInt(columnIndex);
            food *= duration;
        }
        Log.d(TAG,"money: "+money+" food: "+food);
        trajectory.setMoneyCost(money);
        trajectory.setFoodCost(food);

        for(Map.Entry e : events.entrySet()){
            Double risk = 1-Math.pow(1 - ((Event) e.getValue()).getRate(trajectory), duration);
            risk = (int)(risk*1000)/1000.0;
            Log.d(TAG,e.getKey()+" "+risk);
            if(risk>0.001)
                trajectory.setRisk((String)e.getKey(),risk);
        }

        c.close();
        db.close();

        return trajectory;
    }

    public boolean canSail(int duration){

        return true;
    }


    public Happening tryEvents(Trajectory t) {
        Happening happened = Happening.happen("none");
        for(Event e : events.values()){
            double rate = e.getRate(t);
            if(Math.random()<rate) {
                happened = Happening.happen(e.getType());
                return happened;
            }
        }
        return happened;
    }

    public void tick() {
        for(Event e : events.values())
            e.tick();
        for(Island i : Globals.archipelago.values())
            i.tick();

    }

    public class Wind{
        private final static double INIT = 10;
        private final static double INCR = 5;
        double x;
        double y;
        public Wind(){
            this(INIT*Math.random()-INIT/2,INIT*Math.random()-INIT/2);
        }

        public Wind(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void tick(){
            x += INCR*Math.random()-INCR/2;
            y += INCR*Math.random()-INCR/2;
        }

        public double getSpeed(double x1, double y1, double x2, double y2){
            double norm2 = Math.pow(x2-x1,2)+Math.pow(y2-y1,2);
            return (x*(x2-x1)+y*(y2-y1))/norm2;
        }
    }
}
