package dev.sagar.smsblocker.ux.listeners.actionmodecallbacks;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVHomeAdapter;

/**
 * Created by sagarpawar on 24/11/17.
 */

public class AMCallbackThreadOverview implements ActionMode.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());
    private ActionMode actionMode = null;

    private RVHomeAdapter adapter;
    public AMCallbackThreadOverview(RVHomeAdapter adapter){
        this.adapter = adapter;
    }

    private void delete(){
        final String methodName =  "onActionItemClicked()";
        log.justEntered(methodName);

        adapter.deleteSelections();

        log.returning(methodName);
    }

    public void finish(){
        final String methodName =  "onActionItemClicked()";
        log.justEntered(methodName);

        if(actionMode!=null)
            actionMode.finish();

        log.returning(methodName);
    }

    //--- ActionMode.Callback Overrides Start ---
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        final String methodName =  "onActionItemClicked()";
        log.justEntered(methodName);

        switch (item.getItemId()){
            case R.id.action_delete: delete(); break;
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
        inflater.inflate( R.menu.contextual_threadoverview, menu );
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
