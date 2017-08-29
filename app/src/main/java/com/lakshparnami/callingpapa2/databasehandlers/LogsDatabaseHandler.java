package com.lakshparnami.callingpapa2.databasehandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class LogsDatabaseHandler {
    private static final String RealNum = "realnum";
    private static final String Fakenum = "fakenum";
    private static final String Name = "name";
    private static final String ImageID = "image";
    private static final String Time = "time";
    private static final String Duration = "duration";
    private static final String Photo = "photo";
    private static final String TableName = "logtable";
    private static final String DBName = "status.db";
    private static final int DBVersion = 3;
    private static final String Table_Create = "create table if not exists logtable (" +
            "realnum text not null," +
            "fakenum text not null," +
            "image text not null," +
            "time text not null," +
            "duration text not null," +
            "photo blob," +
            "name text not null)";


    private final DataBaseHelper dbh;
    private SQLiteDatabase sql;

    public LogsDatabaseHandler(Context context) {
        dbh = new DataBaseHelper(context);
    }



    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context) {
            super(context, DBName, null, DBVersion);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.e("I WAS HERE", "1");
                db.execSQL(Table_Create);

            } catch (SQLException e) {
                Log.e("I WAS HERE", "2");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop Table If Exists logtable");
            onCreate(db);
        }
    }

    public void Open() {
        sql = dbh.getWritableDatabase();
        sql.execSQL(Table_Create);
    }

    public void Close() {
        dbh.close();
    }

    public void insertData(String realNum,String fakenum,String name,String imageID,String time,String duration,byte[] photo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RealNum,realNum);
        contentValues.put(Fakenum,fakenum);
        contentValues.put(Name, name);
        contentValues.put(ImageID,imageID);
        contentValues.put(Time,time);
        contentValues.put(Duration,duration);
        contentValues.put(Photo,photo);
        sql.insert(TableName, null, contentValues);
    }
    /*
    public void deleteData(String time) {
        sql.execSQL("Delete from " + TableName + " where " + Time + "='" + time + "'");
    }*/
    public void deleteAll() {
        sql.execSQL("Drop Table If Exists logtable");
    }

    public Cursor returnData() {
        return sql.query(TableName, new String[]{RealNum,Fakenum,Name,ImageID,Time,Duration,Photo}, null, null, null, null, null);
    }

    public Cursor returnDetails(String realNum) {
        return sql.query(TableName, new String[]{RealNum,Fakenum,Name,ImageID,Time,Duration,Photo}," "+RealNum + "='" + realNum + "'", null, null, null, null);    }

}
