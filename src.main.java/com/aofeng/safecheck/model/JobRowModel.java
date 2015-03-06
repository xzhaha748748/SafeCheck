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
	// 对应的ModelView
	private final JobDownModel model;

	// 计划id
	private final String ID;

	public JobRowModel(String ID, JobDownModel model) {
		this.ID = ID;
		this.model = model;
	}

	// 显示的名称
	public StringObservable Name = new StringObservable("");

	// 任务状态
	public StringObservable State = new StringObservable("");

	// 任务下载命令
	public Command JobDown = new Command() {
		@Override
		public void Invoke(View view, Object... args) {

			// 查看数据文件是否存在
			if(!Util.DBExists(model.mContext))
				return;
			
			if(model.mContext.isBusy)
			{
				Toast.makeText(model.mContext, "请等待下载完成。", Toast.LENGTH_SHORT).show();
				return;
			}
			model.mContext.isBusy = true;

			// 调用后台服务，获取计划数据
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
					    //int fileSize = conn.getContentLength();//根据响应获取文件大小
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
					// 插入计划到本地库
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
					msg.obj = "插入计划";
					jobHandler.sendMessage(msg);

					// 删除隐患
					db.execSQL(
							"delete from T_IC_SAFECHECK_HIDDEN");
					// where id in (select id from T_INSPECTION where CHECKPLAN_ID=?)",
					//		new Object[] { id });
					Cursor c = db.rawQuery(
							"SELECT id from T_INSPECTION where CHECKPLAN_ID =?", new String[] { id });
					if(c.moveToNext())
						Util.deleteFiles(model.mContext, c.getString(c.getColumnIndex("id")));
					// 删除入户安检记录				
					db.execSQL(
							"delete from T_INSPECTION");
					//where CHECKPLAN_ID =?",
					//		new Object[] { id });
					// 删除原所有安检内容
					db.execSQL(
							"delete from T_IC_SAFECHECK_PAPAER");
					// where CHECKPLAN_ID=?",
					//		new Object[] { id });
					// 保存计划中的安检项目到本地库
					JSONArray array = json.getJSONArray("f_checks");
					for (int i = 0; i < array.length(); i++) {
				    	msg = new Message();
						msg.what = 1;
						msg.obj = "插入计划" + (i+1);
						jobHandler.sendMessage(msg);
						JSONObject aJson = array.getJSONObject(i);
						String aId = aJson.getString("id");
						String CARD_ID = aJson.getString("CARD_ID"); // 卡号
						String USER_NAME = aJson.getString("USER_NAME"); // 用户名称
						String TELPHONE = aJson.getString("TELPHONE"); // 电话
						String ROAD = aJson.getString("ROAD");// 街道
						String UNIT_NAME = aJson.getString("UNIT_NAME");// 小区
						String CUS_DOM = aJson.getString("CUS_DOM");// 楼号
						String CUS_DY = aJson.getString("CUS_DY");// 单元
						String CUS_FLOOR = aJson.getString("CUS_FLOOR");// 楼层
						String CUS_ROOM = aJson.getString("CUS_ROOM");// 房号
						String OLD_ADDRESS = aJson.getString("OLD_ADDRESS"); // 用户档案地址
						String SAVE_PEOPLE = aJson.getString("SAVE_PEOPLE"); // 安检员
						String CONDITION = aJson.getString("CONDITION"); //安检状态
						String USERID = aJson.getString("f_userid"); //安检状态
						if(CONDITION == "null")
						{
							CONDITION = "0";
						}

						db.execSQL("INSERT INTO T_IC_SAFECHECK_PAPAER("
								+ "id, CARD_ID, "
								+ // 卡号
								" USER_NAME ,"
								+ // 用户名称
								" TELPHONE ,"
								+ // 电话
								" ROAD,"
								+ // 街道
								" UNIT_NAME,"
								+ // 小区
								" CUS_DOM,"
								+ // 楼号
								" CUS_DY,"
								+ // 单元
								" CUS_FLOOR ,"
								+ // 楼层
								" CUS_ROOM ,"
								+ // 房号
								" OLD_ADDRESS ,"
								+ // 用户档案地址
								" SAVE_PEOPLE ,"
								+ // 用户编号
								" f_userid ,"
								//安检状态
								+ "CONDITION,"
								+ // 安检计划ID
								"CHECKPLAN_ID" + ") " + "VALUES (?, "
								+ "?, ?, ?, ?, ?, ?,?,  ?, ?, ?, ?, ?, ?, ?)",
								new Object[] { aId, CARD_ID, // 卡号
										USER_NAME, // 用户名称
										TELPHONE, // 电话
										ROAD, // 街道
										UNIT_NAME, // 小区
										CUS_DOM, // 楼号
										CUS_DY, // 单元
										CUS_FLOOR, // 楼层
										CUS_ROOM, // 房号
										OLD_ADDRESS, // 用户档案地址
										SAVE_PEOPLE, // 安检员
										USERID,      //用户编号
										CONDITION,   //安检状态
										id // 安检计划ID
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

	// 获取某个计划后的处理过程
	private final Handler jobHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//完成
			if(msg.what == 2)
			{
				State.set("已下载");
				model.mContext.isBusy = false;
			}
			else if(msg.what == 1)
			{
				State.set(msg.obj.toString());
			}
			//进行中
			else if(msg.what==0)
			{
				State.set(msg.obj.toString() + "字节");
			}
			else
			{
				Toast.makeText(model.mContext, "下载出错！", Toast.LENGTH_SHORT).show();
				State.set("出错");
				model.mContext.isBusy = false;
			}
		}

	};
}
