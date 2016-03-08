package com.seaco.denguesitecapture.activities;

import java.util.ArrayList;
import java.util.List;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;

import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends Activity implements TabListener {

	private static final String TAG = "Main Page";
	List<Fragment> fragList = new ArrayList<Fragment>();
	Intent intentObject = getIntent();
	private MySQLiteHelper db;
	String userRegtype, userEmail, userName, userID, section;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		userEmail = getIntent().getExtras().getString("userEmail");
		userRegtype = getIntent().getExtras().getString("userRegtype");
		userName = getIntent().getExtras().getString("userName");
		userID = getIntent().getExtras().getString("userID");
		
		Log.d(TAG,"userEmail ["+userEmail+"] and userRegtype["+userRegtype+"] and userName["+userName+"] and userID["+userID+"]");
		
		/* temporary using before implement server database
		 * 
			db = new MySQLiteHelper(this);
			userRegtype = db.getRegType(userEmail);
		*
		*/

		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		if(userRegtype.equalsIgnoreCase("COM")){ //COMMUNITY
			Log.d("test........","COM");
			commTabDisplay();
		}else{
			Log.d("test........","AO"); //AGENCY OFFICER
			officerTabDisplay();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//Inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft){

	}

	public void onTabSelected(Tab tab, FragmentTransaction ft){
		
		if(userRegtype.equalsIgnoreCase("COM")){ //COMMUNITY
			Log.d("test........","COM");
			commTabSelected(tab, ft);
		}else{
			Log.d("test........","AO"); //AGENCY OFFICER
			officerTabSelected(tab, ft);
		}
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft){
		if(fragList.size() > tab.getPosition()){
			ft.remove(fragList.get(tab.getPosition()));
		}
	}

	public void commTabDisplay(){

		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for(int i=1; i<= 3; i++){
			Tab tab = bar.newTab();
			tab.setText("Tab " + i);
			//Set the titles for the tabs 
			if(i == 1){
				tab.setText("Capture Dengue Site");
			}else if(i == 2){
				tab.setText("History");
			}else if(i == 3){
				tab.setText("Photos");
			}

			//tab.setText("Tab " + i);
			tab.setTabListener(this);
			bar.addTab(tab);
		}

	}

	public void officerTabDisplay(){

		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for(int i=1; i<= 5; i++){ //OFFICER
			Tab tab = bar.newTab();
			tab.setText("Tab " + i);
			//Set the titles for the tabs 
			if(i == 1){
				tab.setText("Capture Dengue Site");
			}else if(i == 2){
				tab.setText("History");
			}else if(i == 3){
				tab.setText("New Task");
			}else if(i == 4){
				tab.setText("Task History");
			}else if(i == 5){
				tab.setText("Photos");
			}
			//tab.setText("Tab " + i);
			tab.setTabListener(this);
			bar.addTab(tab);
		}
	}

	public void commTabSelected(Tab tab, FragmentTransaction ft){
		Fragment f = null;
		TabFragment tf = null;
		CapturePhotoFragment cpf = null;
		DengueHistoryFragment dhf = null;
		CapturedPhotosFragment cpsf = null;

		if(fragList.size() > tab.getPosition()){
			fragList.get(tab.getPosition());
		}

		if(f == null){
			/* Capture Photo Fragment */
			if(tab.getPosition() == 0){
				cpf = new CapturePhotoFragment(userEmail, userName, userID);
				//cpf = new CapturePhoto();
				fragList.add(cpf);
				ft.replace(android.R.id.content, cpf);
			}else if(tab.getPosition() == 1){
				dhf = new DengueHistoryFragment(userEmail, userName, userID);
				fragList.add(dhf);
				ft.replace(android.R.id.content, dhf);
			}else if(tab.getPosition() == 2){
				cpsf = new CapturedPhotosFragment(userEmail, userName, userID, userRegtype);
				fragList.add(cpsf);
				ft.replace(android.R.id.content, cpsf);				
			}else{
				tf = new TabFragment();
				Bundle data = new Bundle();				
				data.putInt("idx", tab.getPosition());
				tf.setArguments(data);
				fragList.add(tf);
				ft.replace(android.R.id.content, tf);
			}
		}else{
			tf = (TabFragment) f;
			cpf = (CapturePhotoFragment) f;
			ft.replace(android.R.id.content, tf);
		}
	}
	
	public void officerTabSelected(Tab tab, FragmentTransaction ft){
		Fragment f = null;
		TabFragment tf = null;
		CapturePhotoFragment cpf = null;
		DengueHistoryFragment dhf = null;
		DengueHistoryTaskFragment dhtf = null;
		CapturedPhotosFragment cpsf = null;
		DengueNewTaskFragment dhtnf = null;

		if(fragList.size() > tab.getPosition()){
			fragList.get(tab.getPosition());
		}

		if(f == null){
			/* Capture Photo Fragment */
			if(tab.getPosition() == 0){
				cpf = new CapturePhotoFragment(userEmail, userName, userID);
				//cpf = new CapturePhoto();
				fragList.add(cpf);
				ft.replace(android.R.id.content, cpf);
			}else if(tab.getPosition() == 1){
				dhf = new DengueHistoryFragment(userEmail, userName, userID);
				fragList.add(dhf);
				ft.replace(android.R.id.content, dhf);
			}else if(tab.getPosition() == 2){
				dhtnf = new DengueNewTaskFragment(userEmail, userName, userID);
				fragList.add(dhtnf);
				ft.replace(android.R.id.content, dhtnf);				
			}else if(tab.getPosition() == 3){
				dhtf = new DengueHistoryTaskFragment(userEmail, userName, userID);
				fragList.add(dhtf);
				ft.replace(android.R.id.content, dhtf);				
			}else if(tab.getPosition() == 4){
				cpsf = new CapturedPhotosFragment(userEmail, userName, userID, userRegtype);
				fragList.add(cpsf);
				ft.replace(android.R.id.content, cpsf);				
			}else{
				tf = new TabFragment();
				Bundle data = new Bundle();				
				data.putInt("idx", tab.getPosition());
				tf.setArguments(data);
				fragList.add(tf);
				ft.replace(android.R.id.content, tf);
			}
		}else{
			tf = (TabFragment) f;
			cpf = (CapturePhotoFragment) f;
			ft.replace(android.R.id.content, tf);
		}
	}

}