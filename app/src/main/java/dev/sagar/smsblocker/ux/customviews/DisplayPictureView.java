package dev.sagar.smsblocker.ux.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 15/12/17.
 */

public class DisplayPictureView extends RelativeLayout{
    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private ImageView circularIv;
    private TextView tvLetter;
    private View tvHolder;

    //Constant
    private static final String DEFAULT_TEXT_COLOR = "#FFFFFF";

    public DisplayPictureView(Context context) {
        super(context);
        initializeViews(context, null, 0, 0);
    }

    public DisplayPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs, 0,0);
    }

    public DisplayPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context, attrs, defStyleAttr, 0);
    }

    public DisplayPictureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context, attrs, defStyleAttr, defStyleRes);
    }


    private void initializeViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        final String methodName = "initializeViews()";
        log.justEntered(methodName);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.compoundview_displaypicture, this);

        circularIv = view.findViewById(R.id.iv_dp);
        tvLetter = view.findViewById(R.id.tv_letter);
        tvHolder = findViewById(R.id.tv_holder);

        if(attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.DisplayPictureView, defStyleAttr, defStyleRes);

            String letterText = typedArray.getString(R.styleable.DisplayPictureView_letterText);
            Drawable letterBackground = typedArray.getDrawable(R.styleable.DisplayPictureView_letterBackground);
            Drawable dpSrc = typedArray.getDrawable(R.styleable.DisplayPictureView_pictureSrc);
            int defaultColor = Color.parseColor(DEFAULT_TEXT_COLOR);
            int letterColor = typedArray.getColor(R.styleable.DisplayPictureView_letterColor, defaultColor);

            if(dpSrc!=null){
                log.debug(methodName, "Setting Picture");
                setPictureSrc(dpSrc);
            }
            else{
                log.debug(methodName, "Setting Text");
                setLetterText(letterText);
                setLetterBackground(letterBackground);
                setLetterColor(letterColor);
            }
            typedArray.recycle();
        }

        log.returning(methodName);
    }

    //--- Public Method Starts ---
    public void setPictureSrc(Drawable drawable){
        circularIv.setVisibility(VISIBLE);
        circularIv.setImageDrawable(drawable);
        tvHolder.setVisibility(GONE);

    }

    public void setPictureSrc(Uri uri){

        circularIv.setVisibility(VISIBLE);
        circularIv.setImageURI(uri);
        tvHolder.setVisibility(GONE);

    }

    public void setLetterText(String text){
        tvHolder.setVisibility(VISIBLE);
        tvLetter.setText(text);
        circularIv.setVisibility(GONE);
    }

    public void setLetterBackground(Drawable drawable){
        tvLetter.setBackground(drawable);
    }

    public void setLetterColor(int resId){
        tvLetter.setTextColor(resId);
    }
    //--- Public Method Ends ---

    //--- RelativeLayout Overrides Start----
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        circularIv = findViewById(R.id.iv_dp);
        tvLetter = findViewById(R.id.tv_letter);
        tvHolder = findViewById(R.id.tv_holder);

    }
    //--- RelativeLayout Overrides Ends ----
}
