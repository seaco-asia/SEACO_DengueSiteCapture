package com.seaco.denguesitecapture.activities;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CapturedPhotosFragment extends Fragment {

	private static final String TAG = "CapturedPhotosFragment";
	Context context;
	String email, name, id, regType;

	public CapturedPhotosFragment(String userEmail, String userName, String userID, String userRegtype) {
		email = userEmail;
		name = userName;
		id = userID;
		regType = userRegtype;
		// TODO Auto-generated constructor stub
		Log.d(TAG, "com/officer's email ["+email+"] and com/officer's name ["+name+"] AND idComm_Officer["+id+"]");
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment, container, false);


		//		Intent intent = new Intent(getActivity().getApplicationContext(), CapturedPhotosGrid.class);
		//		Intent intent = new Intent(getActivity().getApplicationContext(), DownloadImageActivity.class);
		//		Intent intent = new Intent(getActivity().getApplicationContext(), CapturedPhotosGridTest.class);
		if(regType.equalsIgnoreCase("COM")){
			Intent intent = new Intent(getActivity().getApplicationContext(), CapturedPhotosGrid.class);
			intent.putExtra("userID", id);
			startActivity(intent);	
		}else{
			Intent intent = new Intent(getActivity().getApplicationContext(), CapturedPhotosGridOff.class);
			startActivity(intent);	
		}

		//		intent.putExtra("userID", id);
		//		container.setContent(intent);
		return rootView;
	}
}
