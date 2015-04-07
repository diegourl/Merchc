package ift3150.merchc;

import android.app.ListFragment;
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

import java.util.ArrayList;


public class IslandActivity extends FragmentActivity {
    private static final String TAG = "IslandActivity";
    private static final int NUM_TABS = 4;
    DbHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<Island> neighbours;
    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_island);
        Globals.currentIsland.setResources(Globals.loadResources(Globals.currentIsland.getName()));
        Globals.currentIsland.setEquipment(Globals.loadEquipment(Globals.currentIsland.getName()));
        Globals.currentIsland.setPassengers(Globals.loadPassengers(Globals.currentIsland.getName()));
        Globals.currentIsland.setCrew(Globals.loadCrew(Globals.currentIsland.getName()));
        Log.d(TAG,Globals.currentIsland.getName());


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

    private static class MyPagerAdapter extends FragmentPagerAdapter{



        public MyPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                default : return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }
    }

    public static class MyListFragment extends ListFragment{

    }


}
