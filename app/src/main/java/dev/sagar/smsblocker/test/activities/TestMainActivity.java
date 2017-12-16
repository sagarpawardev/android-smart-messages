package dev.sagar.smsblocker.test.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;

public class TestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        //makeAppDefault();
        super.onResume();
    }


    public void makeAppDefault(){
        PermissionUtilSingleton permissions = PermissionUtilSingleton.getInstance();
        boolean isAppDefault = permissions.isAppDefault(this);

        if(isAppDefault)
            Toast.makeText(this, "App is Already Default", Toast.LENGTH_SHORT).show();
        else{
            Toast.makeText(this, "Not Default", Toast.LENGTH_SHORT).show();
            permissions.askToMakeAppDefault(this);
        }
    }
}
