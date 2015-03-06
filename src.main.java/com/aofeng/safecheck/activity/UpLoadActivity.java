package com.aofeng.safecheck.activity;

import gueei.binding.Binder;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.UpLoadModel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class UpLoadActivity extends Activity{
	UpLoadModel model;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new UpLoadModel(this);
		Binder.setAndBindContentView(this, R.layout.upload, model);		
	}

	/**
	 *  on pressing the back button
	 */
	@Override
	public void onBackPressed() {
		//����ϴ���ťδ�����£�����
		if(!model.cancelable)
			super.onBackPressed();
		else
			Toast.makeText(this, "�ϴ������У���ȡ����ȴ��ϴ�������", Toast.LENGTH_SHORT).show();
	}

	
}