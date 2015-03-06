package com.aofeng.safecheck.model;

import gueei.binding.Command;
import gueei.binding.cursor.CursorRowModel;
import gueei.binding.cursor.IdField;
import gueei.binding.observables.StringObservable;
import android.view.View;

public class TaskRowModel extends CursorRowModel {
	
	private TaskListModel taskList;
	
	public IdField Id = new IdField(0);
	public StringObservable PersonName = new StringObservable("test1");
	public StringObservable Content = new StringObservable("test2");
	
	public TaskRowModel(TaskListModel taskList) {
		this.taskList = taskList;
	}
	public Command ShowContact = new Command() {
		public void Invoke(View view, Object... args) {
			taskList.SetEdit(TaskRowModel.this);
		}		
	};

	
}
