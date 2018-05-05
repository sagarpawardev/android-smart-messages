package dev.sagar.smsblocker.ux.listeners.actionmodecallbacks;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVThreadAdapter;

/**
 * Created by sagarpawar on 24/11/17.
 */

public class AMCallbackThread implements ActionMode.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());
    private ActionMode actionMode = null;

    //Java Android
    private RVThreadAdapter adapter;
    private Context context;

    public AMCallbackThread(Context context, RVThreadAdapter adapter){
        this.adapter = adapter;
        this.context = context;
    }

    public void enableCopy(boolean isEnable){
        final String methodName =  "enableCopy()";
        log.justEntered(methodName);

        if(actionMode != null) {
            log.debug(methodName, "Enabling copy: "+isEnable);
            MenuItem item = actionMode.getMenu().findItem(R.id.action_copy);
            item.setVisible(!isEnable);
        }

        log.returning(methodName);
    }

    private void delete(){
        final String methodName =  "delete()";
        log.justEntered(methodName);

        boolean isDeleted = adapter.deleteSelections();
        if(isDeleted) {
            String txtDeleted = context.getResources().getString(R.string.txt_thread__deleted);
            Toast.makeText(context, txtDeleted, Toast.LENGTH_SHORT).show();
        }

        log.returning(methodName);
    }

    private void starSMS(){
        final String methodName =  "starSMS()";
        log.justEntered(methodName);

        boolean isStarred = adapter.startSelections();
        if(isStarred) {
            String txtSMSSaved = context.getResources().getString(R.string.txt_thread__starred);
            Toast.makeText(context, txtSMSSaved, Toast.LENGTH_SHORT).show();
        }

        log.returning(methodName);
    }

    private void copy(){
        final String methodName =  "onActionItemClicked()";
        log.justEntered(methodName);

        boolean isCopied = adapter.copySelection();
        if(isCopied){
            String txtDeleted = context.getResources().getString(R.string.txt_thread__copied);
            Toast.makeText(context, txtDeleted, Toast.LENGTH_SHORT).show();
        }

        log.returning(methodName);
    }

    public void finish(){
        final String methodName =  "onActionItemClicked()";
        log.justEntered(methodName);

        if(actionMode!=null)
            actionMode.finish();

        log.returning(methodName);
    }

    /*
    Currently not using this method as it contains bugs. Action mode destroys as soon as an action is selected
    Thus clears selected lists but selectors remain active if we comment line selectedSMS.clear()
    */
    /*private void showAlertDialog(){
        final String methodName =  "onActionItemClicked()";
        log.justEntered(methodName);

        adapter.logSelectedSize();
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Delete Messages");
        alertDialog.setMessage("Messages will be lost forever?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.logSelectedSize();
                        AMCallbackThread.this.finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.logSelectedSize();
                adapter.deleteSelections();
            }
        });

        alertDialog.show();

        log.returning(methodName);
    }*/

    //--- ActionMode.Callback Overrides Start ---
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        final String methodName =  "onActionItemClicked()";
        log.justEntered(methodName);

        switch (item.getItemId()){
            case R.id.action_delete: delete(); break;
            case R.id.action_star_sms: starSMS(); break;
            case R.id.action_copy: copy(); break;

        }
        mode.finish();

        log.returning(methodName);
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        final String methodName =  "onPrepareActionMode()";
        log.justEntered(methodName);

        log.returning(methodName);
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        final String methodName =  "onCreateActionMode()";
        log.justEntered(methodName);

        adapter.setSelectionModeOn(true);

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate( R.menu.contextual_chat, menu );
        this.actionMode = actionMode;

        log.returning(methodName);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        final String methodName =  "onDestroyActionMode()";
        log.justEntered(methodName);

        adapter.setSelectionModeOn(false);
        this.actionMode = null;

        log.returning(methodName);
    }
    //--- ActionMode.Callback Overrides End ---

}
