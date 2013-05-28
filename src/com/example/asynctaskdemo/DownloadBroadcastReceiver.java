package com.example.asynctaskdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @description 接收下载进度的广播接收者
 * @author xinge21
 * @time 2012-10-5 上午10:47:20
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {
	Context context;
	MyAdapter applistAdapter;				//广播要通知更新的适配器

	public DownloadBroadcastReceiver(Context c,MyAdapter apkListAdapter) {
		context = c;
		this.applistAdapter = apkListAdapter;
	}

	// 动态注册
	public void registerAction(String action) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(action);

		context.registerReceiver(this, filter);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String pkname;
		AppInfo appInfo = (AppInfo)intent.getSerializableExtra("appinfo");
		if (appInfo==null) {
			pkname = intent.getStringExtra("pkname");
		}else {
			pkname = appInfo.getPackageName();
		}
		applistAdapter.updateItemView(pkname);
	}

} 