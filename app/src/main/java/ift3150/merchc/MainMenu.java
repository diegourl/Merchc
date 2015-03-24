package ift3150.merchc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.*;
public class MainMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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
        Intent intent = new Intent(this, LoadingActivity.class);
        Bundle args = new Bundle();
        Toast toast = Toast.makeText(this,"starting new game..." ,Toast.LENGTH_SHORT);
        toast.show();
        args.putCharSequence("map","myisland.xml");
        args.putCharSequence("boat","myboat.xml");
        startActivity(intent);
    }

    public void loadGame(View view){
        Toast toast = Toast.makeText(this,"loading game..." ,Toast.LENGTH_SHORT);
        toast.show();

    }
}
