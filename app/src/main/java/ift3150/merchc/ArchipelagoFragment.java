package ift3150.merchc;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by Diego on 2015-05-11.
 */
public class ArchipelagoFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ArchipelagoFragment";
    private int TILE_SIZE;
    private int TILES;
    private Eventler eventler;
    RelativeLayout relativeLayout;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        TILE_SIZE = ((BMapActivity)getActivity()).getTILE_SIZE();
        TILES = ((BMapActivity)getActivity()).getTILES();
        relativeLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_archipelago,container,false);
        eventler = new Eventler();

        //do all the islands and stuff
        relativeLayout = addIslands(relativeLayout);
        relativeLayout = addTrajectories(relativeLayout);



        /*ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.green);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(TILE_SIZE,TILE_SIZE);
        layoutParams.topMargin = Globals.boat.getCurrentIsland().yCoord*TILE_SIZE;
        layoutParams.leftMargin = Globals.boat.getCurrentIsland().xCoord*TILE_SIZE;
        iv.setLayoutParams(layoutParams);
        relativeLayout.addView(iv);*/
        return relativeLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*TextView textView = new TextView(getActivity());
        textView.setTextColor(0xffffffff);
        textView.setText("ballingdoe");

        LinearLayout itemInfo = (LinearLayout)getView().findViewById(R.id.archipelagoItemInfo);

        LinearLayout shortDescript = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.archipelago_island_short,null);

        itemInfo.addView(shortDescript,itemInfo.getChildCount()-1);*/
    }




    private RelativeLayout addIslands(RelativeLayout relativeLayout){
        for(Island i: Globals.archipelago.values()){
            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.green);///@TODO add correct island image
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setTag(i);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(TILE_SIZE,TILE_SIZE);
            layoutParams.topMargin = i.getyCoord()*TILE_SIZE;
            layoutParams.leftMargin = i.getxCoord()*TILE_SIZE;
            iv.setLayoutParams(layoutParams);
            iv.setId(R.id.island);
            Log.d(TAG, "topmargin: " + ((RelativeLayout.LayoutParams) (iv.getLayoutParams())).topMargin + " leftmargin: " + ((RelativeLayout.LayoutParams) (iv.getLayoutParams())).leftMargin);
            iv.setOnClickListener(this);
            relativeLayout.addView(iv);
        }
        return relativeLayout;
    }

    private RelativeLayout addTrajectories(RelativeLayout relativeLayout){
        Object [] archipelago = Globals.archipelago.values().toArray();
        for(int i = 0;i<archipelago.length;i++){
            for(int j = i+1 ;j<archipelago.length;j++){
                Trajectory trajectory;
                if(((Island)archipelago[j]).getName().equals(Globals.boat.getCurrentIsland().getName()))
                    trajectory = eventler.getTrajectory((Island)archipelago[j],(Island)archipelago[i]);//make sure current island is the from
                else
                    trajectory = eventler.getTrajectory((Island)archipelago[i],(Island)archipelago[j]);

                if(!trajectory.isFeasible())
                    return relativeLayout;

                int fromx = trajectory.getFrom().getxCoord();
                int tox = trajectory.getTo().getxCoord();
                int fromy = trajectory.getFrom().getyCoord();
                int toy = trajectory.getTo().getyCoord();

                ImageView iv = new ImageView(getActivity());
                iv.setId(R.id.trajectory);

                iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                int deltax = (Math.abs(tox-fromx));
                int deltay = (Math.abs(toy-fromy));
                Log.d(TAG,"delta: ("+deltax+","+deltay+")");

                int image_width=(int)(TILE_SIZE*(getDistance(0,0,deltax,deltay)-Math.sqrt(2)/1.7));
                int image_height= 15;
                int topMargin=(TILE_SIZE*(toy+fromy+1)-image_height)/2;
                int leftMargin=(TILE_SIZE*(tox+fromx+1)-image_width)/2;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(image_width, image_height);
                Log.d(TAG,"topmargin: "+topMargin+" leftmargin: "+leftMargin);
                layoutParams.topMargin = topMargin;
                layoutParams.leftMargin = leftMargin;

                iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//needed for dashed line to work properly
                iv.setRotation((float) Math.toDegrees(Math.atan2(deltay, deltax)));

                iv.setLayoutParams(layoutParams);

                if(Globals.boat.getCurrentIsland().getName().equals(trajectory.getFrom().getName())){
                    iv.setTag(trajectory);
                    iv.setImageResource(R.drawable.dashed_line);
                    iv.setFocusable(false);
                    Log.d(TAG, "focusable: " + iv.isFocusable());
                    iv.setOnClickListener(this);
                }else{
                    iv.setImageResource(R.drawable.dashed_line);
                }
                relativeLayout.addView(iv);

            }
        }
        return relativeLayout;
    }





    private double getDistance(int p1x,int p1y, int p2x, int p2y){
        double t= Math.pow((p1x - p2x), 2);
        double s=Math.pow((p1y-p2y),2);
        return Math.sqrt(s+t);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"click.");
        //ImageView iv = (ImageView) v;
        LinearLayout itemInfo = (LinearLayout)getView().findViewById(R.id.archipelagoItemInfo);
        itemInfo.removeAllViews();

        switch (v.getId()){
            case R.id.island :

                Island i = (Island) v.getTag();
                //@TODO move to separate function

                Log.d(TAG,"island clicked: "+i.getName());

                //itemInfo.animate().translationY(itemInfo.getHeight()).setStartDelay(2000);
                //Log.d(TAG,"wait: "+wait);
                LinearLayout shortDescript = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.archipelago_island_short,null);

                TextView islandName = (TextView) shortDescript.findViewById(R.id.islandName);
                islandName.setText(i.getName());

                TextView islandDesc = (TextView) shortDescript.findViewById(R.id.islandDescript);
                islandDesc.setText(" is a " + i.getIndustry() + " island.");
                itemInfo.addView(shortDescript,itemInfo.getChildCount());

                if(i.getName().equals(Globals.boat.getCurrentIsland().getName())){
                    Log.d(TAG,"current island.");
                    TextView youAreHere = (TextView) new TextView(getActivity());
                    youAreHere.setText("You are here.");
                    youAreHere.setTextColor(0xffffffff);
                    itemInfo.addView(youAreHere,itemInfo.getChildCount());
                }else{
                    Log.d(TAG,"not current island.");
                    SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
                    String selection = DbHelper.C_FILENAME+" = ? and "+DbHelper.C_DESTINATION+" = ? and "+DbHelper.C_CONTAINER+" = ?";
                    String [] selectArgs = new String[]{Globals.saveName,i.getName(),Globals.boat.getName()};
                    String [] columns = new String []{DbHelper.C_TYPE,DbHelper.C_NAME,DbHelper.C_DAYSLEFT};
                    Cursor c = db.query(DbHelper.T_PASSENGERS,columns,selection,selectArgs,null,null,DbHelper.C_DAYSLEFT+" ASC");
                    //if(!c.moveToFirst()) {Log.d(TAG,"cursor empty");break;}
                    Log.d(TAG,"passcount: "+c.getCount());
                    for(int j = 0; (j<c.getCount())&&(j<3);j++){
                        c.moveToPosition(j);
                        TextView passengerHeaded = new TextView(getActivity());
                        int columnIndex = c.getColumnIndex(DbHelper.C_TYPE);
                        String type = c.getString(columnIndex);
                        columnIndex = c.getColumnIndex(DbHelper.C_NAME);
                        String name = c.getString(columnIndex);
                        columnIndex = c.getColumnIndex(DbHelper.C_DAYSLEFT);
                        int daysLeft = c.getInt(columnIndex);
                        passengerHeaded.setText(type+" "+name+" must get there "+(daysLeft>0?"in "+daysLeft+" days.":"immediately."));
                        passengerHeaded.setTextColor(0xffffffff);
                        itemInfo.addView(passengerHeaded,itemInfo.getChildCount());
                    }
                    if(c.getCount()>4){
                        TextView manyPassengers = new TextView(getActivity());
                        manyPassengers.setText(c.getCount()+" more passengers are headed there.");
                        manyPassengers.setTextColor(0xffffffff);
                        itemInfo.addView(manyPassengers,itemInfo.getChildCount());
                    }
                    c.close();
                    db.close();
                }
                itemInfo.setVisibility(View.VISIBLE);


                //itemInfo.animate().translationY(itemInfo.getHeight()).setStartDelay(2000);
                //itemInfo.animate().translationY(-300);
                //itemInfo.animate().translationY(0);
                break;

            case R.id.trajectory :
                Trajectory trajectory = (Trajectory)v.getTag();
                Log.d(TAG,"trajectory clicked");
                itemInfo = setTrajectoryInfo(itemInfo,trajectory);
                itemInfo.setVisibility(View.VISIBLE);
                break;

            case R.id.sailButton :

                Log.d(TAG,"sail clicked.");
                sail((Trajectory) v.getTag());
                break;

                /*


                int days = trajectory.getDuration();
                int moneyCost = trajectory.getMoneyCost();
                int foodCost = trajectory.getFoodCost();

                if(moneyCost+100>Globals.boat.getMoney()){
                    TextView cannotComplete = new TextView(getActivity());
                    cannotComplete.setText("You cannot complete this \n please\n don't");
                    cannotComplete.setTextColor(0xffffffff);
                    itemInfo.addView(cannotComplete,itemInfo.getChildCount());
                    itemInfo.setVisibility(View.VISIBLE);
                    break;
                }

                TextView duration = new TextView(getActivity());
                duration.setText("This trajectory will take "+days+" day"+((days>1)?"s.":"."));
                duration.setTextColor(0xffffffff);
                itemInfo.addView(duration,itemInfo.getChildCount());
                Log.d(TAG,"duration: "+trajectory.getDuration());

                TextView  cost = new TextView(getActivity());
                cost.setText("It will cost " + trajectory.getMoneyCost() + " $ and " + trajectory.getFoodCost() + " food.");
                cost.setTextColor(0xffffffff);
                itemInfo.addView(cost,itemInfo.getChildCount());
                Log.d(TAG,"cost: "+trajectory.getMoneyCost()+"$ "+trajectory.getFoodCost());

                for(Map.Entry<String, Double> entry: trajectory.getAverageRisks().entrySet()){
                    TextView riskTV = new TextView(getActivity());
                    riskTV.setText("There is a "+entry.getValue()*100+"% chance of "+entry.getKey()+".");
                    riskTV.setTextColor(0xffffffff);
                    itemInfo.addView(riskTV,itemInfo.getChildCount());
                }

                LinearLayout ll = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.sail_button_layout, null);
                Button sailButton = (Button) ll.findViewById(R.id.button);
                sailButton.setOnClickListener(this);
                itemInfo.addView(ll);


                itemInfo.setVisibility(View.VISIBLE);*/

        }
    }

    public void sail(Trajectory t) {
        int day = 0;
        LinearLayout itemInfo = (LinearLayout)getView().findViewById(R.id.archipelagoItemInfo);
        itemInfo.removeAllViews();

        while(day++<t.getDuration()) {
            Globals.boat.tick();
            Happening happened = eventler.tryEvents(t);
            if(!happened.getType().equals(Event.NOTHING_HAPPENED)) {
                TextView outcome = new TextView(getActivity());
                outcome.setText(happened.getOutcomeMessage());
                outcome.setTextColor(getResources().getColor(R.color.text_colour));
                itemInfo.addView(outcome,itemInfo.getChildCount());
            }
            eventler.tick();
        }

        Globals.boat.setCurrentIsland(t.getTo());
        Globals.boat.getCurrentIsland().deliver();


        relativeLayout.removeAllViews();
        relativeLayout = addIslands(relativeLayout);
        relativeLayout = addTrajectories(relativeLayout);

        TextView arrival = new TextView(getActivity());
        arrival.setText("You have arrived at "+t.getTo().getName());
        arrival.setTextColor(getResources().getColor(R.color.text_colour));
        itemInfo.addView(arrival,itemInfo.getChildCount());

        relativeLayout.addView(itemInfo);

        BMapActivity bMapActivity = (BMapActivity)getActivity();
        bMapActivity.setBoatStats();
        bMapActivity.setIslandName();


    }

    //@TODO handle lack of both
    private LinearLayout setTrajectoryInfo(LinearLayout linearLayout, Trajectory trajectory){
        int days = trajectory.getDuration();
        int moneyCost = trajectory.getMoneyCost();
        int foodCost = trajectory.getFoodCost();

        Log.d(TAG, "duration: "+days+"  cost: "+moneyCost+"$ "+foodCost+" food.");
        if(moneyCost>Globals.boat.getMoney()){
            TextView lackOfMoney = new TextView(getActivity());
            lackOfMoney.setText("You don't have enough money to complete this trajectory.\n" +
                                "It takes "+days+(days>1?"days.":"day.")+"\n"+
                                "It costs "+moneyCost+"$ and "+foodCost+" food.\n"+
                                "You are "+(moneyCost-Globals.boat.getMoney())+"$ short."
            );
            lackOfMoney.setTextColor(getResources().getColor(R.color.text_colour));
            linearLayout.addView(lackOfMoney,linearLayout.getChildCount());
            return linearLayout;
        }

        if(foodCost>Globals.boat.getFood()){
            TextView lackOfFood= new TextView(getActivity());
            lackOfFood.setText("You don't have enough food to complete this trajectory.\n" +
                            "It takes "+days+(days>1?" days.":" day.")+"\n"+
                            "It costs "+moneyCost+"$ and "+foodCost+" food.\n"+
                            "You are "+(foodCost-Globals.boat.getFood())+" food short."
            );
            lackOfFood.setTextColor(getResources().getColor(R.color.text_colour));
            linearLayout.addView(lackOfFood,linearLayout.getChildCount());
            return linearLayout;

        }
        TextView costInfo= new TextView(getActivity());
        costInfo.setText("This trajectory takes " + days + (days > 1 ? " days." : " day.") + "\n" +
                        "It will cost " + moneyCost + "$ and " + foodCost + " food."
        );
        costInfo.setTextColor(getResources().getColor(R.color.text_colour));
        linearLayout.addView(costInfo,linearLayout.getChildCount());

        for(Map.Entry<String, Double> entry: trajectory.getAverageRisks().entrySet()){
            TextView riskTV = new TextView(getActivity());
            riskTV.setText("There is a "+entry.getValue()*100+"% chance of "+entry.getKey()+".");
            riskTV.setTextColor(getResources().getColor(R.color.text_colour));
            linearLayout.addView(riskTV, linearLayout.getChildCount());
        }

        LinearLayout buttonLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.sail_button_layout, null);
        Button sailButton = (Button) buttonLayout.findViewById(R.id.sailButton);
        sailButton.setTag(trajectory);
        sailButton.setOnClickListener(this);
        linearLayout.addView(buttonLayout);

        return linearLayout;

    }













    /////DEPRECATION LINE/////////
    private RelativeLayout addTrajectoriesC(RelativeLayout relativeLayout){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME+" = ?";//archipelago....
        String [] selectArgs = {Globals.saveName};
        Cursor c = db.query(DbHelper.T_TRAJECTORIES,null,selection,selectArgs,null,null,null);
        int columnIndex;
        if(!c.moveToFirst()){db.close();c.close(); return relativeLayout;}
        do{
            columnIndex = c.getColumnIndex(DbHelper.C_FROM);
            Island from = Globals.archipelago.get(c.getString(columnIndex));
            columnIndex = c.getColumnIndex(DbHelper.C_TO);
            Island to = Globals.archipelago.get(c.getString(columnIndex));
            columnIndex = c.getColumnIndex(DbHelper.C_DAYS);
            int days = c.getInt(columnIndex);

            int tox = to.getxCoord();
            int toy = to.getyCoord();
            int fromx = from.getxCoord();
            int fromy = from.getyCoord();

            ImageView iv = new ImageView(getActivity());
            iv.setId(R.id.trajectory);


            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            int deltax = (Math.abs(tox-fromx));
            int deltay = (Math.abs(toy-fromy));
            Log.d(TAG,"delta: ("+deltax+","+deltay+")");
            /*
            double length = Math.sqrt(deltax*deltax+deltay*deltay);
            Log.d(TAG,"length: "+length);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)length, 50);
            double width = layoutParams.height;
            layoutParams.topMargin = (int)((TILE_SIZE*(fromy+toy+1)-width)/2);
            layoutParams.leftMargin = (int)((TILE_SIZE*(fromx+tox+1)-length)/2);*/

            int image_width=(int)(TILE_SIZE*(getDistance(0,0,deltax,deltay)-Math.sqrt(2)/1.7));
            int image_height= 15;
            int topMargin=(TILE_SIZE*(toy+fromy+1)-image_height)/2;
            int leftMargin=(TILE_SIZE*(tox+fromx+1)-image_width)/2;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(image_width, image_height);
            Log.d(TAG,"topmargin: "+topMargin+" leftmargin: "+leftMargin);
            layoutParams.topMargin = topMargin;
            layoutParams.leftMargin = leftMargin;
            //Log.d(TAG,"topmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).topMargin+" leftmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).leftMargin);
            iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//needed for dashed line to work properly
            iv.setRotation((float) Math.toDegrees(Math.atan2(deltay, deltax)));
            //iv.setRotation(45);
            iv.setLayoutParams(layoutParams);

            if(Globals.boat.getCurrentIsland().getName().equals(from.getName())||Globals.boat.getCurrentIsland().getName().equals(to.getName())){
                /*Trajectory trajectory = Eventler.getTrajectory(new Point(fromx,fromy),new Point(tox,toy));
                iv.setTag(trajectory);*/
                iv.setImageResource(R.drawable.dashed_line);
                iv.setFocusable(false);
                Log.d(TAG, "focusable: " + iv.isFocusable());
                iv.setOnClickListener(this);
            }else{
                iv.setImageResource(R.drawable.dashed_line);
            }
            relativeLayout.addView(iv);

        }while(c.moveToNext());
        c.close();
        db.close();
        return relativeLayout;

    }

    private RelativeLayout addTrajectoriesB(RelativeLayout relativeLayout){
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        final String FROM_ISLAND = "islandFrom";
        final String TO_ISLAND = "islandTo";
        final String C_TO_X = "fromx";
        final String C_TO_Y = "fromy";
        final String C_FROM_X = "tox";
        final String C_FROM_Y = "toy";
        String query = "SELECT "
                +TO_ISLAND+"."+DbHelper.C_X+" as "+C_TO_X+", "
                +TO_ISLAND+"."+DbHelper.C_Y+" as "+C_TO_Y+", "
                +FROM_ISLAND+"."+DbHelper.C_X+" as "+C_FROM_X+", "
                +FROM_ISLAND+"."+DbHelper.C_Y+" as "+C_FROM_Y+", "
                +FROM_ISLAND+"."+DbHelper.C_Y+" as "+C_FROM_Y+", "
                +FROM_ISLAND+"."+DbHelper.C_NAME+" as "+DbHelper.C_NAME+", "
                +DbHelper.T_TRAJECTORIES+"."+DbHelper.C_DAYS+" as "+DbHelper.C_DAYS
                +" FROM " + DbHelper.T_TRAJECTORIES
                +" JOIN "+DbHelper.T_ISLANDS+" as "+FROM_ISLAND+" ON "+FROM_ISLAND+"."+DbHelper.C_NAME+" = "+DbHelper.T_TRAJECTORIES+"."+DbHelper.C_FROM
                +" JOIN "+DbHelper.T_ISLANDS+" as "+TO_ISLAND+" ON "+TO_ISLAND+"."+DbHelper.C_NAME+" = "+DbHelper.T_TRAJECTORIES+"."+DbHelper.C_TO
                +" WHERE "+DbHelper.T_TRAJECTORIES+"."+DbHelper.C_FILENAME+" = ?";
        String [] selectArgs = {Globals.saveName};
        Cursor c = db.rawQuery(query,selectArgs);
        if(!c.moveToFirst()){db.close();c.close(); return relativeLayout;}
        int columnIndex;
        do {
            columnIndex = c.getColumnIndex(C_TO_X);
            int tox = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(C_TO_Y);
            int toy = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(C_FROM_X);
            int fromx = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(C_FROM_Y);
            int fromy = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_NAME);
            String fromName = c.getString(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_DAYS);
            int days = c.getInt(columnIndex);
            Log.d(TAG,"tilesize "+TILE_SIZE);
            Log.d(TAG,"("+fromx+","+fromy+") to ("+tox+","+toy+")");

            ImageView iv = new ImageView(getActivity());
            if(fromName.equals(Globals.boat.getCurrentIsland().getName())){
                iv.setImageResource(R.drawable.dashed_line);
                //setonclicklistener
            }else{
                iv.setImageResource(R.drawable.dashed_line);
            }
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            int deltax = (Math.abs(tox-fromx));
            int deltay = (Math.abs(toy-fromy));
            Log.d(TAG,"delta: ("+deltax+","+deltay+")");
            /*
            double length = Math.sqrt(deltax*deltax+deltay*deltay);
            Log.d(TAG,"length: "+length);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)length, 50);
            double width = layoutParams.height;
            layoutParams.topMargin = (int)((TILE_SIZE*(fromy+toy+1)-width)/2);
            layoutParams.leftMargin = (int)((TILE_SIZE*(fromx+tox+1)-length)/2);*/

            int image_width=(int)(TILE_SIZE*(getDistance(0,0,deltax,deltay)-Math.sqrt(2)/1.7));
            int image_height= 15;
            int topMargin=(TILE_SIZE*(toy+fromy+1)-image_height)/2;
            int leftMargin=(TILE_SIZE*(tox+fromx+1)-image_width)/2;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(image_width, image_height);
            Log.d(TAG,"topmargin: "+topMargin+" leftmargin: "+leftMargin);
            layoutParams.topMargin = topMargin;
            layoutParams.leftMargin = leftMargin;
            //Log.d(TAG,"topmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).topMargin+" leftmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).leftMargin);
            iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//needed for dashed line to work properly
            iv.setRotation((float) Math.toDegrees(Math.atan2(deltay, deltax)));
            //iv.setRotation(45);
            iv.setTag(days);
            iv.setLayoutParams(layoutParams);
            Log.d(TAG,"topmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).topMargin+" leftmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).leftMargin);
            relativeLayout.addView(iv);
        }while(c.moveToNext());
        return relativeLayout;
    }

    private RelativeLayout addIslandsB(RelativeLayout relativeLayout) {
        SQLiteDatabase db = Globals.dbHelper.getReadableDatabase();
        String selection = DbHelper.C_FILENAME + "= ? ";//+ DbHelper.C_ARCHIPELAGO + ' = ?"
        String [] selectArgs = new String[]{Globals.saveName};
        Cursor c = db.query(DbHelper.T_ISLANDS,null,selection,selectArgs,null,null,null);
        if(!c.moveToFirst()){db.close();c.close(); return relativeLayout;}
        int columnIndex;
        do{
            columnIndex = c.getColumnIndex(DbHelper.C_NAME);
            String name = c.getString(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_X);
            int x = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_Y);
            int y = c.getInt(columnIndex);
            columnIndex = c.getColumnIndex(DbHelper.C_INDUSTRY);
            String industry = c.getString(columnIndex);

            Island i = new Island(name,x,y,industry);

            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.green);///@TODO add correct island image
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setTag(i);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(TILE_SIZE,TILE_SIZE);
            layoutParams.topMargin = y*TILE_SIZE;
            layoutParams.leftMargin = x*TILE_SIZE;
            iv.setLayoutParams(layoutParams);
            Log.d(TAG,"topmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).topMargin+" leftmargin: "+((RelativeLayout.LayoutParams)(iv.getLayoutParams())).leftMargin);
            relativeLayout.addView(iv);
        }while(c.moveToNext());
        c.close();
        db.close();

        return relativeLayout;

    }
}
