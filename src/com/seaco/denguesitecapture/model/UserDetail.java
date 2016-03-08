package com.seaco.denguesitecapture.model;

public class UserDetail {

	private int userId;
	private String userName;
	private String userEmail;
	private String userPassword;
	private String userRegtype;

	public UserDetail(){}
	
	public UserDetail(String userName, String userEmail, String userPassword, String userRegtype){
		super();
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.userRegtype = userRegtype;
	}



	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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


}