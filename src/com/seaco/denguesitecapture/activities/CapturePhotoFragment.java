package com.seaco.denguesitecapture.activities;

import java.util.ArrayList;
import java.util.List;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class CapturePhotoFragment extends Fragment {

	
	Context context;
	private Spinner spinner1;
	private String selected;
	//private OnItemSelectedListener listener;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.capture_photo, container, false);
		
		spinner1 = (Spinner) rootView.findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		
		list.add("Site A");
		list.add("Site B");
		list.add("Site C");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		
		spinner1.setAdapter(dataAdapter);
		
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
				//container.setContent(intent);
				startActivity(intent);
			}
		});
		
		return rootView;
	}
	
	public void addItemsonSpinner(){
		//spinner1 = (Spinner) findViewById(R.id.spinner1);
		
		List<String> list = new ArrayList<String>();
		
		list.add("Site A");
		list.add("Site B");
		list.add("Site C");
		
	}
	
}
