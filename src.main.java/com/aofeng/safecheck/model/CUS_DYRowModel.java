package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.DetailAddressActivity;
import com.aofeng.safecheck.modelview.BigAddressModel;

public class CUS_DYRowModel extends CursorRowModel {
	//对应的ModelView
	private BigAddressModel model;
	private String ROAD;
	private String UNIT_NAME;
	private String CUS_DOM;
	
	public CUS_DYRowModel(BigAddressModel model, String road, String unit, String dom,boolean selected) {
		this.model = model;
		ROAD = road;
		UNIT_NAME = unit;
		CUS_DOM = dom;
		this.Chosen.set(selected);
	}
	public  BooleanObservable Chosen = new BooleanObservable(false);
	
	public IntegerObservable dyentry_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg4_selected);
	public IntegerObservable dyentry_background = new IntegerObservable(R.drawable.ajjh_titlebg4);
	
	//楼名称
	public StringObservable CUS_DY = new StringObservable("");
	
	public String CHECKPLAN_ID;

	//到住户安检页面
	public Command ListCUS_ROOM = new Command() {
		public void Invoke(View view, Object... args) {
		       Intent intent = new Intent();
		       //利用包袱传递参数给Activity
		       Bundle bundle = new Bundle();
		       bundle.putString("ROAD", ROAD);
		       bundle.putString("UNIT_NAME", UNIT_NAME);
		       bundle.putString("CUS_DOM", CUS_DOM);
		       bundle.putString("CUS_DY", CUS_DY.get());
		       bundle.putString("CHECKPLAN_ID", CHECKPLAN_ID);
		       intent.setClass(model.getContext(), DetailAddressActivity.class);
		       intent.putExtras(bundle);
		       model.getContext().startActivity(intent);	
		       model.OndyentryItemIdxChanged();
			}
	};
}
