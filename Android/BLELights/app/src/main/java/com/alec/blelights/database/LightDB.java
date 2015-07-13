package com.alec.blelights.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class LightDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Lights.db";
    public static final String TABLE_NAME = "LIGHTS";
    public static final String NAME = "NAME";
    public static final String ADDRESS = "ADDRESS";
    public static final String NNAME = "NNAME";
    public static final String PRIMARY = "ISPRIMARY";
    public LightDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    private static final String CREATE_COMMAND = "CREATE TABLE " + TABLE_NAME + " ("
            + BaseColumns._ID + " TEXT PRIMARY KEY, "
            + NAME + " TEXT, "
            + ADDRESS + " TEXT, "
            + NNAME + " TEXT, "
            + PRIMARY + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}