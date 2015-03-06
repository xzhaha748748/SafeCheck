package com.aofeng.safecheck;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.aofeng.safecheck.model.TaskListModel;

public class SafeCheckActivity extends Activity  {

	private static String TAG = "safecheck";
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		TaskListModel model = new TaskListModel(this);
		Binder.setAndBindContentView(this, R.layout.main, model);
    }
}
