package service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 创建数据类
 * filedownlog  数据库表名 - 自增长
 * downpath 表字段-下载路径
 * threadid  表字段 - 该线程ID
 * downlength 表字段 - 该线程下载的数据量
 * 
 */
public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "itcast.db";
	private static final int VERSION = 1;
	
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	//创建数据库
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (" +
				"id integer primary key autoincrement, " +
				"downpath varchar(100), " +
				"threadid INTEGER, " +
				"downlength INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS filedownlog");
		onCreate(db);
	}

}
