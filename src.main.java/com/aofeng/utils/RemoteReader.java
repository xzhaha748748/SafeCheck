package com.aofeng.utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class RemoteReader {
	private String url;
	private IRemoteReaderListener listener;
	private int total;
	private int progress;
	private Handler mHandler;
	
	/**
	 * 
	 * @param servicePath   ����·��
	 * @param hql   hibernate query language string
	 */
	public RemoteReader(String servicePath, String hql)
	{
		url = servicePath + URLEncoder.encode(hql)	.replace("+", "%20");
	}
	
	public RemoteReader(String url)
	{
		this.url = url;
	}

	/**
	 * ���ü�����
	 * @param listener
	 */
	public void setRemoteReaderListener(IRemoteReaderListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * ������ȡ������ȡ���ڲ������̣߳�����Message������ûص������߳�
	 */
	public void start()
	{
		Thread th = new Thread(new Runnable() {
			//������Ϣ
			private void sendMsg(MsgWrapper mw)
			{
				Message msg = new Message();
				msg.what = mw.what;
				msg.obj = mw;
				mHandler.sendMessage(msg);
			}

			@Override
			public void run() {
				//���ŵ���UI�̵߳�Handler
			      mHandler = new Handler(Looper.getMainLooper()) {
			          @SuppressWarnings("unchecked")
					public void handleMessage(Message msg) {
			        	  if(listener == null)
			        		  return;
			        	  MsgWrapper mw = (MsgWrapper)msg.obj;
			             if(msg.what ==0 )
			             {
			            	 listener.onProgress(mw.progress, mw.total);
			             }
			             else if(msg.what == 1)
			             {
			            	 listener.onSuccess((List<Map<String, Object>>)mw.obj);
			             }
			             else if(msg.what == -1)
			             {
			            	 listener.onFailure((String)mw.obj);
			             }
			          }
			      };
		
				try {
					URL myURL = new URL(url);
					URLConnection conn = myURL.openConnection();
					conn.connect();
					InputStream is = conn.getInputStream();
					total = conn.getContentLength();// ������Ӧ��ȡ�ļ���С
					ByteBuffer bucket = ByteBuffer.allocate(1024 * 1024 * 2);
					byte buf[] = new byte[1024];
					progress = 0;
					sendMsg(new MsgWrapper(0, total, progress, null));
					do {
						int numread = is.read(buf);
						if (numread == -1) {
							break;
						}
						progress += numread;
						bucket.put(buf, 0, numread);
						sendMsg(new MsgWrapper(0, total, progress, null));
					} while (true);
					String json = new String(bucket.array(),0, progress, Charset.forName("UTF-8"));
					bucket = null;
					System.gc();
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					JSONArray array;
					if(json.startsWith("{"))
						array =  new JSONArray("[" + json + "]");
					else
						array = new JSONArray(json);
					
					if(parseArray(array, list))
					{
						sendMsg(new MsgWrapper(1, -1, -1, list));
					}
					else
					{
						sendMsg(new MsgWrapper(-1, -1, -1, "�������ݴ���"));
					}
				} catch (Exception e) {
					sendMsg(new MsgWrapper(-1, -1, -1, "���ش��������������ϵ����Ա��"));
				}
			}

			/**
			 * ����JSON����
			 * @param array ����
			 * @param list   �������map�б�
			 * @return
			 */
			private boolean parseArray(JSONArray array, List<Map<String, Object>> list) {
				try
				{
					for(int i=0; i<array.length(); i++)
					{
						JSONObject obj = array.getJSONObject(i);
						Map<String, Object> map = new HashMap<String, Object>();
						list.add(map);
						if(!parseJSON(obj, map))
							return false;
					}
					return true;
				}
				catch(Exception e)
				{
					return false;
				}
			}

			/**
			 * ����json����
			 * @param obj ����
			 * @param map �����ֵ�ֵ�
			 * @return
			 */
			private boolean parseJSON(JSONObject obj, Map<String, Object> map) {
				try
				{
					@SuppressWarnings("unchecked")
					Iterator<String> itr = obj.keys();
					while(itr.hasNext())
					{
						String key = itr.next();
						Object property = obj.get(key);
						ValueWrapper wrapper = new ValueWrapper();
						if(!parseProperty(property, wrapper))
							return false;
						map.put(key, wrapper.value);
					}
					return true;
				}
				catch(Exception e)
				{
					return false;
				}
			}

			/**
			 * ��������
			 * @param property ����
			 * @param wrapper ����ֵ
			 * @return
			 */
			private boolean parseProperty(Object property, ValueWrapper wrapper) {
				if(property instanceof JSONArray)
				{
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					if(!parseArray((JSONArray)property, list))
						return false;
					wrapper.value=list;
				}
				else if(property instanceof JSONObject)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					if(!parseJSON((JSONObject)property,map))
						return false;
					wrapper.value = map;
				}
				else
					wrapper.value = property;
				return true;
			}
		});
		th.start();
	}

	/**
	 * ���ʹ�ñ���
	 * @param args
	 */
	public static void main(String[] args) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL,
				"select distinct c from T_CHECKPLAN c left join fetch c.f_checks");
		reader.setRemoteReaderListener(new RemoteReaderListener() {

			@Override
			public void onSuccess(List<Map<String, Object>> map) {
				super.onSuccess(map);
			}

			@Override
			public void onFailure(String errMsg) {
				super.onFailure(errMsg);
			}

		});
		reader.start();
	}

}

/**
 * ��װ����ֵ����������
 * @author Administrator
 *
 */
class ValueWrapper
{
	public Object value;
}

/**
 * ��װMessage
 * @author Administrator
 *
 */
class MsgWrapper
{
	public MsgWrapper(int what, int total, int progress, Object obj)
	{
		this.what = what;
		this.total = total;
		this.progress = progress;
		this.obj = obj;
	}
	
	public int what;
	public int total;
	public int progress;
	public Object obj;
}


