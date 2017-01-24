package model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lifka on 30/12/16.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DABABASE_NAME = "ephemeralcontacts.db";
    private static DBHelper instance;
    private static Context context;

    private DBHelper() {
        super(context, DABABASE_NAME, null, DATABASE_VERSION);
    }

    public static void setContext(Context c) {
        context = c;
        instance = new DBHelper();
    }

    public static DBHelper getInstance() {
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*************/Log.d("[-------DEBUG-------]", "DBHelper: CREANDO DB...");

        String table_contacts = "CREATE TABLE " + DBContract.TABLE_NAME_CONTACTS + "("
                + DBContract.ID_CON + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBContract.TAG_CON + " INTEGER,"
                + DBContract.NAME_CON + " TEXT,"
                + DBContract.PHONE_CON + " TEXT,"
                + DBContract.DATE_REC_CON + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + DBContract.DATE_EXPIRATION_CON + " DATETIME,"
                + DBContract.DATE_EXPIRATION_DAYS_CON + " INTEGER "
                + ");";

        try {
            sqLiteDatabase.execSQL(table_contacts);
            /*************/Log.d("[-------DEBUG-------]", "DBHelper: execSQL: " + table_contacts);
        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBHelper error: execSQL: " + table_contacts);
        }


        String table_tags = "CREATE TABLE " + DBContract.TABLE_NAME_TAGS + "("
                + DBContract.ID_TAG + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBContract.TAG_TAG + " TEXT,"
                + DBContract.COUNT_TAG + " TEXT "
                + ");";

        try {
            sqLiteDatabase.execSQL(table_tags);
            /*************/Log.d("[-------DEBUG-------]", "DBHelper: execSQL: " + table_tags);
        } catch (Exception e){
            /*************/Log.e("[-------DEBUG-------]", "DBHelper error: execSQL: " + table_tags);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}