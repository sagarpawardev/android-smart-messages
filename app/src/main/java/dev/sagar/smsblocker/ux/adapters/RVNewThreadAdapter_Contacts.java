package dev.sagar.smsblocker.ux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;

/**
 * Created by sagarpawar on 22/10/17.
 */

public class RVNewThreadAdapter_Contacts extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    //Java Android
    private Context context;

    //Java Core
    private ArrayList<Contact> contacts;
    private Callback callback;

    //Constants
    private final int CONTACTS = 0;
    private final int NON_CONTACTS = 1;

    public RVNewThreadAdapter_Contacts(Context context, ArrayList<Contact> contacts, Callback callback) {
        this.context = context;
        this.contacts = contacts;
        this.callback = callback;
    }

    private ContactViewHolder contactViewHolder(ViewGroup parent, LayoutInflater inflater){
        View itemView= inflater.inflate(R.layout.row_rv_new_thread__contacts, parent, false);
        itemView.setOnClickListener(this);
        ContactViewHolder holder = new ContactViewHolder(itemView);
        return holder;
    }

    private NonContactViewHolder nonContactViewHolder(ViewGroup parent, LayoutInflater inflater){
        View itemView= inflater.inflate(R.layout.row_rv_new_thread__noncontacts, parent, false);
        itemView.setOnClickListener(this);

        NonContactViewHolder holder = new NonContactViewHolder(itemView);
        return holder;
    }

    private void contactsOnBindViewHolder(RecyclerView.ViewHolder viewHolder, int position){
        ContactViewHolder holder = (ContactViewHolder) viewHolder;
        Contact contact = contacts.get(position);
        String displayName = contact.getDisplayName();
        String phoneNo = contact.getNumber();
        String id = contact.getId();
        holder.tvId.setText(id);
        holder.tvName.setText(displayName);
        holder.tvNumber.setText(phoneNo);
    }

    private void nonContactsOnBindViewHolder(RecyclerView.ViewHolder viewHolder){
        NonContactViewHolder holder = (NonContactViewHolder) viewHolder;
        String phoneNo = callback.onEmptySearchList();
        holder.tvNumber.setText(phoneNo);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType){
            case CONTACTS: holder = contactViewHolder(parent, inflater);  break;
            case NON_CONTACTS: holder = nonContactViewHolder(parent, inflater);  break;
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return contacts.size()==0 ? NON_CONTACTS : CONTACTS;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof  ContactViewHolder){
            contactsOnBindViewHolder(viewHolder, position);
        }
        else if(viewHolder instanceof NonContactViewHolder){
            nonContactsOnBindViewHolder(viewHolder);
        }
    }

    @Override
    public int getItemCount() {
        int size = contacts.size();
        if(size == 0) size = 1;
        return size;
    }

    @Override
    public void onClick(View view) {
        TextView tvPhoneNo = view.findViewById(R.id.tv_phone_no);
        String phoneNo = tvPhoneNo.getText().toString();
        callback.onClicked(phoneNo);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvNumber, tvId;

        ContactViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_display_name);
            tvNumber = view.findViewById(R.id.tv_phone_no);
            tvId = view.findViewById(R.id.tv_contact_id);
        }
    }

    class NonContactViewHolder extends RecyclerView.ViewHolder{
        TextView tvNumber, tvId;

        NonContactViewHolder(View view) {
            super(view);
            tvNumber = view.findViewById(R.id.tv_phone_no);
            tvId = view.findViewById(R.id.tv_contact_id);
        }
    }

    public interface Callback{
        void onClicked(String threadId);
        String onEmptySearchList();
    }
}
