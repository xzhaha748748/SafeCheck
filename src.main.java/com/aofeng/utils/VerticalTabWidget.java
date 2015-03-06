package com.aofeng.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabWidget;

public class VerticalTabWidget extends TabWidget{
	
	public VerticalTabWidget(Context context, AttributeSet attrs) {  
        super(context, attrs);  
  
        setOrientation(LinearLayout.VERTICAL);  
    }  
  
    @Override  
    public void addView(View child) {  
        ViewGroup.LayoutParams lp = new LayoutParams(  
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
        child.setLayoutParams(lp);  
          
        super.addView(child);  
    }  
}
