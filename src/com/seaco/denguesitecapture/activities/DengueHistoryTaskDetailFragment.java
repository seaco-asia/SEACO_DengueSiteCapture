package com.seaco.denguesitecapture.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;

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
import android.widget.TableLayout;
import android.widget.TextView;

public class DengueHistoryTaskDetailFragment extends Fragment {

	private static final String TAG = "Task History-Detail";
	Context context;
	private TableLayout tableLayout;
	View tableRow;
	public int totalTasks, pageCount, pageCountDisplay, decisionOfficer;
	public int NUM_ITEMS_PAGE= 10, number= 0, increment = 0;
	String email, historyID;
	private String JSON_STRING;
	public String action = "common";
	String  emailTaskOfficer, nameOfficer, idOfficer;
	
	public DengueHistoryTaskDetailFragment(String taskHistoryID, String email2, String name2, String id2) {
		// TODO Auto-generated constructor stub
		historyID = taskHistoryID;
		emailTaskOfficer = email2;
		nameOfficer = name2;
		idOfficer = id2;
	}

	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

		Log.d(TAG,"enter here...");

		final View rootView = inflater.inflate(R.layout.table_historytask_detail, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);


		/* temporary using before implement server database
		 * 
			final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);

			//sql
			MySQLiteHelper db = new MySQLiteHelper(getActivity());
			final String[][] getTaskResult = db.getTaskResult(Integer.parseInt(historyID));
		 *
		 */


		//button prev and next
		final Button buttonPrev = (Button) rootView.findViewById(R.id.buttonPrevious);
		final Button buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
		final Button buttonBack = (Button) rootView.findViewById(R.id.buttonBack);
		
		buttonBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Log.d(TAG,"buttonBack");
				DengueHistoryTaskFragment dengueHistoryTaskFragment = new DengueHistoryTaskFragment(emailTaskOfficer, nameOfficer, idOfficer);
				FragmentManager fm = getFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				//				fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment).addToBackStack(null);
				fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment);
				fragmentTransaction.commit();

			}
		});


		buttonPrev.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				action = "prev";
				increment--;
				getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, historyID) ;

			}
		});


		buttonNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				action = "next";
				increment++;
				getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, historyID);

			}
		});

		getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, historyID);
		return rootView;
	}

	//Method checking the number of pages
	public int numberPages(int increment){
		int val = totalTasks%NUM_ITEMS_PAGE;
		val = val==0?0:1;
		pageCount = totalTasks/NUM_ITEMS_PAGE+val;
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
	private void getJSON(final View rootView, final LayoutInflater inflater, final String action, final int increment, final Button buttonPrev, final Button buttonNext, final String historyID) {

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
					showTaskPrevious(rootView, inflater, action, increment, buttonPrev, buttonNext, historyID);
				}else if(action.equalsIgnoreCase("next")){
					Log.d(TAG,"getJSON: "+action);
					showTaskNext(rootView, inflater,increment, buttonPrev, buttonNext, historyID);
				}else{
					Log.d(TAG,"getJSON: "+action);
					showTaskCommon(rootView, inflater, buttonPrev, buttonNext, historyID);
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				Log.d(TAG,"doInBackground taskDetail historyID: "+historyID);
				String s = rh.sendGetRequestParam(Config.URL_GET_DETAIL_TASK, historyID);
				Log.d(TAG,"doInBackground checkUser"+s);
				return s;
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute();
	}

	//show all data firstime enter this page
	private void showTaskCommon(View rootView, LayoutInflater inflater, final Button buttonPrev, final Button buttonNext, String historyID) {
		JSONObject jsonObject = null;
		//ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			jsonObject = new JSONObject(JSON_STRING);
			JSONArray result = jsonObject
					.getJSONArray(Config.TAG_JSON_ARRAY_TASK);

			//total data
			totalTasks =result.length();

			//display the number of pages
			pageCount = numberPages(increment);
			final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			textPageNumber.setText("Page "+(number+1)+" of "+pageCount);

			//display history data
			int start = number * NUM_ITEMS_PAGE;
			int i=start;
			
			Log.d(TAG,"showTaskCommon totalTasks: "+totalTasks);

			while(i<(start)+NUM_ITEMS_PAGE){
				if(i<totalTasks){
					
					Log.d(TAG,"showTaskCommon totalTasks i: "+i);
					
					JSONObject jo = result.getJSONObject(i);
					final String taskID = jo.getString(Config.TAG_TASK_TASKID);
					final String officerName = jo.getString(Config.TAG_TASK_OFFICERNAME);
					String claimDate = jo.getString(Config.TAG_TASK_CLAIMDATE);
					String decision = jo.getString(Config.TAG_TASK_TASKDECISION);
					String comment = jo.getString(Config.TAG_TASK_TASKDESC);

					tableRow = inflater.inflate(R.layout.table_historytask_detail_item, null, false);

					TextView history_taskID  = (TextView) tableRow.findViewById(R.id.taskID);
					TextView history_officerName  = (TextView) tableRow.findViewById(R.id.officerName);
					TextView history_claimDate  = (TextView) tableRow.findViewById(R.id.claimDate);
					TextView history_decision  = (TextView) tableRow.findViewById(R.id.decision);
					TextView history_comment  = (TextView) tableRow.findViewById(R.id.comment);

					history_taskID.setText(taskID);
					history_officerName.setText(officerName);
					history_claimDate.setText(claimDate);
					
					//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
					if(decision.equals("1")){history_decision.setText("Viewed");}
						else if(decision.equals("2")){history_decision.setText("In Process");}
							else if(decision.equals("3")){history_decision.setText("Resolved");}
								else if(decision.equals("0")){history_decision.setText("Rejected");}
									else{history_decision.setText("");}
					
					history_comment.setText(comment);

					i++;
					tableLayout.addView(tableRow);
				}else{
					break;
				}
			}

			if(checkEnablePrevious()){buttonPrev.setEnabled(true);}else{buttonPrev.setEnabled(false);}
			if(checkEnableNext()){buttonNext.setEnabled(true);}else{buttonNext.setEnabled(false);}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//show all data during click previous button
	private void showTaskPrevious(View rootView, LayoutInflater inflater, String action, int increment, final Button buttonPrev, final Button buttonNext, String historyID) {
		JSONObject jsonObject = null;
		//ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			jsonObject = new JSONObject(JSON_STRING);
			JSONArray result = jsonObject
					.getJSONArray(Config.TAG_JSON_ARRAY_TASK);

			//increment--;
			pageCount = numberPages(increment);

			final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			textPageNumber.setText("Page "+(increment+1)+" of "+pageCount);

			int start = increment * NUM_ITEMS_PAGE;
			int j=start;

			Log.d("DengueHistoryTaskDetailFragment","buttonPrevious");

			tableLayout.removeAllViews();

			while(j<(start)+NUM_ITEMS_PAGE){

				JSONObject jo = result.getJSONObject(j);
				final String taskID = jo.getString(Config.TAG_TASK_TASKID);
				final String officerName = jo.getString(Config.TAG_TASK_OFFICERNAME);
				String claimDate = jo.getString(Config.TAG_TASK_CLAIMDATE);
				String decision = jo.getString(Config.TAG_TASK_TASKDECISION);
				String comment = jo.getString(Config.TAG_TASK_TASKDESC);

				tableRow = inflater.inflate(R.layout.table_historytask_detail_item, null, false);

				TextView history_taskID  = (TextView) tableRow.findViewById(R.id.taskID);
				TextView history_officerName  = (TextView) tableRow.findViewById(R.id.officerName);
				TextView history_claimDate  = (TextView) tableRow.findViewById(R.id.claimDate);
				TextView history_decision  = (TextView) tableRow.findViewById(R.id.decision);
				TextView history_comment  = (TextView) tableRow.findViewById(R.id.comment);

				history_taskID.setText(taskID);
				history_officerName.setText(officerName);
				history_claimDate.setText(claimDate);
				
				//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
				if(decision.equals("1")){history_decision.setText("Viewed");}
					else if(decision.equals("2")){history_decision.setText("In Process");}
						else if(decision.equals("3")){history_decision.setText("Resolved");}
							else if(decision.equals("0")){history_decision.setText("Rejected");}
								else{history_decision.setText("");}
				
				history_comment.setText(comment);

				j++;
				tableLayout.addView(tableRow);
				tableLayout.refreshDrawableState();
			}

			if(checkEnablePrevious()){buttonPrev.setEnabled(true);}else{buttonPrev.setEnabled(false);}
			if(checkEnableNext()){buttonNext.setEnabled(true);}else{buttonNext.setEnabled(false);}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//show all data during click next button
	private void showTaskNext(View rootView, LayoutInflater inflater, int increment, final Button buttonPrev, final Button buttonNext, String historyID) {
		JSONObject jsonObject = null;
		//ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			jsonObject = new JSONObject(JSON_STRING);
			JSONArray result = jsonObject
					.getJSONArray(Config.TAG_JSON_ARRAY_TASK);

			//increment++;
			pageCount = numberPages(increment);

			final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			textPageNumber.setText("Page "+(increment+1)+" of "+pageCount);

			int start = increment * NUM_ITEMS_PAGE;
			int j=start;

			Log.d("DengueHistoryTaskDetailFragment","buttonNext");

			tableLayout.removeAllViews();

			while(j<(start)+NUM_ITEMS_PAGE){
				if(j<result.length()){

					JSONObject jo = result.getJSONObject(j);
					final String taskID = jo.getString(Config.TAG_TASK_TASKID);
					final String officerName = jo.getString(Config.TAG_TASK_OFFICERNAME);
					String claimDate = jo.getString(Config.TAG_TASK_CLAIMDATE);
					String decision = jo.getString(Config.TAG_TASK_TASKDECISION);
					String comment = jo.getString(Config.TAG_TASK_TASKDESC);

					tableRow = inflater.inflate(R.layout.table_historytask_detail_item, null, false);

					TextView history_taskID  = (TextView) tableRow.findViewById(R.id.taskID);
					TextView history_officerName  = (TextView) tableRow.findViewById(R.id.officerName);
					TextView history_claimDate  = (TextView) tableRow.findViewById(R.id.claimDate);
					TextView history_decision  = (TextView) tableRow.findViewById(R.id.decision);
					TextView history_comment  = (TextView) tableRow.findViewById(R.id.comment);

					history_taskID.setText(taskID);
					history_officerName.setText(officerName);
					history_claimDate.setText(claimDate);
					
					//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
					if(decision.equals("1")){history_decision.setText("Viewed");}
						else if(decision.equals("2")){history_decision.setText("In Process");}
							else if(decision.equals("3")){history_decision.setText("Resolved");}
								else if(decision.equals("0")){history_decision.setText("Rejected");}
									else{history_decision.setText("");}
					
					history_comment.setText(comment);

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

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}