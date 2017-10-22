package dev.sagar.smsblocker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.beans.SMS;

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

        return new RVThreadAdapter.SMSViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {
        SMS sms = smses.get(position);
        String body = sms.getBody();
        long time = sms.getDateTime();
        String type = sms.getType() == SMS.TYPE_SENT ? "Sent" : "Received";
        boolean isRead = sms.isRead();

        holder.tvBody.setText(body);
        holder.tvTime.setText(time+"");
        holder.tvType.setText(type);
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

        SMSViewHolder(View view) {
            super(view);
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            tvType = view.findViewById(R.id.tv_type);
            tvRead = view.findViewById(R.id.tv_read);
        }
    }
}
