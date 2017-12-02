package dev.sagar.smsblocker.ux.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class RVThreadOverviewAdapter extends RecyclerView.Adapter<RVThreadOverviewAdapter.SMSViewHolder> implements View.OnClickListener{
    private Context context;
    private Map<String, SMS> smsMap;
    private Callback callback;
    private List<String> threads;

    public RVThreadOverviewAdapter(Context context, Map<String, SMS> smsMap, Callback callback) {
        this.context = context;
        this.smsMap = smsMap;
        this.callback = callback;

        threads = new ArrayList<>();
    }

    @Override
    public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rv_thread_overview, parent, false);
        itemView.setOnClickListener(this);
        return new SMSViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {

        /* This part need to change later it is a performance issue though solved Bug #30*/
        //Section start
        Set<String> keys = smsMap.keySet();
        threads.clear();
        threads.addAll(keys);
        //Section End

        String thread = threads.get(position);
        SMS sms = smsMap.get(thread);
        String fromNumber = sms.getFrom();
        String fromName = ContactUtilSingleton.getInstance().getContactName(context, fromNumber);

        if(sms.isRead()) {
            holder.tvFrom.setTypeface(null, Typeface.NORMAL);
            holder.tvBody.setTypeface(null, Typeface.NORMAL);
            holder.tvTime.setTypeface(null, Typeface.NORMAL);
        }
        else{
            holder.tvFrom.setTypeface(null, Typeface.BOLD);
            holder.tvBody.setTypeface(null, Typeface.BOLD);
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
        Uri dpUri = ContactUtilSingleton.getInstance().getPictureUri(context, sms.getFrom());
        if(dpUri != null) {
            holder.ivDP.setVisibility(View.VISIBLE);
            holder.ivDP.setImageURI(dpUri);
        }
        else {
            //holder.ivDP.setImageResource(R.drawable.male);
            holder.ivDP.setVisibility(View.GONE);
            holder.ivDP.setImageURI(null);
            if(!fromName.equals(fromNumber)) {
                String c = String.valueOf(fromName.charAt(0));
                holder.tvIcon.setText(c);
            }
            else{
                holder.tvIcon.setText(R.string.hash);
            }
        }
    }

    @Override
    public int getItemCount() {
        return smsMap.size();
    }

    @Override
    public void onClick(View view) {
        TextView tvThreadID = view.findViewById(R.id.tv_thread_id);
        String threadId = tvThreadID.getText().toString();

        callback.onItemClicked(threadId);
    }


    public interface Callback{
        void onItemClicked(String threadId);
    }


    protected class SMSViewHolder extends RecyclerView.ViewHolder {
        TextView tvFrom, tvBody, tvTime, tvThreadId, tvIcon;
        ImageView ivDP;

        SMSViewHolder(View view) {
            super(view);
            tvFrom = view.findViewById(R.id.tv_from);
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            tvIcon = view.findViewById(R.id.tv_icon);
            tvThreadId = view.findViewById(R.id.tv_thread_id);
            ivDP = view.findViewById(R.id.iv_dp);
        }
    }
}
