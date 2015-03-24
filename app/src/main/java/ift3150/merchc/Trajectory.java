package ift3150.merchc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diego
 * I will work on this one later.
 */
public class Trajectory {
    private Island from;
    private Island to;
    private Map<String,Event> events = new HashMap<>();

    public Trajectory(Island from, Island to){
        this.from = from;
        this.to = to;
    }

}
