package ift3150.merchc;

/**
 * Created by yashMaster on 11/04/2015.
 */

        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.FrameLayout;
        import android.widget.RelativeLayout;



public class MapActivity extends ActionBarActivity {
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        RelativeLayout relativeLayout= (RelativeLayout ) findViewById(R.id.linearLayout_gridtableLayout);
        FrameLayout frameLayout= (FrameLayout ) findViewById(R.id.frameLayout_annotate);
        MapPopulator mapPopulator= new MapPopulator(this);
        mapPopulator.populate(relativeLayout);
        mapPopulator.annotate(frameLayout);

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
}