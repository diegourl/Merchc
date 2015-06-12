package ift3150.merchc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego on 2015-03-11.
 */
public class Equipment extends Cargo{

    public Equipment(String type){
        this(type,1);
    }

    public Equipment(String type, int amount){
        super(type);
        this.amount = amount;
    }

    public Equipment(int weight, int volume,String name){
        super(weight,volume,name);
    }

    //@TODO add equipment types
    protected void inflate(String type){
        switch (type){
            case "cannon": this.weight = 3; this.volume = 2; break;
            case "pistol": this.weight = 1; this.volume = 1; break;
            case "compass": this.weight = 0; this.volume = 0; break;
            case "telescope": this.weight = 0; this.volume = 0; break;
            case "medicine": this.weight = 1; this.volume = 2; break;
            case "anchor": this.weight = 3; this.volume = 1;break;
            case "si_map": this.weight = 0; this.volume = 0;break;
            case "st_map": this.weight = 0; this.volume = 0;break;
            case "flag": this.weight = 0; this.volume = 0;break;
            case "junk": this.weight = 2; this.volume = 3;break;

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

    @Override
    public int getVolume() {
        return volume*amount;
    }

    @Override
    public int getWeight() {
        return weight*amount;
    }


    public static int getPrice(String type, int amount){
        if(amount == 0) amount = 1;
        switch (type){
            case "cannon": return 70/amount;
            case "pistol": return 50/amount;
            case "compass": return 20/amount;
            case "telescope": return 20/amount;
            case "medicine": return 10/amount;
            case "anchor": return 40/amount;
            case "si_map": return 150/amount;
            case "st_map": return 100/amount;
            case "flag": return 15/amount;
            case "junk": return 30/amount;
            default: return 20/amount;

        }
    }


}
