package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.LongObservable;
import gueei.binding.observables.StringObservable;
import android.view.View;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.CustomPlanModel;

public class DOMEntryModel extends CursorRowModel {
	//对应的ModelView
	private CustomPlanModel model;
	private String ROAD;
	private String UNIT;
	
	public DOMEntryModel(CustomPlanModel model, String road, String unit,boolean selected, long n) {
		this.model = model;
		ROAD = road;
		UNIT = unit;
		this.Chosen.set(selected);
		Added.set(n>0);
	}
	
	public  BooleanObservable Chosen = new BooleanObservable(false);
	public  BooleanObservable Added = new BooleanObservable(false);

	public IntegerObservable domentry_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg3_selected);
	public IntegerObservable domentry_background = new IntegerObservable(R.drawable.ajjh_titlebg3);
	//楼名称
	public StringObservable CUS_DOM = new StringObservable("");

	//列出单元命令
	public Command ListCUS_DY = new Command() {
		public void Invoke(View view, Object... args) {
			model.listCUS_DYs(ROAD, UNIT, CUS_DOM.get());
			model.OnDomentryItemChanged();
		}
	};
}
