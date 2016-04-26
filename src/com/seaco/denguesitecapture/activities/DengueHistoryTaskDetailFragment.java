package com.seaco.denguesitecapture.activities;

import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seaco.denguesitecapture.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class DengueHistoryTaskDetailFragment extends Fragment {

	private static final String TAG = "Task History-Detail";
	Context context;
	Bitmap bitmap;
	ImageView img;
	View tableRow;
	RadioGroup radFilter;
	TableLayout tableLayout, tableLayoutTitle;
	LinearLayout linearLayout;
	public int totalTasks, pageCount, pageCountDisplay;
	public int NUM_ITEMS_PAGE= 16, number= 0, increment = 0;
	private String JSON_STRING;
	public String action = "common";
	String idOfficer, nameOfficer, historyId, filename, gps, reportDate, photoDesc, statusDesc, reportBy;
	RelativeLayout layout_relativeTaskDetail;
	TextView history_id_TaskDetail, history_filename_TaskDetail, history_gps_TaskDetail, 
	history_datereport_TaskDetail, history_uploaded_TaskDetail, history_username_TaskDetail;
	ImageButton imageButtonBack, imageButtonImage;


	public DengueHistoryTaskDetailFragment(String id2, String name2, String historyID2, String filename2, String gps2, String reportDate2, String reportBy2, String photoDesc2) {
		nameOfficer = name2;
		idOfficer = id2;
		historyId = historyID2;
		filename = filename2;
		gps = gps2;
		reportDate = reportDate2;
		reportBy = reportBy2;
		photoDesc = photoDesc2;
	}

	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

		final View rootView = inflater.inflate(R.layout.table_historytask_detail, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);
		layout_relativeTaskDetail = (RelativeLayout)rootView.findViewById(R.id.layout_relativeTaskDetail);

		context = container.getContext();

		//set info 
		history_id_TaskDetail=(TextView)rootView.findViewById(R.id.history_id_TaskDetail);
		history_filename_TaskDetail=(TextView)rootView.findViewById(R.id.history_filename_TaskDetail);
		history_gps_TaskDetail=(TextView)rootView.findViewById(R.id.history_gps_TaskDetail);
		history_datereport_TaskDetail=(TextView)rootView.findViewById(R.id.history_datereport_TaskDetail);
		history_uploaded_TaskDetail=(TextView)rootView.findViewById(R.id.history_uploaded_TaskDetail);
		history_username_TaskDetail = (TextView)rootView.findViewById(R.id.history_username_TaskDetail);

		history_id_TaskDetail.setText(historyId);
		history_filename_TaskDetail.setText(filename);
		history_gps_TaskDetail.setText(gps);
		history_datereport_TaskDetail.setText(reportDate);
		history_uploaded_TaskDetail.setText(photoDesc);
		history_username_TaskDetail.setText(reportBy);

		/* temporary using before implement server database
		 * 
		    final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			//sql
			MySQLiteHelper db = new MySQLiteHelper(getActivity());
			final String[][] photos = db.getPhotosResult();

			final MySQLiteHelper db1 = new MySQLiteHelper(getActivity());
		 *
		 */

		//button prev, next, back
		final Button buttonPrev = (Button) rootView.findViewById(R.id.buttonPrevious);
		final Button buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
		imageButtonBack = (ImageButton) rootView.findViewById(R.id.imageButtonBack);
		imageButtonImage = (ImageButton) rootView.findViewById(R.id.imageButtonImage);
		
		buttonPrev.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				action = "prev";
				increment--;
				getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, historyId) ;

			}
		});


		buttonNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				action = "next";
				increment++;
				getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, historyId);

			}
		});

		//put back and image
		imageButtonBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				DengueHistoryTaskFragment dengueHistoryTaskFragment= new DengueHistoryTaskFragment();
				FragmentManager fm = getFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				//passing value from activity to fragment
				Bundle bundle=new Bundle();
				bundle.putString("userName", nameOfficer);
				bundle.putString("userID", idOfficer);
				dengueHistoryTaskFragment.setArguments(bundle);

				//remove layout
				layout_relativeTaskDetail.removeAllViews();
				layout_relativeTaskDetail.refreshDrawableState();

				//replace layout
				fragmentTransaction.replace(R.id.layout_relativeTaskDetail, dengueHistoryTaskFragment);
				fragmentTransaction.commit();

			}
		});

		//view large image when click on picture
		imageButtonImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG,"Enter enlarge picture");
				// custom dialog
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.dialog_custom_image);
				dialog.setTitle("View Image");

				// set the custom dialog components - text, image and button
				img = (ImageView) dialog.findViewById(R.id.image);

				new LoadImage().execute("https://storage.googleapis.com/seaco-storage1/dengueapps/"+filename);

				dialog.show();
			}
		});


		getJSON(rootView, inflater, action, increment, buttonPrev, buttonNext, historyId);
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
	private void getJSON(final View rootView, final LayoutInflater inflater, final String action, final int increment, final Button buttonPrev, final Button buttonNext, final String historyId) {

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
					showTaskPrevious(rootView, inflater, action, increment, buttonPrev, buttonNext, historyId);
				}else if(action.equalsIgnoreCase("next")){
					Log.d(TAG,"getJSON: "+action);
					showTaskNext(rootView, inflater,increment, buttonPrev, buttonNext, historyId);
				}else{
					Log.d(TAG,"getJSON: "+action);
					showTaskCommon(rootView, inflater, buttonPrev, buttonNext, historyId);
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				Log.d(TAG,"doInBackground taskDetail historyId: "+historyId);
				String s = rh.sendGetRequestParam(Config.URL_GET_DETAIL_TASK, historyId);
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
					String status = jo.getString(Config.TAG_TASK_TASKDECISION);
					String comment = jo.getString(Config.TAG_TASK_TASKDESC);

					tableRow = inflater.inflate(R.layout.table_historytask_detail_item, null, false);

					TextView task_id  = (TextView) tableRow.findViewById(R.id.task_id);
					TextView task_updatedate  = (TextView) tableRow.findViewById(R.id.task_updatedate);
					TextView task_status  = (TextView) tableRow.findViewById(R.id.task_status);
					TextView task_comment  = (TextView) tableRow.findViewById(R.id.task_comment);

					task_id.setText(taskID);
					task_updatedate.setText(claimDate+" - "+officerName);

					//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
					task_status.setText(statusDesc(status));

					task_comment.setText(comment);

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

				Log.d(TAG,"showTaskCommon totalTasks j: "+j);

				JSONObject jo = result.getJSONObject(j);
				final String taskID = jo.getString(Config.TAG_TASK_TASKID);
				final String officerName = jo.getString(Config.TAG_TASK_OFFICERNAME);
				String claimDate = jo.getString(Config.TAG_TASK_CLAIMDATE);
				String status = jo.getString(Config.TAG_TASK_TASKDECISION);
				String comment = jo.getString(Config.TAG_TASK_TASKDESC);

				tableRow = inflater.inflate(R.layout.table_historytask_detail_item, null, false);

				TextView task_id  = (TextView) tableRow.findViewById(R.id.task_id);
				TextView task_updatedate  = (TextView) tableRow.findViewById(R.id.task_updatedate);
				TextView task_status  = (TextView) tableRow.findViewById(R.id.task_status);
				TextView task_comment  = (TextView) tableRow.findViewById(R.id.task_comment);

				task_id.setText(taskID);
				task_updatedate.setText(claimDate+" - "+officerName);

				//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
				task_status.setText(statusDesc(status));

				task_comment.setText(comment);

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

					Log.d(TAG,"showTaskCommon totalTasks i: "+j);

					JSONObject jo = result.getJSONObject(j);
					final String taskID = jo.getString(Config.TAG_TASK_TASKID);
					final String officerName = jo.getString(Config.TAG_TASK_OFFICERNAME);
					String claimDate = jo.getString(Config.TAG_TASK_CLAIMDATE);
					String status = jo.getString(Config.TAG_TASK_TASKDECISION);
					String comment = jo.getString(Config.TAG_TASK_TASKDESC);

					tableRow = inflater.inflate(R.layout.table_historytask_detail_item, null, false);

					TextView task_id  = (TextView) tableRow.findViewById(R.id.task_id);
					TextView task_updatedate  = (TextView) tableRow.findViewById(R.id.task_updatedate);
					TextView task_status  = (TextView) tableRow.findViewById(R.id.task_status);
					TextView task_comment  = (TextView) tableRow.findViewById(R.id.task_comment);

					task_id.setText(taskID);
					task_updatedate.setText(claimDate+" - "+officerName);

					//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
					task_status.setText(statusDesc(status));

					task_comment.setText(comment);

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

	//display image
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//pDialog = new ProgressDialog(TestDisplaySinglePhoto.this);
			//pDialog.setMessage("Loading Image ....");
			//pDialog.show();

		}
		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {

			if(image != null){
				// set the custom dialog components - text, image and button
				img.setImageBitmap(image);
				//pDialog.dismiss();

			}else{

				//pDialog.dismiss();
				//Toast.makeText(TestDisplaySinglePhoto.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

			}
		}
	}

	//status description
	public String statusDesc(String status){
		//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
		if(status.equals("1")){
			statusDesc ="Viewed";
		}else if(status.equals("2")){
			statusDesc ="In Process";
		}else if(status.equals("3")){
			statusDesc ="Resolved";
		}else if(status.equals("0")){
			statusDesc ="Rejected";
		}else{
			statusDesc ="New";
		}
		return statusDesc;
	}
}