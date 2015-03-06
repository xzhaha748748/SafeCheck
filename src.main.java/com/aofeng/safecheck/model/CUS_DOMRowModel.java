package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.LongObservable;
import gueei.binding.observables.StringObservable;
import android.view.View;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.BigAddressModel;

public class CUS_DOMRowModel extends CursorRowModel {
	//对应的ModelView
	private BigAddressModel model;
	private String ROAD;
	private String UNIT;
	
	public CUS_DOMRowModel(BigAddressModel model, String road, String unit,boolean selected) {
		this.model = model;
		ROAD = road;
		UNIT = unit;
		this.Chosen.set(selected);
	}
	
	public  BooleanObservable Chosen = new BooleanObservable(false);
	
	public IntegerObservable domentry_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg3_selected);
	public IntegerObservable domentry_background = new IntegerObservable(R.drawable.ajjh_titlebg3);
	//楼名称
	public StringObservable CUS_DOM = new StringObservable("");

	//列出单元命令
	public Command ListCUS_DY = new Command() {
		public void Invoke(View view, Object... args) {
			model.listCUS_DYs(ROAD, UNIT, CUS_DOM.get());
			model.dyentryItemIdx.set((long) 0);
			model.dyentryItemIdx2 = 0;			
			model.OnDomentryItemChanged();
		}
	};
}
