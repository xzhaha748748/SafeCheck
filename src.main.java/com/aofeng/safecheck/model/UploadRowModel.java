package com.aofeng.safecheck.model;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import com.aofeng.safecheck.modelview.UpLoadModel;
import com.aofeng.utils.Vault;

public class UploadRowModel {
	UpLoadModel model;
	// 下发的安检单ID
	public StringObservable ID = new StringObservable("");
	// 地址
	public StringObservable Address = new StringObservable("");
	//进度
	public FloatObservable progress = new FloatObservable(0f);
	
	//上传成功、不成功、已检、未检、拒绝、维修、新增、删除
	public BooleanObservable UPLOADED = new BooleanObservable(false);
	public BooleanObservable UN_UPLOADED = new BooleanObservable(true);
	public BooleanObservable INSPECTED = new BooleanObservable(false);
	public BooleanObservable UN_INSPECTED = new BooleanObservable(true);
	public BooleanObservable DENIED = new BooleanObservable(false);
	public BooleanObservable NOANSWER = new BooleanObservable(false);
	public BooleanObservable REPAIR = new BooleanObservable(false);
	public BooleanObservable NEW = new BooleanObservable(false);
	public BooleanObservable DELETED = new BooleanObservable(false);

	public UploadRowModel(UpLoadModel upLoadModel, String id,	String address, String state) {
		model = upLoadModel;
		Address.set(address);
		ID.set(id);
		if(state.length()!=0)
		{
			int flag = Integer.valueOf(state).intValue();
			this.INSPECTED.set((flag & Vault.INSPECT_FLAG)>0);
			this.UN_INSPECTED.set(!this.INSPECTED.get());
			this.UPLOADED.set((flag & Vault.UPLOAD_FLAG)>0);
			this.UN_UPLOADED.set(!this.UPLOADED.get());
			this.DENIED.set((flag & Vault.DENIED_FLAG)>0);
			this.NOANSWER.set((flag & Vault.NOANSWER_FLAG)>0);
			this.DELETED.set((flag & Vault.DELETE_FLAG)>0);
			this.NEW.set((flag & Vault.NEW_FLAG)>0);
			this.REPAIR.set((flag & Vault.REPAIR_FLAG)>0);
			if(this.UPLOADED.get())
				this.progress.set(1.0f);
			else
				this.progress.set(0.0f);
		}
	}

}
