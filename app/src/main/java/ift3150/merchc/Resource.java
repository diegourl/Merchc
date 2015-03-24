package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */
public class Resource extends Cargo {


    public Resource(String type){
        super(type);
    }

    public Resource(String type, int amount){
        super(type);
        this.amount = amount;
    }

    public Resource(float weight, float volume,String name){
        super(weight,volume,name);
    }

    //@TODO add resource types
    protected void inflate(String type){
        switch (type){
            case "metal" : {weight = 5; volume = 1;}
            case "fish" : {weight = 2; volume = 3;}
            case "food" : {weight = 1; volume = 4;}
            case "coconut" : {weight = 3; volume = 2;}
            case "water" : {weight = 4; volume = 1;}
            case "booze" : {weight = 4; volume = 1;}
            case "tobacco" : {weight = 2; volume = 1;}

        }

    }

}
