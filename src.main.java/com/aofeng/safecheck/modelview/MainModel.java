package com.aofeng.safecheck.modelview;

import gueei.binding.Command;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.aofeng.safecheck.activity.*;

public class MainModel {
	private Activity mContext;

	public MainModel(Activity context) {
		this.mContext = context;
	}
	
	//进入设置界面命令
	public Command Setup = new Command(){
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent(mContext,SetupActivity.class);
			mContext.startActivity(intent);			
		}
	};
		
	//进入计划下载界面命令
	public Command JobDown = new Command(){
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent(mContext,JobDownActivity.class);
			mContext.startActivity(intent);			
		}
	};
	
	//进入安检计划界面命令
	public Command BigAddress = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,BigAddressActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	//进入公告界面命令
	public Command Notice = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,NoticeActivity.class);
			mContext.startActivity(intent);
		}
	};
	//进入上传界面命令
	public Command UpLoad = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,UpLoadActivity.class);
			mContext.startActivity(intent);
		}
	};
	//进入我的安检界面命令
	public Command MySecurity = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,MySecurityActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	//进入计划定制
	public Command CustomPlan = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,CustomPlanActivity.class);
			mContext.startActivity(intent);
		}
	};

}
