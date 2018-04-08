package dev.sagar.smsblocker.ux.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.customviews.DisplayPictureView;
import dev.sagar.smsblocker.ux.utils.ThemeUtil;

/**
 * Created by sagarpawar on 22/10/17.
 */

public class RVNewThreadAdapter_Contacts extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;

    //Java Core
    private ArrayList<Contact> contacts;
    private Callback callback;
    private ThemeUtil themeUtil;

    //Constants
    private final int CONTACTS = 0;
    private final int NON_CONTACTS = 1;

    public RVNewThreadAdapter_Contacts(Context context, ArrayList<Contact> contacts, Callback callback) {
        this.context = context;
        this.contacts = contacts;
        this.callback = callback;
        themeUtil = new ThemeUtil(context);
    }

    private ContactViewHolder contactViewHolder(ViewGroup parent, LayoutInflater inflater){
        final String methodName =  "contactViewHolder()";
        log.justEntered(methodName);

        View itemView= inflater.inflate(R.layout.row_rv_new_thread__contacts, parent, false);
        itemView.setOnClickListener(this);
        ContactViewHolder holder = new ContactViewHolder(itemView);

        log.returning(methodName);
        return holder;
    }

    private NonContactViewHolder nonContactViewHolder(ViewGroup parent, LayoutInflater inflater){
        final String methodName =  "nonContactViewHolder()";
        log.justEntered(methodName);

        View itemView= inflater.inflate(R.layout.row_rv_new_thread__noncontacts, parent, false);
        itemView.setOnClickListener(this);

        NonContactViewHolder holder = new NonContactViewHolder(itemView);

        log.returning(methodName);
        return holder;
    }

    private void contactsOnBindViewHolder(RecyclerView.ViewHolder viewHolder, int position){
        final String methodName =  "contactsOnBindViewHolder()";
        log.justEntered(methodName);


        ContactViewHolder holder = (ContactViewHolder) viewHolder;
        Contact contact = contacts.get(position);
        String displayName = contact.getDisplayName();
        String phoneNo = contact.getNumber();
        String id = contact.getId();
        String type = contact.getType();
        Uri uri = contact.getPhotoThumbnail();

        holder.tvId.setText(id);
        holder.tvName.setText(displayName);
        holder.tvNumber.setText(phoneNo);
        holder.tvAddressType.setText(type);
        themeUtil.styleDPView(holder.dpView, uri, contact);
        //holder.dpView.setPictureSrc(uri);


        //Adi changes Start
        Typeface myFont = themeUtil.getTypeface();
        holder.tvName.setTypeface(myFont,Typeface.BOLD);
        holder.tvNumber.setTypeface(myFont);
        holder.tvAddressType.setTypeface(myFont);
        //Adi changes End

        log.returning(methodName);
    }

    private void nonContactsOnBindViewHolder(RecyclerView.ViewHolder viewHolder){
        final String methodName =  "nonContactsOnBindViewHolder()";
        log.justEntered(methodName);

        NonContactViewHolder holder = (NonContactViewHolder) viewHolder;
        String phoneNo = callback.needSearchString();
        holder.tvNumber.setText(phoneNo);

        log.returning(methodName);
    }

    private boolean isNumber(char c){
        final String methodName =  "isNumber()";
        log.justEntered(methodName);

        boolean result = '0'<=c && c<='9';

        log.returning(methodName);
        return result;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final String methodName =  "onCreateViewHolder()";
        log.justEntered(methodName);

        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType){
            case CONTACTS: holder = contactViewHolder(parent, inflater);  break;
            case NON_CONTACTS: holder = nonContactViewHolder(parent, inflater);  break;
        }

        log.returning(methodName);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        final String methodName =  "getItemViewType()";
        log.justEntered(methodName);

        int type = contacts.size()==0 ? NON_CONTACTS : CONTACTS;

        log.returning(methodName);
        return type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final String methodName =  "onBindViewHolder()";
        log.justEntered(methodName);


        if(viewHolder instanceof  ContactViewHolder){
            contactsOnBindViewHolder(viewHolder, position);
        }
        else if(viewHolder instanceof NonContactViewHolder){
            nonContactsOnBindViewHolder(viewHolder);
        }

        log.returning(methodName);
    }

    @Override
    public int getItemCount() {
        final String methodName =  "getItemCount()";
        log.justEntered(methodName);

        int size = contacts.size();
        outer: if(size == 0){
            String searchStr = callback.needSearchString();

            //If Search String is empty and size also 0 then permissions problem or no contact
            if(searchStr.isEmpty()){
                size = 0;
                break outer;
            }

            //If Neither Numeric not spaces like '+91 8877993322' notify Activity and return size 0
            for(char c: searchStr.toCharArray()){
                if( !isNumber(c) && c!='+') {
                    callback.onEmptySearch();
                    log.returning(methodName);
                    size = 0;
                    break outer;
                }
            }
            size = 1;
        }

        log.returning(methodName);
        return size;
    }

    @Override
    public void onClick(View view) {
        final String methodName =  "onClick()";
        log.justEntered(methodName);

        TextView tvPhoneNo = view.findViewById(R.id.tv_phone_no);
        String phoneNo = tvPhoneNo.getText().toString();
        callback.onClicked(phoneNo);

        log.returning(methodName);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvNumber, tvId, tvAddressType;
        EditText etSearch;
        DisplayPictureView dpView;

        ContactViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tv_display_name);
            tvNumber = view.findViewById(R.id.tv_phone_no);
            tvId = view.findViewById(R.id.tv_contact_id);
            etSearch =view.findViewById(R.id.et_search_contact);
            tvAddressType = view.findViewById(R.id.tv_address_type);
            dpView = view.findViewById(R.id.dpv_picture);

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
        String needSearchString();
        void onEmptySearch();
    }
}
