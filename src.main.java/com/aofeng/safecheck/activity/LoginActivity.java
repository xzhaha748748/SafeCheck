package com.aofeng.safecheck.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.LoginModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class LoginActivity extends Activity {
	private LoginModel model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new LoginModel(this);
		Binder.setAndBindContentView(this, R.layout.login, model);
		this.findViewById(R.id.appVersion).setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				v.setEnabled(false);
				if(Util.dbbackup(LoginActivity.this))
					Toast.makeText(LoginActivity.this, "�������ݿ�ɹ���", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(LoginActivity.this, "�������ݿ�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				v.setEnabled(true);
				return false;
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		// ���õ�¼��ť����
		((Button) findViewById(R.id.button1)).setEnabled(true);

		// ����û���Ϣ
		SharedPreferences sp = getSharedPreferences(Vault.appID,LoginModel.SAFE_CHECK_MODEL);
		String savedUser = sp.getString(Vault.USER_NAME, "");
		if(savedUser.length()>0)
			model.Name.set(sp.getString(Vault.USER_NAME, ""));
		((TextView)findViewById(R.id.appVersion)).setText("��ǰ�汾�ţ�" +Util.getVersionCode(this));
	}
}
