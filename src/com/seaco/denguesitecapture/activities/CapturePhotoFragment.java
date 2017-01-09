package com.seaco.denguesitecapture.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class CapturePhotoFragment extends Fragment {

	private static final String TAG = "CapturePhotoFragment";
	Context context;
	private Spinner spinner1;
	private String selected, reportChoice;
	private String JSON_STRING;
	//private OnItemSelectedListener listener;
	String phoneNo, email, regType, name, id, lang, languageType, radModeType;
	RadioGroup radReportChoice;
	RadioButton radBtnBreeding, radBtnCase, radBtnTrap;
	Button btn_mys, btn_eng, btn_Logout, btn_uploadOfflineMain;
	TextView lblTypeReport;

	//use SharedPreferences to store and retrieve languageType parameter
	SharedPreferences sharedpreferences;
	public static final String mypreference = "mypref";
	public static final String languageTypePref = "languageTypePrefKey";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		final View rootView = inflater.inflate(R.layout.capture_photo, container, false);

		//receive parameter from activity
		phoneNo=getArguments().getString("userPhoneNo");
		email=getArguments().getString("userEmail");
		regType=getArguments().getString("userRegtype");
		name=getArguments().getString("userName");
		id=getArguments().getString("userID");
		radModeType = getArguments().getString("radModeType");
		//languageType=getArguments().getString("languageType");

		//use SharedPreferences to store and retrieve languageType parameter
		sharedpreferences = this.getActivity().getSharedPreferences(mypreference,Context.MODE_PRIVATE);

		if (sharedpreferences.contains(languageTypePref)) {
			languageType = sharedpreferences.getString(languageTypePref, "");
		}else{
			languageType = "en";
		}


		Log.d(TAG, "community's email ["+email+"] and community's's name ["+name+"] AND idCommunity["+id+"]");
		
		lblTypeReport = (TextView) rootView.findViewById(R.id.msgSiteReport);

		spinner1 = (Spinner) rootView.findViewById(R.id.spinner1);

		List<String> list = new ArrayList<String>();

		list.add("Residence");
		list.add("Construction");
		list.add("Public area");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		spinner1.setAdapter(dataAdapter);

		radReportChoice = (RadioGroup) rootView.findViewById(R.id.radioGroupReport);
		radBtnBreeding = (RadioButton) rootView.findViewById(R.id.radBreeding);
		radBtnCase = (RadioButton) rootView.findViewById(R.id.radCase);
		radBtnTrap = (RadioButton) rootView.findViewById(R.id.radTrap);

		btn_Logout = (Button) rootView.findViewById(R.id.btn_Logout);
		
		createFolder();
		//addItemsonSpinner();
		//getJSON();
		//loadDengueSiteList();

		OnItemSelectedListener os = new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
				selected = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0){

			}

		};

		//Set listener for an item select
		spinner1.setOnItemSelectedListener(os);
		
		//if Officer, this group of button will enable. Otherwise, disable
		if(regType.equalsIgnoreCase("COM")){
			lblTypeReport.setVisibility(View.VISIBLE);
			radReportChoice.setVisibility(View.VISIBLE);
			radBtnCase.setVisibility(View.GONE);
			
		}else{
			lblTypeReport.setVisibility(View.VISIBLE);
			radReportChoice.setVisibility(View.VISIBLE);
			radBtnCase.setVisibility(View.VISIBLE);
			
		}

		//setRadioButton
		radReportChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radBreeding:
					reportChoice = "1"; //set decision value
					break;

				case R.id.radCase:
					// do operations specific to this selection
					reportChoice = "2"; //set decision value
					break;
					
				case R.id.radTrap:
					// do operations specific to this selection
					reportChoice = "3"; //set decision value
					break;
				}
			}
		});

		Button btnLaunchCapture = (Button) rootView.findViewById(R.id.btnLaunchCapture);

		btnLaunchCapture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity().getApplicationContext(), CapturePhoto.class);
				intent.putExtra("reportChoice", reportChoice!=null?reportChoice:"1");
				intent.putExtra("siteChoice", selected);
				intent.putExtra("userPhoneNo", phoneNo);
				intent.putExtra("userEmail", email);
				intent.putExtra("userRegtype", regType);
				intent.putExtra("userName", name);
				intent.putExtra("userID", id);
				intent.putExtra("radModeType", radModeType);
				//intent.putExtra("languageType", languageType);
				startActivity(intent);
			}
		});
		
		btn_Logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Log.d(TAG,"Logout");
				Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		//test offline
		Button btn_uploadOfflineMain = (Button) rootView.findViewById(R.id.btn_UploadOfflineMain);
		btn_uploadOfflineMain.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity().getApplicationContext(), DengueUploadManual.class);
				intent.putExtra("reportChoice", reportChoice!=null?reportChoice:"1");
				intent.putExtra("siteChoice", selected);
				intent.putExtra("userPhoneNo", phoneNo);
				intent.putExtra("userEmail", email);
				intent.putExtra("userRegtype", regType);
				intent.putExtra("userName", name);
				intent.putExtra("userID", id);
				intent.putExtra("radModeType", radModeType);
				//intent.putExtra("languageType", languageType);
				startActivity(intent);
			}
		});
		//end test offline

		return rootView;
	}

	public void addItemsonSpinner(){
		//spinner1 = (Spinner) findViewById(R.id.spinner1);

		List<String> list = new ArrayList<String>();

		list.add("Resident");
		list.add("Construction");
		list.add("Public area");



	}

	public void createFolder(){
		File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "com.seaco.denguesiteCapture");
		Log.d("loadDengueSiteList", "create folder..."+folder);
		if (!folder.exists()) {
			folder.mkdir();
			Log.d("loadDengueSiteList", "if successs");
		}
	}

	public void loadDengueSiteList(){

		String folder = Environment.getExternalStorageDirectory() + File.separator + 
				"com.seaco.denguesiteCapture";
		File file = new File(folder,"DengueSiteList.txt");

		List<String> list = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {

				Log.d("loadDengueSiteList", "loadDengueSiteList/data 1: "+line);

				list.add(line);
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
						R.layout.spinner_item, list);
				dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				spinner1.setAdapter(dataAdapter);
			}
			br.close();
		}catch (IOException e) {
			Log.d("loadDengueSiteList", "errorMessage"+e);
		}
	}

	// To retrieve data from database
	private void getJSON() {

		class GetJSON extends AsyncTask<Void, Void, String> {

			// ProgressDialog loading;

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
							.getJSONArray(Config.TAG_JSON_ARRAY_SITELIST);

					List<String> list = new ArrayList<String>(); //for spinner

					for (int i = 0; i < result.length(); i++) {
						JSONObject c = result.getJSONObject(i);
						Log.d(TAG,"resultSiteList: "+c.getString(Config.TAG_SITENAME_ENG));
						list.add(c.getString(Config.TAG_SITENAME_ENG));
					}

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
							R.layout.spinner_item, list);
					dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

					// attaching data adapter to spinner
					spinner1.setAdapter(dataAdapter);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				String s = rh.sendGetRequest(Config.URL_GET_SITELIST);
				return s;
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute();
	}

}
