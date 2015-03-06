package com.aofeng.safecheck.modelview;

import gueei.binding.collections.ArrayListObservable;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.aofeng.safecheck.activity.JobDownActivity;
import com.aofeng.safecheck.model.JobRowModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class JobDownModel {
	public JobDownActivity mContext;

	public Activity getActivity() {
		return mContext;
	}

	public JobDownModel(JobDownActivity context) {
		this.mContext = context;
		// 产生计划列表显示
		listJobs();
	}

	// 计划列表
	public ArrayListObservable<JobRowModel> JobList = new ArrayListObservable<JobRowModel>(
			JobRowModel.class);

	// 从服务器下载计划列表，与本地数据库进行比较，产生用于显示的列表。
	private void listJobs() {
		// 从服务器获取计划列表
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Vault.DB_URL
							+ URLEncoder.encode("from T_CHECKPLAN t where t.f_issued='是' and (f_checkman = '" + Util.getSharedPreference(mContext, Vault.CHECKER_NAME) + "' or f_checkman is null) order by f_date desc", "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
					if (code == 200) {
						String strResult = EntityUtils.toString(response
								.getEntity(), "UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler.sendMessage(msg);
					}
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler.sendMessage(msg);
				}
			}
		});
		th.start();
	}

	// 获取任务列表后的处理过程
	private final Handler listHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);

				try {
					// 将服务器上获取的计划数据转换成Map
					String text = (String) msg.obj;
					JSONArray array = new JSONArray(text);
					List<Map<String, Object>> serverData = toMap(array);
					// 从本地库获取所有计划，先临时保存到list中
					List<JobRowModel> localData = new ArrayList<JobRowModel>();
					if(!Util.DBExists(mContext))
						return;

					SQLiteDatabase db = mContext.openOrCreateDatabase(
							"safecheck.db", Context.MODE_PRIVATE, null);

					Cursor c = db.rawQuery("SELECT * FROM t_checkplan order by f_date desc",
							new String[] {});

					while (c.moveToNext()) {
						String id = c.getString(c.getColumnIndex("id"));
						String name = c.getString(c.getColumnIndex("f_name"));

						// 将本地库数据添加到list中, 状态为已下载
						JobRowModel row = new JobRowModel(id, JobDownModel.this);
						row.Name.set(name);
						row.State.set("已下载");
						localData.add(row);
						// 去除服务器中与本地库重复的数据
						Map<String, Object> sMap = findById(serverData, id);
						if (sMap != null) {
							serverData.remove(sMap);
						}
					}
					db.close();
					// 先将服务器上剩余的计划添加到任务列表中，状态为未下载
					for (Map<String, Object> map : serverData) {
						JobRowModel row = new JobRowModel(
								(String) map.get("id"), JobDownModel.this);
						row.Name.set((String) map.get("f_name"));
						row.State.set("未下载");
						JobList.add(row);
					}
					// 将本地库的数据添加到任务列表中
					JobList.addAll(localData);

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				Toast.makeText(mContext, "请检查网络或者与管理员联系。", Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	// 找到与给定编号一致的数据
	private Map<String, Object> findById(List<Map<String, Object>> list,
			String id) {
		for (Map<String, Object> map : list) {
			String sid = (String) map.get("id");
			if (sid.equals(id)) {
				return map;
			}
		}
		return null;
	}

	// 把后台服务返回的Json串，转换成Map列表
	private List<Map<String, Object>> toMap(JSONArray array) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// 对于array中的每一项
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", json.getString("id"));
				map.put("f_name", json.getString("f_name"));
				map.put("f_date", json.getString("f_date"));
				result.add(map);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
