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
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Diego on 2015-04-21.
 */
public class ResourceCursorAdapter extends SimpleCursorAdapter implements View.OnClickListener {
    static final String TAG = "ResourceCursorAdapter";
    LayoutInflater inflater;
    Context context;
    public ResourceCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
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

    @Override
//refactor with from and to
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null){
            row = inflater.inflate(R.layout.row_resource,parent,false);
        }

        TextView tvType = (TextView) row.findViewById(R.id.resourceType);
        ImageView iv = (ImageView) row.findViewById(R.id.resourceImage);
        TextView tvWeight = (TextView) row.findViewById(R.id.resourceWeight);
        TextView tvVolume = (TextView) row.findViewById(R.id.resourceVolume);
        TextView tvBoatAmount = (TextView) row.findViewById(R.id.resourceBoatAmount);
        TextView tvIslandAmount = (TextView) row.findViewById(R.id.resourceIslandAmount);
        TextView tvPrice = (TextView) row.findViewById(R.id.resourcePrice);
        Button buttonPlus = (Button) row.findViewById(R.id.resourceBAddToBoat);
        Button buttonMinus = (Button) row.findViewById(R.id.resourceBAddToIsland);

        Cursor cursor = getCursor();
        Log.d(TAG,"position: "+position);
        Log.d(TAG,"cursor position: "+cursor.getPosition());

        String previousType;
        cursor.moveToFirst();
        Log.d(TAG,"cursor first position: "+cursor.getPosition());


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
        //set resource type
        cursor.moveToPosition(cursorPosition);
        previousType = cursor.getString(columnIndex);
        Log.d(TAG, previousType);
        Resource r = new Resource(previousType);
        iv.setImageResource(iv.getContext().getResources().getIdentifier("drawable/portrait_resource_" + r.getType(), null, iv.getContext().getPackageName()));
        tvType.setText(r.getType());


        //fetch and set resource weight
        tvWeight.setText(r.getWeight()+" kg");

        //set resource volume
        tvVolume.setText(r.getVolume()+" m3");

        //set resource amount on boat
        //weak...refactor...
        columnIndex = cursor.getColumnIndex(DbHelper.C_CONTAINER);
        String container = cursor.getString(columnIndex);
        int boatAmount = 0;
        int islandAmount = 0;
        if(Globals.boat.getName().equals(container)) {
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

        //determine and set resource amount on island


        tvIslandAmount.setText(islandAmount+"");

        //fetch price for type and amount and set
        int price = Resource.getPrice(r.getType(), islandAmount);
        tvPrice.setText(price+ "$");


        buttonPlus.setEnabled(islandAmount > 0);

        buttonPlus.setTag(R.id.resourceType,r);
        buttonPlus.setTag(R.id.resourceBoatAmount, boatAmount);
        buttonPlus.setTag(R.id.resourceIslandAmount, islandAmount);
        buttonPlus.setTag(R.id.resourcePrice,price);

        buttonMinus.setEnabled(boatAmount>0);

        buttonMinus.setTag(R.id.resourceType,r);
        buttonMinus.setTag(R.id.resourceBoatAmount, boatAmount);
        buttonMinus.setTag(R.id.resourceIslandAmount, islandAmount);
        buttonMinus.setTag(R.id.resourcePrice,price);

        buttonPlus.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);

        return row;
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        SQLiteDatabase db = Globals.dbHelper.getWritableDatabase();

        Resource r = (Resource) b.getTag(R.id.resourceType);
        int price = (Integer) b.getTag(R.id.resourcePrice);
        int boatAmount = (Integer) b.getTag(R.id.resourceBoatAmount);
        int islandAmount = (Integer) b.getTag(R.id.resourceIslandAmount);

        String selection = DbHelper.C_FILENAME + " = ? and "+DbHelper.C_TYPE+" = ? and "+DbHelper.C_CONTAINER+" = ?";
        String [] selectArgsBoat = new String[]{Globals.saveName,r.getType(),Globals.boat.getName()};
        String [] selectArgsIsland = new String [] {Globals.saveName,r.getType(),Globals.boat.getCurrentIsland().getName()};

        ContentValues boatValues = new ContentValues();
        ContentValues islandValues = new ContentValues();
        Log.d(TAG,"button pressed type : "+r.getType());

        boatValues.put(DbHelper.C_FILENAME,Globals.saveName);
        boatValues.put(DbHelper.C_CONTAINER,Globals.boat.getName());
        boatValues.put(DbHelper.C_TYPE,r.getType());
        islandValues.put(DbHelper.C_FILENAME,Globals.saveName);
        islandValues.put(DbHelper.C_CONTAINER,Globals.boat.getCurrentIsland().getName());
        islandValues.put(DbHelper.C_TYPE,r.getType());

        switch(b.getId()){
            case R.id.resourceBAddToBoat:
                Log.d(TAG,"plus");
                Toast toast;
                if((Globals.boat.getTotalWeight()+r.getWeight())>Globals.boat.getMaxWeight()){
                    toast = Toast.makeText(context,"You're too heavy!" ,Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                if((Globals.boat.getTotalVolume()+r.getVolume())>Globals.boat.getMaxVolume()){
                    toast = Toast.makeText(context,"You don't have enough space!" ,Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                if((r.getAmount()*price)>Globals.boat.getMoney()){
                    toast = Toast.makeText(context,"You're out of money!" ,Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }

                boatValues.put(DbHelper.C_AMOUNT, boatAmount + r.getAmount());
                islandValues.put(DbHelper.C_AMOUNT, islandAmount - r.getAmount());
                long boatResponse = db.insertWithOnConflict(DbHelper.T_RESOURCES,null,boatValues,SQLiteDatabase.CONFLICT_REPLACE);
                long islandResponse = db.insertWithOnConflict(DbHelper.T_RESOURCES,null,islandValues,SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG,"boatrep "+ boatResponse);
                Log.d(TAG,"islerep "+ islandResponse);

                if((boatResponse>=0)&&(islandResponse>=0)){
                    Globals.boat.addMoney(-price);
                    Globals.boat.addWeight(r.getWeight());
                    Globals.boat.addVolume(r.getVolume());
                    Globals.boat.addFood(r.getFoodValue());
                }
                break;

            case R.id.resourceBAddToIsland:
                Log.d(TAG,"minus");
                boatValues.put(DbHelper.C_AMOUNT, boatAmount - r.getAmount());
                islandValues.put(DbHelper.C_AMOUNT, islandAmount + r.getAmount());

                if((db.insertOrThrow(DbHelper.T_RESOURCES,null,boatValues)>=0)&&(db.insertOrThrow(DbHelper.T_RESOURCES,null,islandValues)>=0)){
                    Globals.boat.addMoney(price);
                    Globals.boat.addWeight(-r.getWeight());
                    Globals.boat.addVolume(-r.getVolume());
                    Globals.boat.addFood(-r.getFoodValue());
                };
                break;
        }
        selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
        String [] selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.boat.getCurrentIsland().getName()};
        String order = DbHelper.C_TYPE+", "+DbHelper.C_CONTAINER+" "+ (Globals.boat.getName().compareTo(Globals.boat.getCurrentIsland().getName())<0?"ASC":"DESC");
        changeCursor(db.query(DbHelper.T_RESOURCES,null,selection,selectArgs,null,null,order));
        //db.close();
        Log.d(TAG, "new boat stats: " + Globals.boat.getTotalWeight() + " " + Globals.boat.getTotalVolume() + " " + Globals.boat.getMoney());
        ((IslandActivity)context).setBoatStats();
    }
}
