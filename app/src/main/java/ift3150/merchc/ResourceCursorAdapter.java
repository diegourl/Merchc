package ift3150.merchc;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Diego on 2015-04-21.
 */
public class ResourceCursorAdapter extends SimpleCursorAdapter {
    static final String TAG = "ResourceCursorAdapter";
    LayoutInflater inflater;
    public ResourceCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
//refactor with from and to
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row == null){
            row = inflater.inflate(R.layout.row_resource,parent,false);
        }

        TextView tvType = (TextView) row.findViewById(R.id.resourceType);
        TextView tvWeight = (TextView) row.findViewById(R.id.resourceWeight);
        TextView tvVolume = (TextView) row.findViewById(R.id.resourceVolume);
        TextView tvBoatAmount = (TextView) row.findViewById(R.id.resourceBoatAmount);
        TextView tvIslandAmount = (TextView) row.findViewById(R.id.resourceIslandAmount);
        TextView tvPrice = (TextView) row.findViewById(R.id.resourcePrice);

        Cursor cursor = getCursor();

        String previousType;
        cursor.moveToFirst();

        int cursorPosition = 0;
        int columnIndex = cursor.getColumnIndex(DbHelper.C_TYPE);

        while(position-->0){ //cursorPosition keeps track of what distance the jumps should cover, position, of how many jumps you should take
            cursor.moveToPosition(cursorPosition);
            previousType = cursor.getString(columnIndex);
            cursor.moveToNext();
            if(previousType.equals(cursor.getString(columnIndex))) //change for while for more containers. Rest as is.
                cursorPosition++;
            cursorPosition++;
        }
        //set resource type
        cursor.moveToPosition(cursorPosition);
        previousType = cursor.getString(columnIndex);
        Log.d(TAG, previousType);
        Resource r = new Resource(previousType);
        tvType.setText(r.getType());


        //fetch and set resource weight
        tvWeight.setText(r.getWeight()+" kg");

        //set resource volume
        tvVolume.setText(r.getVolume()+" m3");

        //set resource amount on boat
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

        //determine and set resource amount on island


        tvIslandAmount.setText(islandAmount+"");

        //fetch price for type and amount and set
        tvPrice.setText(Resource.getPrice(r.getType(),islandAmount)+ "$");

        return row;
    }
}
