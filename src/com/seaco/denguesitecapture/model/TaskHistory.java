package com.seaco.denguesitecapture.model;

import java.util.Date;

public class TaskHistory {

	private int taskId;
	private int historyId;
	private String filename;
	private int officerId;
	private int taskDecision;
	private String taskDesc;
	private String claimDate;
	
	public TaskHistory(){}
	
	public TaskHistory(int historyId, String filename, int officerId, int taskDecision, String taskDesc, String claimDate){
		super();
		this.historyId = historyId;
		this.filename = filename;
		this.officerId = officerId;
		this.taskDecision = taskDecision;
		this.taskDesc = taskDesc;
		this.claimDate = claimDate;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getOfficerId() {
		return officerId;
	}

	public void setOfficerId(int officerId) {
		this.officerId = officerId;
	}

	public int getTaskDecision() {
		return taskDecision;
	}

	public void setTaskDecision(int taskDecision) {
		this.taskDecision = taskDecision;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getClaimDate() {
		return claimDate;
	}

	public void setClaimDate(String claimDate) {
		this.claimDate = claimDate;
	}

}
