package com.aofeng.safecheck.modelview;

import java.io.File;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.IndoorInspectActivity;
import com.aofeng.safecheck.activity.ShootActivity;
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

public class UpLoadModel {
		public Activity mContext;
		public boolean cancelable;
		public volatile boolean canceled;
		
		public Activity getActivity() {
			return mContext;
		}

		public UpLoadModel(Activity context) {
			this.mContext = context;
			if(Util.DBExists(mContext))
				listByExample(0, false);
		}
		/**
		 * 加亮当前选择
		 * @param imgId
		 */
		private void HilightChosenImg(int imgId) {

			allImgId.set(R.drawable.all_btn);
			weiImgId.set(R.drawable.unupload_btn);
			yiImgId.set(R.drawable.uploaded_btn);
			weixiuImgId.set(R.drawable.weixiu_btn);
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
			else if(imgId == R.drawable.weixiu_btn_hover)
			{
				weixiuImgId.set(imgId);
			}		
		}
		public IntegerObservable allImgId = new IntegerObservable(R.drawable.all_btn_hover);
		public Command AllClicked = new Command(){
			public void Invoke(View view, Object... args) {
				UpLoadModel.this.HilightChosenImg(R.drawable.all_btn_hover);
				listByExample(0, false);
			}
		};
		
		public IntegerObservable weiImgId = new IntegerObservable(R.drawable.unupload_btn);
		public Command WeiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				UpLoadModel.this.HilightChosenImg(R.drawable.unupload_btn_hover);
				listByExample(Vault.UPLOAD_FLAG, false);
			}
		};

		public IntegerObservable yiImgId = new IntegerObservable(R.drawable.uploaded_btn);
		public Command YiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				UpLoadModel.this.HilightChosenImg(R.drawable.uploaded_btn_hover);
				listByExample(Vault.UPLOAD_FLAG, true);
			}
		};
		public IntegerObservable weixiuImgId = new IntegerObservable(R.drawable.weixiu_btn);
		public Command WeixiuImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				UpLoadModel.this.HilightChosenImg(R.drawable.weixiu_btn_hover);
				listByExample(Vault.REPAIR_FLAG, true);
			}
		};
		
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
				UploadRowModel item = uploadList.get(idx);
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
					Util.SetBit(mContext, Vault.UPLOAD_FLAG, item.ID.get());
				}
				//进度
				else if(msg.what == 1)
				{
					item.progress.set(progress);
				}
				else if(msg.what == -3)
				{
					Toast.makeText(mContext, "上传出错：校验失败。", Toast.LENGTH_SHORT).show();
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
							upload(Util.getSharedPreference(mContext, Vault.USER_ID) + "_" + uploadList.get(i).ID.get(), i);
						}
						//恢复上传状态
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
						//校验数据
						if(!ValidateRow(json))
						{
							sendMsg(-3, idx*1000);
							return;
						}
						//上传数据
						if(!UploadRow(json))
						{
							sendMsg(-1, idx*1000);
							return;
						}
						else
						//完成10%
						sendMsg(1, idx*1000 + 10);
						//上传图片
						//签名
						String[] url = {"_sign.png", "_1.jpg","_2.jpg","_3.jpg","_4.jpg","_5.jpg"};
						for(int i=0; i<url.length; i++)
						{
							if(!UploadFile(uuid+url[i]))
								sendMsg(-1, idx);
							sendMsg(1, idx*1000 + 10 + (i+1)*15);
						}
						//完成100%
						sendMsg(0, idx*1000 + 100);
					}
					
					//数据验证
					private boolean ValidateRow(String json) {
						try
						{
							JSONObject row = new JSONObject(json);
							boolean needsValidation = row.getString("CONDITION").equals("'正常'");
							if(!needsValidation)
								return true;
							String validationURL = Vault.IIS_URL + "CAValidate/" + row.getString("F_KAHAO").replace("'","") + "/" + URLEncoder.encode(row.getString("ROAD").replace("'","")).replace("+", "%20") 
									+ "/" + URLEncoder.encode(row.getString("UNIT_NAME").replace("'","")).replace("+", "%20") + "/" + URLEncoder.encode(row.getString("CUS_DOM").replace("'","")).replace("+", "%20") + "/" + URLEncoder.encode(row.getString("CUS_DY").replace("'","")).replace("+", "%20") + "/" + URLEncoder.encode(row.getString("CUS_FLOOR").replace("'","")).replace("+", "%20")
									+ "/" + URLEncoder.encode(row.getString("CUS_ROOM").replace("'","")).replace("+", "%20") + "/" + Util.getSharedPreference(mContext, Vault.USER_ID) + "/" +  URLEncoder.encode(row.getString("ARRIVAL_TIME").replace("'","")).replace("+", "%20") + "/" + row.getString("F_JBDUSHU").replace("'",""); 
							HttpGet getMethod = new HttpGet(validationURL);
							HttpClient httpClient = new DefaultHttpClient();
							HttpResponse response = httpClient.execute(getMethod);
							JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF8"));
							if(obj.getString("ok").equals("ok"))
								return true;
							else
								return false;
						}
						catch(Exception e)
						{
							e.printStackTrace();
							return false;
						}
					}

					/**
					 * 上传文件path
					 * @param path
					 * @return
					 */
					private boolean UploadFile(String path) {
						String fn = Util.getSharedPreference(mContext, "FileDir") + path;
						if(!Util.fileExists(fn))
							return true;
						try
						{
							HttpClient httpClient = new DefaultHttpClient();
							HttpContext httpContext = new BasicHttpContext();
							String url = Vault.DB_URL + "savefile" + "?FileName="
									+ URLEncoder.encode(fn) + "&BlobId="
									+ URLEncoder.encode(path.substring(0, path.length()-4))
									+ "&EntityName=t_blob";
							HttpPost httpPost = new HttpPost(url);
							FileEntity entity = new FileEntity(new File(fn), "binary/octet-stream");
							httpPost.setEntity(entity);
							HttpResponse response = httpClient.execute(httpPost, httpContext);
							response.getEntity();
							return true;
						}
						catch(Exception e)
						{
							return false;
						}
					}

					/**
					 * 上传数据
					 * @param json
					 * @return
					 */
					private boolean UploadRow(String json) {
						HttpPost httpPost = new HttpPost(Vault.IIS_URL + "CAupdate/"+ Util.getSharedPreference(mContext, Vault.CHECKER_NAME) +"/"+ Util.getSharedPreference(mContext, Vault.department));
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
						Cursor c = db.rawQuery("select * from T_INSPECTION where id = ?",  new String[] { uuid });
						if (c.moveToNext()) {
							for (String col : c.getColumnNames())
							{
								String value = c.getString(c.getColumnIndex(col));
								if(c.getType(c.getColumnIndex(col)) == Cursor.FIELD_TYPE_NULL)
									obj.put(col.toUpperCase(), "NULL");
								else if(c.getType(c.getColumnIndex(col)) == Cursor.FIELD_TYPE_STRING)
									obj.put(col.toUpperCase(), Util.quote(value));
								else 
									obj.put(col.toUpperCase(), value);
							}
						}
						JSONArray lines = new JSONArray();
						c = db.rawQuery("select * from T_IC_SAFECHECK_HIDDEN where id=?", new String[] { uuid });
						while(c.moveToNext())
						{
							JSONObject line = new JSONObject();
							for(String col : c.getColumnNames())
								line.put(col.toUpperCase(), c.getString(c.getColumnIndex(col)));
							lines.put(line);
						}
						obj.put("suggestions", lines);
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
		public ArrayListObservable<UploadRowModel> uploadList = new ArrayListObservable<UploadRowModel>(
				UploadRowModel.class);
		
		/**
		 * 按条件查找记录
		 */
		public void listByExample(int mask, boolean IsSet) {
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			
			String sql = "SELECT id, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, CONDITION" +
					"  FROM T_IC_SAFECHECK_PAPAER "
					//必须已检、无人或拒检
							+ " where (CAST(CONDITION as INTEGER) & " 
							+  (Vault.INSPECT_FLAG + Vault.DENIED_FLAG + Vault.NOANSWER_FLAG+Vault.REPAIR_FLAG) + ")>0 and " +
							" (CAST(CONDITION as INTEGER) & " +  mask + ")" + (IsSet?">0":"=0") +
							" order by  (CAST(CONDITION as INTEGER) & " +  Vault.REPAIR_FLAG + ")," +
							"ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM";	
			// 从安检单里获取所有街道名
			Cursor c = db.rawQuery(
							sql,
									new String[]{}); 
			uploadList.clear();
			while (c.moveToNext()) {
				String address = c.getString(1) + " " + c.getString(2)  + " " + c.getString(3)  + " "+c.getString(4) + " " +c.getString(5) + " " +c.getString(6) ; 
				UploadRowModel row = new UploadRowModel(this, 
						c.getString(0), address, c.getString(c.getColumnIndex("CONDITION"))); 
				uploadList.add(row);
			}
			db.close();
		}

		
}
