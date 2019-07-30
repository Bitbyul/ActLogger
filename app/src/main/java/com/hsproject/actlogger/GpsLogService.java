package com.hsproject.actlogger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GpsLogService extends Service {
    public GpsLogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
