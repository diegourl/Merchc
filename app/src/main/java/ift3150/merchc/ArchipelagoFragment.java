package ift3150.merchc;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
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
        RelativeLayout trajectoryLayout = (RelativeLayout)relativeLayout.findViewById(R.id.trajectoryLayout);
        //@TODO add island layout.       Right?
        trajectoryLayout = addTrajectories(trajectoryLayout);

        relativeLayout = addBoat(relativeLayout);

        return relativeLayout;
    }

    private RelativeLayout addBoat(RelativeLayout relativeLayout) {
        Island i = Globals.boat.getCurrentIsland();
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.boat);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(TILE_SIZE,TILE_SIZE);
        layoutParams.topMargin = (int)((i.getyCoord()+.5)*TILE_SIZE);
        layoutParams.leftMargin = (int)((i.getxCoord()+.5)*TILE_SIZE);
        iv.setLayoutParams(layoutParams);
        iv.setId(R.id.boat);
        relativeLayout.addView(iv);

        return relativeLayout;
    }


    private RelativeLayout addIslands(RelativeLayout relativeLayout){
        for(Island i: Globals.archipelago.values()){
            ImageView iv = new ImageView(getActivity());
            //iv.setImageResource(R.drawable.green);///@TODO add correct island image
            iv.setImageResource(iv.getContext().getResources().getIdentifier("drawable/"+i.getImage(),null,iv.getContext().getPackageName()));
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
        int tr = 0;
        for(int i = 0;i<archipelago.length;i++){
            for(int j = i+1 ;j<archipelago.length;j++){
                Log.d(TAG,"tr: "+tr++);
                Log.d(TAG,"length: "+archipelago.length);
                Trajectory trajectory;
                if(((Island)archipelago[j]).getName().equals(Globals.boat.getCurrentIsland().getName()))
                    trajectory = eventler.getTrajectory((Island)archipelago[j],(Island)archipelago[i]);//make sure current island is the from
                else
                    trajectory = eventler.getTrajectory((Island)archipelago[i],(Island)archipelago[j]);

                Log.d(TAG,"feasibility: "+trajectory.getFrom().getName()+" "+trajectory.getTo().getName()+" "+trajectory.isFeasible());
                if(!trajectory.isFeasible())
                    continue;

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
                int image_height= 220;
                int topMargin=(TILE_SIZE*(toy+fromy+1)-image_height)/2;
                int leftMargin=(TILE_SIZE*(tox+fromx+1)-image_width)/2;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(image_width, image_height);
                Log.d(TAG,"topmargin: "+topMargin+" leftmargin: "+leftMargin);
                layoutParams.topMargin = topMargin;
                layoutParams.leftMargin = leftMargin;

                iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//needed for dashed line to work properly
                iv.setRotation((float) Math.toDegrees(Math.atan2(toy-fromy, tox-fromx)));

                iv.setLayoutParams(layoutParams);

                if(Globals.boat.getCurrentIsland().getName().equals(trajectory.getFrom().getName())){
                    iv.setTag(trajectory);
                    iv.setImageResource(R.drawable.arrow);
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
        itemInfo.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_down));
        itemInfo.removeAllViews();

        switch (v.getId()){
            case R.id.island :

                Island i = (Island) v.getTag();

                Log.d(TAG,"island clicked: "+i.getName());

                //itemInfo.animate().translationY(itemInfo.getHeight()).setStartDelay(2000);
                //Log.d(TAG,"wait: "+wait);
                itemInfo = setBoatInfo(i,itemInfo);
                itemInfo.setVisibility(View.VISIBLE);
                itemInfo.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.slide_up));


                //itemInfo.animate().translationY(itemInfo.getHeight()).setStartDelay(2000);
                //itemInfo.animate().translationY(-300);
                //itemInfo.animate().translationY(0);
                break;

            case R.id.trajectory :
                Trajectory trajectory = (Trajectory)v.getTag();
                Log.d(TAG,"trajectory clicked");
                itemInfo = setTrajectoryInfo(itemInfo,trajectory);
                itemInfo.setVisibility(View.VISIBLE);
                itemInfo.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.slide_up));
                break;

            case R.id.sailButton :

                Log.d(TAG,"sail clicked.");
                Trajectory t = (Trajectory) v.getTag();
                itemInfo = sail(t, itemInfo);
                RelativeLayout trajectoryLayout = (RelativeLayout) relativeLayout.findViewById(R.id.trajectoryLayout);
                trajectoryLayout.removeAllViews();
                trajectoryLayout = addTrajectories(trajectoryLayout);

                ImageView boatView = (ImageView)relativeLayout.findViewById(R.id.boat);

                (boatAnimator(t,boatView)).start();
                Log.d(TAG,"boat: "+boatView.getX()+","+boatView.getY());

                BMapActivity bMapActivity = (BMapActivity)getActivity();
                bMapActivity.setBoatStats();
                bMapActivity.setIslandName();

                itemInfo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_up));
                break;

        }
    }


    //@TODO events happen once at most
    public LinearLayout sail(Trajectory t, LinearLayout itemInfo) {
        int day = 0;

        while(day++<t.getDuration()) {
            Globals.boat.tick();//handled by happened when Event.SAIL ?
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




        TextView arrival = new TextView(getActivity());
        arrival.setText("You have arrived at "+t.getTo().getName());
        arrival.setTextColor(getResources().getColor(R.color.text_colour));
        itemInfo.addView(arrival,itemInfo.getChildCount());

        return itemInfo;


    }

    private LinearLayout setBoatInfo(Island i, LinearLayout itemInfo) {
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
        return itemInfo;
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

    private AnimatorSet boatAnimator(Trajectory t, ImageView  boatView){
        float k = (float)0.20;
        AnimatorSet animatorSet = new AnimatorSet();

        float fromx = boatView.getX();
        float fromy = boatView.getY();

        /*Log.d(TAG,"boat: "+fromx+","+fromy);*/

        float tox =  (1-k)*t.getTo().getxCoord() + (k)*t.getFrom().getxCoord();
        tox *= TILE_SIZE;
        float toy = (1-k)*t.getTo().getyCoord() + k*t.getFrom().getyCoord();
        toy *= TILE_SIZE;

        float nextx = k*t.getTo().getxCoord() + (1-k)*t.getFrom().getxCoord();
        nextx *= TILE_SIZE;
        float nexty = k*t.getTo().getyCoord() + (1-k)*t.getFrom().getyCoord();
        nexty *= TILE_SIZE;

        ObjectAnimator firstR = ObjectAnimator.ofFloat(boatView,"rotation",180+(float)Math.toDegrees(Math.atan2(nexty-fromy, nextx-fromx)));
        ObjectAnimator firstX = ObjectAnimator.ofFloat(boatView,"x",fromx,nextx);
        ObjectAnimator firstY = ObjectAnimator.ofFloat(boatView,"y",fromy,nexty);

        ObjectAnimator lastR = ObjectAnimator.ofFloat(boatView,"rotation",180+(float)Math.toDegrees(Math.atan2(toy-nexty, tox-nextx)));
        ObjectAnimator lastX = ObjectAnimator.ofFloat(boatView,"x",nextx,tox);
        ObjectAnimator lastY = ObjectAnimator.ofFloat(boatView,"y",nexty,toy);

        animatorSet.play(firstR).before(firstX).with(firstY).before(lastR);
        animatorSet.play(lastX).with(lastY).after(lastR);

        animatorSet.setDuration(1000);

        return animatorSet;
    }


}
