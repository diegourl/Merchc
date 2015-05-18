package ift3150.merchc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Diego on 2015-03-25.
 */
public class XmlLoader {
    private static final String TAG = "Loader";
    private File boatFile;
    private File mapFile;
    private String saveName;

    public XmlLoader(File boatFile, File mapFile, String saveName){
        this.boatFile = boatFile;
        this.mapFile = mapFile;
        this.saveName = saveName;
    }


    public boolean load() {        
        try{
            readMap();
            readBoat();
        }catch (IOException e) {
            Log.d(TAG,"ioerror: " + e.getMessage());
            return false;
        }
        catch(XmlPullParserException e){
            Log.d(TAG,"ioerror: " + e.getMessage());
            return false;
        }
        return true;
    }


    public void readMap()throws IOException,XmlPullParserException{

        Inflater inflater = new Inflater(saveName);
        BufferedReader reader = new BufferedReader(new FileReader(mapFile));
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(reader);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "map");//namespace is null
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("archipelago")) {
                readArchipelago(parser, inflater);
            }
        }
        inflater.closeDB();
        return;// inflater.getMap();
        //InputStream fos = openFileInput(FILENAME);
        // fos.write(string.getBytes());
        // fos.close();
    }

    public void readArchipelago(XmlPullParser parser, Inflater inflater)throws IOException,XmlPullParserException{

        String name =  parser.getAttributeValue(null,"name");
        int Xcoordinate =  Integer.parseInt(parser.getAttributeValue(null, "Xcoordinate"));
        int Ycoordinate =  Integer.parseInt(parser.getAttributeValue(null, "Ycoordinate"));
        Log.d(TAG, "Reading Archipelago : " + name +" "+Xcoordinate + " " + Ycoordinate);
        inflater.inflateArchipelago(name,Xcoordinate,Ycoordinate);
        inflater.setCurrentArchipelago(name);
        parser.nextTag();
        String tag = parser.getName();
        while (!tag.equals("archipelago")){
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                if (tag.equals("island")) {
                    readIsland(parser, inflater);
                }else if(tag.equals("trajectory")){
                    readTrajectory(parser, inflater);
                }
            }

            parser.nextTag();
            tag = parser.getName();

        }
    }

    public void readBoat() throws IOException,XmlPullParserException{

        Inflater inflater = new Inflater(saveName);
        BufferedReader reader = new BufferedReader(new FileReader(boatFile));
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(reader);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "boat");//namespace is null

        String name =  parser.getAttributeValue(null,"name");
        String type =  parser.getAttributeValue(null, "class");
        String starting =  parser.getAttributeValue(null, "startingIsland");
        int money = Integer.parseInt(parser.getAttributeValue(null,"money"));

        Log.d(TAG, "Reading Boat : " + name + " " + type);
        inflater.setContainerName(name);
        inflater.inflateBoat(name,type,starting,money);
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // Starts by looking for the entry tag
            switch (tag) {
                case "passenger":
                    readPassenger(parser, inflater);
                    break;
                case "resource":
                    readResource(parser, inflater);
                    break;
                case "equipment":
                    readEquipment(parser, inflater);
                    break;
                case "crew":
                    readCrew(parser, inflater);
                    break;
            }
        }
        inflater.completeBoatStats(name);
        inflater.closeDB();
        return;// inflater.getBoat();
        //InputStream fos = openFileInput(FILENAME);
        // fos.write(string.getBytes());
        // fos.close();


    }

    public void readIsland(XmlPullParser parser, Inflater inflater)throws XmlPullParserException, IOException{

        String name =  parser.getAttributeValue(null,"name");
        int Xcoordinate =  Integer.parseInt(parser.getAttributeValue(null, "Xcoordinate"));
        int Ycoordinate =  Integer.parseInt(parser.getAttributeValue(null, "Ycoordinate"));
        String industry =  parser.getAttributeValue(null, "industry");
        Log.d(TAG, "Reading Island : " + name +" "+Xcoordinate + " " + Ycoordinate + " " + industry);
        inflater.inflateIsland(name,Xcoordinate,Ycoordinate,industry);
        inflater.setContainerName(name);
        parser.nextTag();
        String tag = parser.getName();
        while (!tag.equals("island")){
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                switch (tag) {
                    case "passenger":
                        readPassenger(parser, inflater);
                        break;
                    case "resource":
                        readResource(parser, inflater);
                        break;
                    case "equipment":
                        readEquipment(parser, inflater);
                        break;
                    case "crew":
                        readCrew(parser, inflater);
                        break;
                }
            }
            parser.nextTag();
            tag = parser.getName();

        }


    }

    private void readTrajectory(XmlPullParser parser, Inflater inflater)throws XmlPullParserException, IOException{

        int days =  Integer.parseInt(parser.getAttributeValue(null, "days"));
        String to =  parser.getAttributeValue(null, "to");
        String from =  parser.getAttributeValue(null, "from");
        Log.d(TAG, "Reading Trajectory : " + days +" "+to + " " + from);
        inflater.inflateTrajectory(days, to, from);
        //inflater.inflateIsland(name,Xcoordinate,Ycoordinate,industry);
        //inflater.setContainerName(name);

    }

    public void readPassenger(XmlPullParser parser, Inflater inflater) {

        String type = parser.getAttributeValue(null, "type");
        String destination = parser.getAttributeValue(null, "destination");
        inflater.inflatePassenger(type,destination);
        Log.d(TAG, "Reading Passenger : " + type +" "+destination);
    }

    public void readResource(XmlPullParser parser, Inflater inflater) {

        String type = parser.getAttributeValue(null, "type");
        int amount = Integer.parseInt(parser.getAttributeValue(null, "amount"));
        inflater.inflateResource(type, amount);
        Log.d(TAG, "Reading Resource : " + type +" "+amount);
    }

    public void readEquipment(XmlPullParser parser, Inflater inflater) {

        String type = parser.getAttributeValue(null, "type");
        int amount = Integer.parseInt(parser.getAttributeValue(null, "amount"));
        inflater.inflateEquipment(type, amount);
        Log.d(TAG, "Reading Equipment : " + type +" "+amount);
    }

    public void readCrew(XmlPullParser parser, Inflater inflater) {

        String type = parser.getAttributeValue(null, "type");
        int amount = Integer.parseInt(parser.getAttributeValue(null, "amount"));
        inflater.inflateCrew(type, amount);
        Log.d(TAG, "Reading Crew : " + type +" "+amount);
    }



    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
