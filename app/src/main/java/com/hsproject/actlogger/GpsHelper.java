package com.hsproject.actlogger;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

public class GpsHelper {
    Context context;
    LocationManager lManager;
    //LocationProvider lProvider;
    LocationListener lListener;
    private final int GPS_INTERVAL_TIME_MS = 10000;
    private final int GPS_DISTANCE_DELTA_M = 10;

    public GpsHelper(Context c) {
        context = c;
        lManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        updateLocation();
    }

    private void updateLocation() {

        // 권한 체크
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                1,
                gpsLocationListener);
        lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,
                1,
                gpsLocationListener);
    }



    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double time = location.getTime();
            double speed = location.getSpeed()*3.6f;
            double heading = location.getBearing();
            double accuracy = location.getAccuracy();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            // TODO: DB에 저장

            // TODO: GpsLogService에 브로드캐스트

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}
