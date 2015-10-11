package com.seaco.denguesitecapture.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.seaco.denguesitecapture.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.seaco.denguesitecapture.activities.CustomMultiPartEntity.ProgressListener;
import com.seaco.denguesitecapture.model.MySQLiteHelper;
import com.seaco.denguesitecapture.model.SitePhotos;

public class CapturePhoto extends Activity implements OnClickListener {
	private Button mTakePhoto;
	private ImageView mImageView;
	private static final String TAG = "upload";
	private MySQLiteHelper db;
	private String siteChoice;
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Intent inte = getIntent();
		siteChoice = inte.getStringExtra("siteChoice"); //We now know the type of site selected by the user
		
		mTakePhoto = (Button) findViewById(R.id.take_photo);
		mImageView = (ImageView) findViewById(R.id.imageview);

		mTakePhoto.setOnClickListener(this);
		
		db = new MySQLiteHelper(this);
		
		gps = new GPSTracker(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.capture, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.take_photo:
			takePhoto();
			break;
		}
	}

	private void takePhoto() {
//		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//		startActivityForResult(intent, 0);
		dispatchTakePictureIntent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onActivityResult: " + this);
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
			setPic();
		}
	}
	
	private void sendPhoto(Bitmap bitmap) throws Exception {
		new UploadTask().execute(bitmap);
	}

	private class UploadTask extends AsyncTask<Bitmap, Integer, Void> implements DialogInterface.OnCancelListener {
		
		private ProgressDialog mProgressDialog;
		protected Context mContext;
		long totalSize;
		private long sizeLength;
		
		/*public UploadTask(Context context){
			mContext = context;
		}*/
		
		protected void onPreExecute(){
			mProgressDialog = new ProgressDialog(CapturePhoto.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setTitle("Uploading image to Dengue Server");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setMessage("Uploading...");
			mProgressDialog.setMax(100);
			mProgressDialog.show();
		}
		
		protected Void doInBackground(Bitmap... bitmaps) {
			if (bitmaps[0] == null)
				return null;
			setProgress(0);
			
			int progressCounter = 0;
			
			
			/* Needs cleaning up -- reuse code */
	        double latitude = 0.0;
	        double longitude = 0.0;
	        
	        /* Read the latest longitude and latitude data */
	    	if(gps.canGetLocation()){
	    	   latitude = gps.getLatitude();
	    	   longitude = gps.getLongitude();
	    	}else{
	    	   gps.showSettingsAlert();
	    	}			
			
			Bitmap bitmap = bitmaps[0];
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
			InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				
	            CustomMultiPartEntity entity = new CustomMultiPartEntity(new ProgressListener() {
	                public void transferred(long num, float totalSize){
	                	publishProgress((int)((num / totalSize) * 100));
	                }
	            });

				MultipartEntityCustom customEntity = new MultipartEntityCustom();
				
				String fileMillis = System.currentTimeMillis() + ".jpg";
				
				TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
				
				
				StringBody lat = null, lon = null, site = null, im = null;
				
				try{
					lat = new StringBody(String.valueOf(latitude));
					lon = new StringBody(String.valueOf(longitude));
					site = new StringBody(siteChoice);
					im = new StringBody(mngr.getDeviceId());
				}catch(Exception el){
					Log.i("error_stringbody",lat.toString() + lon.toString() + site.toString() + im.toString());
				}
				
				//FormBodyPart bodyPart = new FormBodyPart("lat",lat);
				
				/* Set a server side php script location */
				HttpPost httppost = new HttpPost("http://xyz.abc.com/server.php");
				
				/* All the details that we need to upload to the server */
				customEntity.addPart("myFile", fileMillis, in);  //The image
				customEntity.addPart("latitude", String.valueOf(latitude));
				customEntity.addPart("lat", String.valueOf(latitude));
				customEntity.addPart("lon", String.valueOf(longitude));
				customEntity.addPart("siteChoice", siteChoice);
				customEntity.addPart("imei", mngr.getDeviceId());
				
				Log.i("customEntity","Lat: "+String.valueOf(latitude) + "Lon: "+ String.valueOf(longitude) + "siteChoice: "+ siteChoice + "imei: " + mngr.getDeviceId());
				
				/* New */
				httppost.setEntity(customEntity);
				
				db.addPicture(new SitePhotos(fileMillis,""));
				
				Log.i(TAG, "request " + httppost.getRequestLine());
				HttpResponse response = null;
				try {
					response = httpclient.execute(httppost);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if (response != null)
						Log.i(TAG, "response " + response.getStatusLine().toString());
				} finally {

				}
			} finally {

			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress){	
			//protected void onProgressUpdate(Void... values) {
			// TODO: Progress update needs fixing!
			//super.onProgressUpdate(values);
			mProgressDialog.setProgress((int) (progress[0]));
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			//publishProgress(100);
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			Toast.makeText(CapturePhoto.this, "Uploaded to server", Toast.LENGTH_LONG).show();
		}
	
		public void onCancel(DialogInterface dialog){
			cancel(true);
			dialog.dismiss();
		}
	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume: " + this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
	}
	
	String mCurrentPhotoPath;
	
	static final int REQUEST_TAKE_PHOTO = 1;
	File photoFile = null;

	private void dispatchTakePictureIntent() {
		
	    double latitude = 0.0;
	    double longitude = 0.0;
		
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	            
	        } catch (IOException ex) {
	            // Error occurred while creating the File

	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));         
	            
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}

	/**
	 * http://developer.android.com/training/camera/photobasics.html
	 */
	private File createImageFile() throws IOException {
	    double latitude = 0.0;
	    double longitude = 0.0;
		
		// Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
	    File dir = new File(storageDir);
	    if (!dir.exists())
	    	dir.mkdir();
	    
	    File image = new File(storageDir + "/" + imageFileName + ".jpg");

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = image.getAbsolutePath();
	    Log.i(TAG, "photo path = " + mCurrentPhotoPath);
	
	    
	    return image;
	}
	
	private void setPic() {
		
		//Lets try to set the exif here
        double latitude = 0.0;
        double longitude = 0.0;
        
        
    	    /* Read the latest longitude and latitude data */
    	    if(gps.canGetLocation()){
    	    	latitude = gps.getLatitude();
    	    	longitude = gps.getLongitude();
    	    }else{
    	    	gps.showSettingsAlert();
    	    }
    	    
    	    ExifInterface exif;
    	    
    	    /* Add EXIF data on the image */
    	    try{
    	    	
    	    	//exif = new ExifInterface(storageDir + "/" + imageFileName + ".jpg");
    	    	//exif = new ExifInterface(photoFile.getAbsolutePath());
    	    	exif = new ExifInterface(mCurrentPhotoPath);
    	    	
    	    	
    	        int lat_1 = (int)Math.floor(latitude);
    	        int lat_2 = (int)Math.floor((latitude - lat_1) * 60);
    	        double lat_3 = (latitude - ((double)lat_1+((double)lat_2/60))) * 3600000;

    	        int lon_1 = (int)Math.floor(longitude);
    	        int lon_2 = (int)Math.floor((longitude - lon_1) * 60);
    	        double lon_3 = (longitude - ((double)lon_1+((double)lon_2/60))) * 3600000;

    	        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat_1+"/1,"+lat_2+"/1,"+lat_3+"/1000");
    	        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon_1+"/1,"+lon_2+"/1,"+lon_3+"/1000");


    	        if (latitude > 0) {
    	            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
    	        } else {
    	            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
    	        }

    	        if (longitude > 0) {
    	            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
    	        } else {
    	        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
    	        }

    	        exif.saveAttributes();
    	    }
    	    catch (FileNotFoundException io) {}
            catch (IOException io) {}
            catch (NullPointerException np){}	    		
		
		
		// Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor << 1;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    
	    Matrix mtx = new Matrix();
	    mtx.postRotate(90);
	    // Rotating Bitmap
	    Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

	    if (rotatedBMP != bitmap)
	    	bitmap.recycle();
	    
	    mImageView.setImageBitmap(rotatedBMP);
	    
	    try {
			sendPhoto(rotatedBMP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
