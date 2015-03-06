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
		 * ������ǰѡ��
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
		
		//�̷߳�����Ϣ��handler
		private final Handler mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//�ϴ���������
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
				//������ָ����
				ListView lv= (ListView)mContext.findViewById(R.id.lvUpload);
				lv.smoothScrollToPosition(idx);
				//����
				if(msg.what == -1)
				{
					Toast.makeText(mContext, "�ϴ�����", Toast.LENGTH_SHORT).show();
				}
				//���
				else if(msg.what == 0)
				{
					item.progress.set(progress);
					item.UPLOADED.set(true);
					item.UN_UPLOADED.set(false);
					Util.SetBit(mContext, Vault.UPLOAD_FLAG, item.ID.get());
				}
				//����
				else if(msg.what == 1)
				{
					item.progress.set(progress);
				}
				else if(msg.what == -3)
				{
					Toast.makeText(mContext, "�ϴ�����У��ʧ�ܡ�", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		
		//�ϴ���ť
		public Command AutoUpload = new Command(){
			public void Invoke(View view, Object... args) {
				Button btnUpload = (Button)mContext.findViewById(R.id.btnUpLoad);
				//�û����ȡ���ϴ���֪ͨ�߳�ֹͣ�ϴ�
				if(cancelable)
				{
					canceled = true;
					return;
				}
				//�û���������ϴ����ı䰴ťΪȡ�������ð�ťΪȡ��״̬����ʼ�̣߳�Ĭ���̲߳���ȡ��
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
					
					//ִ��
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
							//��ʼ�ϴ�����
							upload(Util.getSharedPreference(mContext, Vault.USER_ID) + "_" + uploadList.get(i).ID.get(), i);
						}
						//�ָ��ϴ�״̬
						sendMsg(-2, 0);
					}

					//������Ϣ
					private void sendMsg(int what, Object obj)
					{
						Message msg = new Message();
						msg.what = what;
						msg.obj = obj;
						mHandler.sendMessage(msg);
					}
					
					/**
					 * �ϴ�
					 * @param uuid
					 * @param idx   ��ǰ�������
					 */
					private void upload(String uuid, int idx) {
						//��ȡ����
						String json = GetRowInJson(uuid);
						if(json==null)
						{
							sendMsg(-1, idx*1000);
							return;
						}
						//У������
						if(!ValidateRow(json))
						{
							sendMsg(-3, idx*1000);
							return;
						}
						//�ϴ�����
						if(!UploadRow(json))
						{
							sendMsg(-1, idx*1000);
							return;
						}
						else
						//���10%
						sendMsg(1, idx*1000 + 10);
						//�ϴ�ͼƬ
						//ǩ��
						String[] url = {"_sign.png", "_1.jpg","_2.jpg","_3.jpg","_4.jpg","_5.jpg"};
						for(int i=0; i<url.length; i++)
						{
							if(!UploadFile(uuid+url[i]))
								sendMsg(-1, idx);
							sendMsg(1, idx*1000 + 10 + (i+1)*15);
						}
						//���100%
						sendMsg(0, idx*1000 + 100);
					}
					
					//������֤
					private boolean ValidateRow(String json) {
						try
						{
							JSONObject row = new JSONObject(json);
							boolean needsValidation = row.getString("CONDITION").equals("'����'");
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
					 * �ϴ��ļ�path
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
					 * �ϴ�����
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

				//����JSON����
				private String GetRowInJson(String uuid) {
					try {
						JSONObject obj = new JSONObject();
						SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
						// �Ӱ��쵥���ȡ���нֵ���
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

		//��δ�ϴ����б�
		public ArrayListObservable<UploadRowModel> uploadList = new ArrayListObservable<UploadRowModel>(
				UploadRowModel.class);
		
		/**
		 * ���������Ҽ�¼
		 */
		public void listByExample(int mask, boolean IsSet) {
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			
			String sql = "SELECT id, ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM, CONDITION" +
					"  FROM T_IC_SAFECHECK_PAPAER "
					//�����Ѽ졢���˻�ܼ�
							+ " where (CAST(CONDITION as INTEGER) & " 
							+  (Vault.INSPECT_FLAG + Vault.DENIED_FLAG + Vault.NOANSWER_FLAG+Vault.REPAIR_FLAG) + ")>0 and " +
							" (CAST(CONDITION as INTEGER) & " +  mask + ")" + (IsSet?">0":"=0") +
							" order by  (CAST(CONDITION as INTEGER) & " +  Vault.REPAIR_FLAG + ")," +
							"ROAD, UNIT_NAME, CUS_DOM, CUS_DY, CUS_FLOOR, CUS_ROOM";	
			// �Ӱ��쵥���ȡ���нֵ���
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
