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
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import dev.sagar.smsblocker.ux.filterable.ConversationMapFilter;
import dev.sagar.smsblocker.ux.filterable.ConversationUnreadFilter;
import dev.sagar.smsblocker.ux.utils.ThemeUtil;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class RVHomeAdapter extends RecyclerView.Adapter<RVHomeAdapter.SMSViewHolder>
        implements View.OnClickListener,
        ConversationMapFilter.Callback, ConversationUnreadFilter.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;
    private ConversationMapFilter convFilter;
    private ConversationUnreadFilter convUnreadFilter;
    private Typeface myFont;

    //Java Core
    private IndexedHashMap<String, Conversation> conversationMap;
    private IndexedHashMap<String, Conversation> filteredConvMap;
    private Callback callback;
    private boolean isSelectionModeOn=false;
    private ArrayList<String> selectedConversations = new ArrayList<>(); //Better if Changed to Set
    private InboxUtil inboxUtil;
    private ThemeUtil themeUtil;

    //Constants
    public static final int FILTER_UNREAD = 0;
    public static final int FILTER_TEXT = 1;


    public RVHomeAdapter(Context context, IndexedHashMap<String, Conversation> conversationMap, Callback callback) {
        this.context = context;
        this.conversationMap = conversationMap;
        this.callback = callback;

        this.convFilter = new ConversationMapFilter(context, conversationMap, this);
        this.convUnreadFilter = new ConversationUnreadFilter(context, conversationMap, this);
        this.filteredConvMap = conversationMap;

        log.debug("Constructor", "Conversation Map count: "+ conversationMap.size());
        inboxUtil = new InboxUtil(context);
        themeUtil = new ThemeUtil(context);

        myFont = themeUtil.getTypeface();
    }

    public void setSelectionModeOn(boolean isModeOn){
        final String methodName =  "setSelectionModeOn()";
        log.justEntered(methodName);

        isSelectionModeOn = isModeOn;
        selectedConversations.clear();

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

        //TODO Change for loop to sql In Statement
        log.error(methodName, "This part reduces performance can be improved");
        for (String thread: selectedConversations) {
            //Delete SMS from database
            int deleteCount = inboxUtil.deleteThread(thread);
            count += deleteCount;

            //Delete SMS from UI
            int position = selectedConversations.indexOf(thread);

            if(deleteCount>0) {
                notifyItemRemoved(position);
                filteredConvMap.remove(thread);
                conversationMap.remove(thread);
            }
            log.debug(methodName, "Deleted "+deleteCount+ " in this Thread but Total: "+count);
        }
        selectedConversations.clear();

        log.returning(methodName);
        return count;
    }


    public void markSelectionsRead(){
        final String methodName =  "markSelectionsRead()";
        log.justEntered(methodName);

        log.info(methodName, "Marking SMS Read..");
        Set<String> set = new HashSet<>();
        for (String conversationId: selectedConversations) {
            set.add(conversationId);

            //Delete SMS from UI
            int position = selectedConversations.indexOf(conversationId);

            filteredConvMap.get(position).setReadState(true);
            conversationMap.get(position).setReadState(true);
        }
        inboxUtil.markSMSRead(set);
        selectedConversations.clear();
        notifyDataSetChanged();

        log.returning(methodName);
    }

    public Filter getFilter(int type){
        final String methodName =  "getFilter(int)";
        log.justEntered(methodName);

        Filter filter = null;
        switch (type){
            case FILTER_TEXT: filter = convFilter; break;
            case FILTER_UNREAD: filter = convUnreadFilter; break;
            default: log.error(methodName, "No such filter of type: "+type);
        }
        log.debug(methodName, "SGR Conversation Filter: "+convFilter.getClass().getName());

        log.returning(methodName);
        return filter;
    }

    //--- RecyclerView.Adapter Overrides Start ---
    @Override
    public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final String methodName =  "onCreateViewHolder()";
        log.justEntered(methodName);

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rv_home, parent, false);
        itemView.setOnClickListener(this);
        SMSViewHolder holder = new SMSViewHolder(itemView);

        log.returning(methodName);
        return holder;
    }

    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {
        final String methodName =  "onBindViewHolder()";
        log.justEntered(methodName);

        Conversation conversation = filteredConvMap.get(position);
        String address = conversation.getAddress();

        String fromName = conversation.getContactName();

        //If SMS is selected in Multiselect mode
        boolean isSelected = selectedConversations.contains(fromName);
        holder.parent.setSelected(isSelected);

        if(position == 0){
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.bkg_threadoverview__top_rounded);
            holder.parent.setBackground(drawable);
        }
        else{
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.selector_threadoverview);
            holder.parent.setBackground(drawable);
        }

        int unreadCount = conversation.getUnreadCount();
        log.debug(methodName, "Unread Count of Conversation: "+unreadCount+" for address: "+address);

        //If SMS is read
        if(unreadCount == 0) {
            holder.tvBadge.setVisibility(View.INVISIBLE);
        }
        else{
            holder.tvBadge.setVisibility(View.VISIBLE);
            String strUnreadCount = String.valueOf(unreadCount);
            holder.tvBadge.setText(strUnreadCount);
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

        //Style DisplayPictureView
        themeUtil.styleDPView(holder.dpView, dpUri, conversation);

        log.returning(methodName);
    }

    @Override
    public int getItemCount() {
        final String methodName =  "getItemCount()";
        log.justEntered(methodName);

        int size = filteredConvMap.size();
        log.debug(methodName, "List Size: "+size);

        log.returning(methodName);
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
            if(!selectedConversations.contains(threadId)){
                selectedConversations.add(threadId);
                setViewSelected(view, true);
                log.info(methodName, "Item Added in Selected List");
            }
            else{
                selectedConversations.remove(threadId);
                setViewSelected(view, false);
                log.info(methodName, "Item removed from Selected List");
            }

            switch (selectedConversations.size()){
                case 0: callback.onAllDeselected(); break;
                default: break;
            }
        }

        log.returning(methodName);
    }
    //--- View.OnClickListener Overrides End ---


    //--- Filterable Overrides Starts ---

    /*public Filter getFilter() {
        return convFilter;
    }*/
    //--- Filterable Overrides Ends ---


    //--- ConversationMapFilter.Callback Overrides Ends ---
    @Override
    public void onResultsFiltered(IndexedHashMap<String, Conversation> filteredConvMap) {
        this.filteredConvMap = filteredConvMap;
        notifyDataSetChanged();
        callback.onResultsFiltered();
    }
    //--- ConversationMapFilter.Callback Overrides Ends ---


    protected class SMSViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView tvFrom, tvBody, tvTime, tvAddress;
        DisplayPictureView dpView;
        View parent;
        TextView tvBadge;

        SMSViewHolder(View view) {
            super(view);
            tvFrom = view.findViewById(R.id.tv_from);
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            dpView = view.findViewById(R.id.dpv_picture);
            tvAddress = view.findViewById(R.id.tv_thread_id);
            tvBadge = view.findViewById(R.id.tv_badge);
            parent = view;

            //Set Font for TextViews
            tvBody.setTypeface(myFont);
            tvFrom.setTypeface(myFont);
            tvTime.setTypeface(myFont);

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
                Conversation tSms = filteredConvMap.get(position);
                selectedConversations.add(tSms.getAddress());
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
        void onResultsFiltered();
    }
}
