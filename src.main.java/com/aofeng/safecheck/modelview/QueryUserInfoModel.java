package com.aofeng.safecheck.modelview;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.aofeng.safecheck.activity.ShootActivity;
import com.aofeng.safecheck.model.UserRow;
import com.aofeng.utils.Vault;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;
import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//@SuppressLint("HandlerLeak")
public class QueryUserInfoModel {
	public Activity mContext;
	
	public QueryUserInfoModel(Activity Context) {
		this.mContext = Context;
		Bundle bundle = new Bundle();
		bundle = mContext.getIntent().getExtras();
		this.txtUserName.set(bundle.getString("userName"));
		this.txtTelephone.set(bundle.getString("telephone"));
		this.txtAddress.set(bundle.getString("address"));
	}
	
	public StringObservable txtUserName = new StringObservable("");
	public StringObservable txtTelephone = new StringObservable("");
	public StringObservable txtAddress = new StringObservable("");
	
	public ArrayListObservable<UserRow> userList = new ArrayListObservable<UserRow>(UserRow.class);
	
	public Command SearchUserInfo = new Command()
	{

		@Override
		public void Invoke(View arg0, Object... arg1) {
			execute();
		}
		
	};

	protected void execute() {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(txtUserName.get() != null || txtUserName.get().length() != 0 || txtTelephone.get().length() >= 6 || txtAddress.get() != null || txtAddress.get().length() != 0)
					{
						//Nothing to do.
					}
					else
					{
						Message msg = new Message();
						msg.what = 3;
						listHandler.sendMessage(msg);
						return;
					}
					
					//String sql ="SELECT t1.f_username, f_phone, f_address, f_cardid, f_city, f_area, '',f_meternumber 基表号,f_aroundmeter 左右表, f_jbfactory 基表厂家, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, f_userid from (select * from t_userfiles WHERE f_username like '%"+ txtUserName.get() +"%' and (f_phone like '%"+ txtTelephone.get() +"%' or f_phone is null) and (f_address like '%"+ txtAddress.get() +"%' or f_address is null)) t1 left join (SELECT f_username, SUM (f_pregas) tsum, count(f_pregas) tcount FROM t_sellinggas WHERE f_username like '%"+ txtUserName.get() +"%' group by f_username) t2 on  t1.f_username= t2.f_username";
					
					String sql = "SELECT t1.f_username, f_phone, f_address, f_cardid, f_city, f_area, '',f_meternumber 基表号,f_aroundmeter 左右表, f_jbfactory 基表厂家, f_road, f_districtname, f_cusDom, f_cusDy, f_cusFloor, f_apartment, tsum, tcount, t1.f_userid from (select * from t_userfiles WHERE f_username like '%"+ txtUserName.get() +"%' and (f_phone like '%"+ txtTelephone.get() +"%' or f_phone is null) and (f_address like '%"+ txtAddress.get() +"%' or f_address is null)) t1 left join (SELECT f_userid, f_username, SUM (f_pregas) tsum, count(f_pregas) tcount FROM t_sellinggas WHERE f_username like '%"+ txtUserName.get() +"%' group by f_userid, f_username) t2 on  t1.f_username= t2.f_username and t1.f_userid = t2.f_userid";
					String url = Vault.DB_URL + "sql/"
							+ URLEncoder
							.encode(sql, "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// 数据下载完成
					if (code == 200) {
						String strResult = EntityUtils.toString(
								response.getEntity(), "UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 2;
						listHandler.sendMessage(msg);
					}
				}catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler.sendMessage(msg);
				}
			}
		});
		th.start();
	}
	
	// 显示用户信息
	private final Handler listHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					userList.clear();
					JSONArray array = new JSONArray((String) msg.obj);
					for(int i=0; i<array.length(); i++)
					{
						JSONObject obj = array.getJSONObject(i);
						UserRow profile = new UserRow();
						profile.model = QueryUserInfoModel.this;
						if (!obj.has("col0")) {
							// 查不到此IC卡用户
							Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT)	.show();
						} 
						else
						{
							if(obj.has("col0"))
								profile.userName.set(obj.getString("col0"));
							if(obj.has("col1"))
								profile.telephone.set(obj.getString("col1"));
							if(obj.has("col2"))
								profile.address.set(obj.getString("col2"));
							if(obj.has("col3"))
								profile.cardID.set(obj.getString("col3"));
							if(obj.has("col4"))
								profile.city.set(obj.getString("col4"));
							if(obj.has("col5"))
								profile.area.set(obj.getString("col5"));
							if(obj.has("col7"))
								profile.biaohao.set(obj.getString("col7"));
							if(obj.has("col8"))
							{
								/*int idx = f_rqbiaoxing.indexOf(obj.getString("col8"));
								if(idx>=0)
									((Spinner)mContext.findViewById(R.id.f_rqbiaoxing)).setSelection(idx);*/
								profile.zuoyoubiao.set(obj.getString("col8"));
							}
							if(obj.has("col9"))
								profile.biaochang.set(obj.getString("col9"));
							if(obj.has("col10"))
								profile.road.set(obj.getString("col10"));
							if(obj.has("col11"))
								profile.districtname.set(obj.getString("col11"));
							if(obj.has("col12"))
								profile.cusDom.set(obj.getString("col12"));
							if(obj.has("col13"))
								profile.cusDy.set(obj.getString("col13"));
							if(obj.has("col14"))
								profile.cusFloor.set(obj.getString("col14"));
							if(obj.has("col15"))
								profile.apartment.set(obj.getString("col15"));
							if(obj.has("col16"))
								profile.tsum.set(obj.getString("col16"));
							if(obj.has("col17"))
								profile.tcount.set(obj.getString("col17"));
							if(obj.has("col18"))
								profile.userID.set(obj.getString("col18"));
							String archiveAddress = "";
							for(int i1=10; i1<16; i1++)
								if(obj.has("col" + i1))
									archiveAddress += obj.getString("col"+i1) + "---";
								else
									archiveAddress += "---";
							if(archiveAddress.endsWith("---"))
								archiveAddress = archiveAddress.substring(0, archiveAddress.length()-3);
							profile.f_archiveaddress.set(archiveAddress);
							
							userList.add(profile);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					// 查不到此IC卡用户
					Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT).show();
				}
			} else if (0 == msg.what) {
				Toast.makeText(mContext, "请检查网络或者与管理员联系。", Toast.LENGTH_LONG)
				.show();
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "无此用户。", Toast.LENGTH_LONG).show();
			} else if (3 == msg.what) {
				Toast.makeText(mContext, "请输入用户姓名。", Toast.LENGTH_LONG).show();
			}
		}
	};	
}