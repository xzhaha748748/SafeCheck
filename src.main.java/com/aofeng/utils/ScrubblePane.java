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
 * 涂鸦板
 */
public class ScrubblePane extends View {
	float preX;
	float preY;
	private Path path;
	public Paint paint = null;
	private int VIEW_WIDTH;
	private int VIEW_HEIGHT;
	public String url; 
	// 定义一个内存中的图片，该图片将作为缓冲区
	Bitmap cacheBitmap = null;
	// 定义cacheBitmap上的Canvas对象
	Canvas cacheCanvas = null;
	//是否绘制
	private boolean dirty;

	public ScrubblePane(Context context, AttributeSet set) {
		super(context, set);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 获取拖动事件的发生位置
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
		// 返回true表明处理方法已经处理该事件
		return true;
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		VIEW_WIDTH = xNew;
		VIEW_HEIGHT = yNew;
		// 创建一个与该View相同大小的缓存区
		cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT,
				Config.ARGB_8888);
		if(url != null)
			showImg();
		cacheCanvas = new Canvas();
		path = new Path();
		// 设置cacheCanvas将会绘制到内存中的cacheBitmap上
		cacheCanvas.setBitmap(cacheBitmap);
		// 设置画笔的颜色
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(Color.BLACK);
		// 设置画笔风格
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(8);
		// 反锯齿
		paint.setAntiAlias(true);
		paint.setDither(true);
	}

	@Override
	public void onDraw(Canvas canvas) {
		Paint bmpPaint = new Paint();
		// 将cacheBitmap绘制到该View组件上
		canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint); // ②
		// 沿着path绘制
		canvas.drawPath(path, paint);
	}

	/**
	 * 清除画板
	 */
	public void clearPane() {
		cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT,
				Config.ARGB_8888);
		cacheCanvas.setBitmap(cacheBitmap);
		dirty = false;
		this.invalidate();
	}

	/**
	 * 显示签名
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
	 * 保存签名
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
