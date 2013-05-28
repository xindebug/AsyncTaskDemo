package com.example.asynctaskdemo;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	TextView textView;
	DownloadBroadcastReceiver downloadBroadcastReceiver;
	MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DownloadMgr.init(getApplicationContext());
		if (myAdapter==null) {
			myAdapter = new MyAdapter(this);
		}
		setListAdapter(myAdapter);
		textView = (TextView) findViewById(R.id.textView1);
		getListView().setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				textView.setText(textView.getText().toString() + "：停止了");
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				textView.setText("当前显示" + firstVisibleItem + "--"
						+ (firstVisibleItem + visibleItemCount));
				// TODO Auto-generated method stub

			}
		});
		downloadBroadcastReceiver = new DownloadBroadcastReceiver(getApplicationContext(), myAdapter);

	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		downloadBroadcastReceiver.registerAction(DownloadMgr.PROGRESS_BROADCAST);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		unregisterReceiver(downloadBroadcastReceiver);
	}
	

}
