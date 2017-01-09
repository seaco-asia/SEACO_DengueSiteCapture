package com.seaco.denguesitecapture.activities;

import java.util.ArrayList;
import java.util.List;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;
import com.seaco.denguesitecapture.model.UserDetail;

import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends Activity implements TabListener {

	private static final String TAG = "Main Page";
	List<Fragment> fragList = new ArrayList<Fragment>();
	Intent intentObject = getIntent();
	private MySQLiteHelper db;
	String userRegtype, userEmail, userName, userID, userPhoneNo, section, languageType, languageTypeTest, userPassword, radModeType;

	//use SharedPreferences to store and retrieve languageType parameter
	SharedPreferences sharedpreferences;
	public static final String mypreference = "mypref";
	public static final String languageTypePref = "languageTypePrefKey";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		//use SharedPreferences to store and retrieve languageType parameter
		sharedpreferences = getSharedPreferences(mypreference,
				Context.MODE_PRIVATE);

		if (sharedpreferences.contains(languageTypePref)) {
			languageType = sharedpreferences.getString(languageTypePref, "");
		}else{
			languageType = "en";
		}

		//get Intent
		userPhoneNo = getIntent().getExtras().getString("userPhoneNo");
		userEmail = getIntent().getExtras().getString("userEmail");
		userRegtype = getIntent().getExtras().getString("userRegtype");
		userName = getIntent().getExtras().getString("userName");
		userID = getIntent().getExtras().getString("userID");
		userPassword = getIntent().getExtras().getString("userPassword");
		radModeType = getIntent().getExtras().getString("radModeType");
		//languageType = getIntent().getExtras().getString("languageType");

		Log.d(TAG,"userEmail ["+userEmail+"] and userRegtype["+userRegtype+"] and userName["+userName+"] and userID["+userID+"]");

		/* temporary using before implement server database
		 * 
			db = new MySQLiteHelper(this);
			userRegtype = db.getRegType(userEmail);
		 *
		 */
		
		//use for offline.
		//final List<UserDetail> users = db.getAllUsers();
		//Log.d(TAG,"retrieve data users......"+users.indexOf(0));
		//Log.d(TAG,"retrieve data users......"+users.indexOf(6));
		//String userPassword = "seaco2015";
		db = new MySQLiteHelper(this);
		//db.checkUserAlreadyInDB(userPhoneNo, userPassword);
		//end use for offline.

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

		for(int i=1; i<= 4; i++){
			Tab tab = bar.newTab();
			tab.setText("Tab " + i);
			//Set the titles for the tabs 
			if(i == 1){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_CAPTURE:Constants.en.LABEL_TAB_CAPTURE);//tab.setText("Capture Dengue Site");
			}else if(i == 2){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_AREAREPORTED:Constants.en.LABEL_TAB_AREAREPORTED);//tab.setText("History");
			}else if(i == 3){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_MOSQUITOTRAP:Constants.en.LABEL_TAB_MOSQUITOTRAP);//tab.setText("History");
			}else if(i == 4){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_GALLERY:Constants.en.LABEL_TAB_GALLERY);//tab.setText("Photos");
			}

			//tab.setText("Tab " + i);
			tab.setTabListener(this);
			bar.addTab(tab);
		}

	}

	public void officerTabDisplay(){

		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for(int i=1; i<= 7; i++){ //OFFICER
			Tab tab = bar.newTab();
			tab.setText("Tab " + i);
			//Set the titles for the tabs 
			if(i == 1){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_CAPTURE:Constants.en.LABEL_TAB_CAPTURE);//tab.setText("Capture Dengue Site");
			}else if(i == 2){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_AREAREPORTED:Constants.en.LABEL_TAB_AREAREPORTED);//tab.setText("History");
			}else if(i == 3){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_MOSQUITOTRAP:Constants.en.LABEL_TAB_MOSQUITOTRAP);//tab.setText("History");
			}else if(i == 4){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_NEWTASK:Constants.en.LABEL_TAB_NEWTASK);//tab.setText("History");//tab.setText("New Task");
			}else if(i == 5){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_UPDATEDTASK:Constants.en.LABEL_TAB_UPDATEDTASK);//tab.setText("Task History");
			}else if(i == 6){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_CASE:Constants.en.LABEL_TAB_CASE);//tab.setText("Photos");
			}else if(i == 7){
				tab.setText(languageType.equalsIgnoreCase("ms")?Constants.ms.LABEL_TAB_GALLERY:Constants.en.LABEL_TAB_GALLERY);//tab.setText("Photos");
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
		DengueMosquitoTrapFragment dmtf = null;
		CapturedPhotosFragment cpsf = null;

		//passing value from activity to fragment
		Bundle bundle=new Bundle();
		bundle.putString("userPhoneNo", userPhoneNo);
		bundle.putString("userEmail", userEmail);
		bundle.putString("userRegtype", userRegtype);
		bundle.putString("userName", userName);
		bundle.putString("userID", userID);
		bundle.putString("radModeType", radModeType);
		//bundle.putString("languageType", languageType);

		if(fragList.size() > tab.getPosition()){
			fragList.get(tab.getPosition());
		}

		if(f == null){
			/* Capture Photo Fragment */
			if(tab.getPosition() == 0){
				//cpf = new CapturePhotoFragment(userEmail, userName, userID);
				//cpf = new CapturePhotoFragment(userName, userID);
				//cpf = new CapturePhoto();
				cpf = new CapturePhotoFragment();
				cpf.setArguments(bundle);
				fragList.add(cpf);
				ft.replace(android.R.id.content, cpf);
			}else if(tab.getPosition() == 1){
				dhf = new DengueHistoryFragment();
				dhf.setArguments(bundle);
				fragList.add(dhf);
				ft.replace(android.R.id.content, dhf);
			}else if(tab.getPosition() == 2){
				dmtf = new DengueMosquitoTrapFragment();
				dmtf.setArguments(bundle);
				fragList.add(dmtf);
				ft.replace(android.R.id.content, dmtf);				
			}else if(tab.getPosition() == 3){
				cpsf = new CapturedPhotosFragment();
				cpsf.setArguments(bundle);
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
		DengueMosquitoTrapFragment dmtf = null;
		DengueHistoryTaskFragment dhtf = null;
		CapturedPhotosFragment cpsf = null;
		DengueNewTaskFragment dhtnf = null;
		DengueCaseFragment dcf = null;

		//passing value from activity to fragment
		Bundle bundle=new Bundle();
		bundle.putString("userPhoneNo", userPhoneNo);
		bundle.putString("userEmail", userEmail);
		bundle.putString("userRegtype", userRegtype);
		bundle.putString("userName", userName);
		bundle.putString("userID", userID);
		bundle.putString("radModeType", radModeType);
		//bundle.putString("languageType", languageType);

		if(fragList.size() > tab.getPosition()){
			fragList.get(tab.getPosition());
		}

		if(f == null){
			/* Capture Photo Fragment */
			if(tab.getPosition() == 0){
				//cpf = new CapturePhotoFragment(userName, userID);
				//cpf = new CapturePhoto();
				cpf = new CapturePhotoFragment();
				cpf.setArguments(bundle);
				fragList.add(cpf);
				ft.replace(android.R.id.content, cpf);
			}else if(tab.getPosition() == 1){
				//dhf = new DengueHistoryFragment(userName, userID);
				dhf = new DengueHistoryFragment();
				dhf.setArguments(bundle);
				fragList.add(dhf);
				ft.replace(android.R.id.content, dhf);
			}else if(tab.getPosition() == 2){
				//dhf = new DengueHistoryFragment(userName, userID);
				dmtf = new DengueMosquitoTrapFragment();
				dmtf.setArguments(bundle);
				fragList.add(dmtf);
				ft.replace(android.R.id.content, dmtf);
			}else if(tab.getPosition() == 3){
				//dhtnf = new DengueNewTaskFragmentTest(userName, userID);
				dhtnf = new DengueNewTaskFragment();
				dhtnf.setArguments(bundle);
				fragList.add(dhtnf);
				ft.replace(android.R.id.content, dhtnf);				
			}else if(tab.getPosition() == 4){
				//dhtf = new DengueHistoryTaskFragment(userName, userID);
				dhtf = new DengueHistoryTaskFragment();
				dhtf.setArguments(bundle);
				fragList.add(dhtf);
				ft.replace(android.R.id.content, dhtf);				
			}else if(tab.getPosition() == 5){
				//cpsf = new CapturedPhotosFragment(userID, userRegtype);
				dcf = new DengueCaseFragment();
				dcf.setArguments(bundle);
				fragList.add(dcf);
				ft.replace(android.R.id.content, dcf);			
			}else if(tab.getPosition() == 6){
				//cpsf = new CapturedPhotosFragment(userID, userRegtype);
				cpsf = new CapturedPhotosFragment();
				cpsf.setArguments(bundle);
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
	
	//Disable back function in android device. User need to logout to login again.
	@Override
	 public void onBackPressed() {
		return;
	 }

}