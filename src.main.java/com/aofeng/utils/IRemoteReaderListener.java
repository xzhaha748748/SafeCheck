package com.aofeng.utils;

import java.util.List;
import java.util.Map;

public interface IRemoteReaderListener {
	public void onSuccess(List<Map<String, Object>> list);
	public void onFailure(String errMsg);
	/**
	 * 下载进度
	 * @param progress 当前下载进度
	 * @param total 有可能为负值，负值表示不能得到下载对象的大小
	 */
	public void onProgress(int progress, int total);
}
