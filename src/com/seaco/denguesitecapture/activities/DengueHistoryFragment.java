package com.seaco.denguesitecapture.activities;

import java.util.List;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DengueHistoryFragment extends Fragment {

	
	Context context;
	private TableLayout tableLayout;
	private Activity containingActivity;
	private LayoutInflater inflater;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
		View rootView = inflater.inflate(R.layout.table, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);
		
		MySQLiteHelper db = new MySQLiteHelper(getActivity());
		
		//List photos = db.getAllPhotos();
		
		String[][] photos = db.getPhotosResult();
		
		int j = 0;
		
		while(j < photos.length){
			//View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_item,null,false);
			//View tableRow = rootView.inflate(getActivity(),R.layout.table_item);
			
			View tableRow = inflater.inflate(R.layout.table_item, null, false);
			
		    TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
	        TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
	        TextView history_gps  = (TextView) tableRow.findViewById(R.id.history_gps);
	        TextView history_uploaded  = (TextView) tableRow.findViewById(R.id.history_uploaded);

	        history_id.setText(photos[j][0]);
	        history_filename.setText(photos[j][1]);
	        history_gps.setText(photos[j][2]);
	        history_uploaded.setText("");
	        
	        j++;
	        tableLayout.addView(tableRow);
		}
		
		return rootView;
	}	
	
}
