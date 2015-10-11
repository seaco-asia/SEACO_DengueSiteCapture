package com.seaco.denguesitecapture.model;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	
	private  static final String DATABASE_NAME = "DengueSites";
	
	public MySQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_DENGUE_SITE_TABLE = "CREATE TABLE denguesites (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"filename TEXT, " +
				"gps TEXT )";
		db.execSQL(CREATE_DENGUE_SITE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS denguesites");
		this.onCreate(db);
	}
	
	private static final String TABLE_DENGUESITES = "denguesites";
	
	private static final String KEY_ID = "id";
	private static final String KEY_FILENAME = "filename";
	private static final String KEY_GPS = "gps";
	
	private static final String[] COLUMNS = {KEY_ID, KEY_FILENAME, KEY_GPS};
	
	public void addPicture(SitePhotos photo){
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put(KEY_FILENAME, photo.getFilename());
		values.put(KEY_GPS, photo.getGps());
		
		db.insert(TABLE_DENGUESITES, null, values);
		
		db.close();
	}
	
	public SitePhotos getPhoto(int id){
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_DENGUESITES, COLUMNS, " id = ?", new String[] {String.valueOf(id)}, null, null, null, null);
		
		if(cursor != null){
			cursor.moveToFirst();
		}
		
		SitePhotos photo = new SitePhotos();
		photo.setId(Integer.parseInt(cursor.getString(0)));
		photo.setFilename(cursor.getString(1));
		photo.setGps(cursor.getString(2));
		
		return photo;
		
	}
	
	public List<SitePhotos> getAllPhotos(){
		
		List<SitePhotos> photos = new LinkedList<SitePhotos>();
		
		String query = "SELECT * FROM " + TABLE_DENGUESITES;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		SitePhotos photo = null;
		
		if(cursor.moveToFirst()){
			do{
				photo = new SitePhotos();
				photo.setId(Integer.parseInt(cursor.getString(0)));
				photo.setFilename(cursor.getString(1));
				photo.setGps(cursor.getString(2));
				
				photos.add(photo);
			}while (cursor.moveToNext());
		}
		
		return photos;
	}
	
	public String[][] getPhotosResult(){
		
		int i = 0;
		
		String query = "SELECT * FROM " + TABLE_DENGUESITES;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		Log.i("getPhotosResult", "After rawquery");
		
		String[][] photos = new String[cursor.getCount()][3];
		
		Log.i("getPhotosResult", "After photos[][] instatiation");
		
		if(cursor.moveToFirst()){
			
			while(!cursor.isAfterLast()){
				photos[i][0] = cursor.getString(0);
				photos[i][1] = cursor.getString(1);
				photos[i][2] = cursor.getString(2);
				
				Log.i("insideCursor", cursor.getString(0));
				Log.i("insideCursor", cursor.getString(1));
				Log.i("insideCursor", cursor.getString(2));
				
				i++;
				cursor.moveToNext();
			}
			
		}
		
		return photos;
	}	
	
	
}
