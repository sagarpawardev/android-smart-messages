package dev.sagar.smsblocker.ux.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SIM;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.SystemUtilSingleton;
import dev.sagar.smsblocker.tech.utils.TelephonyUtilSingleton;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class RVThreadAdapter extends RecyclerView.Adapter<RVThreadAdapter.SMSViewHolder>{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;
    private Callback callback;
    private InboxUtil inboxUtil;

    //Java Core
    private List<SMS> smses;
    private boolean isSelectionModeOn = false;
    private List<SMS> selectedSMSes = new ArrayList<>();
    private TelephonyUtilSingleton telephonyUtil;
    private boolean replyNotSupportedTold = false;

    //Constants
    private static final int TYPE_RECEIVED = 0;
    private static final int TYPE_SENT = 1;

    public RVThreadAdapter(Context context, Callback callback, List<SMS> smses) {
        final String methodName =  "RVThreadAdapter()";
        log.justEntered(methodName);

        this.context = context;
        this.callback = callback;
        this.smses = smses;
        inboxUtil = new InboxUtil(context);
        telephonyUtil = TelephonyUtilSingleton.getInstance();

        log.returning(methodName);
    }

    public void logSelectedSize(){
        log.debug("logSelectedSize()", "Current Size: "+selectedSMSes.size());
    }

    public boolean deleteSelections(){
        final String methodName =  "deleteSelections()";
        log.justEntered(methodName);

        log.error(methodName, "This can be improved");
        log.info(methodName, "Selected SMS count: "+ selectedSMSes.size());
        for (SMS sms: selectedSMSes) {
            log.debug(methodName, "Deleting SMS: "+sms.getBody());

            //Delete SMS from database
            inboxUtil.deleteSMS(sms);

            //Delete SMS from UI
            int position = selectedSMSes.indexOf(sms);
            smses.remove(sms);
            notifyItemRemoved(position);
        }
        selectedSMSes.clear();

        log.returning(methodName);
        return true;
    }


    public boolean copySelection(){
        final String methodName="copySelection()";
        log.justEntered(methodName);

        if(selectedSMSes.size() != 1){
            log.error(methodName, "Selected SMS are either greater or less than 1");
            return false;
        }

        SMS sms = selectedSMSes.get(0);
        SystemUtilSingleton systemUtil = SystemUtilSingleton.getInstance();
        systemUtil.copy(context, sms);

        log.returning(methodName);
        return true;
    }


    public void setSelectionModeOn(boolean isModeOn){
        final String methodName =  "setSelectionModeOn()";
        log.justEntered(methodName);

        isSelectionModeOn = isModeOn;
        selectedSMSes.clear();

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

    private void setViewSelected(SMSViewHolder holder, boolean selected){
        final String methodName =  "setViewSelected()";
        log.justEntered(methodName);

        setViewSelected(holder.llParent, selected);

        log.returning(methodName);
    }


    //--- RecyclerView.Adapter Overrides Start ---
    @Override
    public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final String methodName =  "onCreateViewHolder()";
        log.justEntered(methodName);

        View itemView = null;

        switch (viewType){
            case TYPE_SENT: itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_rv_thread__received, parent, false);
                    break;
            case TYPE_RECEIVED: itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_rv_thread__sent, parent, false);
                    break;
            default: break;
        }

        log.returning(methodName);
        return new SMSViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        SMS sms = smses.get(position);
        boolean smsType = (sms.getType()==SMS.TYPE_RECEIVED);

        return  smsType ? TYPE_SENT: TYPE_RECEIVED ;
    }

    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {
        final String methodName =  "onBindViewHolder()";
        log.justEntered(methodName);

        //Adi changes Start
        Typeface myFont = Typeface.createFromAsset(context.getAssets(),"fonts/VarelaRound-Regular.ttf");
        holder.tvBody.setTypeface(myFont);
        holder.tvTime.setTypeface(myFont);
        //Adi changes End

        SMS sms = smses.get(position);
        String body = sms.getBody();
        long time = sms.getDateTime();
        long type = sms.getType();
        boolean isRead = sms.isRead();
        String socialDate = DateUtilSingleton.getInstance().socialFormat(time);
        boolean isReplySupported = sms.isReplySupported();

        if(type == SMS.TYPE_QUEUED) {
            holder.tvSending.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvSending.setVisibility(View.GONE);
        }

        //If SMS is selected in RecyclerView
        boolean isSelected = selectedSMSes.contains(sms);
        setViewSelected(holder, isSelected);

        holder.tvBody.setText(body);
        holder.tvTime.setText(socialDate);

        if(!replyNotSupportedTold && !isReplySupported){
            callback.onReplyNotSupported();
        }

        log.returning(methodName);
    }

    @Override
    public int getItemCount() {
        final String methodName =  "getItemCount()";
        log.justEntered(methodName);

        log.returning(methodName);
        return smses.size();
    }
    //--- RecyclerView.Adapter Overrides End ---


    public interface Callback{
        void onItemLongClicked();
        void singleSelectionMode();
        void multiSelectionMode();
        void allDeselected();
        void onReplyNotSupported();
    }


    class SMSViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        TextView tvBody, tvTime, tvSending;
        View llParent;
        //Log Initiate
        LogUtil log = new LogUtil(this.getClass().getName());
        String format = context.getResources().getString(R.string.format_thread__datetime);
        DateFormat dateFormat = new SimpleDateFormat(format);

        SMSViewHolder(View view) {
            super(view);
            final String methodName =  "SMSViewHolder()";
            log.justEntered(methodName);

            llParent = view;
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            tvSending = view.findViewById(R.id.tv_sending);

            view.setOnLongClickListener(this);
            view.setOnClickListener(this);

            log.returning(methodName);
        }

        @Override
        public boolean onLongClick(View view) {
            final String methodName =  "onLongClick()";
            log.justEntered(methodName);
            boolean result = false;

            if (!isSelectionModeOn) {
                callback.onItemLongClicked();

                //Add Long Pressed Item in Selected List
                int position = getAdapterPosition();
                SMS sms = smses.get(position);
                selectedSMSes.add(sms);

                log.debug(methodName, "Size: "+selectedSMSes.size());
                setViewSelected(view, true);

                result = true;
            }

            log.returning(methodName);
            return result;
        }

        @Override
        public void onClick(View view) {
            final String methodName =  "onClick()";
            log.debug(methodName, "Just Entered..");

            //If Selector is in Selection Mode
            if (isSelectionModeOn) {
                log.info(methodName, "Clicked In Selection Mode");

                int position = getAdapterPosition();
                SMS sms = smses.get(position);
                if(!selectedSMSes.contains(sms)){
                    selectedSMSes.add(sms);
                    setViewSelected(view, true);
                    log.info(methodName, "Item Added in Selected List");
                }
                else{
                    selectedSMSes.remove(sms);
                    setViewSelected(view, false);
                    log.info(methodName, "Item removed from Selected List");
                }

                switch (selectedSMSes.size()){
                    case 0: callback.allDeselected(); break;
                    case 1: callback.singleSelectionMode(); break;
                    default: callback.multiSelectionMode(); break;
                }

            }
            else{
                log.info(methodName, "Clicked In Non-Selection Mode");
                int position = getAdapterPosition();
                SMS sms = smses.get(position);
                long dateTime = sms.getDateTime();
                Date date = new Date(dateTime);
                String strDateTime = dateFormat.format(date);

                int subscriptionId = sms.getSubscription();
                SIM sim = telephonyUtil.getSim(context, subscriptionId);
                String operatorName = sim.getOperator();

                StringBuilder sb = new StringBuilder();
                if(operatorName != null){
                    sb.append(operatorName+"  |  ");
                }
                sb.append(strDateTime);

                String formattedStr = sb.toString();
                tvTime.setText(formattedStr);
            }
        }
    }
}
