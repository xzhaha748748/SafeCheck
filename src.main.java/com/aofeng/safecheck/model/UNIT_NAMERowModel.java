package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.view.View;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.BigAddressModel;

public class UNIT_NAMERowModel extends CursorRowModel {
	//对应的ModelView
	private BigAddressModel model;
	
	private String ROAD;
	
	public UNIT_NAMERowModel(BigAddressModel model, String road,  boolean selected) {
		this.model = model;
		ROAD = road;
		this.Chosen.set(selected);
	}
	
	public  BooleanObservable Chosen = new BooleanObservable(false);
	
	public IntegerObservable unit_selected_background = new IntegerObservable(R.drawable.ajjh_titlebg2_selected);
	public IntegerObservable unit_background = new IntegerObservable(R.drawable.ajjh_titlebg2);
	
	//小区名称
	public StringObservable UNIT_NAME = new StringObservable("");

	//列出楼号命令
	public Command ListCUS_DOM = new Command() {
		public void Invoke(View view, Object... args) {
			model.listCUS_DOMs(ROAD, UNIT_NAME.get());
			model.domentryItemIdx.set((long) 0);
			model.dyentryItemIdx.set((long) 0);
			
			model.domentryItemIdx2 = 0;
			model.dyentryItemIdx2 = 0;			
			model.onUnitItemChanged();
			}
	};
}
