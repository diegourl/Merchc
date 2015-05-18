package ift3150.merchc;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.*;

import java.io.File;

public class MainMenu extends Activity {
    private static final String TAG = "MainMenu";
    final String startUpBoatFileName = "myboat.xml";
    final String startupMapFileName = "myisland.xml";
    final String saveName = "yolo";
    final static String FIRSTGAMEFLAG = "firstGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Globals.dbHelper = new DbHelper(getApplicationContext());
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean firstGame = preferences.getBoolean(FIRSTGAMEFLAG, true);
        Log.d(TAG,"firstGame :" + firstGame);
        if(firstGame) { //if first time, load game button is dropped. but not forgotten
            View view = findViewById(R.id.buttonLoadGame);
            view.setVisibility(View.GONE);
        }
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

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean firstGame = preferences.getBoolean(FIRSTGAMEFLAG, true);
        if(!firstGame)
            clearDB();
        File rootdir = Environment.getExternalStorageDirectory();
        File boatFile = new File(rootdir, startUpBoatFileName);
        File mapFile = new File(rootdir, startupMapFileName);
        XmlLoader xmlLoader = new XmlLoader(boatFile,mapFile,saveName);
        if(xmlLoader.load()) {
            if(firstGame){
                preferences = getPreferences(MODE_PRIVATE); //game has been played and now load game is available
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(FIRSTGAMEFLAG,false);
                editor.commit();
            }
            loadGame(view);
        }
        else {
            toast = Toast.makeText(this,"Unable to read startup file..." ,Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    public void loadGame(View view){
        Toast toast = Toast.makeText(this,"loading game..." ,Toast.LENGTH_SHORT);
        toast.show();
        Globals.saveName = saveName; //maybe move this from here i don't know. what do i know?godlaksd;oiasdg;olaksno;adslg;alsdn
        Globals.boat = Globals.loadBoat(saveName);
        if(Globals.boat ==null){
            toast = Toast.makeText(this,"Unable to load boat." ,Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Intent intent = new Intent(this, IslandActivity.class);
        startActivity(intent);
    }

    private void clearDB() {
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        String [] args = {saveName};
        db.delete(DbHelper.T_BOAT,DbHelper.C_FILENAME + " = ?" ,args);
        db.delete(DbHelper.T_ISLANDS,DbHelper.C_FILENAME + " = ?",args);
        db.delete(DbHelper.T_TRAJECTORIES,DbHelper.C_FILENAME + " = ?" ,args);
        db.delete(DbHelper.T_PASSENGERS,DbHelper.C_FILENAME + " = ?",args);
        db.delete(DbHelper.T_CREW,DbHelper.C_FILENAME + " = ?",args);
        db.delete(DbHelper.T_RESOURCES,DbHelper.C_FILENAME + " = ?" ,args);
        db.delete(DbHelper.T_EQUIPMENT,DbHelper.C_FILENAME + " = ?" ,args);
        db.close();
    }


}
