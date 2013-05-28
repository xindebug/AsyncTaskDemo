package com.example.asynctaskdemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * @description
 * @author xinge21
 * @time 2013-5-26 上午3:26:58
 * @copyright
 */

public class MyAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext = null;
	private List<AppInfo> list = new ArrayList<AppInfo>();

	public MyAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		for (int i = 0; i < 30; i++) {
			list.add(new AppInfo("pk" + i));
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateItemView(String pkname) {
		if (pkname != null) {
			// 更新UI
			Log.d("", "收到更新广播");
			notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		AppInfo appInfo = list.get(position);
		if (convertView == null) {
			// 和item_custom.xml脚本关联
			convertView = mInflater.inflate(R.layout.list_item, null);
			convertView.setTag(appInfo.getPackageName());
		}
		return setItemView(convertView, appInfo, position);
	}

	private View setItemView(View convertView, final AppInfo appInfo,
			int position) {

		
		Button buttonStart = (Button) convertView
				.findViewById(R.id.buttonStart);
		Button buttonPause = (Button) convertView
				.findViewById(R.id.buttonPause);
		Button buttonCancel = (Button) convertView
				.findViewById(R.id.buttonCancel);
		Button buttonDelete = (Button) convertView
				.findViewById(R.id.buttonDelete);
		Button buttonInstall = (Button) convertView
				.findViewById(R.id.buttonInstall);
		buttonStart.setVisibility(View.GONE);
		buttonPause.setVisibility(View.GONE);
		buttonCancel.setVisibility(View.GONE);
		buttonDelete.setVisibility(View.GONE);
		buttonInstall.setVisibility(View.GONE);
		String statusString = "";
		switch (appInfo.getState()) {
		case DownloadMgr.DOWNLOAD_STATE_NULL:
			if (appInfo.getDoneSize() == 0) {
				buttonStart.setVisibility(View.VISIBLE);
				statusString= "未开始";
			} else if (appInfo.getDoneSize() < appInfo.getFileSize()) {
				buttonStart.setVisibility(View.VISIBLE);
				buttonCancel.setVisibility(View.VISIBLE);
				statusString= "已暂停";
			} else {
				buttonInstall.setVisibility(View.VISIBLE);
				buttonDelete.setVisibility(View.VISIBLE);
				statusString= "已完成";
			}
			break;
		case DownloadMgr.DOWNLOAD_STATE_ING:
			buttonCancel.setVisibility(View.VISIBLE);
			buttonPause.setVisibility(View.VISIBLE);
			statusString= "下载中";
			break;
		case DownloadMgr.DOWNLOAD_STATE_PENDING:
			buttonCancel.setVisibility(View.VISIBLE);
			statusString= "排队中";
			break;

		default:
			break;
		}

		TextView itemTextView = (TextView) convertView
				.findViewById(R.id.textView);
		itemTextView.setText(appInfo.getDoneSize()+"/" +appInfo.getFileSize()+"["+ position+"]"+statusString);
		buttonStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DownloadMgr.start(appInfo);
			}
		});
		buttonPause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DownloadMgr.pause(appInfo.getPackageName());
			}
		});
		buttonCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DownloadMgr.cancel(appInfo.getPackageName());
			}
		});
		buttonDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 删除
				appInfo.setDoneSize(0);
				notifyDataSetChanged();
			}
		});
		buttonInstall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 安装
			}
		});

		return convertView;

	}
}