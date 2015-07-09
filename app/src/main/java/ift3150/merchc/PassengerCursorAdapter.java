package ift3150.merchc;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    Context context;

    public PassengerCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
//refactor with from and to
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null){
            row = inflater.inflate(R.layout.row_passenger,parent,false);
        }
        
        //get views
        ImageView iv = (ImageView) row.findViewById(R.id.passengerImage);
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
        String type = cursor.getString(columnIndex);
        tvType.setText(type);
        iv.setImageResource(iv.getContext().getResources().getIdentifier("drawable/portrait_passenger_" + type, null, iv.getContext().getPackageName()));
        /*iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));*/
        columnIndex = cursor.getColumnIndex(DbHelper.C_NAME);
        String name = cursor.getString(columnIndex);
        //tvName.setText(name);
        tvName.setText("");
        columnIndex = cursor.getColumnIndex(DbHelper.C_DESTINATION);
        String destination = cursor.getString(columnIndex);
        tvDestination.setText(destination);
        columnIndex = cursor.getColumnIndex(DbHelper.C_FEE);
        int fee = cursor.getInt(columnIndex);
        tvFee.setText(fee+" $");
        columnIndex = cursor.getColumnIndex(DbHelper.C_DAYSLEFT);
        int daysLeft = cursor.getInt(columnIndex);
        //set daysLeft
        columnIndex = cursor.getColumnIndex(DbHelper.C_WEIGHT);
        int weight = cursor.getInt(columnIndex);
        tvWeight.setText(weight+" kg");
        columnIndex = cursor.getColumnIndex(DbHelper.C_VOLUME);
        int volume = cursor.getInt(columnIndex);
        tvVolume.setText(volume+" m3");

        Passenger p = new Passenger(type,weight,volume,name,destination,fee,daysLeft);
        
        //button
        columnIndex = cursor.getColumnIndex(DbHelper.C_CONTAINER);
        String container = cursor.getString(columnIndex);
        passengerButton.setTag(R.id.passengerName, p);
        passengerButton.setTag(R.id.passengerButton, container);
        if(!Globals.boat.getName().equals(container)){
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
        Button b = (Button) v;
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();
        switch(b.getId()){
            case R.id.passengerButton :
                Passenger p = (Passenger) b.getTag(R.id.passengerName);
                String container = (String) b.getTag(R.id.passengerButton);
                ContentValues values = new ContentValues();
                String selection = DbHelper.C_FILENAME + " = ? and "+DbHelper.C_NAME+" = ? and "+DbHelper.C_TYPE+" = ?";
                String [] selectArgs = new String[]{Globals.saveName,p.getName(),p.getType()};
                if(container.equals(Globals.boat.getName())){
                    values.put(DbHelper.C_CONTAINER,Globals.boat.getCurrentIsland().getName());
                    Globals.boat.addWeight(-p.getWeight());
                    Globals.boat.addVolume(-p.getVolume());
                    /*b.setTag(R.id.passengerButton,Globals.boat.getCurrentIsland().getName());
                    b.setText(BOARD_BUTTON_TEXT);*/
                }else{
                    values.put(DbHelper.C_CONTAINER,Globals.boat.getName());
                    Toast toast;
                    if((Globals.boat.getTotalWeight()+p.getWeight())>Globals.boat.getMaxWeight()){
                        toast = Toast.makeText(context, "You're too heavy!", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }
                    if((Globals.boat.getTotalVolume()+p.getVolume())>Globals.boat.getMaxVolume()){
                        toast = Toast.makeText(context,"You don't have enough space!" ,Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }
                    /*if((c.getSalary())>Globals.boat.getMoney()){
                        toast = Toast.makeText(context,"You're out of money!" ,Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }*/
                    Globals.boat.addWeight(p.getWeight());
                    Globals.boat.addVolume(p.getVolume());
                    //Globals.boat.addMoney(p.getFee());
                    /*b.setTag(R.id.passengerButton,Globals.boat.getName());
                    b.setText(DUMP_BUTTON_TEXT);*/
                }
                db.update(DbHelper.T_PASSENGERS,values,selection,selectArgs);
        }
        String selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
        String [] selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.boat.getCurrentIsland().getName()};
        String order = DbHelper.C_CONTAINER+", "+DbHelper.C_TYPE+" "+ (Globals.boat.getName().compareTo(Globals.boat.getCurrentIsland().getName())<0?"ASC":"DESC");
        changeCursor(db.query(DbHelper.T_PASSENGERS,null,selection,selectArgs,null,null,order));
        db.close();
        Log.d(TAG,"new boat stats: "+ Globals.boat.getTotalWeight()+" "+Globals.boat.getTotalVolume()+" "+Globals.boat.getMoney());
        ((IslandActivity)context).setBoatStats();


    }
}
