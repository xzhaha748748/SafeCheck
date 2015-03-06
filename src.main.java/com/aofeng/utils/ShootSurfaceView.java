package com.aofeng.utils;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ShootSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private Camera camera;
	private SurfaceHolder sHolder;
	public int maxZoom;

	public ShootSurfaceView(Context context) {
		super(context);
		sHolder = this.getHolder();
		sHolder.addCallback(this);
	}

	// the 3 methods below implements surfaceholder call back.
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(0);
		try {
			camera.setPreviewDisplay(holder);
			Camera.Parameters params = camera.getParameters();
			maxZoom = params.getMaxZoom();
		} catch (IOException e) {
			Log.e("Shooter", "��������ŵ�չʾ�����");
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Surface�����ı��ʱ�򽫱����ã���һ����ʾ�������ʱ��Ҳ�ᱻ����
		if (sHolder.getSurface() == null) {
			// ���SurfaceΪ�գ�����������
			return;
		}

		// ֹͣCamera��Ԥ��
		try {
			camera.stopPreview();
		} catch (Exception e) {
			Log.d("Shooter", "��Surface�ı��ֹͣԤ������");
		}

		// ��Ԥ��ǰ����ָ��Camera�ĸ������

		// ���¿�ʼԤ��
		try {
			camera.setPreviewDisplay(sHolder);
			Camera.Parameters params = camera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			camera.setParameters(params);
			camera.startPreview();
		} catch (Exception e) {
			Log.d("Shooter", "Ԥ��Camera����");
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	/**
	 * set zoom
	 * 
	 * @param zoom
	 */
	public void setZoom(int zoom) {
		Camera.Parameters params = camera.getParameters();
		if (params.isZoomSupported()) {
			params.setZoom(zoom);
		}
		camera.setParameters(params);
	}

	/**
	 * so called taking pics
	 */
	public void shoot(final PictureCallback jpegCallback) {
		camera.autoFocus(new AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if(success)
					camera.takePicture(null, null, jpegCallback);
				}
		});
	}

	public void reset() {
		camera.startPreview();
	}
}
