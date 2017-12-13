package dev.sagar.smsblocker.ux.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 14/12/17.
 */

public class NotificationView extends RelativeLayout {
    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    TextView tvTitle;
    TextView tvDesc;
    ImageButton closeBtn;


    public NotificationView(Context context) {
        super(context);
        initializeViews(context);
    }

    public NotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public NotificationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compoundview_notification, this);
    }


    //---- Public Methods Start ----
    public void setTitleText(String txt){
        tvTitle.setText(txt);
    }

    public void setTitleText(int txtId){
        String txt = getContext().getString(txtId);
        setTitleText(txt);
    }

    public void setDescText(String txt){
        tvDesc.setText(txt);
    }

    public void setDescText(int txtId){
        String txt = getContext().getString(txtId);
        setDescText(txt);
    }

    public void setTitleColor(int colorId){
        tvTitle.setTextColor(colorId);
    }

    public void setDescColor(int colorId){
        tvDesc.setTextColor(colorId);
    }

    public void setCloseBtnColor(int colorId){
        closeBtn.setColorFilter(colorId);
    }


    public void setCloseBtnBackground(Drawable drawable){
        closeBtn.setBackground(drawable);
    }

    public void setCloseBtnBackgroundColor(int colorId){
        closeBtn.setBackgroundColor(colorId);
    }

    public void setCloseBtnBackgroundResource(int resId){
        closeBtn.setBackgroundResource(resId);
    }

    public void setOnCloseClickListener(OnClickListener listener){
        closeBtn.setOnClickListener(listener);
    }

    /*@Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        tvDesc.setOnClickListener(listener);
        tvTitle.setOnClickListener(listener);
    }*/

    //---- Public Methods End ----

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvTitle = findViewById(R.id.tv_title);
        tvDesc = findViewById(R.id.tv_desc);
        closeBtn = findViewById(R.id.imgbtn_close);


    }
}
