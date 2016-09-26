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

	private static final String TAG = "MySQLiteHelper";

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
				"filename_temp TEXT, " +
				"latitude TEXT, " +
				"longitude TEXT, " +
				"siteChoice TEXT, " +
				"reportChoice TEXT, " +
				"imei TEXT, " +
				"insertDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
				"photoDesc TEXT, " +
				"insertBy TEXT, " +
				"locality_name TEXT, " +
				"house_mukim TEXT, " +
				"house_fullAddress TEXT, " +
				"locality_other TEXT, " +
				"locality_ind_type TEXT, " +
				"locality_ind_name TEXT, " +
				"isDuplicate TEXT, " +
				"flagUpload TEXT," +
				"path TEXT,"+
				"case_patientName TEXT)";
		db.execSQL(CREATE_DENGUE_SITE_TABLE);

		/*String CREATE_DENGUE_SITE_USER_TABLE = "CREATE TABLE denguesitesUser (" +
				"userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"userName TEXT, " +
				"userEmail TEXT, " +
				"userPassword TEXT, "+
				"userRegtype TEXT)";
		db.execSQL(CREATE_DENGUE_SITE_USER_TABLE);*/

		String CREATE_DENGUE_SITE_USER_TABLE = "CREATE TABLE denguesitesUser (" +
				"userId INTEGER, " +
				"userName TEXT, " +
				"userEmail TEXT, " +
				"userPassword TEXT, "+
				"userRegtype TEXT, "+
				"userPhoneNo TEXT, "+
				"regDate DATETIME DEFAULT CURRENT_TIMESTAMP)";
		db.execSQL(CREATE_DENGUE_SITE_USER_TABLE);

		/*String CREATE_DENGUE_SITE_TASK_TABLE = "CREATE TABLE denguesitesTask (" +
				"taskId INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"historyId INTEGER, " +
				"filename TEXT, " +
				"officerId INTEGER, "+
				"taskDecision INTEGER, " +
				"taskDesc TEXT, " +
				"claimDate DATETIME DEFAULT CURRENT_TIMESTAMP)";
		db.execSQL(CREATE_DENGUE_SITE_TASK_TABLE);*/

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS denguesites");
		this.onCreate(db);
	}

	private static final String TABLE_DENGUESITES = "denguesites";

	private static final String KEY_ID = "id";
	private static final String KEY_FILENAME = "filename";
	private static final String KEY_FILENAME_TEMP = "filename_temp";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_SITECHOICE = "siteChoice";
	private static final String KEY_REPORTCHOICE = "reportChoice";
	private static final String KEY_IMEI = "imei";
	private static final String KEY_INSERTDATE = "insertDate";
	private static final String KEY_PHOTODESC = "photoDesc";
	private static final String KEY_INSERTBY = "insertBy";
	private static final String KEY_LOCALITY_NAME = "locality_name";
	private static final String KEY_HOUSE_MUKIM = "house_mukim";
	private static final String KEY_HOUSE_FULLADDRESS = "house_fullAddress";
	private static final String KEY_LOCALITY_OTHER = "locality_other";
	private static final String KEY_LOCALITY_IND_TYPE = "locality_ind_type";
	private static final String KEY_LOCALITY_IND_NAME = "locality_ind_name";
	private static final String KEY_ISDUPLICATE = "isDuplicate";
	private static final String KEY_FLAGUPLOAD = "flagUpload";
	private static final String KEY_PATH = "path";
	private static final String KEY_CASENAME = "case_patientName";

	private static final String[] COLUMNS = {KEY_ID, KEY_FILENAME, KEY_FILENAME_TEMP, KEY_LATITUDE, KEY_LONGITUDE, KEY_SITECHOICE, KEY_REPORTCHOICE, KEY_IMEI, KEY_INSERTDATE, 
		KEY_PHOTODESC, KEY_INSERTBY, KEY_LOCALITY_NAME, KEY_HOUSE_MUKIM, KEY_HOUSE_FULLADDRESS, KEY_LOCALITY_OTHER, KEY_LOCALITY_IND_TYPE, KEY_LOCALITY_IND_NAME, 
		KEY_ISDUPLICATE, KEY_FLAGUPLOAD, KEY_PATH, KEY_CASENAME};

	public void addPicture(SitePhotos photo){ 

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FILENAME, photo.getFilename());
		values.put(KEY_FILENAME_TEMP, photo.getFilename_temp());
		values.put(KEY_LATITUDE, photo.getLatitude());
		values.put(KEY_LONGITUDE, photo.getLongitude());
		values.put(KEY_SITECHOICE, photo.getSiteChoice());
		values.put(KEY_REPORTCHOICE, photo.getReportChoice());
		values.put(KEY_IMEI, photo.getImei());
		values.put(KEY_INSERTDATE, photo.getInsertDate());
		values.put(KEY_PHOTODESC, photo.getPhotoDesc());
		values.put(KEY_INSERTBY, photo.getInsertBy());
		values.put(KEY_LOCALITY_NAME, photo.getLocality_name());
		values.put(KEY_HOUSE_MUKIM, photo.getHouse_mukim());
		values.put(KEY_HOUSE_FULLADDRESS, photo.getHouse_fullAddress());
		values.put(KEY_LOCALITY_OTHER, photo.getLocality_other());
		values.put(KEY_LOCALITY_IND_TYPE, photo.getLocality_ind_type());
		values.put(KEY_LOCALITY_IND_NAME, photo.getLocality_name());
		values.put(KEY_ISDUPLICATE, photo.getIsDuplicate());
		values.put(KEY_FLAGUPLOAD, photo.getFlagUpload());
		values.put(KEY_PATH, photo.getPath());
		values.put(KEY_CASENAME, photo.getCase_patientName());

		db.insert(TABLE_DENGUESITES, null, values);

		db.close();
	}

	public void updatePicture(String id){ 

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FLAGUPLOAD, "Y");
		db.update(TABLE_DENGUESITES, values, "id = ?", new String[]{id});

		db.close();
	}


	/*public SitePhotos getPhoto(int id){

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DENGUESITES, COLUMNS, " id = ?", new String[] {String.valueOf(id)}, null, null, null, null);

		if(cursor != null){
			cursor.moveToFirst();
		}

		SitePhotos photo = new SitePhotos();
		photo.setId(Integer.parseInt(cursor.getString(0)));
		photo.setFilename(cursor.getString(1));
		//photo.setGps(cursor.getString(2));
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
				//photo.setGps(cursor.getString(2));
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
	}*/

	private static final String TABLE_DENGUE_SITE_USER = "denguesitesUser";

	private static final String KEY_USER_ID = "userId";
	private static final String KEY_USER_NAME = "userName";
	private static final String KEY_USER_EMAIL = "userEmail";
	private static final String KEY_USER_PASSWORD = "userPassword";
	private static final String KEY_USER_Regtype = "userRegtype";
	private static final String KEY_USER_PHONENO ="userPhoneNo"; 
	private static final String KEY_USER_REGDATE ="regDate";


	private static final String[] COLUMNSUSER = {KEY_USER_ID, KEY_USER_NAME, KEY_USER_EMAIL, KEY_USER_PASSWORD,KEY_USER_Regtype, KEY_USER_PHONENO, KEY_USER_REGDATE};

	public void addUser(UserDetail userDetail){ 

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID, userDetail.getUserId());
		values.put(KEY_USER_NAME, userDetail.getUserName());
		values.put(KEY_USER_EMAIL, userDetail.getUserEmail());
		values.put(KEY_USER_PASSWORD, userDetail.getUserPassword());
		values.put(KEY_USER_Regtype, userDetail.getUserRegtype());
		values.put(KEY_USER_PHONENO, userDetail.getUserPhoneNo());
		values.put(KEY_USER_REGDATE, userDetail.getRegDate());
		db.insert(TABLE_DENGUE_SITE_USER, null, values);

		db.close();
	}

	/*public List<UserDetail> getAllUsers(){

		List<UserDetail> users = new LinkedList<UserDetail>();

		String query = "SELECT * FROM " + TABLE_DENGUE_SITE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		UserDetail user = null;

		if(cursor.moveToFirst()){
			do{
				user = new UserDetail();
				user.setUserId(cursor.getString(0));
				user.setUserName(cursor.getString(1));
				user.setUserEmail(cursor.getString(2));
				user.setUserPassword(cursor.getString(3));
				user.setUserRegtype(cursor.getString(4));
				user.setUserPhoneNo(cursor.getString(5));
				user.setRegDate(cursor.getString(6));
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

		String[][] users = new String[cursor.getCount()][7];

		Log.i("getUserResult", "After users[][] instatiation");

		if(cursor.moveToFirst()){

			while(!cursor.isAfterLast()){
				users[i][0] = cursor.getString(0);
				users[i][1] = cursor.getString(1);
				users[i][2] = cursor.getString(2);
				users[i][3] = cursor.getString(3);
				users[i][4] = cursor.getString(4);
				users[i][3] = cursor.getString(5);
				users[i][4] = cursor.getString(6);

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
	 */
	/*	public String getRegType (String userEmail){

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

	}*/

	/*START OFFLINE MODE*/

	//start for the firstime login, need to be online. User record will be insert to local device
	public boolean checkUsernInsertInDB (String userId, String userName, String userEmail, String userPassword, String userRegType, String userPhoneNo, String regDate){

		Log.d(TAG,"1 and userPhoneNo " +userPhoneNo+ "and userPassword: ["+userPassword+"] and regDate: ["+regDate+"]");
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DENGUE_SITE_USER, COLUMNSUSER, "userPhoneNo = ? AND userPassword = ?", new String[] {userPhoneNo,userPassword}, null, null, null, null);
		Log.d(TAG,"2");
		if (cursor.getCount() <= 0) {

			//IF RECORD NOT EXIST, INSERT
			addUser(new UserDetail(userId,userName, userEmail, userPassword, userRegType, userPhoneNo, regDate));

			Log.d(TAG,"3");

			cursor.close();
			return false;

		}else{
			Log.d(TAG,"4");

			if(cursor.moveToNext()) {
				Log.d(TAG,"Data 1: "+cursor.getString(0));
				Log.d(TAG,"Data 2: "+cursor.getString(1));
				Log.d(TAG,"Data 3: "+cursor.getString(2));
				Log.d(TAG,"Data 4: "+cursor.getString(3));
				Log.d(TAG,"Data 5: "+cursor.getString(4));
				Log.d(TAG,"Data 6: "+cursor.getString(5));
				Log.d(TAG,"Data 7: "+cursor.getString(6));
			}


			cursor.close();
			return true;

		}

	}
	//end for the firstime login, need to be online. User record will be insert to local device

	//start check if user id already exist in local device or not
	public boolean checkUserInDB (String userPassword, String userPhoneNo){

		Log.d(TAG,"checkUserInDB1: and userPhoneNo " +userPhoneNo+ "and userPassword: ["+userPassword+"]");
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DENGUE_SITE_USER, COLUMNSUSER, "userPhoneNo = ? AND userPassword = ?", new String[] {userPhoneNo,userPassword}, null, null, null, null);
		Log.d(TAG,"checkUserInDB: 2");
		if (cursor.getCount() <= 0) {

			//IF RECORD NOT EXIST, INSERT
			//addUser(new UserDetail(userId,userName, userEmail, userPassword, userRegType, userPhoneNo, regDate));

			Log.d(TAG,"checkUserInDB: 3");

			cursor.close();
			return false;

		}else{
			Log.d(TAG,"checkUserInDB: 4");

			if(cursor.moveToNext()) {
				Log.d(TAG,"Data 1: "+cursor.getString(0));
				Log.d(TAG,"Data 2: "+cursor.getString(1));
				Log.d(TAG,"Data 3: "+cursor.getString(2));
				Log.d(TAG,"Data 4: "+cursor.getString(3));
				Log.d(TAG,"Data 5: "+cursor.getString(4));
				Log.d(TAG,"Data 6: "+cursor.getString(5));
				Log.d(TAG,"Data 7: "+cursor.getString(6));
			}


			cursor.close();
			return true;

		}

	}
	//end check if user id already exist in local device or not

	//start retrieve user data from local device
	public UserDetail getUserDetail(String userPassword, String userPhoneNo){

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DENGUE_SITE_USER, COLUMNSUSER, "userPhoneNo = ? AND userPassword = ?", new String[] {userPhoneNo,userPassword}, null, null, null, null);

		if(cursor != null){
			cursor.moveToFirst();
		}

		UserDetail userDetail = new UserDetail();
		userDetail.setUserId((cursor.getString(0)));
		userDetail.setUserName(cursor.getString(1));
		userDetail.setUserEmail(cursor.getString(2));
		userDetail.setUserPassword(cursor.getString(3));
		userDetail.setUserRegtype(cursor.getString(4));
		userDetail.setUserPhoneNo(cursor.getString(5));
		userDetail.setRegDate(cursor.getString(6));
		return userDetail;

	}
	//end retrieve user data from local device

	//start retrieve photo data from local device
	public List<SitePhotos> getSitePhotos(String userId){

		List<SitePhotos> photos = new LinkedList<SitePhotos>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DENGUESITES, COLUMNS, "insertBy = ?", new String[] {userId}, null, null, null, null);

		//SitePhotos photo = null;
		SitePhotos sitePhotos =new SitePhotos();

		if(cursor.moveToFirst()){
			do{
				//sitePhotos.setUserId((cursor.getString(0)));
				sitePhotos.setId(Integer.parseInt(cursor.getString(0)));
				sitePhotos.setFilename(cursor.getString(1));
				sitePhotos.setFilename_temp(cursor.getString(2));
				sitePhotos.setLatitude(cursor.getString(3));
				sitePhotos.setLongitude(cursor.getString(4));
				sitePhotos.setSiteChoice(cursor.getString(5));
				sitePhotos.setReportChoice(cursor.getString(6));
				sitePhotos.setImei(cursor.getString(7));
				sitePhotos.setInsertBy(cursor.getString(10));
				sitePhotos.setLocality_name(cursor.getString(11));
				sitePhotos.setHouse_mukim(cursor.getString(12));
				sitePhotos.setHouse_fullAddress(cursor.getString(13));
				sitePhotos.setLocality_other(cursor.getString(14));
				sitePhotos.setLocality_ind_type(cursor.getString(15));
				sitePhotos.setLocality_ind_name(cursor.getString(16));
				sitePhotos.setIsDuplicate(cursor.getString(17));
				sitePhotos.setFlagUpload(cursor.getString(18));
				sitePhotos.setPath(cursor.getString(19));
				sitePhotos.setCase_patientName(cursor.getString(20));
				//return sitePhotos;

				photos.add(sitePhotos);

				Log.d(TAG,"HISTORY ID:["+sitePhotos.getId()+"]");
				Log.d(TAG,"Filename:["+sitePhotos.getFilename()+"]");
				Log.d(TAG,"Filename Temp:["+sitePhotos.getFilename_temp()+"]");
				Log.d(TAG,"Latitude:["+sitePhotos.getLatitude()+"]");
				Log.d(TAG,"Longitude:["+sitePhotos.getLongitude()+"]");
				Log.d(TAG,"SiteChoice:["+sitePhotos.getSiteChoice()+"]");
				Log.d(TAG,"ReportChoice:["+sitePhotos.getReportChoice()+"]");
				Log.d(TAG,"Imei:["+sitePhotos.getImei()+"]");
				Log.d(TAG,"InsertBy:["+sitePhotos.getInsertBy()+"]");
				Log.d(TAG,"InsertDate:["+sitePhotos.getInsertDate()+"]");
				Log.d(TAG,"Locality_name:["+sitePhotos.getLocality_name()+"]");
				Log.d(TAG,"House_mukim:["+sitePhotos.getHouse_mukim()+"]");
				Log.d(TAG,"House_fullAddress:["+sitePhotos.getHouse_fullAddress()+"]");
				Log.d(TAG,"Locality_othe:["+sitePhotos.getLocality_other()+"]");
				Log.d(TAG,"Locality_ind_type:["+sitePhotos.getLocality_ind_type()+"]");
				Log.d(TAG,"Locality_ind_name:["+sitePhotos.getLocality_ind_name()+"]");
				Log.d(TAG,"IsDuplicate:["+sitePhotos.getIsDuplicate()+"]");
				Log.d(TAG,"FlagUpload:["+sitePhotos.getFlagUpload()+"]");
				Log.d(TAG,"Path:["+sitePhotos.getPath()+"]");
			}while (cursor.moveToNext());
		}

		return photos;
	}

	public String[][] getSingle_SitePhotosNotUpload(String userId, Integer selectedMain, String subValues, int page){

		Log.d(TAG,"userID["+ userId+"] selectedMain["+selectedMain+"] subValues ["+ subValues+"] currentPage["+page+"]");

		//set display per page
		int per_page = 16; 

		//set start page
		int start = (page-1)*per_page;

		int i = 0;

		SQLiteDatabase db = this.getReadableDatabase();

		String query = null;

		if(selectedMain == 0){
			//SQL 1
			query  = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N'  ORDER BY id desc limit "+start+","+per_page+"";
		}

		/*if (selectedMain == "1"){
			//SQL 2
			$sql = "SELECT * FROM denguesites WHERE insertBy = '$insertBy' AND reportChoice = 'subValues' order by id desc limit $start,$per_page";
		}*/

		if (selectedMain == 1){
			//SQL 4
			query = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N' AND locality_name ='"+subValues+"' ORDER BY id desc limit "+start+","+per_page+"";
		}

		if (selectedMain == 2){
			//SQL 3
			query = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N' AND house_mukim ='"+subValues+"' ORDER BY id desc limit "+start+","+per_page+"";
		}

		if (selectedMain == 3){
			//SQL 5
			if (subValues == "9"){
				//String query = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N' AND status IS NULL order by id desc limit $start,$per_page";
			}
			else{
				//String query = "SELECT * FROM denguesites WHERE insertBy = '$insertBy' AND reportChoice = '1' AND status = 'subValues' order by id desc limit $start,$per_page";
			}
		}

		//String query = "SELECT * FROM " + TABLE_DENGUESITES;
		Cursor cursor = db.rawQuery(query,null);

		Log.i("getPhotosResult", "After rawquery");

		String[][] photos = new String[cursor.getCount()][21];

		Log.i("getPhotosResult", "After photos[][] instatiation");

		if(cursor.moveToFirst()){

			while(!cursor.isAfterLast()){
				photos[i][0] = cursor.getString(0);
				photos[i][1] = cursor.getString(1);
				photos[i][2] = cursor.getString(2);
				photos[i][3] = cursor.getString(3);
				photos[i][4] = cursor.getString(4);
				photos[i][5] = cursor.getString(5);
				photos[i][6] = cursor.getString(6);
				photos[i][7] = cursor.getString(7);
				photos[i][8] = cursor.getString(8);
				photos[i][9] = cursor.getString(9);
				photos[i][10] = cursor.getString(10);
				photos[i][11] = cursor.getString(11);
				photos[i][12] = cursor.getString(12);
				photos[i][13] = cursor.getString(13);
				photos[i][14] = cursor.getString(14);
				photos[i][15] = cursor.getString(15);
				photos[i][16] = cursor.getString(16);
				photos[i][17] = cursor.getString(17);
				photos[i][18] = cursor.getString(18);
				photos[i][19] = cursor.getString(19);
				photos[i][20] = cursor.getString(20);

				Log.i("insideCursor", cursor.getString(0));
				//Log.i("insideCursor", cursor.getString(1));
				//Log.i("insideCursor", cursor.getString(2));

				i++;
				cursor.moveToNext();
			}

		}

		return photos;
	}

	public int getSingle_SitePhotosNotUploadCount(String userId, Integer selectedMain, String subValues){

		Log.d(TAG,"subValues:["+subValues+"]");

		//set display per page
		int per_page = 16; 

		int i = 0;

		SQLiteDatabase db = this.getReadableDatabase();

		String query = null;

		if(selectedMain == 0){
			//SQL 1
			query  = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N'  ORDER BY id";
		}

		/*if (selectedMain == "1"){
			//SQL 2
			$sql = "SELECT * FROM denguesites WHERE insertBy = '$insertBy' AND reportChoice = 'subValues' order by id desc limit $start,$per_page";
		}*/

		if (selectedMain == 1){
			//SQL 4
			query = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N' AND locality_name ='"+subValues+"' ORDER BY id";
		}

		if (selectedMain == 2){
			//SQL 3
			query = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N' AND house_mukim ='"+subValues+"' ORDER BY id";
		}

		if (selectedMain == 3){
			//SQL 5
			if (subValues == "9"){
				//String query = "SELECT * FROM denguesites WHERE insertBy ="+userId+" AND flagUpload = 'N' AND status IS NULL order by id desc limit $start,$per_page";
			}
			else{
				//String query = "SELECT * FROM denguesites WHERE insertBy = '$insertBy' AND reportChoice = '1' AND status = 'subValues' order by id desc limit $start,$per_page";
			}
		}

		Cursor cursor = db.rawQuery(query,null);

		Log.i("getPhotosResult", "After rawquery");

		int count = cursor.getCount();
		int pages = 0;

		if(count % per_page == 0){
			pages =  count/per_page;
		}else{
			pages =  (count/per_page)+1;
		}
		//String[][] photos = new String[cursor.getCount()][18];

		Log.i("getPhotosResultPages", "Total ["+pages+"]");

		return pages;
	}

	//start retrieve user data from local device
	public SitePhotos getPhotoPerId(String id){

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DENGUESITES, COLUMNS, "id = ?", new String[] {id}, null, null, null, null);

		if(cursor != null){
			cursor.moveToFirst();
		}

		SitePhotos sitePhotos = new SitePhotos();
		sitePhotos.setId(Integer.parseInt(cursor.getString(0)));
		sitePhotos.setFilename(cursor.getString(1));
		sitePhotos.setFilename_temp(cursor.getString(2));
		sitePhotos.setLatitude(cursor.getString(3));
		sitePhotos.setLongitude(cursor.getString(4));
		sitePhotos.setSiteChoice(cursor.getString(5));
		sitePhotos.setReportChoice(cursor.getString(6));
		sitePhotos.setImei(cursor.getString(7));
		sitePhotos.setInsertDate(cursor.getString(8));
		sitePhotos.setPhotoDesc(cursor.getString(9));
		sitePhotos.setInsertBy(cursor.getString(10));
		sitePhotos.setLocality_name(cursor.getString(11));
		sitePhotos.setHouse_mukim(cursor.getString(12));
		sitePhotos.setHouse_fullAddress(cursor.getString(13));
		sitePhotos.setLocality_other(cursor.getString(14));
		sitePhotos.setLocality_ind_type(cursor.getString(15));
		sitePhotos.setLocality_ind_name(cursor.getString(16));
		sitePhotos.setIsDuplicate(cursor.getString(17));
		sitePhotos.setFlagUpload(cursor.getString(18));
		sitePhotos.setPath(cursor.getString(19));
		sitePhotos.setCase_patientName(cursor.getString(20));
		return sitePhotos;

	}
	//end retrieve user data from local device

	/*public String[][] getSitePhotosNotUpload(String userId){

		int i = 0;

		//String query = "SELECT * FROM " + TABLE_DENGUESITES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DENGUESITES, COLUMNS, " insertBy = ? AND flagUpload = ?", new String[] {userId, "N"}, null, null, null, null);
		//Cursor cursor = db.rawQuery(query, null);

		Log.i("getPhotosResult", "After rawquery");

		String[][] photos = new String[cursor.getCount()][18];

		Log.i("getPhotosResult", "After photos[][] instatiation");

		if(cursor.moveToFirst()){

			while(!cursor.isAfterLast()){
				photos[i][0] = cursor.getString(0);
				photos[i][1] = cursor.getString(1);
				photos[i][2] = cursor.getString(2);
				photos[i][3] = cursor.getString(3);
				photos[i][4] = cursor.getString(4);
				photos[i][5] = cursor.getString(5);
				photos[i][6] = cursor.getString(6);
				photos[i][7] = cursor.getString(7);
				photos[i][8] = cursor.getString(10);
				photos[i][9] = cursor.getString(11);
				photos[i][10] = cursor.getString(12);
				photos[i][11] = cursor.getString(13);
				photos[i][12] = cursor.getString(14);
				photos[i][13] = cursor.getString(15);
				photos[i][14] = cursor.getString(16);
				photos[i][15] = cursor.getString(17);
				photos[i][16] = cursor.getString(18);
				photos[i][17] = cursor.getString(19);
//				photos[i][10] = cursor.getString(10);
//				photos[i][11] = cursor.getString(11);
//				photos[i][12] = cursor.getString(12);
//				photos[i][13] = cursor.getString(13);
//				photos[i][14] = cursor.getString(14);
//				photos[i][15] = cursor.getString(15);
//				photos[i][16] = cursor.getString(16);
//				photos[i][17] = cursor.getString(17);
//				photos[i][18] = cursor.getString(18);
//				photos[i][19] = cursor.getString(19);

				Log.i("insideCursor", cursor.getString(0));
				//Log.i("insideCursor", cursor.getString(1));
				//Log.i("insideCursor", cursor.getString(2));

				i++;
				cursor.moveToNext();
			}

		}

		return photos;
	}*/	
	/*public SitePhotos getSitePhotos(String userId){

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DENGUESITES, COLUMNS, "insertBy = ?", new String[] {userId}, null, null, null, null);

		//if(cursor != null){
		//cursor.moveToFirst();
		//}

		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			try {
				for (int i = 0; i < cursor.getCount(); i++) {
					SitePhotos sitePhotos = new SitePhotos();
					//sitePhotos.setUserId((cursor.getString(0)));
					sitePhotos.setFilename(cursor.getString(1));
					sitePhotos.setLatitude(cursor.getString(2));
					sitePhotos.setLongitude(cursor.getString(3));
					sitePhotos.setSiteChoice(cursor.getString(4));
					sitePhotos.setReportChoice(cursor.getString(5));
					sitePhotos.setImei(cursor.getString(6));
					sitePhotos.setInsertBy(cursor.getString(9));
					sitePhotos.setLocality_name(cursor.getString(10));
					sitePhotos.setHouse_mukim(cursor.getString(11));
					sitePhotos.setHouse_fullAddress(cursor.getString(12));
					sitePhotos.setLocality_other(cursor.getString(13));
					sitePhotos.setLocality_ind_type(cursor.getString(14));
					sitePhotos.setLocality_ind_name(cursor.getString(15));
					sitePhotos.setIsDuplicate(cursor.getString(16));
					sitePhotos.setFlagUpload(cursor.getString(17));
					return sitePhotos;

				}

			}finally {
			    cursor.close();
			}
		}
	}*/
	//end retrieve photo data from local device


	/*END OFFLINE MODE*/

	/*private static final String TABLE_DENGUE_SITE_TASK = "denguesitesTask";

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

	} */
}