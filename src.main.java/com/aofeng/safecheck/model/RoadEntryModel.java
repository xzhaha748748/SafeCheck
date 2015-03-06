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

public class RoadEntryModel extends CursorRowModel {
	//对应的ModelView
	private CustomPlanModel model;
	
	public RoadEntryModel(CustomPlanModel model,boolean selected) {
		this.model = model;
		this.Chosen.set(selected);
	}
	
	

	public  BooleanObservable Chosen = new BooleanObservable(false);
	
	public IntegerObservable block_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg1_selected);
	public IntegerObservable block_background = new IntegerObservable(R.drawable.ajjh_titlebg1);
	
	//街道名称
	public StringObservable ROAD = new StringObservable("");

	//列出小区命令
	public Command ListUNIT_NAME = new Command() {
		public void Invoke(View view, Object... args) {
			model.listUNIT_NAMEs(ROAD.get());			
			model.onBlockItemIdxChanged();
		}
	};
}
