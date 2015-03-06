package com.aofeng.safecheck.model;

import java.util.UUID;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.IndoorInspectActivity;
import com.aofeng.safecheck.modelview.DetailAddressModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

@SuppressWarnings({ "deprecation", "deprecation" })
public class FloorRoomRowModel extends CursorRowModel {
	// 对应的ModelView
	private final DetailAddressModel model;

	@SuppressWarnings("deprecation")
	public FloorRoomRowModel(DetailAddressModel model, String planId, String id, String state, 
			String road,String unitName, String cusDom,String cusDy, String floor,String room) {
		this.model = model;
		ID.set(id);
		CUS_FLOOR.set(floor);
		CUS_ROOM.set(room);
		CUS_DY.set(cusDy);
		CHECKPLAN_ID = planId;
		ROAD = road;
		UNIT_NAME = unitName;
		CUS_DOM = cusDom;
		if(state.length()!=0)
		{
			int flag = Integer.valueOf(state).intValue();
			this.INSPECTED.set((flag & Vault.INSPECT_FLAG)>0);
			this.UPLOADED.set((flag & Vault.UPLOAD_FLAG)>0);
			this.UN_UPLOADED.set(!this.UPLOADED.get());
			this.DENIED.set((flag & Vault.DENIED_FLAG)>0);
			this.NOANSWER.set((flag & Vault.NOANSWER_FLAG)>0);
			this.DELETED.set((flag & Vault.DELETE_FLAG)>0);
			this.NEW.set((flag & Vault.NEW_FLAG)>0);
			this.REPAIR.set((flag & Vault.REPAIR_FLAG)>0);
			//如果不是已检、无人、拒绝
			this.UN_INSPECTED.set(!(this.INSPECTED.get()|this.NOANSWER.get()|this.DENIED.get()|this.REPAIR.get()));
		}
	}

	// 下发的安检单ID
	public StringObservable ID = new StringObservable("");
	// 楼层
	public StringObservable CUS_FLOOR = new StringObservable("");
	// 房间
	public StringObservable CUS_ROOM = new StringObservable("");
	//上传成功、不成功、已检、未检、拒绝、维修、新增、删除
	public BooleanObservable UPLOADED = new BooleanObservable(false);
	public BooleanObservable UN_UPLOADED = new BooleanObservable(true);
	public BooleanObservable INSPECTED = new BooleanObservable(false);
	public BooleanObservable UN_INSPECTED = new BooleanObservable(true);
	public BooleanObservable DENIED = new BooleanObservable(false);
	public BooleanObservable NOANSWER = new BooleanObservable(false);
	public BooleanObservable REPAIR = new BooleanObservable(false);
	public BooleanObservable NEW = new BooleanObservable(false);
	public BooleanObservable DELETED = new BooleanObservable(false);
	
	public String CHECKPLAN_ID;
	public String ROAD = ""; // 街道
	public String UNIT_NAME = ""; // 小区
	public String CUS_DOM = ""; // 楼号
	public StringObservable CUS_DY = new StringObservable(""); // 单元

	// 入户安检
	public Command InspectApartment = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if(DELETED.get())
			{
				Toast.makeText(model.mContext, "已删除计划不能安检！", Toast.LENGTH_SHORT).show();
				return;
			}
			//删除缓存
			Util.ClearCache(model.mContext, Util.getSharedPreference(model.mContext, Vault.USER_ID) + "_" + ID.get());
			if(!INSPECTED.get())
				Util.deleteFiles(model.mContext, Util.getSharedPreference(model.mContext, Vault.USER_ID) + "_" + ID.get());
			
			//给安检单Activity传入用户编号
			String userID = FindUserIDBySQLite(CHECKPLAN_ID, ROAD, UNIT_NAME, CUS_DOM, CUS_DY.toString(), CUS_FLOOR.toString(), CUS_ROOM.toString());
			if(userID == "")
			{
				Toast.makeText(model.mContext, "此用户档案有问题，请反馈收费系统处理", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			// 利用包袱传递参数给Activity
			Bundle bundle = new Bundle();

			bundle.putString("ID", ID.get());
			bundle.putString("CHECKPLAN_ID", CHECKPLAN_ID);
			bundle.putString("CUS_FLOOR", CUS_FLOOR.get());
			bundle.putString("CUS_ROOM", CUS_ROOM.get());
			bundle.putString("CUS_DY", CUS_DY.get());
			bundle.putString("ROAD", ROAD);
			bundle.putString("UNIT_NAME", UNIT_NAME);
			bundle.putString("CUS_DOM", CUS_DOM);
			bundle.putString("USERID", userID);
			bundle.putBoolean("INSPECTED", !UN_INSPECTED.get());

			intent.setClass(model.mContext, IndoorInspectActivity.class);
			intent.putExtras(bundle);
			model.mContext.startActivity(intent);
		}
	};
	
	private String FindUserIDBySQLite(String CheckPlanID, String Road, String UnitName, String cusDom, String cusDy, String cusFloor, String cusRoom)
	{
		String userid = "";
		SQLiteDatabase db = model.mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
		String sql = "select f_userid from T_IC_SAFECHECK_PAPAER where CHECKPLAN_ID = ? and ROAD = ? " +
				"and UNIT_NAME = ? and CUS_DOM = ? and CUS_DY = ? and CUS_FLOOR = ? and CUS_ROOM = ?";
		Cursor c = db.rawQuery(sql, new String[] {CheckPlanID, Road, UnitName, cusDom, cusDy, cusFloor, cusRoom});
		
		if(c.getCount() != 1)
		{
			return "";
		}
		while(c.moveToNext())
		{
			userid = c.getString(c.getColumnIndex("f_userid"));
		}
		db.close();
		return userid;
	}
	
	// 删除
	public Command CoinNew = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			LayoutInflater inflater = model.mContext.getLayoutInflater();
			View layout = inflater.inflate(R.layout.new_plan, (ViewGroup) model.mContext.findViewById(R.id.new_plan_dialog));
			TextView txtHint = (TextView)layout.findViewById(R.id.txtHint);
			txtHint.setText("地址：" + ROAD + " " + UNIT_NAME + " " +  CUS_DOM + " " + CUS_DY.get() + " "  +  CUS_FLOOR.get() + "层");
			final EditText txtRoomNo =(EditText)layout.findViewById(R.id.txtRoomNo);
			int roomId;
			try
			{
				roomId = Integer.parseInt(CUS_ROOM.get());
			}
			catch(Exception e)
			{
				roomId = -1;
			}
			if(roomId != -1)
				txtRoomNo.setText((roomId + 1) + "");
			else
			{
				try
				{
					txtRoomNo.setText((char)((byte)CUS_ROOM.get().charAt(0) + 1) + "");
				}
				catch(Exception e)
				{
				}
			}
			AlertDialog.Builder builder = new Builder(model.mContext);
			builder.setCancelable(false);
			builder.setView(layout);
			builder.setTitle("添加房号");
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setPositiveButton("确定", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String result = SavePlan(txtRoomNo.getText() + ""); 
					if( result != null)
						Toast.makeText(model.mContext, result, Toast.LENGTH_SHORT).show();
				}
			});
			builder.setNegativeButton("取消", null);
			builder.create().show();
		}
	};

	// 删除
	public Command Remove = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if(NEW.get())
			{
				if(DeletePlan())
					NEW.set(false);		
				else
					Toast.makeText(model.mContext, "删除失败!", Toast.LENGTH_SHORT);
			}
			else
			{
				Util.SetBit(model.mContext, Vault.DELETE_FLAG, ID.get());
				DELETED.set(true);
			}
		}
	};
	
	// 反删除
	public Command CancelRemove = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Util.ClearBit(model.mContext, Vault.DELETE_FLAG, ID.get());
			DELETED.set(false);
		}
	};
	
	//保存计划
	private String SavePlan(String roomNo) {
		String result = "";
		SQLiteDatabase db = model.mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		try {
			// 判断roomNo是否重复，重复退出
			Cursor c = db.rawQuery(
							"SELECT id "
									+ "  FROM T_IC_SAFECHECK_PAPAER "
									+ " where ROAD=? and UNIT_NAME=? and CUS_DOM=? and CUS_DY=? "
									+ " and CUS_ROOM=? and CUS_FLOOR=?", new String[] {
									ROAD, UNIT_NAME, CUS_DOM, CUS_DY.get(),
									roomNo, CUS_FLOOR.get() });
			if (c.moveToNext()) {
				return "房号重复，请重新添加！";
			}
			//添加到数据库
			String uuid = UUID.randomUUID().toString().replace("-", "");
			db.execSQL("INSERT INTO T_IC_SAFECHECK_PAPAER(ID, CONDITION, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, CHECKPLAN_ID)" +
					" VALUES(?,?,?,?,?,?,?,?,?)", new String[]
					{
						uuid, Vault.NEW_FLAG+"",
						ROAD, UNIT_NAME, CUS_DOM, CUS_DY.get(), CUS_FLOOR.get(), roomNo, CHECKPLAN_ID
					});
			//添加到绑定
		  FloorRoomRowModel row = new FloorRoomRowModel(model, CHECKPLAN_ID, uuid, Vault.NEW_FLAG+"", 
				  ROAD, UNIT_NAME, CUS_DOM, CUS_DY.get(),CUS_FLOOR.get(), roomNo);
		  int i;
		  for( i =0; i< model.floorRoomList.size(); i++)
			  if(model.floorRoomList.get(i).ID.get().equals(ID.get()))
				  break;
		  model.floorRoomList.add(i+1, row);
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "添加出错！";
		}
		finally {
			db.close();
		}
	}
	
	private boolean DeletePlan()
	{
		//删除当前id的
		SQLiteDatabase db = model.mContext.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		try {
			//删除安检及隐患记录
			db.execSQL("delete from T_IC_SAFECHECK_HIDDEN where id in" +
					" (select id from T_INSPECTION where CHECKPAPER_ID='" + ID.get()  + "')");
			db.execSQL("delete from T_INSPECTION where CHECKPAPER_ID='" + ID.get()  + "'");			
			db.execSQL("DELETE FROM T_IC_SAFECHECK_PAPAER where id = ?", new String[]
					{
						ID.get()
					});
			//从绑定删除
		  for( int i =0; i< model.floorRoomList.size(); i++)
			  if(model.floorRoomList.get(i).ID.get().equals(ID.get()))
			  {
				  model.floorRoomList.remove(i);
				  break;
			  }
		  return true;
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally {
			db.close();
		}	
	}
	
}
