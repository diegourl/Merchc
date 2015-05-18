package ift3150.merchc;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Diego on 2015-03-19.
 */
public class Event {
    private static final String TAG = "Event";
    private static final double k = 0.01;
    private Point center;
    double range;
    double growthRate;
    private String type;
    private boolean over = false;

    public static final String NOTHING_HAPPENED = "nothing happened";
    public static final String PIRATE_ATTACK = "pirate attack";
    public static final String STORM = "storm";

    public Event(String type){
        this.type = type;
        switch (type){
            default:
                range =1+Math.random()*3;
                growthRate = 0.5+Math.random();
                int x = (int)(Math.random()*Globals.TILES);
                int y = (int)(Math.random()*Globals.TILES);
                center = new Point(x,y);

        }
        Log.d(TAG,type+" x:"+center.x+" y:"+center.y+" range:"+range);
    }
    public Event(String type,int x, int y, double range, double growthRate){
        this.type = type;
        this.range = range;
        this.growthRate = growthRate;
        this.center = new Point(x,y);
    }

    protected double getRate(Trajectory t){
        double distance = pointLineDistance(new Point(t.getFrom().getxCoord(),t.getFrom().getyCoord()),new Point(t.getTo().getxCoord(),t.getTo().getyCoord()));
        if(distance ==0)
            return 0.5;
        double rate = k*range/distance;
        if(rate>0.5)
            return 0.5;
        else if(rate<0.01)
            return 0;
        else
            return rate;
    };

    protected void tick(){
        range *= growthRate;
        if(over = range<1)
            range = 0;
        if(growthRate>0.5)
            growthRate -= 0.1;
    }

    protected double pointLineDistance(Point from, Point to){
        double abs = Math.abs((to.x-from.x)*(from.y-center.y)-(to.y-from.y)*(from.x-center.x));
        double sqrt = Math.sqrt(Math.pow(to.x-from.x,2)+Math.pow(to.y-from.y,2));
        return abs/sqrt;
    }

    public boolean isOver() {
        return over;
    }

    public String getType() {
        return type;
    }
}
