package dev.sagar.smsblocker.ux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;

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
    private ArrayList<SMS> smses;
    private ArrayList<SMS> selectedSMS = new ArrayList<>();
    private boolean isSelectionModeOn = false;
    private List<SMS> selectedSMSes = new ArrayList<>();

    public RVThreadAdapter(Context context, Callback callback, ArrayList<SMS> smses) {
        final String methodName =  "RVThreadAdapter()";
        log.debug(methodName, "Just Entered..");

        this.context = context;
        this.callback = callback;
        this.smses = smses;
        inboxUtil = new InboxUtil(context);

        log.debug(methodName, "Returning..");
    }


    public boolean deleteSelections(){
        final String methodName =  "deleteSelections()";
        log.debug(methodName, "Just Entered..");

        for (SMS sms: selectedSMS) {
            //Delete SMS from database
            inboxUtil.deleteSMS(sms);

            //Delete SMS from UI
            int position = selectedSMS.indexOf(sms);
            smses.remove(sms);
            notifyItemRemoved(position);
        }
        selectedSMS.clear();

        log.debug(methodName, "Returning..");
        return true;
    }


    public void setSelectionModeOn(boolean isModeOn){
        final String methodName =  "setSelectionModeOn()";
        log.debug(methodName, "Just Entered..");

        isSelectionModeOn = isModeOn;
        selectedSMS.clear();

        log.debug(methodName, "Returning..");
    }


    //--- RecyclerView.Adapter Overrides Start ---
    @Override
    public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final String methodName =  "onCreateViewHolder()";
        log.debug(methodName, "Just Entered..");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rv_thread, parent, false);

        log.debug(methodName, "Returning..");
        return new SMSViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {
        final String methodName =  "onBindViewHolder()";
        log.debug(methodName, "Just Entered..");

        SMS sms = smses.get(position);
        String body = sms.getBody();
        long time = sms.getDateTime();
        long type = sms.getType();
        boolean isRead = sms.isRead();
        String socialDate = DateUtilSingleton.getInstance().socialFormat(time);

        if(type == SMS.TYPE_SENT) {
            holder.lvParent.setGravity(Gravity.END);
            holder.tvBody.setBackgroundResource(R.drawable.sender);
        }
        else {
            holder.lvParent.setGravity(Gravity.START);
            holder.tvBody.setBackgroundResource(R.drawable.reciever);
        }

        holder.tvBody.setText(body);
        holder.tvTime.setText(socialDate);

        log.debug(methodName, "Returning..");
    }

    @Override
    public int getItemCount() {
        final String methodName =  "getItemCount()";
        log.debug(methodName, "Just Entered..");

        log.debug(methodName, "Returning..");
        return smses.size();
    }
    //--- RecyclerView.Adapter Overrides End ---


    public interface Callback{
        void onItemLongClicked();
    }


    class SMSViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        TextView tvBody, tvTime;
        LinearLayout lvParent;
        //Log Initiate
        LogUtil log = new LogUtil(this.getClass().getName());

        SMSViewHolder(View view) {
            super(view);
            final String methodName =  "SMSViewHolder()";
            log.debug(methodName, "Just Entered..");

            lvParent = (LinearLayout)view;
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);

            view.setOnLongClickListener(this);
            view.setOnClickListener(this);

            log.debug(methodName, "Returning..");
        }

        @Override
        public boolean onLongClick(View view) {
            final String methodName =  "onLongClick()";
            log.debug(methodName, "Just Entered..");

            if (!isSelectionModeOn) {
                callback.onItemLongClicked();

                //Add Long Pressed Item in Selected List
                int position = getAdapterPosition();
                SMS sms = smses.get(position);
                selectedSMS.add(sms);

                return true;
            }

            log.debug(methodName, "Returning..");
            return false;
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
                if(!selectedSMS.contains(sms)){
                    selectedSMS.add(sms);
                    log.info(methodName, "Item Added in Selected List");
                }
                else{
                    selectedSMS.remove(sms);
                    log.info(methodName, "Item removed from Selected List");
                }

            }
            else{
                log.info(methodName, "Clicked In Non-Selection Mode");
            }
        }
    }
}
