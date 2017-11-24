package dev.sagar.smsblocker.ux.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Map;

import dev.sagar.smsblocker.Constants;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVThreadOverviewAdapter;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;

public class ThreadOverviewActivity extends AppCompatActivity implements RVThreadOverviewAdapter.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //View
    RecyclerView recyclerView;
    FloatingActionButton fab;

    //Java Core
    InboxUtil inboxUtil = null;
    final private int REQUEST_CODE_ALL_PERMISSIONS = 123;
    private PermissionUtilSingleton permissionInstance = PermissionUtilSingleton.getInstance();

    //Java Android
    Map<String, SMS> smsMap = null;
    RVThreadOverviewAdapter adapter;

    private void init(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.lv_threads);
    }

    private void preGetData(){
        inboxUtil = new InboxUtil(this);
    }

    private void getData(){
        smsMap = inboxUtil.getMsgs();
    }

    private void postGetData(){
        adapter = new RVThreadOverviewAdapter(this, smsMap, this);
    }

    private void addListeners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewThreadActivity();
            }
        });
    }

    private void process(){
        boolean hasPermissions = permissionInstance.hasPermissions(this, Constants.PERMISSIONS);
        if(!hasPermissions) {
            askPermissions();
            return;
        }
        showInbox();
    }

    private void showInbox(){

        if(smsMap.size() == 0) {
            Toast.makeText(this, "You have not recieved any SMS Yet!!", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void askPermissions(){
        ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);
    }

    private void startNewThreadActivity(){
        Intent intent = new Intent(this, NewThreadActivity.class);
        startActivity(intent);
    }

    private void refresh(){
        String methodName = "refresh()";
        log.info(methodName, "Entering..");

        smsMap.clear();
        Map<String, SMS> map = inboxUtil.getMsgs();
        smsMap.putAll(map);
        adapter.notifyDataSetChanged();

        log.error(methodName, "Returning..");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ALL_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    showInbox();
                } else {
                    // Permission Denied
                    Toast.makeText(ThreadOverviewActivity.this, "Permissions Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        preGetData();
        getData();
        postGetData();
        process();
        addListeners();
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
        refresh();
        super.onStart();
    }

    @Override
    public void onItemClicked(String threadId) {
        Intent intent = new Intent(this, ThreadActivity.class);
        Bundle basket = new Bundle();

        basket.putString(ThreadActivity.KEY_THREAD_ID, threadId);
        intent.putExtras(basket);
        startActivity(intent);
    }
}
