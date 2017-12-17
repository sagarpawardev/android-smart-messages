package dev.sagar.smsblocker.ux.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVNewThreadAdapter_Contacts;
import dev.sagar.smsblocker.tech.beans.Contact;

public class NewThreadActivity extends AppCompatActivity implements RVNewThreadAdapter_Contacts.Callback{
    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private EditText etSearchContact;
    private RecyclerView rvContacts;
    private SearchView searchView;

    //Java Core
    private ArrayList<Contact> contacts;
    private RVNewThreadAdapter_Contacts contactsAdapter;

    public void init(){
        final String methodName =  "init()";
        log.justEntered(methodName);

        //etSearchContact = (EditText) findViewById(R.id.et_search_contact);
        rvContacts = (RecyclerView) findViewById(R.id.rv_contacts);

        log.returning(methodName);
    }

    public void getData(){
        final String methodName =  "getData()";
        log.justEntered(methodName);

        contacts = ContactUtilSingleton.getInstance().getAllContacts(this);

        log.returning(methodName);
    }

    public void process(){
        final String methodName =  "process()";
        log.justEntered(methodName);

        contactsAdapter = new RVNewThreadAdapter_Contacts(this, contacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setAdapter(contactsAdapter);

        log.returning(methodName);
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

        ArrayList<Contact> contacts = ContactUtilSingleton.getInstance().searchContacts(this, searchStr);
        this.contacts.clear();
        this.contacts.addAll(contacts);
        contactsAdapter.notifyDataSetChanged();

        log.returning(methodName);
    }

    public void addListeners(){
        final String methodName =  "addListeners()";
        log.justEntered(methodName);

        /*etSearchContact.addTextChangedListener(new TextWatcher() {
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

                *//*String searchStr = editable.toString();
                searchContacts(searchStr);*//*

                log.returning(methodName);
            }
        });*/

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

        init();
        getData();
        process();
        addListeners();

        log.returning(methodName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_newthread, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchContacts(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            //onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        //String text = etSearchContact.getText().toString();
        String text = searchView.getQuery().toString();
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
}
