package com.seaco.denguesitecapture.model;

public class SitePhotos {

	private int id;
	private String filename;
	private String gps;
	private String photoDesc;
	
	public SitePhotos(){}
	
	public SitePhotos(String filename, String gps, String photoDesc){
		super();
		this.filename = filename;
		this.gps = gps;
		this.photoDesc = photoDesc;
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
	
	public String getGps(){
		return gps;
	}
	
	public void setGps(String gps){
		this.gps = gps;
	}

	public String getPhotoDesc() {
		return photoDesc;
	}

	public void setPhotoDesc(String photoDesc) {
		this.photoDesc = photoDesc;
	}
	
}
