package com.hsproject.actlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.location.Location;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "database.sqlite";
    private static final int VERSION = 1;

    /* DB 목록 */
    //위치 저장 DB
    public static final String TABLE_LOCATION = "locations";
    public static final String COLUMN_LOCATION_TIMESTAMP = "timestamp"; // 시간 TYPE : 정수
    public static final String COLUMN_LOCATION_LATITUDE = "latitude"; // 위도 TYPE : 실수
    public static final String COLUMN_LOCATION_LONGITUDE = "longitude"; // 경도 TYPE : 실수
    public static final String COLUMN_LOCATION_ALTITUDE = "altitude"; // 고도 TYPE : 실수
    public static final String COLUMN_LOCATION_SPEED = "speed"; // 속도 TYPE : 실수
    public static final String COLUMN_LOCATION_HEADING = "heading"; // 방향 TYPE : 실수
    public static final String COLUMN_LOCATION_ACCURACY = "accuracy"; // 정확도 TYPE : 실수
    public static final String COLUMN_LOCATION_PROVIDER = "provider"; // 정보제공자 TYPE : 문자열

    //활동정보(위치-활동 연계) 저장 DB
    public static final String TABLE_BEHAVIOR = "behaviors";
    public static final String COLUMN_BEHAVIOR_TIMESTAMP = "timestamp"; // 시간 TYPE : 정수
    public static final String COLUMN_BEHAVIOR_NAME = "name"; // 활동이름 TYPE : 문자열

    //활동정보 업데이트 정보 저장 DB
    private static final String TABLE_BEHAVIOR_LAST = "behaviorlast";
    private static final String COLUMN_BEHAVIOR_LAST_TIMESTAMP = "timestamp"; // 최근 업데이트 시간 TYPE : 정수
    private static final String COLUMN_BEHAVIOR_LAST_LATITUDE = "latitude"; // 최근 위도 TYPE : 실수
    private static final String COLUMN_BEHAVIOR_LAST_LONGITUDE = "longitude"; // 최근 경도 TYPE : 실수

    //활동 설정 정보 저장 DB
    public static final String TABLE_BEHAVIOR_SETTING = "behaviorsetting";
    public static final String COLUMN_BEHAVIOR_SETTING_NAME = "name"; // 활동이름 TYPE : 문자열
    public static final String COLUMN_BEHAVIOR_SETTING_COLOR = "color"; // 활동색깔 TYPE : 정수
    public static final String COLUMN_BEHAVIOR_SETTING_LATITUDE = "latitude"; // 활동위도 TYPE : 실수
    public static final String COLUMN_BEHAVIOR_SETTING_LONGITUDE = "longitude"; // 활동경도 TYPE : 실수
    public static final String COLUMN_BEHAVIOR_SETTING_RANGE = "range"; // 활동범위 TYPE : 정수
    public static final String COLUMN_BEHAVIOR_SETTING_CATEGORY = "category"; // 활동 세부 카테고리 TYPE : 문자열

    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 위치정보 테이블 생성
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

        // 활동정보 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_BEHAVIOR +" (" +
                " timestamp integer, " +
                " name varchar(100)" +
                " )");

        // 활동정보 업데이트(최근) 정보 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_BEHAVIOR_LAST +" (" +
                " timestamp integer, " +
                " latitude real, " +
                " longitude real" +
                " )");

        // 활동 설정 정보 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_BEHAVIOR_SETTING +" (" +
                " name varchar, " +
                " color integer, " +
                " latitude real, " +
                " longitude real, " +
                " range integer, " +
                " category varchar(1024)" +
                " )");

        //초기값 설정
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BEHAVIOR_LAST,null);
        if(cursor.getCount()<1) {
            Log.d(TAG, "TABLE_BEHAVIOR_LAST에 초기값을 적용합니다.");
            db.execSQL("insert into " + TABLE_BEHAVIOR_LAST + " values(0,0,0)");
        }

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

    /* 최근정보를 업데이트하는 메서드 */
    public long updateLastInfo() {
        SQLiteDatabase db = getReadableDatabase();

        Log.d(TAG, "최근정보 업데이트");

        ContentValues cv = getLastLocationAsCv();

        long result = getWritableDatabase().update(TABLE_BEHAVIOR_LAST, cv,null,null);
        close();
        return result;
    }

    /* DB에서 가장 최근 위치정보를 불러오는 메서드 */
    public ContentValues getLastLocationAsCv(){

        Log.d(TAG, "getLastLocationAsCv");

        ContentValues cv = new ContentValues();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        String sql = "SELECT "+COLUMN_LOCATION_TIMESTAMP+","+COLUMN_LOCATION_LATITUDE+","+COLUMN_LOCATION_LONGITUDE
                + " FROM " + TABLE_LOCATION + " ORDER BY " + COLUMN_LOCATION_TIMESTAMP + " DESC limit 1";
        cursor = db.rawQuery(sql,null);

        cursor.moveToFirst();
        int size = cursor.getCount();
        if(size<1) {
            close();
            return null;
        }
        long timestamp = cursor.getLong(0);
        double latitude = cursor.getDouble(1);
        double longitude = cursor.getDouble(2);

        cv.put(COLUMN_LOCATION_TIMESTAMP, timestamp);
        cv.put(COLUMN_LOCATION_LATITUDE, latitude);
        cv.put(COLUMN_LOCATION_LONGITUDE, longitude);

        close();

        return cv;
    }

    public long insertActSetting(String name, int color, double latitude, double longitude, int range, String category){
        Log.d(TAG, "활동설정정보 저장 : 이름=" + name);
        long result = 0;

        ContentValues cv = new ContentValues();

        if(getActSettingByName(name)==null) {   // 중복 체크
            // INSERT
            cv.put(COLUMN_BEHAVIOR_SETTING_NAME, name);
            cv.put(COLUMN_BEHAVIOR_SETTING_COLOR, color);
            cv.put(COLUMN_BEHAVIOR_SETTING_LATITUDE, latitude);
            cv.put(COLUMN_BEHAVIOR_SETTING_LONGITUDE, longitude);
            cv.put(COLUMN_BEHAVIOR_SETTING_RANGE, range);
            cv.put(COLUMN_BEHAVIOR_SETTING_CATEGORY, category);

            result = getWritableDatabase().insert(TABLE_BEHAVIOR_SETTING, null, cv);
        }else{
            // UPDATE
            cv.put(COLUMN_BEHAVIOR_SETTING_COLOR, color);
            cv.put(COLUMN_BEHAVIOR_SETTING_LATITUDE, latitude);
            cv.put(COLUMN_BEHAVIOR_SETTING_LONGITUDE, longitude);
            cv.put(COLUMN_BEHAVIOR_SETTING_RANGE, range);
            cv.put(COLUMN_BEHAVIOR_SETTING_CATEGORY, category);

            result = getWritableDatabase().update(TABLE_BEHAVIOR_SETTING, cv,"name=?",new String[]{name});
        }

        close();
        return result;
    }

    public long deleteActSettingByName(String name){
        long result = getWritableDatabase().delete(TABLE_BEHAVIOR_SETTING,"name=?",new String[]{name});

        return result;
    }

    public ContentValues getActSettingByName(String name){
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        String sql = "SELECT "+COLUMN_BEHAVIOR_SETTING_COLOR+","+COLUMN_BEHAVIOR_SETTING_LATITUDE+","+COLUMN_BEHAVIOR_SETTING_LONGITUDE+","+COLUMN_BEHAVIOR_SETTING_RANGE
                + " FROM " + TABLE_BEHAVIOR_SETTING + " WHERE " + COLUMN_BEHAVIOR_SETTING_NAME + "='" + name +"'";
        cursor = db.rawQuery(sql,null);

        cursor.moveToFirst();
        int size = cursor.getCount();
        if(size<1) {
            close();
            return null;
        }

        int color = cursor.getInt(0);
        double latitude = cursor.getDouble(1);
        double longitude = cursor.getDouble(2);
        int range = cursor.getInt(3);

        cv.put(COLUMN_BEHAVIOR_SETTING_NAME, name);
        cv.put(COLUMN_BEHAVIOR_SETTING_COLOR, color);
        cv.put(COLUMN_BEHAVIOR_SETTING_LATITUDE, latitude);
        cv.put(COLUMN_BEHAVIOR_SETTING_LONGITUDE, longitude);
        cv.put(COLUMN_BEHAVIOR_SETTING_RANGE, range);

        close();

        return cv;
    }

    public ArrayList<String> getActSettingNames(){
        ArrayList<String> al = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        String sql = "SELECT " + COLUMN_BEHAVIOR_SETTING_NAME + " FROM " + TABLE_BEHAVIOR_SETTING + " ORDER BY " + COLUMN_BEHAVIOR_SETTING_NAME;
        cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            String name = cursor.getString(0);
            al.add(name);
        }

        close();

        return al;
    }

    public int getActColorByName(String name){
        int color = Color.rgb(200,200,200);
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        String sql = "SELECT "+COLUMN_BEHAVIOR_SETTING_COLOR
                + " FROM " + TABLE_BEHAVIOR_SETTING + " WHERE " + COLUMN_BEHAVIOR_SETTING_NAME + "='" + name +"'";
        cursor = db.rawQuery(sql,null);

        cursor.moveToFirst();
        int size = cursor.getCount();
        if(size<1) {
            close();
            return color;
        }

        color = cursor.getInt(0);

        return color;
    }

    public ArrayList<ContentValues> getActSettingList(){
        ArrayList<ContentValues> al = new ArrayList<ContentValues>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        String sql = "SELECT * FROM " + TABLE_BEHAVIOR_SETTING + " ORDER BY " + COLUMN_BEHAVIOR_SETTING_NAME;
        cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            String name = cursor.getString(0);
            int color = cursor.getInt(1);
            double latitude = cursor.getDouble(2);
            double longitude = cursor.getDouble(3);
            int range = cursor.getInt(4);

            Log.d(TAG,"DB에서 불러옴: " + name);

            cv.put(COLUMN_BEHAVIOR_SETTING_NAME, name);
            cv.put(COLUMN_BEHAVIOR_SETTING_COLOR, color);
            cv.put(COLUMN_BEHAVIOR_SETTING_LATITUDE, latitude);
            cv.put(COLUMN_BEHAVIOR_SETTING_LONGITUDE, longitude);
            cv.put(COLUMN_BEHAVIOR_SETTING_RANGE, range);

            al.add(cv);
        }

        close();

        return al;
    }

    public long updateActLogFromLast(){
        Log.d(TAG, "updateActLogFromLast()");

        long result = 0;
        long lastUpdatedTimestamp;
        long min_10 = 1000*60*10;

        ArrayList<ContentValues> locationLogList = new ArrayList<ContentValues>();

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor;
        String sql = "SELECT "+ COLUMN_BEHAVIOR_TIMESTAMP + " FROM " + TABLE_BEHAVIOR + " ORDER BY " + COLUMN_BEHAVIOR_TIMESTAMP + " DESC limit 1"; // 가장 최근 update된 TimeStamp
        cursor = db.rawQuery(sql,null);

        cursor.moveToFirst();
        int size = cursor.getCount();
        if(size<1) {
            lastUpdatedTimestamp = 0;
        }else {
            lastUpdatedTimestamp = cursor.getLong(0);
        }

        Log.d(TAG, "updateActLogFromLast() :" + (lastUpdatedTimestamp + min_10) + " AND " + (System.currentTimeMillis() / min_10) * min_10);

        sql = "SELECT " + COLUMN_LOCATION_TIMESTAMP+","+COLUMN_LOCATION_LATITUDE+","+COLUMN_LOCATION_LONGITUDE+","+COLUMN_LOCATION_ACCURACY
                + " FROM " + TABLE_LOCATION + " WHERE " + COLUMN_LOCATION_TIMESTAMP + " BETWEEN " + (lastUpdatedTimestamp + min_10) + " AND " + ((System.currentTimeMillis() / min_10) * min_10) + " ORDER BY " + COLUMN_LOCATION_TIMESTAMP;
        cursor = db.rawQuery(sql,null);

        Log.d(TAG, "Location Log size = " + cursor.getCount());
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();

            long timestamp = cursor.getLong(0);
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);
            double accuracy = cursor.getDouble(3);

            cv.put(COLUMN_LOCATION_TIMESTAMP, timestamp);
            cv.put(COLUMN_LOCATION_LATITUDE, latitude);
            cv.put(COLUMN_LOCATION_LONGITUDE, longitude);
            cv.put(COLUMN_LOCATION_ACCURACY, accuracy);

            locationLogList.add(cv);
        }

        long timestamp;
        long timestamp_next = 0;
        long unique = 0;
        double accuracy = 0;
        int bestIndex = 0;

        for(int i=0; i<locationLogList.size(); i++) {
            timestamp = locationLogList.get(i).getAsLong(COLUMN_LOCATION_TIMESTAMP);
            accuracy = locationLogList.get(i).getAsDouble(COLUMN_LOCATION_ACCURACY);
            bestIndex = i;
            unique = timestamp / min_10;

            //if(i != locationLogList.size()-1) {
                int j;
                for(j=i+1; j<locationLogList.size(); j++) {
                    timestamp_next = locationLogList.get(j).getAsLong(COLUMN_LOCATION_TIMESTAMP);
                    if (unique == (timestamp_next / min_10)) {
                        // 10분 중에 가장 정확한 위치정보 사용
                        if(accuracy > locationLogList.get(j).getAsDouble(COLUMN_LOCATION_ACCURACY)) {
                            accuracy = locationLogList.get(j).getAsDouble(COLUMN_LOCATION_ACCURACY);
                            bestIndex = j;
                        }
                        continue;
                    }else {
                        break;
                    }
                }
                i = j-1;
            //}

            String name;
            name = isInArea(locationLogList.get(bestIndex).getAsDouble(COLUMN_LOCATION_LATITUDE), locationLogList.get(bestIndex).getAsDouble(COLUMN_LOCATION_LONGITUDE));

            insertBehavior(unique*min_10, name);
        }

        return result;
    }

    public long deleteBehaviorsAsName(String name){
        long result = getWritableDatabase().delete(TABLE_BEHAVIOR, "name=?",new String[]{name});

        return result;
    }

    public long insertBehavior(long timestamp, String name){
        Log.d(TAG, "행동 저장 : TimeStamp = " + timestamp + " / Name = " + name);

        long result = 0;

        ContentValues cv = new ContentValues();

        // INSERT
        cv.put(COLUMN_BEHAVIOR_TIMESTAMP, timestamp);
        cv.put(COLUMN_BEHAVIOR_NAME, name);

        result = getWritableDatabase().insert(TABLE_BEHAVIOR, null, cv);

        return result;
    }

    public ArrayList<ContentValues> getActLogFromTo(long startTime, long endTime){
        ArrayList<ContentValues> al = new ArrayList<ContentValues>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        String sql = "SELECT * FROM " + TABLE_BEHAVIOR + " WHERE " + COLUMN_BEHAVIOR_TIMESTAMP + " BETWEEN " + startTime + " AND " + endTime + " ORDER BY " + COLUMN_BEHAVIOR_TIMESTAMP;
        cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();

            long timestamp = cursor.getLong(0);
            String name = cursor.getString(1);

            Log.d(TAG,"DB에서 불러옴: " + name);

            cv.put(COLUMN_BEHAVIOR_TIMESTAMP, timestamp);
            cv.put(COLUMN_BEHAVIOR_NAME, name);

            al.add(cv);
        }

        return al;
    }

    public String isInArea(double myLatitude, double myLongitude){
        ArrayList<ContentValues> actList = getActSettingList();

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

        return "정보없음";
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
}