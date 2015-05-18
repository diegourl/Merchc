package ift3150.merchc;

import android.graphics.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego
 * I will work on this one later.
 */
public class Trajectory {
    private Island from;
    private Island to;
    private boolean feasible;
    private int duration;
    private int moneyCost;
    private int foodCost;
    private Map<String,Double> averageRisk;

    public Trajectory(Island from, Island to){
        this.from = from;
        this.to = to;
        averageRisk = new HashMap<>();
    }

    public Double getRisk(String key){
        return averageRisk.get(key);
    }

    public void setRisk(String key, Double value){
        averageRisk.put(key, value);
    }

    public Map<String,Double> getAverageRisks(){
        return averageRisk;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMoneyCost() {
        return moneyCost;
    }

    public void setMoneyCost(int moneyCost) {
        this.moneyCost = moneyCost;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public void setFoodCost(int foodCost) {
        this.foodCost = foodCost;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public Island getFrom() {
        return from;
    }

    public Island getTo() {
        return to;
    }
}
