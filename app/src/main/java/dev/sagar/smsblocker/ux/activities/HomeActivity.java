package dev.sagar.smsblocker.ux.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.EventCode;
import dev.sagar.smsblocker.tech.beans.Conversation;
import dev.sagar.smsblocker.tech.broadcastreceivers.LocalSMSReceivedReceiver;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.datastructures.PositionLog;
import dev.sagar.smsblocker.tech.utils.ConversationUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVHomeAdapter;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.ux.customviews.NotificationView;
import dev.sagar.smsblocker.ux.listeners.actionmodecallbacks.AMCallbackThreadOverview;

public class HomeActivity extends AppCompatActivity
        implements RVHomeAdapter.Callback, LocalSMSReceivedReceiver.Callback, ConversationUtil.Callback,
        View.OnClickListener{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //View
    RecyclerView recyclerView;
    FloatingActionButton fab;
    NotificationView notificationView;
    View viewPlaceHolder;

    //Java Core
    //InboxUtil inboxUtil = null;
    ConversationUtil conversationUtil = null;
    final private int REQUEST_CODE_ALL_PERMISSIONS = 123;
    private PermissionUtilSingleton permUtil = PermissionUtilSingleton.getInstance();
    private LocalSMSReceivedReceiver smsReceiver = null;
    private boolean alreadyAsked = false;

    //Java Android
    IndexedHashMap<String, Conversation> conversationMap = new IndexedHashMap<>();
    RVHomeAdapter adapter;
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
        viewPlaceHolder = findViewById(R.id.holder_placeholder);

        //if(inboxUtil == null) inboxUtil = new InboxUtil(this);
        if(conversationUtil == null) conversationUtil = new ConversationUtil(this, this);
        adapter = new RVHomeAdapter(this, conversationMap, this);
        amCallback = new AMCallbackThreadOverview(adapter);
        smsReceiver = new LocalSMSReceivedReceiver(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void showInboxView(){
        viewPlaceHolder.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        conversationUtil.getLatestMsgs();

        if(conversationMap.size() == 0) {
            Toast.makeText(this, "You have not received any SMS Yet!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideInboxView(){
        recyclerView.setVisibility(View.GONE);
        viewPlaceHolder.setVisibility(View.VISIBLE);
        TextView tvPlaceholder = (TextView) findViewById(R.id.tv_placeholder);
        tvPlaceholder.setText(R.string.text_placeholder__inbox_permission);
        ImageView imgView = (ImageView) findViewById(R.id.iv_placeholder);
        imgView.setImageResource(R.drawable.placeholder_permission__inbox2);
        Button btn = (Button) findViewById(R.id.btn_placeholder);
        btn.setOnClickListener(this);
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
                permUtil.askToMakeAppDefault(HomeActivity.this);
            }
        });
    }


    private void askForDefault(){
        String methodName = "askForDefault()";
        log.justEntered(methodName);

        log.debug(methodName, "Checking if App is Default");
        if(!permUtil.isAppDefault(this) && !alreadyAsked){
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

    public void registerSMSReceiver(){
        final String methodName =  "registerReceivers()";
        log.debug(methodName, "Just Entered..");

        registerReceiver(smsReceiver, new IntentFilter(EventCode.LOCAL_SMS_RECEIVED));
        smsReceiver.isRegistered = true;

        log.debug(methodName, "Returning..");
    }

    public void unregisterSMSReceiver(){
        final String methodName =  "unregisterReceivers()";
        log.justEntered(methodName);

        if(smsReceiver.isRegistered)
            unregisterReceiver(smsReceiver);
        smsReceiver.isRegistered = false;

        log.returning(methodName);
    }

    public void updateSMSinUI(SMS sms){
        final String methodName =  "addSMSinUI()";
        log.justEntered(methodName);

        String phoneNo = sms.getFrom();
        Conversation conversation = new Conversation(getApplicationContext(), sms);

        log.error(methodName, "Improvement can be done here");
        PositionLog mPositionLog = conversationMap.put(phoneNo, conversation);
        int oldPosition = mPositionLog.getOldPosition();
        int newPosition = mPositionLog.getNewPosition();
        if(oldPosition == -1) { //Item Newly Added
            adapter.notifyItemInserted(0); //Moved Item to First
        }
        else{
            adapter.notifyItemMoved(oldPosition, newPosition);
        }

        //If List is on top
        if(listIsAtTop()){
            recyclerView.scrollToPosition(0); //Scroll to first
        }
        else{
            Toast.makeText(this, "New SMS", Toast.LENGTH_SHORT).show();
        }

        log.returning(methodName);
    }

    private boolean listIsAtTop()   {
        if(recyclerView.getChildCount() == 0) return true;
        return recyclerView.getChildAt(0).getTop() == 0;
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

        final String methodName =  "onCreate()";
        log.justEntered(methodName);

        setContentView(R.layout.activity_home);

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

        log.returning(methodName);

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
        final String methodName =  "onStart()";
        log.justEntered(methodName);

        boolean hasPermission = permUtil.hasPermission(this, READ_SMS);
        if(hasPermission) {
            showInboxView();
            askForDefault();
        }
        else{
            permUtil.ask(this, ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);
        }
        registerSMSReceiver();

        log.info(methodName, "Refreshinng local DB...");
        conversationUtil.refreshDB();
        super.onStart();

        log.returning(methodName);
    }

    @Override
    protected void onStop() {
        unregisterSMSReceiver();
        super.onStop();
    }

    @Override
    protected void onResume() {
        final String methodName =  "onResume()";
        log.justEntered(methodName);

        boolean isAppDefault = permUtil.isAppDefault(this);
        if(isAppDefault && notificationView.getVisibility() == View.VISIBLE){
            notificationView.close();
        }

        super.onResume();
        log.returning(methodName);
    }

    //--- AppCompatActivity Overrides End ---


    //--- RVHomeAdapter.Callback Overrides Start ---
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
    //--- RVHomeAdapter.Callback Overrides End ---


    //--- LocalSMSReceivedReceiver.Callback Overriders Start ---
    @Override
    public void onSMSReceived(SMS sms) {
        final String methodName = "onSMSReceived()";
        log.justEntered(methodName);

        updateSMSinUI(sms);

        log.returning(methodName);
    }
    //--- LocalSMSReceivedReceiver.Callback Overriders Ends ---


    //--- View.OnClickListener Overrides Starts ---
    @Override
    public void onClick(View view) {
        final String methodName = "onSMSReceived()";
        log.justEntered(methodName);

        int id = view.getId();
        switch (id){
            case R.id.btn_placeholder:
                permUtil.ask(this, READ_SMS, REQUEST_CODE_ALL_PERMISSIONS); break;
        }

        log.returning(methodName);
    }
    //--- View.OnClickListener Overrides Ends ---


    //--- ConversationUtil.Callback Overrides Starts ---
    @Override
    public void onDBRefreshed(int count) {
        final String methodName =  "onDBRefreshed()";
        log.justEntered(methodName);

        log.info(methodName, "DB Refreshed :D...");
        log.info(methodName, "Now retrieving latest list");
        conversationUtil.getLatestMsgs();

        log.returning(methodName);
    }

    @Override
    public void onLatestMsgsFetched(IndexedHashMap<String, Conversation> map) {
        final String methodName =  "onDBRefreshed()";
        log.justEntered(methodName);

        conversationMap.update(map);
        adapter.notifyDataSetChanged();

        log.returning(methodName);
    }

    //--- ConversationUtil.Callback Overrides Ends ---
}
