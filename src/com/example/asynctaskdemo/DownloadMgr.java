package com.example.asynctaskdemo;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

/**
 * @description
 * @author xinge21
 * @time 2013-5-25 下午9:00:41
 * @copyright
 */

public class DownloadMgr {
	public static final String PROGRESS_BROADCAST = "PROGRESS_BROADCAST";

	// 任务队列：在下载的和排队中的
	public static Map<String, DownloadTask> downloadingQueue = new HashMap<String, DownloadTask>();
	// 下载列表：下载中的+排队的+暂停的
	public static Map<String, AppInfo> downloadList = new HashMap<String, AppInfo>();
	
	public static  Map<String, AppInfo> completedList = new HashMap<String, AppInfo>();

	// 没有在执行，可以显示：开始（没下载完）、取消（没下载完）、安装（下载完成）、删除（下载完成）
	public static final int DOWNLOAD_STATE_NULL = 0;
	// 在执行，可以： 取消、暂停
	public static final int DOWNLOAD_STATE_ING = 1;
	// 排队中，可以： 取消
	public static final int DOWNLOAD_STATE_PENDING = 2;

	private static Context mContext;

	public static void init(Context context) {
		mContext = context;
	}

	/**
	 * 启动下载。继续下载也是调用这个方法
	 * @description：添加到两个列表并启动任务，有可能会进入排队状态
	 * @author xinge21
	 * @time 2013-5-26 下午8:38:42
	 *  @param appInfo void:
	 */
	@SuppressLint("NewApi")
	public static void start(AppInfo appInfo) {
		DownloadTask downloadTask = new DownloadTask(mContext, appInfo);
		downloadingQueue.put(appInfo.getPackageName(), downloadTask);
		downloadList.put(appInfo.getPackageName(), appInfo);
//		downloadTask.execute(appInfo);
//		downloadTask.executeOnExecutor((ExecutorService) Executors.newFixedThreadPool(7));
//		downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			 downloadTask.execute(appInfo);
	        } else {
	        	downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
	}
	/**
	 * 暂停，并把任务从队列移出
	 * @description：
	 * @author xinge21
	 * @time 2013-5-26 下午8:40:12
	 *  @param pkname void:
	 */
	public static void pause(String pkname) {
		DownloadTask downloadTask = downloadingQueue.get(pkname);
		downloadingQueue.remove(pkname);
		downloadTask.onPaused();
	}

	/**
	 * 取消，并把任务和下载对象从列表里移出
	 * @description：
	 * @author xinge21
	 * @time 2013-5-26 下午8:40:43
	 *  @param pkname void:
	 */
	public static void cancel(String pkname) {
		DownloadTask downloadTask = downloadingQueue.get(pkname);
		if (downloadTask != null) {
			downloadTask.onCancelled();
		} else {
			//被回收了，那么直接更新状态并发通知======================
			AppInfo appInfo = downloadList.get(pkname);
			if (appInfo!=null) {
				downloadList.get(pkname).setState(DOWNLOAD_STATE_NULL);
				downloadList.get(pkname).setDoneSize(0);
			}
			//【发广播】
			sendProgressBroadcast(pkname);
		}
		downloadingQueue.remove(pkname);
		downloadList.remove(pkname);
	}

	/**
	 * 下载完成。并把任务和下载对象从列表里移出
	 * @description：
	 * @author xinge21
	 * @time 2013-5-26 下午8:41:34
	 *  @param pkname void:
	 */
	public static void finish(AppInfo appInfo) {
		downloadingQueue.remove(appInfo.getPackageName());
		downloadList.remove(appInfo.getPackageName());
		completedList.put(appInfo.getPackageName(), appInfo);
	}

	/**
	 * 发送广播告诉状态更新
	 * @description：
	 * @author xinge21
	 * @time 2013-5-26 下午8:42:12
	 *  @param pkname void:
	 */
	public static void sendProgressBroadcast(String pkname) {
		Intent intent = new Intent(PROGRESS_BROADCAST);
		intent.putExtra("pkname", pkname);
		mContext.sendBroadcast(intent);
	}

}
