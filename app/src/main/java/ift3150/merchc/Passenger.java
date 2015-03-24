package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */
public class Passenger extends Cargo{
    private String destination;
    private int fee;
    private int daysAlong;

    public Passenger(String type, String destination){
        super(type);
        this.destination = destination;
    }

    public Passenger(float weight, float volume,String name){
        super(weight,volume,name);
    }

    //@TODO add passenger types
    protected void inflate(String type){
        switch (type){
            case "farmer": {this.weight = 4; this.volume = 2;}
            case "missionary": {this.weight = 1; this.volume = 1;}
            case "witch": {this.weight = 2; this.volume = 2;}
            case "viking": {this.weight = 3; this.volume = 2;}
            case "tradesperson": {this.weight = 2; this.volume = 2;}
            case "assassin": {this.weight = 2; this.volume = 2;}
            case "oldman": {this.weight = 2; this.volume = 1;}
        }
        fee = 10;
        daysAlong = 0;
        name = NameGenerator.generateName(type);

    }


    public String getDestination() {
        return destination;
    }

    public int getFee() {
        return fee;
    }

    public int getDaysAlong() {
        return daysAlong;
    }
}
