package dev.sagar.smsblocker.tech.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeadlessSMSService extends Service {
    public HeadlessSMSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
