package ift3150.merchc;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
/**
 * Created by yashMaster on 24/04/2015.
 */
import android.app.DialogFragment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class TrajectoryDialog extends DialogFragment{
    private static final String TAG = "TrajectDialog";
    private AlertDialog dialog;
    private Context context;
    private MapActivity mapActivity;
    public TrajectoryDialog(){}
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)  {
        // Use the Builder class for convenient dialog construction
        mapActivity= (MapActivity)getActivity();
        final Bundle bundle = getArguments();//not really sure on the fianl but we're not messing with it
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage((getString(R.string.trajectory_dialog_pre_days)+" "+ bundle.getCharSequence("days").toString()+
                " "+ getString(R.string.trajectory_dialog_post_days))+(Globals.getDailyDollars()* Integer.parseInt(bundle.getCharSequence("days").toString()))+
                " dollars and use  "+(Globals.getDailyFood()* Integer.parseInt(bundle.getCharSequence("days").toString()))+"units of food.");//R.string.dialog_fire_missiles
        builder.setPositiveButton(R.string.sail_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "trajectorydialog:" + bundle.getInt("tox") + "," + bundle.getInt("toy"));
                Globals.sail(Integer.parseInt(bundle.getCharSequence("days").toString()), bundle.getInt("tox")
                        , bundle.getInt("toy"));
                Log.d(TAG, "done sail");
                mapActivity.updateMenu();
                ImageView im =(ImageView)((MapActivity)getActivity()).findViewById(R.id.boat);
                int deltaX=(bundle.getInt("tox")- bundle.getInt("fromx"))*mapActivity.mapPopulator.DEFAULT_SIZE;
                int deltaY=(bundle.getInt("toy")- bundle.getInt("fromy"))*mapActivity.mapPopulator.DEFAULT_SIZE;
                TranslateAnimation moveLefttoRight = new TranslateAnimation(0,deltaX,0,-deltaY);
                moveLefttoRight.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }
                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        onEnd();

                    }
                });
                //should set in function of length
                moveLefttoRight.setDuration(1000);
                //moveLefttoRight.setFillAfter(true);//this little shit was the disspearing problem

                im.startAnimation(moveLefttoRight);
                Log.d(TAG, Globals.boat.getCurrentIsland().getName());
            }
        });

        builder.setNegativeButton(R.string.exit_island_info, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Create the AlertDialog object and return it
        dialog= builder.create();

        return dialog;
    }

    //called when the animation
    public void onEnd(){
        Log.d(TAG, Globals.boat.getCurrentIsland().getName());
        mapActivity.resetTrajectories();
        mapActivity.mapPopulator.resetBoat();
        //((MapActivity)getActivity()).resetTrajectories();

    }

    @Override
    public void onStart(){
        super.onStart();
        final Bundle bundle = getArguments();//not really sure on the fianl but we're not messing with it
        if(Globals.canSail(Integer.parseInt(bundle.getCharSequence("days").toString()))){
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);}
        else dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

}