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
				"gps TEXT, " +
				"photoDesc TEXT)";
		db.execSQL(CREATE_DENGUE_SITE_TABLE);

		String CREATE_DENGUE_SITE_USER_TABLE = "CREATE TABLE denguesitesUser (" +
				"userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"userName TEXT, " +
				"userEmail TEXT, " +
				"userPassword TEXT, "+
				"userRegtype TEXT)";
		db.execSQL(CREATE_DENGUE_SITE_USER_TABLE);

		String CREATE_DENGUE_SITE_TASK_TABLE = "CREATE TABLE denguesitesTask (" +
				"taskId INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"historyId INTEGER, " +
				"filename TEXT, " +
				"officerId INTEGER, "+
				"taskDecision INTEGER, " +
				"taskDesc TEXT, " +
				"claimDate DATETIME DEFAULT CURRENT_TIMESTAMP)";
		db.execSQL(CREATE_DENGUE_SITE_TASK_TABLE);

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
	private static final String KEY_PHOTO_DESC = "photoDesc";

	private static final String[] COLUMNS = {KEY_ID, KEY_FILENAME, KEY_GPS, KEY_PHOTO_DESC};

	//Fix cannot insert gps value into db
	public void addPicture(SitePhotos photo){ 

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FILENAME, photo.getFilename());
		values.put(KEY_GPS, photo.getGps());
		values.put(KEY_PHOTO_DESC, photo.getPhotoDesc());
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
		photo.setPhotoDesc(cursor.getString(3));
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
				photo.setPhotoDesc(cursor.getString(3));

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

		String[][] photos = new String[cursor.getCount()][4];

		Log.i("getPhotosResult", "After photos[][] instatiation");

		if(cursor.moveToFirst()){

			while(!cursor.isAfterLast()){
				photos[i][0] = cursor.getString(0);
				photos[i][1] = cursor.getString(1);
				photos[i][2] = cursor.getString(2);
				photos[i][3] = cursor.getString(3);

				Log.i("insideCursor", cursor.getString(0));
				Log.i("insideCursor", cursor.getString(1));
				Log.i("insideCursor", cursor.getString(2));

				i++;
				cursor.moveToNext();
			}

		}

		return photos;
	}

	private static final String TABLE_DENGUE_SITE_USER = "denguesitesUser";

	private static final String KEY_USER_ID = "userId";
	private static final String KEY_USER_NAME = "userName";
	private static final String KEY_USER_EMAIL = "userEmail";
	private static final String KEY_USER_PASSWORD = "userPassword";
	private static final String KEY_USER_Regtype = "userRegtype";


	private static final String[] COLUMNSUSER = {KEY_USER_ID, KEY_USER_NAME, KEY_USER_EMAIL, KEY_USER_PASSWORD,KEY_USER_Regtype};

	public void addUser(UserDetail userDetail){ 

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_NAME, userDetail.getUserName());
		values.put(KEY_USER_EMAIL, userDetail.getUserEmail());
		values.put(KEY_USER_PASSWORD, userDetail.getUserPassword());
		values.put(KEY_USER_Regtype, userDetail.getUserRegtype());
		db.insert(TABLE_DENGUE_SITE_USER, null, values);

		db.close();
	}

	public List<UserDetail> getAllUsers(){

		List<UserDetail> users = new LinkedList<UserDetail>();

		String query = "SELECT * FROM " + TABLE_DENGUE_SITE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		UserDetail user = null;

		if(cursor.moveToFirst()){
			do{
				user = new UserDetail();
				user.setUserName(cursor.getString(1));
				user.setUserEmail(cursor.getString(2));
				user.setUserPassword(cursor.getString(3));
				user.setUserRegtype(cursor.getString(4));

				users.add(user);
			}while (cursor.moveToNext());
		}

		return users;
	}

	public String[][] getUserResult(){

		int i = 0;

		String query = "SELECT * FROM " + TABLE_DENGUE_SITE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		Log.i("getUserResult", "After rawquery");

		String[][] users = new String[cursor.getCount()][5];

		Log.i("getUserResult", "After users[][] instatiation");

		if(cursor.moveToFirst()){

			while(!cursor.isAfterLast()){
				users[i][0] = cursor.getString(0);
				users[i][1] = cursor.getString(1);
				users[i][2] = cursor.getString(2);
				users[i][3] = cursor.getString(3);
				users[i][4] = cursor.getString(4);

				Log.i("insideCursor", cursor.getString(0));
				Log.i("insideCursor", cursor.getString(1));
				Log.i("insideCursor", cursor.getString(2));
				Log.i("insideCursor", cursor.getString(3));
				Log.i("insideCursor", cursor.getString(4));

				i++;
				cursor.moveToNext();
			}

		}

		return users;
	}

	public boolean checkUserAlreadyInDB (String userEmail, String userPassword){

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DENGUE_SITE_USER, COLUMNSUSER, "userEmail = ? AND userPassword = ?", new String[] {userEmail,userPassword}, null, null, null, null);

		if (cursor.getCount() <= 0) {
			cursor.close();
			return false;
		}else{
			cursor.close();
			return true;
		}

	}

	public String getRegType (String userEmail){

		SQLiteDatabase db = this.getReadableDatabase();

		String regType = null;

		Cursor cursor = db.query(TABLE_DENGUE_SITE_USER, COLUMNSUSER, "userEmail = ?", new String[] {userEmail}, null, null, null, null);

		if (cursor.getCount() <= 0) {
			cursor.close();
			return regType;
		}else{
			if(cursor.moveToNext()) {
				regType= cursor.getString(4);
				cursor.close();
				return regType;
			}
		}
		return regType;

	}

	public String getUsername (String userEmail){

		SQLiteDatabase db = this.getReadableDatabase();

		String username = null;

		Cursor cursor = db.query(TABLE_DENGUE_SITE_USER, COLUMNSUSER, "userEmail = ?", new String[] {userEmail}, null, null, null, null);

		if (cursor.getCount() <= 0) {
			cursor.close();
			return username;
		}else{
			if(cursor.moveToNext()) {
				username= cursor.getString(1);
				cursor.close();
				return username;
			}


		}
		return username;

	}
	
	public String getUserID(String userEmail){
		String userID = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DENGUE_SITE_USER, COLUMNSUSER, "userEmail = ?", new String[] {userEmail}, null, null, null, null);

		if(cursor.getCount() >0) {
			cursor.moveToNext();
			userID = cursor.getString(0);

			return userID;
		}
		return userID;

	}  


	private static final String TABLE_DENGUE_SITE_TASK = "denguesitesTask";

	private static final String KEY_TASK_ID = "taskId";
	private static final String KEY_HISTORY_ID = "historyId";
	private static final String KEY_TASK_FILENAME = "filename";
	private static final String KEY_OFFICER_ID = "officerId";
	private static final String KEY_TASK_DECISION = "taskDecision";
	private static final String KEY_TASK_DESC= "taskDesc";
	private static final String KEY_CLAIM_DATE= "claimDate";

	private static final String[] COLUMNSTASKHISTORY = {KEY_TASK_ID, KEY_HISTORY_ID, KEY_TASK_FILENAME, KEY_OFFICER_ID, KEY_TASK_DECISION, KEY_TASK_DESC, KEY_CLAIM_DATE};

	public void addTasks(TaskHistory taskHistory){ 

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_HISTORY_ID, taskHistory.getHistoryId());
		values.put(KEY_TASK_FILENAME, taskHistory.getFilename());
		values.put(KEY_OFFICER_ID, taskHistory.getOfficerId());
		values.put(KEY_TASK_DECISION, taskHistory.getTaskDecision());
		values.put(KEY_TASK_DESC, taskHistory.getTaskDesc());
		values.put(KEY_CLAIM_DATE, taskHistory.getClaimDate());
		db.insert(TABLE_DENGUE_SITE_TASK, null, values);

		Log.d("save task","taskID: "+taskHistory.getTaskId());
		Log.d("save task","historyID: "+taskHistory.getHistoryId());
		Log.d("save task","historyFilename: "+taskHistory.getFilename());
		Log.d("save task","historyOfficerID: "+taskHistory.getOfficerId());
		Log.d("save task","taskDecision: "+taskHistory.getTaskDecision());
		Log.d("save task","taskDescription: "+taskHistory.getTaskDesc());

		db.close();
	}

	public String[][] getTaskResult(int historyID){

		int i = 0;

		SQLiteDatabase db = this.getReadableDatabase();
		Log.d("getTaskResult","getTaskResult SQL"+historyID);

		String query = "SELECT " + TABLE_DENGUE_SITE_TASK +".* ,"+ TABLE_DENGUE_SITE_USER +"."+KEY_USER_NAME +
				" FROM " + TABLE_DENGUE_SITE_TASK + 
				" LEFT JOIN " + TABLE_DENGUE_SITE_USER +" ON "+ TABLE_DENGUE_SITE_TASK +"."+ KEY_OFFICER_ID +" = "+ TABLE_DENGUE_SITE_USER +"."+ KEY_USER_ID +
				" WHERE "+ TABLE_DENGUE_SITE_TASK +"."+ KEY_HISTORY_ID +" = "+historyID +
				" ORDER BY "+ TABLE_DENGUE_SITE_TASK +"."+KEY_CLAIM_DATE +" DESC";

		Cursor cursor = db.rawQuery(query,null);

		Log.i("getTaskResult", "After rawquery");

		String[][] taskResult = new String[cursor.getCount()][8];

		Log.i("getUserResult", "After users[][] instatiation");

		if(cursor.moveToFirst()){

			while(!cursor.isAfterLast()){
				taskResult[i][0] = cursor.getString(0);
				taskResult[i][1] = cursor.getString(1);
				taskResult[i][2] = cursor.getString(2);
				taskResult[i][3] = cursor.getString(3);
				taskResult[i][4] = cursor.getString(4).equals("1")?"Approved":"Rejected";
				taskResult[i][5] = cursor.getString(5);
				taskResult[i][6] = cursor.getString(6);
				taskResult[i][7] = cursor.getString(7);

				Log.i("insideCursor ", "TASKID: "+cursor.getString(0));
				Log.i("insideCursor ","HISTORYID: "+ cursor.getString(1));
				Log.i("insideCursor ", "FILENAME: "+cursor.getString(2));
				Log.i("insideCursor ", "OFFICERID: "+cursor.getString(3));
				Log.i("insideCursor ", "TASKDECISION: "+cursor.getString(4));
				Log.i("insideCursor ", "TASKDESC: "+cursor.getString(5));
				Log.i("insideCursor ", "CLAIMDATE: "+cursor.getString(6));
				Log.i("insideCursor ", "OFFICERNAME: "+cursor.getString(7));

				i++;
				cursor.moveToNext();
			}

		}

		return taskResult;
	}

	public String getTaskDecision(int historyID){

		int desc = 0;
		String decision = null;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DENGUE_SITE_TASK, COLUMNSTASKHISTORY, "historyID = ?",  new String[] { String.valueOf(historyID) }, null, null, KEY_CLAIM_DATE +" DESC", null);

		if(cursor.getCount() >0) {

			cursor.moveToNext();

			desc = Integer.parseInt(cursor.getString(4));
			if (desc == 1){
				decision = "Approved";
			}else if (desc == 0){
				decision = "Rejected";
			}else{
				decision = "";
			}
			return decision;
		}
		return decision;

	} 

	public String getTaskDesc(int historyID){

		String description = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DENGUE_SITE_TASK, COLUMNSTASKHISTORY, "historyID = ?",  new String[] { String.valueOf(historyID) }, null, null, null, null);

		if(cursor.getCount() >0) {
			cursor.moveToNext();
			description = (cursor.getString(5));
			return description;
		}
		return description;

	} 
}