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

public class RVNewThreadAdapter_Contacts extends RecyclerView.Adapter<RVNewThreadAdapter_Contacts.ContactViewHolder> implements View.OnClickListener{

    private Context context;
    private ArrayList<Contact> contacts;
    private Callback callback;

    public RVNewThreadAdapter_Contacts(Context context, ArrayList<Contact> contacts, Callback callback) {
        this.context = context;
        this.contacts = contacts;
        this.callback = callback;
    }

    @Override
    public RVNewThreadAdapter_Contacts.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rv_new_thread__contacts, parent, false);

        itemView.setOnClickListener(this);
        return new RVNewThreadAdapter_Contacts.ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RVNewThreadAdapter_Contacts.ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        String displayName = contact.getDisplayName();
        String phoneNo = contact.getNumber();
        String id = contact.getId();

        holder.tvId.setText(id);
        holder.tvName.setText(displayName);
        holder.tvNumber.setText(phoneNo);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
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

    public interface Callback{
        void onClicked(String threadId);
    }
}
