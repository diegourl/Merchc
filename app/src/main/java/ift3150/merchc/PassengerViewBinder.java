package ift3150.merchc;

import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Created by Diego on 2015-04-13.
 * We use SimpleAdapter to adapt the passenger to a listview. It offers an interface with only one method. This class
 * implements that interface. An instance of this class is set as the viewbinder for the simpleadapter. When attempting to bind the array
 * of data to the array of views, simpleadapter call the setviewvalue we implement here for each of these views. It's important to note
 * that it calls it one view at a time (not all together) and if setviewvalue returns true, it means we were able to bind the value
 * to the view and simpleadapter moves on to the next one. If setviewvalue returns false, simpleadapter binds it on its own. Since
 * simple adapter is perfectly capable of binding strings and images to textviews and images, we only treat some cases and return false
 * otherwise.
 */
public class PassengerViewBinder implements SimpleAdapter.ViewBinder {
    final static int BOARD_BUTTON_COLOR = 0xff558855;
    final static String BOARD_BUTTON_TEXT = "BOARD";
    final static int DUMP_BUTTON_COLOR = 0xff885555;
    final static String DUMP_BUTTON_TEXT = "DUMP";

    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        switch (view.getId()){
            case R.id.passengerFee :
                ((TextView)view).setText(data + " $");
                return true;
            case R.id.passengerWeight:
                ((TextView)view).setText(data + " kg");
                return true;
            case R.id.passengerVolume:
                ((TextView)view).setText(data + " m3");
                return true;
            case R.id.passengerButton:
                if(!data.equals(Globals.boat.getName())) {
                    ((Button) view).setText(BOARD_BUTTON_TEXT);
                    ((Button) view).setBackgroundColor(BOARD_BUTTON_COLOR);
                }else{
                    ((Button) view).setText(DUMP_BUTTON_TEXT);
                    ((Button) view).setBackgroundColor(DUMP_BUTTON_COLOR);
                }
                return true;
        }
        return false;
    }
}
