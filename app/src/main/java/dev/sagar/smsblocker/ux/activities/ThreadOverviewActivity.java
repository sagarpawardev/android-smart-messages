package dev.sagar.smsblocker.ux.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVThreadOverviewAdapter;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.ux.customviews.NotificationView;
import dev.sagar.smsblocker.ux.listeners.actionmodecallbacks.AMCallbackThreadOverview;

public class ThreadOverviewActivity extends AppCompatActivity implements RVThreadOverviewAdapter.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //View
    RecyclerView recyclerView;
    FloatingActionButton fab;
    NotificationView notificationView;

    //Java Core
    InboxUtil inboxUtil = null;
    final private int REQUEST_CODE_ALL_PERMISSIONS = 123;
    private PermissionUtilSingleton permUtil = PermissionUtilSingleton.getInstance();

    //Java Android
    Map<String, SMS> smsMap = new LinkedHashMap<>();
    RVThreadOverviewAdapter adapter;
    private AMCallbackThreadOverview amCallback;

    //Constants
    final String[] ALL_PERMISSIONS = Permission.ALL;
    final String READ_SMS = Permission.READ_SMS;
    final String RECEIVE_SMS = Permission.RECEIVE_SMS;
    final String SEND_SMS = Permission.SEND_SMS;
    final String READ_CONTACTS = Permission.READ_CONTACTS;

    private void init(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        notificationView = (NotificationView) findViewById(R.id.notificationView);
        recyclerView = (RecyclerView) findViewById(R.id.lv_threads);

        if(inboxUtil == null) inboxUtil = new InboxUtil(this);
        adapter = new RVThreadOverviewAdapter(this, smsMap, this);
        amCallback = new AMCallbackThreadOverview(adapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void showInboxView(){
        smsMap.clear();
        Map<String, SMS> map = inboxUtil.getMsgs();
        smsMap.putAll(map);
        adapter.notifyDataSetChanged();
        if(smsMap.size() == 0) {
            Toast.makeText(this, "You have not recieved any SMS Yet!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideInboxView(){

    }

    private void addListeners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewThreadActivity();
            }
        });
        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permUtil.askToMakeAppDefault(ThreadOverviewActivity.this);
            }
        });
    }


    private void askForDefault(){
        String methodName = "askForDefault()";
        log.justEntered(methodName);

        log.debug(methodName, "Checking if App is Default");
        if(!permUtil.isAppDefault(this)){
            notificationView.setVisibility(View.VISIBLE);
        }
        else {
            notificationView.setVisibility(View.GONE);
        }

        log.returning(methodName);
    }

    private void startNewThreadActivity(){
        Intent intent = new Intent(this, NewThreadActivity.class);
        startActivity(intent);
    }


    //--- AppCompatActivity Overrides Start ---
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ALL_PERMISSIONS:

                boolean hasReadSMSPermission = permUtil.hasPermission(this, READ_SMS);

                if(hasReadSMSPermission){
                    showInboxView();
                    askForDefault();
                }
                else{
                    hideInboxView();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threadoverview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        init();
        addListeners();

        //By default assume permission is Not given
        hideInboxView();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

        boolean hasPermission = permUtil.hasPermission(this, READ_SMS);
        if(hasPermission) {
            showInboxView();
            askForDefault();
        }
        else{
            permUtil.ask(this, ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);
        }
        super.onStart();
    }
    //--- AppCompatActivity Overrides End ---


    //--- RVThreadOverviewAdapter.Callback Overrides Start ---
    @Override
    public void onItemClicked(String threadId) {
        Intent intent = new Intent(this, ThreadActivity.class);
        Bundle basket = new Bundle();

        basket.putString(ThreadActivity.KEY_THREAD_ID, threadId);
        intent.putExtras(basket);
        startActivity(intent);
    }

    @Override
    public void onItemLongClicked() {
        final String methodName =  "onItemLongClicked()";
        log.debug(methodName, "Just Entered..");

        startActionMode(amCallback);

        log.debug(methodName, "Returning..");
    }

    @Override
    public void onAllDeselected() {
        final String methodName =  "allDeselected()";
        log.debug(methodName, "Just Entered..");

        amCallback.finish();

        log.debug(methodName, "Returning..");
    }
    //--- RVThreadOverviewAdapter.Callback Overrides End ---
}
