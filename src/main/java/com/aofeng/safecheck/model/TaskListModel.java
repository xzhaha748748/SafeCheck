package com.aofeng.safecheck.model;

import com.aofeng.safecheck.TaskEntryActivity;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class TaskListModel {
	private Activity mContext;
	
	public TaskListModel(Activity context) {
		this.mContext = context;
	}
	
	//任务列表
	public ArrayListObservable<TaskRowModel> TaskList = new ArrayListObservable<TaskRowModel>(TaskRowModel.class);

	public StringObservable PersonName = new StringObservable("test1"); 
	
	public StringObservable Content = new StringObservable("test2");
	
	public Command PopulateList = new Command(){
		public void Invoke(View view, Object... args) {
			populateTaskList();
		}
	};
	
	public Command SetProperty = new Command() {
		public void Invoke(View view, Object... args) {
			PersonName.set("dd1");
			Content.set("dd2");
		}
	};
	
	public Command ToNext = new Command(){
		public void Invoke(View View, Object... arg1) {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent(mContext,TaskEntryActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	private void populateTaskList() {
        // Build adapter with contact entries
		for(int i = 0; i < 10; i++) {
			TaskRowModel row = new TaskRowModel(this);
			row.Id.set((long)i);
			row.PersonName.set("a" + i);
			row.Content.set("content" + i);
			TaskList.add(row);
		}
	}
	
	public void SetEdit(TaskRowModel row) {
		this.PersonName.set(row.PersonName.get());
		this.Content.set(row.Content.get());
	}
}
