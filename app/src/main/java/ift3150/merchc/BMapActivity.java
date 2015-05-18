package ift3150.merchc;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class BMapActivity extends FragmentActivity{
    private static final String TAG = "BMapActivity";
    protected int TILE_SIZE;
    protected static final int TILES = Globals.TILES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmap);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        TILE_SIZE = size.x/TILES;

        if (savedInstanceState != null) {
            return;
        }


        ArchipelagoFragment currentArchipelagoFragment = new ArchipelagoFragment();
        currentArchipelagoFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().add(R.id.fragment_container, currentArchipelagoFragment).commit();

        setIslandName();
        setBoatStats();


        /*ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.green);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relMap);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(TILE_SIZE,TILE_SIZE);
        layoutParams.topMargin = Globals.boat.getCurrentIsland().yCoord*TILE_SIZE;
        layoutParams.leftMargin = Globals.boat.getCurrentIsland().xCoord*TILE_SIZE;
        iv.setLayoutParams(layoutParams);
        rl.addView(iv);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bmap, menu);
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

    @Override
    //@TODO add Event save
    public void onBackPressed() {
        String message = Globals.saveBoat()?"Game saved.":"Game could not be saved.";
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent(this,MainMenu.class);
        startActivity(intent);
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

    public void setIslandName(){
        TextView islandNameTV = (TextView) findViewById(R.id.islandName);
        islandNameTV.setText(Globals.boat.getCurrentIsland().getName());
    }

    public int getTILE_SIZE() {
        return TILE_SIZE;
    }

    public int getTILES(){return TILES;}
}
