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
		// �����ƻ��б���ʾ
		listJobs();
	}

	// �ƻ��б�
	public ArrayListObservable<JobRowModel> JobList = new ArrayListObservable<JobRowModel>(
			JobRowModel.class);

	// �ӷ��������ؼƻ��б��뱾�����ݿ���бȽϣ�����������ʾ���б�
	private void listJobs() {
		// �ӷ�������ȡ�ƻ��б�
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Vault.DB_URL
							+ URLEncoder.encode("from T_CHECKPLAN t where t.f_issued='��' and (f_checkman = '" + Util.getSharedPreference(mContext, Vault.CHECKER_NAME) + "' or f_checkman is null) order by f_date desc", "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// �����������
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

	// ��ȡ�����б��Ĵ������
	private final Handler listHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);

				try {
					// ���������ϻ�ȡ�ļƻ�����ת����Map
					String text = (String) msg.obj;
					JSONArray array = new JSONArray(text);
					List<Map<String, Object>> serverData = toMap(array);
					// �ӱ��ؿ��ȡ���мƻ�������ʱ���浽list��
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

						// �����ؿ�������ӵ�list��, ״̬Ϊ������
						JobRowModel row = new JobRowModel(id, JobDownModel.this);
						row.Name.set(name);
						row.State.set("������");
						localData.add(row);
						// ȥ�����������뱾�ؿ��ظ�������
						Map<String, Object> sMap = findById(serverData, id);
						if (sMap != null) {
							serverData.remove(sMap);
						}
					}
					db.close();
					// �Ƚ���������ʣ��ļƻ���ӵ������б��У�״̬Ϊδ����
					for (Map<String, Object> map : serverData) {
						JobRowModel row = new JobRowModel(
								(String) map.get("id"), JobDownModel.this);
						row.Name.set((String) map.get("f_name"));
						row.State.set("δ����");
						JobList.add(row);
					}
					// �����ؿ��������ӵ������б���
					JobList.addAll(localData);

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				Toast.makeText(mContext, "����������������Ա��ϵ��", Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	// �ҵ���������һ�µ�����
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

	// �Ѻ�̨���񷵻ص�Json����ת����Map�б�
	private List<Map<String, Object>> toMap(JSONArray array) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// ����array�е�ÿһ��
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
