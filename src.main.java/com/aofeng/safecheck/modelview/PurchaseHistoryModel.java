package com.aofeng.safecheck.modelview;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;

import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.aofeng.safecheck.model.PurchaseRowModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class PurchaseHistoryModel {
	public Activity mContext;

	public Activity getActivity() {
		return mContext;
	}

	public PurchaseHistoryModel(Activity context) {
		this.mContext = context;
	}

	public StringObservable SUM = new StringObservable("");
	public StringObservable COUNT = new StringObservable("");
	
	// 列表
	public ArrayListObservable<PurchaseRowModel> PurchaseList = new ArrayListObservable<PurchaseRowModel>(
			PurchaseRowModel.class);

	public void listPurchases(final String USERID) {
		// 从服务器获取计划列表
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Vault.DB_URL
							+ URLEncoder.encode("from t_sellinggas t where t.f_userid='" + USERID+ "' order by t.f_deliverydate desc, t.f_deliverytime desc", "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
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

	// 获取任务列表后的处理过程
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
						PurchaseRowModel row = new PurchaseRowModel();
						row.USERID.set(json.getString("f_userid"));
						row.OPERATE_DATE.set(Util.FormatDate("yyyy-MM-dd", json.getLong("f_deliverydate")));
						row.SELLGAS_GAS.set(json.getString("f_pregas"));
						row.PRICE.set(json.getString("f_myprice"));
						row.MONEY.set(json.getString("f_preamount"));
						PurchaseList.add(row);
					}
					COUNT.set(array.length()+"");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				Toast.makeText(mContext, "请检查网络或者与管理员联系。", Toast.LENGTH_LONG)
						.show();
			}
		}
	};
	
}
