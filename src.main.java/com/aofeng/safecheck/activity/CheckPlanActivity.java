package com.aofeng.safecheck.activity;

import gueei.binding.Binder;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.CheckPlanModel;

public class CheckPlanActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.startwork,
		tabHost.getTabContentView(), true);		
		View view1 = getLayoutInflater().inflate(R.layout.checkmenu, null);
		TextView text1 = (TextView) view1.findViewById(R.id.tab_label);
		text1.setText("用户基本信息");
		View view2 = getLayoutInflater().inflate(R.layout.checkmenu, null);
		TextView text2 = (TextView) view2.findViewById(R.id.tab_label);
		text2.setText("燃气表信息");
		View view3 = getLayoutInflater().inflate(R.layout.checkmenu, null);
		TextView text3 = (TextView) view3.findViewById(R.id.tab_label);
		text3.setText("隐患");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(view1)
				.setContent(R.id.tab1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(view2)
				.setContent(R.id.tab2));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(view3)
				.setContent(R.id.tab3));
		tabHost.setCurrentTab(0);
		CheckPlanModel model = new CheckPlanModel(this);
		Binder.setAndBindContentView(this, R.layout.checkplan, model);
	}
}
