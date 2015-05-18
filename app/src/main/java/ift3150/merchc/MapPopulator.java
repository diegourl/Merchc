package ift3150.merchc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private FrameLayout f;
    private TextView food;
    private TextView money;
    public final int DEFAULT_SIZE;
    public final int BOAT_SIZE=100;
    private int rows=0;
    private int cols=0;
    private int x_zero=0;
    private int y_zero=0;
    private ArrayList<ArrayList<Integer>> mapImages = new ArrayList<ArrayList<Integer>>();
    //the size that can be unlocked at once...also default begining size
    public static final int TILE_SIZE=5;//gives a size of 5

    public MapPopulator(Context c){
        mContext=c;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        DEFAULT_SIZE = size.x/TILE_SIZE;
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
                    for(Map.Entry<String,Island> isle: Globals.archipelago.entrySet()){
                        //@TODO adjust here as the zeros move
                        if(isle.getValue().getxCoord()-1==j &&isle.getValue().getyCoord()-1==i) {
                            imageView.setTag(isle.getValue());

                        }
                    }
                }
                imageView.setImageResource(mapImages.get(i).get(j));
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DEFAULT_SIZE, DEFAULT_SIZE);
                //complement so y vales go up - DEFAULT_SIZE is because you are starting on the "other side" of the box
                layoutParams.topMargin = (DEFAULT_SIZE *(mapImages.size()- i)) -DEFAULT_SIZE;
                layoutParams.leftMargin = (DEFAULT_SIZE * j) ;
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getTag() instanceof  Island) {
                            Bundle bundle = new Bundle();
                            bundle.putCharSequence("name",((Island) v.getTag()).name);
                            bundle.putCharSequence("industry",((Island) v.getTag()).getIndustry());
                            DialogFragment dialog= new IslandDialog();
                            dialog.setArguments(bundle);
                            dialog.show(((Activity)mContext).getFragmentManager(), "");
                        }
                    }
                });
                l.addView(imageView);
            }
        }



        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) l.getLayoutParams();
        params.height = (rows)*DEFAULT_SIZE;
        params.width= (cols)*DEFAULT_SIZE;
        l.setLayoutParams(params);

    }

    public void annotate(FrameLayout f){
        this.f=f;//should be depreciated soon

        addTrajectories(0,0,TILE_SIZE, TILE_SIZE, f);
        insertBoat(f);
    }
    public void resetTrajectories(){
        addTrajectories(0,0,TILE_SIZE, TILE_SIZE, f);
    }
    public static void moveBoat(){

    }
    public void updateDollars(TextView tv1){
        tv1.setText(mContext.getText(R.string.map_dollars)+" "+Float.toString(Globals.boat.getMoney()));
        this.money=tv1;
    }
    public void updateFood(TextView tv2){
        tv2.setText(mContext.getText(R.string.map_food)+" "+Float.toString(Globals.getBoatFood()));
        this.food=tv2;
    }
    // for trajectories starting at the current island
    private void insertPath(int fromx,int fromy, int tox, int toy, FrameLayout f, int days){
        //possibly things with the margins will go to shit if we get into negative values
        //will need to implement an internal positioning system

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.clear_arrow);
        imageView.setBackgroundColor(Color.TRANSPARENT);//TRASPARENT
        int image_height=(int)(getDistance(tox, toy, fromx, fromy)*DEFAULT_SIZE)-DEFAULT_SIZE;
        int image_width= 200;
        int topMargin=(DEFAULT_SIZE*((TILE_SIZE-toy)+(TILE_SIZE-fromy)+1)-image_height)/2;
        int leftMargin=(DEFAULT_SIZE*(tox+fromx-1)-image_width)/2;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(image_width, image_height);
        layoutParams.topMargin = topMargin;
        layoutParams.leftMargin = leftMargin;
        //Log.d(TAG, "Trajectory (topMargin, leftMargin) : " +layoutParams.topMargin+" "+layoutParams.leftMargin);
        //Log.d(TAG, "Trajectory angle : " +(float)getDegrees(fromx,fromy,tox,toy)+" ");
        //Log.d(TAG, "Trajectory between : (" +fromx+","+fromy+"),("+tox+","+toy+")");
        imageView.setLayoutParams(layoutParams);
        imageView.setRotation((float)getDegrees(fromx,fromy,tox,toy));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);//FIT_XY
        String s= Integer.toString(days) ;//change to array later
        imageView.setTag(R.id.id_days,s);
        imageView.setTag(R.id.id_fromx,fromx);
        imageView.setTag(R.id.id_fromy, fromy);
        imageView.setTag(R.id.id_tox, tox);
        imageView.setTag(R.id.id_toy,toy);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putCharSequence("days", (String) v.getTag(R.id.id_days));
                //bundle.putFloat("dailyDollars", (float)v.getTag(R.id_daily))
                bundle.putInt("fromx", (int) v.getTag(R.id.id_fromx));// was putCharSequence and cast to string
                bundle.putInt("fromy", (int) v.getTag(R.id.id_fromy));
                bundle.putInt("tox", (int) v.getTag(R.id.id_tox));
                bundle.putInt("toy", (int) v.getTag(R.id.id_toy));
                Log.d(TAG, "Trajectory between : (" +(int) v.getTag(R.id.id_fromx)+","+(int) v.getTag(R.id.id_fromy)+"),("
                        +(int) v.getTag(R.id.id_tox)+","+(int) v.getTag(R.id.id_toy)+")");
                DialogFragment dialog = new TrajectoryDialog();
                dialog.setArguments(bundle);
                dialog.show(((Activity) mContext).getFragmentManager(), "");

            }
        });

        f.addView(imageView);
    }

    private void insertTrajectory(int fromx,int fromy, int tox, int toy, FrameLayout f, int days){
        //possibly things with the margins will go to shit if we get into negative values
        //will need to implement an internal positioning system

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.dashed_line);
        imageView.setBackgroundColor(Color.TRANSPARENT);//TRASPARENT

        int image_width=(int)(getDistance(tox, toy, fromx, fromy)*DEFAULT_SIZE)-DEFAULT_SIZE;
        int image_height= 100;
        int topMargin=(DEFAULT_SIZE*((TILE_SIZE-toy)+(TILE_SIZE-fromy)+1)-image_height)/2;
        int leftMargin=(DEFAULT_SIZE*(tox+fromx-1)-image_width)/2;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(image_width, image_height);
        layoutParams.topMargin = topMargin;
        layoutParams.leftMargin = leftMargin;
        imageView.setLayoutParams(layoutParams);
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//needed for dashed line to work properly
        imageView.setRotation((float)getDegrees(fromx,fromy,tox,toy)+90);

        /*int image_width=(int)(getDistance(tox, toy, fromx, fromy)*DEFAULT_SIZE)-DEFAULT_SIZE;
        int image_height= 200;
        int leftMargin=(DEFAULT_SIZE*((TILE_SIZE-toy)+(TILE_SIZE-fromy)+1)-image_height)/2;
        int topMargin=(DEFAULT_SIZE*(tox+fromx-1)-image_width)/2;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(image_width, image_height);
        layoutParams.topMargin = topMargin;
        layoutParams.leftMargin = leftMargin;
        imageView.setLayoutParams(layoutParams);
        imageView.setRotation((float)getDegrees(fromx,fromy,tox,toy)+90);*/


        imageView.setScaleType(ImageView.ScaleType.FIT_XY);//FIT_XY
        String s= Integer.toString(days) ;//change to array later
        imageView.setTag(R.id.id_days,s);
        imageView.setTag(R.id.id_fromx,fromx);
        imageView.setTag(R.id.id_fromy, fromy);
        imageView.setTag(R.id.id_tox, tox);
        imageView.setTag(R.id.id_toy,toy);

        f.addView(imageView);
    }
    //Technically this is now just place boat...its already inserted
    private void insertBoat(FrameLayout f){
        //possibly things with the margins will go to shit if we get into negative values
        //will need to implement an internal positioning system

        //We need to access the db the first time because the current island doesn't
        //appear to be set correctly? I dunno problems when I go straight to Globals
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME + " = ? ";
        String [] selectionArgs = {Globals.saveName};
        Cursor trajectoriesCursor = db.query(DbHelper.T_BOAT,null,selection,selectionArgs,null,null,null);
        trajectoriesCursor.moveToFirst(); //maybe?
        int columnIndex=trajectoriesCursor.getColumnIndex(DbHelper.C_CURRENTISLAND);
        String currentIsle= trajectoriesCursor.getString(columnIndex);
        String whereClause = DbHelper.C_NAME+ "=?";
        String[] whereArgs = new String[] {
                currentIsle
        };
        Log.d(TAG, "Boat placed at (island name)  : " + currentIsle);
        Cursor islandCursor = db.query(DbHelper.T_ISLANDS,null,whereClause,whereArgs,null,null,null);
        //SHOULD MAYBE CHECK THE QUERRY DOES RETURN NULL
        islandCursor.moveToFirst();
        columnIndex= islandCursor.getColumnIndex(DbHelper.C_X);
        int xVal= islandCursor.getInt(columnIndex);
        columnIndex= islandCursor.getColumnIndex(DbHelper.C_Y);
        int yVal= islandCursor.getInt(columnIndex);

        //you would think the bellow code woudl replace the above but I'm not sure the stuff was set
        //right


        ImageView imageView= (ImageView) f.findViewById(R.id.boat);
        imageView.setBackgroundColor(Color.TRANSPARENT);

        Log.d(TAG, "Boat placed at  : " + xVal+ " "+ yVal);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(BOAT_SIZE, BOAT_SIZE);
        layoutParams.topMargin =(int) ((DEFAULT_SIZE *(mapImages.size()- yVal)) - .5*DEFAULT_SIZE);
        layoutParams.leftMargin = (int) (xVal*DEFAULT_SIZE - .5*DEFAULT_SIZE);
        ((MapActivity)mContext).verticalScrollTo(layoutParams.topMargin);

        imageView.setLayoutParams(layoutParams);


    }

    public void resetBoat(){
        //not using the DB anymore just the GLOBAL

        int xVal= (int) Globals.boat.getCurrentIsland().getxCoord();
        int yVal= (int) Globals.boat.getCurrentIsland().getyCoord();

        ImageView imageView= (ImageView) f.findViewById(R.id.boat);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.bringToFront();


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(BOAT_SIZE, BOAT_SIZE);
        layoutParams.topMargin =(int) ((DEFAULT_SIZE *(mapImages.size()- yVal)) - .5*DEFAULT_SIZE);
        layoutParams.leftMargin = (int) (xVal*DEFAULT_SIZE - .5*DEFAULT_SIZE);
        Log.d(TAG, "2Boat margins at  : " + layoutParams.leftMargin+ " "+ layoutParams.leftMargin);
        imageView.setLayoutParams(layoutParams);
        // f.addView(imageView);


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
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME + " = ? ";
        String [] selectionArgs = {Globals.saveName};
        Cursor islandCursor = db.query(DbHelper.T_ISLANDS,null,selection,selectionArgs,null,null,null);
        while(islandCursor.moveToNext()) {
            int colIndex = islandCursor.getColumnIndex(DbHelper.C_X);
            int xCoord = islandCursor.getInt(colIndex);
            colIndex = islandCursor.getColumnIndex(DbHelper.C_Y);
            int yCoord = islandCursor.getInt(colIndex);
            if (xCoord <= xmax && yCoord <= ymax) {
                mapImages.get(yCoord - 1).set(xCoord - 1, R.drawable.green);
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
            columnIndex = trajectoriesCursor.getColumnIndex(DbHelper.C_DAYS);
            int days= trajectoriesCursor.getInt(columnIndex);
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
            if((toX==(int)Globals.boat.getCurrentIsland().getxCoord()&&toY==(int)Globals.boat.getCurrentIsland().getyCoord())) {
                Log.d(TAG,"!!SWITCH" +Integer.toString(toX)+" " + Integer.toString( toY)+" "+Integer.toString(fromX)+" "+Integer.toString( fromY));
                insertPath(toX, toY, fromX, fromY, f, days);
            }else if((fromX==(int)Globals.boat.getCurrentIsland().getxCoord()&&fromY==(int)Globals.boat.getCurrentIsland().getyCoord())){
                Log.d(TAG,"!!"+Integer.toString(fromX)+" " + Integer.toString( fromY)+" "+Integer.toString(toX)+" "+Integer.toString( toY));
                insertPath(fromX, fromY, toX, toY, f, days);
            }
            else insertTrajectory(fromX, fromY, toX, toY, f, days);
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