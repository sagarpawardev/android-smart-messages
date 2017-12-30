package dev.sagar.smsblocker.ux.activities;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.broadcastreceivers.LocalSMSReceiver;
import dev.sagar.smsblocker.tech.broadcastreceivers.SMSReceiver;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVThreadAdapter;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.tech.utils.SMSUtil;
import dev.sagar.smsblocker.ux.listeners.actionmodecallbacks.AMCallbackThread;

public class ThreadActivity extends AppCompatActivity implements
        RVThreadAdapter.Callback,
        LocalSMSReceiver.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Constants
    public static final String KEY_THREAD_ID = "THREAD_ID";
    final String[] ALL_PERMISSIONS = Permission.ALL;
    final String READ_SMS = Permission.READ_SMS;
    final String RECEIVE_SMS = Permission.RECEIVE_SMS;
    final String SEND_SMS = Permission.SEND_SMS;
    final String READ_CONTACTS = Permission.READ_CONTACTS;

    //View
    private RecyclerView recyclerView;
    private ImageButton btnSend;
    private EditText etMsg;
    private TextView tvHeader;

    //Java Android
    private RVThreadAdapter adapter;
    private AMCallbackThread amCallback;

    //Java Core
    private List<SMS> smses = new ArrayList<>();
    private String threadId;
    private InboxUtil inboxUtil = null;
    private SMSUtil smsUtil;
    private final int REQUEST_CODE_ALL_PERMISSIONS = 123;
    private PermissionUtilSingleton permUtil = PermissionUtilSingleton.getInstance();
    private LocalSMSReceiver smsReceiver = null;


    private void showMsgs(){
        smses.clear();
        List<SMS> tmp = inboxUtil.getAllSMSFromTo(threadId);
        smses.addAll(tmp);
        adapter.notifyDataSetChanged();
    }

    private void hideMsgs(){

    }


    private void updateActionBar(){
        final String methodName =  "updateActionBar()";
        log.debug(methodName, "Just Entered..");

        //If has permissions than Set Name otherwise Set number by default
        boolean hasContactPermission = permUtil.hasPermission(this, READ_CONTACTS);
        String contact = threadId;
        if(hasContactPermission){
            contact = ContactUtilSingleton.getInstance().getContactName(this, threadId);
        }
        tvHeader.setText(contact);

        log.debug(methodName, "Returning..");
    }


    private SMS sendMsg(){
        final String methodName =  "sendMsg()";
        log.debug(methodName, "Just Entered..");

        String msg = etMsg.getText().toString();
        String phoneNo = threadId;
        SMS newSMS = smsUtil.sendSMS(phoneNo, msg);

        log.debug(methodName, "Returning..");
        return newSMS;
    }


    public void registerSMSReceiver(){
        final String methodName =  "registerSMSReceiver()";
        log.debug(methodName, "Just Entered..");

        registerReceiver(smsReceiver, new IntentFilter(SMSReceiver.LOCAL_SMS_RECEIVED));
        smsReceiver.isRegistered = true;

        log.debug(methodName, "Returning..");
    }


    public void unregisterSMSReceiver(){
        final String methodName =  "unregisterSMSReceiver()";
        log.debug(methodName, "Just Entered..");

        if(smsReceiver.isRegistered)
            unregisterReceiver(smsReceiver);
        smsReceiver.isRegistered = false;

        log.debug(methodName, "Returning..");
    }


    public void smsSendUpdate(SMS newSMS) {
        final String methodName =  "smsSendUpdate()";
        log.justEntered(methodName);

        //add item in list
        smses.add(0, newSMS);

        //notify adapter that item is inserted
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0); //Scroll to bottom

        log.returning(methodName);
    }

    public void updateSMSinUI(SMS sms){
        final String methodName =  "updateSMSinUI()";
        log.debug(methodName, "Just Entered..");

        smses.add(0, sms); //Adding Element to first in List
        adapter.notifyDataSetChanged();
        //recyclerView.scrollToPosition(smses.size()-1);

        log.returning(methodName);
    }


    private void init(){
        final String methodName =  "init()";
        log.debug(methodName, "Just Entered..");

        recyclerView = (RecyclerView) findViewById(R.id.lv_sms);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        etMsg = (EditText) findViewById(R.id.et_msg);
        tvHeader = (TextView) findViewById(R.id.tv_header);

        if(inboxUtil == null) inboxUtil = new InboxUtil(this);
        smsUtil = new SMSUtil(this);
        smsReceiver = new LocalSMSReceiver(this);

        //From Previous Activity
        Bundle basket = getIntent().getExtras();
        threadId = basket.getString(KEY_THREAD_ID);
        adapter = new RVThreadAdapter(this, this, smses);
        amCallback = new AMCallbackThread(this, adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        log.returning(methodName);
    }




    private void addListeners(){
        final String methodName =  "addListeners()";
        log.debug(methodName, "Just Entered..");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String msg = etMsg.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Blank Message can not be send", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    SMS newSMS = sendMsg();
                    smsSendUpdate(newSMS);
                }
                etMsg.setText("");
            }
        });

        log.debug(methodName, "Returning..");
    }


    //--- Activity Overriders Start ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String methodName =  "onCreate()";
        log.debug(methodName, "Just Entered..");

        setContentView(R.layout.activity_thread);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set Action Bar Transparent
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        init();
        updateActionBar();
        hideMsgs();
        addListeners();

        log.debug(methodName, "Returning..");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        final String methodName =  "onRequestPermissionsResult()";
        log.justEntered(methodName);

        switch (requestCode) {
            case REQUEST_CODE_ALL_PERMISSIONS:
                boolean hasInboxPerm = permUtil.hasPermission(this, READ_CONTACTS);
                if(hasInboxPerm){
                    showMsgs();
                    updateActionBar();
                }
                else{
                    hideMsgs();
                    Toast.makeText(ThreadActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        log.returning(methodName);
    }

    @Override
    protected void onStart() {
        final String methodName =  "onStart()";
        log.justEntered(methodName);

        boolean hasPermission = permUtil.hasPermission(this, READ_CONTACTS);
        if(hasPermission){
            showMsgs();
            registerSMSReceiver();
        }
        else{
            permUtil.ask(this, ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);
        }

        super.onStart();

        log.returning(methodName);
    }

    @Override
    protected void onPause() {
        final String methodName =  "onPause()";
        log.justEntered(methodName);

        //setStatusRead
        inboxUtil.setStatusRead(threadId);

        super.onPause();

        log.returning(methodName);
    }

    @Override
    protected void onStop() {
        final String methodName =  "onStop()";
        log.debug(methodName, "Just Entered..");

        unregisterSMSReceiver();

        super.onStop();

        log.debug(methodName, "Returning..");
    }
    //--- Activity Overriders End ---


    //--- LocalSMSReceiver.Callback Overriders Start ---
    @Override
    public void onSMSReceived(SMS sms) {
        final String methodName = "onSMSReceived()";
        log.info(methodName, "Just Entered..");

        updateSMSinUI(sms);

        log.info(methodName, "Returning");
    }
    //--- LocalSMSReceiver.Callback Overriders Start ---


    //--- RVThreadAdapter.Callback Starts ---
    @Override
    public void onItemLongClicked() {
        final String methodName =  "onItemLongClicked()";
        log.debug(methodName, "Just Entered..");

        startActionMode(amCallback);

        log.debug(methodName, "Returning..");
    }

    @Override
    public void singleSelectionMode() {
        final String methodName =  "singleSelectionMode()";
        log.debug(methodName, "Just Entered..");

        amCallback.enableCopy(false);

        log.debug(methodName, "Returning..");
    }

    @Override
    public void multiSelectionMode() {
        final String methodName =  "multiSelectionMode()";
        log.debug(methodName, "Just Entered..");

        amCallback.enableCopy(true);

        log.debug(methodName, "Returning..");
    }

    @Override
    public void allDeselected() {
        final String methodName =  "allDeselected()";
        log.debug(methodName, "Just Entered..");

        amCallback.finish();

        log.debug(methodName, "Returning..");
    }
    //--- RVThreadAdapter.Callback Ends ---


}
