package org.minic.destinaionwaker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DBHelperSettingManager extends SQLiteOpenHelper {

    public DBHelperSettingManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE SETTING_SQLITE (_setting INTEGER PRIMARY KEY, " + " ischecked INTEGER);");
        db.execSQL( "INSERT INTO SETTING_SQLITE VALUES(0, 0);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updatesetting(int ischecked) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE SETTING_SQLITE SET ischecked='" + ischecked + "' WHERE _setting=0;");
        db.close();
    }

    public int playBellOnVibeMode() {
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM SETTING_SQLITE WHERE _setting=0;",null);
        while (cursor.moveToNext()) {
            result = cursor.getInt(1);
        }
        return result;

    }
}
