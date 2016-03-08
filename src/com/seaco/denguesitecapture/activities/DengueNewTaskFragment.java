package com.seaco.denguesitecapture.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seaco.denguesitecapture.R;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

public class DengueNewTaskFragment extends Fragment {

	private static final String TAG = "DengueHistoryTaskFragment";
	Context context;
	private TableLayout tableLayout;
	private LinearLayout linearLayout;
	View tableRow;
	public int totalPhotos, pageCount, pageCountDisplay;
	String decisionOfficer;
	public int NUM_ITEMS_PAGE= 10, number= 0, increment = 0;
	String email, name, id, valDecision;
	private String JSON_STRING;
	public String action = "common";
	public String sections = "new";
	RadioGroup radFilter;


	public DengueNewTaskFragment(String userEmail, String userName, String userID) {
		email = userEmail;
		name = userName;
		id = userID;
		// TODO Auto-generated constructor stub
		Log.d(TAG, "officer's email ["+email+"] and officer's name ["+name+"] AND idOfficer["+id+"]");
	}

	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

		final View rootView = inflater.inflate(R.layout.table, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);
		linearLayout = (LinearLayout)rootView.findViewById(R.id.linearLayoutHistory);
//		linearLayouts = (LinearLayout)rootView.findViewById(R.id.linearLayoutHistorys);

		/* temporary using before implement server database
		 * 
		    final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			//sql
			MySQLiteHelper db = new MySQLiteHelper(getActivity());
			final String[][] photos = db.getPhotosResult();

			final MySQLiteHelper db1 = new MySQLiteHelper(getActivity());
		 *
		 */
//		if(sections.equalsIgnoreCase("new")){
//			linearLayouts.setVisibility(View.INVISIBLE);
//		}
		
		//filter radio button
		//radFilter = (RadioGroup) rootView.findViewById(R.id.radFilter);
	

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
		
		
		//approve and rejected function
//		radFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
//		{
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				switch(checkedId){
//				case R.id.buttonAppr:
//					valDecision = "1"; //set decision value
//					getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, sections, valDecision);
//					break;
//
//				case R.id.buttonReject:
//					// do operations specific to this selection
//					valDecision = "0"; //set decision value
//					getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, sections, valDecision);
//					break;
//					
//				case R.id.buttonAll:
//					// do operations specific to this selection
//					valDecision = "00"; //set decision value
//					getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, sections, valDecision);
//					break;
//				}
//			}
//		});
		
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
					String gps = latitude +","+ longitude;
					String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);

					Log.d(TAG,"id ["+historyID+"] and filename ["+filename+"]");

					tableRow = inflater.inflate(R.layout.table_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_gps  = (TextView) tableRow.findViewById(R.id.history_gps);
					TextView history_uploaded  = (TextView) tableRow.findViewById(R.id.history_uploaded);
					TextView history_status  = (TextView) tableRow.findViewById(R.id.history_status);

					Button buttonClaim = (Button) tableRow.findViewById(R.id.btnClaim);
					Button buttonDetail = (Button) tableRow.findViewById(R.id.btnDetail);

					if(sections.equalsIgnoreCase("new")){buttonDetail.setVisibility(View.INVISIBLE);}

					history_id.setText(historyID);
					history_filename.setText(filename);
					history_gps.setText(gps);
					history_uploaded.setText(photoDesc);

					//history_uploaded.setText("");

					//final String historyID=photos[i][0];
					//final String historyFilename = photos[i][1];

					//claim and detail function
					buttonClaim.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {

							Log.d(TAG, "historyID["+id+"] historyFilename["+filename+"]");

							/*Intent intent = new Intent(getActivity().getApplicationContext(), DengueHistoryTaskClaim.class);
							intent.putExtra("email", email);
							intent.putExtra("historyID", historyID);
							intent.putExtra("filename", filename);
							intent.putExtra("name", name);
							intent.putExtra("id", id);
							//container.setContent(intent);
							startActivity(intent);	*/	

							DengueHistoryTaskClaimFragment dengueHistoryTaskClaimFragment = new DengueHistoryTaskClaimFragment(email, historyID, filename, name, id, sections);
							FragmentManager fm = getFragmentManager();
							FragmentTransaction fragmentTransaction = fm.beginTransaction();
							linearLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.refreshDrawableState();
							fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskClaimFragment);
							fragmentTransaction.commit();

						}
					});

					buttonDetail.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {

							DengueHistoryTaskDetailFragment dengueHistoryTaskDetailFragment = new DengueHistoryTaskDetailFragment(historyID, email, name, id);
							FragmentManager fm = getFragmentManager();
							FragmentTransaction fragmentTransaction = fm.beginTransaction();
							linearLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.refreshDrawableState();
							fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskDetailFragment);
							fragmentTransaction.commit();
						}
					});

					/*for temporary commented
					//decisionOfficer = db1.getTaskDecision(Integer.parseInt(photos[i][0]));//extract decision of officer
					//history_status.setText(decisionOfficer);*/

					getTask(historyID, history_status);//extract decision of officer

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
				String gps = latitude +","+ longitude;
				String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);

				Log.d(TAG,"id ["+historyID+"] and filename ["+filename+"]");

				tableRow = inflater.inflate(R.layout.table_item, null, false);

				TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
				TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
				TextView history_gps  = (TextView) tableRow.findViewById(R.id.history_gps);
				TextView history_uploaded  = (TextView) tableRow.findViewById(R.id.history_uploaded);
				TextView history_status  = (TextView) tableRow.findViewById(R.id.history_status);

				history_id.setText(historyID);
				history_filename.setText(filename);
				history_gps.setText(gps);
				history_uploaded.setText(photoDesc);
				//history_uploaded.setText("");

				//final String historyID=photos[j][0];
				//final String historyFilename = photos[j][1];

				Button buttonClaim = (Button) tableRow.findViewById(R.id.btnClaim);
				Button buttonDetail = (Button) tableRow.findViewById(R.id.btnDetail);

				buttonDetail.setVisibility(View.INVISIBLE);

				//claim and detail function
				buttonClaim.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						Log.d(TAG, "historyID["+id+"] historyFilename["+filename+"]");

						DengueHistoryTaskClaimFragment dengueHistoryTaskClaimFragment = new DengueHistoryTaskClaimFragment(email, historyID, filename, name, id, sections);
						FragmentManager fm = getFragmentManager();
						FragmentTransaction fragmentTransaction = fm.beginTransaction();
						linearLayout.removeAllViews(); //remove all view during click on detail
						tableLayout.removeAllViews(); //remove all view during click on detail
						tableLayout.refreshDrawableState();
						fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskClaimFragment);
						fragmentTransaction.commit();

					}
				});

				buttonDetail.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						DengueHistoryTaskDetailFragment dengueHistoryTaskDetailFragment = new DengueHistoryTaskDetailFragment(historyID, email, name, id);
						FragmentManager fm = getFragmentManager();
						FragmentTransaction fragmentTransaction = fm.beginTransaction();
						linearLayout.removeAllViews(); //remove all view during click on detail
						tableLayout.removeAllViews(); //remove all view during click on detail
						tableLayout.refreshDrawableState();
						fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskDetailFragment);
						fragmentTransaction.commit();
					}
				});

				/*for temporary commented
				decisionOfficer = db1.getTaskDecision(Integer.parseInt(photos[j][0]));//extract decision of officer
				history_status.setText(decisionOfficer);*/

				getTask(historyID, history_status);//extract decision of officer

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
					String gps = latitude +","+ longitude;
					String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);

					Log.d(TAG,"id ["+id+"] and filename ["+filename+"]");

					tableRow = inflater.inflate(R.layout.table_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_gps  = (TextView) tableRow.findViewById(R.id.history_gps);
					TextView history_uploaded  = (TextView) tableRow.findViewById(R.id.history_uploaded);
					TextView history_status  = (TextView) tableRow.findViewById(R.id.history_status);

					history_id.setText(historyID);
					history_filename.setText(filename);
					history_gps.setText(gps);
					history_uploaded.setText(photoDesc);
					//history_uploaded.setText("");

					//final String historyID=photos[j][0];
					//final String historyFilename = photos[j][1];

					Button buttonClaim = (Button) tableRow.findViewById(R.id.btnClaim);
					Button buttonDetail = (Button) tableRow.findViewById(R.id.btnDetail);

					if(sections.equalsIgnoreCase("new")){buttonDetail.setVisibility(View.INVISIBLE);}

					//claim and detail function
					buttonClaim.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {

							Log.d(TAG, "historyID["+historyID+"] historyFilename["+filename+"]");

							DengueHistoryTaskClaimFragment dengueHistoryTaskClaimFragment = new DengueHistoryTaskClaimFragment(email, historyID, filename, name, id, sections);
							FragmentManager fm = getFragmentManager();
							FragmentTransaction fragmentTransaction = fm.beginTransaction();
							linearLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.refreshDrawableState();
							fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskClaimFragment);
							fragmentTransaction.commit();

						}
					});

					buttonDetail.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {

							DengueHistoryTaskDetailFragment dengueHistoryTaskDetailFragment = new DengueHistoryTaskDetailFragment(historyID, email, name, id);
							FragmentManager fm = getFragmentManager();
							FragmentTransaction fragmentTransaction = fm.beginTransaction();
							linearLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.removeAllViews(); //remove all view during click on detail
							tableLayout.refreshDrawableState();
							fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskDetailFragment);
							fragmentTransaction.commit();
						}
					});

					/*for temporary commented
					MySQLiteHelper db1 = new MySQLiteHelper(getActivity());
					decisionOfficer = db1.getTaskDecision(Integer.parseInt(photos[j][0]));//extract decision of officer
					history_status.setText(decisionOfficer);*/

					getTask(historyID, history_status);//extract decision of officer


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

	private void getTask(final String historyID, final TextView history_status){

		class GetTask extends AsyncTask<Void,Void,String>{

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String t) {
				super.onPostExecute(t);

				Log.d(TAG,"postExecute getTask");

				JSON_STRING = t;

				try {
					JSONObject jsonObject;
					jsonObject = new JSONObject(t);
					JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
					JSONObject c = result.getJSONObject(0);
					String  decisionOfficer = c.getString(Config.KEY_TASK_TASKDECISION);

					if(decisionOfficer.equals("1")){history_status.setText("Approved");}else if(decisionOfficer.equals("0")){history_status.setText("Rejected");}else{history_status.setText("");}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				String t = rh.sendGetRequestParam(Config.URL_GET_PER_TASK,historyID);
				return t;
			}
		}

		GetTask getTask = new GetTask();
		getTask.execute();
	}

}