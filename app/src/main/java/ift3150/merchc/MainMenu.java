package ift3150.merchc;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.*;

import java.io.File;

public class MainMenu extends ActionBarActivity {
    final String startUpBoatFileName = "myboat.xml";
    final String startupMapFileName = "myisland.xml";
    final String saveName = "yolo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Globals.dbHelper = new DbHelper(getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

    public void newGame(View view){
        Toast toast = Toast.makeText(this,"starting new game..." ,Toast.LENGTH_SHORT);
        toast.show();
        File rootdir = Environment.getExternalStorageDirectory();
        File boatFile = new File(rootdir, startUpBoatFileName);
        File mapFile = new File(rootdir, startupMapFileName);
        XmlLoader xmlLoader = new XmlLoader(boatFile,mapFile,saveName);
        if(xmlLoader.load())
            loadGame(view);
        else {
            toast = Toast.makeText(this,"Unable to read startup file..." ,Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void loadGame(View view){
        Toast toast = Toast.makeText(this,"loading game..." ,Toast.LENGTH_SHORT);
        toast.show();
        Globals.boat = Globals.loadBoat(saveName);
        initBoat();
        Globals.currentIsland = Globals.boat.getCurrentIsland();
        Intent intent = new Intent(this, IslandActivity.class);
        startActivity(intent);
    }

    private static void initBoat(){
        Globals.boat.setResources(Globals.loadResources(Globals.boat.getName()));
        Globals.boat.setEquipment(Globals.loadEquipment(Globals.boat.getName()));
        Globals.boat.setPassengers(Globals.loadPassengers(Globals.boat.getName()));
        Globals.boat.setCrew(Globals.loadCrew(Globals.boat.getName()));
    }
}
