package org.minic.destinaionwaker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Reference : http://blog.naver.com/PostView.nhn?blogId=hee072794&logNo=220619425456
 */

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 DEMO_SQLITE이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 문자열 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL( "CREATE TABLE DESTINATION_SQLITE (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +  "lat DOUBLE, lng DOUBLE, name TEXT);");

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(double lat, double lng,String name) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL( "INSERT INTO DESTINATION_SQLITE VALUES(null, " + "'" + lat + "', " + lng + ", '" + name + "');");
        db.close();
    }

    public void update(double lat, double lng,int id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE DESTINATION_SQLITE SET lat='" + lat + "', lng='" + lng + "', name='" + name + "' WHERE _id='" + id + "';");
        db.close();
    }

    public void delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM DESTINATION_SQLITE WHERE _id='" + id + "';");
        db.close();
    }

    int maxrow=0;
    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM DESTINATION_SQLITE", null);
        while (cursor.moveToNext()) {
            maxrow=Integer.parseInt(cursor.getString(0));
        }
        return result;
    }

    public String getResultrow(int row) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        Cursor cursor = db.rawQuery("SELECT * FROM DESTINATION_SQLITE WHERE _id='" + row + "';",null);
        while (cursor.moveToNext()) {
            result += "id:"
                    + cursor.getString(0)
                    + " /lat: "
                    + cursor.getDouble(1)
                    + " /lng: "
                    + cursor.getDouble(2)
                    + "/이름: "
                    + cursor.getString(3);

        }

        return result;
    }

    public double getLatrow(int row) {
        SQLiteDatabase db = getReadableDatabase();
        double result = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM DESTINATION_SQLITE WHERE _id='" + row + "';",null);
        while (cursor.moveToNext()) {
            result += cursor.getDouble(1);
        }
        return result;
    }

    public double getLngrow(int row) {
        SQLiteDatabase db = getReadableDatabase();
        double result = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM DESTINATION_SQLITE WHERE _id='" + row + "';",null);
        while (cursor.moveToNext()) {
            result += cursor.getDouble(2);
        }
        return result;
    }
    public String getname(int row) {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        Cursor cursor = db.rawQuery("SELECT * FROM DESTINATION_SQLITE WHERE _id='" + row + "';",null);
        while (cursor.moveToNext()) {
            result += cursor.getString(3);
        }
        return result;
    }

}
