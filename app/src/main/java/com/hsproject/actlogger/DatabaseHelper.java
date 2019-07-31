package com.hsproject.actlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "database.sqlite";
    private static final int VERSION = 1;

    /* DB 목록 */
    //위치
    private static final String TABLE_LOCATION = "locations";
    private static final String COLUMN_LOCATION_TIMESTAMP = "timestamp"; // TYPE : 정수
    private static final String COLUMN_LOCATION_LATITUDE = "latitude"; // TYPE : 실수
    private static final String COLUMN_LOCATION_LONGITUDE = "longitude"; // TYPE : 실수
    private static final String COLUMN_LOCATION_ALTITUDE = "altitude"; // TYPE : 실수
    private static final String COLUMN_LOCATION_SPEED = "speed"; // TYPE : 실수
    private static final String COLUMN_LOCATION_HEADING = "heading"; // TYPE : 실수
    private static final String COLUMN_LOCATION_ACCURACY = "accuracy"; // TYPE : 실수
    private static final String COLUMN_LOCATION_PROVIDER = "provider"; // TYPE : 정수

    // TODO: 활동정보(위치-활동 연계) 저장 DB 추가

    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "location" table
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_LOCATION +" (" +
                " timestamp integer, " +
                " latitude real, " +
                " longitude real, " +
                " altitude real, " +
                " speed real, " +
                " heading real, " +
                " accuracy real, " +
                " provider varchar(100)" +
                " )");

        Log.d(TAG, "database helper이(가) 생성되었습니다.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement schema changes and data massage here when upgrading
    }

    /* 현재 위치정보를 DB에 저장하는 메서드 */
    public long insertLocation(Location location) {

        Log.d(TAG, "위치정보 저장 : 위도=" + location.getLatitude() + " 경도=" + location.getLongitude());

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LOCATION_TIMESTAMP, location.getTime());
        cv.put(COLUMN_LOCATION_LATITUDE, location.getLatitude());
        cv.put(COLUMN_LOCATION_LONGITUDE, location.getLongitude());
        cv.put(COLUMN_LOCATION_ALTITUDE, location.getAltitude());
        cv.put(COLUMN_LOCATION_SPEED, location.getSpeed()*3.6f);    // KPH 변환
        cv.put(COLUMN_LOCATION_HEADING, location.getBearing());
        cv.put(COLUMN_LOCATION_ACCURACY, location.getAccuracy());
        cv.put(COLUMN_LOCATION_PROVIDER, location.getProvider());

        long result = getWritableDatabase().insert(TABLE_LOCATION, null, cv);
        close();
        return result;
    }

    public ContentValues getLastLocation(){
        ContentValues cv = new ContentValues();
        // TODO: DB에서 가장 최근 위치정보 불러오기
        SQLiteDatabase db = getReadableDatabase();


        return cv;
    }
}