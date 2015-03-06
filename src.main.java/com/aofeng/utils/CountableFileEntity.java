package com.aofeng.utils;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import com.aofeng.utils.CustomMultiPartEntity.ProgressListener;

public class CountableFileEntity extends FileEntity
{
	private final ProgressListener listener;
	

	public CountableFileEntity(File file, String contentType, ProgressListener progressListener) {
		super(file, contentType);
		this.listener = progressListener;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}
}
