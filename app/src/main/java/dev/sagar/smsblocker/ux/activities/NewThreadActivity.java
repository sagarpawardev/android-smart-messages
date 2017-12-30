package dev.sagar.smsblocker.ux.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.exceptions.ReadContactPermissionException;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.ux.adapters.RVNewThreadAdapter_Contacts;
import dev.sagar.smsblocker.tech.beans.Contact;

public class NewThreadActivity extends AppCompatActivity
        implements RVNewThreadAdapter_Contacts.Callback, View.OnClickListener{
    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //View
    private EditText etSearchContact;
    private RecyclerView rvContacts;
    private View viewPlaceholder;

    //Internal Objects
    private ArrayList<Contact> contacts = new ArrayList<>();
    private RVNewThreadAdapter_Contacts contactsAdapter;
    private PermissionUtilSingleton permUtil;
    private ContactUtilSingleton contactUtil;

    //Constants
    public static final String KEY_THREAD_ID = "THREAD_ID";
    final String[] ALL_PERMISSIONS = Permission.ALL;
    final String READ_SMS = Permission.READ_SMS;
    final String RECEIVE_SMS = Permission.RECEIVE_SMS;
    final String SEND_SMS = Permission.SEND_SMS;
    final String READ_CONTACTS = Permission.READ_CONTACTS;
    private final int REQUEST_CODE_ALL_PERMISSIONS = 123;

    public void init(){
        final String methodName =  "init()";
        log.justEntered(methodName);

        etSearchContact = (EditText) findViewById(R.id.et_search_contact);
        rvContacts = (RecyclerView) findViewById(R.id.rv_contacts);
        viewPlaceholder = findViewById(R.id.holder_placeholder);

        permUtil = PermissionUtilSingleton.getInstance();
        contactUtil = ContactUtilSingleton.getInstance();
        contactsAdapter = new RVNewThreadAdapter_Contacts(this, contacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setAdapter(contactsAdapter);

        log.returning(methodName);
    }

    public void hideContacts(){
        //Toast.makeText(this, "Hide Contacts Here", Toast.LENGTH_SHORT).show();
        rvContacts.setVisibility(View.GONE);
        viewPlaceholder.setVisibility(View.VISIBLE);
        TextView tvPlaceholder = (TextView) findViewById(R.id.tv_placeholder);
        tvPlaceholder.setText(R.string.text_placeholder__contact_permission);
        Button btn = (Button) findViewById(R.id.btn_placeholder);
        btn.setOnClickListener(this);
    }

    public void showContacts(){

        try {
            viewPlaceholder.setVisibility(View.GONE);
            rvContacts.setVisibility(View.VISIBLE);
            contacts.clear();
            ArrayList<Contact> temp = contactUtil.getAllContacts(this);
            contacts.addAll(temp);
            contactsAdapter.notifyDataSetChanged();
        }
        catch (ReadContactPermissionException ex){
            ex.printStackTrace();
        }

    }



    public void onContactSelected(String phoneNo){
        final String methodName =  "onContactSelected()";
        log.justEntered(methodName);

        Bundle bundle = new Bundle();
        bundle.putString(ThreadActivity.KEY_THREAD_ID, phoneNo);
        Intent intent = new Intent(this, ThreadActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

        log.returning(methodName);
    }

    public void searchContacts(String searchStr){
        final String methodName =  "searchContacts()";
        log.justEntered(methodName);

        ArrayList<Contact> contacts = null;
        try {
            contacts = contactUtil.searchContacts(this, searchStr);
            this.contacts.clear();
            this.contacts.addAll(contacts);
            contactsAdapter.notifyDataSetChanged();

        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        log.returning(methodName);
    }

    public void addListeners(){
        final String methodName =  "addListeners()";
        log.justEntered(methodName);

        etSearchContact.addTextChangedListener(new TextWatcher() {
            private LogUtil log = new LogUtil(this.getClass().getName());

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String methodName =  "beforeTextChanged()";
                log.justEntered(methodName);
                log.info(methodName,"Nothing done here");
                log.returning(methodName);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String methodName =  "onTextChanged()";
                log.justEntered(methodName);
                log.info(methodName,"Nothing done here");
                log.returning(methodName);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String methodName =  "afterTextChanged()";
                log.justEntered(methodName);

                String searchStr = editable.toString();
                searchContacts(searchStr);

                log.returning(methodName);
            }
        });

        log.returning(methodName);
    }


    //--- AppCompatActivity Overrides Start ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String methodName =  "onCreate()";
        log.justEntered(methodName);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set Action Bar Transparent
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        init();
        addListeners();
        hideContacts();

        log.returning(methodName);
    }

    @Override
    protected void onStart() {
        final String methodName =  "onStart()";
        log.justEntered(methodName);

        boolean hasContactPermission = permUtil.hasPermission(this, READ_CONTACTS);
        if(hasContactPermission){
            showContacts();
        }
        else{
            permUtil.ask(this, READ_CONTACTS, REQUEST_CODE_ALL_PERMISSIONS);
        }

        super.onStart();

        log.returning(methodName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        final String methodName =  "onRequestPermissionsResult()";
        log.justEntered(methodName);

        switch (requestCode) {
            case REQUEST_CODE_ALL_PERMISSIONS:
                boolean hasContactPermission = permUtil.hasPermission(this, READ_CONTACTS);
                if(hasContactPermission){
                    showContacts();
                }
                else{
                    hideContacts();
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        log.returning(methodName);
    }
    //--- AppCompatActivity Overrides Ends ---


    //--- RVNewThreadAdapter_Contacts.Callback Override Starts---
    @Override
    public void onClicked(String phoneNo) {
        final String methodName =  "onClicked()";
        log.justEntered(methodName);

        onContactSelected(phoneNo);

        log.returning(methodName);
    }

    @Override
    public String needSearchString() {
        final String methodName =  "needSearchString()";
        log.justEntered(methodName);

        String text = etSearchContact.getText().toString();

        log.returning(methodName);
        return text;
    }

    @Override
    public void onEmptySearch() {
        final String methodName =  "onEmptySearch()";
        log.justEntered(methodName);

        log.error(methodName, "Need to put Placeholder Here");


        log.returning(methodName);
    }
    //--- RVNewThreadAdapter_Contacts.Callback Override Ends---

    //--- View.OnClickListener Override Starts
    @Override
    public void onClick(View v) {
        final String methodName =  "onClick()";
        log.justEntered(methodName);

        switch (v.getId()) {
            case R.id.btn_placeholder: permUtil.ask(this, READ_CONTACTS, REQUEST_CODE_ALL_PERMISSIONS);
            break;
        }

        log.returning(methodName);
    }
    //--- View.OnClickListener Override Ends
}
