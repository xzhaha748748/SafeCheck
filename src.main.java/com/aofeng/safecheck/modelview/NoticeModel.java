package com.aofeng.safecheck.modelview;

import gueei.binding.collections.ArrayListObservable;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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

import com.aofeng.safecheck.model.JobRowModel;
import com.aofeng.safecheck.model.NoticeRowModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class NoticeModel {
	public Activity mContext;

	public Activity getActivity() {
		return mContext;
	}

	public NoticeModel(Activity context) {
		this.mContext = context;
		// �����ƻ��б���ʾ
		listNotices();
	}

	// �ƻ��б�
	public ArrayListObservable<NoticeRowModel> NoticeList = new ArrayListObservable<NoticeRowModel>(
			NoticeRowModel.class);

	// �ӷ��������ؼƻ��б��뱾�����ݿ���бȽϣ�����������ʾ���б�
	private void listNotices() {
		// �ӷ�������ȡ�ƻ��б�
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Vault.DB_URL
							+ URLEncoder.encode("from t_gonggao order by f_cdate desc", "UTF8").replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// �����������
					if (code == 200) {
						String strResult = EntityUtils.toString(response
								.getEntity(),"UTF8");
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
					String text = (String) msg.obj;
					JSONArray array = new JSONArray(text);
					for(int i=0; i<array.length(); i++)
					{
						JSONObject json = array.getJSONObject(i);
						NoticeRowModel row = new NoticeRowModel(
								json.getString("id"), NoticeModel.this);
						row.Title.set(json.getString("title"));
						row.Date.set(Util.FormatDate("yyyy-MM-dd", json.getLong("f_cdate")));
						row.Content.set(json.getString("comtext"));
						NoticeList.add(row);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				Toast.makeText(mContext, "����������������Ա��ϵ��", Toast.LENGTH_LONG)
						.show();
			}
		}
	};
	
}
