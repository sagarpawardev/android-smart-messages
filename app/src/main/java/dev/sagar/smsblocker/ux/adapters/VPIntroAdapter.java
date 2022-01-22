package dev.sagar.smsblocker.ux.adapters;

/**
 * Created by sagarpawar on 31/03/18.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;

public class VPIntroAdapter extends PagerAdapter {

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    private Activity mActivity;
    private static final int NUM_PAGES = 3;

    private static final int PAGE_APP_DEFAULT = 0;
    private static final int PAGE_SMS_PERMISSION = 1;
    private static final int PAGE_CONTACT_PERMISSION = 2;
    private PermissionUtilSingleton permUtil = PermissionUtilSingleton.getInstance();

    public VPIntroAdapter(Activity context) {
        mActivity = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        final String methodName = "instantiateItem()";
        log.justEntered(methodName);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.frag_permissions, collection, false);

        switch (position){
            case PAGE_APP_DEFAULT: setDefaultAppLayout(layout); break;
            case PAGE_SMS_PERMISSION: setSMSPermissionView(layout); break;
            case PAGE_CONTACT_PERMISSION: setContactPermissionView(layout); break;
            default: log.error(methodName, "Invalid position for view pager(position): "+position);
        }

        collection.addView(layout);
        return layout;
    }


    public void setDefaultAppLayout(ViewGroup layout){
        TextView tvTitle = layout.findViewById(R.id.tv_title);
        TextView tvBody = layout.findViewById(R.id.tv_body);

        String text = mActivity.getString(R.string.title_app_default);
        tvTitle.setText(text);
        text = mActivity.getString(R.string.body_app_default);
        tvBody.setText(text);

        Button btnDefault = layout.findViewById(R.id.btn_default);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permUtil.askToMakeAppDefault(mActivity);
            }
        });
    }

    public void setSMSPermissionView(ViewGroup layout){
        TextView tvTitle = layout.findViewById(R.id.tv_title);
        TextView tvBody = layout.findViewById(R.id.tv_body);

        String text = mActivity.getString(R.string.title_sms_perm);
        tvTitle.setText(text);
        text = mActivity.getString(R.string.body_sms_perm);
        tvBody.setText(text);

        Button btnDefault = layout.findViewById(R.id.btn_default);
        btnDefault.setText(R.string.lbl_grant);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permUtil.ask(mActivity, Permission.READ_SMS, PermissionUtilSingleton.RESULT_CODE_SMS_PERMISSION);
            }
        });
    }

    public void setContactPermissionView(ViewGroup layout){
        TextView tvTitle = layout.findViewById(R.id.tv_title);
        TextView tvBody = layout.findViewById(R.id.tv_body);

        String text = mActivity.getString(R.string.title_contact_perm);
        tvTitle.setText(text);
        text = mActivity.getString(R.string.body_contact_perm);
        tvBody.setText(text);

        Button btnDefault = layout.findViewById(R.id.btn_default);
        btnDefault.setText(R.string.lbl_grant);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permUtil.ask(mActivity, Permission.READ_CONTACTS, PermissionUtilSingleton.RESULT_CODE_CONTACT_PERMISSION);
            }
        });
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
