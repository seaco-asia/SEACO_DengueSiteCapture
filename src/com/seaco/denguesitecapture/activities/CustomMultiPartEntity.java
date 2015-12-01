package com.seaco.denguesitecapture.activities;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import android.util.Log;

public class CustomMultiPartEntity extends MultipartEntity
{

	private ProgressListener listener;

	public CustomMultiPartEntity()
	{
		super();
		Log.d("test purpose customMultiPartEntity 1:","test purpose");

	}

	public CustomMultiPartEntity(final HttpMultipartMode mode)
	{
		super(mode);

	}

	public CustomMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset)
	{
		super(mode, boundary, charset);

	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException
	{
		super.writeTo(new CountingOutputStream(outstream, this.listener));


	}

	public ProgressListener getListener() {
		return listener;
	}

	public void setListener(ProgressListener listener) {
		this.listener = listener;
	}
	/**
	 * 
	 * Count the OutputStream
	 *
	 */
	public static class CountingOutputStream extends FilterOutputStream
	{

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener)
		{
			super(out);
			this.listener = listener;
			this.transferred = 0;
			Log.d("test purpose customMultiPartEntity 2:","test purpose");
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException
		{
			out.write(b, off, len);
			this.transferred += len;

			if(this.listener !=null){

				this.listener.transferred(this.transferred);
				Log.d("test purpose customMultiPartEntity 3:","test purpose");

			}

		}
		@Override
		public void write(int b) throws IOException
		{
			out.write(b);
			this.transferred++;

			if(this.listener !=null){

				this.listener.transferred(this.transferred);
				Log.d("test purpose customMultiPartEntity 4:","test purpose");

			}

		}
	}
}