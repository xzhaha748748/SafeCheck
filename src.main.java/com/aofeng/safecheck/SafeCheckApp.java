package com.aofeng.safecheck;

import java.io.File;

import com.aofeng.safecheck.activity.ShootActivity;
import com.aofeng.utils.Util;

import gueei.binding.Binder;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

public class SafeCheckApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
		GetPath();
	}

	private void GetPath() {
		ContextWrapper cw = new ContextWrapper(this);
		File directory = cw.getFilesDir();
		Util.setSharedPreference(this, "FileDir", directory.getAbsolutePath() +"/");
	}
	
	//有些Activity需要根据是否第一次进入进行某些操作
	//这是android的生命周期机制导致
	public boolean IsRepairFirstEntry = false;
	
	//当前登录的数据库名
	public String DBFileName = "";
}
