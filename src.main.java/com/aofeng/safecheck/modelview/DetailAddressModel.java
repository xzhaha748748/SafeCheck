package com.aofeng.safecheck.modelview;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.model.FloorRoomRowModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class DetailAddressModel {
	public Activity mContext;
	private int idx;

	private final String ROAD; // 街道
	private final String UNIT_NAME; // 小区
	private final String CUS_DOM; // 楼号
	private final String CUS_DY; // 单元

	public ArrayListObservable<FloorRoomRowModel> floorRoomList = new ArrayListObservable<FloorRoomRowModel>(
			FloorRoomRowModel.class);
	
	//统计信息
	public StringObservable txtTotalNum = new StringObservable("0");
	public StringObservable txtInspectedNum = new StringObservable("0");
	public StringObservable txtUninspectedNum = new StringObservable("0");
	public StringObservable txtDeniedNum = new StringObservable("0");
	public StringObservable txtNoAnswerNum = new StringObservable("0");
	public StringObservable txtNeedFixNum = new StringObservable("0");
	
	//楼层
	public StringObservable txtFloorFrom = new StringObservable("");
	public StringObservable txtFloorTo = new StringObservable("");

	
	/**
	 * 加亮当前选择
	 * @param imgId
	 */
	private void HilightChosenImg(int imgId) {

		allImgId.set(R.drawable.all_btn);
		weiImgId.set(R.drawable.weijian_btn);
		yiImgId.set(R.drawable.yijian_btn);
		weixiuImgId.set(R.drawable.weixiu_btn);
		juImgId.set(R.drawable.jujian_btn);
		wuImgId.set(R.drawable.wuren_btn);
		if(imgId == R.drawable.all_btn_hover)
		{
			allImgId.set(imgId);
			idx = 0;
		}
		else if(imgId == R.drawable.weijian_btn_hover)
		{
			weiImgId.set(imgId);
			idx = 2;
		}
		else if(imgId == R.drawable.yijian_btn_hover)
		{
			yiImgId.set(imgId);
			idx = 1;
		}		
		else if(imgId == R.drawable.weixiu_btn_hover)
		{
			weixiuImgId.set(imgId);
			idx = 5;
		}		
		else if(imgId == R.drawable.jujian_btn_hover)
		{
			juImgId.set(imgId);
			idx = 3;
		}		
		else if(imgId == R.drawable.wuren_btn_hover)
		{
			wuImgId.set(imgId);
			idx = 4;
		}
	}
	
	public String getROAD() {
		return ROAD;
	}

	public String getUNIT_NAME() {
		return UNIT_NAME;
	}

	public String getCUS_DOM() {
		return CUS_DOM;
	}

	public String getCUS_DY() {
		return CUS_DY;
	}

	public DetailAddressModel(Activity Context) {
		this.mContext = Context;
		Bundle bundle = new Bundle();
		bundle = mContext.getIntent().getExtras();
		this.ROAD = bundle.getString("ROAD");
		this.UNIT_NAME = bundle.getString("UNIT_NAME");
		this.CUS_DOM = bundle.getString("CUS_DOM");
		this.CUS_DY = bundle.getString("CUS_DY");
	}

	public IntegerObservable allImgId = new IntegerObservable(R.drawable.all_btn_hover);
	public Command AllClicked = new Command(){
		public void Invoke(View view, Object... args) {
			DetailAddressModel.this.HilightChosenImg(R.drawable.all_btn_hover);
			listFloorRoomsByExample(0, false);
		}
	};
	
	public IntegerObservable weiImgId = new IntegerObservable(R.drawable.weijian_btn);
	public Command WeiImgClicked = new Command(){
		public void Invoke(View view, Object... args) {
			DetailAddressModel.this.HilightChosenImg(R.drawable.weijian_btn_hover);
			listFloorRoomsByExample(Vault.INSPECT_FLAG, false);
		}
	};

	public IntegerObservable yiImgId = new IntegerObservable(R.drawable.yijian_btn);
	public Command YiImgClicked = new Command(){
		public void Invoke(View view, Object... args) {
			DetailAddressModel.this.HilightChosenImg(R.drawable.yijian_btn_hover);
			listFloorRoomsByExample(Vault.INSPECT_FLAG, true);
		}
	};
	
	public IntegerObservable juImgId = new IntegerObservable(R.drawable.jujian_btn);
	public Command JuImgClicked = new Command(){
		public void Invoke(View view, Object... args) {
			DetailAddressModel.this.HilightChosenImg(R.drawable.jujian_btn_hover);
			listFloorRoomsByExample(Vault.DENIED_FLAG, true);
		}
	};
	
	public IntegerObservable wuImgId = new IntegerObservable(R.drawable.wuren_btn);
	public Command WuImgClicked = new Command(){
		public void Invoke(View view, Object... args) {
			DetailAddressModel.this.HilightChosenImg(R.drawable.wuren_btn_hover);
			listFloorRoomsByExample(Vault.NOANSWER_FLAG, true);
		}
	};
	public IntegerObservable weixiuImgId = new IntegerObservable(R.drawable.weixiu_btn);
	public Command WeixiuImgClicked = new Command(){
		public void Invoke(View view, Object... args) {
			DetailAddressModel.this.HilightChosenImg(R.drawable.weixiu_btn_hover);
			listFloorRoomsByExample(Vault.REPAIR_FLAG, true);
		}
	};
	
	public Command SearchByState = new Command(){
		public void Invoke(View view, Object... args) {
			ListByCondition();
		}
	};

	/**
	 * 根据选择进行查询
	 */
	public void ListByCondition() {
		if(idx == 0)
			listFloorRoomsByExample(0, false);
		else if(idx == 1)
			listFloorRoomsByExample(Vault.INSPECT_FLAG, true);
		else if(idx == 2)
			listFloorRoomsByExample(Vault.INSPECT_FLAG, false);
		else if(idx == 3)
			listFloorRoomsByExample(Vault.DENIED_FLAG, true);
		else if(idx == 4)
			listFloorRoomsByExample(Vault.NOANSWER_FLAG, true);
		else if(idx == 5)
			listFloorRoomsByExample(Vault.REPAIR_FLAG, true);
	}

	/**
	 * 按照楼层、安检条件列出楼层和房间
	 */
	public void listFloorRoomsByExample(int mask, boolean IsSet) {
		String floorFrom, floorTo;
		if(txtFloorFrom.get().trim().length()==0 && txtFloorTo.get().trim().length()==0)
		{
			floorFrom = "0";
			floorTo = "z";
		}
		else
		{
			if(!validate())
				return;
			floorFrom = txtFloorFrom.get().trim();
			floorTo =  txtFloorTo.get().trim();
		}
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
		String sql = 	"SELECT id, CUS_FLOOR, CUS_ROOM, CONDITION,CHECKPLAN_ID" +
				"  FROM T_IC_SAFECHECK_PAPAER "
				+ " where ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? " +
				" and CUS_FLOOR  >=? and CUS_FLOOR <= ? " +
				" and (CAST(CONDITION as INTEGER) & " + mask + ")" + (IsSet?">0":"=0") +
				" order by  length(cus_FLOOR), CUS_FLOOR, length(cus_room), CUS_ROOM";
		if((mask == Vault.INSPECT_FLAG) && !IsSet )
			sql = "SELECT id, CUS_FLOOR, CUS_ROOM, CONDITION,CHECKPLAN_ID" +
					"  FROM T_IC_SAFECHECK_PAPAER "
					+ " where ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? " +
					" and CUS_FLOOR  >=? and CUS_FLOOR <= ? " +
					" and CONDITION='0' " +
					" order by  length(cus_FLOOR), CUS_FLOOR, length(cus_room), CUS_ROOM";
		// 从安检单里获取所有街道名
		Cursor c = db.rawQuery(
						sql, 
						new String[] { ROAD, UNIT_NAME, CUS_DOM, CUS_DY, floorFrom, floorTo });
		floorRoomList.clear();
		while (c.moveToNext()) {
			FloorRoomRowModel row = new FloorRoomRowModel(this, c.getString(c.getColumnIndex("CHECKPLAN_ID")),
					c.getString(c.getColumnIndex("id")), c.getString(c.getColumnIndex("CONDITION")), ROAD, UNIT_NAME, CUS_DOM, CUS_DY,
					c.getString(c.getColumnIndex("CUS_FLOOR")), c.getString(c.getColumnIndex("CUS_ROOM"))
					);
			floorRoomList.add(row);
		}
		db.close();
	}

	/**
	 *    统计
	 *     select count(id), sum((CAST(CONDITION as INTEGER) & 2)/2) from T_IC_SAFECHECK_PAPAER  
	 * 	where DEPARTURE_TIME >'2000-01-01 00:00:00' and DEPARTURE_TIME <= '2000-01-01 23:59:59'
	 */
	public void total()
	{
		String today = Util.FormatDateToday("yyyy-MM-dd");
		//                                全部            
		String sql = "select count(a.id), " +
		//                                已检
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.INSPECT_FLAG + ") /" + Vault.INSPECT_FLAG + "), " +
		//                                未检
				"sum((" + Vault.INSPECT_FLAG  + "- (CAST(a.CONDITION as INTEGER) & " +  Vault.INSPECT_FLAG + ")) /" + Vault.INSPECT_FLAG + "), " +
		//                                拒检
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.DENIED_FLAG + ") /" + Vault.DENIED_FLAG + "), " +
		//                                无人
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.NOANSWER_FLAG + ") /" + Vault.NOANSWER_FLAG + "), " +
		//                                维修
				"sum((CAST(a.CONDITION as INTEGER) & " +  Vault.REPAIR_FLAG + ") /" + Vault.REPAIR_FLAG + ") "  +
				" from T_IC_SAFECHECK_PAPAER a join  T_INSPECTION b " +
				" on  a.id = b.CHECKPAPER_ID " 
			+ " where  (b.DEPARTURE_TIME >='" + today  + " 00:00:00' and b.DEPARTURE_TIME <= '" + today + " 23:59:59')"
			//+ " or a.DEPARTURE_TIME is NULL"
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
	/**
	 * 校验
	 * @return
	 */
	private boolean validate() {
		if(txtFloorFrom.get().trim().length()==0 || txtFloorTo.get().trim().length()==0)
		{
			Toast.makeText(mContext, "请补全楼层条件。", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
//		try
//		{
//			int from = Integer.parseInt(txtFloorFrom.get().trim());
//			int to = Integer.parseInt(txtFloorTo.get().trim());
//			if(from>=to)
//			{
//				Toast.makeText(mContext, "结束楼层不能小于开始楼层。", Toast.LENGTH_SHORT).show();
//				return false;
//			}
//			return true;
//		}
//		catch(Exception e)
//		{
//			Toast.makeText(mContext, "楼层必须是数字。", Toast.LENGTH_SHORT).show();
//			return false;
//		}
	}

}
