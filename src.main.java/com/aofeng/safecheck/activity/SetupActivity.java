package com.aofeng.safecheck.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.SetupModel;

public class SetupActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SetupModel model = new SetupModel(this);
		Binder.setAndBindContentView(this, R.layout.setup, model);		
	}
	
	public boolean isBusy;
	
	@Override
	public void onBackPressed() {
		if(isBusy)
		{
			Toast.makeText(this, "请等待上次操作完成。", Toast.LENGTH_SHORT).show();
		}
		else
			super.onBackPressed();
	}
}
