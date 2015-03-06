package com.aofeng.utils;

import java.util.List;
import java.util.Map;

public interface IRemoteReaderListener {
	public void onSuccess(List<Map<String, Object>> list);
	public void onFailure(String errMsg);
	/**
	 * ���ؽ���
	 * @param progress ��ǰ���ؽ���
	 * @param total �п���Ϊ��ֵ����ֵ��ʾ���ܵõ����ض���Ĵ�С
	 */
	public void onProgress(int progress, int total);
}
