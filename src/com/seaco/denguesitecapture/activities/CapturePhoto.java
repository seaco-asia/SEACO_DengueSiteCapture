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
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.seaco.denguesitecapture.R;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seaco.denguesitecapture.model.MySQLiteHelper;
import com.seaco.denguesitecapture.model.SitePhotos;

public class CapturePhoto extends Activity implements OnClickListener {
	private Button mTakePhoto, mSavePhoto, mAddDescImage, mAddAddressImage;
	private ImageView mImageView;
	private static final String TAG = "upload";
	private static final String TAG_PROGRESS = "progress upload";
	private MySQLiteHelper db;
	private String siteChoice;
	private EditText mEditText, mImageDesc, answAddress_1, answAddress_3, answAddress_5, answAddress_6, mEditTextAddr;
	GPSTracker gps;
	private ImageButton mSaveImageDesc, mSaveImageAddress;
	private TextView titleAddress_I1, titleAddress_I3, titleAddress_I5;
	private RadioGroup radTypeStreet, radTypeArea, radTypeMukim;
	RadioButton radBtnJln, radBtnLrg, radBtnNotAppStreet, radBtnTmn, radBtnKg, radBtnFelda, radBtnNotAppArea,
	radBtnBekok, radBtnChaah, radBtnGemereh, radBtnSgSgmt, radBtnJabi;
	LinearLayout linearLayoutQuestion3, linearLayoutQuestion5;
	
	ProgressDialog progressDialog;
	long imageSize = 0; // kb
	public ProgressListener listener;
	Boolean captureExist = false;
	Intent intentObject = getIntent();
	String userID, typeStreetValue, typeAreaValue, typeMukimValue, houseBatu, typeStreetValueDesc, typeAreaValueDesc, typeMukimValueDesc,
	fullAddress;

	Context context = this;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Intent inte = getIntent();
		siteChoice = inte.getStringExtra("siteChoice"); //We now know the type of site selected by the user
		userID = inte.getStringExtra("userID"); //We now know the type of site selected by the user

		Log.d(TAG, "idCommunity["+userID+"]");

		mTakePhoto = (Button) findViewById(R.id.take_photo);

		mImageView = (ImageView) findViewById(R.id.imageview);

		mEditText =  (EditText) findViewById(R.id.editText_Photo);
		//mEditText.setVisibility(View.INVISIBLE);
		mEditText.setEnabled(false); 
		//mEditText.setText("Click here to fill up this field");

		mEditTextAddr =  (EditText) findViewById(R.id.editText_Address);
		mEditTextAddr.setEnabled(false); 
		//mEditTextAddr.setText("Click here to fill up this field");

		mSavePhoto = (Button) findViewById(R.id.save_Photo);
		//mSavePhoto.setVisibility(View.INVISIBLE);
		mSavePhoto.setEnabled(false); 

		//mAddDescImage = (Button) findViewById(R.id.add_descImage);
		//mAddDescImage.setEnabled(false);

		//mAddAddressImage = (Button) findViewById(R.id.add_addressImage);
		//mAddAddressImage.setEnabled(false);

		mTakePhoto.setOnClickListener(this);
		mSavePhoto.setOnClickListener(this);
		//mAddDescImage.setOnClickListener(this);
		//mAddAddressImage.setOnClickListener(this);

		mEditText.setOnClickListener(this);
		mEditTextAddr.setOnClickListener(this);

		/* temporary using before implement server database
		 * 
			db = new MySQLiteHelper(this);
		 *	
		 */

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
		case R.id.save_Photo:
			Bitmap photo = getPic();
			try {
				sendPhoto(photo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			//case R.id.add_descImage:
			//addDescriptionImage();
			//break;
			//case R.id.add_addressImage:
			//addAddressImage();
			//break;
		case R.id.editText_Photo:
			addDescriptionImage();
			//takePhoto();
			break;
		case R.id.editText_Address:
			addAddressImage();
			//takePhoto();
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
			getPic();

		}
	}

	private void sendPhoto(Bitmap bitmap) throws Exception {
		new UploadTask().execute(bitmap);
	}

	private class UploadTask extends AsyncTask<Bitmap, Integer, Boolean> implements ProgressListener, DialogInterface.OnCancelListener {

		private ProgressDialog mProgressDialog;
		protected Context mContext;

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

		protected Boolean doInBackground(Bitmap... bitmaps) {
			if (bitmaps[0] == null)
				return null;
			setProgress(0);

			/*photoDesc*/
			mEditText =  (EditText) findViewById(R.id.editText_Photo);

			/* Needs cleaning up -- reuse code */
			double latitude = 0.0;
			double longitude = 0.0;

			/* Read the latest longitude and latitude data */
			if(gps.canGetLocation()){
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				Log.d(TAG,"latitude ["+latitude+"] and longitude ["+longitude+"]");
			}else{
				Log.d(TAG,"latitude else ["+latitude+"] and longitude ["+longitude+"]");
				gps.showSettingsAlert();
			}			

			Bitmap bitmap = bitmaps[0];

			ByteArrayOutputStream stream = new ByteArrayOutputStream();   
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream  
			byte[] imageInByte = stream.toByteArray(); 
			long lengthbmp = imageInByte.length; 
			imageSize = this.getFileSize(lengthbmp);

			InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				//MultipartEntityCustom customEntity = new MultipartEntityCustom();
				CustomMultiPartEntity customEntity = new CustomMultiPartEntity();

				String fileMillis = System.currentTimeMillis() + ".jpg";

				TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 


				StringBody lat = null, lon = null, site = null, im = null, insertBy = null;
				StringBody photoDesc = null;
				StringBody houseNo = null, typeStreetValues = null, houseStreetName = null, typeAreaValues = null, 
						houseAreaName = null, houseBatu = null, typeMukimValues = null, houseFullAddress = null;

				try{
					lat = new StringBody(String.valueOf(latitude));
					lon = new StringBody(String.valueOf(longitude));
					site = new StringBody(siteChoice);
					im = new StringBody(mngr.getDeviceId());
					photoDesc = new StringBody(mEditText.getText().toString());
					insertBy = new StringBody(userID);
					houseNo = new StringBody(answAddress_1.getText().toString());
					typeStreetValues = new StringBody(typeStreetValue);
					houseStreetName = new StringBody(answAddress_3.getText().toString());
					typeAreaValues = new StringBody(typeAreaValue);
					houseAreaName = new StringBody(answAddress_5.getText().toString());
					houseBatu = new StringBody(answAddress_6.getText().toString());
					typeMukimValues = new StringBody(typeMukimValue);
					houseFullAddress = new StringBody(mEditTextAddr.getText().toString());

				}catch(Exception el){
					Log.i("error_stringbody",lat.toString() + lon.toString() + site.toString() + im.toString() + photoDesc.toString() + insertBy +
							houseNo.toString() + typeStreetValues + houseStreetName.toString() + typeAreaValues + houseAreaName.toString() + houseBatu + typeMukimValues);
				}

				customEntity.setListener(this);

				/* Set a server side php script location */
				HttpPost httppost = new HttpPost(Config.URL_UPLOAD);

				byte[] data = null;
				try {
					data = this.convertToByteArray(in);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(data),fileMillis);
				Log.i("inputstreambody_clength",String.valueOf(inputStreamBody.getContentLength()));
				customEntity.addPart("myFile", inputStreamBody);  //The image
				customEntity.addPart("latitude", lat);
				customEntity.addPart("lat", lat);
				customEntity.addPart("lon", lon);
				customEntity.addPart("siteChoice", site);
				customEntity.addPart("imei", im);	
				customEntity.addPart("photoDesc",photoDesc);
				customEntity.addPart("insertBy",insertBy);
				customEntity.addPart("houseNo",houseNo);
				customEntity.addPart("typeStreetValues",typeStreetValues);
				customEntity.addPart("houseStreetName",houseStreetName);
				customEntity.addPart("typeAreaValues",typeAreaValues);
				customEntity.addPart("houseAreaName",houseAreaName);
				customEntity.addPart("houseBatu",houseBatu);
				customEntity.addPart("typeMukimValues",typeMukimValues);
				customEntity.addPart("houseFullAddress",houseFullAddress);

				/* temporary using before implement server database
				 * 
				String photoDesc = mEditText.getText().toString();

				//combine the value of latitude and longitude
				String latitudeLongitude = String.valueOf(latitude)+","+String.valueOf(longitude);
				Log.i("customEntity","Lat: "+String.valueOf(latitude) + "Lon: "+ String.valueOf(longitude) + "siteChoice: "+ siteChoice + "imei: " + mngr.getDeviceId());

				//Insert into db
				//db.addPicture(new SitePhotos(fileMillis,latitudeLongitude,photoDesc));
				 * 
				 */

				/* New */
				httppost.setEntity(customEntity);
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
			return true;
		}

		private long getFileSize(long lengthbmp) {
			long length = 0;

			try {

				length = lengthbmp / 1024;

			} catch (Exception e) {

				e.printStackTrace();
			}

			return length;
		}

		@Override
		protected void onProgressUpdate(Integer... progress){	
			//protected void onProgressUpdate(Void... values) {
			// TODO: Progress update needs fixing!
			//super.onProgressUpdate(values);
			mProgressDialog.setProgress((int) (progress[0]));
		}

		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			Log.d("onPostExecute upload","result: "+result);
			super.onPostExecute(result);

			if(result){
				mProgressDialog.dismiss();
				Toast.makeText(CapturePhoto.this, "Uploaded to server", Toast.LENGTH_LONG).show();
			}else{
				// UPLOADING DATA FAILED
				mProgressDialog.setMessage("Uploading Failed");
				mProgressDialog.setCancelable(true);
			}
		}

		public void onCancel(DialogInterface dialog){
			cancel(true);
			dialog.dismiss();
		}


		private byte[] convertToByteArray(InputStream inputStream) throws IOException{

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			int next = inputStream.read();
			while (next > -1) {
				bos.write(next);
				next = inputStream.read();
			}

			bos.flush();

			return bos.toByteArray();
		}

		public void transferred(long num) {

			// COMPUTE DATA UPLOADED BY PERCENT
			long dataUploaded = ((num / 1024) * 100 ) / imageSize;

			// PUBLISH PROGRESS
			this.publishProgress((int)dataUploaded);

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
		//Toast.makeText(CapturePhoto.this, "Capture Done", Toast.LENGTH_LONG).show();
		Log.i(TAG, "onSaveInstanceState");
	}

	String mCurrentPhotoPath;

	static final int REQUEST_TAKE_PHOTO = 1;
	File photoFile = null;

	private void dispatchTakePictureIntent() {

		double latitude = 0.0;
		double longitude = 0.0;
		mEditText.setText("");

		Log.d(TAG_PROGRESS, "take photo 3: ");
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
		String storageDir = Environment.getExternalStorageDirectory() + "/picupload/" +userID;
		File dir = new File(storageDir);
		if (!dir.exists())
			dir.mkdir();

		File image = new File(storageDir + "/" + imageFileName + ".jpg");

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();

		Log.i(TAG, "photo path = " + mCurrentPhotoPath);

		return image;
	}

	private Bitmap getPic(){

		captureExist = true;

		//Lets try to set the exif here
		double latitude = 0.0;
		double longitude = 0.0;


		//Read the latest longitude and latitude data 
		if(gps.canGetLocation()){
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
		}else{
			gps.showSettingsAlert();
		}

		ExifInterface exif;

		//Add EXIF data on the image 
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
		
		Log.d("CapturePhoto","widthTab: "+photoW);
		Log.d("CapturePhoto","heightTab: "+photoW);

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		Matrix mtx = new Matrix();
		//set rotation based on width capture
		if (photoW > 1600) 
			mtx.postRotate(0);//for asus zenfone 5
		else
			mtx.postRotate(90); //for tab
		
		// Rotating Bitmap
		Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

		if (rotatedBMP != bitmap)
			bitmap.recycle();

		mImageView.setImageBitmap(rotatedBMP);

		mEditText =  (EditText) findViewById(R.id.editText_Photo);
		mSavePhoto = (Button) findViewById(R.id.save_Photo);

		//condition to appear textfield and button save photo
		if (captureExist){
			//mEditText.setVisibility(View.VISIBLE);
			//mEditText.getText();
			//mSavePhoto.setVisibility(View.VISIBLE);
			//mAddDescImage.setEnabled(true);
			mEditText.setEnabled(true);
			mEditTextAddr.setEnabled(true);
		}
		try {
			//sendPhoto(rotatedBMP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rotatedBMP;
	}

	public void addDescriptionImage(){
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_custom_image_desc);
		dialog.setTitle("Image Description");

		Log.d(TAG,"addDescriptionImage()");

		// set the custom dialog components - text, image and button
		mSaveImageDesc = (ImageButton) dialog.findViewById(R.id.save_ImageDesc);
		mImageDesc = (EditText) dialog.findViewById(R.id.editText_Photo);

		//mSaveImageDesc.setOnClickListener(this);

		mSaveImageDesc.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ***Do what you want with the click here***
				Log.d(TAG,"SAVE addDescriptionImage");

				//require mandatory
				if (mImageDesc != null){
					mEditText.setText(mImageDesc.getText().toString());
					//mAddAddressImage.setEnabled(true); 
					dialog.dismiss();
				}

			}
		});


		dialog.show();
	}

	public void addAddressImage(){
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_custom_image_address);
		dialog.setTitle("Image Address");

		Log.d(TAG,"addAddressImage()");

		// set the custom dialog components - text, image and button
		mSaveImageAddress = (ImageButton) dialog.findViewById(R.id.save_ImageAddress);
		
		//set linear
		linearLayoutQuestion3 = (LinearLayout) dialog.findViewById(R.id.linearLayoutQuestion3);
		linearLayoutQuestion5 = (LinearLayout) dialog.findViewById(R.id.linearLayoutQuestion5);
		
		// set label
		titleAddress_I1 = (TextView) dialog.findViewById(R.id.titleAddress_I1);
		titleAddress_I3 = (TextView) dialog.findViewById(R.id.titleAddress_I3);
		titleAddress_I5 = (TextView) dialog.findViewById(R.id.titleAddress_I5);

		titleAddress_I1.setText("Please include \"No\" or \"Lot\" as part of the answer (i.e: If the house" +
				"\nnumber is 10, then type \"No.10\" or if the house number is lot 23A, then type \"Lot 23A\")");

		titleAddress_I3.setText("Do not include \"Jalan/Lorong\" as part of the answer (i.e: If \"Jalan Asam\" or \"Lorong Limau\", then only type \"Asam\" or \"Limau\")");

		titleAddress_I5.setText("Do not include \"Taman\", \"Kampung\", or \"Felda\" as part of the answer (i.e: If \"Taman Segar\", then only type \"Segar\", If \"Kampung Lubok Batu\", then only type \"Lubok Batu\", or If \"Felda Medoi\", then only type " +
				"\n\"Medoi\")");

		// set textfield
		answAddress_1 = (EditText) dialog.findViewById(R.id.answAddress_1);
		answAddress_3 = (EditText) dialog.findViewById(R.id.answAddress_3);
		answAddress_5 = (EditText) dialog.findViewById(R.id.answAddress_5);
		answAddress_6 = (EditText) dialog.findViewById(R.id.answAddress_6);

		//set value of radio Button
		radTypeStreet = (RadioGroup) dialog.findViewById(R.id.answAddress_2);
		radBtnJln = (RadioButton) dialog.findViewById(R.id.radio2_1);
		radBtnLrg = (RadioButton) dialog.findViewById(R.id.radio2_2);
		radBtnNotAppStreet = (RadioButton) dialog.findViewById(R.id.radio2_3);

		radTypeArea = (RadioGroup) dialog.findViewById(R.id.answAddress_4);
		radBtnTmn = (RadioButton) dialog.findViewById(R.id.radio4_1);
		radBtnKg = (RadioButton) dialog.findViewById(R.id.radio4_2);
		radBtnFelda = (RadioButton) dialog.findViewById(R.id.radio4_3);
		radBtnNotAppArea = (RadioButton) dialog.findViewById(R.id.radio4_4);

		radTypeMukim = (RadioGroup) dialog.findViewById(R.id.answAddress_7);
		radBtnBekok = (RadioButton) dialog.findViewById(R.id.radio7_1);
		radBtnChaah = (RadioButton) dialog.findViewById(R.id.radio7_2);
		radBtnGemereh = (RadioButton) dialog.findViewById(R.id.radio7_3);
		radBtnSgSgmt = (RadioButton) dialog.findViewById(R.id.radio7_4);
		radBtnJabi = (RadioButton) dialog.findViewById(R.id.radio7_5);

		radTypeStreet.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radio2_1:
					typeStreetValue = "1"; //set decision value
					typeStreetValueDesc = "Jalan";
					linearLayoutQuestion3.setVisibility(LinearLayout.VISIBLE);
					Log.d("CommonRadioValue","1");
					break;

				case R.id.radio2_2:
					// do operations specific to this selection
					typeStreetValue = "2"; //set decision value
					typeStreetValueDesc = "Lorong";
					linearLayoutQuestion3.setVisibility(LinearLayout.VISIBLE);
					Log.d("CommonRadioValue","2");
					break;
				case R.id.radio2_3:
					typeStreetValue = "3"; //set decision value
					typeStreetValueDesc = null;
					linearLayoutQuestion3.setVisibility(LinearLayout.GONE);
					Log.d("CommonRadioValue","3");
					break;
				}
			}

		});

		radTypeArea.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radio4_1:
					typeAreaValue = "1"; //set decision value
					typeAreaValueDesc = "Taman";
					linearLayoutQuestion5.setVisibility(LinearLayout.VISIBLE);
					Log.d("CommonRadioValue","1");
					break;

				case R.id.radio4_2:
					// do operations specific to this selection
					typeAreaValue = "2"; //set decision value
					typeAreaValueDesc = "Kampung";
					linearLayoutQuestion5.setVisibility(LinearLayout.VISIBLE);
					Log.d("CommonRadioValue","2");
					break;
				case R.id.radio4_3:
					typeAreaValue = "3"; //set decision value
					typeAreaValueDesc = "Felda";
					linearLayoutQuestion5.setVisibility(LinearLayout.VISIBLE);
					Log.d("CommonRadioValue","3");
					break;
				case R.id.radio4_4:
					typeAreaValue = "4"; //set decision value
					typeAreaValueDesc = null;
					linearLayoutQuestion5.setVisibility(LinearLayout.GONE);
					Log.d("CommonRadioValue","4");
					break;
				}
			}

		});

		radTypeMukim.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radio7_1:
					typeMukimValue = "1"; //set decision value
					typeMukimValueDesc = "Bekok"; 
					Log.d("CommonRadioValue","1");
					break;

				case R.id.radio7_2:
					// do operations specific to this selection
					typeMukimValue = "2"; //set decision value
					typeMukimValueDesc = "Chaah"; 
					Log.d("CommonRadioValue","2");
					break;
				case R.id.radio7_3:
					typeMukimValue = "3"; //set decision value
					typeMukimValueDesc = "Gemereh"; 
					Log.d("CommonRadioValue","3");
					break;
				case R.id.radio7_4:
					typeMukimValue = "4"; //set decision value
					typeMukimValueDesc = "Sg Segamat"; 
					Log.d("CommonRadioValue","4");
					break;
				case R.id.radio7_5:
					typeMukimValue = "5"; //set decision value
					typeMukimValueDesc = "Jabi";
					Log.d("CommonRadioValue","5");
					break;
				}
			}

		});

		mSaveImageAddress.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {

				//new Address(typeStreetValue);
				///Address address = new Address(typeStreetValue);
				//address.getTypeStreetValue();

				//test
				boolean valid = answAddress_1 !=null && typeStreetValue!=null && typeAreaValue !=null && answAddress_6 !=null && typeMukimValue !=null;

				if(valid){

					//					fullAddress = answAddress_1.getText().toString() + "," + typeStreetValueDesc + " "+answAddress_3.getText().toString()+","
					//							+"\n"+typeAreaValueDesc +" "+answAddress_5.getText().toString() +"," + answAddress_6.getText().toString() +","
					//							+"\n"+typeMukimValueDesc;

					fullAddress = "";
					String [] addressSplit = {answAddress_1.getText().toString(), typeStreetValueDesc, answAddress_3.getText().toString(), typeAreaValueDesc, answAddress_5.getText().toString(), answAddress_6.getText().toString(), typeMukimValueDesc}; 
					for( String address : addressSplit ) {
						if(address!=null )
							fullAddress+=address+" ";
					}

					Log.d( TAG, "fullAddresssCombine: "+ fullAddress);
					mEditTextAddr.setText(fullAddress);
					mSavePhoto.setEnabled(true); 

					Log.d(TAG,"STEET TYPE VALUE: "+typeStreetValue);
					Log.d(TAG,"AREA TYPE VALUE: "+typeAreaValue);
					Log.d(TAG,"MUKIM TYPE VALUE: "+typeMukimValue);
					Log.d(TAG,"SAVE addAddressImage");
					dialog.dismiss();

				}
			}
		});


		dialog.show();
	}

	public String AddressCombine(){
		String fullAddress = null;


		return fullAddress;

	}
}
