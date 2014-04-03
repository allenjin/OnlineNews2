package online.news.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{
	public static final String DATABASE_FILE_NAME = "store.db";
	private static final int VERSION = 1;
	public static final String TABLE_PASSAGE = "passage";
	private Context context;
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	public DataBaseHelper(Context context){	
		super(context,DATABASE_FILE_NAME,null,VERSION);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createPassageTable = "create table "+ TABLE_PASSAGE+"(blockId integer,pid integer,priorty integer,title text,content text,addTime text,editTime text,subTitle text,editor text,reporter text,source text,keyWord text)";
		db.execSQL(createPassageTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
