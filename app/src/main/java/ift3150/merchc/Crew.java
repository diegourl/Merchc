package ift3150.merchc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego on 2015-03-11.
 */
public class Crew extends Cargo {

    private float salary;
    private float upkeep;

    public Crew(String type){
        super(type);
        inflate(type);
    }

    public Crew(String type, float weight, float volume, String name, float upkeep, float salary) {
        super(weight,volume,name);
        this.upkeep = upkeep;
        this.salary = salary;
        this.type = type;
    }

    protected void inflate(String type){
        switch (type){
            case "sailor": this.weight = 0; this.volume = 0; this.salary = 2; this.upkeep = 3; break;
            case "doctor": this.weight = 0; this.volume = 0; this.salary = 4; this.upkeep = 3; break;
            case "mercenary": this.weight = 0; this.volume = 0; this.salary = 3; this.upkeep = 4;break;
            case "spiritguide": this.weight = 0; this.volume = 0; this.salary = 0; this.upkeep = 0;break;

        }
        name = NameGenerator.generateName(type);

    }

    public Map<String,String> toMap(){
        Map<String,String> m = new HashMap<>();
        m.put(DbHelper.C_NAME,name);
        m.put(DbHelper.C_WEIGHT,weight+"");
        m.put(DbHelper.C_VOLUME,volume+"");
        m.put(DbHelper.C_TYPE,type);
        m.put(DbHelper.C_UPKEEP,upkeep+"");
        m.put(DbHelper.C_SALARY,salary+"");
        return m;
    }


    public float getSalary() {
        return salary;
    }

    public float getUpkeep() {
        return upkeep;
    }
}
