package com.aofeng.safecheck.activity;

import gueei.binding.Binder;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.MySecurityModel;
import com.aofeng.utils.Util;

import android.app.Activity;
import android.os.Bundle;

public class MySecurityActivity extends Activity{
	MySecurityModel model;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new MySecurityModel(this);
		Binder.setAndBindContentView(this, R.layout.mysecurity, model);		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Util.DBExists(this))
			model.listBySelection();
	}

	
}