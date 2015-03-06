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
			Log.e("Shooter", "把相机附着到展示面出错！");
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Surface发生改变的时候将被调用，第一次显示到界面的时候也会被调用
		if (sHolder.getSurface() == null) {
			// 如果Surface为空，不继续操作
			return;
		}

		// 停止Camera的预览
		try {
			camera.stopPreview();
		} catch (Exception e) {
			Log.d("Shooter", "当Surface改变后，停止预览出错");
		}

		// 在预览前可以指定Camera的各项参数

		// 重新开始预览
		try {
			camera.setPreviewDisplay(sHolder);
			Camera.Parameters params = camera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			camera.setParameters(params);
			camera.startPreview();
		} catch (Exception e) {
			Log.d("Shooter", "预览Camera出错");
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
