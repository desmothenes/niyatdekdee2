package com.niyatdekdee.notfy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NIYAYNAME = "name";
    public static final String KEY_URL = "url";
    public static final String KEY_LASTCHAPTER = "chapter";
    public static final String KEY_LASTTITLE = "title";

    private static final String TAG = "DatabaseAdapter";
    private static final String DATABASE_NAME = "NiyayDekD";
    private static final String DATABASE_TABLE = "niyays";
    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_CREATE = "create table niyays " +
            "(_id integer primary key autoincrement, " +
            "name text,url text,chapter numeric,title text);"; //,update_date date default CURRENT_TIMESTAMP
    private Context context = null;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public DatabaseAdapter open() {
        Log.w(TAG, "open");
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        Log.w(TAG, "close");
        DBHelper.close();
    }

    public long insertNiyay(String name, String url, int chapter, String title) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NIYAYNAME, name);
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_LASTCHAPTER, chapter);
        initialValues.put(KEY_LASTTITLE, title);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteNiyay(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateNiyay(long rowId, String name, String url, int chapter, String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_NIYAYNAME, name);
        args.put(KEY_URL, url);
        args.put(KEY_LASTCHAPTER, chapter);
        args.put(KEY_LASTTITLE, title);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateTitle(long rowId, String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_LASTTITLE, title);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

//	public boolean updateTime(long rowId,String time) {
//		ContentValues args = new ContentValues();
//		args.put(KEY_TIME, time);
//		return db.update(DATABASE_TABLE,args, KEY_ROWID+"="+rowId, null)>0;
//	}	

    public boolean updateChapter(long rowId, int chapter, String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_LASTCHAPTER, chapter);
        args.put(KEY_LASTTITLE, title);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor getAllNiyay() {
        return db.query(DATABASE_TABLE, new String[]{
                KEY_ROWID,
                KEY_NIYAYNAME,
                KEY_URL,
                KEY_LASTCHAPTER,
                KEY_LASTTITLE
        }, null, null, null, null, null);
    }

    public Cursor getNiyay(long rowId) throws SQLException {
        Cursor mCorsor = db.query(true, DATABASE_TABLE, new String[]{
                KEY_ROWID,
                KEY_NIYAYNAME,
                KEY_URL,
                KEY_LASTCHAPTER,
                KEY_LASTTITLE
        }, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCorsor != null) {
            mCorsor.moveToFirst();
        }
        return mCorsor;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
//			if (oldVersion < 2) {
//				final String ALTER_TBL = 
//		                "ALTER TABLE " + DATABASE_TABLE +
//		                " ADD COLUMN update_date date default CURRENT_TIMESTAMP;";
//		            db.execSQL(ALTER_TBL);
//			}	
            Log.w(TAG, "upgrad ver" + oldVersion + " to  " + newVersion);
        }
    }
}

