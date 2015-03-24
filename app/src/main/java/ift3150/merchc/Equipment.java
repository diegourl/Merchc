package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */
public class Equipment extends Cargo{
    private int amount;

    public Equipment(String type){
        super(type);
    }

    public Equipment(String type, int amount){
        super(type);
        this.amount = amount;
    }

    public Equipment(float weight, float volume,String name){
        super(weight,volume,name);
    }
    //@TODO add equipment types
    protected void inflate(String type){
        switch (type){
            case "cannon": {this.weight = 3; this.volume = 2;}
            case "pistol": {this.weight = 1; this.volume = 1;}
            case "compass": {this.weight = 0; this.volume = 0;}
            case "telescope": {this.weight = 0; this.volume = 0;}
            case "medicine": {this.weight = 1; this.volume = 2;}
            case "anchor": {this.weight = 3; this.volume = 1;}
            case "si_map": {this.weight = 0; this.volume = 0;}
            case "st_map": {this.weight = 0; this.volume = 0;}
            case "flag": {this.weight = 0; this.volume = 0;}
            case "junk": {this.weight = 2; this.volume = 3;}

        }

    }
}
