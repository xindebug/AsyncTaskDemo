package com.example.asynctaskdemo;

import java.io.Serializable;

/**
 * @description
 * @author xinge21
 * @time 2013-5-25 下午11:26:34
 * @copyright
 */

public class AppInfo implements Serializable {
	private String packageName;
	private int state = 0;
	private int doneSize;
	private int fileSize = 20;

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getDoneSize() {
		return doneSize;
	}

	public void setDoneSize(int doneSize) {
		this.doneSize = doneSize;
	}

	public AppInfo(String pkname){
		packageName = pkname;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	
}
