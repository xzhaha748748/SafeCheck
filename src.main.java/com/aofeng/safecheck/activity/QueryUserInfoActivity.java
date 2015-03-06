package com.aofeng.safecheck.activity;

import gueei.binding.Binder;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.QueryUserInfoModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class QueryUserInfoActivity extends Activity {
	QueryUserInfoModel model;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new QueryUserInfoModel(this);
		Binder.setAndBindContentView(this, R.layout.query_user_info, model);
	}


}
