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

public class RoomEntryModel extends CursorRowModel {
	//对应的ModelView
	private CustomPlanModel model;
	private String ROAD;
	private String UNIT_NAME;
	private String CUS_DOM;
	private String CUS_DY;
	private String CUS_FLOOR;

	
	public RoomEntryModel(CustomPlanModel model, String road, String unit, String dom, String dy, String floor, String room, boolean selected, long n) {
		this.model = model;
		ROAD = road;
		UNIT_NAME = unit;
		CUS_DOM = dom;
		CUS_DY = dy;
		CUS_FLOOR=floor;
		CUS_ROOM.set(room);
		this.Chosen.set(selected);
		Added.set(n>0);
	}
	public  BooleanObservable Chosen = new BooleanObservable(false);
	public  BooleanObservable Added = new BooleanObservable(false);
	public IntegerObservable room_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg4_selected);
	public IntegerObservable room_background = new IntegerObservable(R.drawable.ajjh_titlebg4);
	
	//房号
	public StringObservable CUS_ROOM = new StringObservable("");
	
	public Command onClick = new Command() {
		public void Invoke(View view, Object... args) {
			model.onRoomItemChanged();
			}
	};
}
