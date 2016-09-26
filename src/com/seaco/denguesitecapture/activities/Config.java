package com.seaco.denguesitecapture.activities;


public class Config {

	//Address of our scripts was here
	public static final String URL_MAIN 	  					= "https://dengueapps.appspot.com/"; //will be replace into octopus.seaco.asia. if your are using LAN connection, please put your IP Address here
	public static final String URL_ADD 		  					= URL_MAIN +"insert_user.php";
	public static final String URL_CHECK_USER 					= URL_MAIN +"check_user.php";
	public static final String URL_UPLOAD     					= URL_MAIN +"upload.php";
	public static final String URL_UPLOAD_OFFLINE     			= URL_MAIN +"insert_photoOffline.php";
	public static final String URL_UPLOAD_OFFLINE_TEST     		= URL_MAIN +"uploadOffline.php";
	public static final String URL_GET_ALL_PHOTOS				= URL_MAIN +"getAll_photosPagination.php";//getAll_photos.php
	public static final String URL_GET_ALL_PHOTOS_PAGE			= URL_MAIN +"getAll_photosCount.php";
	public static final String URL_GET_ALL_PHOTOS_CLAIM			= URL_MAIN +"getAll_photosClaimPagination.php";//getAll_photosClaim.php
	public static final String URL_GET_ALL_PHOTOS_CLAIM_PAGE	= URL_MAIN +"getAll_photosClaimCount.php";
	public static final String URL_GET_PER_PHOTOS				= URL_MAIN +"getSingle_photoPagination.php";//getSingle_Photo.php?userID=
	public static final String URL_GET_PER_PHOTOS_PAGE			= URL_MAIN +"getSingle_photoCount.php";
	public static final String URL_ADD_TASK						= URL_MAIN +"insert_task.php";
	public static final String URL_ADD_DUPLICATE				= URL_MAIN +"insert_log.php";
	public static final String URL_ADD_NOTRELEVANT				= URL_MAIN +"insert_logTask.php";
	public static final String URL_GET_PER_TASK					= URL_MAIN +"getSingle_Task.php?historyID=";
	public static final String URL_GET_DETAIL_TASK 				= URL_MAIN +"getDetail_Task.php?historyID=";
	public static final String URL_GET_ALL_UPLOADED 			= URL_MAIN +"getAll_photosUpload.php";
	public static final String URL_GET_ALL_UPLOADED_COMM   		= URL_MAIN +"getAll_photosUploadS.php?userID=";
	public static final String URL_GET_SITELIST 				= URL_MAIN +"getSiteList.php";
	public static final String URL_GET_LOCALITYLIST 			= URL_MAIN +"getLocalityList.php";
	public static final String URL_GET_ALL_CASES				= URL_MAIN +"getAll_CasePagination.php";//getAll case
	public static final String URL_GET_ALL_CASES_PAGE			= URL_MAIN +"getAll_CaseCount.php";
	public static final String URL_GET_ALL_MOSQUITO_TRAP		= URL_MAIN +"getAll_MosquitoTrapPagination.php";//getAll mosquito trap
	public static final String URL_GET_ALL_MOSQUITO_TRAP_PAGE	= URL_MAIN +"getAll_MosquitoTrapCount.php";
	public static final String URL_GET_PER_MOSQUITO_TRAP		= URL_MAIN +"getSingle_MosquitoTrapPagination.php";//getAll mosquito trap
	public static final String URL_GET_PER_MOSQUITO_TRAP_PAGE	= URL_MAIN +"getSingle_MosquitoTrapCount.php";
	/*
	 * 
		public static final String URL_ADD 		  			= URL_MAIN +"dengueSites/insert_user.php";
    	public static final String URL_CHECK_USER 			= URL_MAIN +"dengueSites/check_user.php";
    	public static final String URL_UPLOAD     			= URL_MAIN +"dengueSites/upload.php";
    	public static final String URL_GET_ALL_PHOTOS		= URL_MAIN +"dengueSites/getAll_photos.php";
    	public static final String URL_GET_PER_PHOTOS		= URL_MAIN +"dengueSites/getSingle_photo.php?userID=";
    	public static final String URL_ADD_TASK				= URL_MAIN +"dengueSites/insert_task.php";
    	public static final String URL_GET_PER_TASK			= URL_MAIN +"dengueSites/getSingle_Task.php?historyID=";
    	public static final String URL_GET_DETAIL_TASK 		= URL_MAIN +"dengueSites/getDetail_Task.php?historyID=";
    	public static final String URL_GET_ALL_UPLOADED 	= URL_MAIN +"dengueSites/getAll_photosUpload.php";
	 *
	 */

	//Keys that will be used to send the request to php scripts
	public static final String KEY_USER_ID = "userId";
	public static final String KEY_USER_NAME = "userName";
	public static final String KEY_USER_EMAIL = "userEmail";
	public static final String KEY_USER_PASSWORD = "userPassword";
	public static final String KEY_USER_Regtype = "userRegtype";
	public static final String KEY_USER_PHONENO = "userPhoneNo";
	public static final String KEY_USER_REGDATE = "regDate";
	
	public static final String KEY_ID = "id";
	public static final String KEY_FILENAME = "filename";

	public static final String KEY_TASK_HISTORYID = "historyId";
	public static final String KEY_TASK_FILENAME = "filename";
	public static final String KEY_TASK_OFFICERID = "officerId";
	public static final String KEY_TASK_TASKDECISION = "taskDecision";
	public static final String KEY_TASK_TASKDESC = "taskDesc";
	public static final String KEY_TASK_CLAIMDATE = "claimDate";

	public static final String KEY_SEARCH_MAIN = "selectedMain";
	public static final String KEY_SEARCH_SUB = "subValues";
	public static final String KEY_SEARCH_CURRPAGE = "currentPage";

	//JSON Tags
	public static final String TAG_JSON_ARRAY="result";
	public static final String TAG_USER_ID = "userId";
	public static final String TAG_USER_NAME = "userName";
	public static final String TAG_USER_EMAIL = "userEmail";
	public static final String TAG_USER_PASSWORD = "userPassword";
	public static final String TAG_USER_Regtype = "userRegtype";

	public static final String TAG_JSON_ARRAY_PHOTOS="result";
	public static final String TAG_ID = "id";
	public static final String TAG_FILENAME = "filename";
	public static final String TAG_LATITUDE = "latitude";
	public static final String TAG_LONGITUDE = "longitude";
	public static final String TAG_SITE_CHOICE = "siteChoice";
	public static final String TAG_IMEI = "imei";
	public static final String TAG_INSERT_DATE = "insertDate";
	public static final String TAG_PHOTO_DESC = "photoDesc";
	public static final String TAG_INSERTBY = "insertBy";
	public static final String TAG_PATH = "path";
	public static final String TAG_STATUS = "status";
	public static final String TAG_OFFICER_COMMENT = "offDesc";
	public static final String TAG_REPORT_BY = "userName";
	public static final String TAG_PHONE_NO = "userPhoneNo";
	public static final String TAG_HOUSE_FULL_ADDRESS = "house_fullAddress";
	public static final String TAG_HOUSE_LOCALITY_NAME = "locality_name";
	public static final String TAG_HOUSE_LOCALITY_OTHER = "locality_other";
	public static final String TAG_CASE_PATIENT_NAME = "case_patientName";
	public static final String TAG_IS_DUPLICATE = "isDuplicate";

	public static final String TAG_JSON_ARRAY_TASK="result";
	public static final String TAG_TASK_TASKID = "taskId";
	public static final String TAG_TASK_HISTORYID = "historyId";
	public static final String TAG_TASK_FILENAME = "filename";
	public static final String TAG_TASK_OFFICERID = "officerId";
	public static final String TAG_TASK_TASKDECISION = "taskDecision";
	public static final String TAG_TASK_TASKDESC = "taskDesc";
	public static final String TAG_TASK_CLAIMDATE = "claimDate";
	public static final String TAG_TASK_OFFICERNAME = "userName";

	public static final String TAG_JSON_ARRAY_SITELIST="result";
	public static final String TAG_SITE_ID = "id";
	public static final String TAG_SITENAME_ENG = "sitename_eng";
	public static final String TAG_SITENAME_MYS = "sitename_mys";

	public static final String TAG_JSON_ARRAY_LOCALITYLIST="result";
	public static final String TAG_LOCALITY_ID = "id";
	public static final String TAG_LOCALITYNAME = "locality_name";

	//employee id to pass with intent
	public static final String USER_EMAIL = "userEmail";
	public static final String USER_PASSWORD = "userPassword";

	//
	public static final String TAG_JSON_ARRAY_UPLOADED ="result";
	public static final String TAG_IMAGE_URL = "path";
	public static final String TAG_IMAGE_FILENAME = "filename";

	//
	public static final String TAG_JSON_ARRAY_PAGE_COUNT="result";
	public static final String TAG_PAGE_COUNT = "pageCount";

	// public static final String TAG_PUBLISHER = "publisher";

	/*public static final String URL_GET_JASON		= URL_MAIN+"getSingle_TaskJson.php?id=";

    public static final String id= "features_list_properties_OBJECTID";
    public static final String name = "features_list_properties_NAME";
    public static final String address = "features_list_properties_ADDRESS";*/
}