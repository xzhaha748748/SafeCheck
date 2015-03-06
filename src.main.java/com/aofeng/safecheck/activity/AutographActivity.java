package com.aofeng.safecheck.activity;

import java.io.File;

import com.aofeng.safecheck.R;
import com.aofeng.utils.ScrubblePane;
import com.aofeng.utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AutographActivity extends Activity{
	private String fileName;
	private ScrubblePane signPad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.autograph);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
			fileName = bundle.getString("ID");
		signPad = (ScrubblePane) (findViewById(R.id.signPad));
		if(Util.fileExists(Util.getSharedPreference(this, "FileDir") + fileName + ".png"))
			signPad.url = Util.getSharedPreference(this, "FileDir") + fileName + ".png";
		Button btnClear = (Button)this.findViewById(R.id.button_clear);
		btnClear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) {
				signPad = (ScrubblePane) (findViewById(R.id.signPad));
				signPad.clearPane();
				File file = new File(Util.getSharedPreference(AutographActivity.this, "FileDir") + fileName + ".png");
				file.delete();
			}
		});
		Button btnReturn = (Button)this.findViewById(R.id.button_return);
		btnReturn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) {
				signPad.saveBiography(fileName);
				if(Util.fileExists(Util.getSharedPreference(AutographActivity.this, "FileDir") + fileName + ".png"))
				{
					Intent intent =new Intent();
					intent.putExtra("result", fileName);
					intent.putExtra("signature", "true");
					setResult(RESULT_OK, intent);
				}
				AutographActivity.this.finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		signPad.saveBiography(fileName);
		if(Util.fileExists(Util.getSharedPreference(AutographActivity.this, "FileDir") + fileName + ".png"))
		{
			Intent intent =new Intent();
			intent.putExtra("result", fileName);
			intent.putExtra("signature", "true");
			setResult(RESULT_OK, intent);
		}
		super.onBackPressed();
	}	

}
