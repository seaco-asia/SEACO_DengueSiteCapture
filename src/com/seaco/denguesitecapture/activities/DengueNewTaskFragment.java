package com.seaco.denguesitecapture.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class DengueNewTaskFragment extends Fragment {

	private static final String TAG = "DengueNewTaskFragment";
	Context context;
	Bitmap bitmap;
	ImageView img;
	View tableRow;
	RadioGroup radFilter;
	TableLayout tableLayout, tableLayoutTitle;
	LinearLayout linearLayout;
	public int totalPhotos, pageCount, pageCountDisplay;
	public int NUM_ITEMS_PAGE= 16, number= 0, increment = 0;
	private String JSON_STRING;
	public String action = "common";
	public String sections = "new";
	String email, name, id, valDecision, statusDesc;
	String decisionOfficer;
	RelativeLayout layoutRelative;

	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

		final View rootView = inflater.inflate(R.layout.table_task_title, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);

		context = container.getContext();

		layoutRelative = (RelativeLayout)rootView.findViewById(R.id.layoutRelative);

		//receive parameter from activity
		id=getArguments().getString("userID");
		name=getArguments().getString("userName");

		/* temporary using before implement server database
		 * 
		    final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			//sql
			MySQLiteHelper db = new MySQLiteHelper(getActivity());
			final String[][] photos = db.getPhotosResult();

			final MySQLiteHelper db1 = new MySQLiteHelper(getActivity());
		 *
		 */

		//button prev and next
		final Button buttonPrev = (Button) rootView.findViewById(R.id.buttonPrevious);
		final Button buttonNext = (Button) rootView.findViewById(R.id.buttonNext);

		buttonPrev.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				action = "prev";
				increment--;
				getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext) ;

			}
		});


		buttonNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				action = "next";
				increment++;
				getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext);

			}
		});

		getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext);
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

	// To retrieve data from database
	private void getJSON(final View rootView, final LayoutInflater inflater, final String action, final int increment, final Button buttonPrev, final Button buttonNext) {

		class GetJSON extends AsyncTask<Void, Void, String> {

			// ProgressDialog loading;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// loading =
				// ProgressDialog.show(ViewAllEmployee.this,"Fetching Data","Wait...",false,false);
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				// loading.dismiss();
				JSON_STRING = s;

				if(action.equalsIgnoreCase("prev")){
					Log.d(TAG,"getJSON: "+action);
					showPhotosPrevious(rootView, inflater, action, increment, buttonPrev, buttonNext, sections);
				}else if(action.equalsIgnoreCase("next")){
					Log.d(TAG,"getJSON: "+action);
					showPhotosNext(rootView, inflater,increment, buttonPrev, buttonNext, sections);
				}else{
					Log.d(TAG,"getJSON: "+action);
					showPhotosCommon(rootView, inflater, buttonPrev, buttonNext, sections);
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				String s = rh.sendGetRequest(Config.URL_GET_ALL_PHOTOS);
				return s;
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute();
	}

	//show all data firstime enter this page
	private void showPhotosCommon(View rootView, LayoutInflater inflater, final Button buttonPrev, final Button buttonNext, final String sections) {
		JSONObject jsonObject = null;
		//ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			jsonObject = new JSONObject(JSON_STRING);
			JSONArray result = jsonObject
					.getJSONArray(Config.TAG_JSON_ARRAY_PHOTOS);

			//total data
			totalPhotos =result.length();

			//display the number of pages
			pageCount = numberPages(increment);

			final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			textPageNumber.setText("Page "+(number+1)+" of "+pageCount);

			//display history data
			int start = number * NUM_ITEMS_PAGE;
			int i=start;

			while(i<(start)+NUM_ITEMS_PAGE){
				if(i<result.length()){

					JSONObject jo = result.getJSONObject(i);
					final String historyID = jo.getString(Config.TAG_ID);
					final String filename = jo.getString(Config.TAG_FILENAME);
					String latitude = jo.getString(Config.TAG_LATITUDE);
					String longitude = jo.getString(Config.TAG_LONGITUDE);
					final String gps = latitude +","+ longitude;
					final String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);
					final String reportDate = jo.getString(Config.TAG_INSERT_DATE);
					final String reportBy = jo.getString(Config.TAG_REPORT_BY);

					Log.d(TAG,"id ["+historyID+"] and filename ["+filename+"]");

					tableRow = inflater.inflate(R.layout.table_task_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_reportDate  = (TextView) tableRow.findViewById(R.id.history_reportDate);
					ImageView history_claim  = (ImageView) tableRow.findViewById(R.id.imageView_detail);

					history_id.setText(historyID);
					history_filename.setText(filename);
					history_reportDate.setText(reportDate);

					//click on detail image
					history_claim.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {

							Log.d(TAG, "historyID["+id+"] historyFilename["+filename+"]");

							DengueHistoryTaskClaimFragment dengueHistoryTaskClaimFragment = new DengueHistoryTaskClaimFragment(id, name, historyID, filename, gps, reportDate, reportBy, photoDesc, sections);
							FragmentManager fm = getFragmentManager();
							FragmentTransaction fragmentTransaction = fm.beginTransaction();

							//remove all views
							layoutRelative.removeAllViews();
							layoutRelative.refreshDrawableState();

							//replace views
							fragmentTransaction.replace(R.id.layoutRelative, dengueHistoryTaskClaimFragment);
							fragmentTransaction.commit();
						}
					});

					/*for temporary commented
					decisionOfficer = db1.getTaskDecision(Integer.parseInt(photos[j][0]));//extract decision of officer
					history_status.setText(decisionOfficer);*/

					i++;
					tableLayout.addView(tableRow);
				}else{
					break;
				}
			}

			if(checkEnablePrevious()){buttonPrev.setEnabled(true); Log.d(TAG,"buttonPrevEnable [common] true"); }else{buttonPrev.setEnabled(false); Log.d(TAG,"buttonPrevEnable [common] false");}
			if(checkEnableNext()){buttonNext.setEnabled(true); Log.d(TAG,"buttonNextEnable [common] true");}else{buttonNext.setEnabled(false); Log.d(TAG,"buttonNextEnable [common] false");}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//show all data during click previous button
	private void showPhotosPrevious(View rootView, LayoutInflater inflater, String action, int increment, final Button buttonPrev, final Button buttonNext, final String sections) {
		JSONObject jsonObject = null;
		//ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			jsonObject = new JSONObject(JSON_STRING);
			JSONArray result = jsonObject
					.getJSONArray(Config.TAG_JSON_ARRAY_PHOTOS);

			//increment--;
			pageCount = numberPages(increment);

			final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			textPageNumber.setText("Page "+(increment+1)+" of "+pageCount);

			int start = increment * NUM_ITEMS_PAGE;
			int j=start;

			tableLayout.removeAllViews();

			while(j<(start)+NUM_ITEMS_PAGE){

				JSONObject jo = result.getJSONObject(j);
				final String historyID = jo.getString(Config.TAG_ID);
				final String filename = jo.getString(Config.TAG_FILENAME);
				String latitude = jo.getString(Config.TAG_LATITUDE);
				String longitude = jo.getString(Config.TAG_LONGITUDE);
				final String gps = latitude +","+ longitude;
				final String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);
				final String reportDate = jo.getString(Config.TAG_INSERT_DATE);
				final String reportBy = jo.getString(Config.TAG_REPORT_BY);

				Log.d(TAG,"id ["+historyID+"] and filename ["+filename+"]");

				tableRow = inflater.inflate(R.layout.table_task_item, null, false);

				TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
				TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
				TextView history_reportDate  = (TextView) tableRow.findViewById(R.id.history_reportDate);
				ImageView history_claim  = (ImageView) tableRow.findViewById(R.id.imageView_detail);

				history_id.setText(historyID);
				history_filename.setText(filename);
				history_reportDate.setText(reportDate);

				//click on detail image
				history_claim.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						Log.d(TAG, "historyID["+id+"] historyFilename["+filename+"]");

						DengueHistoryTaskClaimFragment dengueHistoryTaskClaimFragment = new DengueHistoryTaskClaimFragment(id, name, historyID, filename, gps, reportDate, reportBy, photoDesc, sections);
						FragmentManager fm = getFragmentManager();
						FragmentTransaction fragmentTransaction = fm.beginTransaction();

						//remove all views
						layoutRelative.removeAllViews();
						layoutRelative.refreshDrawableState();

						//replace views
						fragmentTransaction.replace(R.id.layoutRelative, dengueHistoryTaskClaimFragment);
						fragmentTransaction.commit();
					}
				});

				/*for temporary commented
				decisionOfficer = db1.getTaskDecision(Integer.parseInt(photos[j][0]));//extract decision of officer
				history_status.setText(decisionOfficer);*/

				j++;
				tableLayout.addView(tableRow);
				tableLayout.refreshDrawableState();
			}

			if(checkEnablePrevious()){buttonPrev.setEnabled(true); 	Log.d(TAG,"buttonPrevEnable [prev] true"); }else{buttonPrev.setEnabled(false); 	Log.d(TAG,"buttonPrevEnable [prev] false");}
			if(checkEnableNext()){buttonNext.setEnabled(true); Log.d(TAG,"buttonNextEnable [prev] true"); }else{buttonNext.setEnabled(false); Log.d(TAG,"buttonNextEnable [prev] false"); }

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//show all data during click next button
	private void showPhotosNext(View rootView, LayoutInflater inflater, int increment, final Button buttonPrev, final Button buttonNext, final String sections) {
		JSONObject jsonObject = null;
		//ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			jsonObject = new JSONObject(JSON_STRING);
			JSONArray result = jsonObject
					.getJSONArray(Config.TAG_JSON_ARRAY_PHOTOS);

			//increment++;
			pageCount = numberPages(increment);

			final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			textPageNumber.setText("Page "+(increment+1)+" of "+pageCount);

			int start = increment * NUM_ITEMS_PAGE;
			int j=start;

			tableLayout.removeAllViews();

			while(j<(start)+NUM_ITEMS_PAGE){
				if(j<result.length()){

					JSONObject jo = result.getJSONObject(j);
					final String historyID = jo.getString(Config.TAG_ID);
					final String filename = jo.getString(Config.TAG_FILENAME);
					String latitude = jo.getString(Config.TAG_LATITUDE);
					String longitude = jo.getString(Config.TAG_LONGITUDE);
					final String gps = latitude +","+ longitude;
					final String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);
					final String reportDate = jo.getString(Config.TAG_INSERT_DATE);
					final String reportBy = jo.getString(Config.TAG_REPORT_BY);

					Log.d(TAG,"id ["+historyID+"] and filename ["+filename+"]");

					tableRow = inflater.inflate(R.layout.table_task_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_reportDate  = (TextView) tableRow.findViewById(R.id.history_reportDate);
					ImageView history_claim  = (ImageView) tableRow.findViewById(R.id.imageView_detail);

					history_id.setText(historyID);
					history_filename.setText(filename);
					history_reportDate.setText(reportDate);

					//click on detail image
					history_claim.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {

							Log.d(TAG, "historyID["+id+"] historyFilename["+filename+"]");

							DengueHistoryTaskClaimFragment dengueHistoryTaskClaimFragment = new DengueHistoryTaskClaimFragment(id, name, historyID, filename, gps, reportDate, reportBy, photoDesc, sections);
							FragmentManager fm = getFragmentManager();
							FragmentTransaction fragmentTransaction = fm.beginTransaction();

							//remove all views
							layoutRelative.removeAllViews();
							layoutRelative.refreshDrawableState();

							//replace views
							fragmentTransaction.replace(R.id.layoutRelative, dengueHistoryTaskClaimFragment);
							fragmentTransaction.commit();
						}
					});

					/*for temporary commented
					decisionOfficer = db1.getTaskDecision(Integer.parseInt(photos[j][0]));//extract decision of officer
					history_status.setText(decisionOfficer);*/

					j++;

					tableLayout.addView(tableRow);
					tableLayout.refreshDrawableState();
					buttonNext.setEnabled(false);

				}else{
					break;
				}
			}

			if(checkEnablePrevious()){buttonPrev.setEnabled(true); Log.d(TAG,"buttonPrevEnable [next] true"); }else{buttonPrev.setEnabled(false); Log.d(TAG,"buttonPrevEnable [next] false");}
			if(checkEnableNext()){buttonNext.setEnabled(true); Log.d(TAG,"buttonNextEnable [next] true"); }else{buttonNext.setEnabled(false); Log.d(TAG,"buttonNextEnable [next] false"); }

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}