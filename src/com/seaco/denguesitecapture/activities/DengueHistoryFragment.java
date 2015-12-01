package com.seaco.denguesitecapture.activities;


import java.util.ArrayList;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class DengueHistoryFragment extends Fragment {


	Context context;
	private TableLayout tableLayout;
	View tableRow;
	public int totalPhotos, pageCount, pageCountDisplay;
	public int NUM_ITEMS_PAGE= 10, number= 0, increment = 0;



	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

		View rootView = inflater.inflate(R.layout.table, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);

		final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);

		//sql
		MySQLiteHelper db = new MySQLiteHelper(getActivity());
		final String[][] photos = db.getPhotosResult();

		//button prev and next
		final Button buttonPrev = (Button) rootView.findViewById(R.id.buttonPrevious);
		final Button buttonNext = (Button) rootView.findViewById(R.id.buttonNext);

		buttonPrev.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				increment--;
				pageCount = numberPages(increment);
				textPageNumber.setText("Page "+(increment+1)+" of "+pageCount);

				int start = increment * NUM_ITEMS_PAGE;
				int j=start;

				tableLayout.removeAllViews();

				while(j<(start)+NUM_ITEMS_PAGE){

					tableRow = inflater.inflate(R.layout.table_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_gps  = (TextView) tableRow.findViewById(R.id.history_gps);
					TextView history_uploaded  = (TextView) tableRow.findViewById(R.id.history_uploaded);

					history_id.setText(photos[j][0]);
					history_filename.setText(photos[j][1]);
					history_gps.setText(photos[j][2]);
					history_uploaded.setText(photos[j][3]);
					//history_uploaded.setText("");

					j++;
					tableLayout.addView(tableRow);
					tableLayout.refreshDrawableState();
				}

				if(checkEnablePrevious()){buttonPrev.setEnabled(true);}else{buttonPrev.setEnabled(false);}
				if(checkEnableNext()){buttonNext.setEnabled(true);}else{buttonNext.setEnabled(false);}

			}
		});


		buttonNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				increment++;
				pageCount = numberPages(increment);
				textPageNumber.setText("Page "+(increment+1)+" of "+pageCount);

				int start = increment * NUM_ITEMS_PAGE;
				int j=start;

				tableLayout.removeAllViews();

				while(j<(start)+NUM_ITEMS_PAGE){
					if(j<photos.length){

						tableRow = inflater.inflate(R.layout.table_item, null, false);

						TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
						TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
						TextView history_gps  = (TextView) tableRow.findViewById(R.id.history_gps);
						TextView history_uploaded  = (TextView) tableRow.findViewById(R.id.history_uploaded);

						history_id.setText(photos[j][0]);
						history_filename.setText(photos[j][1]);
						history_gps.setText(photos[j][2]);
						history_uploaded.setText(photos[j][3]);
						//history_uploaded.setText("");

						j++;

						tableLayout.addView(tableRow);
						tableLayout.refreshDrawableState();
						buttonNext.setEnabled(false);
					}else{
						break;
					}
				}

				if(checkEnablePrevious()){buttonPrev.setEnabled(true);}else{buttonPrev.setEnabled(false);}
				if(checkEnableNext()){buttonNext.setEnabled(true);}else{buttonNext.setEnabled(false);}

			}
		});

		//total data
		totalPhotos =photos.length;

		//display the number of pages
		pageCount = numberPages(increment);
		textPageNumber.setText("Page "+(number+1)+" of "+pageCount);

		//display history data
		int start = number * NUM_ITEMS_PAGE;
		int i=start;

		while(i<(start)+NUM_ITEMS_PAGE){
			if(i<photos.length){

				tableRow = inflater.inflate(R.layout.table_item, null, false);

				TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
				TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
				TextView history_gps  = (TextView) tableRow.findViewById(R.id.history_gps);
				TextView history_uploaded  = (TextView) tableRow.findViewById(R.id.history_uploaded);

				history_id.setText(photos[i][0]);
				history_filename.setText(photos[i][1]);
				history_gps.setText(photos[i][2]);
				history_uploaded.setText(photos[i][3]);
				//history_uploaded.setText("");

				i++;
				tableLayout.addView(tableRow);
			}else{
				break;
			}
		}

		if(checkEnablePrevious()){buttonPrev.setEnabled(true);}else{buttonPrev.setEnabled(false);}
		if(checkEnableNext()){buttonNext.setEnabled(true);}else{buttonNext.setEnabled(false);}

		return rootView;
	}

	//Method checking the number of pages
	public int numberPages(int increment){
		int val = totalPhotos%NUM_ITEMS_PAGE;
		val = val==0?0:1;
		pageCount = totalPhotos/NUM_ITEMS_PAGE+val;
		return pageCount;
	}

	//Method for enabling and disabling Buttons Previous
	public Boolean checkEnablePrevious(){
		if(increment == 0)
			return false;
		else
			return true;
	}

	//Method for enabling and disabling Buttons Next
	public Boolean checkEnableNext(){
		if(increment+1 == pageCount)
			return false;
		else
			return true;
	}

}