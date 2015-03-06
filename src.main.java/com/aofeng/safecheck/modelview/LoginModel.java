package com.aofeng.safecheck.modelview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import gueei.binding.Command;
import gueei.binding.observables.StringObservable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.MainActivity;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class LoginModel {
	private final Activity mContext;

	public static final int SAFE_CHECK_MODEL = 1;
	public ProgressDialog pd;
	boolean gotoRepair= false;
	
	public LoginModel(Activity context) {
		this.mContext = context;
	}

	// ����
	public StringObservable Name = new StringObservable("");
	// ����
	public StringObservable Password = new StringObservable("");

	// ��¼����
	public Command Login = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if (!checkInPut(Name.get(), Password.get())) {
				return;
			}
			// ��¼��ť���óɲ�����
			((Button) mContext.findViewById(R.id.button1)).setEnabled(false);

			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String sql = "select ID, NAME, ENAME, charindex((select id from t_role where NAME='ά����Ա'), roles,1),f_parentname from t_user where ename='"
								+ Name.get() + "' and password='" + Password.get() +"'";
						HttpGet getMethod = new HttpGet(Vault.DB_URL + "sql/" + URLEncoder.encode(sql).replace("+", "%20"));
						HttpClient httpClient = new DefaultHttpClient();
						HttpResponse response = httpClient.execute(getMethod);
						int code = response.getStatusLine().getStatusCode();
						if(code == 200)
						{
							Message msg = new Message();
							msg.what = 1;
							msg.obj = EntityUtils.toString(response.getEntity(), "UTF8");
							mHandler.sendMessage(msg);
						}
						else {
							Message msg = new Message();
							msg.what = 0;
							mHandler.sendMessage(msg);							
						}
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = 0;
						msg.obj = e.toString();
						mHandler.sendMessage(msg);
					}
				}
			});
			th.start();
		}
	};

	// ��¼�������
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String USER_ID, CHECKER_NAME,department;
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					JSONArray array = new JSONArray((String)msg.obj);
					JSONObject user = array.getJSONObject(0);
					USER_ID = user.getString("col0");
					CHECKER_NAME = user.getString("col1");
					int h=user.getInt("col3");
					department = user.getString("col4");
					if(!user.has("col3"))
						gotoRepair = false;
					else
						gotoRepair = user.getInt("col3")>0;
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(mContext, "��¼ʧ�ܡ������û����������Ƿ���ȷ��", Toast.LENGTH_SHORT).show();
					((Button) mContext.findViewById(R.id.button1)).setEnabled(true);
					return;
				}
				// �����û���Ϣ
				SharedPreferences.Editor prefEditor = mContext.getSharedPreferences(Vault.appID,LoginModel.SAFE_CHECK_MODEL).edit();
				prefEditor.putString(Vault.USER_NAME, Name.get());
				prefEditor.putString(Vault.CHECKER_NAME, CHECKER_NAME);				
				prefEditor.putString(Vault.USER_ID, USER_ID);
				prefEditor.putString(Vault.PASSWORD, Password.get());
				prefEditor.putString(Vault.department, department);
				prefEditor.commit();
				//����Ƿ����
				download();
			}
			else if(100 == msg.what)
			{
				DownloadRec dr = (DownloadRec)msg.obj;
				if(dr.progress==0)
				{
					pd.show();
				}
				pd.setProgress((int)(dr.progress *100.0/ dr.size));				
			}
			else if(101 == msg.what)
			{
				pd.dismiss();
				upgrade();
			}
			else if(-1 == msg.what)
			{
				//�������ݿ�
				upgradeDatabase();
				// ҳ����ת
				if(gotoRepair)
				{
					Intent intent = new Intent(mContext, MainActivity.class);
					mContext.startActivity(intent);
				}
				else
				{
					Toast.makeText(mContext, "��¼ʧ�ܡ������Ƿ�������ȷ���û����������Ƿ���ȷ��", Toast.LENGTH_SHORT).show();
					((Button) mContext.findViewById(R.id.button1)).setEnabled(true);
				}
			}
			else  if( 0==msg.what){
				if(pd != null  && pd.isShowing())
					pd.dismiss();
				Toast.makeText(mContext, "��¼ʧ�ܡ������Ƿ�������ȷ���û����������Ƿ���ȷ��", Toast.LENGTH_SHORT)
						.show();
				((Button) mContext.findViewById(R.id.button1)).setEnabled(true);
			}
		}
	};

	private boolean checkInPut(String inputName, String inputPassword) {

		String hint = "";

		if ("".equals(inputName)) {
			hint = "�������û�����\n";
		}

		if (("".equalsIgnoreCase(inputPassword))) {
			hint += "���������롣";
		}
		// �����ʾ��Ϣ
		if (!("".equals(hint))) {
			Toast.makeText(mContext, hint, Toast.LENGTH_LONG).show();
		}

		return "".equals(hint);
	}

	
	//����
	private void download()
	{
		pd = new ProgressDialog(this.mContext);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("�������أ����Ե�......");
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		//���ز���װ
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpGet getMethod = new HttpGet(Vault.checkVersionURL);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);
					int code = response.getStatusLine().getStatusCode();
					if(code!=200)
					{
						Message msg = new Message();
						msg.what = 0;
						mHandler.sendMessage(msg);
						return;					
					}	
					JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity(),"UTF8"));
					int rv = obj.getInt("value");
					int lv = Util.getVersionCode(mContext);
					if(rv <= lv)
					{
						Message msg = new Message();
						msg.what = -1;
						mHandler.sendMessage(msg);
						return;					
					}
					URL myURL = new URL(Vault.downloadURL);
			    	URLConnection conn = myURL.openConnection();
			    	conn.connect();
			    	InputStream is = conn.getInputStream();
				    int fileSize = conn.getContentLength();//������Ӧ��ȡ�ļ���С
				    File file = new File(Environment.getExternalStorageDirectory(), Vault.apkName);
			    	FileOutputStream fos = new FileOutputStream(file);
				    byte buf[] = new byte[1024];
				    int bytesDownloaded = 0;
					Message msg = new Message();
					msg.what =100;
					msg.obj = new DownloadRec(0, fileSize);
					mHandler.sendMessage(msg);
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
						msg.obj = new DownloadRec(bytesDownloaded, fileSize);
						msg.what = 100;
						mHandler.sendMessage(msg);
				    }while(true);
				    fos.flush();
				    fos.close();
			    	msg = new Message();
					msg.what = 101;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessage(msg);
				}
			}
		});
		th.start();
	}
	
	//��װapk
	private void upgrade()
	{
	        Intent intent = new Intent(Intent.ACTION_VIEW);
			ContextWrapper cw = new ContextWrapper(this.mContext);
			File directory = cw.getFilesDir();
	        Uri url = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Vault.apkName));
	        intent.setDataAndType(url, "application/vnd.android.package-archive");
	        mContext.startActivity(intent);
	        //play suicide.
	        System.exit(-1);
	}
	
	//���ݿ����������ݰ汾����
	public boolean upgradeDatabase()
	{
		SQLiteDatabase db = null;
		try {
			//�������ݿ�
			db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String   sql = "CREATE TABLE if not exists t_version (" +
					"id VARCHAR PRIMARY KEY, " +
					"ver integer )";
			db.execSQL(sql);
			sql = "insert into t_version values('1', 1)";
			db.execSQL(sql);
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			if(db != null)
				db.close();
		}
		return true;
	}
	
	//������Ϣ
	class DownloadRec
	{
		public DownloadRec(int progress, int size)
		{
			this.size = size;
			this.progress = progress;
		}
		public int size;
		public int progress;
	}
}
