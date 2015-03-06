package com.aofeng.safecheck.activity;

import gueei.binding.Binder;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.RepairUploadModel;
import com.aofeng.safecheck.modelview.UpLoadModel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class RepairUploadActivity extends Activity{
	RepairUploadModel model;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new RepairUploadModel(this);
		Binder.setAndBindContentView(this, R.layout.repair_upload, model);		
	}

	/**
	 *  on pressing the back button
	 */
	@Override
	public void onBackPressed() {
		//如果上传按钮未被按下，返回
		if(!model.cancelable)
			super.onBackPressed();
		else
			Toast.makeText(this, "上传进行中，请取消或等待上传结束。", Toast.LENGTH_SHORT).show();
	}

	
}