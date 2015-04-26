package ift3150.merchc;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Created by Diego on 2015-04-13.
 * No constructor needed.
 */
public class CrewViewBinder implements SimpleAdapter.ViewBinder{
    private static final String TAG = "CrewViewBinder";
    final static int HIRE_BUTTON_COLOR = 0xff558855;
    final static String HIRE_BUTTON_TEXT = "HIRE";
    final static int FIRE_BUTTON_COLOR = 0xff885555;
    final static String FIRE_BUTTON_TEXT = "FIRE";

    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        switch (view.getId()){
            case R.id.crewUpkeep :
                ((TextView)view).setText(data + " food");
                return true;
            case R.id.crewSalary :
                ((TextView)view).setText(data + " $");
                return true;
            case R.id.crewWeight:
                ((TextView)view).setText(data + " kg");
                return true;
            case R.id.crewVolume:
                ((TextView)view).setText(data + " m3");
                return true;
            case R.id.crewButton:
                if(!data.equals(Globals.boat.getName())) {
                    ((Button) view).setText(HIRE_BUTTON_TEXT);
                    ((Button) view).setBackgroundColor(HIRE_BUTTON_COLOR);
                }else{
                    ((Button) view).setText(FIRE_BUTTON_TEXT);
                    ((Button) view).setBackgroundColor(FIRE_BUTTON_COLOR);
                }
                return true;
        }
        return false;
    }
}
