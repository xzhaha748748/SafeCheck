package com.aofeng.safecheck.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.CustomPlanModel;
import com.aofeng.utils.Util;

public class CustomPlanActivity extends Activity {
	CustomPlanModel model;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		model = new CustomPlanModel(this);
		Binder.setAndBindContentView(this, R.layout.custom_plan, model);
		super.onCreate(savedInstanceState);
	}
}
