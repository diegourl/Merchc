package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */
public abstract class Cargo extends Typable {

    protected float weight;
    protected float volume;
    protected int amount;

    public Cargo(String type){
        super(type);
    }
    public Cargo(float weight, float volume, String name){
        this.weight = weight;
        this.volume = volume;
        this.name = name;

    }
    @Override
    protected abstract void inflate(String type);

    public float getWeight(){
        return weight;
    }
    private void setWeight(float weight){
        this.weight= weight;
    }
    public float getVolume(){
        return volume;
    }
    private void setVolume(float volume){
        this.volume= volume;
    }
    public int getAmount(){ return amount;}

}
