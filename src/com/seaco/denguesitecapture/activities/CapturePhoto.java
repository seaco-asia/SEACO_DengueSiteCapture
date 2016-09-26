package com.seaco.denguesitecapture.activities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.seaco.denguesitecapture.model.MySQLiteHelper;
import com.seaco.denguesitecapture.model.SitePhotos;

public class CapturePhoto extends Activity implements OnClickListener {
	private Button mTakePhoto, mSavePhoto;
	private ImageView mImageView;
	private static final String TAG = "upload";
	private static final String TAG_PROGRESS = "progress upload";
	private MySQLiteHelper db;
	private String siteChoice, reportChoice;
	private EditText mEditText, mImageDesc, answAddress_1, answAddress_2, mEditTextAddr, mEditPatientName;
	GPSTracker gps;
	//private ImageButton mSaveImageDesc, mSaveImageAddress;
	private Button mSaveImageDesc, mSaveImageAddress;
	TextView titleAddress_B1, titleAddress_B1_1, titleAddress_B1_2, titleAddress_I1, mlblPatientName;
	RadioGroup radTypeStreet, radTypeArea, radTypeMukim, radTypeLocation;
	RadioButton radBtnJln, radBtnLrg, radBtnNotAppStreet, radBtnTmn, radBtnKg, radBtnFelda, radBtnNotAppArea,
	radBtnBekok, radBtnChaah, radBtnGemereh, radBtnSgSgmt, radBtnJabi, radBtnJementah,radBtnSermin,radBtnBulohKasap,
	radBtnGemas,radBtnPagoh,radBtnLabis,radBtnAddress, radBtnLandmark, radBtnPole, radBtnNotAppLocation;
	LinearLayout linearLayoutQuestion3, linearLayoutQuestion5, linearLayoutQuestion1, linearLayoutQuestion1_1, linearLayoutQuestion1_2;
	Spinner spinnerLocality;

	ProgressDialog progressDialog;
	long imageSize = 0; // kb
	public ProgressListener listener;
	Boolean captureExist = false;
	Intent intentObject = getIntent();
	String userID, userRegtype, userEmail, userName, userPhoneNo, typeStreetValue, typeAreaValue, typeMukimValue, typeLocationValue, typeLocationValueDesc, houseBatu, typeStreetValueDesc, typeAreaValueDesc, typeMukimValueDesc,
	fullAddress, patientName, selected, selectedDesc, languageType, radModeType, imageFileName;

	Context context = this;
	private String JSON_STRING;

	Locale myLocale;
	//use SharedPreferences to store and retrieve languageType parameter
	SharedPreferences sharedpreferences;
	public static final String mypreference = "mypref";
	public static final String languageTypePref = "languageTypePrefKey";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		//get Intent
		Intent inte = getIntent();
		reportChoice = inte.getStringExtra("reportChoice"); 
		siteChoice = inte.getStringExtra("siteChoice"); //We now know the type of site selected by the user
		userPhoneNo = inte.getStringExtra("userPhoneNo"); 
		userEmail = inte.getStringExtra("userEmail"); 
		userRegtype = inte.getStringExtra("userRegtype"); 
		userName = inte.getStringExtra("userName"); 
		userID = inte.getStringExtra("userID"); 
		radModeType = inte.getStringExtra("radModeType");

		Log.d(TAG, "idCommunity["+userID+"]");

		//use SharedPreferences to store and retrieve languageType parameter
		sharedpreferences = getSharedPreferences(mypreference,Context.MODE_PRIVATE);

		if (sharedpreferences.contains(languageTypePref)) {
			languageType = sharedpreferences.getString(languageTypePref, "");
		}else{
			languageType = "en";
		}

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

		mlblPatientName =  (TextView) findViewById(R.id.txtPatientName);
		mEditPatientName =  (EditText) findViewById(R.id.editTxtPatientName);

		//only officer can key in the patient name
		if (userRegtype.equalsIgnoreCase("AO") && reportChoice.equalsIgnoreCase("2") ) {

			mlblPatientName.setVisibility(View.VISIBLE);
			mEditPatientName.setVisibility(View.VISIBLE);

		}else{

			mlblPatientName.setVisibility(View.GONE);
			mEditPatientName.setVisibility(View.GONE);
		}

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
		case R.id.save_Photo:
			Bitmap photo = getPic();
			try {
				//checking if user select online mode on, proceed with insert data into live DB OTHERWISE insert into local DB
				if (radModeType.equalsIgnoreCase("Y")){
					//send to online db
					sendPhoto(photo);
				}else{
					//send to offline db
					sendPhotoLocal();
				}
				//end checking 
				setLocale(languageType);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
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


	//send photo if offline
	public void sendPhotoLocal(){

		Log.d(TAG,"Photo path 1:["+mCurrentPhotoPath+"]");
		Log.d(TAG,"TempPhoto Name 1:["+imageFileName+"]");

		//set progress dialog
		final ProgressDialog progressDialog = new ProgressDialog(CapturePhoto.this,R.style.AppBaseTheme);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Saving...");
		progressDialog.show();

		new android.os.Handler().postDelayed(
				new Runnable() {
					public void run() {
						//send to local db
						sendPhotoInLocalDevice(progressDialog);
					}
					//}
				}, 3000);
	}

	//send photo if online
	private void sendPhoto(Bitmap bitmap) throws Exception {
		new UploadTask().execute(bitmap);
	}

	//save into online db
	private class UploadTask extends AsyncTask<Bitmap, Integer, Boolean> implements ProgressListener, DialogInterface.OnCancelListener {

		private ProgressDialog mProgressDialog;
		protected Context mContext;

		/*public UploadTask(Context context){
			mContext = context;
		}*/

		protected void onPreExecute(){
			mProgressDialog = new ProgressDialog(CapturePhoto.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setTitle(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_UPLOAD:Constants.en.CAPTURE_UPLOAD);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setMessage(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_UPLOADING:Constants.en.CAPTURE_UPLOADING);
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
				Log.d("latitude","latitude ["+latitude+"] and longitude ["+longitude+"]");
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


				StringBody lat = null, lon = null, report = null, site = null, im = null, insertBy = null;
				StringBody photoDesc = null;
				StringBody localityName = null, localityOther = null, localityIndType = null, localityIndName = null, 
						typeMukimValues = null, houseFullAddress = null, patientName = null, casePatientName = null;
				Log.d("address 1",":"+selected);
				Log.d("address 2",":"+answAddress_1.getText().toString());
				Log.d("address 3",":"+typeLocationValue);
				Log.d("address 4",":"+answAddress_2.getText().toString());
				Log.d("address 5",":"+typeMukimValue);
				Log.d("address 6",":"+mEditTextAddr.getText().toString());
				Log.d("address 7",":"+mEditPatientName.getText().toString());
				try{
					lat = new StringBody(String.valueOf(latitude));
					lon = new StringBody(String.valueOf(longitude));
					report = new StringBody(reportChoice);
					site = new StringBody(siteChoice);
					im = new StringBody(mngr.getDeviceId());
					photoDesc = new StringBody(mEditText.getText().toString());
					insertBy = new StringBody(userID);
					localityName = new StringBody(selected);
					localityOther = new StringBody(answAddress_1.getText().toString().toUpperCase());
					localityIndType = new StringBody(typeLocationValue);
					localityIndName = new StringBody(answAddress_2.getText().toString().toUpperCase());
					typeMukimValues = new StringBody(typeMukimValue);
					houseFullAddress = new StringBody(mEditTextAddr.getText().toString().toUpperCase());
					patientName = new StringBody(mEditPatientName.getText().toString().toUpperCase());
				}catch(Exception el){
					Log.i("error_stringbody",lat.toString() + lon.toString() + site.toString() + im.toString() + photoDesc.toString() + insertBy +
							localityName.toString() + localityOther.toString() + localityIndType + localityIndName.toString() + typeMukimValues);
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
				customEntity.addPart("reportChoice", report);
				customEntity.addPart("siteChoice", site);
				customEntity.addPart("imei", im);	
				customEntity.addPart("photoDesc",photoDesc);
				customEntity.addPart("insertBy",insertBy);
				customEntity.addPart("localityName",localityName);
				customEntity.addPart("localityOther",localityOther);
				customEntity.addPart("localityIndType",localityIndType);
				customEntity.addPart("localityIndName",localityIndName);
				customEntity.addPart("typeMukimValues",typeMukimValues);
				customEntity.addPart("houseFullAddress",houseFullAddress);
				customEntity.addPart("casePatientName",patientName);
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
				Toast.makeText(CapturePhoto.this, languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_UPLOADED:Constants.en.CAPTURE_UPLOADED, Toast.LENGTH_LONG).show();
				//test
				//				Intent i = new Intent(CapturePhoto.this, MainActivity.class);
				//				i.putExtra("userPhoneNo", userPhoneNo);
				//				i.putExtra("userEmail", userEmail);
				//				i.putExtra("userRegtype", userRegtype);
				//				i.putExtra("userName", userName);
				//				i.putExtra("userID", userID);
				//				i.putExtra("languageType", languageType);
				//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//				startActivity(i);
				finish();
			}else{
				// UPLOADING DATA FAILED
				mProgressDialog.setMessage(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_UPLOAD_FAIL:Constants.en.CAPTURE_UPLOAD_FAIL);
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
	//end save into online db

	//save into offline db
	public void sendPhotoInLocalDevice(ProgressDialog progressDialog) {

		Log.d(TAG,"Photo path 2:["+mCurrentPhotoPath+"]");
		Log.d(TAG,"TempPhoto Name 2:["+imageFileName+"]");

		/*photoDesc*/
		mEditText =  (EditText) findViewById(R.id.editText_Photo);

		/* Needs cleaning up -- reuse code */
		double latitude = 0.0;
		double longitude = 0.0;

		/* Read the latest longitude and latitude data */
		if(gps.canGetLocation()){
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			Log.d("latitude","latitude ["+latitude+"] and longitude ["+longitude+"]");
		}else{
			Log.d(TAG,"latitude else ["+latitude+"] and longitude ["+longitude+"]");
			gps.showSettingsAlert();
		}

		/* Device ID */
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 

		/* save data to local DB */
		String fileMillis = null, tmpFilename =null, lat = null, lon = null, report = null, site = null, currentDate = null, im = null, insertBy = null, photoDesc = null, 
				localityName = null, localityOther = null, localityIndType = null, localityIndName = null, typeMukimValues = null, 
				houseFullAddress = null, isDuplicate = "N", flagUpload = "N", tmpPath = null;

		lat = String.valueOf(latitude);
		lon = String.valueOf(longitude);
		report = reportChoice;
		site = siteChoice;
		im = mngr.getDeviceId();
		currentDate = getDateTime();
		photoDesc = mEditText.getText().toString();
		insertBy = userID;
		localityName = selected;
		localityOther = answAddress_1.getText().toString().toUpperCase();
		localityIndType = typeLocationValue;
		localityIndName = answAddress_2.getText().toString().toUpperCase();
		typeMukimValues = typeMukimValue;
		houseFullAddress = mEditTextAddr.getText().toString().toUpperCase();
		fileMillis = System.currentTimeMillis() + ".jpg";
		tmpFilename = imageFileName + ".jpg";
		tmpPath = mCurrentPhotoPath;

		db.addPicture(new SitePhotos(fileMillis, tmpFilename, lat, lon, site, report, im, currentDate, photoDesc, insertBy, localityName, typeMukimValues, houseFullAddress, localityOther, localityIndType,  localityIndName, isDuplicate, flagUpload, tmpPath, patientName));
		db.getSitePhotos(insertBy);
		progressDialog.dismiss();

		//get data from local DB - temporary
		//List<SitePhotos> singleSitePhotos= db.getSitePhotos(insertBy);
		/*Log.d(TAG,"Filename:["+photos.indexOf(arg0)+"]");
		Log.d(TAG,"Latitude:["+singleSitePhotos.getLatitude()+"]");
		Log.d(TAG,"Longitude:["+singleSitePhotos.getLongitude()+"]");
		Log.d(TAG,"SiteChoice:["+singleSitePhotos.getSiteChoice()+"]");
		Log.d(TAG,"ReportChoice:["+singleSitePhotos.getReportChoice()+"]");
		Log.d(TAG,"Imei:["+singleSitePhotos.getImei()+"]");
		Log.d(TAG,"InsertBy:["+singleSitePhotos.getInsertBy()+"]");
		Log.d(TAG,"Locality_name:["+singleSitePhotos.getLocality_name()+"]");
		Log.d(TAG,"House_mukim:["+singleSitePhotos.getHouse_mukim()+"]");
		Log.d(TAG,"House_fullAddress:["+singleSitePhotos.getHouse_fullAddress()+"]");
		Log.d(TAG,"Locality_othe:["+singleSitePhotos.getLocality_other()+"]");
		Log.d(TAG,"Locality_ind_type:["+singleSitePhotos.getLocality_ind_type()+"]");
		Log.d(TAG,"Locality_ind_name:["+singleSitePhotos.getLocality_ind_name()+"]");
		Log.d(TAG,"IsDuplicate:["+singleSitePhotos.getIsDuplicate()+"]");
		Log.d(TAG,"FlagUpload:["+singleSitePhotos.getFlagUpload()+"]");*/
		finish();

	}
	//save into offline db


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setLocale(languageType);
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
		imageFileName = "JPEG_" + timeStamp + "_";

		String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
		String storageDirSub = Environment.getExternalStorageDirectory() + "/picupload/" +userID;

		File dir = new File(storageDir);
		if (!dir.exists())
			dir.mkdir();

		File dir2 = new File(storageDirSub);
		if (!dir2.exists()){
			dir2.mkdir();
		}

		File image = new File(storageDirSub + "/" + imageFileName + ".jpg");

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
		
		Log.d(TAG,"IMAGEVIEW WIDTH ["+targetW+"] AND IMAGEVIEW HEIGHT ["+targetH+"]");

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
		//mSaveImageDesc = (ImageButton) dialog.findViewById(R.id.save_ImageDesc);
		mSaveImageDesc = (Button) dialog.findViewById(R.id.save_ImageDesc);
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
		dialog.setContentView(R.layout.dialog_custom_image_info);
		dialog.setTitle("Image Address");

		Log.d(TAG,"addAddressImage()");

		// set the custom dialog components - text, image and button
		mSaveImageAddress = (Button) dialog.findViewById(R.id.save_ImageAddress);

		//set linear
		linearLayoutQuestion1 = (LinearLayout) dialog.findViewById(R.id.linearLayoutQuestion1);

		// set label
		titleAddress_B1 = (TextView) dialog.findViewById(R.id.titleAddress_B1);
		titleAddress_I1 = (TextView) dialog.findViewById(R.id.titleAddress_I1);
		titleAddress_I1.setText("");

		/*titleAddress_I3.setText("Do not include \"Jalan/Lorong\" as part of the answer (i.e: If \"Jalan Asam\" or \"Lorong Limau\", then only type \"Asam\" or \"Limau\")");

		titleAddress_I5.setText("Do not include \"Taman\", \"Kampung\", or \"Felda\" as part of the answer (i.e: If \"Taman Segar\", then only type \"Segar\", If \"Kampung Lubok Batu\", then only type \"Lubok Batu\", or If \"Felda Medoi\", then only type " +
				"\n\"Medoi\")");*/

		// set textfield
		answAddress_1 = (EditText) dialog.findViewById(R.id.answAddress_1);
		answAddress_2 = (EditText) dialog.findViewById(R.id.answAddress_2);

		//visibility
		answAddress_1.setVisibility(View.GONE);
		answAddress_2.setVisibility(View.GONE);

		//set spinner
		spinnerLocality = (Spinner) dialog.findViewById(R.id.answAddress_spinner);

		//set value of radio Button
		radTypeLocation = (RadioGroup) dialog.findViewById(R.id.answAddress_2_radio);
		radBtnLandmark = (RadioButton) dialog.findViewById(R.id.radio2_1);
		radBtnPole = (RadioButton) dialog.findViewById(R.id.radio2_2);
		radBtnNotAppLocation = (RadioButton) dialog.findViewById(R.id.radio2_3);

		radTypeMukim = (RadioGroup) dialog.findViewById(R.id.answAddress_7);
		radBtnBekok = (RadioButton) dialog.findViewById(R.id.radio7_1);
		radBtnChaah = (RadioButton) dialog.findViewById(R.id.radio7_2);
		radBtnGemereh = (RadioButton) dialog.findViewById(R.id.radio7_3);
		radBtnSgSgmt = (RadioButton) dialog.findViewById(R.id.radio7_4);
		radBtnJabi = (RadioButton) dialog.findViewById(R.id.radio7_5);

		radBtnJementah = (RadioButton) dialog.findViewById(R.id.radio7_6);
		radBtnSermin = (RadioButton) dialog.findViewById(R.id.radio7_7);
		radBtnBulohKasap = (RadioButton) dialog.findViewById(R.id.radio7_8);
		radBtnGemas = (RadioButton) dialog.findViewById(R.id.radio7_9);
		radBtnPagoh = (RadioButton) dialog.findViewById(R.id.radio7_10);
		radBtnLabis = (RadioButton) dialog.findViewById(R.id.radio7_11);

		OnItemSelectedListener os = new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
				selected = parent.getItemAtPosition(position).toString();

				Log.d(TAG,"DropdownLocality: "+selected);

				if (selected.equalsIgnoreCase("Other")){
					answAddress_1.setVisibility(View.VISIBLE);
					selectedDesc = null;
				}else{
					answAddress_1.setVisibility(View.GONE);
					selectedDesc = selected;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0){

			}

		};

		//Set listener for an item select
		spinnerLocality.setOnItemSelectedListener(os);

		//load locality
		//getJSON();
		loadLocality();

		radTypeLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radio2_1:

					typeLocationValue = "1"; //set decision value Address
					answAddress_2.setVisibility(View.VISIBLE);
					titleAddress_I1.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_LANDMARK:Constants.en.CAPTURE_ADDR_LANDMARK);
					typeLocationValueDesc =null;
					Log.d("radTypeLocation","1");

					break;
				case R.id.radio2_2:

					typeLocationValue = "2"; //set decision value Address
					answAddress_2.setVisibility(View.VISIBLE);
					titleAddress_I1.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_POLE:Constants.en.CAPTURE_ADDR_POLE);
					typeLocationValueDesc =null;
					Log.d("radTypeLocation","2");

					break;
				case R.id.radio2_3:

					typeLocationValue = "3"; //set decision value Address
					answAddress_2.setVisibility(View.GONE);
					titleAddress_I1.setText("");
					typeLocationValueDesc =null;
					Log.d("radTypeLocation","3");

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
				case R.id.radio7_6:
					typeMukimValue = "6"; //set decision value
					typeMukimValueDesc = "Jementah";
					Log.d("CommonRadioValue","6");
					break;
				case R.id.radio7_7:
					typeMukimValue = "7"; //set decision value
					typeMukimValueDesc = "Sermin";
					Log.d("CommonRadioValue","7");
					break;
				case R.id.radio7_8:
					typeMukimValue = "8"; //set decision value
					typeMukimValueDesc = "Buloh Kasap";
					Log.d("CommonRadioValue","8");
					break;
				case R.id.radio7_9:
					typeMukimValue = "9"; //set decision value
					typeMukimValueDesc = "Gemas";
					Log.d("CommonRadioValue","9");
					break;
				case R.id.radio7_10:
					typeMukimValue = "10"; //set decision value
					typeMukimValueDesc = "Pagoh";
					Log.d("CommonRadioValue","10");
					break;
				case R.id.radio7_11:
					typeMukimValue = "11"; //set decision value
					typeMukimValueDesc = "Labis";
					Log.d("CommonRadioValue","11");
					break;
				}

			}

		});



		mSaveImageAddress.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(validateSaveAddress()){

					fullAddress = "";
					//String [] addressSplit = {answAddress_1.getText().toString(), typeStreetValueDesc, answAddress_3.getText().toString(), typeAreaValueDesc, answAddress_5.getText().toString(), answAddress_6.getText().toString(), typeMukimValueDesc}; 
					String [] addressSplit = {selectedDesc, answAddress_1.getText().toString(), typeLocationValueDesc, answAddress_2.getText().toString(), typeMukimValueDesc}; 
					for( String address : addressSplit ) {
						if(address!=null )
							fullAddress+=address+" ";
					}

					Log.d( TAG, "fullAddresssCombine: "+ fullAddress);
					mEditTextAddr.setText(fullAddress);
					mSavePhoto.setEnabled(true); 
					Log.d(TAG,"SAVE addAddressImage");
					dialog.dismiss();

				}
			}
		});


		dialog.show();
	}

	public boolean validateSaveAddress(){

		boolean valid = true;

		//answer for Q1
		if (selected.equalsIgnoreCase("Other") && answAddress_1.getText().toString().isEmpty()){
			answAddress_1.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH:Constants.en.CAPTURE_ADDR_AUTH);
			valid = false;
		}else{
			answAddress_1.setError(null);
		}

		//answer for Q2
		if(radTypeLocation.getCheckedRadioButtonId()<=0){
			radBtnLandmark.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_LANDMARK:Constants.en.CAPTURE_ADDR_AUTH_LANDMARK);
			radBtnNotAppLocation.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_LANDMARK:Constants.en.CAPTURE_ADDR_AUTH_LANDMARK);
			radBtnNotAppLocation.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_LANDMARK:Constants.en.CAPTURE_ADDR_AUTH_LANDMARK);
			valid = false;
		}

		if(radBtnLandmark.isChecked() && answAddress_2.getText().toString().isEmpty() || radBtnPole.isChecked() && answAddress_2.getText().toString().isEmpty()){  
			answAddress_2.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH:Constants.en.CAPTURE_ADDR_AUTH);
			valid = false;
		}else{
			answAddress_2.setError(null);
		}

		//answer for Q7
		if(radTypeMukim.getCheckedRadioButtonId()<=0){
			//Toast.makeText(getApplicationContext(), "Please select Mukim", Toast.LENGTH_SHORT).show();
			radBtnBekok.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_MUKIM:Constants.en.CAPTURE_ADDR_AUTH_MUKIM);
			radBtnChaah.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_MUKIM:Constants.en.CAPTURE_ADDR_AUTH_MUKIM);
			radBtnGemereh.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_MUKIM:Constants.en.CAPTURE_ADDR_AUTH_MUKIM);
			radBtnSgSgmt.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_MUKIM:Constants.en.CAPTURE_ADDR_AUTH_MUKIM);
			radBtnJabi.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_ADDR_AUTH_MUKIM:Constants.en.CAPTURE_ADDR_AUTH_MUKIM);
			valid = false;
		}

		return valid;

	}

	//load Locality
	public void loadLocality(){

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(getAssets().open("locality.txt"), "UTF-8")); 

			List<String> list = new ArrayList<String>();

			// do reading, usually loop until end of file reading 
			String mLine;
			while ((mLine = reader.readLine()) != null) {
				//process line
				//. ...
				list.add(mLine);
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
						R.layout.spinner_locality_item, list);
				dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_locality_item);
				spinnerLocality.setAdapter(dataAdapter);
				Log.d("Capture Photo","Locality: ["+mLine+"]");
			}
		} catch (IOException e) {
			//log the exception
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					//log the exception
				}
			}
		}


	}	

	//localtization to setup language
	public void setLocale(String lang) {

		Log.d(TAG,"TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT["+lang+"]");
		myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

	}
	
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}

