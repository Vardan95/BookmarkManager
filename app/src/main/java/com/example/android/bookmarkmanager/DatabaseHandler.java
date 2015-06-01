package com.example.android.bookmarkmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpetrosyan on 25.05.2015.
 * Database handler for bookmark manager
 * There are 3 tables
 *
 * 1)Categories Table
 * In this table we store categories
 *
 * Available actions:
 *  -Add
 *  -get(with id)
 *  -getAll
 *  -getCount
 *  -delete
 *  -update(2 types)
 *
 * 1)Bookmarks Table
 * In this table we store categories
 *
 * Available actions:
 *  -Add
 *  -get(with id)
 *  -get(with parent id)
 *  -getAll
 *  -getCount
 *  -delete
 *  -update(4 types)
 *
 * 1)Scheduled Items Table
 * In this table we store categories
 *
 * Available actions:
 *  -Add (WARNING! You must manualy check if item is checkable)
 *  -getAll(with id)
 *  -delete
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "(" + KEY_ID + " INTEGER," + KEY_TITLE + " TEXT," + KEY_MODIFIED_TIME + " INTEGER,"
                                                        + KEY_ADDED_TIME + " INTEGER PRIMARY KEY," + KEY_INDEX + " INTEGER," + KEY_PARENT_ID + " INTEGER,"+ KEY_IS_CATEGORY_EMPTY + " INTEGER" + ")";

        String CREATE_BOOKMARKS_TABLE = "CREATE TABLE " + TABLE_BOOKMARKS + "(" + KEY_ID + " INTEGER," + KEY_TITLE + " TEXT," + KEY_URL + " TEXT,"
                + KEY_ADDED_TIME + " INTEGER PRIMARY KEY," + KEY_INDEX + " INTEGER," + KEY_PARENT_ID + " INTEGER," + KEY_PRIORITY + " INTEGER," + KEY_SCHEDULED_TIME + " INTEGER," + KEY_IS_SCHEDULED + " INTEGER"+ ")";


        String CREATE_SCHEDULED_TABLE = "CREATE TABLE " + TABLE_SCHEDULE_TABLE + "(" + KEY_ID + " INTEGER," + KEY_TITLE + " TEXT," + KEY_URL + " TEXT,"
                + KEY_ADDED_TIME + " INTEGER PRIMARY KEY," + KEY_INDEX + " INTEGER," + KEY_PARENT_ID + " INTEGER," + KEY_PRIORITY + " INTEGER," + KEY_SCHEDULED_TIME + " INTEGER," + KEY_IS_SCHEDULED + " INTEGER"+ ")";

        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_BOOKMARKS_TABLE);
        db.execSQL(CREATE_SCHEDULED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE_TABLE);

        // Create tables again
        onCreate(db);
    }

    public void dropDb()
    {
        // Drop older table if existed
        getWritableDatabase().execSQL("DELETE FROM " + TABLE_CATEGORIES);
        getWritableDatabase().execSQL("DELETE FROM " + TABLE_BOOKMARKS);
    }

    public void addCategory(SimpleBookmarkCategory item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,item.getId_());
        values.put(KEY_TITLE,item.getTitle_());
        values.put(KEY_MODIFIED_TIME,item.getTimeModified_());
        values.put(KEY_ADDED_TIME,item.getTimeAdded_());
        values.put(KEY_INDEX,item.getIndex_());
        values.put(KEY_PARENT_ID, item.getParentId_());
        values.put(KEY_IS_CATEGORY_EMPTY,((item.isEmpty()) ? 1 : 0));

        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public void addBookmark(SimpleBookmarkEntry item,boolean intoScheduleTable)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,item.getId_());
        values.put(KEY_TITLE,item.getTitle_());
        values.put(KEY_URL,item.getUrl_());
        values.put(KEY_ADDED_TIME,item.getTime_());
        values.put(KEY_INDEX,item.getIndexInFolder_());
        values.put(KEY_PARENT_ID,item.getParentId_());
        values.put(KEY_PRIORITY,item.getPriority());
        values.put(KEY_SCHEDULED_TIME,item.getScheduleTime());
        values.put(KEY_IS_SCHEDULED, ((item.isScheduled()) ? 1 : 0));

        if(intoScheduleTable)
        {
            db.insert(TABLE_SCHEDULE_TABLE, null, values);
        }
        else
        {
            db.insert(TABLE_BOOKMARKS, null, values);
        }

        db.close();
    }

    public SimpleBookmarkCategory getCategory(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{KEY_ID,
                        KEY_TITLE, KEY_MODIFIED_TIME,KEY_ADDED_TIME,KEY_INDEX,KEY_PARENT_ID,KEY_IS_CATEGORY_EMPTY}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SimpleBookmarkCategory item = new SimpleBookmarkCategory(cursor.getString(1),Long.parseLong(cursor.getString(3)),Long.parseLong(cursor.getString(2)),Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)));
        if(Integer.parseInt(cursor.getString(6)) == 1)
        {
            item.setIsEmpty(true);
        }
        else
        {
            item.setIsEmpty(false);
        }
        return item;
    }

    public SimpleBookmarkEntry getBookmark(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKMARKS, new String[]{KEY_ID,
                        KEY_TITLE, KEY_URL,KEY_ADDED_TIME,KEY_INDEX,KEY_PARENT_ID,KEY_PRIORITY,KEY_SCHEDULED_TIME,KEY_IS_SCHEDULED}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SimpleBookmarkEntry item = new SimpleBookmarkEntry(cursor.getString(2),cursor.getString(1),Long.parseLong(cursor.getString(3)),Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)));
        item.setPriority(Byte.valueOf(cursor.getString(6)));
        item.setScheduleTime(Long.valueOf(cursor.getString(7)));

        if(Integer.parseInt(cursor.getString(8)) == 1)
        {
            item.setIsScheduled_(true);
        }
        else
        {
            item.setIsScheduled_(false);
        }

        return item;
    }

    public List<SimpleBookmarkEntry>  getBookmarkWithParentID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        List<SimpleBookmarkEntry> bookMarkList = new ArrayList<SimpleBookmarkEntry>();

        Cursor cursor = db.query(TABLE_BOOKMARKS, new String[]{KEY_ID,
                        KEY_TITLE, KEY_URL, KEY_ADDED_TIME, KEY_INDEX, KEY_PARENT_ID, KEY_PRIORITY, KEY_SCHEDULED_TIME, KEY_IS_SCHEDULED}, KEY_PARENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do{
                SimpleBookmarkEntry item = new SimpleBookmarkEntry(cursor.getString(2),cursor.getString(1),Long.parseLong(cursor.getString(3)),Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)));
                item.setPriority(Byte.valueOf(cursor.getString(6)));
                item.setScheduleTime(Long.valueOf(cursor.getString(7)));
                if(Integer.parseInt(cursor.getString(8)) == 1)
                {
                    item.setIsScheduled_(true);
                }
                else
                {
                    item.setIsScheduled_(false);
                }
                bookMarkList.add(item);
            } while (cursor.moveToNext());
        }

        return bookMarkList;
    }

    public List<SimpleBookmarkCategory> getAllCategories()
    {
        List<SimpleBookmarkCategory> categoryList = new ArrayList<SimpleBookmarkCategory>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do{
                SimpleBookmarkCategory item = new SimpleBookmarkCategory(cursor.getString(1),Long.parseLong(cursor.getString(3)),Long.parseLong(cursor.getString(2)),Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)));
                if(Integer.parseInt(cursor.getString(6)) == 1)
                {
                    item.setIsEmpty(true);
                }
                else
                {
                    item.setIsEmpty(false);
                }
                categoryList.add(item);
            } while (cursor.moveToNext());
        }

        return categoryList;
    }

    public List<SimpleBookmarkEntry> getAllBookmarks(boolean intoScheduleTable)
    {
        List<SimpleBookmarkEntry> bookMarkList = new ArrayList<SimpleBookmarkEntry>();
        // Select All Query
        String selectQuery = "SELECT  * FROM ";
        if(intoScheduleTable)
        {
            selectQuery  = selectQuery + TABLE_SCHEDULE_TABLE + " ORDER BY " + KEY_SCHEDULED_TIME + "DESC";
        }
        else
        {
            selectQuery  = selectQuery + TABLE_BOOKMARKS;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do{
                SimpleBookmarkEntry item = new SimpleBookmarkEntry(cursor.getString(2),cursor.getString(1),Long.parseLong(cursor.getString(3)),Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)));
                item.setPriority(Byte.valueOf(cursor.getString(6)));
                item.setScheduleTime(Byte.valueOf(cursor.getString(7)));

                if(Integer.parseInt(cursor.getString(8)) == 1)
                {
                    item.setIsScheduled_(true);
                }
                else
                {
                    item.setIsScheduled_(false);
                }

                bookMarkList.add(item);
            } while (cursor.moveToNext());
        }

        return bookMarkList;
    }

    public int getCategoriesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }

    public int getBookmarksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_BOOKMARKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    // Deleting single bookmark entry
    public void deleteBookmarkEntry(SimpleBookmarkEntry entry,boolean intoScheduleTable) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (intoScheduleTable) {
            db.delete(TABLE_SCHEDULE_TABLE, KEY_ADDED_TIME + " = ?",
                    new String[]{String.valueOf(entry.getTime_())});
        } else {
            db.delete(TABLE_BOOKMARKS, KEY_ADDED_TIME + " = ?",
                    new String[]{String.valueOf(entry.getTime_())});
        }
        db.close();
    }

    public void deleteBookmarkEntryFromScheduleTable(long addedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULE_TABLE, KEY_ADDED_TIME + " = ?",
                    new String[]{String.valueOf(addedTime)});
        db.close();
    }

    // Deleting single bookmark category
    public void deleteBookmarkCategory(SimpleBookmarkCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, KEY_ADDED_TIME + " = ?",
                new String[] { String.valueOf(category.getTimeAdded_()) });
        db.close();
    }

    // Updating single bookmark category
    public int updateBookmarkCategory(SimpleBookmarkCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,category.getId_());
        values.put(KEY_TITLE,category.getTitle_());
        values.put(KEY_MODIFIED_TIME,category.getTimeModified_());
        values.put(KEY_ADDED_TIME,category.getTimeAdded_());
        values.put(KEY_INDEX,category.getIndex_());
        values.put(KEY_PARENT_ID, category.getParentId_());
        values.put(KEY_IS_CATEGORY_EMPTY, category.isEmpty());


        // updating row
        return db.update(TABLE_CATEGORIES, values, KEY_ADDED_TIME + " = ?",
                new String[] { String.valueOf(category.getTimeAdded_()) });
    }

    // Updating single bookmark category
    public int updateBookmarkCategory(Long addedTime,Long modified_time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MODIFIED_TIME,modified_time);

        // updating row
        return db.update(TABLE_CATEGORIES, values, KEY_ADDED_TIME + " = ?",
                new String[] { String.valueOf(addedTime) });
    }

    // Updating single bookmark entry
    public int updateBookmarkEntry(SimpleBookmarkEntry item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,item.getId_());
        values.put(KEY_TITLE,item.getTitle_());
        values.put(KEY_URL,item.getUrl_());
        values.put(KEY_ADDED_TIME,item.getTime_());
        values.put(KEY_INDEX,item.getIndexInFolder_());
        values.put(KEY_PARENT_ID,item.getParentId_());
        values.put(KEY_PRIORITY,item.getPriority());
        values.put(KEY_SCHEDULED_TIME,item.getScheduleTime());
        values.put(KEY_IS_SCHEDULED, ((item.isScheduled()) ? 1 : 0));


        // updating row
        return db.update(TABLE_BOOKMARKS, values, KEY_ADDED_TIME + " = ?",
                new String[] { String.valueOf(item.getTime_()) });
    }

    public int updateBookmarkEntry(Long addedTime,Long scheduled_time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
         values.put(KEY_SCHEDULED_TIME,scheduled_time);

        // updating row
        return db.update(TABLE_BOOKMARKS, values, KEY_ADDED_TIME + " = ?",
                new String[] { String.valueOf(addedTime) });
    }

    public int updateBookmarkEntry(Long addedTime,boolean is_scheduled) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IS_SCHEDULED, (is_scheduled ? 1 : 0));

        // updating row
        return db.update(TABLE_BOOKMARKS, values, KEY_ADDED_TIME + " = ?",
                new String[] { String.valueOf(addedTime) });
    }

    public int updateBookmarkEntry(Long addedTime,int priority) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRIORITY,priority);

        // updating row
        return db.update(TABLE_BOOKMARKS, values, KEY_ADDED_TIME + " = ?",
                new String[] { String.valueOf(addedTime) });
    }

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "bookmarkManager";

    private static final String TABLE_CATEGORIES = "categories";

    private static final String TABLE_BOOKMARKS = "bookmarks";

    private static final String TABLE_SCHEDULE_TABLE = "upcoming_scheduled_items";

    //Categories Table columns names
    protected static final String KEY_ID = "id";
    protected static final String KEY_TITLE = "title";
    protected static final String KEY_URL = "url";
    protected static final String KEY_MODIFIED_TIME = "dateGroupModified";
    protected static final String KEY_ADDED_TIME = "dateAdded";
    protected static final String KEY_INDEX = "itemIndex";
    protected static final String KEY_PARENT_ID = "parentId";
    protected static final String KEY_PRIORITY = "priority";
    protected static final String KEY_SCHEDULED_TIME = "scheduled";
    protected static final String KEY_IS_SCHEDULED = "isscheduled";
    protected static final String KEY_IS_CATEGORY_EMPTY = "isempty";
}
