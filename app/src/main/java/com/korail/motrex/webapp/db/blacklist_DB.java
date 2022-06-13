package com.korail.motrex.webapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class blacklist_DB extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "blacklist.db";//must modify db name
	private static final int DATABASE_VERSION = 1;

	public  static final String TABLE_NAME = "blacklist";

	public static final String ID 			= "id";
	public static final String LIST_NAME 		= "name";

//	public static final String[] FROM 	= {ID , LIST_NAME };
	public static final String[] FROM 	= { LIST_NAME };

	public static String ORDER_BY = ID+" DESC";

	public blacklist_DB(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	 
	 @Override
	    public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " 	+ TABLE_NAME 			
					+ " (" 
//					+ ID  						+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ LIST_NAME				  	+ " TEXT"
					+ ");");
	    }

	 @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        db.execSQL("DROP TABLE IF EXISTS ListT");
	        onCreate(db);
	    }



}