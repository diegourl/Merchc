package ift3150.merchc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;

/**
 * Created by Diego on 2015-04-23.
 */
public class FPAdapter extends FragmentPagerAdapter {
    private final int NUM_TABS;
    private Context context;

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 1: return "crew";
            case 2: return "resources";
            case 3: return "equipment";
            default: return "passengers";
        }
    }

    SQLiteDatabase db;


    Cursor cursor;



    public FPAdapter(FragmentManager fm, Context context, int tabs){
        super(fm);
        this.context = context;
        this.NUM_TABS = tabs;
    }



    @Override
    public Fragment getItem(int i) {

        ListFragment fragment;

        SimpleAdapter simpleAdapter;
        SimpleAdapter.ViewBinder viewBinder;
        String [] columns;
        int [] to;

        db = Globals.dbHelper.getReadableDatabase();
        String selection;
        String [] selectArgs;
        String order;

        switch(i){

            case 1:
                fragment = new ListFragment();
                columns = new String[]{DbHelper.C_TYPE,DbHelper.C_NAME,DbHelper.C_UPKEEP,DbHelper.C_SALARY,DbHelper.C_WEIGHT,DbHelper.C_VOLUME};
                to = new int[]{R.id.crewType,R.id.crewName,R.id.crewUpkeep,R.id.crewSalary,R.id.crewWeight,R.id.crewVolume};//changed
                selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
                selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.currentIsland.getName()};
                order = DbHelper.C_TYPE+", "+DbHelper.C_CONTAINER+" "+ (Globals.boat.getName().compareTo(Globals.currentIsland.getName())<0?"ASC":"DESC");//might wanna change order
                cursor = db.query(DbHelper.T_CREW,null,selection,selectArgs,null,null,order);
                CrewCursorAdapter crewCursorAdapter = new CrewCursorAdapter(context,R.layout.row_crew,cursor, columns, to, 0);
                fragment.setListAdapter(crewCursorAdapter);
                    /*List<Map<String,String>> crew = Globals.boat.getCrewMaps();
                    crew.addAll(Globals.currentIsland.getCrewMaps());
                    simpleAdapter = new SimpleAdapter(context,crew,R.layout.row_crew,columns,to);
                    viewBinder = new CrewViewBinder();
                    simpleAdapter.setViewBinder(viewBinder);
                    fragment.setListAdapter(simpleAdapter);*/
                break;

            case 2:
                fragment = new ListFragment();
                columns = new String[]{DbHelper.C_TYPE,DbHelper.C_CONTAINER,DbHelper.C_CONTAINER};//usless?
                to = new int[]{R.id.resourceType,R.id.resourceBoatAmount,R.id.resourceIslandAmount};//useless
                selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
                selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.currentIsland.getName()};
                order = DbHelper.C_TYPE+", "+DbHelper.C_CONTAINER+" "+ (Globals.boat.getName().compareTo(Globals.currentIsland.getName())<0?"ASC":"DESC");
                cursor = db.query(DbHelper.T_RESOURCES,null,selection,selectArgs,null,null,order);
                ResourceCursorAdapter resourceCursorAdapter = new ResourceCursorAdapter(context,R.layout.row_resource,cursor, columns, to, 0);
                fragment.setListAdapter(resourceCursorAdapter);
                break;

            case 3:
                fragment = new ListFragment();
                columns = new String[]{DbHelper.C_TYPE,DbHelper.C_CONTAINER,DbHelper.C_CONTAINER};//usless?
                to = new int[]{R.id.equipmentType,R.id.equipmentBoatAmount,R.id.equipmentIslandAmount};//useless
                selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
                selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.currentIsland.getName()};
                order = DbHelper.C_TYPE+", "+DbHelper.C_CONTAINER+" "+ (Globals.boat.getName().compareTo(Globals.currentIsland.getName())<0?"ASC":"DESC");
                cursor = db.query(DbHelper.T_EQUIPMENT,null,selection,selectArgs,null,null,order);
                EquipmentCursorAdapter equipmentCursorAdapter = new EquipmentCursorAdapter(context,R.layout.row_equipment,cursor, columns, to, 0);
                fragment.setListAdapter(equipmentCursorAdapter);
                break;

            default :
                fragment = new ListFragment();
                columns = new String[]{DbHelper.C_TYPE,DbHelper.C_NAME,DbHelper.C_DESTINATION, DbHelper.C_FEE, DbHelper.C_WEIGHT, DbHelper.C_VOLUME};
                to = new int[]{R.id.passengerType,R.id.passengerName,R.id.passengerDestination, R.id.passengerFee, R.id.passengerWeight,R.id.passengerVolume};
                selection = DbHelper.C_FILENAME + " = ? and ("+DbHelper.C_CONTAINER+ " = ? or "+DbHelper.C_CONTAINER+" = ?)";
                selectArgs = new String[]{Globals.saveName, Globals.boat.getName(), Globals.currentIsland.getName()};
                order = DbHelper.C_TYPE+", "+DbHelper.C_CONTAINER+" "+ (Globals.boat.getName().compareTo(Globals.currentIsland.getName())<0?"ASC":"DESC");
                cursor = db.query(DbHelper.T_PASSENGERS,null,selection,selectArgs,null,null,order);
                PassengerCursorAdapter passengerCursorAdapter = new PassengerCursorAdapter(context,R.layout.row_passenger,cursor, columns, to, 0);
                fragment.setListAdapter(passengerCursorAdapter);
                    /*List<Map<String,String>> passengers = Globals.boat.getPassengerMaps();
                    passengers.addAll(Globals.currentIsland.getPassengerMaps());
                    simpleAdapter = new SimpleAdapter(context,passengers,R.layout.row_passenger,columns,to);
                    viewBinder = new PassengerViewBinder();
                    simpleAdapter.setViewBinder(viewBinder);
                    fragment.setListAdapter(simpleAdapter);*/
                break;
        }

        //db.close();
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }
}






