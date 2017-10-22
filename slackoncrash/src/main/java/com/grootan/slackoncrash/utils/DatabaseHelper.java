package com.grootan.slackoncrash.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.grootan.slackoncrash.SlackOnCrash;
import com.grootan.slackoncrash.models.SlackRequest;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by KOBIL on 05/02/17.
 */

public class DatabaseHelper {

    private static DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private static final String DB_TABLENAME ="SlackOnCrash";

    private DatabaseHelper(Context context, File file) {
        SQLiteDatabase.loadLibs(context);
        if (!file.exists()) {
            file.mkdirs();
            file.delete();
        }
        WifiManager m_wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        String m_wlanMacAdd = m_wm.getConnectionInfo().getMacAddress()+"_SLACKONCRASH";
        database = SQLiteDatabase.openOrCreateDatabase(file, m_wlanMacAdd, null);
        createObject(new String[]{
                "id","hook","request"
        });

    }

    public static void initialize(Context context, File file) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context, file);
        }
    }

    public static DatabaseHelper getInstance() {
        return databaseHelper;
    }

    private boolean isTableExists() {
        Cursor c = null;
        boolean tableExists = false;
        try {
            c = database.query(DB_TABLENAME, null,
                    null, null, null, null, null);
            tableExists = true;
        } catch (Exception e) {
        }

        return tableExists;
    }

    private void createObject(String[] columnNames) {
        if (!isTableExists()) // if no rows then do
        {
            String query = "create table " + DB_TABLENAME + "(";
            String columns = Arrays.toString(columnNames);
            database.execSQL(query + columns.substring(1, columns.length() - 1) + ")");
        }
    }


    public boolean deleteObject(String id) {
        Log.d("DatabaseHelper",id);
        return database.delete(DB_TABLENAME, "id='" + id + "'", null) > 0;
    }
    public void deleteAllObject() {
        database.execSQL("delete from " + DB_TABLENAME);
    }


    public void insertObject(DBModel model) {
        String columns = Arrays.toString(model.columnNames());
        database.execSQL("insert into " + DB_TABLENAME + "(" + columns.substring(1, columns.length() - 1) + ")values(?, ?, ?)", model.getArray());
    }


    public ArrayList<DBModel> getAll() {
        Cursor cursor = database.query(DB_TABLENAME, null, null, null, null, null, null);
        ArrayList<DBModel> models = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DBModel model = new DBModel(cursor.getString(0), cursor.getString(1),cursor.getString(2));
                models.add(model);
            }
            cursor.close();
        }
        return models;
    }
}
