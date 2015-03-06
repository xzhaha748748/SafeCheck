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
	 * @param servicePath   服务路径
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
	 * 设置监听器
	 * @param listener
	 */
	public void setRemoteReaderListener(IRemoteReaderListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * 启动读取器，读取器内部启动线程，并把Message处理采用回调到主线程
	 */
	public void start()
	{
		Thread th = new Thread(new Runnable() {
			//发送消息
			private void sendMsg(MsgWrapper mw)
			{
				Message msg = new Message();
				msg.what = mw.what;
				msg.obj = mw;
				mHandler.sendMessage(msg);
			}

			@Override
			public void run() {
				//附着到主UI线程的Handler
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
					total = conn.getContentLength();// 根据响应获取文件大小
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
						sendMsg(new MsgWrapper(-1, -1, -1, "解析数据错误！"));
					}
				} catch (Exception e) {
					sendMsg(new MsgWrapper(-1, -1, -1, "下载错误，请检查网络或联系管理员！"));
				}
			}

			/**
			 * 解析JSON数组
			 * @param array 数组
			 * @param list   解析后的map列表
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
			 * 解析json对象
			 * @param obj 对象
			 * @param map 对象键值字典
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
			 * 解析属性
			 * @param property 属性
			 * @param wrapper 属性值
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
	 * 如何使用本类
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
 * 封装属性值，传参数用
 * @author Administrator
 *
 */
class ValueWrapper
{
	public Object value;
}

/**
 * 封装Message
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


