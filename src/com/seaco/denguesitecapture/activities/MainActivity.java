package com.seaco.denguesitecapture.activities;

import java.util.ArrayList;
import java.util.List;

import com.seaco.denguesitecapture.R;

import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.widget.FrameLayout;


public class MainActivity extends Activity implements TabListener {

	List<Fragment> fragList = new ArrayList<Fragment>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		for(int i=1; i<= 3; i++){
			Tab tab = bar.newTab();
			tab.setText("Tab " + i);
			/* Set the titles for the tabs */
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//Inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onTabReselected(Tab tab, FragmentTransaction ft){
		
	}
	
	public void onTabSelected(Tab tab, FragmentTransaction ft){
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
				cpf = new CapturePhotoFragment();
				//cpf = new CapturePhoto();
				fragList.add(cpf);
				ft.replace(android.R.id.content, cpf);
			}else if(tab.getPosition() == 1){
				dhf = new DengueHistoryFragment();
				fragList.add(dhf);
				ft.replace(android.R.id.content, dhf);
			}else if(tab.getPosition() == 2){
				cpsf = new CapturedPhotosFragment();
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
	
	public void onTabUnselected(Tab tab, FragmentTransaction ft){
		if(fragList.size() > tab.getPosition()){
			ft.remove(fragList.get(tab.getPosition()));
		}
	}

	
}
