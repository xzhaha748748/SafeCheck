package com.aofeng.safecheck.model;


import com.aofeng.safecheck.modelview.QueryUserInfoModel;

import android.content.Intent;
import android.view.View;
import gueei.binding.Command;
import gueei.binding.observables.StringObservable;

public class UserRow {
	public StringObservable userName = new StringObservable("");
	public StringObservable telephone = new StringObservable("");
	public StringObservable address = new StringObservable("");
	public StringObservable cardID = new StringObservable("");
	public StringObservable city = new StringObservable("");
	public StringObservable area = new StringObservable("");
	public StringObservable biaohao = new StringObservable("");
	public StringObservable zuoyoubiao = new StringObservable("");
	public StringObservable biaochang = new StringObservable("");
	public StringObservable road = new StringObservable("");
	public StringObservable districtname = new StringObservable("");
	public StringObservable cusDom = new StringObservable("");
	public StringObservable cusDy = new StringObservable("");
	public StringObservable cusFloor = new StringObservable("");
	public StringObservable apartment = new StringObservable("");
	public StringObservable tsum = new StringObservable("");
	public StringObservable tcount = new StringObservable("");
	public StringObservable userID = new StringObservable("");
	public StringObservable f_archiveaddress = new StringObservable("");
	
	public QueryUserInfoModel model;
	/**
	 * 
	 */
	public Command SearchUser = new Command() {
		@Override
		public void Invoke(View arg0, Object... arg1) {
			Intent intent = new Intent();
			intent.putExtra("userName", userName.get());
			intent.putExtra("telephone", telephone.get());
			intent.putExtra("address", address.get());
			intent.putExtra("cardID", cardID.toString());
			intent.putExtra("city", city.toString());
			intent.putExtra("area", area.toString());
			intent.putExtra("biaohao", biaohao.toString());
			intent.putExtra("zuoyoubiao", zuoyoubiao.toString());
			intent.putExtra("biaochang", biaochang.toString());
			intent.putExtra("road", road.toString());
			intent.putExtra("districtname", districtname.toString());
			intent.putExtra("cusDom", cusDom.toString());
			intent.putExtra("cusDy", cusDy.toString());
			intent.putExtra("cusFloor", cusFloor.toString());
			intent.putExtra("apartment", apartment.toString());
			intent.putExtra("tsum", tsum.toString());
			intent.putExtra("tcount", tcount.toString());
			intent.putExtra("userID", userID.toString());
			intent.putExtra("f_archiveaddress", f_archiveaddress.toString());
			model.mContext.setResult(130, intent);
			model.mContext.finish();
		}
	};
}
