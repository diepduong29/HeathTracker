package com.dt2d.heathtracker.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //Them-Sua-Xoa
    public boolean QueryDataBase(String query){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(query);
        return false;
    }

    //Tra ve ket qua truy van select
    public Cursor GetDataBase(String query){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(query, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Tạo bảng cho lưu bước chân
        String createStepsTable = "CREATE TABLE IF NOT EXISTS steps_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "steps INTEGER, " +
                "timestamp TEXT)";
        sqLiteDatabase.execSQL(createStepsTable);

        // Tạo bảng cho lưu lượng nước uống
        String createWaterTable = "CREATE TABLE IF NOT EXISTS water_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount INTEGER, " +
                "timestamp TEXT)";
        sqLiteDatabase.execSQL(createWaterTable);

        // Tạo bảng cho lưu chỉ số BMI
        String createBmiTable = "CREATE TABLE IF NOT EXISTS bmi_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "weight REAL, " +
                "height REAL, " +
                "bmi REAL, " +
                "timestamp TEXT)";
        sqLiteDatabase.execSQL(createBmiTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
