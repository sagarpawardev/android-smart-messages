package dev.sagar.smsblocker.test.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SIM;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.tech.utils.TelephonyUtilSingleton;

public class TestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void showOperator(){
        String operator = TelephonyUtilSingleton.getInstance().getCurrentOperator(this);
        Toast.makeText(this, "Operator: "+operator, Toast.LENGTH_SHORT).show();

        TelephonyUtilSingleton teleUtil = TelephonyUtilSingleton.getInstance();
        List<SIM> sims = teleUtil.getAvailableSims(this );
        for(SIM sim:sims){
            Log.e("MY tag", "Operator Name: "+sim.getOperator());
            Log.e("MY tag", "SIM id: "+sim.getSubscriptionId());
            Log.e("MY tag", "SIM slot: "+sim.getSlotNo());
            Log.e("MY tag", "******************");

        }

        String defaultSim = teleUtil.getCurrentOperator(this);
        Log.e("My Tag", "Default SIM: "+defaultSim);
    }

    @Override
    protected void onResume() {
        //makeAppDefault();
        showOperator();
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
