package ift3150.merchc;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Diego on 2015-06-19.
 */
public class BoatStatusFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.boat_status_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button repairButton = (Button) view.findViewById(R.id.button_repair);
        TextView repairedText = (TextView) view.findViewById(R.id.boat_repaired);
        int damage = Globals.boat.getMaxRepair()-Globals.boat.getRepair();
        if(damage>0){
            repairedText.setVisibility(View.GONE);
            int cost = Globals.boat.getCurrentIsland().getRepairCost(damage);
            repairButton.setText("Repair your boat for "+cost+"$");
            repairButton.setTag(cost);
        }else{
            repairButton.setVisibility(View.GONE);
        }
    }

    public void fullRepair(View view) {
        view.setVisibility(View.GONE);
        TextView repairedText = (TextView) getView().findViewById(R.id.boat_repaired);
        repairedText.setVisibility(View.VISIBLE);
    }
}
