package com.aofeng.safecheck.modelview;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.model.FloorRoomRowModel;
import com.aofeng.safecheck.model.SecurityRowModel;
import com.aofeng.safecheck.model.UploadRowModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

public class MySecurityModel {
		public Activity mContext;

		public Activity getActivity() {
			return mContext;
		}

		public int idx;
		
		public MySecurityModel(Activity context) {
			this.mContext = context;
		}
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
				idx=0;
				allImgId.set(imgId);
			}
			else if(imgId == R.drawable.weijian_btn_hover)
			{
				idx=1;
				weiImgId.set(imgId);
			}
			else if(imgId == R.drawable.yijian_btn_hover)
			{
				idx=2;
				yiImgId.set(imgId);
			}		
			else if(imgId == R.drawable.weixiu_btn_hover)
			{
				idx=3;
				weixiuImgId.set(imgId);
			}		
			else if(imgId == R.drawable.jujian_btn_hover)
			{
				idx=4;
				juImgId.set(imgId);
			}		
			else if(imgId == R.drawable.wuren_btn_hover)
			{
				idx=5;
				wuImgId.set(imgId);
			}
		}
/**
 * 根据选择显示列表
 */
		public void listBySelection()
		{
			if(idx==0)
				listByExample(0, false);
			else if(idx==1)
				listByExample(Vault.INSPECT_FLAG, false);
			else if(idx==2)
				listByExample(Vault.INSPECT_FLAG, true);
			else if(idx==3)
				listByExample(Vault.REPAIR_FLAG, true);
			else if(idx==4)
				listByExample(Vault.DENIED_FLAG, true);
			else if(idx==5)
				listByExample(Vault.NOANSWER_FLAG, true);
		}
		
		public IntegerObservable allImgId = new IntegerObservable(R.drawable.all_btn_hover);
		public Command AllClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.all_btn_hover);
				listByExample(0, false);
			}
		};
		
		public IntegerObservable weiImgId = new IntegerObservable(R.drawable.weijian_btn);
		public Command WeiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.weijian_btn_hover);
				listByExample(Vault.INSPECT_FLAG, false);
			}
		};

		public IntegerObservable yiImgId = new IntegerObservable(R.drawable.yijian_btn);
		public Command YiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.yijian_btn_hover);
				listByExample(Vault.INSPECT_FLAG, true);
			}
		};
		
		public IntegerObservable juImgId = new IntegerObservable(R.drawable.jujian_btn);
		public Command JuImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.jujian_btn_hover);
				listByExample(Vault.DENIED_FLAG, true);
			}
		};
		
		public IntegerObservable wuImgId = new IntegerObservable(R.drawable.wuren_btn);
		public Command WuImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.wuren_btn_hover);
				listByExample(Vault.NOANSWER_FLAG, true);
			}
		};
		public IntegerObservable weixiuImgId = new IntegerObservable(R.drawable.weixiu_btn);
		public Command WeixiuImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.weixiu_btn_hover);
				listByExample(Vault.REPAIR_FLAG, true);
			}
		};

		//显示列表
		public ArrayListObservable<SecurityRowModel> plainList = new ArrayListObservable<SecurityRowModel>(
				SecurityRowModel.class);
		
		protected void listByExample(int mask, boolean IsSet) {
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			// 从安检单里获取所有街道名
			Cursor c = db.rawQuery(
							"SELECT id, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, CONDITION, CHECKPLAN_ID" +
							"  FROM T_IC_SAFECHECK_PAPAER " +
							" where   (CAST(CONDITION as INTEGER) & " 
							+  (Vault.INSPECT_FLAG + Vault.DENIED_FLAG + Vault.NOANSWER_FLAG+Vault.REPAIR_FLAG) + ")>0 " +
									" and (CAST(CONDITION as INTEGER) & " +  mask + ")" + (IsSet?">0":"=0") +
									" order by  (CAST(CONDITION as INTEGER) & " +  Vault.REPAIR_FLAG + "),"
							+ " ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM",
									new String[]{}); 
			plainList.clear();
			while (c.moveToNext()) {
				String address = c.getString(1) + " " + c.getString(2)  + " " + c.getString(3)  + " "+c.getString(4) + " " +c.getString(5) + " " +c.getString(6) ; 
				SecurityRowModel row = new SecurityRowModel(this, 
						c.getString(0), address, c.getString(c.getColumnIndex("CONDITION"))); 
				row.ROAD = c.getString(1);
				row.UNIT_NAME = c.getString(2);
				row.CUS_DOM = c.getString(3);
				row.CUS_DY = c.getString(4);
				row.CUS_FLOOR = c.getString(5);
				row.CUS_ROOM = c.getString(6);
				row.CHECKPLAN_ID = c.getString(8);
				plainList.add(row);
			}
			db.close();
			
		}
}
