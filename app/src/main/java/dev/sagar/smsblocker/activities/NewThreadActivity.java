package dev.sagar.smsblocker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.adapters.RVNewThreadAdapter_Contacts;
import dev.sagar.smsblocker.beans.Contact;
import dev.sagar.smsblocker.utils.ContactUtil;

public class NewThreadActivity extends AppCompatActivity implements RVNewThreadAdapter_Contacts.Callback{
    //View
    private EditText etSearchContact;
    private RecyclerView rvContacts;

    //Internal Objects
    private ArrayList<Contact> contacts;
    private RVNewThreadAdapter_Contacts contactsAdapter;

    public void init(){
        etSearchContact = (EditText) findViewById(R.id.et_search_contact);
        rvContacts = (RecyclerView) findViewById(R.id.rv_contacts);
    }

    public void getData(){
        contacts = ContactUtil.getAllContacts(this);
    }

    public void process(){
        contactsAdapter = new RVNewThreadAdapter_Contacts(this, contacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setAdapter(contactsAdapter);
    }

    public void onContactSelected(String phoneNo){
        Bundle bundle = new Bundle();
        bundle.putString(ThreadActivity.KEY_THREAD_ID, phoneNo);
        Intent intent = new Intent(this, ThreadActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void searchContacts(String searchStr){
        ArrayList<Contact> contacts = ContactUtil.searchContacts(this, searchStr);
        this.contacts.clear();
        this.contacts.addAll(contacts);
        contactsAdapter.notifyDataSetChanged();
    }

    public void addListeners(){
        etSearchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchStr = editable.toString();
                searchContacts(searchStr);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        init();
        getData();
        process();
        addListeners();
    }

    @Override
    public void onClicked(String phoneNo) {
        onContactSelected(phoneNo);
    }
}
