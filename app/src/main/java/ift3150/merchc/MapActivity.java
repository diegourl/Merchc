package ift3150.merchc;

/**
 * Created by yashMaster on 11/04/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Map;


public class MapActivity extends Activity {
    private static final String TAG = "MapActivity";
    public MapPopulator mapPopulator;
    TextView tv1;
    TextView tv2;
    ScrollView verticalScroll;
    HorizontalScrollView horizontalScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        RelativeLayout relativeLayout= (RelativeLayout ) findViewById(R.id.linearLayout_gridtableLayout);
        FrameLayout frameLayout= (FrameLayout ) findViewById(R.id.frameLayout_annotate);
        verticalScroll= (ScrollView) findViewById(R.id.scroll_view);
        horizontalScroll= (HorizontalScrollView) findViewById(R.id.horizontal_scroll_view);
        //verticalScroll.smoothScrollTo(2000, 2000);
        this.mapPopulator= new MapPopulator(this);
        tv1 = (TextView)findViewById(R.id.textView_money);
        tv2 = (TextView)findViewById(R.id.textView_food);
        mapPopulator.updateDollars(tv1);
        mapPopulator.updateFood(tv2);
        mapPopulator.populate(relativeLayout);
        mapPopulator.annotate(frameLayout);


    }
    public void updateMenu(){
        mapPopulator.updateDollars(tv1);
        mapPopulator.updateFood(tv2);
    }
    public void backToList(View view){
        Intent intent = new Intent(this, MainMenu.class);//needs to be IslandActivity
        startActivity(intent);
        Log.d(TAG, "HERE");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void resetTrajectories(){
        Log.d(TAG,"RESETTING");
        FrameLayout frameLayout= (FrameLayout) findViewById(R.id.frameLayout_annotate);
        clearImageView(frameLayout);
        mapPopulator.resetTrajectories();
    }

    private void clearImageView(ViewGroup v) {
        boolean doBreak = false;
        // Log.d(TAG,"IN CLEAR");
        // while (!doBreak) {
        int childCount = v.getChildCount();
        int i;
        //Log.d(TAG,"inCLEAR2");
        for(i=0; i<childCount; i++) {
            // Log.d(TAG,"CLEARING3"+ i);
            View currentChild = v.getChildAt(i);
            if (currentChild instanceof ImageView) {
                if (currentChild.getId() != R.id.boat) {
                    v.removeView(currentChild);
                    i--;
                }
            }
        }

        Log.d(TAG,"CLEARING");
    }
    //attemped STack overflow hack
    public void verticalScrollTo(final int i){
        verticalScroll.post(new Runnable() {

            @Override
            public void run() {
                verticalScroll.scrollTo(0, i);
            }
        });
    }
    public void horizontalScrollTo(int i){
        verticalScroll.scrollTo(i,0);
    }


}