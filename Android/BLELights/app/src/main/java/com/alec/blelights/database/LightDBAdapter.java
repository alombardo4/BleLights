package com.alec.blelights.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Pair;


import com.alec.blelights.model.BLELight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alec on 3/2/15.
 */
public class LightDBAdapter {
    private static LightDBAdapter adapter;
    private static LightDB dbHelper;

    public static LightDBAdapter getInstance(Activity activity) {
        if (adapter == null) {
            adapter = new LightDBAdapter();
            dbHelper = new LightDB(activity);
        }
        return adapter;
    }

    public void addDevice(BLELight device) {
        addDevice(device.getName(), device.getAddress(), device.getNickname());
    }

    private void addDevice(String name, String address, String nickname) {
        ContentValues values = new ContentValues();
        values.put(BaseColumns._ID, address);
        values.put(LightDB.NAME, name);
        values.put(LightDB.ADDRESS, address);
        values.put(LightDB.NNAME, nickname);

        if (getNumberOfDevices() == 0) {
            values.put(LightDB.PRIMARY, "1");
        } else {
            values.put(LightDB.PRIMARY, "0");
        }
        insertItem(values);

    }


    public int getNumberOfDevices() {
        SQLiteDatabase db = getReadable();
        Cursor cursor = db.query(LightDB.TABLE_NAME, new String[]{BaseColumns._ID}, null, null, null, null, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public BLELight getFirstDevice() {
        SQLiteDatabase db = getReadable();

        String whereClause = LightDB.PRIMARY + "=?";
        Cursor cursor = db.query(LightDB.TABLE_NAME,
                new String[]{BaseColumns._ID, LightDB.NAME, LightDB.ADDRESS, LightDB.NNAME},
                whereClause, new String[]{"1"}, null, null, null);
        cursor.moveToFirst();
        db.close();
        if (cursor.getCount() > 0) {
            return new BLELight(cursor.getString(1), cursor.getString(2), cursor.getString(3), true);
        } else {
            return null;
        }

    }

    public List<BLELight> getDevices() {
        List<BLELight> devices = new ArrayList<>();
        SQLiteDatabase db = getReadable();
        Cursor cursor = db.query(LightDB.TABLE_NAME, new String[]{BaseColumns._ID, LightDB.NAME, LightDB.ADDRESS, LightDB.NNAME, LightDB.PRIMARY}, null, null, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            boolean isPrimary = Integer.parseInt(cursor.getString(4)) == 1 ? true : false;
            BLELight device = new BLELight(cursor.getString(1), cursor.getString(2), cursor.getString(3), isPrimary);
            devices.add(device);
        }
        db.close();
        return devices;
    }

    public void updateDevice(BLELight device) {
        SQLiteDatabase db = getWriteable();
        ContentValues values = new ContentValues();
        values.put(LightDB.NAME, device.getName());
        values.put(LightDB.ADDRESS, device.getAddress());
        values.put(LightDB.NNAME, device.getNickname());
        values.put(LightDB.PRIMARY, device.isPrimary() ? "1" : "0");

        String whereClause = BaseColumns._ID + "=?";
        db.update(LightDB.TABLE_NAME, values, whereClause, new String[] {device.getAddress()});
        db.close();
    }

    public void deleteDevice(BLELight device) {
        SQLiteDatabase db = getWriteable();
        String whereClause = BaseColumns._ID + "=?";
        db.delete(LightDB.TABLE_NAME, whereClause, new String[]{device.getAddress()});
        db.close();
    }
    private SQLiteDatabase getWriteable() {
        return dbHelper.getWritableDatabase();
    }

    private SQLiteDatabase getReadable() {
        return dbHelper.getReadableDatabase();
    }

    private void insertItem(ContentValues values) {
        SQLiteDatabase db = getWriteable();
        db.insert(LightDB.TABLE_NAME, null, values);
        db.close();
    }

    public void setPrimaryDevice(BLELight device) {
        SQLiteDatabase db = getWriteable();
        ContentValues values = new ContentValues();
        values.put(LightDB.PRIMARY, "0");
        db.update(LightDB.TABLE_NAME, values, null, null);
        values = new ContentValues();
        values.put(LightDB.PRIMARY, "1");
        String whereClause = BaseColumns._ID + "=?";
        db.update(LightDB.TABLE_NAME, values, whereClause, new String[] {device.getAddress()});
        db.close();
    }


}