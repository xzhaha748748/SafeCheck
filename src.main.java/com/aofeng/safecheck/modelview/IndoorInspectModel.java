package com.aofeng.safecheck.modelview;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import gueei.binding.validation.validators.MaxLength;
import gueei.binding.validation.validators.RegexMatch;
import gueei.binding.validation.validators.Required;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.AutographActivity;
import com.aofeng.safecheck.activity.IndoorInspectActivity;
import com.aofeng.safecheck.activity.JobDownActivity;
import com.aofeng.safecheck.activity.PurchaseHistoryActivity;
import com.aofeng.safecheck.activity.QueryUserInfoActivity;
import com.aofeng.safecheck.activity.ShootActivity;
import com.aofeng.utils.HttpMultipartPost;
import com.aofeng.utils.Pair;
import com.aofeng.utils.ScrubblePane;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

/**
 * �뻧����model
 * 
 * @author lgy
 * 
 */
public class IndoorInspectModel {
	private final IndoorInspectActivity mContext;
	// �뻧����ƻ�ID
	private String indoorInpsectPlanID = "test";
	
	public String[][] names = {
			{"f_meter_wrapped", "f_meter_hanger", "f_meter_nearfire", "f_meter_unfavorable"},   //ȼ����
			{"f_lgfushi", "f_lgsigai", "f_lglouqi", "f_lgbaoguo", "f_lgguawu", "f_lghuoyuan", "f_lgweiguding", "f_lgchuanyue", "f_lgbubianweixiu"}, //����
			{"f_bhgbaoguan", "f_bhgguawu", "f_bhglouqi", "f_bhgjinzhiquyu", "f_bhgdianyuan", "f_bhggaiguan", "f_bhgrst", "f_bhgfushi", "f_bhgbubianweixiu", "f_bhgwxd"}, //����
			{"f_jpglouqi", "f_jpglaohua", "f_jpgguochang", "f_jpgwuguanjia", "f_jpgdiaoding", "f_jpganmai"}, //��Ƥ��
			{"f_cooker_overdue", "f_cooker_nofireprotection", "f_cooker_leakage"}, //���
			{"f_heater_overdue", "f_heater_softconnector", "f_heater_leakage", "f_heater_trapped",  "f_heater_leakage_valve", "f_heater_leakage_connetor", "f_heater_leakage_heater"},//��ˮ��
			{"f_furnace_overdue", "f_furnace_softconnector", "f_furnace_leakage", "f_furnace_trapped",  "f_furnace_leakage_valve", "f_furnace_leakage_connetor", "f_furnace_leakage_furnace"},//�ڹ�¯
			{"f_precaution_kitchen", "f_precaution_multisource"}   //��������
	};
	
	public String[][] cnames={
			{"����","����","���Դ���Դ����","����ά��"} , //ȼ����
			{"��ʴ","˽��","©��","����","����","���Դ���Դ����","δ�̶�","��Խ��ֹ����","����ά��"}, //����
			{"����","����","©��","��Խ��ֹ����","���Դ���Դ����","˽��","����ͨ","��ʴ","����ά��","δ�̶�"},//����
			{"©��","�ϻ�","����","�޹ܼ�","��ǽ�����","����"},//��Ƥ��
			{"����ʹ��","��Ϩ�𱣻���Ϩ�𱣻�ʧЧ","©��"},//���
			{"����ʹ��","�������","©��","ֱ�Ż��̵�δ�ų�����","��","�ӿ�","����"},//��ˮ��
			{"����ʹ��","�������","©��","ֱ�Ż��̵�δ�ų�����","��","�ӿ�","����"},//�ڹ�¯
			{"����ʽ����ʽ����","���ֻ�Դ"}//��������
	};
	
	public IndoorInspectModel(IndoorInspectActivity Context) {
		this.mContext = Context;
		Bundle bundle = mContext.getIntent().getExtras();
		if (bundle != null)
			indoorInpsectPlanID = bundle.getString("ID");
		FillBindingList();
	}

	private Spinner spinSpinner(String param, String choice)
	{
	    ArrayList<String> lst = new ArrayList();
		SQLiteDatabase db = null;
		int idx = 0;
		try
		{
			db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
				String sql = "select NAME from T_PARAMS where code=? order by id";
				Cursor c = db.rawQuery(sql, new String[] { param });
				int i=0;
				while(c.moveToNext())
				{
					if(choice != null && c.getString(0).equals(choice))
						idx = i;
					lst.add(c.getString(0));
					i = i+1;
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
		Spinner spinner = new Spinner(mContext);
	    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, lst);
	    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(spinnerArrayAdapter);
	    spinner.setTag(param);
	    spinner.setSelection(idx);
	    return spinner;
	}
	
	private TextView spinTextView(String text)
	{
		TextView tv = new TextView(mContext);
		tv.setText(text);
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(20);		
		return tv;
	}
	
	
	public void createPrecautions(Map<String, String> choices)
	{
		LinearLayout layout = (LinearLayout)mContext.findViewById(R.id.ideaPad);
		layout.removeAllViews();
		if(!this.f_ruhu.get() || this.f_jujian.get())
			return;
		if(choices==null)
			return;
		Set<String> keys = choices.keySet();
		ArrayList<Pair> devices = this.getAllSuggestionItems();
		for(Pair device : devices)
		{
			//create device band
			TextView tv = spinTextView(device.name + "ά�޽���");
			tv.setBackgroundColor(Color.rgb(152, 166, 215));
			tv.setTextColor(Color.WHITE);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT) );
			layout.addView(tv);
			ArrayList<Pair> precautions = (ArrayList<Pair>)device.value;
			if(precautions.size() % 2 != 0)
			{
				precautions.add(new Pair("", ""));
			}
			LinearLayout pane = null;
			int i =0;
			for(Pair pair : precautions)
			{
				if(i % 2 == 0)
					pane = new LinearLayout(mContext);
				String text = (String)pair.value;
				if(pair.name.length()!=0)
				{
					Spinner spinner;
					if(keys.contains(pair.name))
						spinner = spinSpinner(pair.name, choices.get(pair.name));
					else
						spinner =spinSpinner(pair.name, null);
					tv = spinTextView(text);
					pane.addView(tv);
					pane.addView(spinner, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				}
				if(i % 2 == 1)
					layout.addView(pane);
				i = i+1;
			}
		}
		
	}
	

	public ArrayList<Pair> getAllSuggestionItems()
	{
		Object[][] objs = {
				{this.f_meter_wrapped, this.f_meter_hanger, this.f_meter_nearfire, this.f_meter_unfavorable},   //ȼ����
				{this.f_lgfushi, this.f_lgsigai, this.f_lglouqi, this.f_lgbaoguo, this.f_lgguawu, this.f_lghuoyuan, this.f_lgweiguding, this.f_lgchuanyue, this.f_lgbubianweixiu}, //����
				{this.f_bhgbaoguan, this.f_bhgguawu, this.f_bhglouqi, this.f_bhgjinzhiquyu, this.f_bhgdianyuan, this.f_bhggaiguan, this.f_bhgrst, this.f_bhgfushi, this.f_bhgbubianweixiu, this.f_bhgwxd}, //����
				{this.f_jpglouqi, this.f_jpglaohua, this.f_jpgguochang, this.f_jpgwuguanjia, this.f_jpgdiaoding, this.f_jpganmai}, //��Ƥ��
				{this.f_cooker_overdue, this.f_cooker_nofireprotection, this.f_cooker_leakage}, //���
				{this.f_heater_overdue, this.f_heater_softconnector, this.f_heater_leakage, this.f_heater_trapped,  this.f_heater_leakage_valve, this.f_heater_leakage_connetor, this.f_heater_leakage_heater},//��ˮ��
				{this.f_furnace_overdue, this.f_furnace_softconnector, this.f_furnace_leakage, this.f_furnace_trapped,  this.f_furnace_leakage_valve, this.f_furnace_leakage_connetor, this.f_furnace_leakage_furnace},//�ڹ�¯
				{this.f_precaution_kitchen, this.f_precaution_multisource}   //��������
		};

		String[] devices ={"ȼ����","����","����","��Ƥ��","���","��ˮ��","�ڹ�¯", "��������"};
		ArrayList<Pair> devicePair = new ArrayList<Pair>();
		ArrayList<Pair> suggestions; 
		boolean hasOther = false;
		for(int i = 0;  i< objs.length; i++)
		{
			suggestions = new ArrayList<Pair>();
			for(int j=0; j<objs[i].length; j++)
			{
				BooleanObservable bo = (BooleanObservable)objs[i][j];
				if(bo.get())
				{
					Pair pair = new Pair(names[i][j], cnames[i][j]);
					suggestions.add(pair);
				}
			}
			if(suggestions.size()>0)
			{
				Pair pair = new Pair(devices[i], suggestions);
				if(devices[i].equals("��������"))
					hasOther = true;
				devicePair.add(pair);
			}
		}

		//special care to f_precaution_otheruse
		int idx = ((Spinner)mContext.findViewById(R.id.f_precaution_otheruse)).getSelectedItemPosition();
		if(f_precaution_otheruse.get(idx).length()>0)
		{
			if(hasOther)
			{
				suggestions =(ArrayList<Pair>)devicePair.get(devicePair.size()-1).value;
				Pair pair = new Pair("f_precaution_otheruse1" , "����Ϊ" + f_precaution_otheruse.get(idx));
				suggestions.add(pair);
			}
			else
			{
				suggestions = new ArrayList<Pair>();
				Pair pair = new Pair("f_precaution_otheruse1" , "����Ϊ" + f_precaution_otheruse.get(idx));
				suggestions.add(pair);
				pair = new Pair(devices[7], suggestions);
				devicePair.add(pair);
			}
		}
		return devicePair;
	}
	
	/**
	 * ���ݲ�������б�
	 */
	private void FillBindingList() {
		Object[] list = {this.f_property, this.f_iszhongdian,this.f_gongnuan,this.f_zuzhu,this.REGION_NAME,this.f_baojingqichang,this.f_biaochang,this.f_kachangjia,this.f_biaoqianfa,
				this.f_zaoqianfa,this.f_zibifa,this.f_zjleixing,this.f_zjxianzhuang,this.f_rshqxianzhuang,
				this.f_bglweizhi,this.f_bglxianzhuang, 
				this.f_meter_type, this.f_iccard_type,this.f_plumbing_type,  this.f_metervalve_type,  
				this.f_heater_place, this.f_precaution_otheruse, this.f_meter_cover};
		String[] codes = {"f_property", "f_iszhongdian","f_gongnuan","f_zuzhu","REGION_NAME","f_baojingqichang","f_biaochang","f_kachangjia","f_biaoqianfa",
				"f_zaoqianfa","f_zibifa","f_zjleixing","f_zjxianzhuang","f_rshqxianzhuang",
				"f_bglweizhi","f_bglxianzhuang",
				"f_meter_type", "f_iccard_type","f_plumbing_type",  "f_metervalve_type",  
				"f_heater_place", "f_precaution_otheruse", "f_meter_cover"};
		SQLiteDatabase db = null;
		try
		{
			db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			for(int i=0; i<codes.length; i++)
			{
				ArrayListObservable<String> olist = (ArrayListObservable<String>)list[i];
				olist.clear();
				olist.add("");
				String sql = "select NAME from T_PARAMS where code=? order by id";
				Cursor c = db.rawQuery(sql, new String[] { codes[i] });
				while(c.moveToNext())
				{
					olist.add(c.getString(0));
				}
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
		
	}

	/**
	 * ������ǰѡ��
	 * 
	 * @param imgId
	 */
	private void HilightChosenImg(int imgId) {
		basicImgId.set(R.drawable.basic);
		meterImgId.set(R.drawable.meter);
		plumImgId.set(R.drawable.plum);
		cookerImgId.set(R.drawable.cooker);
		precautionImgId.set(R.drawable.precaution);
		feedbackImgId.set(R.drawable.feedback);
		furnaceImgId.set(R.drawable.furnace);
		suggestionImgId.set(R.drawable.suggestion);
		if (imgId == R.drawable.basic_selected) {
			basicImgId.set(imgId);
			muteOthers(R.id.basicPane);
		} else if (imgId == R.drawable.meter_selected) {
			meterImgId.set(imgId);
			muteOthers(R.id.meterPane);
		} else if (imgId == R.drawable.plum_selected) {
			plumImgId.set(imgId);
			muteOthers(R.id.plumPane);
		} else if (imgId == R.drawable.cooker_selected) {
			cookerImgId.set(imgId);
			muteOthers(R.id.cookerPane);
		}		
		else if(imgId == R.drawable.precaution_selected)
		{
			precautionImgId.set(imgId);
			muteOthers(R.id.precautionPane);
		} else if (imgId == R.drawable.feedback_selected) {
			feedbackImgId.set(imgId);
			muteOthers(R.id.feedbackPane);
		} else if (imgId == R.drawable.suggestion_selected) {
			suggestionImgId.set(imgId);
			muteOthers(R.id.suggestionPane);
		} else if (imgId == R.drawable.furnace_selected) {
			furnaceImgId.set(imgId);
			muteOthers(R.id.furnacePane);
		}
		
		//����״̬
		if(imgId != R.drawable.suggestion_selected)
		{
			mContext.saveSuggestions(mContext.collectSuggestionsFromUI(), mContext.uuid);
		}
		//״̬�ָ�
		else
		{
			this.createPrecautions(mContext.getAllSuggestionsFromDB(mContext.uuid));
		}
	}

	/**
	 * ���س�paneId�������LinearLayout
	 * 
	 * @param paneId
	 */
	public void muteOthers(int paneId) {
		int[] panes = {R.id.basicPane, R.id.meterPane, R.id.plumPane, R.id.cookerPane, R.id.precautionPane, R.id.feedbackPane, R.id.furnacePane, R.id.suggestionPane};
		for(int i=0; i<panes.length; i++)
			if(paneId == panes[i])
				mContext.findViewById(panes[i]).setVisibility(LinearLayout.VISIBLE);
			else
				mContext.findViewById(panes[i]).setVisibility(LinearLayout.GONE);
	}

	// ÿ�����͵İ�����Ϣһ��
	public IntegerObservable suggestionImgId = new IntegerObservable(
			R.drawable.suggestion);
	public Command SuggestionImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.suggestion_selected);
		}
	};
	
	public IntegerObservable furnaceImgId = new IntegerObservable(
			R.drawable.furnace);
	public Command FurnaceImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.furnace_selected);
		}
	};

	public IntegerObservable basicImgId = new IntegerObservable(
			R.drawable.basic_selected);
	public Command MeterImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.meter_selected);
		}
	};

	public IntegerObservable plumImgId = new IntegerObservable(R.drawable.plum);
	public Command PlumImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.plum_selected);
		}
	};

	public IntegerObservable cookerImgId = new IntegerObservable(
			R.drawable.cooker);
	public Command CookerImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this
			.HilightChosenImg(R.drawable.cooker_selected);
		}
	};


	public IntegerObservable precautionImgId = new IntegerObservable(R.drawable.precaution);
	public Command PrecautionImgClicked = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this
			.HilightChosenImg(R.drawable.precaution_selected);
		}
	};

	public IntegerObservable meterImgId = new IntegerObservable(
			R.drawable.meter);
	public Command BasicImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.basic_selected);
		}
	};

	public IntegerObservable feedbackImgId = new IntegerObservable(
			R.drawable.feedback);
	public Command FeedbackImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this
			.HilightChosenImg(R.drawable.feedback_selected);
		}
	};

	/**
	 * ����������¼����
	 */
	public Command ShowPurchaseHistory = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if(f_buygas.get()==null || f_buygas.get().length()==0)
			{
				Toast.makeText(mContext, "���Ȳ�ѯ�û���", Toast.LENGTH_SHORT).show();
				return;
			}
			// ���ð������ݲ�����Activity
			//TODO
			Bundle bundle = new Bundle();
			bundle.putString("USERID", f_userid.get());
			bundle.putString("SUM", f_buygas.get());
			Intent intent = new Intent();
			intent.setClass(mContext, PurchaseHistoryActivity.class);
			intent.putExtras(bundle);
			mContext.startActivity(intent);				
		}
	};

	public Command BGLShiYongCmd = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if(f_bglshiyong.get())
			{
				f_bglgonglv.set("");
				f_furnace_installation_time.set("");
				f_furnace_expire_time.set("");
				f_bglpinpai.set("");
				((Spinner)mContext.findViewById(R.id.f_bglweizhi)).setSelection(0);
				((Spinner)mContext.findViewById(R.id.f_bglxianzhuang)).setSelection(0);
				IndoorInspectModel.this.f_furnace_overdue.set(false);
				IndoorInspectModel.this.f_furnace_softconnector.set(false);	
				IndoorInspectModel.this.f_furnace_trapped.set(false);	
				IndoorInspectModel.this.f_furnace_leakage.set(false);	
				IndoorInspectModel.this.f_furnace_leakage_connetor.set(false);	
				IndoorInspectModel.this.f_furnace_leakage_valve.set(false);	
				IndoorInspectModel.this.f_furnace_leakage_furnace.set(false);	
				f_furnace_precaution_remark.set("");
			}
		}
	};
	
	/**
	 * ���δ�����
	 */
	public Command ZJShiYongCmd = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if(f_zjshiyong.get())
			{
				f_cooker_expire_time.set("");
				f_cooker_installation_time.set("");
				f_zjpinpai.set("");
				((Spinner)mContext.findViewById(R.id.f_zjleixing)).setSelection(0);
				((Spinner)mContext.findViewById(R.id.f_zjxianzhuang)).setSelection(0);
				IndoorInspectModel.this.f_cooker_overdue.set(false);
				IndoorInspectModel.this.f_cooker_nofireprotection.set(false);
				IndoorInspectModel.this.f_cooker_leakage.set(false);
				f_cooker_precaution_remark.set("");
				
			}
		}
	};
	/**
	 * ���δ����ˮ��
	 */
	public Command RSQShiYongCmd = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if(f_rshqshiyong.get())
			{
				f_rshqxinghao.set("");
				f_heater_installation_time.set("");
				f_heater_expire_time.set("");
				f_rshqpinpai.set("");
				((Spinner)mContext.findViewById(R.id.f_rshqxianzhuang)).setSelection(0);
				((Spinner)mContext.findViewById(R.id.f_heater_place)).setSelection(0);
				IndoorInspectModel.this.f_heater_overdue.set(false);
				IndoorInspectModel.this.f_heater_softconnector.set(false);	
				IndoorInspectModel.this.f_heater_trapped.set(false);	
				IndoorInspectModel.this.f_heater_leakage.set(false);	
				IndoorInspectModel.this.f_heater_leakage_connetor.set(false);	
				IndoorInspectModel.this.f_heater_leakage_valve.set(false);	
				IndoorInspectModel.this.f_heater_leakage_heater.set(false);	
				f_heater_precaution_remark.set("");
			}
		}
	};
	
	/**
	 * ���ر��氲���¼
	 */
	public Command saveInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//����Ƿ���Ҫά��
			boolean needsRepair = NeedsRepair();
			if(needsRepair && f_ruhu.get() && !f_jujian.get() && !((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked())
			{
				Toast.makeText(mContext, "���ڹ��ϣ���ѡ��ά�ޡ�", Toast.LENGTH_LONG).show();
				return;
			}
			if(!needsRepair && f_ruhu.get() && !f_jujian.get() && ((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked())
			{
				Toast.makeText(mContext, "�����ڹ��ϣ�����ѡ��ά�ޡ�", Toast.LENGTH_LONG).show();
				return;
			}
			Upload(needsRepair, false);
		}
	};

	/**
	 * �ϴ������¼
	 */
	public Command UploadInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//����Ƿ���Ҫά��
			boolean needsRepair = NeedsRepair();
			if(NeedsRepair()&& f_ruhu.get() && !f_jujian.get() && !((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked())
			{
				Toast.makeText(mContext, "���ڹ��ϣ���ѡ��ά�ޡ�", Toast.LENGTH_LONG).show();
				return;
			}
			if(!NeedsRepair() && f_ruhu.get() && !f_jujian.get() && ((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked())
			{
				Toast.makeText(mContext, "�����ڹ��ϣ�����ѡ��ά�ޡ�", Toast.LENGTH_LONG).show();
				return;
			}
			Upload(needsRepair, true);
		}

	};

	/**
	 * �Ƿ���Ҫά��
	 * @return
	 */
	private boolean NeedsRepair() {
		//�ж��Ƿ�ѡ�����쳣
		if( f_sibiao.get() || f_changtong.get()|| f_fanzhuang.get()||f_qblouqi.get()||f_reading_mismatch.get()||f_qbqita.get()
				|| f_meter_wrapped.get() || f_meter_hanger.get() || f_meter_nearfire.get()|| f_meter_unfavorable.get()
				|| f_lgfushi.get()||f_lgsigai.get()||f_lglouqi.get() || f_lgbaoguo.get() ||f_lgguawu.get()||f_lghuoyuan.get()||f_lgweiguding.get() || f_lgchuanyue.get() || f_lgbubianweixiu.get() 
				|| f_bhgbaoguan.get()||f_bhgguawu.get()||f_bhglouqi.get()||f_bhgjinzhiquyu.get()||f_bhgdianyuan.get() ||f_bhggaiguan.get()|| f_bhgrst.get()|| f_bhgfushi.get() || f_bhgbubianweixiu.get() ||f_bhgwxd.get()|| f_bhgqita.get()
				|| f_jpglouqi.get()||f_jpglaohua.get()||f_jpgguochang.get()|| f_jpgwuguanjia.get()||f_jpgdiaoding.get()||f_jpganmai.get()
				|| f_cooker_overdue.get()||f_cooker_nofireprotection.get()||f_cooker_leakage.get()
				|| f_heater_overdue.get()||f_heater_softconnector.get()||f_heater_trapped.get()||f_heater_leakage.get()
				|| f_furnace_overdue.get()||f_furnace_softconnector.get()||f_furnace_trapped.get()||f_furnace_leakage.get()
				|| f_precaution_kitchen.get()||f_precaution_multisource.get() || f_zgbeizhu.get().trim().length()>0
				|| ((Spinner)mContext.findViewById(R.id.f_precaution_otheruse)).getSelectedItem().toString().length()!=0)
			return true;
		else
		{
			if(!f_biaoqianfa.get(((Spinner)mContext.findViewById(R.id.f_biaoqianfa)).getSelectedItemPosition()).trim().equals("����"))
				return true;
			if(!f_zaoqianfa.get(((Spinner)mContext.findViewById(R.id.f_zaoqianfa)).getSelectedItemPosition()).trim().equals("����"))
				return true;
			if(!f_zibifa.get(((Spinner)mContext.findViewById(R.id.f_zibifa)).getSelectedItemPosition()).trim().equals("����"))
				return true;
			return false;
		}
	}
	
	public void GetRepairPerson(final String uuid, final String inspectTable) {
				SQLiteDatabase db = null;
				//���ý���
				try {
					RepairManList.clear();
					DepartmentList.clear();
					db = mContext.openOrCreateDatabase("safecheck.db",
							Context.MODE_PRIVATE, null);
					Cursor c = db.rawQuery("SELECT NAME FROM T_PARAMS WHERE ID=?", new String[]{"����"} );
					DepartmentList.add("");
					RepairMan r1 = new RepairMan();
					r1.id = "null";
					r1.name = "";
					RepairManList.add(r1);
					while(c.moveToNext())
					{
						DepartmentList.add(c.getString(0));
					}
					c = db.rawQuery(
							"SELECT * FROM " + inspectTable  + " where id=?",
							new String[] { uuid });
					while (c.moveToNext()) {
						String id = c.getString(c.getColumnIndex("REPAIRMAN_ID"));
						if(id==null)
							return;
						((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).setChecked(false);
						Cursor c1 = db.rawQuery("SELECT ID,CODE,NAME FROM T_PARAMS WHERE ID=?", new String[]{c.getString(c.getColumnIndex("f_department"))});
						while(c1.moveToNext())
						{
							RepairMan rm = new RepairMan();
							rm.id = c1.getString(1);
							rm.name = c1.getString(2);
							RepairManList.add(rm);
						}
						for(int i=0; i<RepairManList.size(); i++)
						{
							RepairMan rm = RepairManList.get(i);
							if(rm.id.equals(id))
							{
								Spinner spinner0 = ((Spinner)mContext.findViewById(R.id.DepartmentList));
								Util.SelectItem(c.getString(c.getColumnIndex("f_department")), DepartmentList, spinner0);//����
								Spinner spinner = ((Spinner)mContext.findViewById(R.id.RepairManList));
								spinner.setSelection(i);
								((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).setChecked(true);
								break;
							}
						}
					}
					db.close();
					}
				catch(Exception e)
				{
					if(db != null)
						db.close();
				}
	}
		
	/**
	 * ���������û���Ϣ����
	 */
	public Command SearchByICCardNo = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
//			Intent intent = new Intent(mContext,QueryUserInfoActivity.class);
//			Bundle bundle = new Bundle();
//			bundle.putString("userName", f_consumername.get());
//			bundle.putString("telephone", f_consumerphone.get());
//			bundle.putString("address", f_address.get());
//			intent.putExtras(bundle);
//			mContext.startActivityForResult(intent, 1);
			mContext.GetUserInfo(f_userid.get());
		}
	};

	/**
	 * ǩ��
	 */
	public Command sign = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent();
			// ���ð������ݲ�����Activity
			Bundle bundle = new Bundle();
			bundle.putString("ID", mContext.uuid + "_sign");
			intent.setClass(mContext, AutographActivity.class);
			intent.putExtras(bundle);
			mContext.startActivityForResult(intent, 6);
		}
	};
	


	/**
	 * ���ر��沢�ϴ�
	 */
	private void Upload(boolean saveRepair, boolean trueUpload) {
		//��֤
		if(!mContext.validate())
			return;
		//����Ƿ���Ҫά��
		boolean needsRepair = NeedsRepair();
		if(needsRepair && f_ruhu.get() && !f_jujian.get() &&  !((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked())
		{
			Toast.makeText(mContext, "���ڹ��ϣ���ѡ��ά�ޡ�", Toast.LENGTH_LONG).show();
			return;
		}
		if(!needsRepair && f_ruhu.get() && !f_jujian.get() && ((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked())
		{
			Toast.makeText(mContext, "�����ڹ��ϣ�����ѡ��ά�ޡ�", Toast.LENGTH_LONG).show();
			return;
		}
		if(f_jujian.get())
			needsRepair = false;
		// �����е�ͼƬ
		// �ϴ�ͼƬ
		ArrayList<String> imgs = new ArrayList<String>();
		//�뻧���ϴ�ǩ��
		if(f_ruhu.get())
		{
			if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png")) {
				imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png");
				imgs.add("ǩ��ͼƬ");
				imgs.add(mContext.uuid + "_sign");
			}
		}
		for (int i = 1; i < 6; i++) {
			if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i + ".jpg")) {
				imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i	+ ".jpg");
				imgs.add("������Ƭ" + i);
				imgs.add(mContext.uuid + "_" + i);
			}
		}


		//���ر���
		String row = mContext.SaveToJSONString(needsRepair, true);
		if(!mContext.Save(row, "T_INSPECTION",  false))
		{
			mContext.localSaved = false;
			Toast.makeText(mContext, "���氲���¼��ƽ��ʧ��!", Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{
			mContext.localSaved = true;
			Toast.makeText(mContext, "���氲���¼��ƽ��ɹ�!", Toast.LENGTH_SHORT).show();
		}
		imgs.add(row);
		//����Ǳ��汾�أ���ȥУ��
		boolean needsValidation = trueUpload;
		//��������˻�ܼ죬Ҳ����Ҫ��֤
		needsValidation = !(!f_ruhu.get() || f_jujian.get());
		String validationURL = Vault.IIS_URL + "CAValidate/" + this.f_newmeter.get() + "/" + URLEncoder.encode(this.ROAD.get()).replace("+", "%20") 
				+ "/" + URLEncoder.encode(this.UNIT_NAME.get()).replace("+", "%20") + "/" + URLEncoder.encode(this.CUS_DOM.get()).replace("+", "%20") + "/" + URLEncoder.encode(this.CUS_DY.get()).replace("+", "%20") + "/" + URLEncoder.encode(this.CUS_FLOOR.get()).replace("+", "%20")
				+ "/" + URLEncoder.encode(this.CUS_ROOM.get()).replace("+", "%20") + "/" + Util.getSharedPreference(mContext, Vault.USER_ID) + "/" + URLEncoder.encode(Util.FormatDateToday("yyyy-MM-dd HH:mm:ss")).replace("+", "%20")  + "/" + this.f_jbdushu.get(); 
			HttpMultipartPost poster = new HttpMultipartPost(mContext, trueUpload, validationURL, needsValidation);
			poster.execute(imgs.toArray(new String[imgs.size()]));
	}

//�û�������Ϣ��
	public BooleanObservable hasNotified = new BooleanObservable(false);
	public BooleanObservable f_jujian = new BooleanObservable(false);
	public BooleanObservable f_ruhu = new BooleanObservable(true);
	public StringObservable f_anjianriqi = new StringObservable(Util.FormatDateToday("yyyy-MM-dd"));
	public StringObservable ArrivalTime = new StringObservable(Util.FormatTimeNow("HH:mm:ss"));
	//@Required(ErrorMessage = "���Ų���Ϊ��")
	//@RegexMatch(Pattern = "[0-9][a-z][A-Z]{11}$", ErrorMessage = "���ű���Ϊ11λ���ֻ�����ĸ��")
	public StringObservable f_kahao = new StringObservable("");
	@Required(ErrorMessage = "������������Ϊ��")
	public StringObservable f_consumername = new StringObservable("");
	@Required(ErrorMessage = "�����绰����Ϊ��")
	public StringObservable f_consumerphone = new StringObservable("");
	public StringObservable f_userid = new StringObservable("");
	public StringObservable f_address = new StringObservable("");
	public StringObservable city = new StringObservable("");
	public StringObservable f_area = new StringObservable("");
	public StringObservable ROAD = new StringObservable("");
	public StringObservable UNIT_NAME = new StringObservable("");
	public StringObservable CUS_DOM = new StringObservable("");
	public StringObservable CUS_DY = new StringObservable("");
	public StringObservable CUS_FLOOR = new StringObservable("");
	public StringObservable CUS_ROOM = new StringObservable("");
	public StringObservable ROAD2 = new StringObservable("");
	public StringObservable UNIT_NAME2 = new StringObservable("");
	public StringObservable CUS_DOM2 = new StringObservable("");
	public StringObservable CUS_DY2 = new StringObservable("");
	public StringObservable CUS_FLOOR2 = new StringObservable("");
	public StringObservable CUS_ROOM2 = new StringObservable("");
	@RegexMatch(Pattern = "[0-9]*$", ErrorMessage = "��ס�˿ڱ���Ϊ���֣�")
	public StringObservable f_renkou = new StringObservable("");

	//������ʹ��ʱ��
	public StringObservable f_biaohao = new StringObservable("");
	@Required(ErrorMessage = "�����������Ϊ��")
	@RegexMatch(Pattern = "[0-9]*$", ErrorMessage = "�����������Ϊ���֣�")
	public StringObservable f_jbdushu = new StringObservable("");
	public StringObservable f_shengyu = new StringObservable("");
	public StringObservable f_balance = new StringObservable("");	
	public StringObservable f_buygas = new StringObservable("");
	public BooleanObservable f_newmeter = new BooleanObservable(false);

	public BooleanObservable f_sibiao = new BooleanObservable(false);
	public BooleanObservable f_changtong = new BooleanObservable(false);
	public BooleanObservable f_fanzhuang = new BooleanObservable(false);
	public BooleanObservable f_qblouqi = new BooleanObservable(false);
	public BooleanObservable f_qbqita = new BooleanObservable(false);
	public BooleanObservable f_reading_mismatch = new BooleanObservable(false);
	public StringObservable f_qibiao = new StringObservable("");
	
	public BooleanObservable f_meter_wrapped = new BooleanObservable(false);
	public BooleanObservable f_meter_hanger = new BooleanObservable(false);
	public BooleanObservable f_meter_nearfire = new BooleanObservable(false);
	public BooleanObservable f_meter_unfavorable = new BooleanObservable(false);

	public BooleanObservable f_plumbing_valve = new BooleanObservable(false);
	public BooleanObservable f_plumbing_slipknot = new BooleanObservable(false);
	public BooleanObservable f_plumbing_scaleknot = new BooleanObservable(false);
	public BooleanObservable f_plumbing_diameter = new BooleanObservable(false);
	
	public BooleanObservable f_lgzhengchang = new BooleanObservable(true);
	public BooleanObservable f_lgfushi = new BooleanObservable(false);
	public BooleanObservable f_lgsigai = new BooleanObservable(false);
	public BooleanObservable f_lglouqi = new BooleanObservable(false);
	public BooleanObservable f_lgbaoguo = new BooleanObservable(false);
	public BooleanObservable f_lgguawu = new BooleanObservable(false);
	public BooleanObservable f_lghuoyuan = new BooleanObservable(false);
	public BooleanObservable f_lgweiguding = new BooleanObservable(false);
	public BooleanObservable f_lgchuanyue = new BooleanObservable(false);
	public BooleanObservable f_lgbubianweixiu = new BooleanObservable(false);

	public BooleanObservable f_plumbing_leakage_valve = new BooleanObservable(false);
	public BooleanObservable f_plumbing_leakage_scaleknot = new BooleanObservable(false);
	public BooleanObservable f_plumbing_leakage_slipknot = new BooleanObservable(false);
	public BooleanObservable f_plumbing_leakage_triple = new BooleanObservable(false);
	public BooleanObservable f_plumbing_leakage_diameter = new BooleanObservable(false);

	public BooleanObservable f_bhgzhengchang = new BooleanObservable(true);
	public BooleanObservable f_bhgbaoguan = new BooleanObservable(false);
	public BooleanObservable f_bhglouqi= new BooleanObservable(false);
	public BooleanObservable f_bhggaiguan = new BooleanObservable(false);
	public BooleanObservable f_bhgdianyuan = new BooleanObservable(false);
	public BooleanObservable f_bhgwxd = new BooleanObservable(false);
	public BooleanObservable f_bhgguawu = new BooleanObservable(false);
	public BooleanObservable f_bhgjinzhiquyu = new BooleanObservable(false);
	public BooleanObservable f_bhgrst= new BooleanObservable(false);
	public BooleanObservable f_bhgfushi = new BooleanObservable(false);
	public BooleanObservable f_bhgbubianweixiu = new BooleanObservable(false);
	public BooleanObservable f_bhgqita = new BooleanObservable(false);
	public StringObservable f_bhgbeizhu = new StringObservable("");
	
	public BooleanObservable f_jpgzhengchang = new BooleanObservable(true);
	public BooleanObservable f_jpglouqi = new BooleanObservable(false);
	public BooleanObservable f_jpglaohua= new BooleanObservable(false);
	public BooleanObservable f_jpgguochang = new BooleanObservable(false);
	public BooleanObservable f_jpgwuguanjia = new BooleanObservable(false);
	public BooleanObservable f_jpgdiaoding = new BooleanObservable(false);
	public BooleanObservable f_jpganmai = new BooleanObservable(false);

	public BooleanObservable f_zjshiyong = new BooleanObservable(false);

	public BooleanObservable f_rshqshiyong = new BooleanObservable(false);
	@RegexMatch(Pattern = "[0-9]*$", ErrorMessage = "��ˮ�����ʱ���Ϊ���֣�")
	public StringObservable f_rshqxinghao = new StringObservable("");

	
	public BooleanObservable f_bglshiyong = new BooleanObservable(false);
	@RegexMatch(Pattern = "[0-9]*$", ErrorMessage = "�ڹ�¯���ʱ���Ϊ���֣�")
	public StringObservable f_bglgonglv = new StringObservable("");


	public BooleanObservable f_precaution_kitchen = new BooleanObservable(false);
	public BooleanObservable f_precaution_multisource = new BooleanObservable(false);
	public ArrayListObservable<String> f_precaution_otheruse = new ArrayListObservable<String>(String.class);
	public StringObservable f_renow_id = new StringObservable("");

	public StringObservable f_zgbeizhu = new StringObservable("");

	public StringObservable f_archiveaddress = new StringObservable("");
	
	public ArrayListObservable<String> f_checktype = new ArrayListObservable<String>(String.class, new String[]{"���쵥", "����"});
	//С������
	public ArrayListObservable<String> f_property = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_iszhongdian = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_gongnuan = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_zuzhu = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> REGION_NAME = new ArrayListObservable<String>(String.class);
	
	public ArrayListObservable<String> f_baojingqi = new ArrayListObservable<String>(String.class, new String[]{" ", "��", "��"});
	public ArrayListObservable<String> f_baojingqichang = new ArrayListObservable<String>(String.class);
	public StringObservable f_alarm_installation_time = new StringObservable(""); 
	public StringObservable f_alarm_expire_time = new StringObservable(""); 
	public StringObservable f_meter_manufacture_date = new StringObservable(""); 
	public ArrayListObservable<String> f_meter_type = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_rqbiaoxing = new ArrayListObservable<String>(String.class, new String[]{" ", "���", "�ұ�"});
	public ArrayListObservable<String> f_biaochang = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_kachangjia = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_iccard_type = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_meter_cover = new ArrayListObservable<String>(String.class);

	public ArrayListObservable<String> f_plumbing_type = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_metervalve_type = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_biaoqianfa = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_zaoqianfa = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_zibifa = new ArrayListObservable<String>(String.class);

	public StringObservable f_zjpinpai = new StringObservable("");
	public ArrayListObservable<String> f_zjleixing = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_zjxianzhuang = new ArrayListObservable<String>(String.class);
	public StringObservable f_cooker_installation_time = new StringObservable("");
	public StringObservable f_cooker_expire_time = new StringObservable("");
	public BooleanObservable f_cooker_overdue= new BooleanObservable(false);
	public BooleanObservable f_cooker_nofireprotection= new BooleanObservable(false);
	public BooleanObservable f_cooker_leakage= new BooleanObservable(false);
	public StringObservable f_cooker_precaution_remark = new StringObservable("");
	
	
	public StringObservable f_rshqpinpai = new StringObservable("");
	public ArrayListObservable<String> f_rshqxianzhuang = new ArrayListObservable<String>(String.class);
	public StringObservable f_heater_installation_time = new StringObservable("");
	public StringObservable f_heater_expire_time = new StringObservable("");
	public ArrayListObservable<String> f_heater_place = new ArrayListObservable<String>(String.class);
	public BooleanObservable f_heater_overdue= new BooleanObservable(false);
	public BooleanObservable f_heater_softconnector= new BooleanObservable(false);
	public BooleanObservable f_heater_trapped= new BooleanObservable(false);
	public BooleanObservable f_heater_leakage= new BooleanObservable(false);
	public BooleanObservable f_heater_leakage_connetor= new BooleanObservable(false);
	public BooleanObservable f_heater_leakage_valve= new BooleanObservable(false);
	public BooleanObservable f_heater_leakage_heater= new BooleanObservable(false);
	public StringObservable f_heater_precaution_remark = new StringObservable("");


	public StringObservable f_bglpinpai = new StringObservable("");
	public ArrayListObservable<String> f_bglweizhi = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> f_bglxianzhuang = new ArrayListObservable<String>(String.class);
	public StringObservable f_furnace_installation_time = new StringObservable("");
	public StringObservable f_furnace_expire_time = new StringObservable("");
	public BooleanObservable f_furnace_overdue= new BooleanObservable(false);
	public BooleanObservable f_furnace_softconnector= new BooleanObservable(false);
	public BooleanObservable f_furnace_trapped= new BooleanObservable(false);
	public BooleanObservable f_furnace_leakage= new BooleanObservable(false);
	public BooleanObservable f_furnace_leakage_connetor= new BooleanObservable(false);
	public BooleanObservable f_furnace_leakage_valve= new BooleanObservable(false);
	public BooleanObservable f_furnace_leakage_furnace= new BooleanObservable(false);
	public StringObservable f_furnace_precaution_remark = new StringObservable("");

	// ά����
	public ArrayListObservable<RepairMan> RepairManList = new ArrayListObservable<RepairMan>(RepairMan.class);
	//ά���˲���
	public ArrayListObservable<String> DepartmentList = new ArrayListObservable<String>(String.class);
}


