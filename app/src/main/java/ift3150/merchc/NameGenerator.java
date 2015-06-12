package ift3150.merchc;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Diego on 2015-03-22.
 */
public class NameGenerator {
    private static final String TAG = "NameGen";
    private File nameFile;


    public NameGenerator(File nameFile){
        this.nameFile = nameFile;
    }


    public boolean load() {
        try{
            readNames();
        }catch (IOException e) {
            Log.d(TAG, "ioerror: " + e.getMessage());
            return false;
        }
        catch(XmlPullParserException e){
            Log.d(TAG,"ioerror: " + e.getMessage());
            return false;
        }
        return true;
    }


    public void readNames()throws IOException,XmlPullParserException{
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        BufferedReader reader = new BufferedReader(new FileReader(nameFile));
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        ContentValues values = new ContentValues();
        parser.setInput(reader);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "names");//namespace is null
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String typeIndividual = parser.getName();
            if (typeIndividual.equals("crew") || typeIndividual.equals("passenger")) {
                String name =  parser.getAttributeValue(null,"Name");
                String type =  parser.getAttributeValue(null, "Type");

                values.clear();
                values.put(DbHelper.C_NAME,name);
                values.put(DbHelper.C_TYPE,type);
                db.insertOrThrow(DbHelper.T_NAMES, null, values);
            }
        }

        return;
    }

    //@TODO name generation as a function of type (as u said), perhaps a random access to a type-name db table built from an xml
    public static String generateName(String type) {
        return "joe biden" +(int)(Math.random()*100);
    }
}
