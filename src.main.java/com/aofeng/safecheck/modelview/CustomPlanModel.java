package com.aofeng.safecheck.modelview;

import java.util.ArrayList;
import java.util.UUID;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.LongObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.model.DOMEntryModel;
import com.aofeng.safecheck.model.DYEntryModel;
import com.aofeng.safecheck.model.FloorEntryModel;
import com.aofeng.safecheck.model.RoomEntryModel;
import com.aofeng.safecheck.model.UNITEntryModel;
import com.aofeng.safecheck.model.RoadEntryModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class CustomPlanModel {

	public final Activity mContext;

	public CustomPlanModel(Activity context) {
		this.mContext = context;
		if(Util.DBExists(mContext))
		// 显示所有街道
		listROADs();
	}

	public Activity getContext() {
		return mContext;
	}

	public LongObservable blockItemIdx = new LongObservable(0);
	public LongObservable unitItemIdx = new LongObservable(0);
	public LongObservable domentryItemIdx = new LongObservable(0);
	public LongObservable dyentryItemIdx = new LongObservable(0);
	public LongObservable floorItemIdx = new LongObservable(0);
	public LongObservable roomItemIdx = new LongObservable(0);
	
	public long blockItemIdx2 = 0;
	public long unitItemIdx2 = 0;
	public long domentryItemIdx2 = 0;
	public long dyentryItemIdx2 = 0;
	public long floorItemIdx2 = 0;
	public long roomItemIdx2 = 0;
	
	public void onBlockItemIdxChanged()
	{
		ROADList.get((int)blockItemIdx2).Chosen.set(false);
		long idx = blockItemIdx.get();
		ROADList.get((int)idx).Chosen.set(true);
		blockItemIdx2 = blockItemIdx.get();
	}
	public void onFloorItemChanged()
	{
		FloorList.get((int)floorItemIdx2).Chosen.set(false);
		long idx = floorItemIdx.get();
		FloorList.get((int)idx).Chosen.set(true);
		floorItemIdx2 = floorItemIdx.get();
	}

	public void onRoomItemChanged()
	{
		RoomList.get((int)roomItemIdx2).Chosen.set(false);
		long idx = roomItemIdx.get();
		RoomList.get((int)idx).Chosen.set(true);
		roomItemIdx2 = roomItemIdx.get();
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
	
	// 街道列表
	public ArrayListObservable<RoadEntryModel> ROADList = new ArrayListObservable<RoadEntryModel>(
			RoadEntryModel.class);

	// 小区列表
	public ArrayListObservable<UNITEntryModel> UNIT_NAMEList = new ArrayListObservable<UNITEntryModel>(
			UNITEntryModel.class);

	// 楼号列表
	public ArrayListObservable<DOMEntryModel> CUS_DOMList = new ArrayListObservable<DOMEntryModel>(
			DOMEntryModel.class);

	// 单元列表
	public ArrayListObservable<DYEntryModel> CUS_DYList = new ArrayListObservable<DYEntryModel>(
			DYEntryModel.class);

	// 楼层列表
	public ArrayListObservable<FloorEntryModel> FloorList = new ArrayListObservable<FloorEntryModel>(
			FloorEntryModel.class);

	// 房间列表
	public ArrayListObservable<RoomEntryModel> RoomList = new ArrayListObservable<RoomEntryModel>(
			RoomEntryModel.class);

	// 列出所有街道选项
	private void listROADs() {
		blockItemIdx.set(0l);
		unitItemIdx.set((long) 0);
		domentryItemIdx.set((long) 0);
		dyentryItemIdx.set((long) 0);
		floorItemIdx.set(0l);
		roomItemIdx.set(0l);
		
		blockItemIdx2 = 0;
		unitItemIdx2 = 0;
		domentryItemIdx2 = 0;
		dyentryItemIdx2 = 0;
		floorItemIdx2= 0;
		roomItemIdx2 = 0;
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery(
				"SELECT distinct ROAD FROM T_IC_SAFECHECK_PAPAER", null);
		int i = 0;
		while (c.moveToNext()) {
			String ROAD = c.getString(c.getColumnIndex("ROAD"));
			RoadEntryModel row = new RoadEntryModel(CustomPlanModel.this,i == 0);
			if(i==0)
				listUNIT_NAMEs(ROAD);
			i++;
			row.ROAD.set(ROAD);
			ROADList.add(row);
		}
		db.close();
	}

	// 列出选中街道的所有小区
	public void listUNIT_NAMEs(String ROAD) {
		unitItemIdx.set((long) 0);
		domentryItemIdx.set((long) 0);
		dyentryItemIdx.set((long) 0);
		floorItemIdx.set(0l);
		roomItemIdx.set(0l);
		
		unitItemIdx2 =  0;
		domentryItemIdx2 = 0;
		dyentryItemIdx2 = 0;
		floorItemIdx2= 0;
		roomItemIdx2 = 0;
		
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery(
				"SELECT distinct UNIT_NAME FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=?", new String[] { ROAD });
		UNIT_NAMEList.clear();
		CUS_DOMList.clear();
		CUS_DYList.clear();
		int i= 0;
		while (c.moveToNext()) {
			String UNIT_NAME = c.getString(c.getColumnIndex("UNIT_NAME"));
			UNITEntryModel row = new UNITEntryModel(CustomPlanModel.this,
					ROAD, i==0);
			if(i==0)
				listCUS_DOMs(ROAD, UNIT_NAME);
			i++;
			row.UNIT_NAME.set(UNIT_NAME);
			UNIT_NAMEList.add(row);
		}
		
		db.close();
	}

	// 列出选中小区的所有楼
	public void listCUS_DOMs(String ROAD, String UNIT_NAME) {
		domentryItemIdx.set((long) 0);
		dyentryItemIdx.set((long) 0);
		floorItemIdx.set(0l);
		roomItemIdx.set(0l);
		
		domentryItemIdx2 =  0;
		dyentryItemIdx2 = 0;
		floorItemIdx2= 0;
		roomItemIdx2 = 0;
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery(
				"SELECT CUS_DOM, sum((CAST(CONDITION as INTEGER) & " +  Vault.NEW_FLAG + ")) cnt FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=? and UNIT_NAME=? group by cus_dom order by CUS_DOM", new String[] { ROAD,
						UNIT_NAME });
		CUS_DOMList.clear();
		CUS_DYList.clear();
		int i = 0;
		while (c.moveToNext()) {
			String CUS_DOM = c.getString(c.getColumnIndex("CUS_DOM"));
			DOMEntryModel row = new DOMEntryModel(CustomPlanModel.this,
					ROAD, UNIT_NAME, i == 0, c.getLong(1));
			if(i==0)
				listCUS_DYs(ROAD, UNIT_NAME, CUS_DOM);
			i++;
			row.CUS_DOM.set(CUS_DOM);
			CUS_DOMList.add(row);
		}
		db.close();
	}

	// 列出选中楼的所有单元
	public void listCUS_DYs(String ROAD, String UNIT_NAME, String DOM) {
		dyentryItemIdx.set((long) 0);
		floorItemIdx.set(0l);
		roomItemIdx.set(0l);

		dyentryItemIdx2 = 0;
		floorItemIdx2= 0;
		roomItemIdx2 = 0;
		
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery( 
				"SELECT CUS_DY, sum((CAST(CONDITION as INTEGER) & " +  Vault.NEW_FLAG + ")) cnt  FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=? and UNIT_NAME=? and CUS_DOM=? group by cus_dy order by CUS_DY",
				new String[] { ROAD, UNIT_NAME, DOM });
		CUS_DYList.clear();
		int i = 0;
		while (c.moveToNext()) {
			String CUS_DY = c.getString(c.getColumnIndex("CUS_DY"));
			DYEntryModel row = new DYEntryModel(CustomPlanModel.this, ROAD,
					UNIT_NAME, DOM,i == 0, c.getLong(1));
			if(i==0)
				list_Floors(ROAD, UNIT_NAME, DOM,CUS_DY);
			i++;
			row.CUS_DY.set(CUS_DY);
			CUS_DYList.add(row);
		}
		db.close();
	}

	// 列出选中单元的所有层
	public void list_Floors(String ROAD, String UNIT_NAME, String DOM, String DY) {
		floorItemIdx.set(0l);
		roomItemIdx.set(0l);

		floorItemIdx2= 0;
		roomItemIdx2 = 0;
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery( 
				"SELECT CUS_FLOOR, sum((CAST(CONDITION as INTEGER) & " +  Vault.NEW_FLAG + ")) cnt  FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? group by CUS_FLOOR order by CUS_FLOOR",
				new String[] { ROAD, UNIT_NAME, DOM, DY });
		FloorList.clear();
		int i = 0;
		while (c.moveToNext()) {
			String FLOOR = c.getString(0);
			FloorEntryModel row = new FloorEntryModel(CustomPlanModel.this, ROAD,
					UNIT_NAME, DOM, DY, FLOOR, i == 0, c.getLong(1));
			if(i==0)
				list_Rooms(ROAD, UNIT_NAME, DOM, DY,FLOOR);
			i++;
			FloorList.add(row);
		}
		db.close();
	}

	// 列出选择层的所有房间
	public void list_Rooms(String ROAD, String UNIT_NAME, String DOM, String DY, String FLOOR) {
		roomItemIdx.set(0l);

		roomItemIdx2 = 0;
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery( 
				"SELECT CUS_ROOM, (CAST(CONDITION as INTEGER) & " +  Vault.NEW_FLAG + ") cnt  FROM T_IC_SAFECHECK_PAPAER "
						+ "where ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? and CUS_FLOOR=?  order by CUS_ROOM",
				new String[] { ROAD, UNIT_NAME, DOM, DY, FLOOR });
		RoomList.clear();
		int i = 0;
		while (c.moveToNext()) {
			String ROOM = c.getString(0);
			RoomEntryModel row = new RoomEntryModel(CustomPlanModel.this, ROAD,
					UNIT_NAME, DOM, DY, FLOOR, ROOM,  i == 0, c.getLong(1));
			i++;
			RoomList.add(row);
		}
		db.close();
	}
	
	//更新列表
	private void RefreshLists(int state, String ROAD, String UNIT_NAME, String DOM, String DY, String FLOOR) {
		//楼
		if(state==0)
		{
			listCUS_DOMs(ROAD, UNIT_NAME);
			domentryItemIdx.set((long) (CUS_DOMList.size()-1));
			this.OnDomentryItemChanged();
		}
		//单元
		else if(state==1)
		{
			this.listCUS_DYs(ROAD, UNIT_NAME, DOM);
			this.dyentryItemIdx.set((long) (CUS_DYList.size()-1));
			this.OndyentryItemIdxChanged();
		}
		//楼层
		else if(state==2)
		{
			this.list_Floors(ROAD, UNIT_NAME, DOM, DY);
			this.floorItemIdx.set((long) (FloorList.size()-1));
			this.onFloorItemChanged();
		}
		//房间
		else if(state==3)
		{
			this.list_Rooms(ROAD, UNIT_NAME, DOM, DY, FLOOR);
			this.roomItemIdx.set((long) (RoomList.size()-1));
			this.onRoomItemChanged();
		}
	}

	//插入安检单
	private void CheckAndInsertPlan(String road, String unit, String building, String dy, String floor, String room, String checkPlanID) {
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",	Context.MODE_PRIVATE, null);
		
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery("select id from T_IC_SAFECHECK_PAPAER " +
				"where CHECKPLAN_ID = ? and  ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? and CUS_FLOOR=? and CUS_ROOM=?", 
				new String[]{checkPlanID, road, unit, building, dy, floor, room});
		//不重复，插入
		if (!c.moveToNext())
		{
			String sql = "INSERT INTO T_IC_SAFECHECK_PAPAER" +
					" (ID, CHECKPLAN_ID, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, CONDITION ) VALUES(?,?,?,?,?,?,?,?,?)";
			db.execSQL(sql, new String[]{UUID.randomUUID().toString().replace("-", ""), checkPlanID, road, unit, building, dy, floor, room, Vault.NEW_FLAG+""});
		}
		db.close();		
	}

	/**
	 * 根据起始和结束，结合模式生成序列
	 * @param from
	 * @param to
	 * @param pattern
	 * @return
	 */
	private ArrayList<String> GetList(String from,	String to, String pattern) {
		ArrayList<String> series = new ArrayList<String>();
		if(from.equals(to) && pattern.length()==0)
		{
			series.add(from);
			return series;
		}
		try
		{
			int start = Integer.parseInt(from);
			int end = Integer.parseInt(to);
			for(int i=start; i<=end; i++)
				series.add(applyPattern(i+"", pattern));
			return series;
		}
		catch(Exception e)
		{
			if(from.length()==1 && to.length() ==1 && from.compareTo(to)<=0)
			{
				int start = (int)from.charAt(0);
				int end = (int)to.charAt(0);
				for(int i=start; i<=end; i++)
					series.add(applyPattern((char)i + "", pattern));
			}
			return series;
		}
	}
	
	/**
	 * 模式替换
	 * @param string
	 * @param pattern
	 * @return
	 */
	private String applyPattern(String string, String pattern) {
		if(pattern.length()==0)
			return string;
		else
			return pattern.replace("X", string);
	}

	private String GetCheckPlanID()
	{
		SQLiteDatabase db = null;
		try
		{
			db = mContext.openOrCreateDatabase("safecheck.db",	Context.MODE_PRIVATE, null);
			// 从安检单里获取所有街道名
			Cursor c = db.rawQuery("select id from t_checkplan", new String[]{});  
			if(c.moveToNext())
				return c.getString(0);
			else
				return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(db != null)
				db.close();
		}
	}
	
	/**
	 * 删除增加项
	 * @param state
	 */
	public void Delete(int state)
	{
		if(state==0)
		{
			if(this.CUS_DOMList.size()==0)
			{
				Toast.makeText(mContext, "不能删除！", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(!this.CUS_DOMList.get((int)this.domentryItemIdx2).Added.get())
			{
				Toast.makeText(mContext, "非新增项不能删除。", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		else if(state==1)
		{
			if(this.CUS_DYList.size()==0)
			{
				Toast.makeText(mContext, "不能删除！", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(!this.CUS_DYList.get((int)this.dyentryItemIdx2).Added.get())
			{
				Toast.makeText(mContext, "非新增项不能删除。", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		else if(state==2)
		{
			if(this.FloorList.size()==0)
			{
				Toast.makeText(mContext, "不能删除！", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(!this.FloorList.get((int)this.floorItemIdx2).Added.get())
			{
				Toast.makeText(mContext, "非新增项不能删除。", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		else if(state==3)
		{
			if(this.RoomList.size()==0)
			{
				Toast.makeText(mContext, "不能删除！", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(!this.RoomList.get((int)this.roomItemIdx2).Added.get())
			{
				Toast.makeText(mContext, "非新增项不能删除。", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		String ROAD = ROADList.get((int)blockItemIdx2).ROAD.get();
		String UNIT_NAME = this.UNIT_NAMEList.get((int)this.unitItemIdx2).UNIT_NAME.get();
		String CUS_DOM = this.CUS_DOMList.get((int)this.domentryItemIdx2).CUS_DOM.get();
		String CUS_DY = this.CUS_DYList.get((int)this.dyentryItemIdx2).CUS_DY.get();
		String CUS_FLOOR = this.FloorList.get((int)this.floorItemIdx2).CUS_FLOOR.get();
		String CUS_ROOM = this.RoomList.get((int)this.roomItemIdx2).CUS_ROOM.get();
		TrueDelete(state, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM);		
	}
	
	/**
	 * 做真正的删除
	 * @param state
	 * @param rOAD
	 * @param uNIT_NAME
	 * @param cUS_DOM
	 * @param cUS_DY
	 * @param cUS_FLOOR
	 * @param cUS_ROOM
	 */
	private void TrueDelete(int state, String ROAD, String UNIT_NAME,
			String CUS_DOM, String CUS_DY, String CUS_FLOOR, String CUS_ROOM) {
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db",	Context.MODE_PRIVATE, null);
		String[] params;
		String sql = "delete from T_IC_SAFECHECK_PAPAER where CONDITION = '" +  Vault.NEW_FLAG + "' ";
		if(state==0)
		{
			params = new String[]{ROAD, UNIT_NAME, CUS_DOM};
			db.execSQL(sql + " and  ROAD=? and UNIT_NAME=? and CUS_DOM=?", params);
		}
		else if(state==1)
		{
			params = new String[]{ROAD, UNIT_NAME, CUS_DOM, CUS_DY};
			db.execSQL(sql + " and  ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=?", params);
		}
		else if(state==2)
		{
			params = new String[]{ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR};
			db.execSQL(sql + " and  ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? and CUS_FLOOR=?", params);
		} 
		else if(state==3)
		{
			params = new String[]{ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM};
			db.execSQL(sql + " and  ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? and CUS_FLOOR=? and CUS_ROOM=?", params);
		}
		db.close();
		this.RefreshLists(state, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR);
	}

	/**
	 * 显示对话框
	 * @param state
	 */
	private void ShowDialog(final int state)
	{
		if(ROADList.size()==0)
		{
			Toast.makeText(mContext, "不能进行添加。", Toast.LENGTH_SHORT).show();
			return;
		}
		LayoutInflater inflater = mContext.getLayoutInflater();
		final View layout = inflater.inflate(R.layout.plan_dialog,null);
		AlertDialog.Builder builder = new Builder(mContext);
		
		builder.setCancelable(false);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String  editBuildingFrom = ((EditText)layout.findViewById(R.id.editBuildingFrom)).getText().toString().trim();
				String  editBuildingTo = ((EditText)layout.findViewById(R.id.editBuildingTo)).getText().toString().trim();
				String  editDYFrom = ((EditText)layout.findViewById(R.id.editDYFrom)).getText().toString().trim();
				String  editDYTo = ((EditText)layout.findViewById(R.id.editDYTo)).getText().toString().trim();
				String  editFloorFrom = ((EditText)layout.findViewById(R.id.editFloorFrom)).getText().toString().trim();
				String  editFloorTo = ((EditText)layout.findViewById(R.id.editFloorTo)).getText().toString().trim();
				String  editRoomFrom =((EditText)layout.findViewById(R.id.editRoomFrom)).getText().toString().trim();
				String  editRoomTo = ((EditText)layout.findViewById(R.id.editRoomTo)).getText().toString().trim();
				String  editBuildingPattern = ((EditText)layout.findViewById(R.id.editBuildingPattern)).getText().toString().trim();
				String  editDYPattern = ((EditText)layout.findViewById(R.id.editDYPattern)).getText().toString().trim();
				String  editFloorPattern = ((EditText)layout.findViewById(R.id.editFloorPattern)).getText().toString().trim();
				String  editRoomPattern = ((EditText)layout.findViewById(R.id.editRoomPattern)).getText().toString().trim();
				
				if(editBuildingFrom.length() == 0 ||editBuildingTo.length() == 0 ||
								editDYFrom.length() == 0 ||editDYTo.length() == 0 ||	
								editFloorFrom.length() == 0 ||editFloorTo.length() == 0 ||	
								editRoomFrom.length() == 0 ||editRoomTo.length() == 0 ||
								(editBuildingPattern.length()>0 && !editBuildingPattern.contains("X")) ||(editDYPattern.length()>0 && !editDYPattern.contains("X"))||
								(editFloorPattern.length()>0 && !editFloorPattern.contains("X")) || (editRoomPattern.length()>0 && !editRoomPattern.contains("X"))
						)
				{
					Toast.makeText(mContext, "请正确输入参数，模式必须包含字母X。", Toast.LENGTH_SHORT).show();
				}
				else
				{
					String checkPlanId = GetCheckPlanID();
					ArrayList<String> buildingList = GetList(editBuildingFrom, editBuildingTo, editBuildingPattern);
					ArrayList<String> dyList = GetList(editDYFrom, editDYTo, editDYPattern);
					ArrayList<String> floorList = GetList(editFloorFrom, editFloorTo, editFloorPattern);
					ArrayList<String> roomList = GetList(editRoomFrom, editRoomTo, editRoomPattern);
					String  road = ((TextView)layout.findViewById(R.id.textRoad)).getText().toString().trim();
					String  unit = ((TextView)layout.findViewById(R.id.textUnit)).getText().toString().trim();					

					for(String building:buildingList)
						for(String dy : dyList)
							for(String floor : floorList)
								for(String room : roomList)
									CheckAndInsertPlan(road, unit, building, dy, floor, room, checkPlanId);
					RefreshLists(state, road, unit, editBuildingFrom, editDYFrom, editFloorFrom);
					dialog.dismiss();
					Toast.makeText(mContext, "添加完成。", Toast.LENGTH_SHORT).show();					
				}
			}

		});
		builder.setTitle("地址生成");					
		builder.setView(layout);
		TextView  textRoad = (TextView)layout.findViewById(R.id.textRoad);
		textRoad.setText(this.ROADList.get((int)blockItemIdx2).ROAD.get());
		TextView  textUnit = (TextView)layout.findViewById(R.id.textUnit);
		textUnit.setText(this.UNIT_NAMEList.get((int)unitItemIdx2).UNIT_NAME.get());
		if(state > 0)
		{
			EditText  editBuildingFrom = (EditText)layout.findViewById(R.id.editBuildingFrom);
			editBuildingFrom.setEnabled(false);
			editBuildingFrom.setText(CUS_DOMList.get((int)domentryItemIdx2).CUS_DOM.get());
			EditText  editBuildingTo = (EditText)layout.findViewById(R.id.editBuildingTo);
			editBuildingTo.setEnabled(false);
			editBuildingTo.setText(CUS_DOMList.get((int)domentryItemIdx2).CUS_DOM.get());
			EditText  editBuildingPattern = (EditText)layout.findViewById(R.id.editBuildingPattern);
			editBuildingPattern.setEnabled(false);
			editBuildingPattern.setText("");
		}
		if(state > 1)
		{
			EditText  editDYFrom = (EditText)layout.findViewById(R.id.editDYFrom);
			editDYFrom.setEnabled(false);
			editDYFrom.setText(CUS_DYList.get((int)dyentryItemIdx2).CUS_DY.get());
			EditText  editDYTo = (EditText)layout.findViewById(R.id.editDYTo);
			editDYTo.setEnabled(false);
			editDYTo.setText(CUS_DYList.get((int)dyentryItemIdx2).CUS_DY.get());
			EditText  editDYPattern = (EditText)layout.findViewById(R.id.editDYPattern);
			editDYPattern.setEnabled(false);
			editDYPattern.setText("");
		}
		if(state > 2)
		{
			EditText  editFloorFrom = (EditText)layout.findViewById(R.id.editFloorFrom);
			editFloorFrom.setEnabled(false);
			editFloorFrom.setText(FloorList.get((int)floorItemIdx2).CUS_FLOOR.get());
			EditText  editFloorTo = (EditText)layout.findViewById(R.id.editFloorTo);
			editFloorTo.setEnabled(false);
			editFloorTo.setText(FloorList.get((int)floorItemIdx2).CUS_FLOOR.get());
			EditText  editFloorPattern = (EditText)layout.findViewById(R.id.editFloorPattern);
			editFloorPattern.setEnabled(false);
			editFloorPattern.setText("");
		}
		builder.create().show();		
	}
	
	public Command AddBuilding = new Command(){

		public void Invoke(View view, Object... args) {
			ShowDialog(0);
		}
	};
	
	public Command AddDY = new Command(){

		public void Invoke(View view, Object... args) {
			ShowDialog(1);
		}
	};
	
	public Command AddFloor = new Command(){

		public void Invoke(View view, Object... args) {
			ShowDialog(2);
		}
	};
	
	public Command AddRoom = new Command(){

		public void Invoke(View view, Object... args) {
			ShowDialog(3);
		}
	};

	public Command DelBuilding = new Command(){

		public void Invoke(View view, Object... args) {
			Delete(0);
		}
	};
	
	public Command DelDY = new Command(){

		public void Invoke(View view, Object... args) {
			Delete(1);
		}
	};
	
	public Command DelFloor = new Command(){

		public void Invoke(View view, Object... args) {
			Delete(2);
		}
	};
	
	public Command DelRoom = new Command(){

		public void Invoke(View view, Object... args) {
			Delete(3);
		}
	};
}
