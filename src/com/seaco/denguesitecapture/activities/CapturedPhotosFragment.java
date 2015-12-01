package com.seaco.denguesitecapture.activities;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CapturedPhotosFragment extends Fragment {

	
	Context context;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment, container, false);
		
		
		Intent intent = new Intent(getActivity().getApplicationContext(), CapturedPhotosGrid.class);
		
		//container.setContent(intent);
		startActivity(intent);		
		
		return rootView;
	}
}
