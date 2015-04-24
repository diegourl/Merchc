package ift3150.merchc;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;


public class Boat extends Container{

    private static final int MAXREPAIR = 5;
    private int speed;
    private int repair;
    private float weight;
    private float volume;
    private float maxWeight;
    private float maxVolume;
    private int minCrew;
    private String type;
    private Island currentIsland;
    /*int minCrew;
    Island island;
    String sType;asdlfkjlaskdlkjasdf
    float dollars;*/

    public Boat(String name, String type){
        this.name = name;
        this.type = type;
        inflate();
    }

    public Boat(String name, String type, int repair, Island island){
        this.name = name;
        this.type = type;
        inflate();
        this.repair = repair;
        this.currentIsland = island;
    }
    //@TODO add boat types
    private void inflate(){
        switch(type){
            case "canoe": {
                speed=20;
                maxWeight=3;
                maxVolume=2;
                minCrew=1;
                break;
            }

            case "skiff":{
                speed=40;
                maxWeight=24;
                maxVolume=16;
                minCrew=5;
                break;
            }
            default: {

                System.out.println("Error, incorrect Boat type");
            }

        }
        this.repair = MAXREPAIR;
        //	addCrew(1, 1);
    }

    public int getBaseSpeed(){ return speed;}

    public float getSpeed(){
        return speed;
    }
    
    public String getType(){ return type;}
    
    public int getRepair(){ return repair;}

    public Island getCurrentIsland() {
        return currentIsland;
    }

    public void setCurrentIsland(Island currentIsland) {
        this.currentIsland = currentIsland;
    }



/*

    //type 1=seaman= player in the boat!
    public boolean addCrew(Crew crew){
        if(island.crew.remove(crew)){
            this.crew.add(crew);
            return true;
        }else return false;
    }


    //type 1=seaman= player in the boat!
    public boolean addPass(Passenger pass){
        if(island.passengers.remove(pass)){
            passengers.add(pass);
            return true;
        }else return false;
    }


    //type 1=seaman= player in the boat!
    public boolean addEquip(Equipment equip){
        if(island.equipment.remove(equip)){
            this.equipment.add(equip);
            return true;
        }
        return false;
    }


    //type 1=seaman= player in the boat!
    //on transfer un nombred d'unite vers le bateau
    //metal, fish, wood, water, booze
    public boolean addRes(int metals, int fish, int wood, int water, int booze,
                          int coconuts, int tobacco){
        if(this.resources.transferTo(island.resources, metals, fish, wood, water, booze,
                coconuts,  tobacco)){
            return true;
        }
        return false;
    }

    //using equipment means you can only use it once
    public boolean useEquip(Cargo cargo){
        if(equipment.remove(cargo))return true;
        return false;
    }
    //Passengers are not removed in their usage
    public boolean usePass(Cargo cargo){
        if(equipment.contains(cargo))return true;
        return false;
    }
    //Crewmembers are not removed in their usage
    public boolean useCrew(Cargo cargo){
        if(equipment.contains(cargo))return true;
        return false;
    }
*/
    //in units of food
  /*
    public int getDailyCrewMaintenance(){
        int total=3;//3 unites of food per day. yes above average this is ME that is eating!
        for( Crew cm: this.crew){
            total+=cm.getUpkeep();
        }
        return total;
    }
    //in units of food
    public int getDailyCrewCost(){
        int total=0;
        for( Crew cm: this.crew){
            total+=cm.getSalary();
        }
        return total;
    }
    //units of food
    public int getTrajUpkeep(Trajectory traj){
        return (int) traj.getTrajectoryLength()*getDailyCrewMaintenance();
    }
    //in $$
    public float getTrajCost(Trajectory traj){
        return traj.getTrajectoryLength()*getDailyCrewCost();
    }

    public String printTrajs(){
        String s="";
        for(Trajectory t :island.trajectories){
            s+=t.toString()+" at a cost of:"+getTrajCost(t)+"$ and "+getTrajUpkeep(t)+ "units of food\n";
        }
        return s;
    }

    public boolean buyEquipment(int type, int number){
        int realType= island.equipment.get(type-1).type;//if its first in the list it should be 0 in index
        float price=island.market.equiPrices[realType-1];
        if(dollars>price && number>0 && freeVolume>island.equipment.get(type-1).getVolume()
                && freeWeight>island.equipment.get(type-1).getWeight()){
            Equipment temp=island.equipment.remove(type-1);
            if(temp != null){
                equipment.add(temp);
                number--;
                dollars-=price;
                freeWeight-=temp.getWeight();
                freeVolume-=temp.getVolume();
                System.out.println("Success! "+temp.name+"added to ship.");
                return true;
            }
        }
        else System.out.println("Insufficient funds or insufficient boat capacity");
        return false;
    }
    public boolean buyResources(int type, int number){
        float price=island.getResourcePrice(type-1);
        float addedW=Ressources.getWeight(type)*number;//type here is from 1-7
        float addedV=Ressources.getVolume(type)*number;
        int[] transfer={0,0,0,0,0,0,0};
        transfer[type-1]=number;// set the variable to modify

        if(((price*number)<=dollars) && (freeWeight>= addedW) && (freeVolume>=addedV)){
            if(island.resources.transferTo(this.resources, transfer[0], transfer[1], transfer[2], transfer[3], transfer[4], transfer[5], transfer[6])){
                dollars-=price*number;
                freeWeight-=addedW;
                freeVolume-=addedV;
                return true;
            }else System.out.println("Insufficient resources on island for transfer");
        }else System.out.println("Insufficient funds or insufficient boat capacity");
        return false;
    }

    public boolean sellResources(int type, int number){
        float price=island.getResourcePrice(type-1);
        int[] transfer={0,0,0,0,0,0,0};
        transfer[type-1]=number;// set the variable to modify

        if(this.resources.transferTo(this.island.resources, transfer[0], transfer[1], transfer[2], transfer[3], transfer[4], transfer[5], transfer[6])){
            dollars+=price*number;
            freeWeight+= Ressources.getWeight(type)*number;
            freeVolume+= Ressources.getVolume(type)*number;
            return true;
        }else System.out.println("Insufficient resources on island for transfer");

        return false;
    }
    public boolean board(int num){
        Passenger pass=island.passengers.get(num-1);
        if(pass.volume<freeVolume && pass.weight<freeWeight){
            if(passengers.add(pass)){
                System.out.println(pass.name+" boarded");
                freeWeight-=pass.weight;
                freeVolume-=pass.volume;
                island.passengers.remove(pass);
                return true;
            }
        }
        return false;
    }
    //not sure its very "safe" gonna throw an array outtb if the number if wrong...trycatch?
    //doesn't look very safe

    public boolean hire(int num){
        crew.add(island.crew.remove(num-1));
        return true;
    }

    //
    public int sail(int num){
        Trajectory rout= island.trajectories.get(num-1);
        if(getTrajUpkeep(rout)< resources.totalFood()){
            if(getTrajCost(rout)<dollars){
                if(rout.events.size()!=0){
                    //foreach event logic to come
                }
                else System.out.println("Uneventful journey.");
                dollars-=getTrajCost(rout);
                float preVol= resources.calcVolume();
                float preWei= resources.calcWeight();
                resources.feed(getTrajUpkeep(rout));
                System.out.println("yop");
                freeVolume += preVol- resources.calcVolume();
                freeWeight += preWei- resources.calcWeight();
                this.island = rout.destination;
                return (int) rout.getTrajectoryLength();
            }
            else System.out.println("Insufficient funds for journey ( remove some crew or sell some resources).");
        }
        else System.out.println("Insufficient food for journey ( remove some crew or buy more food resources).");
        return 0;
    }*/
}
