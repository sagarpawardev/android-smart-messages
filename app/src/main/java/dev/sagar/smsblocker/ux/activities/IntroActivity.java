package dev.sagar.smsblocker.ux.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dev.sagar.smsblocker.Permission;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.ux.adapters.VPIntroAdapter;
import dev.sagar.smsblocker.ux.dialog.IntroDialog;

public class IntroActivity extends AppCompatActivity {

    private Button btnDefault;
    private PermissionUtilSingleton permUtil = PermissionUtilSingleton.getInstance();
    private ViewPager pager;
    private PagerAdapter mPagerAdapter;
    private Button btnNext, btnSkip;

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

        btnNext = (Button) findViewById(R.id.btn_next);
        //btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNext();
            }
        });
        /*btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNext();
            }
        });*/

    }

    public void moveToNext(){
        int next = pager.getCurrentItem()+1;

        if(pager.getChildCount() < next){
            showDialog();
        }
        else{
            pager.setCurrentItem(next);
        }
    }

    public void showDialog(){
        boolean hasContactPermission = permUtil.hasPermission(this, Permission.READ_CONTACTS);
        boolean hasSMSPermission = permUtil.hasPermission(this, Permission.READ_SMS);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Nothing Initiated";

        if(!hasContactPermission && !hasSMSPermission){
            message = getResources().getString(R.string.txt_perm_sms_contact);
        }else if(!hasContactPermission){
            message = getResources().getString(R.string.txt_perm_contact);
        }else if(!hasSMSPermission){
            message = getResources().getString(R.string.txt_perm_sms);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
        } else {
            builder.setMessage(Html.fromHtml(message));
        }

        String lblGrant = getResources().getString(R.string.lbl_grant);
        String lblDeny = getResources().getString(R.string.lbl_deny);
        builder.setPositiveButton(lblGrant, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                permUtil.ask(IntroActivity.this, Permission.ALL, PermissionUtilSingleton.REQUEST_CODE_ALL_PERMISSION);
            }
        });

        builder.setNegativeButton(lblDeny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startHomeActivity();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                    Toast.makeText(this, "Permission Not givev :(", Toast.LENGTH_SHORT).show();
                }
                break;

            case PermissionUtilSingleton.RESULT_CODE_SMS_PERMISSION:
                    boolean hasPermission = permUtil.hasPermission(this, Permission.READ_SMS);

                    if(hasPermission){
                        moveToNext();
                    }
                    else{
                        Toast.makeText(this, "Permission Not givev :(", Toast.LENGTH_SHORT).show();
                    }

                    break;

            case PermissionUtilSingleton.REQUEST_CODE_ALL_PERMISSION:
                boolean hasContactPermission = permUtil.hasPermission(this, Permission.READ_CONTACTS);
                boolean hasSMSPermission = permUtil.hasPermission(this, Permission.READ_SMS);

                if(hasContactPermission && hasSMSPermission){
                    startHomeActivity();
                    Toast.makeText(this, "Thanks!!", Toast.LENGTH_SHORT).show();
                }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //--- AppCompatActivity Overrides Ends ---
}
