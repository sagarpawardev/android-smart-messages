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

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class RVThreadAdapter extends RecyclerView.Adapter<RVThreadAdapter.SMSViewHolder> {

    private Context context;
    private Callback callback;
    private ArrayList<SMS> smses;

    public RVThreadAdapter(Context context, Callback callback, ArrayList<SMS> smses) {
        this.context = context;
        this.callback = callback;
        this.smses = smses;
    }

    @Override
    public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rv_thread, parent, false);

        return new SMSViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {
        SMS sms = smses.get(position);
        String body = sms.getBody();
        long time = sms.getDateTime();
        long type = sms.getType();
        boolean isRead = sms.isRead();
        String socialDate = DateUtilSingleton.getInstance().socialFormat(time);

        if(type == SMS.TYPE_SENT) {
            holder.lvParent.setGravity(Gravity.END);
            holder.tvBody.setBackgroundResource(R.drawable.sender);

            holder.tvType.setBackgroundResource(R.drawable.sender);
            holder.tvRead.setBackgroundResource(R.drawable.sender);
        }
        else {
            holder.lvParent.setGravity(Gravity.START);
            holder.tvBody.setBackgroundResource(R.drawable.reciever);

            holder.tvType.setBackgroundResource(R.drawable.reciever);
            holder.tvRead.setBackgroundResource(R.drawable.reciever);
        }


        holder.tvBody.setText(body);
        holder.tvTime.setText(socialDate);
        holder.tvType.setText(type+"");
        holder.tvRead.setText("Read: "+isRead+"");
    }

    @Override
    public int getItemCount() {
        return smses.size();
    }

    public interface Callback{

    }

    class SMSViewHolder extends RecyclerView.ViewHolder {
        TextView tvBody, tvTime, tvType, tvRead;
        LinearLayout lvParent;

        SMSViewHolder(View view) {
            super(view);
            lvParent = (LinearLayout)view;
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            tvType = view.findViewById(R.id.tv_type);
            tvRead = view.findViewById(R.id.tv_read);
        }
    }
}
