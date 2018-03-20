package dev.sagar.smsblocker.ux.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVStarredSMSAdapter;

public class StarredSMSActivity extends AppCompatActivity implements RVStarredSMSAdapter.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private RecyclerView rvStarredSMS;
    private RVStarredSMSAdapter adapter;

    //Java Core
    private List<SMS> smses;
    private InboxUtil inboxUtil;


    private void init(){
        final String methodName =  "init()";
        log.justEntered(methodName);

        rvStarredSMS = (RecyclerView) findViewById(R.id.rv_starred_sms);
        inboxUtil = new InboxUtil(this);

        log.returning(methodName);
    }

    private void getData(){
        final String methodName =  "getData()";
        log.justEntered(methodName);

        smses = inboxUtil.getSavedSMSes(InboxUtil.SORT_DESC);

        log.returning(methodName);
    }

    private void addListeners(){
        final String methodName =  "addListeners()";
        log.justEntered(methodName);

        log.returning(methodName);
    }

    private void process(){
        final String methodName =  "process()";
        log.justEntered(methodName);
        adapter = new RVStarredSMSAdapter(this, smses, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvStarredSMS.setLayoutManager(layoutManager);
        rvStarredSMS.setAdapter(adapter);
        log.returning(methodName);
    }

    //--- AppCompatActivity Overrides Starts ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String methodName =  "onCreate(Bundle)";
        log.justEntered(methodName);

        setContentView(R.layout.activity_starred_sms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        init();
        getData();
        process();
        addListeners();
    }
    //--- AppCompatActivity Overrides Ends ---

}
