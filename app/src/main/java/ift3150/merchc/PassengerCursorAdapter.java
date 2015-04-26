package ift3150.merchc;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Diego on 2015-04-23.
 */
public class PassengerCursorAdapter extends SimpleCursorAdapter implements View.OnClickListener {
    final static int BOARD_BUTTON_COLOR = 0xff558855;
    final static String BOARD_BUTTON_TEXT = "board";
    static final int BOARD_ROW_BACKGROUND = 0xffcccccc;
    final static int DUMP_BUTTON_COLOR = 0xff885555;
    final static String DUMP_BUTTON_TEXT = "dump";
    static final int DUMP_ROW_BACKGROUND = 0xffffffff;

    static final String TAG = "PassengerCursorAdapter";

    LayoutInflater inflater;

    public PassengerCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
//refactor with from and to
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null){
            row = inflater.inflate(R.layout.row_passenger,parent,false);
        }
        
        //get views
        TextView tvType = (TextView) row.findViewById(R.id.passengerType);
        TextView tvName = (TextView) row.findViewById(R.id.passengerName);
        TextView tvDestination = (TextView) row.findViewById(R.id.passengerDestination);
        TextView tvFee = (TextView) row.findViewById(R.id.passengerFee);
        TextView tvWeight = (TextView) row.findViewById(R.id.passengerWeight);
        TextView tvVolume = (TextView) row.findViewById(R.id.passengerVolume);
        Button passengerButton = (Button) row.findViewById(R.id.passengerButton);

        Cursor cursor = getCursor();
        int columnIndex;
        
        cursor.moveToPosition(position);
        
        //set views
        columnIndex = cursor.getColumnIndex(DbHelper.C_TYPE);
        tvType.setText(cursor.getString(columnIndex));
        columnIndex = cursor.getColumnIndex(DbHelper.C_NAME);
        tvName.setText(cursor.getString(columnIndex));
        columnIndex = cursor.getColumnIndex(DbHelper.C_DESTINATION);
        tvDestination.setText(cursor.getString(columnIndex));
        columnIndex = cursor.getColumnIndex(DbHelper.C_FEE);
        tvFee.setText(cursor.getInt(columnIndex)+" $");
        columnIndex = cursor.getColumnIndex(DbHelper.C_WEIGHT);
        tvWeight.setText(cursor.getFloat(columnIndex)+" kg");
        columnIndex = cursor.getColumnIndex(DbHelper.C_VOLUME);
        tvVolume.setText(cursor.getFloat(columnIndex)+" m3");
        
        //button
        columnIndex = cursor.getColumnIndex(DbHelper.C_CONTAINER);
        boolean onBoat = Globals.boat.getName().equals(cursor.getString(columnIndex));
        if(!onBoat){
            passengerButton.setText(BOARD_BUTTON_TEXT);
            //passengerButton.setBackgroundColor(BOARD_BUTTON_COLOR);
            //row.setBackgroundColor(BOARD_ROW_BACKGROUND);
        }else{
            passengerButton.setText(DUMP_BUTTON_TEXT);
            //passengerButton.setBackgroundColor(DUMP_BUTTON_COLOR);
            //row.setBackgroundColor(DUMP_ROW_BACKGROUND);
        }
        passengerButton.setOnClickListener(this);


        return row;


    }


    @Override
    public void onClick(View v) {

    }
}
