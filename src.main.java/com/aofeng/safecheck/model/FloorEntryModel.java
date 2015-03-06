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

public class FloorEntryModel extends CursorRowModel {
	//对应的ModelView
	private CustomPlanModel model;
	private String ROAD;
	private String UNIT_NAME;
	private String CUS_DOM;
	private String CUS_DY;

	
	public FloorEntryModel(CustomPlanModel model, String road, String unit, String dom, String dy, String floor, boolean selected, long n) {
		this.model = model;
		ROAD = road;
		UNIT_NAME = unit;
		CUS_DOM = dom;
		CUS_DY = dy;
		CUS_FLOOR.set(floor);
		this.Chosen.set(selected);
		Added.set(n>0);
	}
	public  BooleanObservable Chosen = new BooleanObservable(false);
	public  BooleanObservable Added = new BooleanObservable(false);
	public IntegerObservable floor_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg4_selected);
	public IntegerObservable floor_background = new IntegerObservable(R.drawable.ajjh_titlebg4);
	
	//楼层
	public StringObservable CUS_FLOOR = new StringObservable("");
	

	//列出房间
	public Command ListCUS_ROOM = new Command() {
		public void Invoke(View view, Object... args) {
			model.list_Rooms(ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR.get());
			model.onFloorItemChanged();
			}
	};
}
