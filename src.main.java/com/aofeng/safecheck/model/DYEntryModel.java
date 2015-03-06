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
import com.aofeng.safecheck.modelview.CustomPlanModel;

public class DYEntryModel extends CursorRowModel {
	//对应的ModelView
	private CustomPlanModel model;
	private String ROAD;
	private String UNIT_NAME;
	private String CUS_DOM;
	
	public DYEntryModel(CustomPlanModel model, String road, String unit, String dom,boolean selected, long n) {
		this.model = model;
		ROAD = road;
		UNIT_NAME = unit;
		CUS_DOM = dom;
		this.Chosen.set(selected);
		Added.set(n>0);
	}
	public  BooleanObservable Chosen = new BooleanObservable(false);
	public  BooleanObservable Added = new BooleanObservable(false);
	public IntegerObservable dyentry_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg4_selected);
	public IntegerObservable dyentry_background = new IntegerObservable(R.drawable.ajjh_titlebg4);
	
	//楼名称
	public StringObservable CUS_DY = new StringObservable("");
	
	//列出楼层
	public Command ListCUS_FLOOR = new Command() {
		public void Invoke(View view, Object... args) {
			model.list_Floors(ROAD, UNIT_NAME, CUS_DOM, CUS_DY.get());
			model.OndyentryItemIdxChanged();
			}
	};
}
