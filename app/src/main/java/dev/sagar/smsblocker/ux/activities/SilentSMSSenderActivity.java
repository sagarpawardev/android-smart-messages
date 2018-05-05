package dev.sagar.smsblocker.ux.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.AnalyticsUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.SMSUtil;
import io.fabric.sdk.android.Fabric;

public class SilentSMSSenderActivity extends AppCompatActivity {

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Constants
    public String KEY_ADDRESS = "address";
    public String KEY_MSG = "message";

    //Java Core
    private String gAddress = null;
    private String gMessage = null;
    private SMSUtil gSMSUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String methodName = "onCreate()";
        log.justEntered(methodName);

        super.onCreate(savedInstanceState);
        AnalyticsUtil.start(this);
        init();
        getData();
        process();

        log.returning(methodName);
    }

    private void getData(){
        final String methodName = "getData()";
        log.justEntered(methodName);

        Bundle basket = getIntent().getExtras();
        gAddress = basket.getString(KEY_ADDRESS);
        gMessage = basket.getString(KEY_MSG);

        log.returning(methodName);
    }

    private void init(){
        final String methodName = "init()";
        log.justEntered(methodName);

        gSMSUtil = new SMSUtil(this);

        log.returning(methodName);
    }

    private void process(){
        final String methodName = "process()";
        log.justEntered(methodName);

        SMS sms = gSMSUtil.sendSMS(gAddress, gMessage);
        Toast.makeText(this, "SMS Sent", Toast.LENGTH_SHORT).show();

        log.returning(methodName);
    }
}
