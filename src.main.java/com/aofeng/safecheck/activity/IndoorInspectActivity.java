package com.aofeng.safecheck.activity;

import gueei.binding.app.BindingActivity;
import gueei.binding.validation.ModelValidator;
import gueei.binding.validation.ValidationResult;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.model.UserRow;
import com.aofeng.safecheck.modelview.IndoorInspectModel;
import com.aofeng.safecheck.modelview.QueryUserInfoModel;
import com.aofeng.safecheck.modelview.RepairMan;
import com.aofeng.utils.MyDigitalClock;
import com.aofeng.utils.Pair;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class IndoorInspectActivity extends BindingActivity {
	// �뻧����ƻ�ID
	private IndoorInspectModel model;
	//������ʱ���ɵ�UUID
	public String uuid;
	private boolean status;
	private boolean inspected;

	//�������ʱ��
	//�����δ�죬����ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��ʱ�ӿ�����
	//                       ����ʱ����¼model�е�ʱ�䣬 ����ʱ��Ϊʱ��ʱ��
	//                       ����ʱ���ָ���ʼʱ�䡢����ʱ��
	//                       �ϴ�ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��Ϊ��ǰʱ��
	//                       �����٣���ͬ����ʱ
	//   �����Ѽ죬����ʱ����ʼʱ��Ϊ����ʱ�䣬����ʱ��ʱ�ӽ�ֹ������ʱ��Ϊ����ʱ�䣬ͬʱ��ֹ�����ˡ��ܼ졢���ò���ѡ�
	//                       ����ʱ����¼model�е�ʱ�䣬 ����ʱ��Ϊʱ��ʱ��
	//                       ����ʱ���ָ���ʼʱ�䡢����ʱ��
	//                       �ϴ�ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��Ϊ��ǰʱ��
	//                       �����٣���ͬ����ʱ
	//  �ܾ�/���ˣ�����ʱ����ʼʱ��Ϊ����ʱ�䣬����ʱ��ʱ�ӽ�ֹ������ʱ��Ϊ����ʱ�䣬�������ˡ��ܼ졢���ò���ѡ�
	//                       �޸����ˡ��ܼ�״̬ʱ�����ÿ�ʼʱ��(�������ʱ��)������ʱ�䣬��������ʱ��
	//                       ����ʱ����¼model�е�ʱ�䣬 ����ʱ��ʱ��ʱ��
	//                       ����ʱ���ָ���ʼʱ�䡢����ʱ��
	//                       �ϴ�ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��Ϊ��ǰʱ��
	//                       �����٣���ͬ����ʱ
	private Date entryDateTime;

	//�Ƿ��ɷ�ά��
	private CheckBox IsDispatchRepair;
	
	//���浱ǰ�������������Ƿ��Ѿ����浽����
	public boolean localSaved;
	public String paperId = "test";
	public String planId="";
	
	// ------------------------����------------------------------------
	private Button shoot1;
	private ImageView img1;
	private Button shoot2;
	private ImageView img2;
	private Button shoot3;
	private ImageView img3;
	private Button shoot4;
	private ImageView img4;
	private Button shoot5;
	private ImageView img5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new IndoorInspectModel(this);
		this.setAndBindRootView(R.layout.indoor_inspect, model);
		model.muteOthers(R.id.basicPane);
		Bundle bundle = getIntent().getExtras();
		boolean readonly = false;
		if (bundle != null) {
			paperId = bundle.getString("ID");
			planId = bundle.getString("CHECKPLAN_ID");
			model.CUS_FLOOR.set(bundle.getString("CUS_FLOOR"));
			model.CUS_ROOM.set(bundle.getString("CUS_ROOM"));
			model.UNIT_NAME.set(bundle.getString("UNIT_NAME"));
			model.ROAD.set(bundle.getString("ROAD"));
			model.CUS_DOM.set(bundle.getString("CUS_DOM"));
			model.CUS_DY.set(bundle.getString("CUS_DY"));
			model.f_userid.set(bundle.getString("USERID"));
			inspected = bundle.getBoolean("INSPECTED");
			if(bundle.containsKey("READONLY"))
				readonly = bundle.getBoolean("READONLY");
		}
		uuid = Util.getSharedPreference(this, Vault.USER_ID) + "_" + paperId;
		shoot1 = (Button) findViewById(R.id.shoot1);
		shoot1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// ���ð������ݲ�����Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_1");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img1 = (ImageView) findViewById(R.id.image1);
		shoot2 = (Button) findViewById(R.id.shoot2);
		shoot2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// ���ð������ݲ�����Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_2");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img2 = (ImageView) findViewById(R.id.image2);
		shoot3 = (Button) findViewById(R.id.shoot3);
		shoot3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// ���ð������ݲ�����Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_3");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img3 = (ImageView) findViewById(R.id.image3);
		shoot4 = (Button) findViewById(R.id.shoot4);
		shoot4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// ���ð������ݲ�����Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_4");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img4 = (ImageView) findViewById(R.id.image4);
		shoot5 = (Button) findViewById(R.id.shoot5);
		shoot5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// ���ð������ݲ�����Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_5");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img5 = (ImageView) findViewById(R.id.image5);
		Button clear1 = (Button) findViewById(R.id.clear1);
		clear1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img1.setImageBitmap(null);

				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_1.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "1.jpg").delete();
			}
		});
		Button clear2 = (Button) findViewById(R.id.clear2);
		clear2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img2.setImageBitmap(null);

				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_2.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "2.jpg").delete();
			}
		});
		Button clear3 = (Button) findViewById(R.id.clear3);
		clear3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img3.setImageBitmap(null);

				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_3.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "3.jpg").delete();
			}
		});
		Button clear4 = (Button) findViewById(R.id.clear4);
		clear4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img4.setImageBitmap(null);

				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_4.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "4.jpg").delete();
			}
		});
		Button clear5 = (Button) findViewById(R.id.clear5);
		clear5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img5.setImageBitmap(null);

				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_5.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "5.jpg").delete();
			}
		});
		
		OnClickListener imgZoom = new OnClickListener()
		{
			@Override
			public void onClick(View v) {		
				int vid = v.getId();
				if(vid == R.id.image1)
					showZoomDialog(1);
				else if(vid == R.id.image2)
					showZoomDialog(2);
				else if(vid == R.id.image3)
					showZoomDialog(3);
				else if(vid == R.id.image4)
					showZoomDialog(4);
				else if(vid == R.id.image5)
					showZoomDialog(5);
			}
		};
		img1.setOnClickListener(imgZoom);
		img2.setOnClickListener(imgZoom);
		img3.setOnClickListener(imgZoom);
		img4.setOnClickListener(imgZoom);
		img5.setOnClickListener(imgZoom);

		if(readonly)
			DisableLayouts();
		this.preDisplayUIWork();
		this.GetUserInfo(model.f_userid.toString());
	}
	
	public void GetUserInfo(String userID)
	{
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(model.f_userid.get().length() != 0)
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

					String sql = "SELECT t1.f_username, f_phone, f_address, f_cardid, f_meternumber �����," +
							"f_aroundmeter ���ұ�, f_jbfactory ������, tsum, tcount, t1.f_userid from (select * from t_userfiles " +
							"WHERE f_userid = '" + model.f_userid.get() + "') t1" +
											" left join (SELECT f_userid, f_username, SUM (f_pregas) tsum, count(f_pregas) tcount " +
											"FROM t_sellinggas WHERE f_userid = '" + model.f_userid.get() +
													"' group by f_userid, f_username) t2 on t1.f_userid = t2.f_userid";
					String url = Vault.DB_URL + "sql/"
							+ URLEncoder
							.encode(sql, "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// �����������
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
	
	// ��ʾ�û���Ϣ
		private final Handler listHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (1 == msg.what) {
					super.handleMessage(msg);
					try {
						JSONArray array = new JSONArray((String) msg.obj);
						if(array.length() != 1)
						{
							ShowToast("���û���Ϣ���û��������ظ�");
							return;
						}
						JSONObject obj = array.getJSONObject(0);
						if (!obj.has("col0")) {
							// �鲻����IC���û�
							ShowToast("�޴��û���Ϣ");
						} 
						else
						{
							if(obj.has("col0"))
								model.f_consumername.set(obj.getString("col0"));
							if(obj.has("col1"))
								model.f_consumerphone.set(obj.getString("col1"));
							if(obj.has("col2"))
								model.f_address.set(obj.getString("col2"));
							if(obj.has("col3"))
								model.f_kahao.set(obj.getString("col3"));
							if(obj.has("col4"))
								model.f_biaohao.set(obj.getString("col4"));
							if(obj.has("col5"))
								Util.SelectItem(obj.getString("col5"), model.f_rqbiaoxing, ((Spinner)findViewById(R.id.f_rqbiaoxing)));
							if(obj.has("col6"))
								Util.SelectItem(obj.getString("col6"), model.f_biaochang, ((Spinner)findViewById(R.id.f_biaochang)));
							if(obj.has("col7"))
								model.f_buygas.set(obj.getString("col7"));
						}
					} catch (Exception e) {
						e.printStackTrace();
						// �鲻����IC���û�
						ShowToast("�޴��û���Ϣ");
					}
				} else if (0 == msg.what) {
					ShowToast("����������������Ա��ϵ");
				} else if (2 == msg.what) {
					ShowToast("�޴��û�");
				} else if (3 == msg.what) {
					ShowToast("�������û�����");
				}
			}
		};
		
	private void ShowToast(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	//��ʾͼƬ�Ի���
	private void showZoomDialog(int  vid)
	{
		if (!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_" + vid + ".jpg"))
			return;
		ImageView iv = new ImageView(this);
		iv.layout(0, 0, 600, 400);
		Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
				 + uuid + "_" + vid + ".jpg");
		iv.setImageBitmap(bmp);
		Dialog alertDialog = new AlertDialog.Builder(this).   
				setView(iv).
				setTitle("").   
				setIcon(android.R.drawable.ic_dialog_info).
				create();   
		WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();  
        layoutParams.width = 600;
        layoutParams.height= 400;
        alertDialog.getWindow().setAttributes(layoutParams);
		alertDialog.show();
	}

	/**
	 * ������ع���
	 */
	private void preDisplayUIWork() {
		this.IsDispatchRepair = (CheckBox)findViewById(R.id.IsDispatchRepair);
		((Spinner)findViewById(R.id.RepairManList)).setEnabled(false);
		((Spinner)findViewById(R.id.DepartmentList)).setEnabled(false);
		this.IsDispatchRepair.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((Spinner)findViewById(R.id.RepairManList)).setEnabled(isChecked);
				((Spinner)findViewById(R.id.DepartmentList)).setEnabled(isChecked);
				if(!isChecked){
					Util.SelectItem("", model.DepartmentList, (Spinner)findViewById(R.id.DepartmentList));
//					model.RepairManList.clear();
//					model.RepairManList.add(new RepairMan());
					((Spinner)findViewById(R.id.DepartmentList)).setSelection(0);
				}
			}
		});
		//�뻧
		((Switch)findViewById(R.id.f_ruhu)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					findViewById(R.id.f_jujian).setEnabled(isChecked);
					findViewById(R.id.hasNotified).setEnabled(!isChecked);
					//�뻧
					if(isChecked)
					{
						model.hasNotified.set(false);
					}
					else
					{
						model.f_jujian.set(false);
					}
					model.f_ruhu.set(isChecked);
			}
		});
		//�ܼ�
		((Switch)findViewById(R.id.f_jujian)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_jujian.set(isChecked);
			}
		});

		final EditText alarm = (EditText)this.findViewById(R.id.f_alarm_installation_time);
		alarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = alarm.getText().toString();
				Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)");
				Matcher match = pattern.matcher(str);
				int year = 2014, month = 0;
				if(match.find())
				{
					year = Integer.valueOf(match.group(1));
					month = Integer.valueOf(match.group(2))-1;
				}
				DatePickerDialog Dt1 = new DatePickerDialog(IndoorInspectActivity.this,
						new OnDateSetListener() {	
								@Override
								public void onDateSet(DatePicker view, int year,	int monthOfYear, int dayOfMonth) {
									model.f_alarm_installation_time.set(year + "-" + (monthOfYear+1));
									model.f_alarm_expire_time.set(getExpireYear(year, "������ʹ������") + "-" + (monthOfYear+1));
								}
						}, year, month, 1);
						Dt1.show();
			}
		});
		
		final EditText metermadedate = (EditText)this.findViewById(R.id.f_meter_manufacture_date);
		metermadedate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = metermadedate.getText().toString();
				Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)");
				Matcher match = pattern.matcher(str);
				int year = 2014, month = 0;
				if(match.find())
				{
					year = Integer.valueOf(match.group(1));
					month = Integer.valueOf(match.group(2))-1;
				}
				DatePickerDialog Dt1 = new DatePickerDialog(IndoorInspectActivity.this,
						new OnDateSetListener() {	
								@Override
								public void onDateSet(DatePicker view, int year,	int monthOfYear, int dayOfMonth) {
									model.f_meter_manufacture_date.set(year + "-" + (monthOfYear+1));
								}
						}, year, month, 1);
						Dt1.show();
			}
		});

		Spinner spinnerDepartmentList = (Spinner)this.findViewById(R.id.DepartmentList);
		spinnerDepartmentList.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				String s = model.DepartmentList.get(((Spinner)findViewById(R.id.DepartmentList)).getSelectedItemPosition());
				model.RepairManList.clear();
				RepairMan rrr = new RepairMan();
				rrr.id = "null";
				rrr.name = "";
				model.RepairManList.add(rrr);
				if("".equals(s))
					return;
				SQLiteDatabase db = null;
				String sql = "SELECT ID,CODE,NAME FROM T_PARAMS WHERE ID=?";
				try
				{
					db=openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
					Cursor c = db.rawQuery(sql, new String[] { s });
					while(c.moveToNext())
					{
						RepairMan rm = new RepairMan();
						rm.id = c.getString(1);
						rm.name = c.getString(2);
						model.RepairManList.add(rm);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				return;
			}
        });
		
		final EditText cookerDate = (EditText)this.findViewById(R.id.f_cooker_installation_time);
		cookerDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = cookerDate.getText().toString();
				Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)");
				Matcher match = pattern.matcher(str);
				int year = 2014, month = 0;
				if(match.find())
				{
					year = Integer.valueOf(match.group(1));
					month = Integer.valueOf(match.group(2))-1;
				}
				DatePickerDialog Dt1 = new DatePickerDialog(IndoorInspectActivity.this,
						new OnDateSetListener() {	
								@Override
								public void onDateSet(DatePicker view, int year,	int monthOfYear, int dayOfMonth) {
									model.f_cooker_installation_time.set(year + "-" + (monthOfYear+1));
									model.f_cooker_expire_time.set(getExpireYear(year, "���ʹ������") + "-" + (monthOfYear+1));
								}
						}, year, month, 1);
						Dt1.show();
			}
		});
				
		final EditText heaterDate = (EditText)this.findViewById(R.id.f_heater_installation_time);
		heaterDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = heaterDate.getText().toString();
				Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)");
				Matcher match = pattern.matcher(str);
				int year = 2014, month = 0;
				if(match.find())
				{
					year = Integer.valueOf(match.group(1));
					month = Integer.valueOf(match.group(2))-1;
				}
				DatePickerDialog Dt1 = new DatePickerDialog(IndoorInspectActivity.this,
						new OnDateSetListener() {	
								@Override
								public void onDateSet(DatePicker view, int year,	int monthOfYear, int dayOfMonth) {
									model.f_heater_installation_time.set(year + "-" + (monthOfYear+1));
									model.f_heater_expire_time.set(getExpireYear(year, "��ˮ��ʹ������") + "-" + (monthOfYear+1));
								}
						}, year, month, 1);
						Dt1.show();
			}
		});
				
		final EditText furnacerDate = (EditText)this.findViewById(R.id.f_furnace_installation_time);
		furnacerDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = furnacerDate.getText().toString();
				Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)");
				Matcher match = pattern.matcher(str);
				int year = 2014, month = 0;
				if(match.find())
				{
					year = Integer.valueOf(match.group(1));
					month = Integer.valueOf(match.group(2))-1;
				}
				DatePickerDialog Dt1 = new DatePickerDialog(IndoorInspectActivity.this,
						new OnDateSetListener() {	
								@Override
								public void onDateSet(DatePicker view, int year,	int monthOfYear, int dayOfMonth) {
									model.f_furnace_installation_time.set(year + "-" + (monthOfYear+1));
									model.f_furnace_expire_time.set(getExpireYear(year, "�ڹ�¯ʹ������") + "-" + (monthOfYear+1));
								}
						}, year, month, 1);
						Dt1.show();
			}
		});

		((Spinner)this.findViewById(R.id.f_baojingqi)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position ==1)
				{
					System.out.println(position);
					((Spinner)IndoorInspectActivity.this.findViewById(R.id.f_baojingqichang)).setEnabled(true);
					((EditText)IndoorInspectActivity.this.findViewById(R.id.f_alarm_installation_time)).setEnabled(true);
					((EditText)IndoorInspectActivity.this.findViewById(R.id.f_alarm_expire_time)).setEnabled(true);
				}
				else
				{
					System.out.println(position);
					((Spinner)IndoorInspectActivity.this.findViewById(R.id.f_baojingqichang)).setEnabled(false);
					((EditText)IndoorInspectActivity.this.findViewById(R.id.f_alarm_installation_time)).setEnabled(false);
					((EditText)IndoorInspectActivity.this.findViewById(R.id.f_alarm_expire_time)).setEnabled(false);
					model.f_alarm_installation_time.set("");
					model.f_alarm_expire_time.set("");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {			
			}
			
		});
		
		((Spinner)this.findViewById(R.id.f_iccard_type)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//��ͨ����������
				if(position ==1)
				{
					IndoorInspectActivity.this.findViewById(R.id.f_shengyu).setEnabled(true);
					IndoorInspectActivity.this.findViewById(R.id.f_balance).setEnabled(false);
					model.f_balance.set("");
				}
				else if(position >1)
				{
					IndoorInspectActivity.this.findViewById(R.id.f_shengyu).setEnabled(false);
					IndoorInspectActivity.this.findViewById(R.id.f_balance).setEnabled(true);
					try
					{
						double d = Double.parseDouble(model.f_balance.get());
						d = d /getGasPrice();
						model.f_shengyu.set(String.format("%.2f", d));
					}
					catch(Exception e)
					{
						model.f_shengyu.set("");	
					}									
				}
				else
				{
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			
			}
		});
		
		((EditText)this.findViewById(R.id.f_balance)).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String balance = s.toString();
				try
				{
					double d = Double.parseDouble(balance);
					d = d /getGasPrice();
					model.f_shengyu.set(String.format("%.2f", d));
				}
				catch(Exception e)
				{
					model.f_shengyu.set("");	
				}				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});

	};

	private double getGasPrice() {
		double price = 0;
		SQLiteDatabase db = null;
		try
		{
			db = this.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String sql = "select code from T_PARAMS where name='����'";
			Cursor c = db.rawQuery(sql, new String[]{});
			if(c.moveToNext())
			{
				price = Double.parseDouble(c.getString(0));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.close();
		}	
		return price;
	}
	
	private int getExpireYear(int year, String name) {
		SQLiteDatabase db = null;
		try
		{
			db = this.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String sql = "select code from T_PARAMS where name=? ";
			Cursor c = db.rawQuery(sql, new String[] { name });
			if(c.moveToNext())
			{
				year = year + Integer.parseInt(c.getString(0));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.close();
		}	
		return year;
	}	
	
	/**
	 * �����ұ߲���Ϊ��ֹʹ��
	 */
	private void DisableLayouts() {
		MyDigitalClock clock = (MyDigitalClock)findViewById(R.id.digitalClock);
		int[] panes = {R.id.basicPane, R.id.meterPane, R.id.plumPane, R.id.cookerPane, R.id.precautionPane, R.id.feedbackPane};
		for(int i=0; i<panes.length; i++)
		{
			ViewGroup vg = (ViewGroup)findViewById(panes[i]);
			disable(vg);
		}
	}
	
	/**
	 * disable every view in the layout recursively
	 * @param layout
	 */
	private void disable(ViewGroup layout) {
		layout.setEnabled(false);
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			if (child instanceof ViewGroup) {
				disable((ViewGroup) child);
			} else {
				child.setEnabled(false);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null)
			return;
		//�ж��Ƿ��ǲ����û���Ϣ���淵�ص�����
		if (resultCode == 130) {
			//TODO
			this.status = true;
			model.f_consumername.set(intent.getStringExtra("userName"));
			model.f_consumerphone.set(intent.getStringExtra("telephone"));
			model.f_address.set(intent.getStringExtra("address"));
			model.f_kahao.set(intent.getStringExtra("cardID"));
			model.city.set(intent.getStringExtra("city"));
			model.f_area.set(intent.getStringExtra("area"));
			model.f_biaohao.set(intent.getStringExtra("biaohao"));
			Util.SelectItem(intent.getStringExtra("zuoyoubiao"), model.f_rqbiaoxing, ((Spinner)findViewById(R.id.f_rqbiaoxing)));
			Util.SelectItem(intent.getStringExtra("biaochang"), model.f_biaochang, ((Spinner)findViewById(R.id.f_biaochang)));
			model.f_buygas.set(intent.getStringExtra("tsum"));
			model.f_userid.set(intent.getStringExtra("userID"));
			/*model.ROAD2.set(intent.getStringExtra("road"));
			model.UNIT_NAME2.set(intent.getStringExtra("districtname"));
			model.CUS_DOM2.set(intent.getStringExtra("cusDom"));
			model.CUS_DY2.set(intent.getStringExtra("cusDy"));
			model.CUS_FLOOR2.set(intent.getStringExtra("cusFloor"));
			model.CUS_ROOM2.set(intent.getStringExtra("apartment"));*/
			//model.f_kahao.set(intent.getStringExtra("tcount"));
			model.f_archiveaddress.set(intent.getStringExtra("f_archiveaddress"));
			setAddressPart(model.f_archiveaddress.get());
		}
		else
		{
			String result = intent.getStringExtra("result");
			Bitmap bmp;
			if(intent.hasExtra("signature"))
			{
				if(!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".png"))
					return;
				bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".png");
				((ImageView)findViewById(R.id.signPad)).setImageBitmap(bmp);
			}
			else
			{
				if(!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".jpg"))
					return;
				bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".jpg");
				String idx = result.substring(result.length() - 1);
				if (idx.equals("1"))
					img1.setImageBitmap(bmp);
				else if (idx.equals("2"))
					img2.setImageBitmap(bmp);
				else if (idx.equals("3"))
					img3.setImageBitmap(bmp);
				else if (idx.equals("4"))
					img4.setImageBitmap(bmp);
				else if (idx.equals("5"))
					img5.setImageBitmap(bmp);
			}
		}
	}

	/**
	 * ������֤
	 * 
	 * @return
	 */
	public boolean validate() {
		//���δ�뻧��ܼ�
		if(!model.f_ruhu.get() || model.f_jujian.get())
			return true;
		// �Ƚ�������У��
		String output = "";
		ValidationResult result = ModelValidator.ValidateModel(model);
		if (!result.isValid()) {
			output = "����:  \n";
			for (String msg : result.getValidationErrors()) {
				output += msg + "\n";
			}
		}
		if (output.length() > 0) {
			Toast.makeText(this, output, Toast.LENGTH_LONG).show();
			return false;
		} 		
		
		if(!model.f_zjshiyong.get() && model.f_zjpinpai.get().trim().length()==0)
		{
			Toast.makeText(this, "���������Ʒ�ơ�", Toast.LENGTH_LONG).show();
			return false;			
		}			
		if(!model.f_rshqshiyong.get() && model.f_rshqpinpai.get().trim().length()==0)
		{
			Toast.makeText(this, "��������ˮ��Ʒ�ơ�", Toast.LENGTH_LONG).show();
			return false;			
		}			
		if(!model.f_bglshiyong.get() && model.f_bglpinpai.get().trim().length()==0)
		{
			Toast.makeText(this, "������ڹ�¯Ʒ�ơ�", Toast.LENGTH_LONG).show();
			return false;			
		}			

		if(model.f_qbqita.get() && model.f_qibiao.get().trim().length()==0)
		{
			Toast.makeText(this, "����д�����������ϡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!model.f_qbqita.get() && model.f_qibiao.get().trim().length()>0)
		{
			Toast.makeText(this, "δѡ�����������ϣ�������д������", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(model.f_lgzhengchang.get() || model.f_lgfushi.get() || model.f_lgsigai.get() || model.f_lglouqi.get() || model.f_lgbubianweixiu.get() || model.f_lgbaoguo.get() || model.f_lgguawu.get() || model.f_lgweiguding.get() || model.f_lgchuanyue.get() || model.f_lghuoyuan.get()))
		{
			Toast.makeText(this, "��ѡ������ѡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(model.f_lgzhengchang.get() &&(model.f_lgfushi.get() || model.f_lgsigai.get() || model.f_lglouqi.get() || model.f_lgbubianweixiu.get() || model.f_lgbaoguo.get() || model.f_lgguawu.get() || model.f_lgweiguding.get() || model.f_lgchuanyue.get() || model.f_lghuoyuan.get()))
		{
			Toast.makeText(this, "���ܹ���ѡ��ì�ܡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(model.f_bhgzhengchang.get() || model.f_bhgbaoguan.get()||model.f_bhglouqi.get() || model.f_bhggaiguan.get() || model.f_bhgdianyuan.get() || model.f_bhgwxd.get()
				|| model.f_bhgguawu.get() || model.f_bhgjinzhiquyu.get() || model.f_bhgfushi.get() || model.f_bhgqita.get() || model.f_bhgrst.get() || model.f_bhgbubianweixiu.get()))
		{
			Toast.makeText(this, "��ѡ���������ѡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(model.f_bhgzhengchang.get() && (model.f_bhgbaoguan.get()||model.f_bhglouqi.get()|| model.f_bhggaiguan.get() || model.f_bhgdianyuan.get() ||  model.f_bhgwxd.get()
				|| model.f_bhgguawu.get() || model.f_bhgjinzhiquyu.get() || model.f_bhgfushi.get() || model.f_bhgqita.get() || model.f_bhgrst.get() || model.f_bhgbubianweixiu.get()))
		{
			Toast.makeText(this, "��������ѡ��ì�ܡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(model.f_jpgzhengchang.get() || model.f_jpglouqi.get()||model.f_jpglaohua.get()||model.f_jpgguochang.get() || model.f_jpgdiaoding.get() || model.f_jpgwuguanjia.get() || model.f_jpganmai.get()))
		{
			Toast.makeText(this, "��ѡ��Ƥ��ѡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(model.f_jpgzhengchang.get() && ( model.f_jpglouqi.get()||model.f_jpglaohua.get()||model.f_jpgguochang.get() || model.f_jpgdiaoding.get() || model.f_jpgwuguanjia.get() || model.f_jpganmai.get()))
		{
			Toast.makeText(this, "��Ƥ��ѡ��ì�ܡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_gongnuan)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ�й�ů��ʽ��", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_zuzhu)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ����ס���͡�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_property)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ��С�����ʡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		/*
		if(((Spinner)this.findViewById(R.id.REGION_NAME)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ��������롣", Toast.LENGTH_LONG).show();
			return false;			
		}
		*/
		if(((Spinner)this.findViewById(R.id.f_iszhongdian)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ���ص��û���", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_baojingqi)).getSelectedItem().toString().equals("��"))
		{
			if(((Spinner)this.findViewById(R.id.f_baojingqichang)).getSelectedItem().toString().length()==0 )
			{
				Toast.makeText(this, "��ѡ�񱨾������ҡ�", Toast.LENGTH_LONG).show();
				return false;			
			}
			if(model.f_alarm_installation_time.get()==null || model.f_alarm_installation_time.get().length()==0)
			{
				Toast.makeText(this, "�����뱨��������ʱ�䡣", Toast.LENGTH_LONG).show();
				return false;			
			}
		}
		/*if(((Spinner)this.findViewById(R.id.f_baojingqichang)).getSelectedItem().toString().length()==0 && ((Spinner)this.findViewById(R.id.f_baojingqichang)).isEnabled())
		{
			Toast.makeText(this, "��ѡ�񱨾������ҡ�", Toast.LENGTH_LONG).show();
			return false;			
		}*/
//		if(((Spinner)this.findViewById(R.id.f_meter_type)).getSelectedItem().toString().length()==0)
//		{
//			Toast.makeText(this, "��ѡ����͡�", Toast.LENGTH_LONG).show();
//			return false;			
//		}
//		if(((Spinner)this.findViewById(R.id.f_biaochang)).getSelectedItem().toString().length()==0)
//		{
//			Toast.makeText(this, "��ѡ������ҡ�", Toast.LENGTH_LONG).show();
//			return false;			
//		}
//		if(((Spinner)this.findViewById(R.id.f_kachangjia)).getSelectedItem().toString().length()==0)
//		{
//			Toast.makeText(this, "��ѡ���IC�����ҡ�", Toast.LENGTH_LONG).show();
//			return false;			
//		}
//		if(((Spinner)this.findViewById(R.id.f_iccard_type)).getSelectedItem().toString().length()==0)
//		{
//			Toast.makeText(this, "��ѡ��IC�������͡�", Toast.LENGTH_LONG).show();
//			return false;			
//		}
//		if(((Spinner)this.findViewById(R.id.f_meter_cover)).getSelectedItem().toString().length()==0)
//		{
//			Toast.makeText(this, "��ѡ���⡣", Toast.LENGTH_LONG).show();
//			return false;			
//		}
		if(((Spinner)this.findViewById(R.id.f_plumbing_type)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ���������͡�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_metervalve_type)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ���ǰ�����͡�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_biaoqianfa)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ���ǰ����", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_zaoqianfa)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ����ǰ����", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(((Spinner)this.findViewById(R.id.f_zibifa)).getSelectedItem().toString().length()==0)
		{
			Toast.makeText(this, "��ѡ���Աշ���", Toast.LENGTH_LONG).show();
			return false;			
		}

		if(!model.f_zjshiyong.get())
		{
			if(((Spinner)this.findViewById(R.id.f_zjleixing)).getSelectedItem().toString().length()==0)
			{
				Toast.makeText(this, "��ѡ��������͡�", Toast.LENGTH_LONG).show();
				return false;			
			}
			//

			if(((Spinner)this.findViewById(R.id.f_zjxianzhuang)).getSelectedItem().toString().length()==0)
			{
				Toast.makeText(this, "��ѡ�������״��", Toast.LENGTH_LONG).show();
				return false;			
			}
		}
		if(!model.f_rshqshiyong.get())
		{
			if(((Spinner)this.findViewById(R.id.f_rshqxianzhuang)).getSelectedItem().toString().length()==0)
			{
				Toast.makeText(this, "��ѡ����ˮ����״��", Toast.LENGTH_LONG).show();
				return false;			
			}
			//

			if(((Spinner)this.findViewById(R.id.f_heater_place)).getSelectedItem().toString().length()==0)
			{
				Toast.makeText(this, "��ѡ����ˮ����װλ�á�", Toast.LENGTH_LONG).show();
				return false;			
			}
		}
		if(!model.f_bglshiyong.get())
		{
			if(((Spinner)this.findViewById(R.id.f_bglxianzhuang)).getSelectedItem().toString().length()==0)
			{
				Toast.makeText(this, "��ѡ��ڹ�¯��״��", Toast.LENGTH_LONG).show();
				return false;			
			}
			//

			if(((Spinner)this.findViewById(R.id.f_bglweizhi)).getSelectedItem().toString().length()==0)
			{
				Toast.makeText(this, "��ѡ��ڹ�¯λ�á�", Toast.LENGTH_LONG).show();
				return false;			
			}
		}
		//����������
		if(((Spinner)this.findViewById(R.id.f_iccard_type)).getSelectedItemPosition() ==1)
		{
			try
			{
				Double.parseDouble(model.f_shengyu.get());
			}
			catch(Exception e)
			{
				Toast.makeText(this, "ʣ����������Ϊ���֡�", Toast.LENGTH_LONG).show();
				return false;			
			}
		}
		else
		{
			try
			{
				Double.parseDouble(model.f_balance.get());
			}
			catch(Exception e)
			{
				Toast.makeText(this, "ʣ�������Ϊ���֡�", Toast.LENGTH_LONG).show();
				return false;			
			}
		}
		return true;
	}

	// ���ر��氲���¼
	public boolean Save(String objStr, String inspectionTable, boolean isTemp) {
		try {
			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);
			JSONObject row = new JSONObject(objStr);
			String uuid = row.getString("ID");
			String paperId = row.getString("CHECKPAPER_ID");
			// ɾ�������
			db.execSQL("DELETE FROM " + inspectionTable + "  where CHECKPAPER_ID=" + paperId);
			String sql1 = "INSERT INTO " + inspectionTable + " (ID";
			String sql2 = ") VALUES(" + uuid;
			Iterator<String> itr = row.keys();
			while (itr.hasNext()) {
				String key = itr.next();
				if (key.equals("ID") || key.equals("suggestions"))
					continue;
				sql1 += "," + key;
				sql2 += "," + row.getString(key);
			}
			sql1 += sql2 + ")";
			db.execSQL(sql1);
			db.close();
			if(!isTemp)
			{
				//���°���״̬		
				String state = row.getString("CONDITION");
				boolean needsRepair = false;
				if(row.has("NEEDS_REPAIR"))
					 needsRepair = row.getString("NEEDS_REPAIR").equals("'��'");
				SetInspectionState(paperId, state, needsRepair);
			}
			//����
			this.saveSuggestions(this.collectSuggestionsFromUI(), uuid);
			return true;

		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
			return false;
		}
	}

	/**
	 * ���°���״̬
	 * @param paperId
	 * @param state
	 * @param needsRepair 
	 */
	private void SetInspectionState(String paperId, String state, boolean needsRepair) {
		if(paperId.startsWith("'"))
			paperId= paperId.substring(1, paperId.length()-1);
		if(state.startsWith("'"))
			state= state.substring(1, state.length()-1);
		
		if(state.equals("����"))
		{
			Util.SetBit(this, Vault.NOANSWER_FLAG, paperId);
		}
		else if(state.equals("�ܼ�"))
		{
			Util.SetBit(this, Vault.DENIED_FLAG , paperId);
		}
		else
		{
			Util.SetBit(this, Vault.INSPECT_FLAG, paperId);
			if(needsRepair)
				Util.SetBit(this, Vault.REPAIR_FLAG, paperId);
			else
				Util.ClearBit(this, Vault.REPAIR_FLAG, paperId);
		}
	}


	/**
	 * ��ҳ���ռ������ֶε�ֵ
	 */
	public String SaveToJSONString(boolean saveRepair, boolean upload) {
		JSONObject row = new JSONObject();
		try {
			row.put("ID", Util.quote(uuid));
			// ���쵥ID
			row.put("CHECKPAPER_ID", Util.quote(this.paperId));
			row.put("f_userid", Util.quote(model.f_userid.get()));
			//����ID
			row.put("CHECKPLAN_ID", Util.quote(this.planId));
			if (!model.f_ruhu.get())
				row.put("CONDITION", Util.quote("����"));
			if (model.f_jujian.get())
				row.put("CONDITION", Util.quote("�ܼ�"));
			row.put("f_ruhu", Util.unquote(model.f_ruhu.get()));
			row.put("f_jujian",Util.unquote(model.f_jujian.get()));
			row.put("SAVE_PEOPLE", Util.quote(Util.getSharedPreference(this, Vault.CHECKER_NAME)));
			// �ѷ����ò�����
			if (model.hasNotified.get())
				row.put("hasNotified", Util.quote("�ѷ�"));
			else
				row.put("hasNotified", Util.quote(""));
			//�����ϴ�
			if(!upload)
			{
				// ����ʱ��
				row.put("ARRIVAL_TIME", Util.quote(model.f_anjianriqi.get() + " "	+ model.ArrivalTime.get()));
				// �뿪ʱ��
				String tm = ((MyDigitalClock)this.findViewById(R.id.digitalClock)).getText().toString();
				row.put("DEPARTURE_TIME",Util.quote( model.f_anjianriqi.get() + " " + tm));
			}
			else
			{
				// ����ʱ��Ϊ�������ʱ��
				row.put("ARRIVAL_TIME", Util.quote(Util.FormatDate("yyyy-MM-dd HH:mm:ss", entryDateTime.getTime())));
				//�뿪ʱ��Ϊ��ǰʱ��
				row.put("DEPARTURE_TIME", Util.quote(Util.FormatDate("yyyy-MM-dd HH:mm:ss", new Date().getTime())));
			}
			// С������
			row.put("UNIT_NAME", Util.quote(model.UNIT_NAME.get()));
			// С����ַ
			row.put("ROAD", Util.quote(model.ROAD.get()));
			// ¥��
			row.put("CUS_DOM", Util.quote(model.CUS_DOM.get()));
			// ��Ԫ
			row.put("CUS_DY", Util.quote(model.CUS_DY.get()));
			// ¥��
			row.put("CUS_FLOOR", Util.quote(model.CUS_FLOOR.get()));
			// ����
			row.put("CUS_ROOM", Util.quote(model.CUS_ROOM.get()));
			if (!row.has("CONDITION"))
				// ������
				row.put("CONDITION", Util.quote("����"));
			else
			{
				return row.toString();
			}
			row.put("f_cardnum", Util.quote(model.f_kahao.get()));
			row.put("f_kahao", Util.quote(model.f_kahao.get()));
			row.put("f_consumername", Util.quote(model.f_consumername.get()));
			row.put("f_consumerphone", Util.quote(model.f_consumerphone.get()));
			row.put("city", Util.quote(model.city.get()));
			row.put("f_area", Util.quote(model.f_area.get()));
			row.put("f_renkou", Util.unquote(model.f_renkou.get()));
			row.put("f_alarm_installation_time", Util.quote(model.f_alarm_installation_time.get()));
			row.put("f_alarm_expire_time", Util.quote(model.f_alarm_expire_time.get()));
			row.put("f_meter_manufacture_date", Util.quote(model.f_meter_manufacture_date.get()));
			row.put("f_biaohao", Util.quote(model.f_biaohao.get()));
			row.put("f_jbdushu", Util.unquote(model.f_jbdushu.get()));
			row.put("f_shengyu", Util.unquote(model.f_shengyu.get()));
			row.put("f_balance", Util.unquote(model.f_balance.get()));
			row.put("f_buygas", Util.unquote(model.f_buygas.get()));
			row.put("f_newmeter", Util.unquote(model.f_newmeter.get()));
			
			row.put("f_changtong", Util.unquote(model.f_changtong.get()));
			row.put("f_sibiao", Util.unquote(model.f_sibiao.get()));
			row.put("f_reading_mismatch", Util.unquote(model.f_reading_mismatch.get()));
			row.put("f_fanzhuang", Util.unquote(model.f_fanzhuang.get()));
			row.put("f_qblouqi", Util.unquote(model.f_qblouqi.get()));
			row.put("f_qbqita", Util.unquote(model.f_qbqita.get()));
			row.put("f_qibiao", Util.quote(model.f_qibiao.get()));
			
			row.put("f_meter_wrapped", Util.unquote(model.f_meter_wrapped.get()));
			row.put("f_meter_hanger", Util.unquote(model.f_meter_hanger.get()));
			row.put("f_meter_nearfire", Util.unquote(model.f_meter_nearfire.get()));
			row.put("f_meter_unfavorable", Util.unquote(model.f_meter_unfavorable.get()));

			row.put("f_plumbing_valve", Util.unquote(model.f_plumbing_valve.get()));
			row.put("f_plumbing_slipknot", Util.unquote(model.f_plumbing_slipknot.get()));
			row.put("f_plumbing_scaleknot", Util.unquote(model.f_plumbing_scaleknot.get()));
			row.put("f_plumbing_diameter", Util.unquote(model.f_plumbing_diameter.get()));
			
			row.put("f_lgzhengchang", Util.unquote(model.f_lgzhengchang.get()));
			row.put("f_lgfushi", Util.unquote(model.f_lgfushi.get()));
			row.put("f_lgsigai", Util.unquote(model.f_lgsigai.get()));
			row.put("f_lglouqi", Util.unquote(model.f_lglouqi.get()));
			row.put("f_lgbaoguo", Util.unquote(model.f_lgbaoguo.get()));
			row.put("f_lgguawu", Util.unquote(model.f_lgguawu.get()));
			row.put("f_lghuoyuan", Util.unquote(model.f_lghuoyuan.get()));
			row.put("f_lgweiguding", Util.unquote(model.f_lgweiguding.get()));
			row.put("f_lgchuanyue", Util.unquote(model.f_lgchuanyue.get()));
			row.put("f_lgbubianweixiu", Util.unquote(model.f_lgbubianweixiu.get()));
		
			row.put("f_plumbing_leakage_valve", Util.unquote(model.f_plumbing_leakage_valve.get()));
			row.put("f_plumbing_leakage_scaleknot", Util.unquote(model.f_plumbing_leakage_scaleknot.get()));
			row.put("f_plumbing_leakage_slipknot", Util.unquote(model.f_plumbing_leakage_slipknot.get()));
			row.put("f_plumbing_leakage_triple", Util.unquote(model.f_plumbing_leakage_triple.get()));
			row.put("f_plumbing_leakage_diameter", Util.unquote(model.f_plumbing_leakage_diameter.get()));
			
			row.put("f_bhgzhengchang", Util.unquote(model.f_bhgzhengchang.get()));
			row.put("f_bhgbaoguan", Util.unquote(model.f_bhgbaoguan.get()));
			row.put("f_bhglouqi", Util.unquote(model.f_bhglouqi.get()));
			row.put("f_bhggaiguan", Util.unquote(model.f_bhggaiguan.get()));
			row.put("f_bhgdianyuan", Util.unquote(model.f_bhgdianyuan.get()));
			row.put("f_bhgwxd",Util.unquote(model.f_bhgwxd.get()));
			row.put("f_bhgguawu", Util.unquote(model.f_bhgguawu.get()));
			row.put("f_bhgjinzhiquyu", Util.unquote(model.f_bhgjinzhiquyu.get()));
			row.put("f_bhgrst", Util.unquote(model.f_bhgrst.get()));
			row.put("f_bhgfushi", Util.unquote(model.f_bhgfushi.get()));
			row.put("f_bhgbubianweixiu", Util.unquote(model.f_bhgbubianweixiu.get()));
			row.put("f_bhgqita", Util.unquote(model.f_bhgqita.get()));
			row.put("f_bhgbeizhu", Util.quote(model.f_bhgbeizhu.get()));

			row.put("f_jpgzhengchang", Util.unquote(model.f_jpgzhengchang.get()));
			row.put("f_jpglouqi", Util.unquote(model.f_jpglouqi.get()));
			row.put("f_jpglaohua", Util.unquote(model.f_jpglaohua.get()));
			row.put("f_jpgguochang", Util.unquote(model.f_jpgguochang.get()));
			row.put("f_jpgdiaoding", Util.unquote(model.f_jpgdiaoding.get()));
			row.put("f_jpgwuguanjia", Util.unquote(model.f_jpgwuguanjia.get()));
			row.put("f_jpganmai", Util.unquote(model.f_jpganmai.get()));
			
			row.put("f_zjshiyong", Util.unquote(model.f_zjshiyong.get()));

			row.put("f_rshqshiyong", Util.unquote(model.f_rshqshiyong.get()));
			row.put("f_rshqxinghao", Util.quote(model.f_rshqxinghao.get()));

			row.put("f_bglshiyong", Util.unquote(model.f_bglshiyong.get()));
			row.put("f_bglgonglv", Util.unquote(model.f_bglgonglv.get()));
		
			row.put("f_precaution_kitchen", Util.unquote(model.f_precaution_kitchen.get()));
			row.put("f_precaution_multisource", Util.unquote(model.f_precaution_multisource.get()));
			row.put("f_precaution_otheruse", Util.quote(model.f_precaution_otheruse.get(((Spinner)findViewById(R.id.f_precaution_otheruse)).getSelectedItemPosition())));
			row.put("f_renow_id", Util.quote(model.f_renow_id.get()));
			row.put("f_zgbeizhu", Util.quote(model.f_zgbeizhu.get()));
			
			row.put("f_archiveaddress", Util.quote(model.f_archiveaddress.get()));
			
			row.put("f_checktype", Util.quote(model.f_checktype.get(((Spinner)findViewById(R.id.f_checktype)).getSelectedItemPosition())));
			row.put("f_property", Util.quote(model.f_property.get(((Spinner)findViewById(R.id.f_property)).getSelectedItemPosition())));
			row.put("f_iszhongdian", Util.quote(model.f_iszhongdian.get(((Spinner)findViewById(R.id.f_iszhongdian)).getSelectedItemPosition())));
			row.put("f_gongnuan", Util.quote(model.f_gongnuan.get(((Spinner)findViewById(R.id.f_gongnuan)).getSelectedItemPosition())));
			row.put("f_zuzhu", Util.quote(model.f_zuzhu.get(((Spinner)findViewById(R.id.f_zuzhu)).getSelectedItemPosition())));
			row.put("REGION_NAME", Util.quote(model.REGION_NAME.get(((Spinner)findViewById(R.id.REGION_NAME)).getSelectedItemPosition())));
			
			row.put("f_baojingqi", Util.quote(model.f_baojingqi.get(((Spinner)findViewById(R.id.f_baojingqi)).getSelectedItemPosition())));
			row.put("f_baojingqichang", Util.quote(model.f_baojingqichang.get(((Spinner)findViewById(R.id.f_baojingqichang)).getSelectedItemPosition())));

			row.put("f_rqbiaoxing", Util.quote(model.f_rqbiaoxing.get(((Spinner)findViewById(R.id.f_rqbiaoxing)).getSelectedItemPosition())));
			row.put("f_meter_type", Util.quote(model.f_meter_type.get(((Spinner)findViewById(R.id.f_meter_type)).getSelectedItemPosition())));
			row.put("f_iccard_type", Util.quote(model.f_iccard_type.get(((Spinner)findViewById(R.id.f_iccard_type)).getSelectedItemPosition())));
			row.put("f_meter_cover", Util.quote(model.f_meter_cover.get(((Spinner)findViewById(R.id.f_meter_cover)).getSelectedItemPosition())));
			row.put("f_biaochang", Util.quote(model.f_biaochang.get(((Spinner)findViewById(R.id.f_biaochang)).getSelectedItemPosition())));
			row.put("f_kachangjia", Util.quote(model.f_kachangjia.get(((Spinner)findViewById(R.id.f_kachangjia)).getSelectedItemPosition())));

			row.put("f_plumbing_type", Util.quote(model.f_plumbing_type.get(((Spinner)findViewById(R.id.f_plumbing_type)).getSelectedItemPosition())));
			row.put("f_metervalve_type", Util.quote(model.f_metervalve_type.get(((Spinner)findViewById(R.id.f_metervalve_type)).getSelectedItemPosition())));
			row.put("f_biaoqianfa", Util.quote(model.f_biaoqianfa.get(((Spinner)findViewById(R.id.f_biaoqianfa)).getSelectedItemPosition())));
			row.put("f_zaoqianfa", Util.quote(model.f_zaoqianfa.get(((Spinner)findViewById(R.id.f_zaoqianfa)).getSelectedItemPosition())));
			row.put("f_zibifa", Util.quote(model.f_zibifa.get(((Spinner)findViewById(R.id.f_zibifa)).getSelectedItemPosition())));

			row.put("f_zjpinpai", Util.quote(model.f_zjpinpai.get()));
			row.put("f_zjleixing", Util.quote(model.f_zjleixing.get(((Spinner)findViewById(R.id.f_zjleixing)).getSelectedItemPosition())));
			row.put("f_zjxianzhuang", Util.quote(model.f_zjxianzhuang.get(((Spinner)findViewById(R.id.f_zjxianzhuang)).getSelectedItemPosition())));
			row.put("f_cooker_installation_time", Util.quote(model.f_cooker_installation_time.get()));
			row.put("f_cooker_expire_time", Util.quote(model.f_cooker_expire_time.get()));
			row.put("f_cooker_overdue", Util.unquote(model.f_cooker_overdue.get()));
			row.put("f_cooker_nofireprotection", Util.unquote(model.f_cooker_nofireprotection.get()));
			row.put("f_cooker_leakage", Util.unquote(model.f_cooker_leakage.get()));
			row.put("f_cooker_precaution_remark", Util.quote(model.f_cooker_precaution_remark.get()));
			
			row.put("f_rshqpinpai", Util.quote(model.f_rshqpinpai.get()));
			row.put("f_rshqxianzhuang", Util.quote(model.f_rshqxianzhuang.get(((Spinner)findViewById(R.id.f_rshqxianzhuang)).getSelectedItemPosition())));
			row.put("f_heater_installation_time", Util.quote(model.f_heater_installation_time.get()));
			row.put("f_heater_expire_time", Util.quote(model.f_heater_expire_time.get()));
			row.put("f_heater_place", Util.quote(model.f_heater_place.get(((Spinner)findViewById(R.id.f_heater_place)).getSelectedItemPosition())));
			row.put("f_heater_overdue", Util.unquote(model.f_heater_overdue.get()));
			row.put("f_heater_softconnector", Util.unquote(model.f_heater_softconnector.get()));
			row.put("f_heater_trapped", Util.unquote(model.f_heater_trapped.get()));
			row.put("f_heater_leakage", Util.unquote(model.f_heater_leakage.get()));
			row.put("f_heater_leakage_connetor", Util.unquote(model.f_heater_leakage_connetor.get()));
			row.put("f_heater_leakage_valve", Util.unquote(model.f_heater_leakage_valve.get()));
			row.put("f_heater_leakage_heater", Util.unquote(model.f_heater_leakage_heater.get()));
			row.put("f_heater_precaution_remark", Util.quote(model.f_heater_precaution_remark.get()));

			row.put("f_bglpinpai", Util.quote(model.f_bglpinpai.get()));
			row.put("f_bglweizhi", Util.quote(model.f_bglweizhi.get(((Spinner)findViewById(R.id.f_bglweizhi)).getSelectedItemPosition())));
			row.put("f_bglxianzhuang", Util.quote(model.f_bglxianzhuang.get(((Spinner)findViewById(R.id.f_bglxianzhuang)).getSelectedItemPosition())));
			row.put("f_furnace_installation_time", Util.quote(model.f_furnace_installation_time.get()));
			row.put("f_furnace_expire_time", Util.quote(model.f_furnace_expire_time.get()));
			row.put("f_furnace_overdue", Util.unquote(model.f_furnace_overdue.get()));
			row.put("f_furnace_softconnector", Util.unquote(model.f_furnace_softconnector.get()));
			row.put("f_furnace_trapped", Util.unquote(model.f_furnace_trapped.get()));
			row.put("f_furnace_leakage", Util.unquote(model.f_furnace_leakage.get()));
			row.put("f_furnace_leakage_connetor", Util.unquote(model.f_furnace_leakage_connetor.get()));
			row.put("f_furnace_leakage_valve", Util.unquote(model.f_furnace_leakage_valve.get()));
			row.put("f_furnace_leakage_furnace", Util.unquote(model.f_furnace_leakage_furnace.get()));
			row.put("f_furnace_precaution_remark", Util.quote(model.f_furnace_precaution_remark.get()));
			
			// �û�����
			if (((RadioButton)findViewById(R.id.FeebackSatisfied)).isChecked())
				row.put("f_kehupingjia", Util.quote("����"));
			else if (((RadioButton)findViewById(R.id.FeebackOK)).isChecked())
				row.put("f_kehupingjia", Util.quote("��������"));
			else if (((RadioButton)findViewById(R.id.FeebackUnsatisfied)).isChecked())
				row.put("f_kehupingjia",Util.quote("������"));
			// ǩ��
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_sign.png"))
				row.put("USER_SIGN", Util.quote(uuid + "_sign"));
			// ͼƬ
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid  + "_1.jpg"))
				row.put("PHOTO_FIRST", Util.quote(uuid + "_1"));
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_2.jpg"))
				row.put("PHOTO_SECOND", Util.quote(uuid + "_2"));
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_3.jpg"))
				row.put("PHOTO_THIRD", Util.quote(uuid + "_3"));
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_4.jpg"))
				row.put("PHOTO_FOUTH", Util.quote(uuid + "_4"));
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_5.jpg"))
				row.put("PHOTO_FIFTH", Util.quote(uuid + "_5"));
			//ά�����
			if(this.IsDispatchRepair.isChecked() && saveRepair)
			{
				row.put("NEEDS_REPAIR", Util.quote("��"));
				Spinner spinner = ((Spinner)findViewById(R.id.RepairManList));
				RepairMan repairMan = (RepairMan)model.RepairManList.get((int)spinner.getSelectedItemId());
				row.put("REPAIRMAN",Util.quote(repairMan.name));
				row.put("REPAIRMAN_ID", Util.quote(repairMan.id));
				row.put("REPAIR_STATE", Util.quote("δά��" ));
				row.put("f_department", Util.quote(this.model.DepartmentList.get(((Spinner)findViewById(R.id.DepartmentList)).getSelectedItemPosition())));
			}
			else
			{
				row.put("NEEDS_REPAIR", Util.quote("��"));
				row.put("REPAIRMAN",Util.quote(""));
				row.put("REPAIRMAN_ID", Util.quote(""));
				row.put("REPAIR_STATE", Util.quote(""));
				row.put("f_department", Util.quote(""));
			}		
			row.put("suggestions", saveSuggestion2JsonObj());
			return row.toString();
		} catch (JSONException e) {
			Log.d("IndoorInsppectoon", e.getMessage());
			return null;
		}
	}

	private JSONArray saveSuggestion2JsonObj() throws JSONException {
		JSONArray suggestions = new JSONArray();
		model.createPrecautions(this.getAllSuggestionsFromDB(uuid));
		Map<String, String> pairs = this.collectSuggestionsFromUI();
		for(String key : pairs.keySet())
		{
			JSONObject suggestion = new JSONObject();
			suggestion.put("INSPECTION_ID", uuid);
			suggestion.put("PARAM", key);
			Pair pair = this.getDevice(key);
			suggestion.put("EQUIPMENT", pair.name);
			suggestion.put("VALUE", pairs.get(key));
			suggestion.put("NAME", (String)pair.value);
			suggestion.put("BZ", "");
			suggestions.put(suggestion);
		}
		return suggestions;
	}

	/**
	 * �ӱ������ݿ��ȡ�����ֶβ����ֶθ�ֵ
	 */
	private void ReadFromDB(String id,  String inspectionTable) {
		// ��ȡ�����������ݣ���������ֶθ�ֵ
		// �����ݿ�
		try {
			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",Context.MODE_PRIVATE, null);
			Cursor c = db.rawQuery(	"SELECT * FROM " + inspectionTable  + " where id=?",	new String[] { id });
			if(c.moveToNext())
			{
				// �ѷ����ò�����
				if (c.getString(c.getColumnIndex("hasNotified")).length() > 0)
					model.hasNotified.set(true);

				// ����ʱ��
				String dt = c.getString(c.getColumnIndex("ARRIVAL_TIME"));
				model.f_anjianriqi.set(dt.substring(0, 10));
				model.ArrivalTime.set(dt.substring(dt.length()-8, dt.length()));
				// �뿪ʱ��
				dt = c.getString(c.getColumnIndex("DEPARTURE_TIME"));
				String stopAt = dt.substring(dt.length()-8, dt.length());
				//�����������ǽ���ʱ
				if(inspectionTable.equals("T_INSPECTION"))
				{
					((MyDigitalClock)this.findViewById(R.id.digitalClock)).stopAt(stopAt);
				}
				// С����ַ
				model.ROAD.set(c.getString(c.getColumnIndex("ROAD")));
				// ¥��
				model.CUS_DOM.set(c.getString(c.getColumnIndex("CUS_DOM")));
				// ��Ԫ
				model.CUS_DY.set(c.getString(c.getColumnIndex("CUS_DY")));
				// ¥��
				model.CUS_FLOOR.set(c.getString(c.getColumnIndex("CUS_FLOOR")));
				// ����
				model.CUS_ROOM.set(c.getString(c.getColumnIndex("CUS_ROOM")));
				if(c.getInt(c.getColumnIndex("f_ruhu".toUpperCase()))==0)
				{
					model.f_ruhu.set(false);
					//if(inspectionTable.equals("T_INSPECTION"))
					{
						db.close();
						model.createPrecautions(null);
						return;
					}
				}
				else
					model.f_ruhu.set(true);
				if(c.getInt(c.getColumnIndex("f_jujian".toUpperCase()))==1)
				{
					model.f_jujian.set(true);
					//if(inspectionTable.equals("T_INSPECTION"))
					{
						db.close();
						model.createPrecautions(null);
						return;
					}
				}
				else
					model.f_jujian.set(false);
				// ������
				if(c.getString(c.getColumnIndex("CONDITION")).equals("����") && inspectionTable.equals("T_INSPECTION"))
				{
					//DisableOtherCondition();
				}
				
				//���쵥����
				Util.SelectItem(c.getString(c.getColumnIndex("f_checktype")), model.f_checktype, ((Spinner)findViewById(R.id.f_checktype)));
				//С������
				Util.SelectItem(c.getString(c.getColumnIndex("f_property".toUpperCase())), model.f_property, ((Spinner)findViewById(R.id.f_property)));
				//��ס
				Util.SelectItem(c.getString(c.getColumnIndex("f_zuzhu".toUpperCase())), model.f_zuzhu, ((Spinner)findViewById(R.id.f_zuzhu)));
				if (!status) {
					//����
					model.f_consumername.set(c.getString(c.getColumnIndex("f_consumername".toUpperCase())));
					//�绰
					model.f_consumerphone.set(c.getString(c.getColumnIndex("f_consumerphone".toUpperCase())));
					//����
					model.f_kahao.set(c.getString(c.getColumnIndex("f_kahao".toUpperCase())));
					/*//��
					model.city.set(c.getString(c.getColumnIndex("city".toUpperCase())));
					//��
					model.f_area.set(c.getString(c.getColumnIndex("f_area".toUpperCase())));
					//�ֵ�
					model.ROAD.set(c.getString(c.getColumnIndex("ROAD".toUpperCase())));*/
					//������ַ
					model.f_archiveaddress.set(c.getString(c.getColumnIndex("f_archiveaddress")));
					setAddressPart(model.f_archiveaddress.get());
					//����
					model.f_biaohao.set(c.getString(c.getColumnIndex("f_biaohao".toUpperCase())));
					//���ұ�
					Util.SelectItem(c.getString(c.getColumnIndex("f_rqbiaoxing".toUpperCase())), model.f_rqbiaoxing, ((Spinner)findViewById(R.id.f_rqbiaoxing)));
					//������
					Util.SelectItem(c.getString(c.getColumnIndex("f_biaochang".toUpperCase())), model.f_biaochang, ((Spinner)findViewById(R.id.f_biaochang)));
					//�ܹ�����
					model.f_buygas.set(c.getString(c.getColumnIndex("f_buygas".toUpperCase())));

					this.status = false;
				}
				//TODO
				
				//��פ�˿�
				model.f_renkou.set(c.getString(c.getColumnIndex("f_renkou".toUpperCase())));
				//�������
				Util.SelectItem(c.getString(c.getColumnIndex("REGION_NAME".toUpperCase())), model.REGION_NAME, ((Spinner)findViewById(R.id.REGION_NAME)));
				//�Ƿ��ص��û�
				Util.SelectItem(c.getString(c.getColumnIndex("f_iszhongdian".toUpperCase())), model.f_iszhongdian, ((Spinner)findViewById(R.id.f_iszhongdian)));
				//��ů��ʽ
				Util.SelectItem(c.getString(c.getColumnIndex("f_gongnuan".toUpperCase())), model.f_gongnuan, ((Spinner)findViewById(R.id.f_gongnuan)));
				//�������з�
				Util.SelectItem(c.getString(c.getColumnIndex("f_baojingqi".toUpperCase())), model.f_baojingqi, ((Spinner)findViewById(R.id.f_baojingqi)));
				//���ұ�����
				Util.SelectItem(c.getString(c.getColumnIndex("f_baojingqichang".toUpperCase())), model.f_baojingqichang, ((Spinner)findViewById(R.id.f_baojingqichang)));
				//��������װʱ��
				model.f_alarm_installation_time.set(c.getString(c.getColumnIndex("f_alarm_installation_time".toUpperCase())));
				//����������ʱ��
				model.f_alarm_expire_time.set(c.getString(c.getColumnIndex("f_alarm_expire_time".toUpperCase())));
				//ȼ��������ʱ��
				model.f_meter_manufacture_date.set(c.getString(c.getColumnIndex("f_meter_manufacture_date".toUpperCase())));
				//����
				Util.SelectItem(c.getString(c.getColumnIndex("f_meter_type".toUpperCase())), model.f_meter_type, ((Spinner)findViewById(R.id.f_meter_type)));
				//IC��������
				Util.SelectItem(c.getString(c.getColumnIndex("f_iccard_type".toUpperCase())), model.f_iccard_type, ((Spinner)findViewById(R.id.f_iccard_type)));
				//ʣ������
				model.f_shengyu.set(c.getString(c.getColumnIndex("f_shengyu".toUpperCase())));
				//ʣ����
				model.f_balance.set(c.getString(c.getColumnIndex("f_balance".toUpperCase())));
				//����
				Util.SelectItem(c.getString(c.getColumnIndex("f_meter_cover".toUpperCase())), model.f_meter_cover, ((Spinner)findViewById(R.id.f_meter_cover)));
				//�������
				model.f_jbdushu.set(c.getString(c.getColumnIndex("f_jbdushu".toUpperCase())));
				//IC������
				Util.SelectItem(c.getString(c.getColumnIndex("f_kachangjia".toUpperCase())), model.f_kachangjia, ((Spinner)findViewById(R.id.f_kachangjia)));
				//�Ƿ񻻱�
				model.f_newmeter.set(c.getInt(c.getColumnIndex("f_newmeter".toUpperCase()))==1);
				//����
				model.f_sibiao.set(c.getInt(c.getColumnIndex("f_sibiao".toUpperCase()))==1);
				//��������
				model.f_reading_mismatch.set(c.getInt(c.getColumnIndex("f_reading_mismatch".toUpperCase()))==1);
				//��ͨ
				model.f_changtong.set(c.getInt(c.getColumnIndex("f_changtong".toUpperCase()))==1);
				//��װ
				model.f_fanzhuang.set(c.getInt(c.getColumnIndex("f_fanzhuang".toUpperCase()))==1);
				//©��
				model.f_qblouqi.set(c.getInt(c.getColumnIndex("f_qblouqi".toUpperCase()))==1);
				//����
				model.f_qbqita.set(c.getInt(c.getColumnIndex("f_qbqita".toUpperCase()))==1);
				//��������
				model.f_qibiao.set(c.getString(c.getColumnIndex("f_qibiao".toUpperCase())));

				//����
				model.f_meter_wrapped.set(c.getInt(c.getColumnIndex("f_meter_wrapped".toUpperCase()))==1);
				//����
				model.f_meter_hanger.set(c.getInt(c.getColumnIndex("f_meter_hanger".toUpperCase()))==1);
				//���Դ����
				model.f_meter_nearfire.set(c.getInt(c.getColumnIndex("f_meter_nearfire".toUpperCase()))==1);
				//����ά��
				model.f_meter_unfavorable.set(c.getInt(c.getColumnIndex("f_meter_unfavorable".toUpperCase()))==1);

				//�ܼ����� ���ܷ�
				model.f_plumbing_valve.set(c.getInt(c.getColumnIndex("f_plumbing_valve".toUpperCase()))==1);
				//�ܼ����� ���
				model.f_plumbing_slipknot.set(c.getInt(c.getColumnIndex("f_plumbing_slipknot".toUpperCase()))==1);
				//�ܼ����� ������
				model.f_plumbing_scaleknot.set(c.getInt(c.getColumnIndex("f_plumbing_scaleknot".toUpperCase()))==1);
				//�ܼ����� �侶
				model.f_plumbing_diameter.set(c.getInt(c.getColumnIndex("f_plumbing_diameter".toUpperCase()))==1);
				
				//��������
				model.f_lgzhengchang.set(c.getInt(c.getColumnIndex("f_lgzhengchang".toUpperCase()))==1);
				//��ʴ
				model.f_lgfushi.set(c.getInt(c.getColumnIndex("f_lgfushi".toUpperCase()))==1);
				//˽��
				model.f_lgsigai.set(c.getInt(c.getColumnIndex("f_lgsigai".toUpperCase()))==1);
				//©��
				model.f_lglouqi.set(c.getInt(c.getColumnIndex("f_lglouqi".toUpperCase()))==1);
				//����
				model.f_lgbaoguo.set(c.getInt(c.getColumnIndex("f_lgbaoguo".toUpperCase()))==1);
				//����
				model.f_lgguawu.set(c.getInt(c.getColumnIndex("f_lgguawu".toUpperCase()))==1);
				//���Դ����
				model.f_lghuoyuan.set(c.getInt(c.getColumnIndex("f_lghuoyuan".toUpperCase()))==1);
				//δ�̶�
				model.f_lgweiguding.set(c.getInt(c.getColumnIndex("f_lgweiguding".toUpperCase()))==1);
				//��Խ��ֹ����
				model.f_lgchuanyue.set(c.getInt(c.getColumnIndex("f_lgchuanyue".toUpperCase()))==1);
				//����ά��
				model.f_lgbubianweixiu.set(c.getInt(c.getColumnIndex("f_lgbubianweixiu".toUpperCase()))==1);
				
				//���ܷ�
				model.f_plumbing_leakage_valve.set(c.getInt(c.getColumnIndex("f_plumbing_leakage_valve".toUpperCase()))==1);
				//������
				model.f_plumbing_leakage_scaleknot.set(c.getInt(c.getColumnIndex("f_plumbing_leakage_scaleknot".toUpperCase()))==1);
				//���
				model.f_plumbing_leakage_slipknot.set(c.getInt(c.getColumnIndex("f_plumbing_leakage_slipknot".toUpperCase()))==1);
				//�侶
				model.f_plumbing_leakage_diameter.set(c.getInt(c.getColumnIndex("f_plumbing_leakage_diameter".toUpperCase()))==1);
				//��ͨ
				model.f_plumbing_leakage_triple.set(c.getInt(c.getColumnIndex("f_plumbing_leakage_triple".toUpperCase()))==1);
				
				//��ǰ������
				Util.SelectItem(c.getString(c.getColumnIndex("f_metervalve_type".toUpperCase())), model.f_metervalve_type, ((Spinner)findViewById(R.id.f_metervalve_type)));
				//��ǰ��
				Util.SelectItem(c.getString(c.getColumnIndex("f_biaoqianfa".toUpperCase())), model.f_biaoqianfa, ((Spinner)findViewById(R.id.f_biaoqianfa)));
				//��������
				Util.SelectItem(c.getString(c.getColumnIndex("f_plumbing_type".toUpperCase())), model.f_plumbing_type, ((Spinner)findViewById(R.id.f_plumbing_type)));
				//��ǰ��
				Util.SelectItem(c.getString(c.getColumnIndex("f_zaoqianfa".toUpperCase())), model.f_zaoqianfa, ((Spinner)findViewById(R.id.f_zaoqianfa)));
				//�Աշ�
				Util.SelectItem(c.getString(c.getColumnIndex("f_zibifa".toUpperCase())), model.f_zibifa, ((Spinner)findViewById(R.id.f_zibifa)));
				//��������
				model.f_bhgzhengchang.set(c.getInt(c.getColumnIndex("f_bhgzhengchang".toUpperCase()))==1);
				//����
				model.f_bhgguawu.set(c.getInt(c.getColumnIndex("f_bhgguawu".toUpperCase()))==1);
				//����
				model.f_bhgbaoguan.set(c.getInt(c.getColumnIndex("f_bhgbaoguan".toUpperCase()))==1);
				//©��
				model.f_bhglouqi.set(c.getInt(c.getColumnIndex("f_bhglouqi".toUpperCase()))==1);
				//��Խ��ֹ����
				model.f_bhgjinzhiquyu.set(c.getInt(c.getColumnIndex("f_bhgwoshi".toUpperCase()))==1);
				//���Դ����
				model.f_bhgdianyuan.set(c.getInt(c.getColumnIndex("f_bhgdianyuan".toUpperCase()))==1);
				//�Ĺ�
				model.f_bhggaiguan.set(c.getInt(c.getColumnIndex("f_bhggaiguan".toUpperCase()))==1);
				//����ͨ
				model.f_bhgrst.set(c.getInt(c.getColumnIndex("f_bhgrst".toUpperCase()))==1);
				//��ʴ
				model.f_bhgfushi.set(c.getInt(c.getColumnIndex("f_bhgfushi".toUpperCase()))==1);
				//����ά��
				model.f_bhgbubianweixiu.set(c.getInt(c.getColumnIndex("f_bhgbubianweixiu".toUpperCase()))==1);
				//δ�̶�
				model.f_bhgwxd.set(c.getInt(c.getColumnIndex("f_bhgwxd".toUpperCase()))==1);
				//����
				model.f_bhgqita.set(c.getInt(c.getColumnIndex("f_bhgqita".toUpperCase()))==1);
				//����
				model.f_bhgbeizhu.set(c.getString(c.getColumnIndex("f_bhgbeizhu".toUpperCase())));
				//��Ƥ������
				model.f_jpgzhengchang.set(c.getInt(c.getColumnIndex("f_jpgzhengchang".toUpperCase()))==1);
				//©��
				model.f_jpglouqi.set(c.getInt(c.getColumnIndex("f_jpglouqi".toUpperCase()))==1);
				//�ϻ�
				model.f_jpglaohua.set(c.getInt(c.getColumnIndex("f_jpglaohua".toUpperCase()))==1);
				//����
				model.f_jpgguochang.set(c.getInt(c.getColumnIndex("f_jpgguochang".toUpperCase()))==1);
				//��
				model.f_jpgdiaoding.set(c.getInt(c.getColumnIndex("f_jpgdiaoding".toUpperCase()))==1);
				//�޹ܼ�
				model.f_jpgwuguanjia.set(c.getInt(c.getColumnIndex("f_jpgwuguanjia".toUpperCase()))==1);
				//�йܼ�
				model.f_jpganmai.set(c.getInt(c.getColumnIndex("f_jpganmai".toUpperCase()))==1);
				//���δ��
				model.f_zjshiyong.set(c.getInt(c.getColumnIndex("f_zjshiyong".toUpperCase()))==1);
				//Ʒ��
				model.f_zjpinpai.set(c.getString(c.getColumnIndex("f_zjpinpai".toUpperCase())));
				//����
				Util.SelectItem(c.getString(c.getColumnIndex("f_zjleixing".toUpperCase())), model.f_zjleixing, ((Spinner)findViewById(R.id.f_zjleixing)));
				//��״
				Util.SelectItem(c.getString(c.getColumnIndex("f_zjxianzhuang".toUpperCase())), model.f_zjxianzhuang, ((Spinner)findViewById(R.id.f_zjxianzhuang)));
				//��߰�װʱ��
				model.f_cooker_installation_time.set(c.getString(c.getColumnIndex("f_cooker_installation_time".toUpperCase())));
				//��ߵ���ʱ��
				model.f_cooker_expire_time.set(c.getString(c.getColumnIndex("f_cooker_expire_time".toUpperCase())));
				//��߳���
				model.f_cooker_overdue.set(c.getInt(c.getColumnIndex("f_cooker_overdue".toUpperCase()))==1);
				//�����Ϩ�𱣻���Ϩ�𱣻�ʧЧ
				model.f_cooker_nofireprotection.set(c.getInt(c.getColumnIndex("f_cooker_nofireprotection".toUpperCase()))==1);
				//���©��
				model.f_cooker_leakage.set(c.getInt(c.getColumnIndex("f_cooker_leakage".toUpperCase()))==1);
				//��߱�ע
				model.f_cooker_precaution_remark.set(c.getString(c.getColumnIndex("f_cooker_precaution_remark".toUpperCase())));
				//��ˮ��δ��
				model.f_rshqshiyong.set(c.getInt(c.getColumnIndex("f_rshqshiyong".toUpperCase()))==1);
				//Ʒ��
				model.f_rshqpinpai.set(c.getString(c.getColumnIndex("f_rshqpinpai".toUpperCase())));
				//����
				model.f_rshqxinghao.set(c.getString(c.getColumnIndex("f_rshqxinghao".toUpperCase())));
				//��״
				Util.SelectItem(c.getString(c.getColumnIndex("f_rshqxianzhuang".toUpperCase())), model.f_rshqxianzhuang, ((Spinner)findViewById(R.id.f_rshqxianzhuang)));
				//��ˮ����װʱ��
				model.f_heater_installation_time.set(c.getString(c.getColumnIndex("f_heater_installation_time".toUpperCase())));
				//��ˮ������ʱ��
				model.f_heater_expire_time.set(c.getString(c.getColumnIndex("f_heater_expire_time".toUpperCase())));
				//��ˮ����װλ��
				Util.SelectItem(c.getString(c.getColumnIndex("f_heater_place".toUpperCase())), model.f_heater_place, ((Spinner)findViewById(R.id.f_heater_place)));
				//��ˮ������
				model.f_heater_overdue.set(c.getInt(c.getColumnIndex("f_heater_overdue".toUpperCase()))==1);
				model.f_heater_softconnector.set(c.getInt(c.getColumnIndex("f_heater_softconnector".toUpperCase()))==1);
				model.f_heater_trapped.set(c.getInt(c.getColumnIndex("f_heater_trapped".toUpperCase()))==1);
				model.f_heater_leakage.set(c.getInt(c.getColumnIndex("f_heater_leakage".toUpperCase()))==1);
				model.f_heater_leakage_connetor.set(c.getInt(c.getColumnIndex("f_heater_leakage_connetor".toUpperCase()))==1);
				model.f_heater_leakage_valve.set(c.getInt(c.getColumnIndex("f_heater_leakage_valve".toUpperCase()))==1);
				model.f_heater_leakage_heater.set(c.getInt(c.getColumnIndex("f_heater_leakage_heater".toUpperCase()))==1);
				model.f_heater_precaution_remark.set(c.getString(c.getColumnIndex("f_heater_precaution_remark".toUpperCase())));
				//�ڹ�¯δ��
				model.f_bglshiyong.set(c.getInt(c.getColumnIndex("f_bglshiyong".toUpperCase()))==1);
				//Ʒ��
				model.f_bglpinpai.set(c.getString(c.getColumnIndex("f_bglpinpai".toUpperCase())));
				//��״
				Util.SelectItem(c.getString(c.getColumnIndex("f_bglxianzhuang".toUpperCase())), model.f_bglxianzhuang, ((Spinner)findViewById(R.id.f_bglxianzhuang)));
				//λ��
				Util.SelectItem(c.getString(c.getColumnIndex("f_bglweizhi".toUpperCase())), model.f_bglweizhi, ((Spinner)findViewById(R.id.f_bglweizhi)));
				//����
				model.f_bglgonglv.set(c.getString(c.getColumnIndex("f_bglgonglv".toUpperCase())));
				//�ڹ�¯��װʱ��
				model.f_furnace_installation_time.set(c.getString(c.getColumnIndex("f_furnace_installation_time".toUpperCase())));
				//�ڹ�¯����
				model.f_furnace_overdue.set(c.getInt(c.getColumnIndex("f_furnace_overdue".toUpperCase()))==1);
				model.f_furnace_softconnector.set(c.getInt(c.getColumnIndex("f_furnace_softconnector".toUpperCase()))==1);
				model.f_furnace_trapped.set(c.getInt(c.getColumnIndex("f_furnace_trapped".toUpperCase()))==1);
				model.f_furnace_leakage.set(c.getInt(c.getColumnIndex("f_furnace_leakage".toUpperCase()))==1);
				model.f_furnace_leakage_connetor.set(c.getInt(c.getColumnIndex("f_furnace_leakage_connetor".toUpperCase()))==1);
				model.f_furnace_leakage_valve.set(c.getInt(c.getColumnIndex("f_furnace_leakage_valve".toUpperCase()))==1);
				model.f_furnace_leakage_furnace.set(c.getInt(c.getColumnIndex("f_furnace_leakage_furnace".toUpperCase()))==1);
				model.f_furnace_precaution_remark.set(c.getString(c.getColumnIndex("f_furnace_precaution_remark".toUpperCase())));
				//�ڹ�¯����ʱ��
				model.f_furnace_expire_time.set(c.getString(c.getColumnIndex("f_furnace_expire_time".toUpperCase())));
				//��������
				model.f_precaution_kitchen.set(c.getInt(c.getColumnIndex("f_precaution_kitchen".toUpperCase()))==1);
				model.f_precaution_multisource.set(c.getInt(c.getColumnIndex("f_precaution_multisource".toUpperCase()))==1);
				model.f_renow_id.set(c.getString(c.getColumnIndex("f_renow_id".toUpperCase())));
				Util.SelectItem(c.getString(c.getColumnIndex("f_precaution_otheruse".toUpperCase())), model.f_precaution_otheruse, ((Spinner)findViewById(R.id.f_precaution_otheruse)));
				//��ע
				model.f_zgbeizhu.set(c.getString(c.getColumnIndex("f_zgbeizhu".toUpperCase())));
				
			
				
				// �û�����
				if (c.getString(c.getColumnIndex("f_kehupingjia".toUpperCase())).equals("����")) {
					((RadioButton)findViewById(R.id.FeebackSatisfied)).setChecked(true);
				} else if (c.getString(c.getColumnIndex("f_kehupingjia".toUpperCase())).equals("��������")) {
					((RadioButton)findViewById(R.id.FeebackOK)).setChecked(true);
				} else if (c.getString(c.getColumnIndex("f_kehupingjia".toUpperCase())).equals("������")) {
					((RadioButton)findViewById(R.id.FeebackUnsatisfied)).setChecked(true);
				}

				// ��Ƭ
				if (c.getString(c.getColumnIndex("USER_SIGN")) != null) {
					ImageView signPad = (ImageView) (findViewById(R.id.signPad));
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("USER_SIGN")) + ".png");
					signPad.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIRST")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FIRST"))
							+ ".jpg");
					img1.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_SECOND")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_SECOND"))
							+ ".jpg");
					img2.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_THIRD")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_THIRD"))
							+ ".jpg");
					img3.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FOUTH")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FOUTH"))
							+ ".jpg");
					img4.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIFTH")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FIFTH"))
							+ ".jpg");
					img5.setImageBitmap(bmp);
				}				
			}
			db.close();
			model.createPrecautions(this.getAllSuggestionsFromDB(uuid));
		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
		}
	}

	private void setAddressPart(String address) {
		try
		{
			String[] sixPhaseAddress = address.split("---");
			model.UNIT_NAME2.set(sixPhaseAddress[1]);
			model.ROAD2.set(sixPhaseAddress[0]);
			model.CUS_DOM2.set(sixPhaseAddress[2]);
			model.CUS_DY2.set(sixPhaseAddress[3]);
			model.CUS_FLOOR2.set(sixPhaseAddress[4]);
			model.CUS_ROOM2.set(sixPhaseAddress[5]);
		}
		catch(Exception e)
		{
			
		}
	}

	/**
	 * ��ֹ�����ˡ��ܷá����͵��ò�����ѡ��
	 */
	private void DisableOtherCondition() {
		findViewById(R.id.f_ruhu).setEnabled(false);
		findViewById(R.id.f_jujian).setEnabled(false);
		findViewById(R.id.hasNotified).setEnabled(false);
	}


	@Override
	protected void onPause() {
		super.onPause();
		Save(SaveToJSONString(true, false), "T_INP" ,  true);
		Util.setSharedPreference(this, "entryDateTime", Util.FormatDate("yyyy-MM-dd HH:mm:ss", entryDateTime.getTime()));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Util.IsCached(this, uuid))
		{
			ReadFromDB(uuid, "T_INP");
			model.GetRepairPerson(uuid, "T_INP");
			String dt = Util.getSharedPreference(this, "entryDateTime");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try
			{
				entryDateTime = formatter.parse(dt);
			}
			catch(Exception e)
			{
				
			}
		}
		else
		{
			if(inspected)
				this.ReadFromDB(uuid,"T_INSPECTION");
			//�첽��ȡά����Ա
			model.GetRepairPerson(uuid, "T_INSPECTION");
			//��¼����ʱ��
			entryDateTime = new Date();			
		}
	}

	@Override
	public void onBackPressed() {
		TextView tv = new TextView(this);
		tv.setText("ȷ��Ҫ�˳���");
		Dialog alertDialog = new AlertDialog.Builder(this).   
				setView(tv).
				setTitle("ȷ��").   
				setIcon(android.R.drawable.ic_dialog_info).
				setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						IndoorInspectActivity.this.finish();
					}
				}).setNegativeButton("ȡ��", null).
				create();  
		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	/**
	 * �õ�ά�޽���
	 * @return
	 */
	public Map<String, String> getAllSuggestionsFromDB(String uuid)
	{
		Map<String, String> suggestions = new HashMap<String,String>();
		String table = "T_IC_SAFECHECK_HIDDEN";
		if(Util.IsCached(this, uuid))
			table = "T_INP_LINE";
		SQLiteDatabase db = null;
		try
		{
			db = this.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String sql = "select param, value from " +  table + " where id=?";
			Cursor c = db.rawQuery(sql, new String[] { uuid });
			while(c.moveToNext())
			{
				suggestions.put(c.getString(0), c.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.close();
		}	
		return suggestions;
	}
	
	/**
	 * �ӽ����ռ�ά�޽���
	 * @return
	 */
	public Map<String, String> collectSuggestionsFromUI()
	{
		Map<String, String> suggestions = new HashMap<String,String>();
		ViewGroup vg = (ViewGroup)this.findViewById(R.id.ideaPad);
		collectSuggestion(vg, suggestions);
		return suggestions;
	}
	
	/**
	 * �ݹ鷽��
	 * @param layout
	 * @param suggestions
	 */
	public void collectSuggestion(ViewGroup layout, Map<String, String> suggestions) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			if (child instanceof ViewGroup && !(child instanceof Spinner)) {
				collectSuggestion((ViewGroup) child, suggestions);
			} else {
				if(child instanceof Spinner)
				{
					Spinner sp = (Spinner)child;
					suggestions.put((String)sp.getTag(), (String)sp.getSelectedItem());
				}
			}
		}
	}

	private Pair getDevice(String uiid)
	{
		Pair pair = new Pair("","");
		if(uiid.startsWith("f_meter"))
		{
			pair.name = "ȼ����";
			pair.value = findName(0, uiid);
		}
		else if(uiid.startsWith("f_lg"))
		{
			pair.name = "����";
			pair.value = findName(1, uiid);
		}
		else if(uiid.startsWith("f_bhg"))
		{
			pair.name = "����";
			pair.value = findName(2, uiid);
		}
		else if(uiid.startsWith("f_jpg"))
		{
			pair.name = "��Ƥ��";
			pair.value = findName(3, uiid);
		}
		else if(uiid.startsWith("f_cooker"))
		{
			pair.name = "���";
			pair.value = findName(4, uiid);
		}
		else if(uiid.startsWith("f_heater"))
		{
			pair.name = "��ˮ��";
			pair.value = findName(5, uiid);
		}
		else if(uiid.startsWith("f_furnace"))
		{
			pair.name = "�ڹ�¯";
			pair.value = findName(6, uiid);
		}
		else
		{
			pair.name = "��������";
			String value =(String)findName(7, uiid);
			if(value.length()==0)
				pair.value = "������;";
			else
				pair.value = value;
		}
		return pair;
	}
	
	private Object findName(int idx, String uiid) {
		for(int i=0; i<model.names[idx].length; i++)
			if(uiid.equals(model.names[idx][i]))
				return model.cnames[idx][i];
		return "";
	}

	public boolean saveSuggestions( Map<String, String> suggestions, String uuid)
	{
		boolean result = true;
		String table = "T_IC_SAFECHECK_HIDDEN";
		if(Util.IsCached(this, uuid))
			table = "T_INP_LINE";
		SQLiteDatabase db = null;
		try
		{
			db = this.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			//deletion first
			String sql = "delete from " +  table + " where id=?";
			db.execSQL(sql, new Object[]{uuid});
			//insertion
			for(String key : suggestions.keySet())
			{
				sql = "insert into " + table + " values(?,?,?,?,?,?,?)";
				Pair pair = getDevice(key);
				db.execSQL(sql, new Object[]{uuid, pair.name, key, suggestions.get(key), uuid, (String)pair.value, ""});
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			result = false;
		}
		finally
		{
			if(db != null)
				db.close();
		}	
		return result;
	}
}
