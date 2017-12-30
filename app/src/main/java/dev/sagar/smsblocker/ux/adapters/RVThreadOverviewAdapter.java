package dev.sagar.smsblocker.ux.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.exceptions.ReadContactPermissionException;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.customviews.DisplayPictureView;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class RVThreadOverviewAdapter extends RecyclerView.Adapter<RVThreadOverviewAdapter.SMSViewHolder>
        implements View.OnClickListener{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;

    //Java Core
    private Map<String, SMS> smsMap;
    private Callback callback;
    private List<String> threads;
    private boolean isSelectionModeOn=false;
    private ArrayList<String> selectedThreads = new ArrayList<>(); //Better if Changed to Set
    private InboxUtil inboxUtil;

    public RVThreadOverviewAdapter(Context context, Map<String, SMS> smsMap, Callback callback) {
        this.context = context;
        this.smsMap = smsMap;
        this.callback = callback;

        threads = new ArrayList<>();
        inboxUtil = new InboxUtil(context);
    }

    public void setSelectionModeOn(boolean isModeOn){
        final String methodName =  "setSelectionModeOn()";
        log.debug(methodName, "Just Entered..");

        isSelectionModeOn = isModeOn;
        selectedThreads.clear();

        //This Line Clears items when Action Mode is destroyed
        if(!isModeOn) notifyDataSetChanged();

        log.debug(methodName, "Returning..");
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
                threads.remove(thread);
                smsMap.remove(thread);
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
                .inflate(R.layout.row_rv_threadoverview, parent, false);
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
        //Section start
        Set<String> keys = smsMap.keySet();
        threads.clear();
        threads.addAll(keys);
        //Section End

        //Adi changes Start
        Typeface myFont = Typeface.createFromAsset(context.getAssets(),"fonts/VarelaRound-Regular.ttf");
        holder.tvBody.setTypeface(myFont);
        holder.tvFrom.setTypeface(myFont);
        holder.tvTime.setTypeface(myFont);
        //Adi changes End

        String thread = threads.get(position);
        SMS sms = smsMap.get(thread);
        String fromNumber = sms.getFrom();


        String fromName = fromNumber;
        try {
            fromName = ContactUtilSingleton.getInstance().getContactName(context, fromNumber);
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }

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
        if(sms.isRead()) {
            holder.tvFrom.setTypeface(myFont, Typeface.NORMAL);
            holder.tvBody.setTypeface(myFont, Typeface.NORMAL);
            holder.tvTime.setTypeface(myFont, Typeface.NORMAL);
        }
        else{
            holder.tvFrom.setTypeface(myFont,Typeface.BOLD);
            holder.tvBody.setTypeface(myFont,Typeface.BOLD);
            //holder.tvTime.setTypeface(null, Typeface.BOLD);
        }

        if(fromName == null)
            fromName = fromNumber;

        long tm = sms.getDateTime();
        String socialDate = DateUtilSingleton.getInstance().socialFormat(tm);
        holder.tvTime.setText(socialDate);
        holder.tvFrom.setText(fromName);
        holder.tvBody.setText(sms.getBody());
        holder.tvThreadId.setText(sms.getFrom());

        //Setting User Image
        Uri dpUri = null;
        try {
            dpUri = ContactUtilSingleton.getInstance().getPictureUri(context, sms.getFrom());
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        if(dpUri != null) {
            holder.dpView.setPictureSrc(dpUri);
        }
        else {
            if(!fromName.equals(fromNumber)) {
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

        int size = smsMap.size();

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
        TextView tvFrom, tvBody, tvTime, tvThreadId;
        DisplayPictureView dpView;
        View parent;

        SMSViewHolder(View view) {
            super(view);
            tvFrom = view.findViewById(R.id.tv_from);
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            dpView = view.findViewById(R.id.dpv_picture);
            tvThreadId = view.findViewById(R.id.tv_thread_id);
            parent = view;

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
                String thread = threads.get(position);
                selectedThreads.add(thread);
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
