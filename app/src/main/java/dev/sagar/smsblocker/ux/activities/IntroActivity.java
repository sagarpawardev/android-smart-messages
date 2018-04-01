package dev.sagar.smsblocker.ux.activities;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.ux.adapters.VPIntroAdapter;

public class IntroActivity extends AppCompatActivity {

    private Button btnDefault;
    private PermissionUtilSingleton permUtil = PermissionUtilSingleton.getInstance();
    private ViewPager pager;
    private PagerAdapter mPagerAdapter;
    private ImageButton ibNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        boolean appIsDefault = permUtil.isAppDefault(getApplicationContext());
        if(appIsDefault){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }

        /*btnDefault = (Button) findViewById(R.id.btn_default);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permUtil.askToMakeAppDefault(IntroActivity.this);
            }
        });*/

        pager = (ViewPager) findViewById(R.id.vp_holder);

        VPIntroAdapter cadapter = new VPIntroAdapter(this);
        pager.setAdapter(cadapter);

        ibNext = (ImageButton) findViewById(R.id.iv_next);
        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNext();
            }
        });

    }

    public void moveToNext(){
        int next = pager.getCurrentItem()+1;
        if(pager.getChildCount()>=next){
            pager.setCurrentItem(next);
        }
    }


    public void startHomeActivity(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //--- AppCompatActivity Overrides Starts ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PermissionUtilSingleton.RESULT_CODE_APP_DEFAULT) {
            boolean appIsDefault = permUtil.isAppDefault(getApplicationContext());
            if (appIsDefault) {
                startHomeActivity();
            } else {
                Toast.makeText(getApplicationContext(), "Not made default", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PermissionUtilSingleton.RESULT_CODE_CONTACT_PERMISSION:

                boolean hasReadSMSPermission = permUtil.hasPermission(this, Permission.READ_CONTACTS);

                if(hasReadSMSPermission){
                    startHomeActivity();
                }
                else{
                    Toast.makeText(this, "Permission Not give :(", Toast.LENGTH_SHORT).show();
                }
                break;

            case PermissionUtilSingleton.RESULT_CODE_SMS_PERMISSION:
                    boolean hasPermission = permUtil.hasPermission(this, Permission.READ_SMS);

                    if(hasPermission){
                        moveToNext();
                    }
                    else{
                        Toast.makeText(this, "Permission Not give :(", Toast.LENGTH_SHORT).show();
                    }

                    break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //--- AppCompatActivity Overrides Ends ---
}
