package com.aofeng.safecheck.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.JobDownModel;

public class JobDownActivity extends Activity {
	public boolean isBusy;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JobDownModel model = new JobDownModel(this);
		Binder.setAndBindContentView(this, R.layout.jobdown, model);		
	}
	
	@Override
	public void onBackPressed() {
		if(isBusy)
		{
			Toast.makeText(this, "请等待下载完成。", Toast.LENGTH_SHORT).show();
		}
		else
			super.onBackPressed();
	}
}
