package ift3150.merchc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego on 2015-03-11.
 */
public class Resource extends Cargo {

    static final String [] foodTypes = {"coconut","fish"};

    @Override
    public int getVolume() {
        return volume*amount;
    }

    @Override
    public int getWeight() {
        return weight*amount;
    }

    public Resource(String type){
        this(type,1);
    }

    public Resource(String type, int amount){
        super(type);
        this.amount = amount;
    }

    public Resource(int weight, int volume,String name){
        super(weight,volume,name);
    }

    //@TODO add resource types
    protected void inflate(String type){
        switch (type){
            case "metal" : weight = 5; volume = 1;break;
            case "fish" : weight = 1; volume = 1;break;
            case "food" : weight = 1; volume = 1;break;
            case "coconut" : weight = 1; volume = 2;break;
            case "water" : weight = 1; volume = 1;break;
            case "wood" : weight = 3; volume = 1; break;
            case "booze" : weight = 2; volume = 1;break;
            case "tobacco" : weight = 1; volume = 2;break;

        }

    }



    public Map<String,String> toMap(){
        Map<String,String> m = new HashMap<>();
        m.put(DbHelper.C_NAME,name);
        m.put(DbHelper.C_WEIGHT,weight+"");
        m.put(DbHelper.C_VOLUME,volume+"");
        m.put(DbHelper.C_TYPE,type);
        m.put(DbHelper.C_AMOUNT,amount+"");
        return m;
    }


//refactor into nonstatic
    public static int getPrice(String type, int amount){
        if(amount < 1) amount = 1;
        switch (type){   //keep alphabetic pls.

            case "booze" : return 40/amount;
            case "coconut" : return 10/amount;
            case "fish" : return 20/amount;
            case "food" : return 15/amount;
            case "metal" : return 30/amount;
            case "tobacco" : return 40/amount;
            case "water" : return 30/amount;
            case "wood" : return 10/amount;

            default: return 10/amount;

        }

    }

    public int getFoodValue() {
        switch(type){
            case "coconut":
            case "fish":
            case "food":
                return weight*amount;
            default: return 0;
        }
    }
}
