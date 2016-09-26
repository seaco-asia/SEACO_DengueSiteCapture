package com.seaco.denguesitecapture.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class DengueMosquitoTrapFragment extends Fragment {

	private static final String TAG = "DengueMosquitoTrapFragment";
	Context context;
	Bitmap bitmap;
	ImageView img;
	View tableRow;
	RadioGroup radFilter;
	TableLayout tableLayout, tableLayoutTitle;
	LinearLayout linearLayout;
	public int totalPhotos, pageCount, pageCountDisplay;
	public int NUM_ITEMS_PAGE= 16; //number= 0, increment = 0;
	private String JSON_STRING, selected;
	public String action = "common";
	public String sections = "new";
	String email, name, id, valDecision, statusDesc;
	String decisionOfficer;
	String subValue, subValues;
	String languageType , regType;
	RelativeLayout layoutRelative;
	Spinner spinnerMain, spinnerSub;
	Integer selectedMain, currentPage;
	Button buttonPrev, buttonNext, buttonSearch;
	TextView textPageNumber, textLblSearchSub;

	//use SharedPreferences to store and retrieve languageType parameter//test SharedPreferences
	SharedPreferences sharedpreferences;
	public static final String mypreference = "mypref";
	public static final String languageTypePref = "languageTypePrefKey";

	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

		final View rootView = inflater.inflate(R.layout.table_mosquito_title, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);

		context = container.getContext();

		layoutRelative = (RelativeLayout)rootView.findViewById(R.id.layoutRelative);

		//receive parameter from activity
		id=getArguments().getString("userID");
		name=getArguments().getString("userName");
		regType=getArguments().getString("userRegtype");
		//languageType=getArguments().getString("languageType"); 

		//use SharedPreferences to store and retrieve languageType parameter
		sharedpreferences = this.getActivity().getSharedPreferences(mypreference,Context.MODE_PRIVATE);

		if (sharedpreferences.contains(languageTypePref)) {
			languageType = sharedpreferences.getString(languageTypePref, "");
		}else{
			languageType = "en";
		}

		/* temporary using before implement server database
		 * 
		    final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			//sql
			MySQLiteHelper db = new MySQLiteHelper(getActivity());
			final String[][] photos = db.getPhotosResult();

			final MySQLiteHelper db1 = new MySQLiteHelper(getActivity());
		 *
		 */

		//label
		textLblSearchSub = (TextView) rootView.findViewById(R.id.mosquito_title_Search2);

		// set dropdown main
		spinnerMain = (Spinner) rootView.findViewById(R.id.spinnerMain);
		// set dropdown sub
		spinnerSub = (Spinner) rootView.findViewById(R.id.spinnerSub);


		//list item dropdown main
		List<String> list = new ArrayList<String>();
		list.add(0,"All");
		//list.add(1,"Report Type");
		list.add(1,"Locality");
		list.add(2,"Mukim");
		list.add(3,"Month/Year");

		//adapter dropdown main
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_search_item, list);
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
					ArrayAdapter<String> dataAdapterSub = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_search_sub_item, listSub);
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
		buttonPrev = (Button) rootView.findViewById(R.id.buttonPrevious);
		buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
		buttonSearch = (Button) rootView.findViewById(R.id.buttonSearch);

		buttonSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				action = "common";
				currentPage = getCurrentPage(action);
				getJSON(rootView, inflater, action, buttonPrev, buttonNext, selectedMain, subValues, currentPage) ;
				getTotalPage(selectedMain, subValues, currentPage);

			}
		});


		buttonPrev.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				action = "prev";
				currentPage = getCurrentPage(action);
				getJSON(rootView, inflater, action, buttonPrev, buttonNext, selectedMain, subValues, currentPage) ;
				getTotalPage(selectedMain, subValues, currentPage);

			}
		});


		buttonNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				action = "next";
				currentPage = getCurrentPage(action);
				getJSON(rootView, inflater, action, buttonPrev, buttonNext, selectedMain, subValues, currentPage);
				getTotalPage(selectedMain, subValues, currentPage);

			}
		});

		//first time load
		currentPage = getCurrentPage(action);
		selectedMain = 0; subValues = "0";
		getJSON(rootView, inflater, action, buttonPrev, buttonNext, selectedMain, subValues, currentPage) ;
		getTotalPage(selectedMain, subValues, currentPage);
		//end first time load

		return rootView;

	}


	// To retrieve data from database
	private void getJSON(final View rootView, final LayoutInflater inflater, final String action, final Button buttonPrev, final Button buttonNext, final Integer selectedMain, final String subValues, final Integer currentPage) {

		class GetJSON extends AsyncTask<Void, Void, String> {

			// ProgressDialog loading;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				JSON_STRING = s;

				showPhotosCommon(rootView, inflater, buttonPrev, buttonNext, sections);
			}

			@Override
			protected String doInBackground(Void... params) {

				RequestHandler rh = new RequestHandler();
				HashMap<String,String> paramSearch = new HashMap<String, String>();
				paramSearch.put(Config.KEY_SEARCH_MAIN,selectedMain.toString());
				paramSearch.put(Config.KEY_SEARCH_SUB,subValues);
				paramSearch.put(Config.KEY_SEARCH_CURRPAGE,currentPage.toString());
				paramSearch.put(Config.KEY_USER_ID,id);

				String s = rh.sendPostRequest(regType.equalsIgnoreCase("AO")?Config.URL_GET_ALL_MOSQUITO_TRAP:Config.URL_GET_PER_MOSQUITO_TRAP,paramSearch);
				return s;
				
				
			}
		}

		GetJSON gj = new GetJSON();
		gj.execute();
	}

	//show all data firstime enter this page
	private void showPhotosCommon(View rootView, LayoutInflater inflater, final Button buttonPrev, final Button buttonNext, final String sections) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(JSON_STRING);
			JSONArray result = jsonObject
					.getJSONArray(Config.TAG_JSON_ARRAY_PHOTOS);

			//total data
			totalPhotos =result.length();

			textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);

			int i = 0;

			//start refresh layout
			tableLayout.removeAllViews();
			tableLayout.refreshDrawableState();
			//end refresh layout

			while(i<result.length()){

				JSONObject jo = result.getJSONObject(i);
				final String mosquitoID = jo.getString(Config.TAG_ID);
				//final String patientName = jo.getString(Config.TAG_CASE_PATIENT_NAME);
				final String filename = jo.getString(Config.TAG_FILENAME);
				String latitude = jo.getString(Config.TAG_LATITUDE);
				String longitude = jo.getString(Config.TAG_LONGITUDE);
				final String gps = latitude +","+ longitude;
				final String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);
				final String status = "-";
				final String commentOfficer = "-";
				final String reportDate = jo.getString(Config.TAG_INSERT_DATE);
				final String houseFullAddress = jo.getString(Config.TAG_HOUSE_FULL_ADDRESS);
				final String localityName = jo.getString(Config.TAG_HOUSE_LOCALITY_NAME);
				final String other = jo.getString(Config.TAG_HOUSE_LOCALITY_OTHER);


				Log.d(TAG,"id ["+mosquitoID+"] and filename ["+filename+"]");

				tableRow = inflater.inflate(R.layout.table_mosquito_item, null, false);

				TextView mosquito_id  = (TextView) tableRow.findViewById(R.id.mosquito_id);
				final TextView mosquito_fileName  = (TextView) tableRow.findViewById(R.id.mosquito_fileName);
				//TextView mosquito_patientName  = (TextView) tableRow.findViewById(R.id.mosquito_patientName);
				//TextView history_status  = (TextView) tableRow.findViewById(R.id.mosquito);
				TextView mosquito_reportDate  = (TextView) tableRow.findViewById(R.id.mosquito_reportDate);
				ImageView mosquito_detail  = (ImageView) tableRow.findViewById(R.id.imageView_detail);

				mosquito_id.setText(!localityName.equalsIgnoreCase("Other")?localityName:other);
				mosquito_fileName.setText(filename);
				mosquito_reportDate.setText(reportDate);

				//click on detail image
				mosquito_detail.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						informationDetail(mosquitoID, filename, gps, photoDesc, status, commentOfficer, reportDate, houseFullAddress);
					}
				});

				/*for temporary commented
					decisionOfficer = db1.getTaskDecision(Integer.parseInt(photos[j][0]));//extract decision of officer
					history_status.setText(decisionOfficer);*/

				i++;
				tableLayout.addView(tableRow);
			} 

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//load Locality
	public void getLocality(){

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(context.getAssets().open("locality.txt"), "UTF-8")); 

			List<String> list = new ArrayList<String>();

			// do reading, usually loop until end of file reading 
			String mLine;
			while ((mLine = reader.readLine()) != null) {
				//process line
				//. ...
				list.add(mLine);
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
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

	// get locality list from database
	/*private void getLocality() {

		class GetJSON extends AsyncTask<Void, Void, String> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				JSON_STRING = s;

				JSONObject jsonObject = null;
				try {

					jsonObject = new JSONObject(JSON_STRING);
					JSONArray result = jsonObject
							.getJSONArray(Config.TAG_JSON_ARRAY_LOCALITYLIST);

					List<String> listSub = new ArrayList<String>(); //for spinner

					for (int i = 0; i < result.length(); i++) {
						JSONObject c = result.getJSONObject(i);
						listSub.add(c.getString(Config.TAG_LOCALITYNAME));
					}

					ArrayAdapter<String> dataAdapterSub = new ArrayAdapter<String>(getActivity().getApplicationContext(),
							R.layout.spinner_search_sub_item, listSub);
					dataAdapterSub.setDropDownViewResource(R.layout.spinner_search_dropdown_sub_item);

					spinnerSub.setAdapter(dataAdapterSub);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				String s = rh.sendGetRequest(Config.URL_GET_LOCALITYLIST);
				return s;
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute();
	}*/

	// get total page from database
	private void getTotalPage(final Integer selectedMain, final String subValues, final Integer currentPage) {

		class GetJSON extends AsyncTask<Void, Void, String> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				// loading.dismiss();
				JSON_STRING = s;

				JSONObject jsonObject = null;
				try {

					jsonObject = new JSONObject(JSON_STRING);
					JSONArray result = jsonObject
							.getJSONArray(Config.TAG_JSON_ARRAY_PAGE_COUNT);

					JSONObject jo = result.getJSONObject(0);
					String pageCount = jo.getString(Config.TAG_PAGE_COUNT);

					textPageNumber.setText("Page "+(currentPage)+" of "+pageCount);

					// Disabled Button Next
					int pageCounts =Integer.parseInt(pageCount);

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

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				HashMap<String,String> paramSearch = new HashMap<String, String>();
				paramSearch.put(Config.KEY_SEARCH_MAIN,selectedMain.toString());
				paramSearch.put(Config.KEY_SEARCH_SUB,subValues);
				paramSearch.put(Config.KEY_USER_ID,id);

				String s = rh.sendPostRequest(regType.equalsIgnoreCase("AO")?Config.URL_GET_ALL_MOSQUITO_TRAP_PAGE:Config.URL_GET_PER_MOSQUITO_TRAP_PAGE,paramSearch);
				return s;
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute();
	}

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

	//status description
	public String statusDesc(String status){
		//0 = Not Relevant;  1 = Viewed;  2 = In Process ; 3 = Resolved
		if(status.equals("1")){
			statusDesc ="Viewed";
		}else if(status.equals("2")){
			statusDesc ="In Process";
		}else if(status.equals("3")){
			statusDesc ="Resolved";
		}else if(status.equals("0")){
			statusDesc ="Not Relevant";
		}else if(status.equals("5")){ //case
			statusDesc ="Alive"; 
		}else if(status.equals("6")){ //Death
			statusDesc ="Dead";
		}else{
			statusDesc ="New";
		}
		return statusDesc;
	}

	public void informationDetail(String id, String mosquito_fileName, String gps, String photoDesc, String status, String commentOfficer, String reportDate, String houseFullAddress){

		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_custom);
		dialog.setTitle("Detail Information");

		// set the custom dialog components - text, image and button
		img = (ImageView) dialog.findViewById(R.id.image);

		TextView mosquito_id_dialog  = (TextView) dialog.findViewById(R.id.history_id_dialog);
		TextView mosquito_filename_dialog  = (TextView) dialog.findViewById(R.id.history_filename_dialog);
		TextView mosquito_gps_dialog  = (TextView) dialog.findViewById(R.id.history_gps_dialog);
		TextView mosquito_datereport_dialog  = (TextView) dialog.findViewById(R.id.history_datereport_dialog);
		TextView mosquito_status_dialog  = (TextView) dialog.findViewById(R.id.history_status_dialog);
		TextView mosquito_uploaded_dialog  = (TextView) dialog.findViewById(R.id.history_uploaded_dialog);
		TextView mosquito_commentofficer_dialog  = (TextView) dialog.findViewById(R.id.history_commentofficer_dialog);
		TextView mosquito_address_dialog  = (TextView) dialog.findViewById(R.id.history_address_dialog);
		//TextView mosquito_patientNames_dialog  = (TextView) dialog.findViewById(R.id.history_patientName_dialog);
		
		TableRow case_patientNamesRow_dialog  = (TableRow) dialog.findViewById(R.id.tableRow9);

		mosquito_id_dialog.setText(id);
		mosquito_filename_dialog.setText(mosquito_fileName);
		mosquito_gps_dialog.setText(gps);
		mosquito_datereport_dialog.setText(reportDate);
		mosquito_status_dialog.setText("-");
		//mosquito_status_dialog.setText(statusDesc(status));
		mosquito_uploaded_dialog.setText(photoDesc);
		mosquito_commentofficer_dialog.setText(!commentOfficer.equals("null")?commentOfficer:"-");
		mosquito_address_dialog.setText(!houseFullAddress.equals("null")?houseFullAddress:"");
		//case_patientNames_dialog.setText(patientName!="null"?patientName:"Not Available");
		new LoadImage().execute("https://storage.googleapis.com/dengue-storage-00/dengueapps/"+mosquito_fileName);
		
		case_patientNamesRow_dialog.setVisibility(View.GONE);

		dialog.show();

	}

	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//pDialog = new ProgressDialog(TestDisplaySinglePhoto.this);
			//pDialog.setMessage("Loading Image ....");
			//pDialog.show();

		}
		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {

			if(image != null){
				// set the custom dialog components - text, image and button
				img.setImageBitmap(image);
				//pDialog.dismiss();

			}else{

				//pDialog.dismiss();
				//Toast.makeText(TestDisplaySinglePhoto.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

			}
		}
	}
}