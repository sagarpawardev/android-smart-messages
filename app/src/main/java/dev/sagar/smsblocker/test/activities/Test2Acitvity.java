package dev.sagar.smsblocker.test.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.utils.NotificationUtilSingleton;
import dev.sagar.smsblocker.test.helpers.BundledNotificationHelper;

public class Test2Acitvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2_acitvity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BundledNotificationHelper(getApplicationContext()).generateBundle(getApplicationContext());
            }
        });
    }

}
