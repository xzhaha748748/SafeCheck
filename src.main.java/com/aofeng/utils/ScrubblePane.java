package com.aofeng.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.aofeng.safecheck.activity.ShootActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Ϳѻ��
 */
public class ScrubblePane extends View {
	float preX;
	float preY;
	private Path path;
	public Paint paint = null;
	private int VIEW_WIDTH;
	private int VIEW_HEIGHT;
	public String url; 
	// ����һ���ڴ��е�ͼƬ����ͼƬ����Ϊ������
	Bitmap cacheBitmap = null;
	// ����cacheBitmap�ϵ�Canvas����
	Canvas cacheCanvas = null;
	//�Ƿ����
	private boolean dirty;

	public ScrubblePane(Context context, AttributeSet set) {
		super(context, set);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ��ȡ�϶��¼��ķ���λ��
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(x, y);
			preX = x;
			preY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(preX, preY, x, y);
			preX = x;
			preY = y;
			break;
		case MotionEvent.ACTION_UP:
			cacheCanvas.drawPath(path, paint); 
			path.reset();
			dirty = true;
			break;
		}
		invalidate();
		// ����true�����������Ѿ�������¼�
		return true;
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		VIEW_WIDTH = xNew;
		VIEW_HEIGHT = yNew;
		// ����һ�����View��ͬ��С�Ļ�����
		cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT,
				Config.ARGB_8888);
		if(url != null)
			showImg();
		cacheCanvas = new Canvas();
		path = new Path();
		// ����cacheCanvas������Ƶ��ڴ��е�cacheBitmap��
		cacheCanvas.setBitmap(cacheBitmap);
		// ���û��ʵ���ɫ
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(Color.BLACK);
		// ���û��ʷ��
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(8);
		// �����
		paint.setAntiAlias(true);
		paint.setDither(true);
	}

	@Override
	public void onDraw(Canvas canvas) {
		Paint bmpPaint = new Paint();
		// ��cacheBitmap���Ƶ���View�����
		canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint); // ��
		// ����path����
		canvas.drawPath(path, paint);
	}

	/**
	 * �������
	 */
	public void clearPane() {
		cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT,
				Config.ARGB_8888);
		cacheCanvas.setBitmap(cacheBitmap);
		dirty = false;
		this.invalidate();
	}

	/**
	 * ��ʾǩ��
	 */
	public void showImg() {
		try
		{
		Bitmap bmp = Util.getLocalBitmap(url);
		
        File file = new File(Util.getSharedPreference(this.getContext(), "FileDir") + "temp.tmp");

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

        // get the width and height of the source bitmap.
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Config type = bmp.getConfig();

        //Copy the byte to the file
        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, bmp.getRowBytes()*height);
        bmp.copyPixelsToBuffer(map);
        //recycle the source bitmap, this will be no longer used.
        bmp.recycle();
        System.gc();// try to force the bytes from the imgIn to be released

        //Create a new bitmap to load the bitmap again. Probably the memory will be available. 
        cacheBitmap = Bitmap.createBitmap(width, height, type);
        map.position(0);
        //load it back from temporary 
        cacheBitmap.copyPixelsFromBuffer(map);
        //close the temporary file and channel , then delete that also
        channel.close();
        randomAccessFile.close();
        // delete the temp file
        file.delete();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT,
					Config.ARGB_8888);
		}
	}

	/**
	 * ����ǩ��
	 * @param id
	 */
	public void saveBiography(String id) {
		if(!dirty)
			return;
		FileOutputStream out = null;
		File target = new File(Util.getSharedPreference(this.getContext(), "FileDir"));
		target.mkdirs();
		try {
			out = new FileOutputStream(Util.getSharedPreference(this.getContext(), "FileDir")+ id +".png");
			cacheBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Throwable ignore) {
			}
		}
	}
}
