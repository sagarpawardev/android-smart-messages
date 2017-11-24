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

    private RVThreadAdapter adapter;
    public AMCallbackThread(RVThreadAdapter adapter){
        this.adapter = adapter;
    }

    //--- ActionMode.Callback Overrides Start ---
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        final String methodName =  "onActionItemClicked()";
        log.debug(methodName, "Just Entered..");

        if(item.getItemId() == R.id.action_delete){
            adapter.deleteSelections();
            mode.finish();
        }

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

        log.debug(methodName, "Returning..");
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        final String methodName =  "onDestroyActionMode()";
        log.debug(methodName, "Just Entered..");

        adapter.setSelectionModeOn(false);

        log.debug(methodName, "Returning..");
    }
    //--- ActionMode.Callback Overrides End ---
}
