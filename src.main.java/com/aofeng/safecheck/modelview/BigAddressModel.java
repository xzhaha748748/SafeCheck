package com.aofeng.safecheck.modelview;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.LongObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.aofeng.safecheck.model.CUS_DOMRowModel;
import com.aofeng.safecheck.model.CUS_DYRowModel;
import com.aofeng.safecheck.model.ROADRowModel;
import com.aofeng.safecheck.model.UNIT_NAMERowModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class BigAddressModel {

	public final Activity mContext;

	public BigAddressModel(Activity context) {
		this.mContext = context;
		if(Util.DBExists(mContext))
		// ��ʾ���нֵ�
		listROADs();
	}

	public Activity getContext() {
		return mContext;
	}

	public LongObservable blockItemIdx = new LongObservable(0);
	public LongObservable unitItemIdx = new LongObservable(0);
	public LongObservable domentryItemIdx = new LongObservable(0);
	public LongObservable dyentryItemIdx = new LongObservable(0);
	
	public long blockItemIdx2 = 0;
	public long unitItemIdx2 = 0;
	public long domentryItemIdx2 = 0;
	public long dyentryItemIdx2 = 0;
	
	public void onBlockItemIdxChanged()
	{
		ROADList.get((int)blockItemIdx2).Chosen.set(false);
		long idx = blockItemIdx.get();
		ROADList.get((int)idx).Chosen.set(true);
		blockItemIdx2 = blockItemIdx.get();
	}
	public void onUnitItemChanged()
	{
		UNIT_NAMEList.get((int)unitItemIdx2).Chosen.set(false);
		long idx = unitItemIdx.get();
		UNIT_NAMEList.get((int)idx).Chosen.set(true);
		unitItemIdx2 = unitItemIdx.get();
	}
	public void OnDomentryItemChanged()
	{
		CUS_DOMList.get((int)domentryItemIdx2).Chosen.set(false);
		long idx = domentryItemIdx.get();
		CUS_DOMList.get((int)idx).Chosen.set(true);
		domentryItemIdx2 = domentryItemIdx.get();
	}
	public void OndyentryItemIdxChanged()
	{
		CUS_DYList.get((int)dyentryItemIdx2).Chosen.set(false);
		long idx = dyentryItemIdx.get();
		CUS_DYList.get((int)idx).Chosen.set(true);
		dyentryItemIdx2 = dyentryItemIdx.get();
	}
	
	// �ֵ��б�
	public ArrayListObservable<ROADRowModel> ROADList = new ArrayListObservable<ROADRowModel>(
			ROADRowModel.class);

	// С���б�
	public ArrayListObservable<UNIT_NAMERowModel> UNIT_NAMEList = new ArrayListObservable<UNIT_NAMERowModel>(
			UNIT_NAMERowModel.class);

	// ¥���б�
	public ArrayListObservable<CUS_DOMRowModel> CUS_DOMList = new ArrayListObservable<CUS_DOMRowModel>(
			CUS_DOMRowModel.class);

	// ��Ԫ�б�
	public ArrayListObservable<CUS_DYRowModel> CUS_DYList = new ArrayListObservable<CUS_DYRowModel>(
			CUS_DYRowModel.class);

	//ͳ����Ϣ
	public StringObservable txtTotalNum = new StringObservable("0");
	public StringObservable txtInspectedNum = new StringObservable("0");
	public StringObservable txtUninspectedNum = new StringObservable("0");
	public StringObservable txtDeniedNum = new StringObservable("0");
	public StringObservable txtNoAnswerNum = new StringObservable("0");
	public StringObservable txtNeedFixNum = new StringObservable("0");
	
	// �г����нֵ�ѡ��
	private void listROADs() {

		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		
		// �Ӱ��쵥���ȡ���нֵ���
		Cursor c = db.rawQuery(
				"SELECT distinct ROAD FROM T_IC_SAFECHECK_PAPAER", null);
		int i = 0;
		while (c.moveToNext()) {
			String ROAD = c.getString(c.getColumnIndex("ROAD"));
			ROADRowModel row = new ROADRowModel(BigAddressModel.this,i == 0);
			i++;
			row.ROAD.set(ROAD);
			ROADList.add(row);
		}
		db.close();
	}

	// �г�ѡ�нֵ�������С��
	public void listUNIT_NAMEs(String ROAD) {
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// �Ӱ��쵥���ȡ���нֵ���
		Cursor c = db.rawQuery(
				"SELECT distinct UNIT_NAME FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=?", new String[] { ROAD });
		UNIT_NAMEList.clear();
		CUS_DOMList.clear();
		CUS_DYList.clear();
		int i= 0;
		while (c.moveToNext()) {
			String UNIT_NAME = c.getString(c.getColumnIndex("UNIT_NAME"));
			UNIT_NAMERowModel row = new UNIT_NAMERowModel(BigAddressModel.this,
					ROAD, i==0);
			i++;
			row.UNIT_NAME.set(UNIT_NAME);
			UNIT_NAMEList.add(row);
		}
		db.close();
	}

	// �г�ѡ��С��������¥
	public void listCUS_DOMs(String ROAD, String UNIT_NAME) {
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// �Ӱ��쵥���ȡ���нֵ���
		Cursor c = db.rawQuery(
				"SELECT distinct CUS_DOM FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=? and UNIT_NAME=? order by length(CUS_DOM), CUS_DOM", new String[] { ROAD,
						UNIT_NAME });
		CUS_DOMList.clear();
		CUS_DYList.clear();
		int i = 0;
		while (c.moveToNext()) {
			String CUS_DOM = c.getString(c.getColumnIndex("CUS_DOM"));
			CUS_DOMRowModel row = new CUS_DOMRowModel(BigAddressModel.this,
					ROAD, UNIT_NAME,i == 0);
			i++;
			row.CUS_DOM.set(CUS_DOM);
			CUS_DOMList.add(row);
		}
		db.close();
	}

	// �г�ѡ��¥�����е�Ԫ
	public void listCUS_DYs(String ROAD, String UNIT_NAME, String DOM) {
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// �Ӱ��쵥���ȡ���нֵ���
		Cursor c = db.rawQuery( 
				"SELECT distinct CUS_DY, CHECKPLAN_ID FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=? and UNIT_NAME=? and CUS_DOM=? order by length(cus_dy), CUS_DY",
				new String[] { ROAD, UNIT_NAME, DOM });
		CUS_DYList.clear();
		int i = 0;
		while (c.moveToNext()) {
			String CUS_DY = c.getString(c.getColumnIndex("CUS_DY"));
			CUS_DYRowModel row = new CUS_DYRowModel(BigAddressModel.this, ROAD,
					UNIT_NAME, DOM,i == 0);
			i++;
			row.CUS_DY.set(CUS_DY);
			row.CHECKPLAN_ID = c.getString(c.getColumnIndex("CHECKPLAN_ID"));
			CUS_DYList.add(row);
		}
		db.close();
	}
	
	/**
	 *    ͳ��
	 *     select count(id), sum((CAST(CONDITION as INTEGER) & 2)/2) from T_IC_SAFECHECK_PAPAER  
	 * 	where DEPARTURE_TIME >'2000-01-01 00:00:00' and DEPARTURE_TIME <= '2000-01-01 23:59:59'
	 */
	public void total()
	{
		String today = Util.FormatDateToday("yyyy-MM-dd");
		//                                ȫ��            
		String sql = "select count(a.id), " +
		//                                �Ѽ�
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.INSPECT_FLAG + ") /" + Vault.INSPECT_FLAG + "), " +
		//                                δ��
				"sum((" + Vault.INSPECT_FLAG  + "- (CAST(a.CONDITION as INTEGER) & " +  Vault.INSPECT_FLAG + ")) /" + Vault.INSPECT_FLAG + "), " +
		//                                �ܼ�
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.DENIED_FLAG + ") /" + Vault.DENIED_FLAG + "), " +
		//                                ����
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.NOANSWER_FLAG + ") /" + Vault.NOANSWER_FLAG + "), " +
		//                                ά��
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.REPAIR_FLAG + ") /" + Vault.REPAIR_FLAG + ") "  +
				" from T_IC_SAFECHECK_PAPAER a  join  T_INSPECTION b  on  a.id = b.CHECKPAPER_ID " 
			+ " where  (b.DEPARTURE_TIME >='" + today  + " 00:00:00' and b.DEPARTURE_TIME <= '" + today + " 23:59:59')"
		//	+ " or a.DEPARTURE_TIME is NULL"
				;
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery(sql, new String[]{});
		if(c.moveToNext())
		{
			txtTotalNum.set(c.getString(0)==null?"0":c.getString(0));
			txtInspectedNum.set(c.getString(1)==null?"0":c.getString(1));
			txtUninspectedNum.set(c.getString(2)==null?"0":c.getString(2));
			txtDeniedNum.set(c.getString(3)==null?"0":c.getString(3));
			txtNoAnswerNum.set(c.getString(4)==null?"0":c.getString(4));
			txtNeedFixNum.set(c.getString(5)==null?"0":c.getString(5));
		}
		db.close();
	}
	
}
