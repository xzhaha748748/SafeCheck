package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.observables.StringObservable;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.aofeng.safecheck.modelview.NoticeModel;

public class NoticeRowModel {
	private final String ID;

	private final NoticeModel model;

	public NoticeRowModel(String ID, NoticeModel model) {
		this.ID = ID;
		this.model = model;
	}

	// ��ʾ������
	public StringObservable Title = new StringObservable("");

	// ��������
	public StringObservable Date = new StringObservable("");
	// ��������
	public StringObservable Content = new StringObservable("");

	// ������������
	public Command ShowDetail = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			TextView textView = new TextView(model.mContext);
			textView.setBackgroundColor(Color.WHITE);
			textView.setTextColor(Color.BLACK);
			textView.setMaxWidth(280);
			
			textView.setLines(10);
			textView.setMaxLines(10);
			textView.setTextSize(25);

			//���������
			textView.setMovementMethod(ScrollingMovementMethod.getInstance());

			Dialog alertDialog = new AlertDialog.Builder(model.mContext).   
					setView(textView).
					setTitle(Title.get()).   
					setIcon(android.R.drawable.ic_dialog_info).
					setPositiveButton("ȷ��", null).
					create();   
			alertDialog.setCancelable(false);
			textView.setText(Content.get());
           
			alertDialog.show();
			
			WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();  
            layoutParams.width = 500;
            alertDialog.getWindow().setAttributes(layoutParams);  
		}
	};
}
