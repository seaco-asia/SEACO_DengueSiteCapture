package com.seaco.denguesitecapture.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class CapturePhotoFragment extends Fragment {

	private static final String TAG = "CapturePhotoFragment";
	Context context;
	private Spinner spinner1;
	private String selected;
	//private OnItemSelectedListener listener;
	Context contexts= getApplicationContext();
	String email, name, id;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.capture_photo, container, false);
		
		//receive parameter from activity
		id=getArguments().getString("userID");
		name=getArguments().getString("userName");
		
		Log.d(TAG, "community's email ["+email+"] and community's's name ["+name+"] AND idCommunity["+id+"]");

		spinner1 = (Spinner) rootView.findViewById(R.id.spinner1);

		createFolder();
		loadDengueSiteList();

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

		Button btnLaunchCapture = (Button) rootView.findViewById(R.id.btnLaunchCapture);

		btnLaunchCapture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity().getApplicationContext(), CapturePhoto.class);
				intent.putExtra("siteChoice", selected);
				intent.putExtra("userID", id);
				startActivity(intent);
			}
		});

		return rootView;
	}

	private Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addItemsonSpinner(){
		//spinner1 = (Spinner) findViewById(R.id.spinner1);

		List<String> list = new ArrayList<String>();

		list.add("Site A");
		list.add("Site B");
		list.add("Site C");

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
}
