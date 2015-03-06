package com.aofeng.safecheck.modelview;

import java.io.File;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.ShootActivity;
import com.aofeng.safecheck.model.RepairUploadRowModel;
import com.aofeng.safecheck.model.UploadRowModel;
import com.aofeng.utils.CountableFileEntity;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RepairUploadModel {
		public Activity mContext;
		public boolean cancelable;
		public volatile boolean canceled;
		
		public Activity getActivity() {
			return mContext;
		}

		public RepairUploadModel(Activity context) {
			this.mContext = context;
			if(Util.DBExists(mContext))
				listByExample(null);
		}
		/**
		 * 加亮当前选择
		 * @param imgId
		 */
		private void HilightChosenImg(int imgId) {

			allImgId.set(R.drawable.all_btn);
			weiImgId.set(R.drawable.unupload_btn);
			yiImgId.set(R.drawable.uploaded_btn);
			if(imgId == R.drawable.all_btn_hover)
			{
				allImgId.set(imgId);
			}
			else if(imgId == R.drawable.unupload_btn_hover)
			{
				weiImgId.set(imgId);
			}
			else if(imgId == R.drawable.uploaded_btn_hover)
			{
				yiImgId.set(imgId);
			}			
		}
		public IntegerObservable allImgId = new IntegerObservable(R.drawable.all_btn_hover);
		public Command AllClicked = new Command(){
			public void Invoke(View view, Object... args) {
				RepairUploadModel.this.HilightChosenImg(R.drawable.all_btn_hover);
				listByExample(null);
			}
		};
		
		public IntegerObservable weiImgId = new IntegerObservable(R.drawable.unupload_btn);
		public Command WeiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				RepairUploadModel.this.HilightChosenImg(R.drawable.unupload_btn_hover);
				listByExample(Vault.REPAIRED_UNUPLOADED);
			}
		};

		public IntegerObservable yiImgId = new IntegerObservable(R.drawable.uploaded_btn);
		public Command YiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				RepairUploadModel.this.HilightChosenImg(R.drawable.uploaded_btn_hover);
				listByExample(Vault.REPAIRED_UPLOADED);
			}
		};

		private void SetRepairUploaded(String uuid) {
			try
			{
				SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);		
				db.execSQL("update T_REPAIR_TASK set REPAIR_STATE=? where id=?", new String[]{Vault.REPAIRED_UPLOADED, uuid} );
				db.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}		
		
	}
		
		//线程发送消息给handler
		private final Handler mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//上传操作结束
				if(msg.what == -2)
				{
					Button btnUpload = (Button)mContext.findViewById(R.id.btnUpLoad);
					btnUpload.setBackgroundResource(R.drawable.upload_btnxml);
					cancelable = false;
					return;
				}	
				int idx = Integer.parseInt(msg.obj.toString());
				float progress =  (idx % 1000)/100.0f;
				idx = idx /1000;
				RepairUploadRowModel item = uploadList.get(idx);
				//滚动到指定行
				ListView lv= (ListView)mContext.findViewById(R.id.lvUpload);
				lv.smoothScrollToPosition(idx);
				//出错
				if(msg.what == -1)
				{
					Toast.makeText(mContext, "上传出错", Toast.LENGTH_SHORT).show();
				}
				//完成
				else if(msg.what == 0)
				{
					item.progress.set(progress);
					item.UPLOADED.set(true);
					item.UN_UPLOADED.set(false);
					SetRepairUploaded(item.ID.get());
				}
				//进度
				else if(msg.what == 1)
				{
					item.progress.set(progress);
				}
			}

		};
		
		
		//上传按钮
		public Command AutoUpload = new Command(){
			public void Invoke(View view, Object... args) {
				Button btnUpload = (Button)mContext.findViewById(R.id.btnUpLoad);
				//用户点击取消上传，通知线程停止上传
				if(cancelable)
				{
					canceled = true;
					return;
				}
				//用户点击的是上传，改变按钮为取消，设置按钮为取消状态，开始线程，默认线程不能取消
				else
				{
					btnUpload.setBackgroundResource(R.drawable.reset_btn_hover);
					cancelable = true;
					canceled = false;
				}
				Thread th = new Thread(new Runnable() {	
					@Override
					public void run() {
						execute();
					}
					
					//执行
					private void execute()
					{
						for(int i=0; i<uploadList.size(); i++)
						{
							if(canceled)
							{
								break;
							}
							if( uploadList.get(i).UPLOADED.get())
								continue;
							//开始上传操作
							upload(uploadList.get(i).ID.get(), i);
						}
						sendMsg(-2, 0);
					}

					//发送消息
					private void sendMsg(int what, Object obj)
					{
						Message msg = new Message();
						msg.what = what;
						msg.obj = obj;
						mHandler.sendMessage(msg);
					}
					
					/**
					 * 上传
					 * @param uuid
					 * @param idx   当前项的索引
					 */
					private void upload(String uuid, int idx) {
						//读取数据
						String json = GetRowInJson(uuid);
						if(json==null)
						{
							sendMsg(-1, idx*1000);
							return;
						}
						//上传数据
						if(!UploadRow(json))
						{
							sendMsg(-1, idx*1000);
							return;
						}
						else
						{
							//完成100%
							sendMsg(0, idx*1000 + 100);
						}
					}


					/**
					 * 上传数据
					 * @param json
					 * @return
					 */
					private boolean UploadRow(String json) {
						HttpPost httpPost = new HttpPost(Vault.IIS_URL + "saveRepair");
						try {
							httpPost.setEntity(new StringEntity(json, "UTF8"));
							HttpClient httpClient = new DefaultHttpClient();
							HttpResponse response = httpClient.execute(httpPost);
							String result =  EntityUtils.toString(response.getEntity(), "UTF8");
							JSONObject obj = new JSONObject(result);
							if(obj.getString("ok").equals("nok"))
								return false;
							else
								return true;
						}
						catch(Exception e)
						{
							return false;
						}
					}

				//生成JSON数据
				private String GetRowInJson(String uuid) {
					try {
						JSONObject obj = new JSONObject();
						SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
						// 从安检单里获取所有街道名
						Cursor c = db.rawQuery("select CONTENT from T_REPAIR_RESULT where id = ?",  new String[] { uuid });
						while (c.moveToNext()) {
							obj.put(c.getString(0), c.getString(0));
						}
						obj.put("ID", uuid);
						db.close();
						return obj.toString();
					} catch (Exception e) {
						return null;
					}
				}
					
				});
				th.start();
			}
		};

		//绑定未上传的列表
		public ArrayListObservable<RepairUploadRowModel> uploadList = new ArrayListObservable<RepairUploadRowModel>(
				RepairUploadRowModel.class);
		
		/**
		 * 按条件查找记录
		 */
		public void listByExample(String state) {
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			
			String sql = "SELECT id, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, REPAIR_STATE" +
					"  FROM T_REPAIR_TASK ";
			if(state != null)
				sql += " where REPAIR_STATE='" + state +"'";
			else
				sql += " where REPAIR_STATE!='" + Vault.REPAIRED_NOT +"'";
			sql += " order by ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM";	
			// 从安检单里获取所有街道名
			Cursor c = db.rawQuery(
							sql,
									new String[]{}); 
			uploadList.clear();
			while (c.moveToNext()) {
				String address = c.getString(1) + " " + c.getString(2)  + " " + c.getString(3)  + " "+c.getString(4) + " " +c.getString(5) + " " +c.getString(6) ; 
				RepairUploadRowModel row = new RepairUploadRowModel(this, 
						c.getString(0), address, c.getString(c.getColumnIndex("REPAIR_STATE"))); 
				uploadList.add(row);
			}
			db.close();
		}

}
