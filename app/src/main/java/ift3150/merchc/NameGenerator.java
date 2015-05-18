package ift3150.merchc;

/**
 * Created by Diego on 2015-03-22.
 */
public class NameGenerator {

//@TODO name generation as a function of type (as u said), perhaps a random access to a type-name db table built from an xml
    public static String generateName(String type) {
        return "joe biden" +(int)(Math.random()*100);
    }
}
