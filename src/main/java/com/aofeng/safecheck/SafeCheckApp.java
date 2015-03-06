package com.aofeng.safecheck;

import android.app.Application;
import gueei.binding.Binder;

public class SafeCheckApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
	}
}
