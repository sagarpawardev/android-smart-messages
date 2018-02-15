package dev.sagar.smsblocker.ux.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.beans.Conversation;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.exceptions.ReadContactPermissionException;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.customviews.DisplayPictureView;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class RVHomeAdapter extends RecyclerView.Adapter<RVHomeAdapter.SMSViewHolder>
        implements View.OnClickListener{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;

    //Java Core
    private IndexedHashMap<String, Conversation> conversationMap;
    private Callback callback;
    private boolean isSelectionModeOn=false;
    private ArrayList<String> selectedThreads = new ArrayList<>(); //Better if Changed to Set
    private InboxUtil inboxUtil;

    public RVHomeAdapter(Context context, IndexedHashMap<String, Conversation> conversationMap, Callback callback) {
        this.context = context;
        this.conversationMap = conversationMap;
        this.callback = callback;

        log.debug("Constructor", "Conversation Map count: "+ conversationMap.size());
        inboxUtil = new InboxUtil(context);
    }

    public void setSelectionModeOn(boolean isModeOn){
        final String methodName =  "setSelectionModeOn()";
        log.justEntered(methodName);

        isSelectionModeOn = isModeOn;
        selectedThreads.clear();

        //This Line Clears items when Action Mode is destroyed
        if(!isModeOn) notifyDataSetChanged();

        log.returning(methodName);
    }

    private void setViewSelected(View view, boolean selected){
        final String methodName =  "setViewSelected()";
        log.justEntered(methodName);

        if(selected) {
            view.setSelected(true);
        }
        else{
            view.setSelected(false);
        }

        log.returning(methodName);
    }

    public int deleteSelections(){
        final String methodName =  "deleteSelections()";
        log.justEntered(methodName);

        int count = 0;

        for (String thread: selectedThreads) {
            //Delete SMS from database
            int deleteCount = inboxUtil.deleteThread(thread);
            count += deleteCount;

            //Delete SMS from UI
            int position = selectedThreads.indexOf(thread);

            if(deleteCount>0) {
                notifyItemRemoved(position);
                conversationMap.remove(thread);
            }
            log.debug(methodName, "Deleted "+deleteCount+ " in this Thread but Total: "+count);
        }
        selectedThreads.clear();

        log.returning(methodName);
        return count;
    }

    //--- RecyclerView.Adapter Overrides Start ---
    @Override
    public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final String methodName =  "onCreateViewHolder()";
        log.debug(methodName, "Just Entered..");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rv_home, parent, false);
        itemView.setOnClickListener(this);
        SMSViewHolder holder = new SMSViewHolder(itemView);

        log.debug(methodName, "Returning..");
        return holder;
    }

    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {
        final String methodName =  "onBindViewHolder()";
        log.debug(methodName, "Just Entered..");

        /* This part need to change later it is a performance issue though solved Bug #30*/
        log.error(methodName, "This part Reduces performance. Need to change later");

        //Adi changes Start
        Typeface myFont = Typeface.createFromAsset(context.getAssets(),"fonts/VarelaRound-Regular.ttf");
        holder.tvBody.setTypeface(myFont);
        holder.tvFrom.setTypeface(myFont);
        holder.tvTime.setTypeface(myFont);
        //Adi changes End

        Conversation conversation = conversationMap.get(position);
        String address = conversation.getAddress();

        String fromName = conversation.getContactName();

        //If SMS is selected in Multiselect mode
        boolean isSelected = selectedThreads.contains(fromName);
        holder.parent.setSelected(isSelected);

        if(position == 0){
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.bkg_threadoverview__top_rounded);
            holder.parent.setBackground(drawable);
        }
        else{
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.selector_threadoverview);
            holder.parent.setBackground(drawable);
        }

        //If SMS is read
        if(conversation.isRead()) {
            holder.tvFrom.setTypeface(myFont, Typeface.NORMAL);
            holder.tvBody.setTypeface(myFont, Typeface.NORMAL);
            holder.tvTime.setTypeface(myFont, Typeface.NORMAL);
        }
        else{
            holder.tvFrom.setTypeface(myFont,Typeface.BOLD);
            //holder.tvTime.setTypeface(null, Typeface.BOLD);
        }

        if(fromName == null)
            fromName = address;

        long tm = conversation.getDateTime();
        String socialDate = DateUtilSingleton.getInstance().socialFormat(tm);
        holder.tvTime.setText(socialDate);
        holder.tvFrom.setText(fromName);
        holder.tvBody.setText(conversation.getBody());
        holder.tvAddress.setText(conversation.getAddress());

        //Setting User Image
        Uri dpUri = conversation.getPhotoThumbnailUri();

        if(dpUri != null) {
            holder.dpView.setPictureSrc(dpUri);
        }
        else {
            if(!fromName.equals(address)) {
                String c = String.valueOf(fromName.charAt(0));
                holder.dpView.setLetterText(c);
            }
            else{
                String str = context.getResources().getString(R.string.hash);
                holder.dpView.setLetterText(str);
            }
        }

        log.debug(methodName, "Returning..");
    }

    @Override
    public int getItemCount() {
        final String methodName =  "getItemCount()";
        log.debug(methodName, "Just Entered..");

        int size = conversationMap.size();
        log.debug(methodName, "List Size: "+size);

        log.debug(methodName, "Returning..");
        return size;
    }
    //--- RecyclerView.Adapter Overrides Ends ---


    //--- View.OnClickListener Overrides Start ---
    @Override
    public void onClick(View view) {
        final String methodName =  "onClick()";
        log.justEntered(methodName);

        TextView tvThreadID = view.findViewById(R.id.tv_thread_id);
        String threadId = tvThreadID.getText().toString();

        if(!isSelectionModeOn) {
            log.debug(methodName, "sending Callback..");
            callback.onItemClicked(threadId);
        }
        else{
            log.debug(methodName, "Selection Mode is on.");
            if(!selectedThreads.contains(threadId)){
                selectedThreads.add(threadId);
                setViewSelected(view, true);
                log.info(methodName, "Item Added in Selected List");
            }
            else{
                selectedThreads.remove(threadId);
                setViewSelected(view, false);
                log.info(methodName, "Item removed from Selected List");
            }

            switch (selectedThreads.size()){
                case 0: callback.onAllDeselected(); break;
                default: break;
            }
        }

        log.returning(methodName);
    }
    //--- View.OnClickListener Overrides End ---


    protected class SMSViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView tvFrom, tvBody, tvTime, tvAddress;
        DisplayPictureView dpView;
        View parent;

        SMSViewHolder(View view) {
            super(view);
            tvFrom = view.findViewById(R.id.tv_from);
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            dpView = view.findViewById(R.id.dpv_picture);
            tvAddress = view.findViewById(R.id.tv_thread_id);
            parent = view;

            dpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contactID = null;
                    try {
                        String threadId = tvAddress.getText().toString();
                        Contact contact = ContactUtilSingleton.getInstance().getContact(context, threadId);
                        contactID = contact.getId();
                    } catch (ReadContactPermissionException e) {
                        e.printStackTrace();
                    }

                    if(contactID!=null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                }
            });
            view.setOnLongClickListener(this);
        }

        //--- View.OnLongClickListener Overrides Start ---
        @Override
        public boolean onLongClick(View view) {
            final String methodName =  "onLongClick()";
            log.debug(methodName, "Just Entered..");

            if (!isSelectionModeOn) {
                callback.onItemLongClicked();

                //Add Long Pressed Item in Selected List
                int position = getAdapterPosition();
                Conversation tSms = conversationMap.get(position);
                selectedThreads.add(tSms.getAddress());
                setViewSelected(view, true);

                return true;
            }

            log.debug(methodName, "Returning..");
            return false;
        }
        //--- View.OnLongClickListener Overrides End ---
    }

    public interface Callback{
        void onItemClicked(String threadId);
        void onItemLongClicked();
        void onAllDeselected();
    }
}
