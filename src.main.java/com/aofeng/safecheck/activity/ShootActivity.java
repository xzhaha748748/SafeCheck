package com.aofeng.safecheck.activity;

import java.io.File;
import java.io.FileOutputStream;

import com.aofeng.safecheck.R;
import com.aofeng.utils.ShootSurfaceView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ShootActivity extends Activity implements PictureCallback{

	private ShootSurfaceView shootSurface;
	private String fileName = "test_1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shoot);
		//test purposes
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
			fileName = bundle.getString("ID");
		SeekBar zoomBar = (SeekBar) findViewById(R.id.sb_zoom);
		//get back camera zoom extent
		Camera camera = Camera.open(0);
		zoomBar.setMax(camera.getParameters().getMaxZoom());
		camera.release();
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		shootSurface = new ShootSurfaceView(this);
		preview.addView(shootSurface);
		Button button_capture = (Button) findViewById(R.id.button_capture);
		button_capture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shootSurface.shoot(ShootActivity.this);
			}
		});
		
		Button reset = (Button) findViewById(R.id.button_reset);
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shootSurface.reset();
			}
		});

		zoomBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				shootSurface.setZoom(seekBar.getProgress());

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});

	}


	/**
	 * call back on taking pic
	 */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
        FileOutputStream fos;
		try {
			fos = this.openFileOutput(fileName + ".jpg", Context.MODE_WORLD_READABLE);
	        fos.write(data);
	        fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		Intent intent =new Intent();
		intent.putExtra("result", fileName);
		ShootActivity.this.setResult(RESULT_OK, intent);
	}

}
