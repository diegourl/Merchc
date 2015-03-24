package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */

import java.util.ArrayList;


//Extended by Boat and Island
public class Container {

    protected String name;
    protected ArrayList<Crew> crew= new ArrayList<Crew>();
    protected ArrayList<Resource> resources = new ArrayList<>();
    protected ArrayList<Passenger> passengers= new ArrayList<Passenger>();
    protected ArrayList<Equipment> equipment= new ArrayList<Equipment>();

/*
    public void setResources(Ressources res){
        this.resources=res;
    }

    */

    public void setPassengers(ArrayList<Passenger> pass){
        this.passengers= pass;
    }
    public void addPassenger(Passenger pass){
        this.passengers.add(pass);
    }
    public void setEquipment(ArrayList<Equipment> equip){
        this.equipment= equip;
    }
    public void addEquipment(Equipment equip){
        this.equipment.add(equip);
    }
    public void setCrew(ArrayList<Crew> crew){
        this.crew=crew;
    }
    public void addCrew(Crew crew){
        this.crew.add(crew);
    }
    public void setResource(ArrayList<Resource> resources){
        this.resources=resources;
    }
    public void addResource(Resource resource){
        this.resources.add(resource);
    }
    public String getName(){return this.name;}


}
