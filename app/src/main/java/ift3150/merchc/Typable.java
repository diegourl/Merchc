package ift3150.merchc;

/**
 * Created by Diego on 2015-03-11.
 */
abstract class Typable {
    String name;
    String type;

    public Typable(String type){
        this.type=type;
        inflate(this.type);
    }

    public Typable(){}

    protected abstract void inflate(String type);

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    protected void setName(String name){ this.name = name;}

    public boolean equals(Typable o){
        if(o.type == this.type)return true;
        return false;
    }
}