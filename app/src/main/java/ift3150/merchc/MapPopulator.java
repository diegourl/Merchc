package ift3150.merchc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yashMaster on 11/04/2015.
 *
 * note this was adapted from ImageAdapter on the Android Studio tutorial
 * kinda a custom solution for a 2way tiled scroll with layers...Gridview
 * doesn't work because you can get a layer "under" the vertical scroll
 *
 */
public class MapPopulator {
    // private ArrayList<Island> islandMap= (ArrayList) Globals.map.values();
    private static final String TAG = "Map Populator";
    private Context mContext;
    private static final int DEFAULT_SIZE=450;
    private int rows=0;
    private int cols=0;
    private int x_zero=0;
    private int y_zero=0;
    private ArrayList<ArrayList<Integer>> mapImages = new ArrayList<ArrayList<Integer>>();
    //the size that can be unlocked at once...also default begining size
    private static final int TILE_SIZE=6;//gives a size of 5

    public MapPopulator(Context c){
        mContext=c;
    }

    public void populate(RelativeLayout l){
        //int top=0;
        //int left=0;
        rows+= TILE_SIZE;
        cols+= TILE_SIZE;
        addWater(0,0,TILE_SIZE,TILE_SIZE);
        addIslands(0,0,TILE_SIZE,TILE_SIZE);

        for(int i=0; i<mapImages.size(); i++) {
            for(int j=0; j<mapImages.get(i).size(); j++) {
                ImageView imageView = new ImageView(mContext);
                if(mapImages.get(i).get(j)!=R.drawable.light_blue){
                    for(Map.Entry<String,Island> isle: Globals.map.entrySet()){
                        //@TODO adjust here as the zeros move
                        if(isle.getValue().xCoord==j &&isle.getValue().yCoord==i) {
                            imageView.setTag(isle.getValue());
                        }
                    }
                }
                imageView.setImageResource(mapImages.get(i).get(j));
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DEFAULT_SIZE, DEFAULT_SIZE);
                layoutParams.topMargin = (DEFAULT_SIZE *(mapImages.size()-1- i)) -DEFAULT_SIZE;
                layoutParams.leftMargin = (DEFAULT_SIZE * j) -DEFAULT_SIZE;
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getTag() instanceof  Island) {
                            Bundle bundle = new Bundle();
                            bundle.putCharSequence("name",((Island) v.getTag()).name);
                            DialogFragment dialog= new IslandDialog();
                            dialog.setArguments(bundle);

                            dialog.show(((Activity)mContext).getFragmentManager(), "");
                           /* Toast.makeText(mContext,
                                    "location " + ((Island) v.getTag()).name,
                                    Toast.LENGTH_LONG).show();
                                    */
                        }
                    }
                });
                //imageView.setPadding(50, 50, 50, 50);
                l.addView(imageView);
            }
        }



        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) l.getLayoutParams();
        params.height = (rows-1)*DEFAULT_SIZE;
        params.width= (cols-1)*DEFAULT_SIZE;
        l.setLayoutParams(params);

    }

    public void annotate(FrameLayout f){
        addTrajectories(0,0,TILE_SIZE, TILE_SIZE, f);
    }
    private void insertTrajectory(int tox,int toy, int fromx, int fromy, FrameLayout f){
        //possibly things with the margins will go to shit if we get into negative values
        //will need to implement an internal positioning system


        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.clear_arrow);
        imageView.setBackgroundColor(Color.TRANSPARENT);//TRASPARENT
        int image_height=(int)(getDistance(tox, toy, fromx, fromy)*DEFAULT_SIZE)-DEFAULT_SIZE;
        //int image_width= Math.abs(tox-fromx)*DEFAULT_SIZE;
        int image_width= 200;
        int topMargin=(DEFAULT_SIZE*(toy+fromy-1)-image_height)/2;
        int leftMargin=(DEFAULT_SIZE*(tox+fromx-1)-image_width)/2;
        Log.d(TAG, "Trajectory (img height) : " +((int)(getDistance(tox, toy, fromx, fromy)*DEFAULT_SIZE)-DEFAULT_SIZE));
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Math.abs(fromx-tox)*DEFAULT_SIZE, Math.abs(fromy-toy)*DEFAULT_SIZE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(image_width, image_height);
        //layoutParams.topMargin = DEFAULT_SIZE * Math.min(fromy, toy);
        //layoutParams.leftMargin = DEFAULT_SIZE * Math.min(fromx, tox);
        layoutParams.topMargin = topMargin;
        layoutParams.leftMargin = leftMargin;
        Log.d(TAG, "Trajectory (topMargin, leftMargin) : " +layoutParams.topMargin+" "+layoutParams.leftMargin);
        imageView.setLayoutParams(layoutParams);
        //Matrix matrix = new Matrix();
        //matrix.postRotate((float) 90, DEFAULT_SIZE / 2, DEFAULT_SIZE / 2);
        imageView.setRotation((float)getDegrees(fromx,fromy,tox,toy));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);//FIT_XY
        f.addView(imageView);

        Log.d(TAG, "Trajectory (height) : " +imageView.getLayoutParams().height);
    }

    private double getDistance(int p1x,int p1y, int p2x, int p2y){
        double t= Math.pow((p1x-p2x),2);
        double s=Math.pow((p1y-p2y),2);
        return Math.sqrt(s+t);
    }
    //http://stackoverflow.com/questions/26076656/calculating-angle-between-two-points-java
    private double getDegrees(int fixedX,int fixedY, int point2X, int point2Y){
        int point1X=fixedX;
        int point1Y=fixedY+4;
        double angle1 = Math.atan2(point1Y - fixedY, point1X - fixedX);
        double angle2 = Math.atan2(point2Y - fixedY, point2X - fixedX);

        double rads= angle1 - angle2;
        return Math.toDegrees(rads);
    }

    private void addWater(int xmin, int ymin, int xmax, int ymax){
        //probably only works for initi water
        //y
        for( int h=ymin; h<ymax; h++) {
            ArrayList row=new ArrayList<Integer>();
            //x
            for (int i = xmin; i < xmax; i++) {
                row.add(R.drawable.light_blue);
            }
            mapImages.add(row);
        }
    }
    //For the record I feel like to coordinates should be INTEGERS
    private void addIslands(int xmin, int ymin, int xmax, int ymax){
        for(Map.Entry<String,Island> isle: Globals.map.entrySet()){
            //@TODO adjust here as the zeros move
            if(isle.getValue().xCoord<xmax &&isle.getValue().yCoord<ymax) {
                mapImages.get((int) isle.getValue().yCoord).set((int) isle.getValue().xCoord, R.drawable.green);
            }
        }
    }
    //For the record I feel like to coordinates should be INTEGERS
    private void addTrajectories(int xmin, int ymin, int xmax, int ymax, FrameLayout f){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME + " = ? ";
        String [] selectionArgs = {Globals.saveName};
        Cursor trajectoriesCursor = db.query(DbHelper.T_TRAJECTORIES,null,selection,selectionArgs,null,null,null);

        int columnIndex;
        if(!trajectoriesCursor.moveToFirst()) {Log.d(TAG,"nomovetofirst");return;}
        do{
        //FROM
            columnIndex = trajectoriesCursor.getColumnIndex(DbHelper.C_FROM);
            selection = DbHelper.C_FILENAME + " = ? and "+DbHelper.C_NAME+ " = ? ";
            selectionArgs = new String[]{Globals.saveName,trajectoriesCursor.getString(columnIndex)};
            Cursor islandCursor = db.query(DbHelper.T_ISLANDS, null, selection,selectionArgs, null, null, null);
            islandCursor.moveToFirst();
            columnIndex = islandCursor.getColumnIndex(DbHelper.C_X);
            int fromX = (int) islandCursor.getFloat(columnIndex);
            columnIndex = islandCursor.getColumnIndex(DbHelper.C_Y);
            int fromY = (int) islandCursor.getFloat(columnIndex);

            //TO
            columnIndex = trajectoriesCursor.getColumnIndex(DbHelper.C_TO);
            selection = DbHelper.C_FILENAME + " = ? and "+DbHelper.C_NAME+ " = ? ";
            selectionArgs = new String[]{Globals.saveName,trajectoriesCursor.getString(columnIndex)};
            islandCursor = db.query(DbHelper.T_ISLANDS, null, selection,selectionArgs, null, null, null);
            islandCursor.moveToFirst();
            columnIndex = islandCursor.getColumnIndex(DbHelper.C_X);
            int toX = (int) islandCursor.getFloat(columnIndex);
            columnIndex = islandCursor.getColumnIndex(DbHelper.C_Y);
            int toY = (int) islandCursor.getFloat(columnIndex);

            insertTrajectory(toX, toY, fromX, fromY, f);

        }while(trajectoriesCursor.moveToNext());



    }

    //soon depreciated
    private Integer[] mThumbIds={
            R.drawable.light_blue, R.drawable.green, R.drawable.green, R.drawable.green,
            R.drawable.green,R.drawable.green,R.drawable.green,R.drawable.light_blue,
            R.drawable.light_blue, R.drawable.green, R.drawable.green, R.drawable.green,
            R.drawable.green,R.drawable.green,R.drawable.green,R.drawable.light_blue
    };

    public void addNorth(RelativeLayout l){
        //@TODO
    }

    public void addSouth(RelativeLayout l){
        //@TODO
    }

    public void addEast(){
        //@TODO
    }

    public void addWest(){
        //@TODO
    }

}