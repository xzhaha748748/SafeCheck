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
	
	//�������ý�������
	public Command Setup = new Command(){
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent(mContext,SetupActivity.class);
			mContext.startActivity(intent);			
		}
	};
		
	//����ƻ����ؽ�������
	public Command JobDown = new Command(){
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent(mContext,JobDownActivity.class);
			mContext.startActivity(intent);			
		}
	};
	
	//���밲��ƻ���������
	public Command BigAddress = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,BigAddressActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	//���빫���������
	public Command Notice = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,NoticeActivity.class);
			mContext.startActivity(intent);
		}
	};
	//�����ϴ���������
	public Command UpLoad = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,UpLoadActivity.class);
			mContext.startActivity(intent);
		}
	};
	//�����ҵİ����������
	public Command MySecurity = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,MySecurityActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	//����ƻ�����
	public Command CustomPlan = new Command(){
		public void Invoke(View view, Object... args){
			Intent intent = new Intent(mContext,CustomPlanActivity.class);
			mContext.startActivity(intent);
		}
	};

}
