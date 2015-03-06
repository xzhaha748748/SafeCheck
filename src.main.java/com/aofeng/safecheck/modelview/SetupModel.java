package com.aofeng.safecheck.modelview;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.SetupActivity;
import com.aofeng.utils.RemoteReader;
import com.aofeng.utils.RemoteReaderListener;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

import gueei.binding.Command;
import gueei.binding.converters.FALSE;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class SetupModel {
	private SetupActivity mContext;

	public SetupModel(SetupActivity context) {
		this.mContext = context;
		UseName.set(Util.getSharedPreference(mContext, Vault.USER_NAME));
	}
	
	//ִ�в�����������
	public Command UpdateParam = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "��ȴ��ϴβ�����ɡ�", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			if (!(Util.fileExists(mContext.getDatabasePath("safecheck.db")))) 
				GetRepairManList(true);
			else
				GetRepairManList(false);
		}
	};

	//ִ��ϵͳ��ʼ������
	public Command Init = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "��ȴ��ϴβ�����ɡ�", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			GetRepairManList(true);
		}
	};

	private void GetRepairManList(final boolean toCreateDB) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL + "sql/",
				"select ID, NAME, ENAME, f_parentname from t_user where charindex((select id from t_role where NAME='ά����Ա'),roles,1)>0");
		reader.setRemoteReaderListener(new RemoteReaderListener() {

			@Override
			public void onSuccess(List<Map<String, Object>> map) {
				super.onSuccess(map);
				ArrayList<RepairMan> RepairManList =new ArrayList<RepairMan>(); 
				for(int i=0; i<map.size(); i++)
				{
					Map<String, Object> aMap = map.get(i);
					RepairMan rm = new RepairMan();
					rm.name = (String)aMap.get("col1");
					rm.id = (String)aMap.get("col0");
					rm.department = (String)aMap.get("col3");
					RepairManList.add(rm);
				}
				GetParamList(RepairManList, toCreateDB);
			}

			@Override
			public void onFailure(String errMsg) {
				super.onFailure(errMsg);
				mContext.isBusy = false;
				Toast.makeText(mContext, "��ȡά����Աʧ�ܣ������������ӡ�", Toast.LENGTH_SHORT).show();
			}

		});
		reader.start();
	}

	/**
	 * �õ������б�
	 * @param repairManList
	 */
	private void GetParamList(final ArrayList<RepairMan> repairManList, final boolean toCreateDB) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL + "sql/",
				"select v.id id, p.Name code, v.Name name from T_PARAMETER p, T_PARAMVALUE v where p.ID = v.PROCESSID"
				+ " union select id, Value code, Name name from T_SINGLEVALUE");
		reader.setRemoteReaderListener(new RemoteReaderListener() {

			@Override
			public void onSuccess(List<Map<String, Object>> map) {
				super.onSuccess(map);
				if(toCreateDB)
					CreateDatabase(repairManList, map);
				else
					UpdateParam(repairManList, map);
				mContext.isBusy = false;
			}

			@Override
			public void onFailure(String errMsg) {
				super.onFailure(errMsg);
				Toast.makeText(mContext, "��ȡϵͳ����ʧ�ܣ������������ӡ�", Toast.LENGTH_SHORT).show();
				mContext.isBusy = false;
			}

		});
		reader.start();		
	}
	
	private void CreateDatabase(ArrayList<RepairMan> RepairManList, List<Map<String, Object>> map) {
			try {
				//�������ݿ�
				SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
				db.execSQL("DROP TABLE IF EXISTS t_version");
				String   sql = "CREATE TABLE t_version (" +
						"id VARCHAR PRIMARY KEY, " +
						"ver integer )";
				db.execSQL(sql);
				sql = "insert into t_version values('1', " + Util.getVersionCode(mContext) + ")";
				db.execSQL(sql);
				//�����ƻ���
				db.execSQL("DROP TABLE IF EXISTS t_checkplan");
				sql = "CREATE TABLE t_checkplan (" +
						"id VARCHAR PRIMARY KEY, " +
						"f_date varchar," +
						"f_name VARCHAR)";
				db.execSQL(sql);
				//�������쵥
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_PAPAER");
				sql = "CREATE TABLE T_IC_SAFECHECK_PAPAER (" +
						"id VARCHAR(80) PRIMARY KEY, " +
						"CONDITION                  varchar(80)                    null,"  +                //������
						"HasNotified				varchar(80)					   null,"  +				//���ò�����
						"USER_NAME                  varchar(80)                    null,"  +             	//�û�����
						"TELPHONE                   varchar(60)                    null,"  +          		//�绰
						"ARRIVAL_TIME               varchar(80)                    null,"  +			    //����ʱ��
						"DEPARTURE_TIME             varchar(80)             	   null,"  +		   		//�뿪ʱ��
						"ROAD                       varchar(80)                    null,"  +                //�ֵ�
						"UNIT_NAME                  varchar(80)                    null,"  +          		//С��
						"CUS_DOM                    varchar(20)                    null,"  +           		//¥��
						"CUS_DY                     varchar(20)                    null,"  +               	//��Ԫ
						"CUS_FLOOR                  varchar(20)                    null,"  +        		//¥��
						"CUS_ROOM                   varchar(20)                    null,"  +       	    	//����
						"OLD_ADDRESS                varchar(500)                   null,"  +  			    //�û�������ַ
					    "ROOM_STRUCTURE             varchar(80)                    null,"  +   	            //���ݽṹ
					    "WARM                       varchar(80)                    null,"  +                //��ů��ʽ
					    "SAVE_PEOPLE                varchar(20)                    null,"  +     			//����Ա
					    "IC_METER_NAME              varchar(20)                    null,"  +                //IC��������
					    "JB_METER_NAME              varchar(20)                    null,"  +                //����������
					    "METER_TYPE                 varchar(80)                    null,"  + 			    //����
					    "CARD_ID                    varchar(80)                    null,"  +             	//����
					    "JB_NUMBER                  integer                        null,"  +          		//������
					    "SURPLUS_GAS                integer                        null,"  +                //ʣ������
					    "RQB_AROUND                 varchar(80)                    null,"  +		        //ȼ�������ұ�
					    "RQB_JBCODE                 varchar(80)                    null,"  +			    //ȼ��������
					    "METERMADEYEAR              varchar(80)                    null,"  +			    //ȼ�����������
					    "RQB                        varchar(80)                    null,"  +				//ȼ����
					    "STANDPIPE                  varchar(80)                    null,"  +				//����
					    "RIGIDITY                   varchar(80)                    null,"  +				//�����Բ���
					    "STATIC                     varchar(80)                    null,"  +				//��ֹѹ��
					    "STATIC_DATA                varchar(80)                    null,"  +			    //��ֹѹ��ֵ
					    "TABLE_TAP                  varchar(80)                    null,"  +				//��ǰ��
					    "COOK_TAP                   varchar(80)                    null,"  +				//��ǰ��
					    "CLOSE_TAP                  varchar(80)                    null,"  +				//�Աշ�
					    "INDOOR                     varchar(80)                    null,"  + 				//���ڹ�
					    "LEAKAGE_COOKER             varchar(80)                    null,"  +				//���©��
					    "LEAKAGE_HEATER             varchar(80)                    null,"  +				//��ˮ��©��
					    "LEAKAGE_BOILER             varchar(80)                    null,"  +				//�ڹ�¯©��
					    "LEAKAGE_NOTIFIED           varchar(80)                    null,"  +				//�����֪��
					    "LEAKGEPLACE                varchar(80)					   null,"  +				//©��λ��
					    "COOK_BRAND                 varchar(80)                    null,"  +			    //���Ʒ��
					    "COOK_TYPE        			varchar(80)					   null,"  +			    //����ͺ�
					    "COOKPIPE_NORMAL            varchar(80)                    null,"  + 			    //������
					    "COOKERPIPECLAMPCOUNT       varchar(80)  				   null,"  +			    //��װ�ܿ�����
					    "COOKERPIPYLENGTH			varchar(80)					   null,"  +			    //������ܳ���
					    "COOK_DATE                  varchar(80)                    null,"  +			    //��߹�������
					  	"WATER_BRAND                varchar(80)                    null,"  +		        //��ˮ��Ʒ��
					    "WATER_TYPE                 varchar(80)                    null,"  +				//��ˮ���ͺ�
					    "WATER_PIPE                 varchar(80)                    null,"  +	 			//��ˮ�����
					    "WATER_FLUE                 varchar(80)                    null,"  + 				//��ˮ���̵�
					    "WATER_NUME                 varchar(80)                    null,"  +				//�����ܿ���
					    "WATER_DATE                 varchar(80)                    null,"  +			    //��ˮ����������
					    "WATER_HIDDEN               varchar(80)                    null,"  +		        //��ˮ������
					    "WHE_BRAND                  varchar(80)                    null,"  +			    //�ڹ�¯Ʒ��
					    "WHE_TYPE                   varchar(80)                    null,"  +	  		    //�ڹ�¯�ͺ�
					    "WHE_DATE                   varchar(80)                    null,"  +			    //�ڹ�¯��������
					    "WHE_HIDDEN                 varchar(80)                    null,"  +		        //�ڹ�¥����
 					    "USER_SUGGESTION            varchar(80)                    null,"  +			    //�ͻ����
 					    "USER_SATISFIED             varchar(80)                    null,"  +			    //�ͻ������
 					    "USER_SIGN                  varchar(80)                    null,"  +			    //�ͻ�ǩ��
					    "THREAT            	        varchar(80)                    null,"  +			    //����
					    "PHOTO_FIRST           	    varchar(80)                    null,"  +	  			//��Ƭ1
					    "PHOTO_SECOND           	varchar(80)                    null,"  +			    //��Ƭ2
					    "PHOTO_THIRD           	    varchar(80)                    null,"  +				//��Ƭ3
					    "PHOTO_FOUTH           	    varchar(80)                    null,"  +			    //��Ƭ4
					    "PHOTO_FIFTH        	    varchar(80)                    null,"  +	 			//��Ƭ5
					    "NEEDS_REPAIR        	    varchar(80)                    null,"  +	 			//�Ƿ���Ҫά��
					    "REPAIRMAN        	        varchar(80)                    null,"  +	 			//ά����
					    "REPAIRMAN_ID        	    varchar(80)                    null,"  +	 			//ά����ID
					    "REPAIR_STATE	            varchar(80)                    null,"  +                //ά��״̬
					    "f_userid			        varchar(80)					   null,"  +				//�û����
					    "CHECKPLAN_ID VARCHAR(80) null)";                                                   //����ƻ�ID
				db.execSQL(sql);
				//�뻧�����
				db.execSQL("DROP TABLE IF EXISTS T_INSPECTION");
				sql = "CREATE TABLE T_INSPECTION ("+
						"ID  TEXT(255) NOT NULL,"+
						"F_CARDNUM  TEXT(255),"+
						"F_CONSUMERNAME  TEXT(255),"+
						"F_USERID  TEXT(255),"+
						"F_CONSUMERPHONE  TEXT(255),"+
						"F_ROOMNUMBER  TEXT(255),"+
						"F_RENKOU  REAL(10),"+
						"F_DONGJIELIANG  REAL(126),"+
						"F_COMMUNITY  TEXT(255),"+
						"F_COMMUNITY2  TEXT(255),"+
						"F_PROPERTY  TEXT(255),"+
						"F_UNITADDRESS  TEXT(255),"+
						"SAVE_PEOPLE  TEXT(255),"+
						"F_ANJIANRIQI  TEXT(7),"+
						"F_GONGNUAN  TEXT(255),"+
						"F_SHANGCIRIQI  TEXT(7),"+
						"F_JUJIAN  TEXT(255),"+
						"F_RUHU  TEXT(255),"+
						"F_BAOJINGQI  TEXT(255),"+
						"F_BAOJINGQICHANG  TEXT(255),"+
						"F_SHIYONGSHIJIAN  REAL(10),"+
						"F_TANTOU  TEXT(255),"+
						"F_LGZHENGCHANG  REAL(1),"+
						"F_LGANSHE  REAL(1),"+
						"F_LGMINGSHE  REAL(1),"+
						"F_LGFUSHI  REAL(1),"+
						"F_LGSIGAI  REAL(1),"+
						"F_LGLOUQI  REAL(1),"+
						"F_LGQITA  REAL(1),"+
						"F_LIGUAN  TEXT(255),"+
						"F_KAHAO  TEXT(255),"+
						"F_RQBIAOXING  TEXT(255),"+
						"F_BIAOCHANG  TEXT(255),"+
						"F_BIAOHAO  TEXT(255),"+
						"F_QBSHIYONG  TEXT(255),"+
						"F_JBDUSHU  REAL(10),"+
						"F_KACHANGJIA  TEXT(255),"+
						"F_SHENGYU  REAL(10),"+
						"F_SIBIAO  REAL(1),"+
						"F_CHANGTONG  REAL(1),"+
						"F_BIAOMENG  REAL(1),"+
						"F_QBXIUSHI  REAL(1),"+
						"F_FANZHUANG  REAL(1),"+
						"F_QBLOUQI  REAL(1),"+
						"F_QBQITA  REAL(1),"+
						"F_QIBIAO  TEXT(255),"+
						"F_QBCHULI  TEXT(255),"+
						"F_GASDOTELL  TEXT(255),"+
						"F_BIAOQIANFA  TEXT(255),"+
						"F_ZAOQIANFA  TEXT(255),"+
						"F_ZIBIFA  TEXT(255),"+
						"F_FMCHULI  TEXT(255),"+
						"F_BHGZHENGCHANG  REAL(1),"+
						"F_BHGBAOGUAN  REAL(1),"+
						"F_BHGLOUQI  REAL(1),"+
						"F_BHGWOSHI  REAL(1),"+
						"F_BHGKETING  REAL(1),"+
						"F_BHGGAIGUAN  REAL(1),"+
						"F_BHGDIANYUAN  REAL(1),"+
						"F_BHGZAOJU  REAL(1),"+
						"F_BHGCHULI  TEXT(255),"+
						"F_QPIPEDOTELL  TEXT(255),"+
						"F_JPGZHENGCHANG  REAL(1),"+
						"F_JPGLOUQI  REAL(1),"+
						"F_JPGLAOHUA  REAL(1),"+
						"F_JPGGUOCHANG  REAL(1),"+
						"F_JPGWU  REAL(1),"+
						"F_JPGWUGUANJIA  REAL(1),"+
						"F_JPGYOUGUANJIA  REAL(1),"+
						"F_JPGCHULI  TEXT(255),"+
						"F_PIPEDOTELL  TEXT(255),"+
						"F_ZJPINPAI  TEXT(255),"+
						"F_ZJSHIYONG  REAL(1),"+
						"F_ZJLEIXING  TEXT(255),"+
						"F_ZJNIAN  REAL(10),"+
						"F_ZJYUE  REAL(10),"+
						"F_ZJXIANZHUANG  TEXT(255),"+
						"F_ZJJIANYI  TEXT(255),"+
						"F_BGLPINPAI  TEXT(255),"+
						"F_BGLSHIYONG  REAL(1),"+
						"F_BGLXINGHAO  TEXT(255),"+
						"F_BGLGONGLV  REAL(10),"+
						"F_BGLNIAN  REAL(10),"+
						"F_BGLYUE  REAL(10),"+
						"F_BGLWEIZHI  TEXT(255),"+
						"F_BGLJIANYI  TEXT(255),"+
						"F_BEIZHU  TEXT(255),"+
						"F_PIPEDODATE  TEXT(7),"+
						"F_KEHUPINGJIA  TEXT(255),"+
						"F_BHGWXD  REAL(1),"+
						"F_RSHQPINPAI  TEXT(255),"+
						"F_RSHQSHIYONG  REAL(1),"+
						"F_RSHQXINGHAO  REAL(10),"+
						"F_RSHQNIAN  REAL(10),"+
						"F_RSHQYUE  REAL(10),"+
						"F_RSHQXIANZHUANG  TEXT(255),"+
						"F_RSHQJIANYI  TEXT(255),"+
						"OP_CODE  TEXT(255),"+
						"OP_DATE  TEXT(7),"+
						"OP_TIME  TEXT(7),"+
						"F_LASTSHENGYU  REAL(126),"+
						"F_MONTHS  REAL(126),"+
						"F_YUEJUN  REAL(126),"+
						"F_VAVLEDOTELL  TEXT(255),"+
						"F_SHANGCIYUQI  REAL(126),"+
						"BULU  TEXT(255),"+
						"F_KEHUYIJIAN  TEXT(255),"+
						"F_BGLXIANZHUANG  TEXT(255),"+
						"YI  REAL(10),"+
						"F_STATE  TEXT(255),"+
						"ANJIANRIQISTR  TEXT(255),"+
						"SHIYONGSHIJIANDATE  TEXT(7),"+
						"WEIXIUSHIJIANSTR  TEXT(255),"+
						"F_BUYGAS  REAL(126),"+
						"F_ZUZHU  TEXT(255),"+
						"F_RSQRST  TEXT(255),"+
						"F_BGRST  TEXT(255),"+
						"F_OLDJIBIAOSHU  REAL(10),"+
						"F_BGLTIAN  REAL(10),"+
						"F_PIPEBZ  TEXT(255),"+
						"F_RSQBZ  TEXT(255),"+
						"F_BIGUAGUOLUBZ  TEXT(255),"+
						"F_RQZCHAONIANXIAN  REAL(1),"+
						"F_RQZXH  REAL(1),"+
						"F_RGGC  REAL(1),"+
						"F_RGLH  REAL(1),"+
						"F_RSQLQ  REAL(1),"+
						"F_RQZLJST  REAL(1),"+
						"F_FBWXBB  REAL(1),"+
						"F_JULIBUGOU  REAL(1),"+
						"F_GUANDAOGUAWU  REAL(1),"+
						"F_JIECHUDIANYUAN  REAL(1),"+
						"F_GHRQZ  REAL(1),"+
						"F_GHRQZORWX  REAL(1),"+
						"F_JIANYIGAIZAO  REAL(1),"+
						"F_TZSHWX  REAL(1),"+
						"F_ZZDWGZ  REAL(1),"+
						"F_FBJXTF  REAL(1),"+
						"F_ZZDWGZ2  REAL(1),"+
						"F_YCGW  REAL(1),"+
						"F_YCDYX  REAL(1),"+
						"F_ZGBEIZHU  TEXT(255),"+
						"F_ISZHONGDIAN  TEXT(255),"+
						"CITY  TEXT(255),"+
						"F_AREA  TEXT(255),"+
						"ROAD  TEXT(255),"+
						"REGION_NAME  TEXT(255),"+
						"UNIT_NAME  TEXT(255),"+
						"UNIT_ADDRESS  TEXT(255),"+
						"CUS_DOM  TEXT(255),"+
						"CUS_DY  TEXT(255),"+
						"CUS_FLOOR  TEXT(255),"+
						"CUS_ROOM  TEXT(255),"+
	 					 " USER_SIGN             varchar(80)                    		null," +			//�ͻ�ǩ��
						  "PHOTO_FIRST           	  varchar(80)                    		null,"	+	  			//��Ƭ1
						  "PHOTO_SECOND           	  varchar(80)                    	null,"	+			//��Ƭ2
						  "PHOTO_THIRD           	  varchar(80)                    		null,"	+				//��Ƭ3
						  "PHOTO_FOUTH           	  varchar(80)                    	null,"	+			//��Ƭ4
						  "PHOTO_FIFTH        	  varchar(80)                    		null,"	+	 			//��Ƭ5
						  "NEEDS_REPAIR        	  varchar(80)                    		null,"	+	 			//�Ƿ���Ҫά��
						  "f_department        	  varchar(80)                    		null,"	+	 			//ά�������ڲ���
						  "REPAIRMAN        	  varchar(80)                    		null,"	+	 			//ά����
						  "REPAIRMAN_ID        	  varchar(80)                    		null,"	+	 			//ά����ID
						  "REPAIR_STATE	     varchar(80)                  null," +              //ά��״̬
						  "CHECKPLAN_ID VARCHAR(80) null,"      +                            //����ƻ�ID
						  "REPAIR_DATE varchar(80) null, " +                               //ά������
						  "CHECKPAPER_ID varchar(80) null ," +                     //���쵥���
						  "CONDITION varchar(80) null, " +                             //״̬
							"hasNotified  varchar(80),"+                                     //�ѷ����ò�����
							"ARRIVAL_TIME       varchar(80)                    	 null,"+			       //����ʱ��
							"DEPARTURE_TIME   varchar(80)             	    null,"+		   		   //�뿪ʱ��
							"f_archiveaddress   varchar(255)             	    null,"+		   		   //������ַ
							"f_checktype   varchar(10)             	    null,"+		   		   //���쵥������
							"F_ALARM_INSTALLATION_TIME  TEXT(255) null ," + 
							"F_ALARM_EXPIRE_TIME  TEXT(255) null ," + 
							"F_SENSOR_INSTALLATION_TIME  TEXT(255) null ," + 
							"F_SENSOR_EXPIRE_TIME  TEXT(255) null ," + 
							"F_METER_MANUFACTURE_DATE  TEXT(255) null ," + 
							"F_METER_TYPE  TEXT(255) null ," + 
							"F_ICCARD_TYPE  TEXT(255) null ," + 
							"F_NEWMETER  REAL(1) null ," + 
							"F_BALANCE  TEXT(255) null ," + 
							"F_METER_COVER  TEXT(255) null ," + 
							"F_READING_MISMATCH  REAL(1) null ," + 
							"F_METER_WRAPPED  REAL(1) null ," + 
							"F_METER_HANGER  REAL(1) null ," + 
							"F_METER_NEARFIRE  REAL(1) null ," + 
							"F_METER_UNFAVORABLE  REAL(1) null ," + 
							"F_PLUMBING_TYPE  TEXT(255) null ," + 
							"F_PLUMBING_VALVE  REAL(1) null ," + 
							"F_PLUMBING_SLIPKNOT  REAL(1) null ," + 
							"F_PLUMBING_SCALEKNOT  REAL(1) null ," + 
							"F_PLUMBING_DIAMETER  REAL(1) null ," + 
							"F_LGBAOGUO  REAL(1) null ," + 
							"F_LGGUAWU  REAL(1) null ," + 
							"F_LGHUOYUAN  REAL(1) null ," + 
							"F_LGWEIGUDING  REAL(1) null ," + 
							"F_LGCHUANYUE  REAL(1) null ," + 
							"F_LGBUBIANWEIXIU  REAL(1) null ," + 
							"F_PLUMBING_LEAKAGE_VALVE  REAL(1) null ," + 
							"F_PLUMBING_LEAKAGE_SCALEKNOT  REAL(1) null ," + 
							"F_PLUMBING_LEAKAGE_SLIPKNOT  REAL(1) null ," + 
							"F_PLUMBING_LEAKAGE_TRIPLE  REAL(1) null ," + 
							"F_PLUMBING_LEAKAGE_DIAMETER  REAL(1) null ," + 
							"F_METERVALVE_TYPE  TEXT(255) null ," + 
							"F_BHGGUAWU  REAL(1) null ," + 
							"F_BHGJINZHIQUYU  REAL(1) null ," + 
							"F_BHGRST  REAL(1) null ," + 
							"F_BHGFUSHI  REAL(1) null ," + 
							"F_BHGBUBIANWEIXIU  REAL(1) null ," + 
							"F_BHGQITA  REAL(1) null ," + 
							"F_BHGBEIZHU  TEXT(255) null ," + 
							"F_JPGDIAODING  REAL(1) null ," + 
							"F_JPGANMAI  REAL(1) null ," + 
							"F_COOKER_INSTALLATION_TIME  TEXT(255) null ," + 
							"F_COOKER_EXPIRE_TIME  TEXT(255) null ," + 
							"F_HEATER_INSTALLATION_TIME  TEXT(255) null ," + 
							"F_HEATER_EXPIRE_TIME  TEXT(255) null ," + 
							"F_FURNACE_INSTALLATION_TIME  TEXT(255) null ," + 
							"F_FURNACE_EXPIRE_TIME  TEXT(255) null ," + 
							"F_COOKER_OVERDUE  REAL(1) null ," + 
							"F_COOKER_NOFIREPROTECTION  REAL(1) null ," + 
							"F_COOKER_LEAKAGE  REAL(1) null ," + 
							"F_HEATER_PLACE  TEXT(255) null ," + 
							"F_HEATER_OVERDUE  REAL(1) null ," + 
							"F_HEATER_SOFTCONNECTOR  REAL(1) null ," + 
							"F_HEATER_TRAPPED  REAL(1) null ," + 
							"F_HEATER_LEAKAGE  REAL(1) null ," + 
							"F_HEATER_LEAKAGE_CONNETOR  REAL(1) null ," + 
							"F_HEATER_LEAKAGE_VALVE  REAL(1) null ," + 
							"F_HEATER_LEAKAGE_HEATER  REAL(1) null ," + 
							"F_FURNACE_OVERDUE  REAL(1) null ," + 
							"F_FURNACE_SOFTCONNECTOR  REAL(1) null ," + 
							"F_FURNACE_TRAPPED  REAL(1) null ," + 
							"F_FURNACE_LEAKAGE  REAL(1) null ," + 
							"F_FURNACE_LEAKAGE_CONNETOR  REAL(1) null ," + 
							"F_FURNACE_LEAKAGE_VALVE  REAL(1) null ," + 
							"F_FURNACE_LEAKAGE_FURNACE  REAL(1) null ," + 
							"F_COOKER_PRECAUTION_REMARK  TEXT(255) null ," + 
							"F_HEATER_PRECAUTION_REMARK  TEXT(255) null ," + 
							"F_FURNACE_PRECAUTION_REMARK  TEXT(255) null ," + 
							"F_PRECAUTION_KITCHEN  REAL(1) null ," + 
							"F_PRECAUTION_MULTISOURCE  REAL(1) null ," + 
							"F_PRECAUTION_OTHERUSE  TEXT(255) null ," + 
							"F_RENOW_ID  TEXT(255) null ," + 
							"PRIMARY KEY (ID))";
				db.execSQL(sql);
				//������
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_HIDDEN");
				sql = "create table T_IC_SAFECHECK_HIDDEN (" +
						"id VARCHAR(80) not null," +
						"equipment            varchar(80)                    not null,"+     //�豸
						"param              varchar(80)                    not null,"+        //����
						"value       varchar(80)                    not null,"+      //ֵ
						"INSPECTION_ID  varchar(80)   not null ," +
						"NAME  varchar(80)   not null ," +
						"BZ                   varchar(80)                    null," +
						" PRIMARY KEY  (id, param, value))";
				db.execSQL(sql);

		
				//ά�ް��쵥��
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_TASK");
				sql = "create table T_REPAIR_TASK as select * from T_INSPECTION ";
				db.execSQL(sql);
				//ά������
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_ITEM");
				sql = "create table T_REPAIR_ITEM as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//���氲����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_INP");
				sql = "create table T_INP as select * from T_INSPECTION ";
				db.execSQL(sql);
				//���氲����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_INP_LINE");
				sql = "create table T_INP_LINE as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//����ά�޲���
				db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
				sql = "create table T_PARAMS (" +
						"ID                  varchar(80)                      null,"+  //�������
						"NAME             varchar(80)                      null,"+  //��������
						"CODE           varchar(80)                      null,"+  //��������
						"PRIMARY KEY  (ID, CODE))";
				db.execSQL(sql);
				Map<String,String> m = new HashMap<String,String>();
				for(RepairMan rm : RepairManList){
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{rm.department, rm.id, rm.name});
					m.put(rm.department, null);
				}
				Iterator iterator = m.keySet().iterator();                
	            while (iterator.hasNext()) {
	             String key = iterator.next()+"";
	             db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"����", key, key});
	            }
				for(int i=0; i<map.size(); i++)
				{
					Map<String, Object> aMap = map.get(i);
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{ (String)aMap.get("col0"),  (String)aMap.get("col1"), (String)aMap.get("col2")});
				}
				//ά�޽������ά��ѡ��ŵ��˱�
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT");
				sql = "create table T_REPAIR_RESULT (" +
						"ID                  varchar(80)                      null,"+  //������
						"CONTENT             varchar(200)                      null,"+  //ά������
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				//ά�޽����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT2");
				sql = "create table T_REPAIR_RESULT2 (" +
						"ID                  varchar(80)                      null,"+  //������
						"CONTENT             varchar(200)                      null,"+  //ά������
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				db.close();
				
				//��ʾ�����ɹ�
				Toast toast = Toast.makeText(mContext, "��ʼ����ɡ�", Toast.LENGTH_SHORT);
				toast.show();
			} catch(Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, "��ʼ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
		}

	private void UpdateParam(ArrayList<RepairMan> RepairManList, List<Map<String, Object>> map) {
		try {
			//�������ݿ�
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);		
			//����ά�޲���
			db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
			String sql = "create table T_PARAMS (" +
					"ID                  varchar(80)                      null,"+  //�������
					"NAME             varchar(80)                      null,"+  //��������
					"CODE           varchar(80)                      null,"+  //��������
					"PRIMARY KEY  (ID, CODE))";
			db.execSQL(sql);
			Map<String,String> m = new HashMap<String,String>();
			for(RepairMan rm : RepairManList){
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{rm.department, rm.id, rm.name});
				m.put(rm.department, null);
			}
			Iterator iterator = m.keySet().iterator();                
            while (iterator.hasNext()) {
             String key = iterator.next()+"";
             db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"����", key, key});
            }
			//����ά��ѡ��
			for(int i=0; i<map.size(); i++)
			{
				Map<String, Object> aMap = map.get(i);
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{ (String)aMap.get("col0"),  (String)aMap.get("col1"), (String)aMap.get("col2")});
			}
			db.close();
			
			//��ʾ�����ɹ�
			Toast toast = Toast.makeText(mContext, "������ȡ��ɡ�", Toast.LENGTH_SHORT);
			toast.show();
		} catch(Exception e) {
			Toast.makeText(mContext, "������ȡʧ�ܣ�", Toast.LENGTH_SHORT).show();
		}
	}
	
	//�û�����
	public StringObservable UseName = new StringObservable("");

	// ������
	public StringObservable OldPassword = new StringObservable("");
	// ������
	public StringObservable NewPassword = new StringObservable("");
	// �ٴ�����������
	public StringObservable NewPasswordAgain = new StringObservable("");

	public Command ChangePassword = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//������֤
			if (CheckPassword()) {
				//���÷���
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							HttpPost httpPost = new HttpPost(Vault.AUTH_URL + "entity");
							StringEntity entity = new StringEntity("[{data:{id:'" + Util.getSharedPreference(mContext, Vault.USER_ID) + "',password:'" + NewPassword.get() + "'}}]" );
							httpPost.setEntity(entity);
							
							HttpClient httpClient = new DefaultHttpClient();
							HttpContext httpContext = new BasicHttpContext();
							HttpResponse response = httpClient.execute(httpPost, httpContext);
							int code = response.getStatusLine().getStatusCode();

							// �����������
							if (code == 200) {
								String strResult = EntityUtils.toString(response
										.getEntity());
								Message msg = new Message();
								msg.obj = strResult;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
							else 
							{
								Message msg = new Message();
								msg.what = 2;
								mHandler.sendMessage(msg);
							}
						}
						catch(Exception e)
						{
							Message msg = new Message();
							msg.what = 0;
							mHandler.sendMessage(msg);
						}
					}
				});
				th.start();
			}
		}
	};

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (1 == msg.what)
			{
				Toast.makeText(mContext, "�����޸ĳɹ���", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(mContext, "�����޸�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private boolean CheckPassword() {
		if ((OldPassword.get()).equals(Util.getSharedPreference(mContext, Vault.PASSWORD))) {
			if ((NewPassword).get().equals(NewPasswordAgain.get()) && (!(NewPassword.get().equals("")))) {
				return true;
			} else {
				Toast.makeText(mContext, "�������������������䣡", Toast.LENGTH_SHORT).show();
				return false; 
			}
		} else {
			Toast.makeText(mContext, "ԭ�����������", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}