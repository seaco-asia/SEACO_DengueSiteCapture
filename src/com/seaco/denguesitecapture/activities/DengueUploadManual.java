package com.seaco.denguesitecapture.activities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;



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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;
import com.seaco.denguesitecapture.model.SitePhotos;

public class DengueUploadManual extends Activity implements OnClickListener {

	private static final String TAG = "DengueUploadManual";
	public String action = "common";
	private MySQLiteHelper db;
	public String JSON_STRING, selected;
	TableLayout tableLayout;
	LinearLayout linearLayout;
	TableRow tableRow;
	Button btnUploadOffline, buttonPrev, buttonNext, buttonSearch;
	TextView textLblSearchSub, textPageNumber;
	EditText textIdhidden;
	Spinner spinnerMain, spinnerSub;
	Locale myLocale;
	//Integer selectedLanguage;
	Integer selectedMain, currentPage, j;
	String languageType, radModeType, reportChoice, siteChoice, userPhoneNo, userEmail, userRegtype, userName, userID,
	filenameOffline, subValues, subValue, histManualId, histManualLocality, histManualReportDate, histManualFlagUplad,
	histManualGps, histManualPhotoDesc, histManualStatus, histManualOffComent, histManualHouseFullAddress, histManualLocalityOther,
	histManualReportChoice, histManualPatientName;
	long imageSize = 0; // kb
	GPSTracker gps;

	//use SharedPreferences to store and retrieve languageType parameter
	SharedPreferences sharedpreferences;
	public static final String mypreference = "mypref";
	public static final String languageTypePref = "languageTypePrefKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_history_manual_title);

		tableLayout = (TableLayout) findViewById(R.id.tableLayout);

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

		//label
		textLblSearchSub = (TextView)findViewById(R.id.histManual_title_Search2);

		// set dropdown main
		spinnerMain = (Spinner)findViewById(R.id.spinnerMain);
		// set dropdown sub
		spinnerSub = (Spinner)findViewById(R.id.spinnerSub);
		//text field hideen to store Id
		textIdhidden = (EditText)findViewById(R.id.histManual_txtIdhidden);


		//list item dropdown main
		List<String> list = new ArrayList<String>();
		list.add(0,"All");
		//list.add(1,"Report Type");
		list.add(1,"Locality");
		list.add(2,"Mukim");
		list.add(3,"Month/Year");

		//adapter dropdown main
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_search_item, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_search_dropdown_item);

		//set Adapter
		spinnerMain.setAdapter(dataAdapter);

		//function item select
		spinnerMain.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				List<String> listSub = new ArrayList<String>();
				selectedMain = parent.getSelectedItemPosition();

				if(selectedMain == 0){

					listSub.add("All");

					textLblSearchSub.setText("All");
					spinnerSub.setEnabled(false);

				}else if(selectedMain == 1){

					getLocality(); 

					textLblSearchSub.setText("Locality");
					spinnerSub.setEnabled(true);

				}else if(selectedMain == 2){

					listSub.add("Bekok");
					listSub.add("Chaah");
					listSub.add("Gemereh");
					listSub.add("Sg Segamat");
					listSub.add("Jabi");
					listSub.add("Jementah");
					listSub.add("Sermin");
					listSub.add("Buloh Kasap");
					listSub.add("Gemas");
					listSub.add("Pagoh");
					listSub.add("Labis");

					textLblSearchSub.setText("Mukim");
					spinnerSub.setEnabled(true);

				}else{

					listSub.add("May/2016");
					listSub.add("June/2016");
					listSub.add("July/2016");
					listSub.add("August/2016");
					listSub.add("Sept/2016");
					listSub.add("Oct/2016");
					listSub.add("Nov/2016");
					listSub.add("Dec/2016");

					textLblSearchSub.setText("Month/Year");
					spinnerSub.setEnabled(true);

				}

				if(selectedMain != 1){
					ArrayAdapter<String> dataAdapterSub = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_search_sub_item, listSub);
					dataAdapterSub.setDropDownViewResource(R.layout.spinner_search_dropdown_sub_item);
					spinnerSub.setAdapter(dataAdapterSub);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		spinnerSub.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selected = parent.getItemAtPosition(position).toString();

				//set parameter in value type
				subValues = getParamSubValue(selected);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		//button prev, next and search
		buttonPrev = (Button) findViewById(R.id.buttonPrevious);
		buttonNext = (Button) findViewById(R.id.buttonNext);
		buttonSearch = (Button)findViewById(R.id.buttonSearch);

		buttonSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				action = "common";
				currentPage = getCurrentPage(action);
				uploadPhotoOffline(userID, selectedMain, subValues, currentPage);
				getTotalPage(selectedMain, subValues, currentPage);

			}
		});


		buttonPrev.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				action = "prev";
				currentPage = getCurrentPage(action);
				uploadPhotoOffline(userID, selectedMain, subValues, currentPage);
				getTotalPage(selectedMain, subValues, currentPage);

			}
		});


		buttonNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				action = "next";
				currentPage = getCurrentPage(action);
				uploadPhotoOffline(userID, selectedMain, subValues, currentPage);
				getTotalPage(selectedMain, subValues, currentPage);

			}
		});

		//use for offline.
		db = new MySQLiteHelper(this);

		//first time load
		currentPage = getCurrentPage(action);
		selectedMain = 0; subValues = "0";
		uploadPhotoOffline(userID, selectedMain, subValues, currentPage);
		getTotalPage(selectedMain, subValues, currentPage);
		//end first time load

		/* temporary using before implement server database
		 * 
			db = new MySQLiteHelper(this);
			final String[][] users = db.getUserResult();
			Log.d(TAG,"retrieve data users......"+users.length);
		 *
		 */
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btn_UploadOffline:
			try {
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

	//start display offline photo
	public void uploadPhotoOffline(String userID, Integer selectedMain, String subValues, Integer currentPage){

		Log.d(TAG,"userID["+ userID+"] selectedMain["+selectedMain+"] subValues ["+ subValues+"] currentPage["+currentPage+"]");

		String[][] photos = db.getSingle_SitePhotosNotUpload(userID, selectedMain, subValues, currentPage);

		int i = 0;

		//start refresh layout
		tableLayout.removeAllViews();
		tableLayout.refreshDrawableState();
		//end refresh layout

		textPageNumber = (TextView)findViewById(R.id.pageNumber);

		Log.d(TAG,"UPLOAD PHOTO OFFLINE"+photos.length);
		while(i < photos.length){

			histManualId = photos[i][0];
			filenameOffline = photos[i][2];
			histManualGps = photos[i][3] +","+photos[i][4];
			histManualReportChoice =photos[i][6];
			histManualReportDate = photos[i][8];
			histManualPhotoDesc = photos[i][9];
			histManualLocality = photos[i][11];
			histManualFlagUplad = photos[i][18];
			histManualHouseFullAddress =photos[i][13];
			histManualLocalityOther =photos[i][14];
			histManualOffComent ="-";
			final String histManualmCurrentPhotoPath = photos[i][19];
			histManualPatientName = photos[i][20];

			linearLayout = (LinearLayout) View.inflate(this,R.layout.table_history_manual_item, null);

			TextView histManual_Id  = (TextView) linearLayout.findViewById(R.id.histManual_id);
			TextView histManual_ReportDate  = (TextView) linearLayout.findViewById(R.id.histManual_reportDate);
			TextView histManual_ReportType  = (TextView) linearLayout.findViewById(R.id.histManual_reportType);
			ImageView histManual_detail  = (ImageView) linearLayout.findViewById(R.id.histManual_imageView_detail);
			Button histManual_btnUpload  = (Button) linearLayout.findViewById(R.id.histManual_btnUpload);


			histManual_Id.setText(!histManualLocality.equalsIgnoreCase("Other")?histManualLocality:histManualLocalityOther);
			histManual_ReportDate.setText(histManualReportDate);
			histManual_ReportType.setText(getReportTypeDesc(histManualReportChoice));

			//set status either has been upload into live db or not
			if (histManualFlagUplad.equalsIgnoreCase("Y")){
				//histManual_btnUpload.setEnabled(false);
				histManualStatus = "Already Uploaded";
			}else{
				//histManual_btnUpload.setEnabled(true);
				histManualStatus = "Not Uploaded";
			}
			//end set status either has been upload into live db or not
			
			//Enable or diable button if user choose Online mode = off into live db
			if (radModeType.equalsIgnoreCase("N")){
				histManual_btnUpload.setEnabled(false);
			}else{
				histManual_btnUpload.setEnabled(true);
			}
			//end Enable or diable button if user choose Online mode = off into live db

			detailnUpload(histManual_detail, histManual_btnUpload, histManualId, filenameOffline, histManualGps, histManualPhotoDesc, histManualStatus, histManualOffComent, histManualReportDate, histManualHouseFullAddress, histManualmCurrentPhotoPath, histManualPatientName);

			i++;
			tableLayout.addView(linearLayout);
		}
	}
	//end display offline photo

	//start display total page
	public void getTotalPage(Integer selectedMain, String subValues, Integer currentPage){

		int pageCount = db.getSingle_SitePhotosNotUploadCount(userID, selectedMain, subValues);

		textPageNumber.setText("Page "+(currentPage)+" of "+pageCount);

		// Disabled Button Next
		int pageCounts =pageCount;

		if(currentPage >= pageCounts){
			buttonNext.setEnabled(false);
		}else{
			buttonNext.setEnabled(true);
		}

		// Disabled Button Previous
		if(currentPage <= 1){
			buttonPrev.setEnabled(false);
		}else{
			buttonPrev.setEnabled(true);
		}
	}
	//end display total page

	//set paramater for searching
	public String getParamSubValue(String selected){
		if(selected.equalsIgnoreCase("All")){
			//set paramater All = 0, selectedMain 
			subValue = "0";
			//SQL 1
		}
		else if(selected.equalsIgnoreCase("Breeding Site")){
			//set paramater Breeding Site = 1, selectedMain 
			subValue = "1";
			//SQL 2
		}
		else if(selected.equalsIgnoreCase("Case")){
			//set paramater Breeding Site = 2, selectedMain 
			subValue = "2";
			//SQL 2
		}
		else if(selected.equalsIgnoreCase("Bekok")){
			//set parameter Bekok= 1, selectedMain 
			subValue = "1";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Chaah")){
			//set parameter Chaah= 2, selectedMain 
			subValue = "2";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Gemereh")){
			//set parameter Gemereh= 3, selectedMain 
			subValue = "3";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Sg Segamat")){
			//set parameter Sg Segamat= 4, selectedMain 
			subValue = "4";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Jabi")){
			//set parameter Jabi= 5, selectedMain 
			subValue = "5";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Jementah")){
			//set parameter Bekok= 1, selectedMain 
			subValue = "6";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Sermin")){
			//set parameter Chaah= 2, selectedMain 
			subValue = "7";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Buloh Kasap")){
			//set parameter Gemereh= 3, selectedMain 
			subValue = "8";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Gemas")){
			//set parameter Sg Segamat= 4, selectedMain 
			subValue = "9";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Pagoh")){
			//set parameter Jabi= 5, selectedMain 
			subValue = "10";
			//SQL 3
		}
		else if(selected.equalsIgnoreCase("Labis")){
			//set parameter Jabi= 5, selectedMain 
			subValue = "11";
			//SQL 3
		}
		else{
			subValue = selected; //for locality and year/month
			//SQL 4 OR SQL 6
		}
		return subValue;
	}

	//return current page
	public Integer getCurrentPage(String action){
		if(action.equalsIgnoreCase("common")){
			currentPage = 1;
		}
		if(action.equalsIgnoreCase("next")){
			currentPage++;
		}
		if(action.equalsIgnoreCase("prev")){
			currentPage--;
		}
		return currentPage;
	}

	//load Locality
	public void getLocality(){

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(getApplicationContext().getAssets().open("locality.txt"), "UTF-8")); 

			List<String> list = new ArrayList<String>();

			// do reading, usually loop until end of file reading 
			String mLine;
			while ((mLine = reader.readLine()) != null) {
				//process line
				//. ...
				list.add(mLine);
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.spinner_search_sub_item, list);
				dataAdapter.setDropDownViewResource(R.layout.spinner_search_dropdown_sub_item);
				spinnerSub.setAdapter(dataAdapter);
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

	//start function on click detail and click upload
	public void detailnUpload(ImageView histManual_detail, Button histManual_btnUpload, final String histManual_ids, 
			final String histManual_filenameOffline, final String histManual_Gps, final String histManual_PhotoDesc, 
			final String histManual_Status, final String histManual_OffComent,final String histManual_ReportDate, 
			final String histManual_HouseFullAddress, final String histManual_mCurrentPhotoPath, final String histManual_PatientName){

		Log.d(TAG,"detailTest["+histManual_ids+"]");

		//click on detail image
		histManual_detail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				informationDetail(histManual_ids, histManual_filenameOffline, histManual_Gps, histManual_PhotoDesc, histManual_Status, histManual_OffComent, histManual_ReportDate, histManual_HouseFullAddress, histManual_mCurrentPhotoPath, histManual_PatientName);
			}
		});

		//click on button upload
		histManual_btnUpload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				textIdhidden.setText(histManual_ids);
				Bitmap photo = getPic(histManual_mCurrentPhotoPath);
				try {
					sendPhoto(photo);
					db.updatePicture(histManual_ids);
					setLocale(languageType);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		});
	}
	//end function on click detail and click upload

	//start display information detail
	public void informationDetail(String id, String filename, String gps, String photoDesc, String status, String commentOfficer, String reportDate, String houseFullAddress, String histManual_mCurrentPhotoPath, String histManual_PatientName){

		// custom dialog
		final Dialog dialog = new Dialog(DengueUploadManual.this);
		dialog.setContentView(R.layout.dialog_custom);
		dialog.setTitle("Detail Information");

		// set the custom dialog components - text, image and button

		ImageView myImage = (ImageView) dialog.findViewById(R.id.image);

		TextView histManual_id_dialog  = (TextView) dialog.findViewById(R.id.history_id_dialog);
		TextView histManual_filename_dialog  = (TextView) dialog.findViewById(R.id.history_filename_dialog);
		TextView histManual_gps_dialog  = (TextView) dialog.findViewById(R.id.history_gps_dialog);
		TextView histManual_datereport_dialog  = (TextView) dialog.findViewById(R.id.history_datereport_dialog);
		TextView histManual_status_dialog  = (TextView) dialog.findViewById(R.id.history_status_dialog);
		TextView histManual_uploaded_dialog  = (TextView) dialog.findViewById(R.id.history_uploaded_dialog);
		TextView histManual_commentofficer_dialog  = (TextView) dialog.findViewById(R.id.history_commentofficer_dialog);
		TextView histManual_address_dialog  = (TextView) dialog.findViewById(R.id.history_address_dialog);
		TextView histManual_patientNames_dialog  = (TextView) dialog.findViewById(R.id.history_patientName_dialog);

		//TableRow histManual_case_patientNamesRow_dialog  = (TableRow) dialog.findViewById(R.id.tableRow9);

		histManual_id_dialog.setText(id);
		histManual_filename_dialog.setText(filename);
		histManual_gps_dialog.setText(gps);
		histManual_datereport_dialog.setText(reportDate);
		histManual_status_dialog.setText(status);
		histManual_uploaded_dialog.setText(photoDesc);
		histManual_commentofficer_dialog.setText(commentOfficer);
		histManual_address_dialog.setText(houseFullAddress);
		histManual_patientNames_dialog.setText(histManual_PatientName!=null?histManual_PatientName:languageType.equalsIgnoreCase("ms")?Constants.ms.COMMON_NOTAPPLICABLE:Constants.en.COMMON_NOTAPPLICABLE);
		//histManual_case_patientNamesRow_dialog.setVisibility(View.GONE);

		File image = new File(histManual_mCurrentPhotoPath);
		if(image.exists()){

			Bitmap bmImg = BitmapFactory.decodeFile(image.getAbsolutePath());
			myImage.setImageBitmap(bmImg);

		}
		dialog.show();

	}
	//end display information detail

	//start upload function-follow step by step

	//Step 3
	private class UploadTask extends AsyncTask<Bitmap, Integer, Boolean> implements ProgressListener, DialogInterface.OnCancelListener {

		private ProgressDialog mProgressDialog;

		protected void onPreExecute(){
			mProgressDialog = new ProgressDialog(DengueUploadManual.this);
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
						typeMukimValues = null, houseFullAddress = null, patientName = null;
				try{
					//get data from db sqlite
					Log.d(TAG,"ID HIDDEN ["+textIdhidden.getText().toString()+"]");
					SitePhotos sitePhotos = db.getPhotoPerId(textIdhidden.getText().toString());
					Log.d(TAG,"ID HIDDEN ["+sitePhotos.getPhotoDesc()+"]");
					//end get data from db sqlite

					lat = new StringBody(sitePhotos.getLatitude());
					lon = new StringBody(sitePhotos.getLongitude());
					report = new StringBody(sitePhotos.getReportChoice());
					site = new StringBody(sitePhotos.getSiteChoice());
					im = new StringBody(mngr.getDeviceId());
					photoDesc = new StringBody(sitePhotos.getPhotoDesc());
					insertBy = new StringBody(userID);
					localityName = new StringBody(sitePhotos.getLocality_name());
					localityOther = new StringBody(sitePhotos.getLocality_other().toUpperCase());
					localityIndType = new StringBody(sitePhotos.getLocality_ind_type());
					localityIndName = new StringBody(sitePhotos.getLocality_ind_name().toUpperCase());
					typeMukimValues = new StringBody(sitePhotos.getHouse_mukim());
					houseFullAddress = new StringBody(sitePhotos.getHouse_fullAddress());
					//patientName = new StringBody(sitePhotos.getp.toUpperCase());
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
				//customEntity.addPart("casePatientName",patientName);
				//temporary using before implement server database

				//String photoDesc = mEditText.getText().toString();

				//combine the value of latitude and longitude
				//String latitudeLongitude = String.valueOf(latitude)+","+String.valueOf(longitude);
				//Log.i("customEntity","Lat: "+String.valueOf(latitude) + "Lon: "+ String.valueOf(longitude) + "siteChoice: "+ siteChoice + "imei: " + mngr.getDeviceId());

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
				Toast.makeText(DengueUploadManual.this, languageType.equalsIgnoreCase("ms")?Constants.ms.CAPTURE_UPLOADED:Constants.en.CAPTURE_UPLOADED, Toast.LENGTH_LONG).show();
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

		//public void execute(String histManual_ids, Bitmap bitmap) {
		// TODO Auto-generated method stub

		//}		
	}

	//Step 2
	private void sendPhoto(Bitmap bitmap) throws Exception {
		new UploadTask().execute(bitmap);
	}

	//Step 1
	private Bitmap getPic(String mCurrentPhotoPath){

		ExifInterface exif;

		//Add EXIF data on the image 
		try{

			//exif = new ExifInterface(storageDir + "/" + imageFileName + ".jpg");
			//exif = new ExifInterface(photoFile.getAbsolutePath());
			exif = new ExifInterface(mCurrentPhotoPath);

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
		try {
			//sendPhoto(rotatedBMP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rotatedBMP;
	}
	//end step 1

	//end upload function-follow step by step

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

	//report type
	public String getReportTypeDesc(String histManualReportChoice){
		String histManualReportChoiceDesc = null;

		if(histManualReportChoice.equals("2")){
			histManualReportChoiceDesc = languageType.equalsIgnoreCase("ms")?Constants.ms.UPLOADMANUAL_CASE:Constants.en.UPLOADMANUAL_CASE;
		}else if(histManualReportChoice.equals("3")){
			histManualReportChoiceDesc = languageType.equalsIgnoreCase("ms")?Constants.ms.UPLOADMANUAL_TRAP:Constants.en.UPLOADMANUAL_TRAP;
		}else{
			histManualReportChoiceDesc = languageType.equalsIgnoreCase("ms")?Constants.ms.UPLOADMANUAL_BREED:Constants.en.UPLOADMANUAL_BREED;
		}
		return histManualReportChoiceDesc;

	}


}
