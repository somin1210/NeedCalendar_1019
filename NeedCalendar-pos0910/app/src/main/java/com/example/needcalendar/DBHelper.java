package com.example.needcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyAppDatabase";
    private static final int DATABASE_VERSION = 1;


    // 사용자 정보 테이블
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NAME = "name";

    /* // 로그인 기록 테이블
    private static final String TABLE_LOGIN_RECORD = "login_record";
    private static final String COLUMN_LOGIN_RECORD_ID = "login_record_id";
    private static final String COLUMN_USER_ID_FK = "user_id_fk";
    private static final String COLUMN_LOGIN_TIMESTAMP = "login_timestamp";*/

    // 로그인 세션 기록 테이블
    private static final String TABLE_SESSION = "sessions";
    private static final String COLUMN_SESSION_ID = "session_id";
    private static final String COLUMN_USER_ID_FK = "user_id_fk";
    private static final String COLUMN_SESSION_TOKEN = "session_token";

    // 일정 정보 테이블
    private static final String TABLE_SCHEDULE = "schedules";
    private static final String COLUMN_SCHEDULE_ID = "schedule_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_DATE = "end_date";
    private static final String COLUMN_END_TIME = "end_time";

    // 사용자 정보 테이블
    private static final String TABLE_USERS_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_NAME + " TEXT);";

    // 스케쥴 정보 테이블
    private static final String TABLE_SCHEDULE_CREATE =
            "CREATE TABLE " + TABLE_SCHEDULE + " (" +
                    COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_START_DATE + " TEXT, " +
                    COLUMN_START_TIME + " TEXT, " +
                    COLUMN_END_DATE + " TEXT, " +
                    COLUMN_END_TIME + " TEXT);";

    // 로그인 세션 정보 테이블 생성 SQL 문
    private static final String TABLE_SESSION_CREATE =
            "CREATE TABLE " + TABLE_SESSION + " (" +
                    COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID_FK + " INTEGER, " +
                    COLUMN_SESSION_TOKEN + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "));";

    /* 로그인 기록 테이블 -> 오류
    private static final String TABLE_LOGIN_RECORD_CREATE =
            "CREATE TABLE " + TABLE_LOGIN_RECORD + " (" +
                    COLUMN_LOGIN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID_FK + " INTEGER, " +
                    COLUMN_LOGIN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "));";

     */

    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USERS_CREATE);
        db.execSQL(TABLE_SCHEDULE_CREATE);
    }

    public static String getColumnUserId() {
        return COLUMN_USER_ID;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        onCreate(db);
    }

    // 사용자 정보를 데이터베이스에 추가하는 메서드
    public boolean addUser(String email, String password, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NAME, name);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // 이메일로 사용자 정보를 검색하는 메서드
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_NAME};
        String selection = COLUMN_EMAIL + "=?";
        String[] selectionArgs = {email};

        return db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }

    // 이메일 형식에 맞는지 검사.
    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_EMAIL + "=?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean isTaken = cursor.getCount() > 0;
        cursor.close();
        return isTaken;
    }

    // 이메일과 패스워드를 확인하는 메서드
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean isValidUser = cursor.getCount() > 0;
        cursor.close();
        return isValidUser;
    }


    // 로그인 세션을 저장하는 코드 -> 작동 안되서 고쳐야함.
    public boolean addSession(long userId, String sessionToken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_SESSION_TOKEN, sessionToken);

        long result = db.insert(TABLE_SESSION, null, values);
        return result != -1;
    }

    // 로그아웃 시 세션 기록을 지우기 위한 코드.
    public boolean removeSession(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_USER_ID_FK + "=?";
        String[] whereArgs = {String.valueOf(userId)};
        int rowsDeleted = db.delete(TABLE_SESSION, whereClause, whereArgs);
        return rowsDeleted > 0;
    }


   /*  로그인 기록 저장하는 코드 -> 기록 저장이 안됨.
   public boolean addLoginRecord(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);

        long result = db.insert(TABLE_LOGIN_RECORD, null, values);
        return result != -1;
    }*/

    // 일정 정보를 데이터베이스에 추가하는 메서드.
    public boolean addSchedule(String title, String startDate, String startTime, String endDate, String endTime) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_START_DATE, startDate);

        long result = db.insert(TABLE_SCHEDULE, null, values);
        return result != -1;
    }


    // 일정 제목이 존재하지 않을 때 오류 출력.
    public String getScheduleTitle(Cursor cursor) {
        int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
        if (titleIndex != -1) {
            return cursor.getString(titleIndex);
        } else {
            // 컬럼이 존재하지 않을 때 처리
            return "일정 제목을 입력하여주세요";
        }
    }

    // 일정 시작 날짜가 존재하지 않을 때 오류 출력.
    public String getScheduleDate(Cursor cursor) {
        int startIndex = cursor.getColumnIndex(COLUMN_START_DATE);
        if (startIndex != -1) {
            return cursor.getString(startIndex);
        } else {
            // 컬럼이 존재하지 않을 때 처리
            return "일정 시작 날짜를 입력하여주세요";
        }
    }

}