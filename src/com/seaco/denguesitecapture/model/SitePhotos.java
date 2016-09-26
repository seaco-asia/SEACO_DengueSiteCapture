package com.seaco.denguesitecapture.model;

public class SitePhotos {

	private int id;
	private String filename;
	private String filename_temp;
	private String latitude;
	private String longitude;
	private String siteChoice; 
	private String reportChoice; 
	private String imei; 
	private String insertDate;
	private String photoDesc;
	private String insertBy; 
	private String locality_name;
	private String house_mukim;
	private String house_fullAddress;
	private String locality_other;
	private String locality_ind_type; 
	private String locality_ind_name; 
	private String isDuplicate;
	private String flagUpload;
	private String path;
	private String case_patientName;

	public SitePhotos(){}

	public SitePhotos(String filename, String filename_temp, String latitude, String longitude, String siteChoice, String reportChoice, 
			String imei, String insertDate, String photoDesc, String insertBy,String locality_name, String house_mukim, String house_fullAddress, 
			String locality_other, String locality_ind_type, String locality_ind_name, String isDuplicate, String flagUpload, String path, 
			String case_patientName){
		super();
		this.filename = filename;
		this.filename_temp = filename_temp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.siteChoice = siteChoice;
		this.reportChoice = reportChoice;
		this.imei = imei;
		this.insertDate = insertDate;
		this.photoDesc = photoDesc;
		this.insertBy = insertBy;
		this.locality_name = locality_name;
		this.house_mukim = house_mukim;
		this.house_fullAddress = house_fullAddress;
		this.locality_other = locality_other;
		this.locality_ind_type = locality_ind_type;
		this.locality_ind_name = locality_ind_name;
		this.isDuplicate = isDuplicate;
		this.flagUpload = flagUpload;
		this.path = path;
		this.case_patientName = case_patientName;

	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getFilename(){
		return filename;
	}

	public void setFilename(String filename){
		this.filename = filename;
	}

	public String getFilename_temp() {
		return filename_temp;
	}

	public void setFilename_temp(String filename_temp) {
		this.filename_temp = filename_temp;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getSiteChoice() {
		return siteChoice;
	}

	public void setSiteChoice(String siteChoice) {
		this.siteChoice = siteChoice;
	}

	public String getReportChoice() {
		return reportChoice;
	}

	public void setReportChoice(String reportChoice) {
		this.reportChoice = reportChoice;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	public String getPhotoDesc() {
		return photoDesc;
	}

	public void setPhotoDesc(String photoDesc) {
		this.photoDesc = photoDesc;
	}

	public String getInsertBy() {
		return insertBy;
	}

	public void setInsertBy(String insertBy) {
		this.insertBy = insertBy;
	}

	public String getLocality_name() {
		return locality_name;
	}

	public void setLocality_name(String locality_name) {
		this.locality_name = locality_name;
	}

	public String getHouse_mukim() {
		return house_mukim;
	}

	public void setHouse_mukim(String house_mukim) {
		this.house_mukim = house_mukim;
	}

	public String getHouse_fullAddress() {
		return house_fullAddress;
	}

	public void setHouse_fullAddress(String house_fullAddress) {
		this.house_fullAddress = house_fullAddress;
	}

	public String getLocality_other() {
		return locality_other;
	}

	public void setLocality_other(String locality_other) {
		this.locality_other = locality_other;
	}

	public String getLocality_ind_type() {
		return locality_ind_type;
	}

	public void setLocality_ind_type(String locality_ind_type) {
		this.locality_ind_type = locality_ind_type;
	}

	public String getLocality_ind_name() {
		return locality_ind_name;
	}

	public void setLocality_ind_name(String locality_ind_name) {
		this.locality_ind_name = locality_ind_name;
	}

	public String getIsDuplicate() {
		return isDuplicate;
	}

	public void setIsDuplicate(String isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public String getFlagUpload() {
		return flagUpload;
	}

	public void setFlagUpload(String flagUpload) {
		this.flagUpload = flagUpload;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCase_patientName() {
		return case_patientName;
	}

	public void setCase_patientName(String case_patientName) {
		this.case_patientName = case_patientName;
	}
}
