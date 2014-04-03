package online.news.sql;


import java.util.ArrayList;
import java.util.List;

import online.news.entity.Passage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class PassageDAO {

	private static final String TAG = "PassageDAO";
	private Context context;
	private DataBaseHelper helper;
	public PassageDAO(Context context,DataBaseHelper helper){
		this.context = context;
		this.helper = helper;
	}
	
	/**@return true if exists*/
	public boolean find(int pid){
		boolean result;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "+ DataBaseHelper.TABLE_PASSAGE+" where pid = ?",new String[]{pid+""});
		result = cursor.moveToNext();
		cursor.close();
		db.close();
		Log.v(TAG, "find:pid="+pid+",result="+result);
		return result;
	}
	
	/**list the passages */
	public List<Passage> list(){
		List<Passage> list = new ArrayList<Passage>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DataBaseHelper.TABLE_PASSAGE, 
				new String[]{"blockId","pid","priorty","title","content","addTime","editTime","subTitle","editor","reporter","source","keyWord"}
				, null, null, null, null, null);
		Passage p = null;
		while(cursor.moveToNext()){
			p = new Passage();
			p.setBlockId(cursor.getInt(cursor.getColumnIndex("blockId")));
			p.setPid(cursor.getInt(cursor.getColumnIndex("pid")));
			p.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			p.setAddTime(cursor.getString(cursor.getColumnIndex("addTime")));
			p.setEditTime(cursor.getString(cursor.getColumnIndex("editTime")));
			p.setSubTitle(cursor.getString(cursor.getColumnIndex("subTitle")));
			p.setEditor(cursor.getString(cursor.getColumnIndex("editor")));
			p.setReporter(cursor.getString(cursor.getColumnIndex("reporter")));
			p.setSource(cursor.getString(cursor.getColumnIndex("source")));
			p.setKeyWord(cursor.getString(cursor.getColumnIndex("keyWord")));
			list.add(p);
		}
		cursor.close();
		db.close();
		Log.v(TAG, "list:"+list);
		return list;
	}
	
	public Passage getPassage(int pid){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DataBaseHelper.TABLE_PASSAGE, 
				new String[]{"blockId","pid","priorty","title","content","addTime","editTime","subTitle","editor","reporter","source","keyWord"}
				, null, null, null, null, null);
		Passage p = null;
		if(cursor.moveToNext()){
			p = new Passage();
			p.setBlockId(cursor.getInt(cursor.getColumnIndex("blockId")));
			p.setPid(cursor.getInt(cursor.getColumnIndex("pid")));
			p.setPriorty(cursor.getInt(cursor.getColumnIndex("priorty")));
			p.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			p.setContent(cursor.getString(cursor.getColumnIndex("content")));
			p.setAddTime(cursor.getString(cursor.getColumnIndex("addTime")));
			p.setEditTime(cursor.getString(cursor.getColumnIndex("editTime")));
			p.setSubTitle(cursor.getString(cursor.getColumnIndex("subTitle")));
			p.setEditor(cursor.getString(cursor.getColumnIndex("editor")));
			p.setReporter(cursor.getString(cursor.getColumnIndex("reporter")));
			p.setSource(cursor.getString(cursor.getColumnIndex("source")));
			p.setKeyWord(cursor.getString(cursor.getColumnIndex("keyWord")));
		}
		cursor.close();
		db.close();
		Log.v(TAG, "getPassage:"+p);
		return p;
	}
	
	/**
	 * store the passage into db
	 * */
	public void add(Passage p){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("blockId",p.getBlockId());
		cv.put("pid", p.getPid());
		cv.put("priorty", p.getPriorty());
		cv.put("title", p.getTitle());
		cv.put("content", p.getContent());
		cv.put("addTime", p.getAddTime());
		cv.put("editTime", p.getEditTime());
		cv.put("subTitle", p.getSubTitle());
		cv.put("editor", p.getEditor());
		cv.put("reporter", p.getReporter());
		cv.put("source", p.getSource());
		cv.put("keyWord", p.getKeyWord());
		db.insert(DataBaseHelper.TABLE_PASSAGE, null, cv);
		db.close();
		Log.v(TAG, "add:"+p);
	}
	/**
	 * delete the passage from db*/
	public void delete(int pid){
		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = "pid = ?";
		String whereValue[] = {pid+""};
		db.delete(DataBaseHelper.TABLE_PASSAGE, whereClause, whereValue);
		db.close();
		Log.v(TAG, "delete:pid="+pid);
	}
	
}
