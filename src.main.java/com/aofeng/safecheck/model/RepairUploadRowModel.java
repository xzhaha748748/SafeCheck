package com.aofeng.safecheck.model;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.StringObservable;

import com.aofeng.safecheck.modelview.RepairUploadModel;
import com.aofeng.utils.Vault;

public class RepairUploadRowModel {
	RepairUploadModel model;
	// 下发的安检单ID
	public StringObservable ID = new StringObservable("");
	// 地址
	public StringObservable Address = new StringObservable("");
	//进度
	public FloatObservable progress = new FloatObservable(0f);
	
	//上传成功、不成功、已检、未检、拒绝、维修、新增、删除
	public BooleanObservable UPLOADED = new BooleanObservable(false);
	public BooleanObservable UN_UPLOADED = new BooleanObservable(true);

	public RepairUploadRowModel(RepairUploadModel uploadModel, String id,	String address, String state) {
		model = uploadModel;
		Address.set(address);
		ID.set(id);
		if(state.equals(Vault.REPAIRED_UPLOADED))
		{
			this.UN_UPLOADED.set(false);
			this.UPLOADED.set(true);
		}
		else
		{
			this.UN_UPLOADED.set(true);
			this.UPLOADED.set(false);
		}
		if(this.UPLOADED.get())
			this.progress.set(1.0f);
		else
			this.progress.set(0.0f);
	}
}
