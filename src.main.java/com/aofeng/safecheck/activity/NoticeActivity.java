package com.aofeng.safecheck.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.DetailAddressModel;
import com.aofeng.safecheck.modelview.NoticeModel;

public class NoticeActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		NoticeModel model = new NoticeModel(this);
		Binder.setAndBindContentView(this, R.layout.notice, model);

	}
}
