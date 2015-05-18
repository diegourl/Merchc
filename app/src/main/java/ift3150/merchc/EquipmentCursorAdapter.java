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
public class EquipmentCursorAdapter extends SimpleCursorAdapter implements View.OnClickListener{
    static final String TAG = "EquipmentCursorAdapter";
    LayoutInflater inflater;
    Context context;
    public EquipmentCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        Cursor cursor = getCursor();
        if(!cursor.moveToFirst()) return 0;
        int count = 1;
        int columnIndex = cursor.getColumnIndex(DbHelper.C_TYPE);
        String previousType = cursor.getString(columnIndex);
        String currentType;
        while(cursor.moveToNext()){
            currentType = cursor.getString(columnIndex);
            if(!currentType.equals(previousType)) count++;
            previousType = currentType;
        }
        return count;
        //return super.getCount();
    }

    @Override//CtlC, CtlV from rca
//refactor with from and to
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null){
            row = inflater.inflate(R.layout.row_equipment,parent,false);
        }

        TextView tvType = (TextView) row.findViewById(R.id.equipmentType);
        TextView tvWeight = (TextView) row.findViewById(R.id.equipmentWeight);
        TextView tvVolume = (TextView) row.findViewById(R.id.equipmentVolume);
        TextView tvBoatAmount = (TextView) row.findViewById(R.id.equipmentBoatAmount);
        TextView tvIslandAmount = (TextView) row.findViewById(R.id.equipmentIslandAmount);
        TextView tvPrice = (TextView) row.findViewById(R.id.equipmentPrice);
        Button buttonPlus = (Button) row.findViewById(R.id.equipmentBAddToBoat);
        Button buttonMinus = (Button) row.findViewById(R.id.equipmentBAddToIsland);

        Cursor cursor = getCursor();

        String previousType;
        cursor.moveToFirst();

        int cursorPosition = 0;
        int columnIndex = cursor.getColumnIndex(DbHelper.C_TYPE);

        while(position-->0){ //cursorPosition keeps track of what distance the jumps should cover, position, of how many jumps you should take
            previousType = cursor.getString(columnIndex);
            if(cursor.moveToNext()) {
                if (previousType.equals(cursor.getString(columnIndex))) { //change for while for more containers. Rest as is.
                    cursorPosition++;
                    cursor.moveToNext();
                }
                cursorPosition++;
            }
        }
        //set equipment type
        cursor.moveToPosition(cursorPosition);
        previousType = cursor.getString(columnIndex);
        Log.d(TAG, previousType);
        Equipment e = new Equipment(previousType);
        tvType.setText(e.getType());


        //fetch and set equipment weight
        tvWeight.setText(e.getWeight()+" kg");

        //set equipment volume
        tvVolume.setText(e.getVolume()+" m3");

        //set equipment amount on boat
        //weak...refactor...
        columnIndex = cursor.getColumnIndex(DbHelper.C_CONTAINER);
        int boatAmount = 0;
        int islandAmount = 0;
        if(Globals.boat.getName().equals(cursor.getString(columnIndex))) {
            columnIndex = cursor.getColumnIndex(DbHelper.C_AMOUNT);
            boatAmount = cursor.getInt(columnIndex);
            columnIndex = cursor.getColumnIndex(DbHelper.C_TYPE);
            if(cursor.moveToNext()&&previousType.equals(cursor.getString(columnIndex))){
                columnIndex = cursor.getColumnIndex(DbHelper.C_AMOUNT);
                islandAmount = (cursor.getInt(columnIndex));
            }
        }else{
            columnIndex = cursor.getColumnIndex(DbHelper.C_AMOUNT);
            islandAmount = (cursor.getInt(columnIndex));
        }
        tvBoatAmount.setText(boatAmount+"");

        //determine and set equipment amount on island


        tvIslandAmount.setText(islandAmount+"");

        //fetch price for type and amount and set
        int price = Equipment.getPrice(e.getType(), islandAmount);
        tvPrice.setText(price+ "$");

        buttonPlus.setEnabled(islandAmount > 0);

        buttonPlus.setTag(R.id.equipmentType,e);
        buttonPlus.setTag(R.id.equipmentBoatAmount, boatAmount);
        buttonPlus.setTag(R.id.equipmentIslandAmount, islandAmount);
        buttonPlus.setTag(R.id.equipmentPrice,price);

        buttonMinus.setEnabled(boatAmount>0);

        buttonMinus.setTag(R.id.equipmentType,e);
        buttonMinus.setTag(R.id.equipmentBoatAmount, boatAmount);
        buttonMinus.setTag(R.id.equipmentIslandAmount, islandAmount);
        buttonMinus.setTag(R.id.equipmentPrice,price);

        buttonPlus.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);


        return row;
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();

        Equipment e = (Equipment) b.getTag(R.id.equipmentType);
        int price = (Integer) b.getTag(R.id.equipmentPrice);
        int boatAmount = (Integer) b.getTag(R.id.equipmentBoatAmount);
        int islandAmount = (Integer) b.getTag(R.id.equipmentIslandAmount);

        String selection = DbHelper.C_FILENAME + " = ? and "+DbHelper.C_TYPE+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgsBoat = new String[]{Globals.saveName,e.getType(),Globals.boat.getName()};
        String [] selectArgsIsland = new String [] {Globals.saveName,e.getType(),Globals.boat.getCurrentIsland().getName()};

        ContentValues boatValues = new ContentValues();
        ContentValues islandValues = new ContentValues();
        Log.d(TAG,"button pressed type : "+e.getType());

        boatValues.put(DbHelper.C_FILENAME,Globals.saveName);
        boatValues.put(DbHelper.C_CONTAINER,Globals.boat.getName());
        boatValues.put(DbHelper.C_TYPE,e.getType());
        islandValues.put(DbHelper.C_FILENAME,Globals.saveName);
        islandValues.put(DbHelper.C_CONTAINER,Globals.boat.getCurrentIsland().getName());
        islandValues.put(DbHelper.C_TYPE,e.getType());

        switch(b.getId()){
            case R.id.equipmentBAddToBoat:
                Log.d(TAG,"plus");
                Toast toast;
                if((Globals.boat.getTotalWeight()+e.getWeight())>Globals.boat.getMaxWeight()){
                    toast = Toast.makeText(context,"You're too heavy!" ,Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                if((Globals.boat.getTotalVolume()+e.getVolume())>Globals.boat.getMaxVolume()){
                    toast = Toast.makeText(context,"You don't have enough space!" ,Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                if((e.getAmount()*price)>Globals.boat.getMoney()){
                    toast = Toast.makeText(context,"You're out of money!" ,Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }

                boatValues.put(DbHelper.C_AMOUNT, boatAmount + e.getAmount());
                islandValues.put(DbHelper.C_AMOUNT, islandAmount - e.getAmount());
                long boatResponse = db.insertWithOnConflict(DbHelper.T_EQUIPMENT,null,boatValues,SQLiteDatabase.CONFLICT_REPLACE);
                long islandResponse = db.insertWithOnConflict(DbHelper.T_EQUIPMENT,null,islandValues,SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG,"boatrep "+ boatResponse);
                Log.d(TAG,"islerep "+ islandResponse);

                if((boatResponse>=0)&&(islandResponse>=0)){
                    Globals.boat.addMoney(-price);
                    Globals.boat.addWeight(e.getWeight());
                    Globals.boat.addVolume(e.getVolume());
                }
                break;

            case R.id.equipmentBAddToIsland:
                Log.d(TAG,"minus");
                boatValues.put(DbHelper.C_AMOUNT, boatAmount - e.getAmount());
                islandValues.put(DbHelper.C_AMOUNT, islandAmount + e.getAmount());

                if((db.insertOrThrow(DbHelper.T_EQUIPMENT,null,boatValues)>=0)&&(db.insertOrThrow(DbHelper.T_EQUIPMENT,null,islandValues)>=0)){
                    Globals.boat.addMoney(price);
                    Globals.boat.addWeight(-e.getWeight());
                    Globals.boat.addVolume(-e.getVolume());
                };
                break;
        }
        selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
        String [] selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.boat.getCurrentIsland().getName()};
        String order = DbHelper.C_TYPE+", "+DbHelper.C_CONTAINER+" "+ (Globals.boat.getName().compareTo(Globals.boat.getCurrentIsland().getName())<0?"ASC":"DESC");
        changeCursor(db.query(DbHelper.T_EQUIPMENT,null,selection,selectArgs,null,null,order));
        //db.close();
        Log.d(TAG, "new boat stats: " + Globals.boat.getTotalWeight() + " " + Globals.boat.getTotalVolume() + " " + Globals.boat.getMoney());
        ((IslandActivity)context).setBoatStats();

    }
}
