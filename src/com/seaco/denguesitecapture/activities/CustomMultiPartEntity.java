package com.seaco.denguesitecapture.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.message.BasicHeader;

import android.util.Log;

//public class CustomMultiPartEntity extends MultipartEntity{
public class CustomMultiPartEntity extends MultipartEntity implements HttpEntity{
	
	private final ProgressListener listener;
	private String boundary = null;
	
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	
    boolean isSetLast = false;
    boolean isSetFirst = false;

	public CustomMultiPartEntity(final ProgressListener listener)
	{
		super();
		this.listener = listener;
		this.boundary = System.currentTimeMillis() + "";
	}

	public CustomMultiPartEntity(final HttpMultipartMode mode, final ProgressListener listener)
	{
		super(mode);
		this.listener = listener;
		this.boundary = System.currentTimeMillis() + "";
	}

	public CustomMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener)
	{
		super(mode, boundary, charset);
		this.listener = listener;
		this.boundary = System.currentTimeMillis() + "";
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException
	{
		//super.writeTo(new CountingOutputStream(outstream, this.listener, Long.valueOf(this.getContentLength())));
		super.writeTo(new CountingOutputStream(outstream, this.listener, getContentLength()));
		Log.i("writeTo-Content Length",String.valueOf(this.getContentLength()));
	}

	public static interface ProgressListener
	{
		//void transferred(long num);
		void transferred(long num, float totalSize);
	}

	public void writeFirstBoundaryIfNeeds(){
	        if(!isSetFirst){
	            try {
	                out.write(("--" + boundary + "\r\n").getBytes());
	            } catch (final IOException e) {
	                
	            }
	        }
	        isSetFirst = true;
	    }

	public void writeLastBoundaryIfNeeds() {
	        if(isSetLast){
	            return ;
	        }
	        try {
	            out.write(("\r\n--" + boundary + "--\r\n").getBytes());
	        } catch (final IOException e) {

	        }
	        isSetLast = true;
	    }	
	
	
    public void addPart(final String key, final String value) {
        writeFirstBoundaryIfNeeds();
        try {
            out.write(("Content-Disposition: form-data; name=\"" +key+"\"\r\n").getBytes());
            out.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
            out.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
            out.write(value.getBytes());
            out.write(("\r\n--" + boundary + "\r\n").getBytes());
        } catch (final IOException e) {

        }
    }

    public void addPart(final String key, final String fileName, final InputStream fin){
        addPart(key, fileName, fin, "application/octet-stream");
    }

    public void addPart(final String key, final String fileName, final InputStream fin, String type){
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: "+type+"\r\n";
            out.write(("Content-Disposition: form-data; name=\""+ key+"\"; filename=\"" + fileName + "\"\r\n").getBytes());
            out.write(type.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4096];
            int l = 0;
            while ((l = fin.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
        } catch (final IOException e) {

        } finally {
            try {
                fin.close();
            } catch (final IOException e) {

            }
        }
    }

    public void addPart(final String key, final File value) {
        try {
            addPart(key, value.getName(), new FileInputStream(value));
        } catch (final FileNotFoundException e) {

        }
    }	
    
	@Override
	public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return out.toByteArray().length;
	}

	@Override
	public Header getContentType() {
		// TODO Auto-generated method stub
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	}    
    
	@Override
	public boolean isChunked() {
		// TODO Auto-generated method stub
		return false;
	}	
	
	@Override
	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStreaming() {
		// TODO Auto-generated method stub
		return false;
	}  	
	
    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public void consumeContent() throws IOException,
    UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
            "Streaming entity does not implement #consumeContent()");
        }
    }	
	

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
        return new ByteArrayInputStream(out.toByteArray());
	}
    
	public static class CountingOutputStream extends FilterOutputStream
	{

		private final ProgressListener listener;
		private long transferred;
		private float totalSize;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener, final long totalSize)
		{
			super(out);
			this.listener = listener;
			this.transferred = 0;
			this.totalSize = totalSize;
		}

		public void write(byte[] b, int off, int len) throws IOException
		{
			out.write(b, off, len);
			this.transferred += len;
			Log.i("Transferred data: CMU ",String.valueOf(this.transferred));
			this.listener.transferred(this.transferred, totalSize);
			Log.i("Total Size data: CMU ",String.valueOf(totalSize));
			//this.listener.transferred((int) ((this.transferred / (float) getContentLength()) * 100);
		}

		public void write(int b) throws IOException
		{
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred, totalSize);
		}
	}


}
