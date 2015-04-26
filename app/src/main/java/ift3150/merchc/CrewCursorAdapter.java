package ift3150.merchc;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Diego on 2015-04-23.
 */
public class CrewCursorAdapter extends SimpleCursorAdapter {
    final static int HIRE_BUTTON_COLOR = 0xff558855;
    final static String HIRE_BUTTON_TEXT = "hire";
    static final int HIRE_ROW_BACKGROUND = 0xffcccccc;
    final static int FIRE_BUTTON_COLOR = 0xff885555;
    final static String FIRE_BUTTON_TEXT = "fire";
    static final int FIRE_ROW_BACKGROUND = 0xffffffff;

    static final String TAG = "CrewCursorAdapter";
    LayoutInflater inflater;

    public CrewCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
//refactor with from and to
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null){
            row = inflater.inflate(R.layout.row_crew,parent,false);
        }

        //get views
        TextView tvType = (TextView) row.findViewById(R.id.crewType);
        TextView tvName = (TextView) row.findViewById(R.id.crewName);
        TextView tvUpkeep = (TextView) row.findViewById(R.id.crewUpkeep);
        TextView tvSalary = (TextView) row.findViewById(R.id.crewSalary);
        TextView tvWeight = (TextView) row.findViewById(R.id.crewWeight);
        TextView tvVolume = (TextView) row.findViewById(R.id.crewVolume);
        Button crewButton = (Button) row.findViewById(R.id.crewButton);

        Cursor cursor = getCursor();
        int columnIndex;

        cursor.moveToPosition(position);

        //set views
        columnIndex = cursor.getColumnIndex(DbHelper.C_TYPE);
        tvType.setText(cursor.getString(columnIndex));
        columnIndex = cursor.getColumnIndex(DbHelper.C_NAME);
        tvName.setText(cursor.getString(columnIndex));
        columnIndex = cursor.getColumnIndex(DbHelper.C_UPKEEP);
        tvUpkeep.setText(cursor.getInt(columnIndex)+" food");
        columnIndex = cursor.getColumnIndex(DbHelper.C_SALARY);
        tvSalary.setText(cursor.getInt(columnIndex)+" $");
        columnIndex = cursor.getColumnIndex(DbHelper.C_WEIGHT);
        tvWeight.setText(cursor.getFloat(columnIndex)+" kg");
        columnIndex = cursor.getColumnIndex(DbHelper.C_VOLUME);
        tvVolume.setText(cursor.getFloat(columnIndex)+" m3");

        //button
        columnIndex = cursor.getColumnIndex(DbHelper.C_CONTAINER);
        boolean onBoat = Globals.boat.getName().equals(cursor.getString(columnIndex));
        if(!onBoat){
            crewButton.setText(HIRE_BUTTON_TEXT);
            //crewButton.setBackgroundColor(HIRE_BUTTON_COLOR);
            //row.setBackgroundColor(HIRE_ROW_BACKGROUND);
        }else{
            crewButton.setText(FIRE_BUTTON_TEXT);
            //crewButton.setBackgroundColor(FIRE_BUTTON_COLOR);
            //row.setBackgroundColor(FIRE_ROW_BACKGROUND);
        }

        return row;


    }
}
