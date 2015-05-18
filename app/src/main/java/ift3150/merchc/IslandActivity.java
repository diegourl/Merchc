package ift3150.merchc;


import android.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class IslandActivity extends FragmentActivity {
    private static final String TAG = "IslandActivity";
    private static final int NUM_TABS = 4;
    DbHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<Island> neighbours;
    ViewPager viewPager;
    FPAdapter fpAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_island);
        /*Globals.currentIsland.setResources(Globals.loadResources(Globals.currentIsland.getName()));
        Globals.currentIsland.setEquipment(Globals.loadEquipment(Globals.currentIsland.getName()));
        Globals.currentIsland.setPassengers(Globals.loadPassengers(Globals.currentIsland.getName()));
        Globals.currentIsland.setCrew(Globals.loadCrew(Globals.currentIsland.getName()));*/
        Log.d(TAG, "at island : " + Globals.boat.getCurrentIsland().getName());
        /*Intent intent = new Intent(this,MapActivity.class);
        startActivity(intent);*/

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        fpAdapter = new FPAdapter(getSupportFragmentManager(),this,NUM_TABS);
        viewPager = (ViewPager) findViewById(R.id.pager);
        //viewPager.setOffscreenPageLimit(NUM_TABS-1);
        viewPager.setAdapter(fpAdapter);
        setIslandName();
        setBoatStats();

    }

    public void setBoatStats() {
        TextView tv = (TextView) findViewById(R.id.statsWeight);
        tv.setText("wgt: "+Globals.boat.getTotalWeight()+"/"+Globals.boat.getMaxWeight());
        tv = (TextView) findViewById(R.id.statsVolume);
        tv.setText("vol: "+Globals.boat.getTotalVolume()+"/"+Globals.boat.getMaxVolume());
        tv = (TextView) findViewById(R.id.statsSpeed);
        tv.setText("speed: "+Globals.boat.getSpeed());
        tv = (TextView) findViewById(R.id.statsMoney);
        tv.setText(Globals.boat.getMoney()+"$");

    }

    @Override
    public void onBackPressed() {

        String message = Globals.saveBoat()?"Game saved.":"Game could not be saved.";
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent(this,MainMenu.class);
        startActivity(intent);
    }

    public void setIslandName(){
        TextView islandNameTV = (TextView) findViewById(R.id.islandName);
        islandNameTV.setText(Globals.boat.getCurrentIsland().getName());
    }

    protected void onResume() {
        super.onResume();


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


    public void goToMap(View view) {
        Intent intent = new Intent(this,BMapActivity.class);
        startActivity(intent);
    }
}

