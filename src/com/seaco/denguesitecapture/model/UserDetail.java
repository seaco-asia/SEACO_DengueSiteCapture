package com.seaco.denguesitecapture.model;

public class UserDetail {

	private String userId;
	private String userName;
	private String userEmail;
	private String userPassword;
	private String userRegtype;
	private String userPhoneNo;
	private String regDate;

	public UserDetail(){}
	
	public UserDetail(String userId, String userName, String userEmail, String userPassword, String userRegtype, String userPhoneNo,  String regDate){
		super();
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.userRegtype = userRegtype;
		this.userPhoneNo = userPhoneNo;
		this.regDate = regDate;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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

	public String getUserPhoneNo() {
		return userPhoneNo;
	}

	public void setUserPhoneNo(String userPhoneNo) {
		this.userPhoneNo = userPhoneNo;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

}