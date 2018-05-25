package dev.sagar.smsblocker.ux.adapters;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SIM;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.beans.TranslateResponse;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.handlers.httprequest.translate.TranslateService;
import dev.sagar.smsblocker.tech.utils.DateUtilSingleton;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.SystemUtilSingleton;
import dev.sagar.smsblocker.tech.utils.TelephonyUtilSingleton;
import dev.sagar.smsblocker.ux.dialog.TranslateDialog;
import dev.sagar.smsblocker.ux.utils.ThemeUtil;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class RVChatAdapter extends RecyclerView.Adapter<RVChatAdapter.SMSViewHolder> {

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;
    private Callback callback;
    private InboxUtil inboxUtil;

    //Java Core
    private IndexedHashMap<String, SMS> smses;
    private boolean isSelectionModeOn = false;
    private List<SMS> selectedSMSes = new ArrayList<>();
    private TelephonyUtilSingleton telephonyUtil;
    private boolean replyNotSupportedTold = false;
    private Set<Integer> markedForUnstar;
    private ThemeUtil themeUtil;

    //Constants
    private static final int TYPE_RECEIVED = 0;
    private static final int TYPE_SENT = 1;
    private static final int HIGHLIGHT_DURATION = 2000;

    //Flag
    private int highlightPosition = -1;

    public RVChatAdapter(Context context, Callback callback, IndexedHashMap<String, SMS> smses) {
        final String methodName =  "RVChatAdapter()";
        log.justEntered(methodName);

        this.context = context;
        this.callback = callback;
        this.smses = smses;
        inboxUtil = new InboxUtil(context);
        telephonyUtil = TelephonyUtilSingleton.getInstance();
        markedForUnstar = new HashSet<>();
        themeUtil = new ThemeUtil(context);

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

            String id = sms.getId();
            smses.remove(id);
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

    public boolean startSelections(){
        final String methodName =  "startSelections()";
        log.justEntered(methodName);

        log.error(methodName, "This can be improved");
        log.info(methodName, "Selected SMS count: "+ selectedSMSes.size());
        for (SMS sms: selectedSMSes) {
            log.debug(methodName, "Starring SMS id: "+sms.getId());

            //Starred SMS to database
            inboxUtil.starSMS(sms);
            sms.setSaved(true);

            //Starred SMS to UI
            int position = selectedSMSes.indexOf(sms);
            notifyItemChanged(position);
        }
        selectedSMSes.clear();

        log.returning(methodName);
        return true;
    }

    public boolean unstarSelections(){
        final String methodName =  "unstarSelections()";
        log.justEntered(methodName);

        List<SMS> unstarredSelections = new ArrayList<>();
        for(int position: markedForUnstar){
            SMS sms = smses.get(position);
            unstarredSelections.add(sms);
        }

        log.info(methodName, "Unstarring SMSes");
        int count = inboxUtil.unstarSMSes(unstarredSelections);
        log.info(methodName, "Requested count: "+unstarredSelections.size()+" Deleted count: "+count);
        markedForUnstar.clear();
        if(count>0)
            Toast.makeText(context, "SMS Unsaved", Toast.LENGTH_SHORT).show();

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

    public void highlightItem(int position){
        final String methodName =  "highlightItem(int)";
        log.justEntered(methodName);

        highlightPosition = position;
        notifyItemChanged(position);

        log.returning(methodName);
    }

    private void resetHighlight(){
        final String methodName =  "highlightItem(int)";
        log.justEntered(methodName);
        highlightPosition = -1;
        log.returning(methodName);
    }


    private void setStateImage(long type, ImageView ivState){
        Drawable drawable = null;
        Resources res = context.getResources();

        if(type == SMS.TYPE_SENT){
            drawable = res.getDrawable(R.drawable.ic_check_white_24dp, null);
        }
        else if(type == SMS.TYPE_FAILED){
            drawable = res.getDrawable(R.drawable.baseline_error_outline_24, null);
        }
        else if(type == SMS.TYPE_QUEUED){
            drawable = res.getDrawable(R.drawable.ic_schedule_white_24dp, null);
        }

        //If message is received type
        if(drawable!=null)
            ivState.setImageDrawable(drawable);
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
    public void onBindViewHolder(final SMSViewHolder holder, int position) {
        final String methodName =  "onBindViewHolder()";
        log.justEntered(methodName);

        //Adi changes Start
        Typeface myFont = themeUtil.getTypeface();
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

        //SetImage View
        setStateImage(type, holder.ivState);

        //Set Delivered Image
        boolean isSeen = sms.getSeen();
        if(holder.ivState==null && isSeen){
            log.debug(methodName, "Somethings weird...");
        }

        if(isSeen){
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_check_all_white_24dp, null);
            holder.ivState.setImageDrawable(drawable);
        }

        //Set Error Image
        if(type == SMS.TYPE_FAILED){
            holder.tvFailure.setVisibility(View.VISIBLE);
        }
        else if(type == SMS.TYPE_SENT){
            holder.tvFailure.setVisibility(View.GONE);
        }

        //If SMS is among saved SMS
        log.debug(methodName, "Checking Saved SMS: "+sms.isSaved()+" id: "+sms.getId());
        if(sms.isSaved()){
            holder.btnStar.setVisibility(View.VISIBLE);

            //If marked for Unstar
            if(markedForUnstar.contains(position)){
                holder.btnStar.setLiked(false);
            }
            else {
                holder.btnStar.setLiked(true);
            }
        }
        else{
            holder.btnStar.setVisibility(View.GONE);
        }

        //If SMS is selected in RecyclerView
        boolean isSelected = selectedSMSes.contains(sms);
        setViewSelected(holder, isSelected);

        holder.tvBody.setText(body);
        holder.tvTime.setText(socialDate);

        if(!replyNotSupportedTold && type==SMS.TYPE_RECEIVED && !isReplySupported){
            callback.onReplyNotSupported();
        }

        //Highlight Item if required
        if(highlightPosition ==position){
            int colorFrom = context.getResources().getColor(R.color.orangeA200, null);
            int colorTo = context.getResources().getColor(R.color.transparent_light_grey, null);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(HIGHLIGHT_DURATION); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    holder.llParent.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {
                    final String methodName =  "onAnimationStart()";
                    log.justEntered(methodName);
                    //Nothing here
                    log.returning(methodName);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    final String methodName =  "onAnimationEnd()";
                    log.justEntered(methodName);

                    log.info(methodName, "Resetting Highlight animation");
                    resetHighlight();
                    log.returning(methodName);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    final String methodName =  "onAnimationCancel()";
                    log.justEntered(methodName);
                    //Nothing here
                    log.returning(methodName);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    final String methodName =  "onAnimationRepeat()";
                    log.justEntered(methodName);
                    //Nothing here
                    log.returning(methodName);
                }
            });
            colorAnimation.start();
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


    class SMSViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
            View.OnClickListener,
            OnLikeListener,
            TranslateService.Callback{
        TextView tvBody, tvTime, tvTranslate;
        View llParent;
        LikeButton btnStar;
        //Log Initiate
        LogUtil log = new LogUtil(this.getClass().getName());
        String format = context.getResources().getString(R.string.format_thread__datetime);
        DateFormat dateFormat = new SimpleDateFormat(format);
        ImageView ivState;
        ProgressBar pbTranslate;
        TextView tvFailure;

        SMSViewHolder(View view) {
            super(view);
            final String methodName =  "SMSViewHolder()";
            log.justEntered(methodName);

            llParent = view;
            tvBody = view.findViewById(R.id.tv_body);
            tvTime = view.findViewById(R.id.tv_time);
            ivState = view.findViewById(R.id.iv_state);
            btnStar = view.findViewById(R.id.btn_star);
            tvTranslate = view.findViewById(R.id.tv_translate);
            pbTranslate = view.findViewById(R.id.pb_translate);
            tvFailure = view.findViewById(R.id.tv_failure);

            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
            btnStar.setOnLikeListener(this);

            if(tvTranslate!=null) {
                tvTranslate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        SMS sms = smses.get(pos);
                        String id = sms.getId();
                        String body = sms.getBody();
                        TranslateService service = new TranslateService(RVChatAdapter.this.context, SMSViewHolder.this);
                        service.execute(id, body);

                        tvTranslate.setVisibility(View.INVISIBLE);
                        pbTranslate.setVisibility(View.VISIBLE);
                    }
                });
            }

            if(tvFailure!=null){
                tvFailure.setTypeface(themeUtil.getTypeface());
            }

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
            final String methodName =  "onClick(View)";
            log.justEntered(methodName);

            int id = view.getId();
            switch (id){
                default: onHolderClick(view); //Kept switch() for extensibility
            }

            log.returning(methodName);
        }

        private void onHolderClick(View view){
            final String methodName =  "onHolderClick(View)";
            log.justEntered(methodName);

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

            log.returning(methodName);
        }

        @Override
        public void liked(LikeButton likeButton) {
            final String methodName =  "liked(LikeButton)";
            log.justEntered(methodName);

            int position = getAdapterPosition();
            markedForUnstar.remove(position);

            log.returning(methodName);
        }

        @Override
        public void unLiked(LikeButton likeButton) {
            final String methodName =  "unLiked(LikeButton)";
            log.justEntered(methodName);

            int position = getAdapterPosition();
            markedForUnstar.add(position);

            SMS sms = smses.get(position);
            log.debug(methodName, "marked Unstarred for sms id: "+sms.getId()+" Position: "+position);

            log.returning(methodName);
        }

        //---- TranslateService.Callback Overrides Starts ----
        @Override
        public void onTranslationSuccess(TranslateResponse response) {
            //Toast.makeText(context, translatedText, Toast.LENGTH_SHORT).show();
            String translatedText = response.getTransText();

            pbTranslate.setVisibility(View.GONE);
            tvTranslate.setVisibility(View.VISIBLE);
            new TranslateDialog(RVChatAdapter.this.context).show(response);
        }

        @Override
        public void onTranslationFailure(Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //---- TranslateService.Callback Overrides Ends ----
    }
}
