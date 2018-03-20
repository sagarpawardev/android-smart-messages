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

import java.util.List;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.beans.Conversation;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.exceptions.ReadContactPermissionException;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.customviews.DisplayPictureView;

/**
 * Created by sagarpawar on 20/03/18.
 */

public class RVStarredSMSAdapter extends RecyclerView.Adapter<RVStarredSMSAdapter.SMSViewHolder>  {

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;

    //Java Core
    private List<SMS> smses;
    private Callback callback;

    public RVStarredSMSAdapter(Context context, List<SMS> smses, Callback callback) {
        this.context = context;
        this.smses = smses;
        this.callback = callback;
    }

    @Override
    public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final String methodName =  "onCreateViewHolder(ViewGroup, int)";
        log.justEntered(methodName);

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rv_starred_sms, parent, false);
        //itemView.setOnClickListener(this);
        SMSViewHolder holder = new SMSViewHolder(itemView);

        log.returning(methodName);
        return holder;
    }

    @Override
    public void onBindViewHolder(SMSViewHolder holder, int position) {
        final String methodName =  "onBindViewHolder(SMSViewHolder, int)";
        log.justEntered(methodName);

        //Adi changes Start
        Typeface myFont = Typeface.createFromAsset(context.getAssets(),"fonts/VarelaRound-Regular.ttf");
        holder.tvBody.setTypeface(myFont);
        holder.tvFrom.setTypeface(myFont);
        holder.tvTime.setTypeface(myFont);
        //Adi changes End

        SMS sms = smses.get(position);
        String address = sms.getAddress();

        Contact contact = null;
        try {
            contact = ContactUtilSingleton.getInstance().getContact(context, sms.getAddress());
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        String displayName = contact.getDisplayName();

        if(position == 0){
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.bkg_threadoverview__top_rounded);
            holder.parent.setBackground(drawable);
        }
        else{
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.selector_threadoverview);
            holder.parent.setBackground(drawable);
        }

        if(displayName == null)
            displayName = address;

        long tm = sms.getDateTime();
        String socialDate = DateUtilSingleton.getInstance().socialFormat(tm);
        holder.tvTime.setText(socialDate);
        holder.tvFrom.setText(displayName);
        holder.tvBody.setText(sms.getBody());

        //Setting User Image
        Uri dpUri = contact.getPhotoThumbnail();

        if(dpUri != null) {
            holder.dpView.setPictureSrc(dpUri);
        }
        else {
            if(!displayName.equals(address)) {
                String c = String.valueOf(displayName.charAt(0));
                holder.dpView.setLetterText(c);
            }
            else{
                String str = context.getResources().getString(R.string.hash);
                holder.dpView.setLetterText(str);
            }
        }

        log.returning(methodName);
    }

    @Override
    public int getItemCount() {
        final String methodName =  "getItemCount()";
        log.justEntered(methodName);

        int count = smses.size();
        log.info(methodName, "Returning Item Count: "+count);

        log.returning(methodName);
        return count;
    }

    protected class SMSViewHolder extends RecyclerView.ViewHolder{
        TextView tvFrom, tvBody, tvTime, tvAddress;
        DisplayPictureView dpView;
        View parent;

        SMSViewHolder(View view) {
            super(view);
            tvFrom = view.findViewById(R.id.tv_display_name);
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_metadata);
            dpView = view.findViewById(R.id.dpv_picture);
            parent = view;

            dpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //FIXME On click gives error
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
        }
    }

    public interface Callback{

    }
}
