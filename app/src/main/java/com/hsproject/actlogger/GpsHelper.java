package com.hsproject.actlogger;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.List;

public class GpsHelper {
    Context context;
    LocationManager lManager;
    //LocationProvider lProvider;
    LocationListener lListener;
    private final int GPS_INTERVAL_TIME_MS = 10000;
    private final int GPS_DISTANCE_DELTA_M = 10;

    private static final String TAG = "GpsHelper";

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
            Log.d(TAG,"위치 액세스 권한이 없습니다.");
            return;
        }

        Log.d(TAG,"위치정보를 업데이트합니다.");
        Toast.makeText(context, "위치정보를 업데이트합니다.", Toast.LENGTH_LONG).show();

        Location location = getLastKnownLocation();
        if(location==null){
            Log.d(TAG,"위치정보 받아오기 실패");
            return;
        }


        String provider = location.getProvider();
        double time = location.getTime();
        double speed = location.getSpeed()*3.6f;
        double heading = location.getBearing();
        double accuracy = location.getAccuracy();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        double altitude = location.getAltitude();

        Log.d(TAG,"위치정보 : " + provider + "\n" +
                "시간 : " + time + "\n" +
                "위도 : " + longitude + "\n" +
                "경도 : " + latitude + "\n" +
                "고도  : " + altitude + "\n" +
                "속도 : " + speed + "\n" +
                "방향 : " + heading + "\n" +
                "정확도 : " + accuracy);

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

            Log.d(TAG, "위치정보 : " + provider + "\n" +
                    "시간 : " + time + "\n" +
                    "위도 : " + longitude + "\n" +
                    "경도 : " + latitude + "\n" +
                    "고도  : " + altitude + "\n" +
                    "속도 : " + speed + "\n" +
                    "방향 : " + heading + "\n" +
                    "정확도 : " + accuracy);

            Toast.makeText(context, "위치정보 새로 받아옴", Toast.LENGTH_SHORT).show();

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

    private Location getLastKnownLocation() {
        Location l=null;
        List<String> providers = lManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                l = lManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
