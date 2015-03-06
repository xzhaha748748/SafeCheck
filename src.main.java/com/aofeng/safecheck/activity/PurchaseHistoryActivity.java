package com.aofeng.safecheck.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.PurchaseHistoryModel;

public class PurchaseHistoryActivity extends Activity{
	private String USERID;
	PurchaseHistoryModel model;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		model = new PurchaseHistoryModel(this);
		model.COUNT.set(bundle.getString("COUNT"));
		model.SUM.set(bundle.getString("SUM"));
		USERID=bundle.getString("USERID");
		Binder.setAndBindContentView(this, R.layout.purchase_history, model);
	}

	@Override
	protected void onResume() {
		super.onResume();
		model.listPurchases(USERID);
	}
	
	
}
