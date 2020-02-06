package uz.fti.ag.memoproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javokhir on 05.08.2017.
 * onCreate() – These is where we need to write create table statements.
 * This is called when database is created.
 * onUpgrade() – This method is called when database is upgraded like
 * modifying the table structure, adding constraints to database etc.,
 */

public class DatabaseHandler  extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "memoManager";

    // Contacts table name
    private static final String TABLE_MEMOS = "memos";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TIME = "time";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

    // TABLE CREATION STATEMENT
      final String CREATE_NEW_TABLE = "create table "
                + TABLE_MEMOS + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_IMAGE + " TEXT NOT NULL, "
                + KEY_TITLE + " TEXT NOT NULL, "
                + KEY_TIME + " TEXT NOT NULL)";
        db.execSQL(CREATE_NEW_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMOS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addMemo(ListViewItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, item.getIcon()); // Memo image
        values.put(KEY_TITLE, item.getTitle()); // Memo title
        values.put(KEY_TIME, item.getTime()); // Memo time



        // Inserting Row
        db.insert(TABLE_MEMOS, null, values);
        db.close(); // Closing database connection
    }

   // Getting single contact
    public ListViewItem getMemo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MEMOS, new String[] { KEY_ID, KEY_IMAGE,
                        KEY_TITLE, KEY_TIME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ListViewItem item = new ListViewItem(Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                cursor.getString(2), cursor.getString(3));
        // return item
        return item;
    }

    // Getting All Memos
    public List<ListViewItem> getAllMemos() {
        List<ListViewItem> memoList = new ArrayList<ListViewItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MEMOS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ListViewItem item = new ListViewItem();
                item.setID(Integer.parseInt(cursor.getString(0)));
                item.setIcon(cursor.getString(1));
                item.setTitle(cursor.getString(2));
                item.setTime(cursor.getString(3));

                memoList.add(item);
            } while (cursor.moveToNext());

        }

        // return memo list

        return memoList;
    }

    // Getting memos Count
    public int getMemosCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MEMOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating single memo
    public int updateMemo(ListViewItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, item.getIcon());
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_TIME, item.getTime());


        // updating row
        return db.update(TABLE_MEMOS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getID()) });
    }

    // Deleting single memo
    public void deleteMemo(ListViewItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMOS, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getID()) });
        db.close();
    }
}
