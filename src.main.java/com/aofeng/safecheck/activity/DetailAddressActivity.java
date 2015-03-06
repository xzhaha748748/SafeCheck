package com.aofeng.safecheck.activity;

import java.util.ArrayList;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.DetailAddressModel;

public class DetailAddressActivity extends Activity{
	DetailAddressModel model;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState){
		model = new DetailAddressModel(this);
		Binder.setAndBindContentView(this, R.layout.detailaddress, model);
		super.onCreate(savedInstanceState);
		
	}
		
	
	@Override
	protected void onResume() {
		super.onResume();
		model.total();
		model.ListByCondition();
	}
	
	
}
