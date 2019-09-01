package com.hsproject.actlogger;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GpsHelper {
    DatabaseHelper db;

    Context context;
    LocationManager lManager;
    //LocationProvider lProvider;
    LocationListener lListener;

    private final int GPS_INTERVAL_TIME_MS = 1000 * 60 * 3; // 3분마다
    private final int GPS_DISTANCE_DELTA_M = 0;

    Geocoder geocoder; // 역지오코딩 하기 위해

    private static final String TAG = "GpsHelper";

    public GpsHelper(Context c, DatabaseHelper db, boolean isServiceMode) {
        context = c;
        lManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        this.db = db;

        geocoder = new Geocoder(context);  // 역지오코딩 하기 위해
        if(isServiceMode)
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
        Toast.makeText(context, "위치정보를 업데이트합니다.", Toast.LENGTH_SHORT).show();

        Location location = getLastKnownLocation();
        if(location==null){
            Log.d(TAG,"위치정보 받아오기 실패");
            return;
        }


        String provider = location.getProvider();
        long time = location.getTime();
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
        // GPS로 위치 탐색
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                GPS_INTERVAL_TIME_MS,
                GPS_DISTANCE_DELTA_M,
                gpsLocationListener);
        // 네트워크로 위치 탐색
        lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                GPS_INTERVAL_TIME_MS,
                GPS_DISTANCE_DELTA_M,
        gpsLocationListener);
    }



    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            long time = location.getTime();
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

            // DB에 저장
            db.insertLocation(location);

            // TODO: MainActivity에 브로드캐스트 (위치 새로고침)

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

    public String reverseCoding(double latitude, double longitude){ // 위도 경도 넣어가지구 역지오코딩 주소값 뽑아낸다
        Log.d(TAG,"reverseCoding");
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
            return "에러";
        }
        if (list != null) {
            if (list.size()==0) {
                return "주소 정보가 없습니다";
            } else {
                // 문자열 자르기
                String cut[] = list.get(0).toString().split(" ");
                /*
                for(int i=0; i<cut.length; i++){
                    System.out.println("cut["+i+"] : " + cut[i]);
                } // cut[0] : Address[addressLines=[0:"대한민국
                // cut[1] : 서울특별시  cut[2] : 송파구  cut[3] : 오금동
                // cut[4] : cut[4] : 41-26"],feature=41-26,admin=null ~~~~
                */
                return (list.get(0).toString().split("0:\"")[1].split("\"")[0]); // 내가 원하는 구의 값을 뽑아내 출력
            }
        }
        return "에러";
    }

    public double getDistance(double lat1, double lng1, double lat2, double lng2){
        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        return locationA.distanceTo(locationB);
    }

    public String isInArea(double myLatitude, double myLongitude){
        ArrayList<ContentValues> actList = db.getActSettingList();

        for(int i=0; i<actList.size(); i++) {
            double actLatitude = actList.get(i).getAsDouble(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_LATITUDE);
            double actLongitude = actList.get(i).getAsDouble(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_LONGITUDE);
            int actRange = actList.get(i).getAsInteger(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_RANGE);
            Log.d(TAG, actLatitude + ", " + actLongitude + ", " + myLatitude + ", " + myLongitude);
            Log.d(TAG, actRange + " > " + getDistance(actLatitude, actLongitude, myLatitude, myLongitude));
            if(actRange > getDistance(actLatitude, actLongitude, myLatitude, myLongitude)) {
                Log.d(TAG, "위치-활동 감지");
                return actList.get(i).getAsString(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_NAME);
            }
        }

        return null;
    }
}
