package com.aofeng.safecheck;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.aofeng.safecheck.model.TaskEntryModel;

public class TaskEntryActivity extends TabActivity{
	TaskEntryModel vm;
	@Override
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        //LayoutInflater.from(this).inflate(R.layout.startwork, tabHost.getTabContentView(), true);        
        setContentView(R.layout.startwork);
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("��Ҫ1").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("��Ҫ2").setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("��Ҫ3").setContent(R.id.tab3));
        tabHost.setCurrentTab(1);
           }  
}