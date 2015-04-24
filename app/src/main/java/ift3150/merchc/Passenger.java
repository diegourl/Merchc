package ift3150.merchc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego on 2015-03-11.
 */
public class Passenger extends Cargo{
    private String destination;
    private int fee;
    private int daysLeft;

    public Passenger(String type, String destination){
        super(type);
        this.destination = destination;
    }

    public Passenger(String type, float weight, float volume, String name, String destination, int fee, int daysLeft){
        super(weight,volume,name);
        this.destination = destination;
        this.fee = fee;
        this.daysLeft = daysLeft;
        this.type = type;
    }

    //@TODO add passenger types
    protected void inflate(String type){
        switch (type){
            case "farmer": this.weight = 4; this.volume = 2;break;
            case "missionary": this.weight = 1; this.volume = 1;break;
            case "witch": this.weight = 2; this.volume = 2;break;
            case "viking": this.weight = 3; this.volume = 2;break;
            case "tradesperson": this.weight = 2; this.volume = 2;break;
            case "assassin": this.weight = 2; this.volume = 2;break;
            case "oldman": this.weight = 2; this.volume = 1;break;
        }
        fee = 10;
        daysLeft = 0;
        name = NameGenerator.generateName(type);

    }


    public String getDestination() {
        return destination;
    }

    public int getFee() {
        return fee;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public Map<String,String> toMap(){
        Map<String,String> m = new HashMap<>();
        m.put(DbHelper.C_NAME,name);
        m.put(DbHelper.C_WEIGHT,weight+"");
        m.put(DbHelper.C_VOLUME,volume+"");
        m.put(DbHelper.C_TYPE,type);
        m.put(DbHelper.C_DESTINATION,destination);
        m.put(DbHelper.C_DAYSLEFT,daysLeft+"");
        m.put(DbHelper.C_FEE,fee+"");
        return m;
    }
}
