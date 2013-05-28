package com.example.asynctaskdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @description
 * @author xinge21
 * @time 2013-5-25 下午8:17:15
 * @copyright
 */

public class DownloadTask extends AsyncTask<AppInfo, Integer, Boolean> {
	// 后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型

	// public static final String STAUS_BROADCAST = "STAUS_BROADCAST";
	private static final String TAG = "DownloadTask";
	private int WORKING_STATTE = 1;// -1工作撤销 0暂停工作 1正常工作
	private static final int KEEP_ALIVE = 3;

	private AppInfo appInfo;
	private Context mContext;

	public DownloadTask(Context context, AppInfo appInfo) {
		mContext = context;
		this.appInfo = appInfo;
		appInfo.setState(DownloadMgr.DOWNLOAD_STATE_PENDING);
		// ==============状态由NULL变为PENDING【发广播】=============================
		DownloadMgr.sendProgressBroadcast(appInfo.getPackageName());
		
		//这里要初始化下载数据库，如果有直接读取信息，如果没有，添加一条
		
	}

	@Override
	protected void onPreExecute() {
		// 第一个执行方法,做准备工作
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(AppInfo... params) {
		//这里才开始下载
		appInfo.setState(DownloadMgr.DOWNLOAD_STATE_ING);
		// 第二个执行方法,onPreExecute()执行完后执行
		// 执行下载，只有标识为正常工作的时候才下载
		while (WORKING_STATTE == 1) {
			//下载完成
			if (appInfo.getDoneSize() >= appInfo.getFileSize()) {
				//通知下载管理移除队列
				DownloadMgr.finish(appInfo);
				break;
			}
			try {
				//模拟下载
				Thread.sleep(1000);
				appInfo.setDoneSize(appInfo.getDoneSize() + 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//通过进度更新来处理广播
			publishProgress(appInfo.getDoneSize());
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		//如果取消了下载，那么删除文件和数据库记录。
		if (WORKING_STATTE == -1) {
			//取消了下载
			appInfo.setDoneSize(0);
			appInfo.setState(DownloadMgr.DOWNLOAD_STATE_NULL);
			//要删除已下载的记录和本地文件############
		}else {
			//暂停和正常的都要更新数据库
			//更新数据库############【这里要补】
			
			
			//如果暂停了下载，状态更新
			 if (WORKING_STATTE == 0){
				 appInfo.setState(DownloadMgr.DOWNLOAD_STATE_NULL);
			 }
		}
		// ==============上面的变化虽然状态未知，但进度发生了变化【发广播】=============================
		DownloadMgr.sendProgressBroadcast(appInfo.getPackageName());
		super.onProgressUpdate(progress);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		appInfo.setState(DownloadMgr.DOWNLOAD_STATE_NULL);
		// ==============状态由ING变为NULL【发广播】=============================
		DownloadMgr.sendProgressBroadcast(appInfo.getPackageName());
		super.onPostExecute(result);
	}

	// 取消下载
	@Override
	protected void onCancelled() {
		//当前下载任务状态变更
		WORKING_STATTE = -1;
		//取消分两种，1种是在下载中，一种是在排队中。对于下载中的，直接设置状态即可，但是在排队中的要直接发广播
		if (appInfo.getState()==DownloadMgr.DOWNLOAD_STATE_PENDING) {
			appInfo.setState(DownloadMgr.DOWNLOAD_STATE_NULL);
			DownloadMgr.sendProgressBroadcast(appInfo.getPackageName());
		}
		super.onCancelled();
	}

	// 暂停下载
	protected void onPaused() {
		WORKING_STATTE = 0;
		super.onCancelled();
	}

	
	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

}
