package ift3150.merchc;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Boat extends Container{

    private static final String TAG = "Boat";

    private static final int MAX_REPAIR = 5;


    private String type;
    private float speed;
    private int repair;
    private int food;
    private int totalWeight;
    private int totalVolume;
    private int money;

    private Island currentIsland;

    private int maxWeight;
    private int maxVolume;
    private int minCrew;
    /*int minCrew;
    Island island;
    String sType;asdlfkjlaskdlkjasdf
    float dollars;*/

    public Boat(String name, String type, int money){
        this.name = name;
        this.type = type;
        this.money = money;
        inflate();
    }

    public Boat(String name, String type, int repair, Island island, int money){
        this.name = name;
        this.type = type;
        inflate();
        this.repair = repair;
        this.currentIsland = island;
        this.money = money;
    }

    //@TODO add boat types
    private void inflate(){
        switch(type){
            case "canoe": {
                speed=40;
                maxWeight=300;
                maxVolume=2000;
                minCrew=1;
                break;
            }

            case "skiff":{
                speed=60;
                maxWeight=600;
                maxVolume=4000;
                minCrew=3;
                break;
            }
            default: {


            }

        }
        this.repair = MAX_REPAIR;
        this.totalWeight = 0;
        this.totalVolume = 0;
        //	addCrew(1, 1);
    }

    public float getBaseSpeed(){ return speed;}

    //@TODO upgrade
    public float getSpeed(){
        return speed;
    }

    //windforce is needed as some equipments changes they way wind affects speed
    //as is weight can slow you down up to 50% of basepeed
    public double getSpeed( double windSpeed){
        return speed*(1-0.75*totalWeight/maxWeight) + windSpeed*0.15;
    }
    
    public String getType(){ return type;}
    
    public int getRepair(){ return repair;}

    public Island getCurrentIsland() {
        return currentIsland;
    }

    public void setCurrentIsland(Island currentIsland) {
        this.currentIsland = currentIsland;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(int totalVolume) {
        this.totalVolume = totalVolume;
    }

    public int getMoney() {
        return money;
    }

    public void addWeight(int weight){
        totalWeight += weight;
    }

    public void addVolume(int volume){
        totalVolume += volume;
    }

    public void addMoney(int m) { money += m;     }

    public int getMaxWeight() {
        return maxWeight;
    }

    public int getMaxVolume() {
        return maxVolume;
    }

    public int getMinCrew() {
        return minCrew;
    }

    public void addFood(int foodValue) {
        food += foodValue;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food){ this.food = food;}

    public List<Happening> tick(){
        ArrayList<Happening> happenings = new ArrayList<>(2);
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        String query = "select sum("+DbHelper.C_SALARY+") as "+DbHelper.C_SALARY+", sum("+DbHelper.C_UPKEEP+") as "+DbHelper.C_UPKEEP+" from "+DbHelper.T_CREW+" where "+DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgs = {Globals.saveName,name};
        Cursor c = db.rawQuery(query, selectArgs);

        int money = 0;
        int food = 0;
        if(c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(DbHelper.C_SALARY);
            money = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_UPKEEP);
            food = c.getInt(columnIndex);
        }

        happenings.add(payCrew(money));
        happenings.add(feedCrew(food));

        query = "update "+DbHelper.T_PASSENGERS+" set "+DbHelper.C_DAYSLEFT+" = ("+DbHelper.C_DAYSLEFT+" - 1) where "+DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        db.rawQuery(query,selectArgs);
        c.close();
        db.close();

        return happenings;
    }

    private Happening feedCrew(int food) {
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        String selection = DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ? and "+DbHelper.C_TYPE+" = ?";
        String [] columns = {DbHelper.C_AMOUNT};
        int [] foodWeights = new int [Resource.foodTypes.length];
        int [] foodAmounts = new int [Resource.foodTypes.length];
        int totalFood = 0;
        for(int i = 0; i < Resource.foodTypes.length;i++){
            String [] selectArgs = {Globals.saveName,name,Resource.foodTypes[i]};
            Cursor c = db.query(DbHelper.T_RESOURCES,columns,selection,selectArgs,null,null,null);
            if(!c.moveToFirst()){foodAmounts[i] = 0; foodWeights[i] = 0; continue;}
            int columnIndex = c.getColumnIndex(DbHelper.C_AMOUNT);
            foodAmounts[i]= c.getInt(columnIndex);
            foodWeights[i] = (new Resource(Resource.foodTypes[i],1)).getWeight();

            totalFood += foodWeights[i]*foodAmounts[i];
        }

        int foodSpent = 0;

        for(int i = 0; i < Resource.foodTypes.length;i++){
            String [] selectArgs = {Globals.saveName,name,Resource.foodTypes[i]};
            //String query = "update "+DbHelper.T_RESOURCES+" set "+DbHelper.C_AMOUNT+" = ("+DbHelper.C_AMOUNT+" - "+") where "+DbHelper.C_FILENAME+" = ? and "+DbHelper.C_CONTAINER+" = ?";
            if(foodAmounts[i]<=0) continue;
            ContentValues values = new ContentValues();
            int contributionAmount =(int) Math.ceil((1.0)*food*foodAmounts[i]/(totalFood));
            contributionAmount = Math.min(contributionAmount,foodAmounts[i]);
            values.put(DbHelper.C_AMOUNT,foodAmounts[i]-contributionAmount);
            db.update(DbHelper.T_RESOURCES,values,selection,selectArgs);
            foodSpent += contributionAmount*foodWeights[i];
            Log.d(TAG,"contribution :"+Resource.foodTypes[i]+" "+contributionAmount);

        }
        Log.d(TAG, "totalFood :"+totalFood);

        addFood(-foodSpent);
        if(food>totalFood) {
            return Happening.happen(Event.FOOD_SHORTAGE);
        }
        return null;
    }

    private Happening payCrew(int money) {
        if(money>this.money) {
            addMoney(-this.money);
            return Happening.happen(Event.MONEY_SHORTAGE);
        }
        addMoney(-money);
        return null;

    }

    public int getMaxRepair(){return MAX_REPAIR;}


}
