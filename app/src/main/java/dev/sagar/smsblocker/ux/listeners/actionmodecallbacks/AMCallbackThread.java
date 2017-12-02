package dev.sagar.smsblocker.ux.listeners.actionmodecallbacks;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVThreadAdapter;

/**
 * Created by sagarpawar on 24/11/17.
 */

public class AMCallbackThread implements ActionMode.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());
    private ActionMode actionMode = null;

    private RVThreadAdapter adapter;
    public AMCallbackThread(RVThreadAdapter adapter){
        this.adapter = adapter;
    }

    public void enableCopy(boolean isEnable){
        final String methodName =  "enableCopy()";
        log.debug(methodName, "Just Entered..");

        if(actionMode != null) {
            log.debug(methodName, "Enabling copy: "+isEnable);
            MenuItem item = actionMode.getMenu().findItem(R.id.action_copy);
            item.setVisible(!isEnable);
        }

        log.debug(methodName, "Returning..");
    }

    private void delete(){
        final String methodName =  "onActionItemClicked()";
        log.debug(methodName, "Just Entered..");

        adapter.deleteSelections();

        log.debug(methodName, "Returning..");
    }

    private void copy(){
        final String methodName =  "onActionItemClicked()";
        log.debug(methodName, "Just Entered..");

        adapter.copySelection();

        log.debug(methodName, "Returning..");
    }

    public void finish(){
        final String methodName =  "onActionItemClicked()";
        log.debug(methodName, "Just Entered..");

        if(actionMode!=null)
            actionMode.finish();

        log.debug(methodName, "Returning..");
    }

    //--- ActionMode.Callback Overrides Start ---
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        final String methodName =  "onActionItemClicked()";
        log.debug(methodName, "Just Entered..");

        switch (item.getItemId()){
            case R.id.action_delete: delete(); break;
            case R.id.action_copy: copy(); break;
        }
        mode.finish();

        log.debug(methodName, "Returning..");
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        final String methodName =  "onPrepareActionMode()";
        log.debug(methodName, "Just Entered..");

        log.debug(methodName, "Returning..");
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        final String methodName =  "onCreateActionMode()";
        log.debug(methodName, "Just Entered..");

        adapter.setSelectionModeOn(true);

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate( R.menu.contextual_thread, menu );
        this.actionMode = actionMode;

        log.debug(methodName, "Returning..");
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        final String methodName =  "onDestroyActionMode()";
        log.debug(methodName, "Just Entered..");

        adapter.setSelectionModeOn(false);
        this.actionMode = null;

        log.debug(methodName, "Returning..");
    }
    //--- ActionMode.Callback Overrides End ---
}
