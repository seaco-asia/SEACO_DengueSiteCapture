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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

public class DengueHistoryFragment extends Fragment {

	private static final String TAG = "DengueHistoryFragment";
	Context context;
	Bitmap bitmap;
	TableLayout tableLayout;
	View tableRow;
	ImageView img;
	public int totalPhotos, pageCount, pageCountDisplay;
	public int NUM_ITEMS_PAGE = 15, number = 0, increment = 0;
	private String JSON_STRING;
	public String action = "common";
	String email, name, id, statusDesc;

	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, Bundle savedInstanceState) {

		//display background
		final View rootView = inflater.inflate(R.layout.table_history_title, container, false);
		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);

		context = container.getContext();

		//receive parameter from activity
		id=getArguments().getString("userID");
		name=getArguments().getString("userName");

		/* temporary using before implement server database
		 * 
		 	//final TextView textPageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
			// MySQLiteHelper db = new MySQLiteHelper(getActivity());
			// final String[][] photos = db.getPhotosResult();
			//totalPhotos =photos.length;
		 * 
		 */

		// button prev and next
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

	// Method checking the number of pages
	public int numberPages(int increment) {
		int val = totalPhotos % NUM_ITEMS_PAGE;
		val = val == 0 ? 0 : 1;
		pageCount = totalPhotos / NUM_ITEMS_PAGE + val;
		return pageCount;
	}

	// Method for enabling and disabling Buttons Previous
	public Boolean checkEnablePrevious() {
		if (increment == 0){
			Log.d(TAG,"if checkEnablePrevious: "+increment);
			return false;}
		else{
			Log.d(TAG,"else checkEnablePrevious: "+increment);
			return true;}
	}

	// Method for enabling and disabling Buttons Next
	public Boolean checkEnableNext() {
		if (increment + 1 == pageCount)
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
					showPhotosPrevious(rootView, inflater, action, increment, buttonPrev, buttonNext);
				}else if(action.equalsIgnoreCase("next")){
					Log.d(TAG,"getJSON: "+action);
					showPhotosNext(rootView, inflater,increment, buttonPrev, buttonNext);
				}else{
					Log.d(TAG,"getJSON: "+action);
					showPhotosCommon(rootView, inflater, buttonPrev, buttonNext);
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				String s = rh.sendGetRequestParam(Config.URL_GET_PER_PHOTOS, id);
				return s;
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute();
	}

	//show all data firstime enter this page
	private void showPhotosCommon(View rootView, LayoutInflater inflater, final Button buttonPrev, final Button buttonNext) {
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
					final String id = jo.getString(Config.TAG_ID);
					final String filename = jo.getString(Config.TAG_FILENAME);
					String latitude = jo.getString(Config.TAG_LATITUDE);
					String longitude = jo.getString(Config.TAG_LONGITUDE);
					final String gps = latitude +","+ longitude;
					final String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);
					final String status = jo.getString(Config.TAG_STATUS);
					final String commentOfficer = jo.getString(Config.TAG_OFFICER_COMMENT);
					final String reportDate = jo.getString(Config.TAG_INSERT_DATE);

					Log.d(TAG,"id ["+id+"] and filename ["+filename+"]");

					tableRow = inflater.inflate(R.layout.table_history_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_status  = (TextView) tableRow.findViewById(R.id.history_status);
					ImageView history_detail  = (ImageView) tableRow.findViewById(R.id.imageView_detail);

					history_id.setText(id);
					history_filename.setText(filename);
					history_status.setText(status);

					//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
					history_status.setText(statusDesc(status));

					//click on detail image
					history_detail.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							informationDetail(id, filename, gps, photoDesc, status, commentOfficer, reportDate);
						}
					});

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
	private void showPhotosPrevious(View rootView, LayoutInflater inflater, String action, int increment, final Button buttonPrev, final Button buttonNext) {
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
				if(j<result.length()){

					JSONObject jo = result.getJSONObject(j);
					final String id = jo.getString(Config.TAG_ID);
					final String filename = jo.getString(Config.TAG_FILENAME);
					String latitude = jo.getString(Config.TAG_LATITUDE);
					String longitude = jo.getString(Config.TAG_LONGITUDE);
					final String gps = latitude +","+ longitude;
					final String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);
					final String status = jo.getString(Config.TAG_STATUS);
					final String commentOfficer = jo.getString(Config.TAG_OFFICER_COMMENT);
					final String reportDate = jo.getString(Config.TAG_INSERT_DATE);

					Log.d(TAG,"id ["+id+"] and filename ["+filename+"]");

					tableRow = inflater.inflate(R.layout.table_history_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_status  = (TextView) tableRow.findViewById(R.id.history_status);
					ImageView history_detail  = (ImageView) tableRow.findViewById(R.id.imageView_detail);

					history_id.setText(id);
					history_filename.setText(filename);

					//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
					history_status.setText(statusDesc(status));

					//click on detail image
					history_detail.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							informationDetail(id, filename, gps, photoDesc, status, commentOfficer, reportDate);
						}
					});


					j++;

					tableLayout.addView(tableRow);
					tableLayout.refreshDrawableState();
				}else{
					break;
				}
			}

			if(checkEnablePrevious()){buttonPrev.setEnabled(true); 	Log.d(TAG,"buttonPrevEnable [prev] true"); }else{buttonPrev.setEnabled(false); 	Log.d(TAG,"buttonPrevEnable [prev] false");}
			if(checkEnableNext()){buttonNext.setEnabled(true); Log.d(TAG,"buttonNextEnable [prev] true"); }else{buttonNext.setEnabled(false); Log.d(TAG,"buttonNextEnable [prev] false"); }

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//show all data during click next button
	private void showPhotosNext(View rootView, LayoutInflater inflater, int increment, final Button buttonPrev, final Button buttonNext) {
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
					final String id = jo.getString(Config.TAG_ID);
					final String filename = jo.getString(Config.TAG_FILENAME);
					String latitude = jo.getString(Config.TAG_LATITUDE);
					String longitude = jo.getString(Config.TAG_LONGITUDE);
					final String gps = latitude +","+ longitude;
					final String photoDesc = jo.getString(Config.TAG_PHOTO_DESC);
					final String status = jo.getString(Config.TAG_STATUS);
					final String commentOfficer = jo.getString(Config.TAG_OFFICER_COMMENT);
					final String reportDate = jo.getString(Config.TAG_INSERT_DATE);

					Log.d(TAG,"id ["+id+"] and filename ["+filename+"]");

					tableRow = inflater.inflate(R.layout.table_history_item, null, false);

					TextView history_id  = (TextView) tableRow.findViewById(R.id.history_id);
					TextView history_filename  = (TextView) tableRow.findViewById(R.id.history_filename);
					TextView history_status  = (TextView) tableRow.findViewById(R.id.history_status);
					ImageView history_detail  = (ImageView) tableRow.findViewById(R.id.imageView_detail);

					history_id.setText(id);
					history_filename.setText(filename);
					history_status.setText(status);

					//0 = Rejected;  1 = Viewed;  2 = In Process ; 3 = Resolved
					history_status.setText(statusDesc(status));

					//click on detail image
					history_detail.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							informationDetail(id, filename, gps, photoDesc, status, commentOfficer, reportDate);
						}
					});

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

	public void informationDetail(String id, String filename, String gps, String photoDesc, String status, String commentOfficer, String reportDate){

		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_custom);
		dialog.setTitle("Detail Information");

		// set the custom dialog components - text, image and button
		img = (ImageView) dialog.findViewById(R.id.image);

		TextView history_id_dialog  = (TextView) dialog.findViewById(R.id.history_id_dialog);
		TextView history_filename_dialog  = (TextView) dialog.findViewById(R.id.history_filename_dialog);
		TextView history_gps_dialog  = (TextView) dialog.findViewById(R.id.history_gps_dialog);
		TextView history_datereport_dialog  = (TextView) dialog.findViewById(R.id.history_datereport_dialog);
		TextView history_status_dialog  = (TextView) dialog.findViewById(R.id.history_status_dialog);
		TextView history_uploaded_dialog  = (TextView) dialog.findViewById(R.id.history_uploaded_dialog);
		TextView history_commentofficer_dialog  = (TextView) dialog.findViewById(R.id.history_commentofficer_dialog);

		history_id_dialog.setText(id);
		history_filename_dialog.setText(filename);
		history_gps_dialog.setText(gps);
		history_datereport_dialog.setText(reportDate);
		history_status_dialog.setText(statusDesc(status));
		history_uploaded_dialog.setText(photoDesc);
		history_commentofficer_dialog.setText(!commentOfficer.equals("null")?commentOfficer:"");

		new LoadImage().execute("https://storage.googleapis.com/dengue-seaco/"+filename);

		dialog.show();

	}

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
