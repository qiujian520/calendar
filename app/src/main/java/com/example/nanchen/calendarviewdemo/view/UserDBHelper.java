package com.example.nanchen.calendarviewdemo.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "UserDBHelper";
    private static final String DB_NAME = "user.db";
    private static final int DB_VERSION = 2;
    private static UserDBHelper mHelper = null;
    private SQLiteDatabase mDB = null;
    private static final String TABLE_NAME = "user_info";

    private UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private UserDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    /**
     * 单例获取Helper对象
     *
     * @param context
     * @param version
     * @return
     */
    public static UserDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new UserDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new UserDBHelper(context);
        }
        return mHelper;
    }

    /**
     * 通过读链接获取SQLiteDatabase
     *
     * @return
     */
    public SQLiteDatabase openReadLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    /**
     * 通过写链接获取SQLiteDatabase
     *
     * @return
     */
    public SQLiteDatabase openWriteLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    /**
     * 关闭数据库
     */
    public void closeLink() {
        if (mDB != null && mDB.isOpen() == true) {
            mDB.close();
            mDB = null;
        }
    }

    /**
     * 获取数据库名称
     *
     * @return
     */
    public String getDbName() {
        if (mHelper != null) {
            return mHelper.getDatabaseName();
        } else {
            return DB_NAME;
        }
    }

    /**
     * 只在第一次打开数据库时执行，再此可进行表结构创建的操作
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "text VARCHAR NOT NULL," + "year VARCHAR NOT NULL,"
                + "month INTEGER NOT NULL," + "day INTEGER NOT NULL,"
                + "name VARCHAR NOT NULL"  + ");";
        db.execSQL(create_sql);
    }

    /**
     * 在数据库版本升高时执行，可以根据新旧版本号进行表结构变更处理
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //其实这里是根据oldVersion的不同进行字段的添加
        if (oldVersion == 1) {
            //Android 的 ALTER 命令不支持一次添加多列，只能分多次添加
            String alter_sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + "phone VARCHAR;";
            db.execSQL(alter_sql);
            alter_sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + "password VARCHAR;";
            db.execSQL(alter_sql);
        }
    }

    /**
     * 根据条件删除数据
     *
     * @param condition
     * @return
     */
    public int delete(String condition) {
        int count = mDB.delete(TABLE_NAME, condition, null);
        //null的位置，如果condition中用了？作为数值占位，比如"id>?",null要给new String[]{"2"}
        return count;
    }

    /**
     * 删除全部数据
     *
     * @return
     */
    public int deleteAll() {
        int count = mDB.delete(TABLE_NAME, "1=1", null);
        return count;
    }

    /**
     * 增加单条数据，其实就是包装了一下调用增加多条数据
     *
     * @param info
     * @return
     */
    public long insert(UserInfo info) {
        ArrayList<UserInfo> infoArray = new ArrayList<>();
        infoArray.add(info);
        return insert(infoArray);
    }

    /**
     * 根据条件进行查询
     *
     * @param condition
     * @return
     */
    public ArrayList<UserInfo> query(String condition) {
        String sql = String.format("select rowid,text,year,month,day,name from %s where %s;", TABLE_NAME, condition);
        ArrayList<UserInfo> infoArray = new ArrayList<>();
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            for (; ; cursor.moveToNext()) {
                UserInfo info = new UserInfo();
                info.setRowid(cursor.getInt(0));
                info.setText(cursor.getString(1));
                info.setYear(cursor.getString(2));
                info.setMonth(cursor.getInt(3));
                info.setDay(cursor.getInt(4));
                info.setName(cursor.getString(5));
                infoArray.add(info);
                if (cursor.isLast() == true) {
                    break;
                }
            }
        }
        cursor.close();
        return infoArray;
    }

    public ArrayList<UserInfo> queryAll() {
        String sql = String.format("select * from %s;", TABLE_NAME);
        ArrayList<UserInfo> infoArray = new ArrayList<>();
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            for (; ; cursor.moveToNext()) {
                UserInfo info = new UserInfo();
                info.setRowid(cursor.getInt(0));
                info.setText(cursor.getString(1));
                info.setYear(cursor.getString(2));
                info.setMonth(cursor.getInt(3));
                info.setDay(cursor.getInt(4));
                info.setName(cursor.getString(5));
                Log.i(TAG, "queryAll: "+info);
                infoArray.add(info);
                if (cursor.isLast() == true) {
                    break;
                }
            }
        }
        cursor.close();
        return infoArray;
    }

    /**
     * 更新多条数据
     *
     * @param info
     * @param condition
     * @return
     */
    public int update(UserInfo info, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("rowid", info.getRowid());
        cv.put("text", info.getText());
        cv.put("year", info.getYear());
        cv.put("month", info.getMonth());
        cv.put("day", info.getDay());
        cv.put("name", info.getName());
        int count = mDB.update(TABLE_NAME, cv, condition, null);
        return count;
    }

    /**
     * 更新指定的数据条目
     *
     * @param info
     * @return
     */
    public int update(UserInfo info) {
        return update(info, "rowid=" + info.getRowid());
    }

    /**
     * 增加多条数据
     *
     * @param infoArray
     * @return
     */
    public long insert(ArrayList<UserInfo> infoArray) {
        long result = -1;
        //循环全部数据
        for (int i = 0; i < infoArray.size(); i++) {
            UserInfo info = infoArray.get(i);
            ArrayList<UserInfo> tempArray = new ArrayList<>();
            //如果存在同名记录，就更新记录。注意条件语句的等号后面要用单引号括起来
            if (info.getName() != null && info.getName().length() > 0) {
                String condition = String.format("name='%s'", info.getName());
                tempArray = query(condition);
                if (tempArray.size() > 0) {
                    update(info, condition);
                    result = tempArray.get(0).getRowid();
                    continue;
                }
            }
            //如果不存在唯一性重复的记录，就插入新记录
            ContentValues cv = new ContentValues();
            cv.put("rowid", info.getRowid());
            cv.put("text", info.getText());
            cv.put("year", info.getYear());
            cv.put("month", info.getMonth());
            cv.put("day", info.getDay());
            cv.put("name", info.getName());
            result = mDB.insert(TABLE_NAME, "", cv);
            //添加成功后返回行号，失败则返回-1
            if (result == -1) {
                return result;
            }
        }
        return result;
    }
}
