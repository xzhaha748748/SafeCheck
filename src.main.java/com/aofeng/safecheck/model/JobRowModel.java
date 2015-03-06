package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.observables.StringObservable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.aofeng.safecheck.modelview.JobDownModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class JobRowModel extends CursorRowModel {
	// ��Ӧ��ModelView
	private final JobDownModel model;

	// �ƻ�id
	private final String ID;

	public JobRowModel(String ID, JobDownModel model) {
		this.ID = ID;
		this.model = model;
	}

	// ��ʾ������
	public StringObservable Name = new StringObservable("");

	// ����״̬
	public StringObservable State = new StringObservable("");

	// ������������
	public Command JobDown = new Command() {
		@Override
		public void Invoke(View view, Object... args) {

			// �鿴�����ļ��Ƿ����
			if(!Util.DBExists(model.mContext))
				return;
			
			if(model.mContext.isBusy)
			{
				Toast.makeText(model.mContext, "��ȴ�������ɡ�", Toast.LENGTH_SHORT).show();
				return;
			}
			model.mContext.isBusy = true;

			// ���ú�̨���񣬻�ȡ�ƻ�����
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String hql = "select distinct c from T_CHECKPLAN c left join fetch c.f_checks"
								+ " where c.id='" + ID + "'";
						String url = Vault.DB_URL
								+ "one/"
								+ URLEncoder.encode(hql, "UTF8").replace("+",
										"%20");
				    	URL myURL = new URL(url);
				    	URLConnection conn = myURL.openConnection();
				    	conn.connect();
				    	InputStream is = conn.getInputStream();
					    //int fileSize = conn.getContentLength();//������Ӧ��ȡ�ļ���С
				    	FileOutputStream fos = model.mContext.openFileOutput("download.tmp", Context.MODE_PRIVATE);
					    byte buf[] = new byte[1024];
					    int bytesDownloaded = 0;
						Message msg = new Message();
						msg.what =0;
						msg.obj = 0;
						jobHandler.sendMessage(msg);
					    do
					    {
					        int numread = is.read(buf);
					        if (numread == -1)
					        {
					          break;
					        }
					        bytesDownloaded += numread;
					        fos.write(buf, 0, numread);
					    	msg = new Message();
							msg.obj = bytesDownloaded;
							msg.what = 0;
							jobHandler.sendMessage(msg);
					    }while(true);
					    fos.close();
					    BufferedReader br = new BufferedReader(new InputStreamReader(model.mContext.openFileInput("download.tmp")));
					    String text = br.readLine();
					    br.close();
					    ImportPlan(text);
				    	msg = new Message();
						msg.what = 2;
						jobHandler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = -1;
						jobHandler.sendMessage(msg);
					}
				}

			private void ImportPlan(String text) {
				try {
					JSONObject json = new JSONObject(text);
					// ����ƻ������ؿ�
					String id = json.getString("id");
					String name = json.getString("f_name");
					SQLiteDatabase db = model.getActivity().openOrCreateDatabase(
							"safecheck.db", Context.MODE_PRIVATE, null);
					db.execSQL("delete from t_checkplan"); // where id=?",
							//new Object[] { id });
					db.execSQL("INSERT INTO t_checkplan(id, f_name) VALUES (?, ?)",
							new Object[] { id, name });
			    	Message msg = new Message();
					msg.what = 1;
					msg.obj = "����ƻ�";
					jobHandler.sendMessage(msg);

					// ɾ������
					db.execSQL(
							"delete from T_IC_SAFECHECK_HIDDEN");
					// where id in (select id from T_INSPECTION where CHECKPLAN_ID=?)",
					//		new Object[] { id });
					Cursor c = db.rawQuery(
							"SELECT id from T_INSPECTION where CHECKPLAN_ID =?", new String[] { id });
					if(c.moveToNext())
						Util.deleteFiles(model.mContext, c.getString(c.getColumnIndex("id")));
					// ɾ���뻧�����¼				
					db.execSQL(
							"delete from T_INSPECTION");
					//where CHECKPLAN_ID =?",
					//		new Object[] { id });
					// ɾ��ԭ���а�������
					db.execSQL(
							"delete from T_IC_SAFECHECK_PAPAER");
					// where CHECKPLAN_ID=?",
					//		new Object[] { id });
					// ����ƻ��еİ�����Ŀ�����ؿ�
					JSONArray array = json.getJSONArray("f_checks");
					for (int i = 0; i < array.length(); i++) {
				    	msg = new Message();
						msg.what = 1;
						msg.obj = "����ƻ�" + (i+1);
						jobHandler.sendMessage(msg);
						JSONObject aJson = array.getJSONObject(i);
						String aId = aJson.getString("id");
						String CARD_ID = aJson.getString("CARD_ID"); // ����
						String USER_NAME = aJson.getString("USER_NAME"); // �û�����
						String TELPHONE = aJson.getString("TELPHONE"); // �绰
						String ROAD = aJson.getString("ROAD");// �ֵ�
						String UNIT_NAME = aJson.getString("UNIT_NAME");// С��
						String CUS_DOM = aJson.getString("CUS_DOM");// ¥��
						String CUS_DY = aJson.getString("CUS_DY");// ��Ԫ
						String CUS_FLOOR = aJson.getString("CUS_FLOOR");// ¥��
						String CUS_ROOM = aJson.getString("CUS_ROOM");// ����
						String OLD_ADDRESS = aJson.getString("OLD_ADDRESS"); // �û�������ַ
						String SAVE_PEOPLE = aJson.getString("SAVE_PEOPLE"); // ����Ա
						String CONDITION = aJson.getString("CONDITION"); //����״̬
						String USERID = aJson.getString("f_userid"); //����״̬
						if(CONDITION == "null")
						{
							CONDITION = "0";
						}

						db.execSQL("INSERT INTO T_IC_SAFECHECK_PAPAER("
								+ "id, CARD_ID, "
								+ // ����
								" USER_NAME ,"
								+ // �û�����
								" TELPHONE ,"
								+ // �绰
								" ROAD,"
								+ // �ֵ�
								" UNIT_NAME,"
								+ // С��
								" CUS_DOM,"
								+ // ¥��
								" CUS_DY,"
								+ // ��Ԫ
								" CUS_FLOOR ,"
								+ // ¥��
								" CUS_ROOM ,"
								+ // ����
								" OLD_ADDRESS ,"
								+ // �û�������ַ
								" SAVE_PEOPLE ,"
								+ // �û����
								" f_userid ,"
								//����״̬
								+ "CONDITION,"
								+ // ����ƻ�ID
								"CHECKPLAN_ID" + ") " + "VALUES (?, "
								+ "?, ?, ?, ?, ?, ?,?,  ?, ?, ?, ?, ?, ?, ?)",
								new Object[] { aId, CARD_ID, // ����
										USER_NAME, // �û�����
										TELPHONE, // �绰
										ROAD, // �ֵ�
										UNIT_NAME, // С��
										CUS_DOM, // ¥��
										CUS_DY, // ��Ԫ
										CUS_FLOOR, // ¥��
										CUS_ROOM, // ����
										OLD_ADDRESS, // �û�������ַ
										SAVE_PEOPLE, // ����Ա
										USERID,      //�û����
										CONDITION,   //����״̬
										id // ����ƻ�ID
								});
					}
					db.close();
					Util.deleteAllPics(model.mContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
			th.start();
		}
	};

	// ��ȡĳ���ƻ���Ĵ������
	private final Handler jobHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//���
			if(msg.what == 2)
			{
				State.set("������");
				model.mContext.isBusy = false;
			}
			else if(msg.what == 1)
			{
				State.set(msg.obj.toString());
			}
			//������
			else if(msg.what==0)
			{
				State.set(msg.obj.toString() + "�ֽ�");
			}
			else
			{
				Toast.makeText(model.mContext, "���س���", Toast.LENGTH_SHORT).show();
				State.set("����");
				model.mContext.isBusy = false;
			}
		}

	};
}
