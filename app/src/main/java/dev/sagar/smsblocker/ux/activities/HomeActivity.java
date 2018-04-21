package dev.sagar.smsblocker.ux.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import dev.sagar.smsblocker.tech.utils.PhoneUtilsSingleton;
import dev.sagar.smsblocker.ux.adapters.RVHomeAdapter;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.ux.customviews.NotificationView;
import dev.sagar.smsblocker.ux.listeners.actionmodecallbacks.AMCallbackHome;

public class HomeActivity extends AppCompatActivity
        implements RVHomeAdapter.Callback, LocalSMSReceivedReceiver.Callback,
        ConversationUtil.Callback, View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        SwipeRefreshLayout.OnRefreshListener{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //View
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private NotificationView notificationView;
    private View viewPlaceHolder, holderLoader, holderMain, holderSwitch;
    private ProgressBar progressBar;
    private SwitchCompat switchUnread;
    private TextView tvTotalCount;
    private SwipeRefreshLayout swipeRefreshLayout;

    //Java Core
    ConversationUtil conversationUtil = null;
    final private int REQUEST_CODE_ALL_PERMISSIONS = 123;
    private PermissionUtilSingleton permUtil = PermissionUtilSingleton.getInstance();
    private PhoneUtilsSingleton phoneUtils = PhoneUtilsSingleton.getInstance();
    private LocalSMSReceivedReceiver smsReceiver = null;
    private boolean alreadyAsked = false;

    //Java Android
    IndexedHashMap<String, Conversation> conversationMap = new IndexedHashMap<>();
    RVHomeAdapter adapter;
    private AMCallbackHome amCallback;

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
        switchUnread = (SwitchCompat) findViewById(R.id.switch_unread);
        tvTotalCount = (TextView) findViewById(R.id.tv_total_count);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        holderLoader = findViewById(R.id.holder_loader);
        holderMain = findViewById(R.id.holder_main);
        holderSwitch = findViewById(R.id.holder_switch);

        //if(inboxUtil == null) inboxUtil = new InboxUtil(this);
        if(conversationUtil == null) conversationUtil = new ConversationUtil(this, this);
        adapter = new RVHomeAdapter(this, conversationMap, this);
        amCallback = new AMCallbackHome(adapter);
        smsReceiver = new LocalSMSReceivedReceiver(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        setFont();
    }

    private void setFont(){
        Typeface myFont = Typeface.createFromAsset(getAssets(),"fonts/VarelaRound-Regular.ttf");
        switchUnread.setTypeface(myFont);
        tvTotalCount.setTypeface(myFont);
    }

    private void showInboxView(){
        viewPlaceHolder.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        conversationUtil.getLatestMsgs();

        if(conversationMap.size() == 0) {
            //Toast.makeText(this, "You have not received any SMS Yet!!", Toast.LENGTH_SHORT).show();
            holderLoader.setVisibility(View.VISIBLE);
            holderMain.setVisibility(View.GONE);
            holderSwitch.setVisibility(View.GONE);
        }
        else{
            holderSwitch.setVisibility(View.VISIBLE);
            holderMain.setVisibility(View.VISIBLE);
            holderLoader.setVisibility(View.GONE);
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
        switchUnread.setOnCheckedChangeListener(this);
        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permUtil.askToMakeAppDefault(HomeActivity.this);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    private void askForDefault(){
        String methodName = "askForDefault()";
        log.justEntered(methodName);

        log.debug(methodName, "Checking if App is Default");
        if(!permUtil.isAppDefault(this) && !alreadyAsked){
            notificationView.setVisibility(View.VISIBLE);
            alreadyAsked = true;
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
        log.justEntered(methodName);

        registerReceiver(smsReceiver, new IntentFilter(EventCode.LOCAL_SMS_RECEIVED));
        smsReceiver.isRegistered = true;

        log.returning(methodName);
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
        final String methodName =  "addSMSinUI(SMS)";
        log.justEntered(methodName);

        String lThreadId = sms.getThreadId();
        Conversation conversation = new Conversation(getApplicationContext(), sms);

        log.error(methodName, "Improvement can be done here");
        PositionLog mPositionLog = conversationMap.put(lThreadId, conversation);
        int oldPosition = mPositionLog.getOldPosition();
        int newPosition = mPositionLog.getNewPosition();
        if(oldPosition == -1) { //Item Newly Added
            adapter.notifyItemInserted(0); //Moved Item to First
        }
        else{
            adapter.notifyItemMoved(oldPosition, newPosition);
            adapter.notifyDataSetChanged();
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

    private void startStarredActivity(){
        final String methodName =  "statrtStarredActivity()";
        log.justEntered(methodName);

        Intent intent = new Intent(this, StarredSMSActivity.class);
        startActivity(intent);

        log.returning(methodName);
    }

    private String getCountText(int count){
        final String methodName =  "getCountText()";
        log.justEntered(methodName);

        String strTotalCount = getString(R.string.text_conversations);
        if(count>1){
            strTotalCount = count +" "+ getString(R.string.text_conversations); //8 conversations
        }
        else if(count==1){
            strTotalCount = count +" "+getString(R.string.text_conversation); //1 conversation
        }
        else if(count == 0){
            strTotalCount = getString(R.string.label_no_such_conv); //No unread conversation
        }
        else{
            strTotalCount = "WTF?? this is not possible"; //Not possible :p
        }

        log.returning(methodName);
        return strTotalCount;
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

        final String methodName =  "onCreate(Bundle)";
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

        swipeRefreshLayout.setColorSchemeResources(
                R.color.grad_start,
                R.color.grad_end);

        //By default assume permission is Not given
        hideInboxView();

        log.returning(methodName);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_starred_sms: startStarredActivity(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_home, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Log Initiate
            private LogUtil log = new LogUtil(this.getClass().getName());

            @Override
            public boolean onQueryTextSubmit(String query) {
                final String methodName =  "onQueryTextSubmit()";
                log.justEntered(methodName);

                log.returning(methodName);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final String methodName =  "onQueryTextChange()";
                log.justEntered(methodName);

                //It is just standard procedure to check length before firing query
                log.info(methodName, "Filtering List with Query: "+newText);
                adapter.getFilter(RVHomeAdapter.FILTER_TEXT).filter(newText);

                //Uncheck switch
                switchUnread.setChecked(false); //Otherwise searching in UnreadOnly mode will show results form read as well

                log.returning(methodName);
                return true;
            }
        });

        return true;
    }

    private void refreshConversations(){
        final String methodName =  "refreshConversations()";
        log.justEntered(methodName);

        boolean hasPermission = permUtil.hasPermission(this, READ_SMS);
        if(hasPermission) {
            showInboxView();
            askForDefault();
            log.info(methodName, "Refreshinng local DB...");
            conversationUtil.refreshDB();
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            permUtil.ask(this, ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);
        }
    }

    @Override
    protected void onStart() {
        final String methodName =  "onStart()";
        log.justEntered(methodName);

        refreshConversations();
        registerSMSReceiver();

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
    public void onItemClicked(Conversation conversation) {
        final String methodName =  "onItemClicked(String)";
        log.justEntered(methodName);

        String threadId = conversation.getThreadId();
        String address = conversation.getAddress();
        Intent intent = new Intent(this, InboxActivity.class);
        Bundle basket = new Bundle();

        basket.putString(InboxActivity.KEY_THREAD_ID, threadId);
        basket.putString(InboxActivity.KEY_ADDRESS, address);
        intent.putExtras(basket);
        startActivity(intent);
        overridePendingTransition(R.anim.transition_fade_in, R.anim.transition_fade_out);

        log.returning(methodName);
    }

    @Override
    public void onItemLongClicked() {
        final String methodName =  "onItemLongClicked()";
        log.justEntered(methodName);

        startActionMode(amCallback);

        log.returning(methodName);
    }

    @Override
    public void onAllDeselected() {
        final String methodName =  "allDeselected()";
        log.justEntered(methodName);

        amCallback.finish();

        log.returning(methodName);
    }

    @Override
    public void onResultsFiltered() {
        final String methodName =  "onResultsFiltered()";
        log.justEntered(methodName);

        //Set Total Count
        String strTotalCount = getCountText(adapter.getItemCount());
        tvTotalCount.setText(strTotalCount);

        log.returning(methodName);
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
        final String methodName =  "onDBRefreshed(int)";
        log.justEntered(methodName);

        log.info(methodName, "DB Refreshed :D...");
        log.info(methodName, "Now retrieving latest list");
        conversationUtil.getLatestMsgs();

        log.returning(methodName);
    }

    @Override
    public void onLatestMsgsFetched(IndexedHashMap<String, Conversation> map) {
        final String methodName =  "onLatestMsgsFetched(IndexedHashMap<String, Conversation>)";
        log.justEntered(methodName);

        conversationMap.update(map);
        adapter.notifyDataSetChanged();

        if(conversationMap.size() == 0) {
            Toast.makeText(this, "You have not received any SMS Yet!!", Toast.LENGTH_SHORT).show();
        }
        else{
            holderMain.setVisibility(View.VISIBLE);
            holderSwitch.setVisibility(View.VISIBLE);
            holderLoader.setVisibility(View.GONE);
        }

        //Set Total Count
        String strTotalCount = getCountText(adapter.getItemCount());
        tvTotalCount.setText(strTotalCount);

        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        log.returning(methodName);
    }
    //--- ConversationUtil.Callback Overrides Ends ---


    //--- CompoundButton.OnCheckedChangeListener Overrides Starts ---
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        final String methodName =  "onCheckedChanged(CompoundButton, boolean)";
        log.justEntered(methodName);

        boolean wantUnread = switchUnread.isChecked();
        log.debug(methodName, "Is Switch Activated: "+wantUnread);
        if(wantUnread){
            adapter.getFilter(RVHomeAdapter.FILTER_UNREAD).filter("*");
        }else{
            adapter.getFilter(RVHomeAdapter.FILTER_UNREAD).filter(null);
        }

        log.returning(methodName);
    }
    //--- CompoundButton.OnCheckedChangeListener Overrides Ends ---


    //--- SwipeRefreshLayout.OnRefreshListener Overrides Starts
    @Override
    public void onRefresh() {
        final String methodName =  "onRefresh()";
        log.justEntered(methodName);

        refreshConversations();

        log.returning(methodName);
    }
    //--- SwipeRefreshLayout.OnRefreshListener Overrides Ends
}
