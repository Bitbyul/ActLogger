package com.hsproject.actlogger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import android.widget.Toast;


public class GpsLogService extends Service {

    GpsHelper gps;
    DatabaseHelper db;

    private static final String TAG = "GpsLogService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHelper(this);
        gps = new GpsHelper(this,db, true);

        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"활동기록 서비스가 실행되었습니다.");
        Toast.makeText(this, "활동기록 서비스가 실행되었습니다.", Toast.LENGTH_LONG).show();
        ContentValues cv = db.getLastLocationAsCv();
        if(cv!=null)
            Log.d(TAG, "가장 최근에 저장된 위치 : " + cv.toString());
        else
            Log.d(TAG, "가장 최근에 저장된 위치가 없습니다.");

        return super.onStartCommand(intent, flags, startId);
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "actlogger_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "ActLogger Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new Builder(this, CHANNEL_ID);
        } else {
            builder = new Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                //.setContent(remoteViews)
                .setContentTitle("활동 기록중")
                .setContentText("현재 활동을 기록중입니다.")
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }
}
