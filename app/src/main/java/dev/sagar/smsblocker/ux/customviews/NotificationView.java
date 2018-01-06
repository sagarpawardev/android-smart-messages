package dev.sagar.smsblocker.ux.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 14/12/17.
 * Notification Default Action in OnClickListener is Hiding Visibility of NotifcationView.
 * However you can override it simply calling setOnCloseBtnClickListener().
 */

public class NotificationView extends RelativeLayout implements View.OnClickListener{
    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private TextView tvTitle;
    private TextView tvDesc;
    private ImageButton closeBtn;

    //Constants
    private static String DEFAULT_TEXT_COLOR = "#000000";
    private static String DEFAULT_COLOR = "#FFFFFF";


    public NotificationView(Context context) {
        super(context);
        initializeViews(context, null, 0, 0);
    }

    public NotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs, 0,0 );
    }

    public NotificationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context, attrs, defStyleAttr, 0);
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final String methodName = "initializeViews()";
        log.justEntered(methodName);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.compoundview_notification, this);

        tvTitle = view.findViewById(R.id.tv_title);
        tvDesc = view.findViewById(R.id.tv_desc);
        closeBtn = view.findViewById(R.id.imgbtn_close);
        if(attrs!=null){
            log.debug(methodName, "Applying Attributes");

            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.NotificationView, defStyleAttr, defStyleRes);

            int defaultTextColor = Color.parseColor(DEFAULT_TEXT_COLOR);
            int defaultColor = Color.parseColor(DEFAULT_COLOR);
            String titleText = typedArray.getString(R.styleable.NotificationView_titleText);
            int titleColor = typedArray.getColor(R.styleable.NotificationView_titleColor, defaultTextColor);
            String descText = typedArray.getString(R.styleable.NotificationView_descText);
            int descColor = typedArray.getColor(R.styleable.NotificationView_descColor, defaultTextColor);
            int closeButtonColor = typedArray.getColor(R.styleable.NotificationView_closeBtnColor, defaultColor);
            Drawable closeBtnBackground = typedArray.getDrawable(R.styleable.NotificationView_closeBtnBackground);

            setTitleText(titleText);
            setTitleColor(titleColor);
            setDescText(descText);
            setDescColor(descColor);
            setCloseBtnColor(closeButtonColor);
            setCloseBtnBackground(closeBtnBackground);

            closeBtn.setOnClickListener(this);

            typedArray.recycle();
        }

        log.returning(methodName);
    }


    //---- Public Methods Start ----
    public void setTitleText(String txt){
        final String methodName = "setTitleText(String)";
        log.justEntered(methodName);

        tvTitle.setText(txt);

        log.returning(methodName);
    }

    public void setTitleText(int txtId){
        final String methodName = "setTitleText(int)";
        log.justEntered(methodName);

        String txt = getContext().getString(txtId);
        setTitleText(txt);

        log.returning(methodName);
    }

    public void setDescText(String txt){
        final String methodName = "setDescText(String)";
        log.justEntered(methodName);

        tvDesc.setText(txt);

        log.returning(methodName);
    }

    public void setDescText(int txtId){
        final String methodName = "setDescText(int)";
        log.justEntered(methodName);

        String txt = getContext().getString(txtId);
        setDescText(txt);

        log.returning(methodName);
    }

    public void setTitleColor(int colorId){
        final String methodName = "setTitleColor()";
        log.justEntered(methodName);

        tvTitle.setTextColor(colorId);

        log.returning(methodName);
    }

    public void setDescColor(int colorId){
        final String methodName = "setDescColor()";
        log.justEntered(methodName);

        tvDesc.setTextColor(colorId);

        log.returning(methodName);
    }

    public void setCloseBtnColor(int colorId){
        final String methodName = "setCloseBtnColor()";
        log.justEntered(methodName);

        closeBtn.setColorFilter(colorId);

        log.returning(methodName);
    }


    public void setCloseBtnBackground(Drawable drawable){
        final String methodName = "setCloseBtnBackground()";
        log.justEntered(methodName);

        closeBtn.setBackground(drawable);

        log.returning(methodName);
    }

    public void setCloseBtnBackgroundColor(int colorId){
        final String methodName = "setCloseBtnBackgroundColor()";
        log.justEntered(methodName);

        closeBtn.setBackgroundColor(colorId);

        log.returning(methodName);
    }

    public void setCloseBtnBackgroundResource(int resId){
        final String methodName = "setCloseBtnBackgroundResource()";
        log.justEntered(methodName);

        closeBtn.setBackgroundResource(resId);

        log.returning(methodName);
    }

    public void setOnCloseClickListener(OnClickListener listener){
        final String methodName = "setOnCloseClickListener()";
        log.justEntered(methodName);

        closeBtn.setOnClickListener(listener);

        log.returning(methodName);
    }

    public void close(){
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.notification_closebtn__default);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
                clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);
    }

    //--- View.OnClickListener Overrides Start ----
    @Override
    public void onClick(View view) {
        close();
    }
    //--- View.OnClickListener Overrides End ----

    /*@Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        tvDesc.setOnClickListener(listener);
        tvTitle.setOnClickListener(listener);
    }*/

    //---- Public Methods End ----
}
