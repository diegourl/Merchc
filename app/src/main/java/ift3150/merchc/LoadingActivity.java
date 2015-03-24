package ift3150.merchc;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;


public class LoadingActivity extends ActionBarActivity {
    private static final String TAG = "Loading";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        try{
            Globals.map = readMap("myisland.xml");
            Globals.boat = readBoat("myboat.xml");
        }catch (IOException e) {
            Toast toast = Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        catch(XmlPullParserException e){
            Toast toast = Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        Intent intent = new Intent(this,IslandActivity.class);
        startActivity(intent);
        finish();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Map<String,Island> readMap(String filename)throws IOException,XmlPullParserException{

            File rootdir = Environment.getExternalStorageDirectory();
            File file = new File(rootdir, filename);
            Inflater inflater = new Inflater(this, file);
            BufferedReader reader = new BufferedReader(new FileReader(file));
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
                if (name.equals("island")) {
                    readIsland(parser, inflater);
                }
            }
            inflater.closeDB();
            return inflater.getMap();
            //InputStream fos = openFileInput(FILENAME);
           // fos.write(string.getBytes());
           // fos.close();
    }

    public Boat readBoat(String filename) throws IOException,XmlPullParserException{

            File rootdir = Environment.getExternalStorageDirectory();
            File file = new File(rootdir, filename);
            Inflater inflater = new Inflater(this, file);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "boat");//namespace is null

            String name =  parser.getAttributeValue(null,"name");
            String type =  parser.getAttributeValue(null, "class");
            String starting =  parser.getAttributeValue(null, "startingIsland");

            Log.d(TAG, "Reading Boat : " + name +" "+type);
            inflater.inflateBoat(name,type, starting);
            inflater.setContainerName(name);
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tag = parser.getName();
                // Starts by looking for the entry tag
                if (tag.equals("passenger")) {
                    readPassenger(parser, inflater);
                }else if (tag.equals("resource")) {
                    readResource(parser, inflater);
                }else if (tag.equals("equipment")) {
                    readEquipment(parser, inflater);
                }else if (tag.equals("crew")) {
                    readCrew(parser, inflater);
                }
            }
            inflater.closeDB();
            return inflater.getBoat();
            //InputStream fos = openFileInput(FILENAME);
            // fos.write(string.getBytes());
            // fos.close();


    }

    public void readIsland(XmlPullParser parser, Inflater inflater)throws XmlPullParserException, IOException{

        String name =  parser.getAttributeValue(null,"name");
        float Xcoordinate =  Float.parseFloat(parser.getAttributeValue(null, "Xcoordinate"));
        float Ycoordinate =  Float.parseFloat(parser.getAttributeValue(null, "Ycoordinate"));
        String industry =  parser.getAttributeValue(null, "industry");
        Log.d(TAG, "Reading Island : " + name +" "+Xcoordinate + " " + Ycoordinate + " " + industry);
        inflater.inflateIsland(name,Xcoordinate,Ycoordinate,industry);
        inflater.setContainerName(name);
        parser.nextTag();
        String tag = parser.getName();
        while (!tag.equals("island")){
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                if (tag.equals("passenger")) {
                    readPassenger(parser, inflater);
                }else if (tag.equals("resource")) {
                    readResource(parser, inflater);
                }else if (tag.equals("equipment")) {
                    readEquipment(parser, inflater);
                }else if (tag.equals("crew")) {
                    readCrew(parser, inflater);
                }
            }
            parser.nextTag();
            tag = parser.getName();

        }


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
