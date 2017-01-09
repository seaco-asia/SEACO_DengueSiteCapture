/*package com.seaco.denguesitecapture.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;

public class UploadOffline extends Activity implements OnClickListener {

	private static final String TAG = "UploadOffline";
	Button btnUploadOffline;
	private MySQLiteHelper db;
	public String JSON_STRING;
	Locale myLocale;
	//Integer selectedLanguage;
	String languageType, radModeType, reportChoice, siteChoice, userPhoneNo, userEmail, userRegtype, userName, userID,
	filenameOffline, mCurrentPhotoPath;
	long imageSize = 0; // kb
	GPSTracker gps;

	//use SharedPreferences to store and retrieve languageType parameter
	SharedPreferences sharedpreferences;
	public static final String mypreference = "mypref";
	public static final String languageTypePref = "languageTypePrefKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_offline);

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

		//label
		//lblSelectLanguage = (TextView)findViewById(R.id.sel_language);

		//use SharedPreferences to store and retrieve languageType parameter
		//sharedpreferences = getSharedPreferences(mypreference,
		//Context.MODE_PRIVATE);

		//languageType = "en"; //set language value
		//Log.d(TAG,"languageTypePref ["+languageType+"]");

		//radModeType ="Y";//set onlineMode value

		//button signup, login
		btnUploadOffline = (Button) findViewById(R.id.btn_UploadOffline);

		btnUploadOffline.setOnClickListener(this);

		// temporary using before implement server database
		  
//			db = new MySQLiteHelper(this);
//			final String[][] users = db.getUserResult();
//			Log.d(TAG,"retrieve data users......"+users.length);
		
		 

		//use for offline.
		db = new MySQLiteHelper(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btn_UploadOffline:
			try {
				uploadPhotoOffline();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//login();
			Log.d(TAG,"finish upload");
			//isOnline();
			break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//Inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	// Start Adding photo if offline - Test insert picture
	 

	//Step 4
	public void uploadPhotoOffline() throws Exception{

		final ProgressDialog progressDialog = new ProgressDialog(UploadOffline.this,R.style.AppBaseTheme);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Saving...");
		progressDialog.show();

		String[][] photos = db.getSitePhotosNotUpload(userID);

		int i = 0;

		Log.d(TAG,"UPLOAD PHOTO OFFLINE"+photos.length);
		while(i < photos.length){

			Log.d(TAG,"HISTORY ID["+photos[i][0]+"]");
			Log.d(TAG,"HISTORY ID["+photos[i][17]+"]");

			filenameOffline = photos[i][1];
			mCurrentPhotoPath = photos[i][17];

			//Bitmap photo = getPic(mCurrentPhotoPath); //will be uncomment soon finish
			//sendPhoto(photo);

			//db.updatePicture(photos[i][0]); //will be uncomment soon finish
			//addPhotoOffline(filenameOffline, progressDialog);


			Log.d(TAG,"Filename["+photos[i][1]+"]");
			Log.d(TAG,"Filename Temp["+photos[i][2]+"]");
			Log.d(TAG,"Latitude["+photos[i][3]+"]");
			Log.d(TAG,"Longitude["+photos[i][4]+"]");
			Log.d(TAG,"SiteChoice["+photos[i][5]+"]");
			Log.d(TAG,"ReportChoice["+photos[i][6]+"]");
			Log.d(TAG,"Imei["+photos[i][7]+"]");
			Log.d(TAG,"InsertBy["+photos[i][8]+"]");
			Log.d(TAG,"Locality_name["+photos[i][9]+"]");
			Log.d(TAG,"House_mukim["+photos[i][10]+"]");
			Log.d(TAG,"House_fullAddress["+photos[i][11]+"]");
			Log.d(TAG,"Locality_othe["+photos[i][12]+"]");
			Log.d(TAG,"Locality_ind_type["+photos[i][13]+"]");
			Log.d(TAG,"Locality_ind_name["+photos[i][14]+"]");
			Log.d(TAG,"IsDuplicate["+photos[i][15]+"]");
			Log.d(TAG,"FlagUpload["+photos[i][16]+"]");
			Log.d(TAG,"Path["+photos[i][17]+"]");


			i++;
		}
	}

	//Step 3
	private class UploadTask extends AsyncTask<Bitmap, Integer, Boolean> implements ProgressListener, DialogInterface.OnCancelListener {

		private ProgressDialog mProgressDialog;

		protected void onPreExecute(){
			mProgressDialog = new ProgressDialog(UploadOffline.this);
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

				StringBody lat = null, lon = null, report = null, site = null, im = null, insertBy = null;
				StringBody photoDesc = null;
				StringBody localityName = null, localityOther = null, localityIndType = null, localityIndName = null, 
						typeMukimValues = null, houseFullAddress = null, patientName = null;
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

				 //Set a server side php script location 
				HttpPost httppost = new HttpPost(Config.URL_UPLOAD_OFFLINE_TEST);

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
				 //temporary using before implement server database
				 
				String photoDesc = mEditText.getText().toString();

				//combine the value of latitude and longitude
				String latitudeLongitude = String.valueOf(latitude)+","+String.valueOf(longitude);
				Log.i("customEntity","Lat: "+String.valueOf(latitude) + "Lon: "+ String.valueOf(longitude) + "siteChoice: "+ siteChoice + "imei: " + mngr.getDeviceId());

				//Insert into db
				//db.addPicture(new SitePhotos(fileMillis,latitudeLongitude,photoDesc));
				 
				 

				 //New 
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
				Toast.makeText(UploadOffline.this, "Telah dimuat naik ke server", Toast.LENGTH_LONG).show();
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
				//finish();
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

	//Step 2
	private void sendPhoto(Bitmap bitmap) throws Exception {
		new UploadTask().execute(bitmap);
	}

	//Step 1
	private Bitmap getPic(String mCurrentPhotoPath){

		//captureExist = true;

		//		//Lets try to set the exif here
		//		double latitude = 0.0;
		//		double longitude = 0.0;


		//Read the latest longitude and latitude data 
		//		if(gps.canGetLocation()){
		//			latitude = gps.getLatitude();
		//			longitude = gps.getLongitude();
		//		}else{
		//			gps.showSettingsAlert();
		//		}

		ExifInterface exif;

		//Add EXIF data on the image 
		try{

			//exif = new ExifInterface(storageDir + "/" + imageFileName + ".jpg");
			//exif = new ExifInterface(photoFile.getAbsolutePath());
			exif = new ExifInterface(mCurrentPhotoPath);


			//			int lat_1 = (int)Math.floor(latitude);
			//			int lat_2 = (int)Math.floor((latitude - lat_1) * 60);
			//			double lat_3 = (latitude - ((double)lat_1+((double)lat_2/60))) * 3600000;
			//
			//			int lon_1 = (int)Math.floor(longitude);
			//			int lon_2 = (int)Math.floor((longitude - lon_1) * 60);
			//			double lon_3 = (longitude - ((double)lon_1+((double)lon_2/60))) * 3600000;
			//
			//			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat_1+"/1,"+lat_2+"/1,"+lat_3+"/1000");
			//			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon_1+"/1,"+lon_2+"/1,"+lon_3+"/1000");


			//			if (latitude > 0) {
			//				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
			//			} else {
			//				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
			//			}
			//
			//			if (longitude > 0) {
			//				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
			//			} else {
			//				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
			//			}

			exif.saveAttributes();
		}

		catch (FileNotFoundException io) {}
		catch (IOException io) {}
		catch (NullPointerException np){}

		// Get the dimensions of the View
		int targetW = 700;//mImageView.getWidth();
		int targetH = 1000;//mImageView.getHeight();

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

		//mImageView.setImageBitmap(rotatedBMP);

		//mEditText =  (EditText) findViewById(R.id.editText_Photo);
		//mSavePhoto = (Button) findViewById(R.id.save_Photo);

		//condition to appear textfield and button save photo
		//if (captureExist){
		//mEditText.setVisibility(View.VISIBLE);
		//mEditText.getText();
		//mSavePhoto.setVisibility(View.VISIBLE);
		//mAddDescImage.setEnabled(true);
		//mEditText.setEnabled(true);
		//mEditTextAddr.setEnabled(true);
		//}
		try {
			//sendPhoto(rotatedBMP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rotatedBMP;
	}


	//Adding photo if offline - Test insert information
	private void addPhotoOffline(final String filenameOffline, final ProgressDialog progressDialog){

		class AddPhotoOffline extends AsyncTask<Void,Void,String>{

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);

				Log.d(TAG,"onPostExecute addPhotoOffline");

				//Toast.makeText(getBaseContext(), languageType.equalsIgnoreCase("ms")?Constants.ms.SIGNUP_SUCCESS:Constants.en.SIGNUP_SUCCESS, Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
				//emptyInputVal(actionType);

				//Intent i = new Intent(Signup.this, Login.class);
				//startActivity(i);
			}

			@Override
			protected String doInBackground(Void... v) {

				Log.d(TAG,"doInBackground addPhotoOffline");

				HashMap<String,String> params = new HashMap<String, String>();
				//					params.put(Config.KEY_ID,userName);
				params.put(Config.KEY_FILENAME,filenameOffline);
				//					params.put(Config.KEY_USER_PHONENO,userPhoneNo);
				//					params.put(Config.KEY_USER_PASSWORD,userPassword);
				//					params.put(Config.KEY_USER_Regtype,userRegtype);

				RequestHandler rh = new RequestHandler();
				String res = rh.sendPostRequest(Config.URL_UPLOAD_OFFLINE, params);
				return res;

			}
		}

		AddPhotoOffline addPhotoOffline = new AddPhotoOffline();
		addPhotoOffline.execute();
	}
	
	
	  //end Adding photo if offline - Test insert picture
	 
}*/
