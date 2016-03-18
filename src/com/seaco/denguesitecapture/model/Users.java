package com.seaco.denguesitecapture.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seaco.denguesitecapture.activities.Config;

/*This class can use as a common page to display user's information from database. 
	Only call this class to return the value in expected java file.*/

public class Users {

	private String userId;
	private String userName;
	private String userEmail;
	private String userPassword;
	private String userRegtype;
	private String userPhoneNo;

	public String JSON_STRING;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String string) {
		this.userId = string;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserRegtype() {
		return userRegtype;
	}
	public void setUserRegtype(String userRegtype) {
		this.userRegtype = userRegtype;
	}
	public String getUserPhoneNo() {
		return userPhoneNo;
	}
	public void setUserPhoneNo(String userPhoneNo) {
		this.userPhoneNo = userPhoneNo;
	}
	public static Users myMethod(String json) throws JSONException {

		JSONObject jsonObject = new JSONObject(json);
		JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
		JSONObject c = result.getJSONObject(0);

		Users u = new Users();
		u.setUserId(c.getString(Config.KEY_USER_ID));
		u.setUserName(c.getString(Config.KEY_USER_NAME));
		u.setUserEmail(c.getString(Config.KEY_USER_EMAIL));
		u.setUserRegtype(c.getString(Config.KEY_USER_Regtype));
		u.setUserPhoneNo(c.getString(Config.KEY_USER_PHONENO));
		return u;
	}
}