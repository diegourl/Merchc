package ift3150.merchc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Diego on 2015-04-23.
 */
public class CrewCursorAdapter extends SimpleCursorAdapter implements View.OnClickListener{
    final static int HIRE_BUTTON_COLOR = 0xff558855;
    final static String HIRE_BUTTON_TEXT = "hire";
    static final int HIRE_ROW_BACKGROUND = 0xffcccccc;
    final static int FIRE_BUTTON_COLOR = 0xff885555;
    final static String FIRE_BUTTON_TEXT = "fire";
    static final int FIRE_ROW_BACKGROUND = 0xffffffff;

    static final String TAG = "CrewCursorAdapter";
    LayoutInflater inflater;
    Context context;

    public CrewCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
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
        String type = cursor.getString(columnIndex);
        tvType.setText(type);
        columnIndex = cursor.getColumnIndex(DbHelper.C_NAME);
        String name = cursor.getString(columnIndex);
        tvName.setText(name);
        columnIndex = cursor.getColumnIndex(DbHelper.C_UPKEEP);
        int upkeep = cursor.getInt(columnIndex);
        tvUpkeep.setText(upkeep+" food");
        columnIndex = cursor.getColumnIndex(DbHelper.C_SALARY);
        int salary = cursor.getInt(columnIndex);
        tvSalary.setText(salary+" $");
        columnIndex = cursor.getColumnIndex(DbHelper.C_WEIGHT);
        int weight = cursor.getInt(columnIndex);
        tvWeight.setText(weight+" kg");
        columnIndex = cursor.getColumnIndex(DbHelper.C_VOLUME);
        int volume = cursor.getInt(columnIndex);
        tvVolume.setText(volume+" m3");

        Crew c = new Crew(type,weight,volume,name,upkeep,salary);

        //button
        columnIndex = cursor.getColumnIndex(DbHelper.C_CONTAINER);
        String container = cursor.getString(columnIndex);
        crewButton.setTag(R.id.crewName,c);
        crewButton.setTag(R.id.crewButton,container);
        Log.d(TAG,"container "+container);
        if(!Globals.boat.getName().equals(container)){
            crewButton.setText(HIRE_BUTTON_TEXT);
            //crewButton.setBackgroundColor(HIRE_BUTTON_COLOR);
            //row.setBackgroundColor(HIRE_ROW_BACKGROUND);
        }else{
            crewButton.setText(FIRE_BUTTON_TEXT);
            //crewButton.setBackgroundColor(FIRE_BUTTON_COLOR);
            //row.setBackgroundColor(FIRE_ROW_BACKGROUND);
        }
        crewButton.setOnClickListener(this);

        return row;


    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        switch(b.getId()){
            case R.id.crewButton :
                Crew c = (Crew) b.getTag(R.id.crewName);
                String container = (String) b.getTag(R.id.crewButton);
                ContentValues values = new ContentValues();
                String selection = DbHelper.C_FILENAME + " = ? and "+DbHelper.C_NAME+" = ? and "+DbHelper.C_TYPE+" = ?";
                String [] selectArgs = new String[]{Globals.saveName,c.getName(),c.getType()};
                if(container.equals(Globals.boat.getName())){
                    values.put(DbHelper.C_CONTAINER,Globals.boat.getCurrentIsland().getName());
                    Globals.boat.addWeight(-c.getWeight());
                    Globals.boat.addVolume(-c.getVolume());
                    /*b.setTag(R.id.passengerButton,Globals.boat.getCurrentIsland().getName());
                    b.setText(BOARD_BUTTON_TEXT);*/
                }else{
                    values.put(DbHelper.C_CONTAINER,Globals.boat.getName());
                    Toast toast;
                    if((Globals.boat.getTotalWeight()+c.getWeight())>Globals.boat.getMaxWeight()){
                        toast = Toast.makeText(context, "You're too heavy!", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }
                    if((Globals.boat.getTotalVolume()+c.getVolume())>Globals.boat.getMaxVolume()){
                        toast = Toast.makeText(context,"You don't have enough space!" ,Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }
                    /*if((c.getSalary())>Globals.boat.getMoney()){
                        toast = Toast.makeText(context,"You're out of money!" ,Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }*/
                    Globals.boat.addWeight(c.getWeight());
                    Globals.boat.addVolume(c.getVolume());
                    /*b.setTag(R.id.passengerButton,Globals.boat.getName());
                    b.setText(DUMP_BUTTON_TEXT);*/
                }
                db.update(DbHelper.T_CREW,values,selection,selectArgs);
        }
        String selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
        String [] selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.boat.getCurrentIsland().getName()};
        String order = DbHelper.C_CONTAINER+", "+DbHelper.C_TYPE+" "+ (Globals.boat.getName().compareTo(Globals.boat.getCurrentIsland().getName())<0?"ASC":"DESC");
        changeCursor(db.query(DbHelper.T_CREW,null,selection,selectArgs,null,null,order));
        db.close();
        Log.d(TAG, "new boat stats: " + Globals.boat.getTotalWeight() + " " + Globals.boat.getTotalVolume() + " " + Globals.boat.getMoney());
        ((IslandActivity)context).setBoatStats();
    }
}
