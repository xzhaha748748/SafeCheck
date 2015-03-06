package com.aofeng.utils;

import gueei.binding.collections.ArrayListObservable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Spinner;
import android.widget.Toast;

import com.aofeng.safecheck.activity.IndoorInspectActivity;
import com.aofeng.safecheck.activity.ShootActivity;

public class Util {
	public static String FormatDateToday(String format)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return  formatter.format(new Date());
	}

	public static String FormatDate(String format, long l)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return  formatter.format(new Date(l));
	}
	
	public static String FormatTime(String format, String dt) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return  formatter.format(new Date(dt));
	}

	public static String FormatTimeNow(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return  formatter.format(new Date());
	}

	public static boolean fileExists(String path)
	{
		File file = new File(path);
		if(file.exists())
			return true;
		else
			return false;
	}

	public static boolean DBExists(Context context)
	{
		// 判断数据库是否存在，到设置界面
		if (!(Util.fileExists(context.getDatabasePath("safecheck.db")))) {
			Toast.makeText(context, "请先进行系统设置。", Toast.LENGTH_LONG).show();
			return false;
		}
		else
		{
			//检查所有表格是否存在
			SQLiteDatabase db = context.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			try
			{
				String sql = "select * from t_checkplan where 1=1";
				db.rawQuery(sql, null);
				sql = "select * from T_IC_SAFECHECK_PAPAER where 1=1";
				db.rawQuery(sql, null);
				sql = "select * from T_INSPECTION where 1=1";
				db.rawQuery(sql, null);
				sql = "select * from T_IC_SAFECHECK_HIDDEN where 1=1";
				db.rawQuery(sql, null);
			}
			catch(Exception e)
			{
				Toast.makeText(context, "请先进行系统设置。", Toast.LENGTH_LONG).show();
				return false;
			}
			finally
			{
				if(db != null)
					db.close();
			}
			return true;
		}
	}
	
	/**
	 * @param databasePath
	 * @return
	 */
	public static boolean fileExists(File path) {
		return path.exists();
	}

	/**
	 * 删除uuid的一系列文件
	 * @param uuid
	 */
	public static void deleteFiles(Context context, String uuid) {
		File file = new File(Util.getSharedPreference(context, "FileDir") + uuid + "_sign.png");
		file.delete();
		for(int i =1; i<6; i++)
		{
			file = new File(Util.getSharedPreference(context, "FileDir") + uuid + "_" + i + ".jpg");
			file.delete();
		}
	}
	
		/**
		 * 清除标记
		 */
	public static void ClearBit(Context context, int mask, String paperId) {
			SQLiteDatabase db = context.openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);
			String sql = "update T_IC_SAFECHECK_PAPAER set CONDITION=CAST (CAST (CONDITION as INTEGER) & " + (~mask)
					+ " as TEXT) where id=?";
			db.execSQL(sql, new Object[] { paperId });
			db.close();
		}

	/**
	 * 清除状态
	 * @param context
	 * @param paperId
	 */
	public static void ResetCondition(Context context, String paperId) {
		SQLiteDatabase db = context.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		String sql = "update T_IC_SAFECHECK_PAPAER set CONDITION=‘0’  where id=?";
		db.execSQL(sql, new Object[] { paperId });
		db.close();
	}

	/**
		 * 设置标记
		 */
	public static void SetBit(Context context, int mask, String paperId) {
		//已检、拒检、无人 一组
		if(mask == Vault.INSPECT_FLAG)
			ClearBit(context, Vault.DENIED_FLAG+Vault.NOANSWER_FLAG  + Vault.UPLOAD_FLAG, paperId);
		else if(mask == Vault.DENIED_FLAG)
			ClearBit(context, Vault.INSPECT_FLAG+Vault.NOANSWER_FLAG + Vault.UPLOAD_FLAG, paperId);
		else if(mask == Vault.NOANSWER_FLAG)
			ClearBit(context, Vault.INSPECT_FLAG+Vault.DENIED_FLAG + Vault.UPLOAD_FLAG, paperId);
			
		SQLiteDatabase db = context.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		String sql = "update T_IC_SAFECHECK_PAPAER set CONDITION=CAST (CAST(CONDITION as INTEGER) | " + mask
				+ "  as TEXT) where id=?";
		db.execSQL(sql, new Object[] { paperId });
		db.close();
	}
	
	public static  Bitmap getLocalBitmap(String url) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 清除安检临时存储
	 */
public static void ClearCache(Context context, String uuid) {
		SQLiteDatabase db = context.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		String sql = "delete from T_INP where id=?";
		db.execSQL(sql, new Object[] { uuid });
		sql = "delete from T_INP_LINE where id=?";
		db.execSQL(sql, new Object[] { uuid });
		db.close();
	}

/**
 * 是否有缓存
 */
public static boolean IsCached(Context context, String uuid) {
	Boolean result= false;
	SQLiteDatabase db = context.openOrCreateDatabase("safecheck.db",
			Context.MODE_PRIVATE, null);
	String sql = "select id from T_INP where id=?";
	Cursor c = db.rawQuery(sql, new String[] { uuid });
	if(c.moveToNext())
		result = true;
	else
		result = false;
	db.close();
	return result;
}

/**
 * 得到安检维修选项
 * @param context
 * @return
 */
public static ArrayList<String> getRepairList(Context context)
{
	ArrayList<String> list = new ArrayList<String>();
	SQLiteDatabase db = null;
	try
	{
		db = context.openOrCreateDatabase("safecheck.db",
				Context.MODE_PRIVATE, null);
		String sql = "select NAME from T_PARAMS where id=?";
		Cursor c = db.rawQuery(sql, new String[] { "安检维修选项" });
		while(c.moveToNext())
			list.add(c.getString(0));
		return list;
	}
	catch(Exception e)
	{
		return new ArrayList<String>();
	}
	finally
	{
		if(db != null)
			db.close();
	}
}

/**
 * 给字符、日期值加引号
 * @param string
 * @return
 */
public static String quote(String value) {
	return "'" + value.replace("'", "''") + "'";
}

/**
 * 给数字值不加引号
 * @param value
 * @return
 */
public static String unquote(String value) {
	if(value== null)
		return "NULL";
	if(value.trim().length()==0)
		return "NULL";
	else
		return value;
}
/**
 * 处理逻辑值
 * @param value
 * @return
 */
public static String unquote(Boolean value) {
	return value.booleanValue()?"1":"0";
}

/**
 * 选择选项
 * @param text
 * @param items
 * @param spinner
 */
public static void SelectItem(String text, ArrayListObservable<String> items, Spinner spinner) {
	int idx =0;
	for(int i=0; i<items.size(); i++)
	{
		if(items.get(i).equals(text))
		{
			idx = i;
			break;
		}
	}
	spinner.setSelection(idx);
}

/**
 * 取本地版本号
 * @return
 */
public static  int getVersionCode(Context context) {
	try
	{
	    PackageInfo manager= context.getPackageManager().getPackageInfo(Vault.packageName,0);
	    return manager.versionCode;
	}
	catch(Exception e)
	{
		return -1;
	}
}
/**
 * 得到存储的偏好值
 * @param context
 * @param key
 * @return
 */
 public static String getSharedPreference(Context context, String key)
 {
	 SharedPreferences sp = context.getSharedPreferences(Vault.appID, 1);
	 return sp.getString(key, "");
 }
 
 /**
  * 存储值到偏好文件
  * @param context
  * @param key
  * @param value
  */
 public static void setSharedPreference(Context context, String key, String value)
 {
	 Editor editor = context.getSharedPreferences(Vault.appID, 1).edit();
	 editor.putString(key, value);
	 editor.commit();
 }

 /**
  * 删除所有的照片
  * @param context
  */
public static void deleteAllPics(Activity context) {
	File file = new File(Util.getSharedPreference(context, "FileDir") );        
    String[] files;      

    files = file.list();
     for (int i=0; i<files.length; i++) {  
    	 if(files[i].toUpperCase().endsWith("JPG") || files[i].toUpperCase().endsWith("PNG") )
    	 {
	         File aFile = new File(file, files[i]);   
	         aFile.delete();
    	 }
     }  
}

@SuppressWarnings("resource")
public static boolean dbbackup(Context context) {
	FileChannel inChannel = null;
	FileChannel outChannel = null;
    try
    {
		File dbFile = context.getDatabasePath("safecheck.db");  
		File exportDir = new File(Environment.getExternalStorageDirectory(), "dbBackup");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File backup = new File(exportDir, "Inspection_"+ Util.getSharedPreference(context, Vault.USER_NAME)  + "_" + Util.getSharedPreference(context, Vault.PASSWORD)
        + "_" + Util.FormatDate("yyyyMMddHHmmss", new Date().getTime()) + ".db");	
	    backup.createNewFile();
		inChannel = new FileInputStream(dbFile).getChannel();
	    outChannel = new FileOutputStream(backup).getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    return true;
    }
    catch(Exception e)
    {
    	e.printStackTrace();
    	return false;
    }
    finally
    {
    	try
    	{
    	if(inChannel != null)
    		inChannel.close();
    	if(outChannel != null)
    		outChannel.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}

}
