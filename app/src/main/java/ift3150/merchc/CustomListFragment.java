package ift3150.merchc;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Diego on 2015-04-26.
 */
public class CustomListFragment extends ListFragment {
    private static final String TAG = "CustomListFragment";
    void blank(View view){}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView =  getListView();
        listView.setDivider(new ColorDrawable(0xffffffff));
        listView.setDividerHeight(1);
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.list_footer,null);
        listView.addFooterView(footerView);
        listView.setFooterDividersEnabled(false);
        /*listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setOverscrollFooter(getResources().getDrawable(R.drawable.diegetic));*/

        Log.d(TAG,""+listView.getOverScrollMode());

    }
}
