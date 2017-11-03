package dev.sagar.smsblocker.ux.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import dev.sagar.smsblocker.Constants;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.ux.adapters.RVThreadAdapter;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.ContactUtil;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.PermissionUtil;
import dev.sagar.smsblocker.tech.utils.SMSUtil;

public class ThreadActivity extends AppCompatActivity implements RVThreadAdapter.Callback {

    //Constants
    public static final String KEY_THREAD_ID = "THREAD_ID";

    //View
    private RecyclerView recyclerView;
    private Button btnSend;
    private EditText etMsg;

    //Internal
    private ArrayList<SMS> smses;
    private String threadId;
    private InboxUtil readerUtil = null;
    private SMSUtil smsUtil;
    final private int REQUEST_CODE_ALL_PERMISSIONS = 123;


    private void process(){

        RVThreadAdapter adapter = new RVThreadAdapter(this, this, smses);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        boolean hasPermissions = PermissionUtil.hasPermissions(this, Constants.PERMISSIONS);
        if(!hasPermissions) {
            askPermissions();
            return;
        }
        updateActionBar();
    }

    private void getData(){
        //From Previous Activity
        Bundle basket = getIntent().getExtras();
        threadId = basket.getString(KEY_THREAD_ID);

        //By Computation
        smses = readerUtil.getAllSMSFromTo(threadId);
    }

    private void updateActionBar(){
        String contact = ContactUtil.getContactName(this, threadId);
        getSupportActionBar().setTitle(contact);
    }

    private void sendMsg(){
        String msg = etMsg.getText().toString();
        String phoneNo = threadId;
        smsUtil.sendSMS(phoneNo, msg, null);
    }

    private void askPermissions(){
        ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);
    }

    private void init(){
        recyclerView = (RecyclerView) findViewById(R.id.lv_sms);
        btnSend = (Button) findViewById(R.id.btn_send);
        etMsg = (EditText) findViewById(R.id.et_msg);

        if(readerUtil == null) readerUtil = new InboxUtil(this);
        smsUtil = new SMSUtil(this);
    }

    private void addListeners(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        init();
        getData();
        process();
        addListeners();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ALL_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    updateActionBar();
                } else {
                    // Permission Denied
                    Toast.makeText(ThreadActivity.this, "Permissions Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
