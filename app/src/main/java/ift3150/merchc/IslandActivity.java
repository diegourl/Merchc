package ift3150.merchc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class IslandActivity extends ActionBarActivity {
    private static final String TAG = "IslandActivity";
    DbHelper dbHelper;
    SQLiteDatabase db;
    Boat boat;
    Island island;
    ArrayList<Island> neighbours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_island);
        boat = Globals.boat;
        Log.d(TAG,"boat: " + boat.name + " " + boat.getType());

        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();



        String [] columns = {DbHelper.C_CURRENTISLAND};
        String islandName = "";
        Cursor c = db.query(DbHelper.T_BOAT,columns,DbHelper.C_NAME + " = '" + boat.getName() + "'",null,null,null,null);
        if(c != null && c.moveToFirst()) {
            int isleNameIndex = c.getColumnIndex(DbHelper.C_CURRENTISLAND);
            islandName = c.getString(isleNameIndex);
            Log.d(TAG, "dbislandName: " + islandName);
        }

        /*c = db.query(DbHelper.T_ISLANDS,columns,DbHelper.C_NAME + " = '" + islandName + "'",null,null,null,null);
        if(c != null && c.moveToFirst()) {
            int isleNameIndex = c.getColumnIndex(DbHelper.C_CURRENTISLAND);
            String islandName = c.getString(isleNameIndex);
            Log.d(TAG, "dbislandName: " + isleName);
        }*/




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_island, menu);
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


}
