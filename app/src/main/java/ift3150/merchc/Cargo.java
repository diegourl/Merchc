package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */
public abstract class Cargo extends Typable {

    protected int weight;
    protected int volume;
    protected int amount;

    public Cargo(String type){
        super(type);
    }
    public Cargo(int weight, int volume, String name){
        this.weight = weight;
        this.volume = volume;
        this.name = name;

    }
    @Override
    protected abstract void inflate(String type);

    public int getWeight(){
        return weight;
    }
    private void setWeight(int weight){
        this.weight= weight;
    }
    public int getVolume(){
        return volume;
    }
    private void setVolume(int volume){
        this.volume= volume;
    }
    public int getAmount(){ return amount;}

}
