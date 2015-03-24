package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */
public class Crew extends Cargo {

    private float salary;
    private float upkeep;

    public Crew(String type){
        super(type);
    }

    public Crew(float weight, float volume,String name){
        super(weight,volume,name);
    }

    protected void inflate(String type){
        switch (type){
            case "sailor": {this.weight = 0; this.volume = 0; this.salary = 2; this.upkeep = 3;}
            case "doctor": {this.weight = 0; this.volume = 0; this.salary = 4; this.upkeep = 3;}
            case "mercenary": {this.weight = 0; this.volume = 0; this.salary = 3; this.upkeep = 4;}
            case "spiritguide": {this.weight = 0; this.volume = 0; this.salary = 0; this.upkeep = 0;}

        }
        name = NameGenerator.generateName(type);

    }


    public float getSalary() {
        return salary;
    }

    public float getUpkeep() {
        return upkeep;
    }
}
