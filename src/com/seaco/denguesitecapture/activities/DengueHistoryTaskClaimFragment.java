package com.seaco.denguesitecapture.activities;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.seaco.denguesitecapture.R;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DengueHistoryTaskClaimFragment extends Fragment {

	private static final String TAG = "DengueHistoryTaskClaimFragment";

	String historyId, decisionOfficer, valDecision, reportBy;
	RadioButton radBtnViewed, radBtnInProcess, radBtnResolved, radBtnRejected;
	EditText txtOfficer, txtComment, txtOffComment;
	TextView txtFilename, txtHistoryID, txtGPS, txtReportDate, txtCommComment, txtReportBy, txtOffName;
	RadioGroup radDecision;
	String  emailTaskOfficer, nameOfficer, email2, historyFilename, idOfficer, filename, comment, 
	descriptionOfficer, name2, section, gps, reportDate, photoDesc;
	ProgressDialog progress;
	TableLayout tableLayout;
	LinearLayout linearLayoutTaskDialog;
	RelativeLayout layout_relativeDialog;
	Bitmap bitmap;
	ImageView img;
	Context context;

	public DengueHistoryTaskClaimFragment(String id2, String name2,  String historyID2, String filename2, String gps2, String reportDate2, String reportBy2, String photoDesc2, String sections2) {
		//emailTaskOfficer = email2;
		nameOfficer = name2;
		idOfficer = id2;
		historyId = historyID2;
		filename = filename2;
		gps = gps2;
		reportDate = reportDate2;
		reportBy = reportBy2;
		photoDesc = photoDesc2;
		section = sections2;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.historytask_dialog, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);
		layout_relativeDialog = (RelativeLayout)rootView.findViewById(R.id.layout_relativeDialog);

		context = container.getContext();

		Log.d(TAG, "officer's email["+emailTaskOfficer+"] AND idOfficer["+idOfficer+"]");

		/* temporary using before implement server database
		 * 
			//db
			final MySQLiteHelper db = new MySQLiteHelper(getActivity());

			nameOfficer = db.getUsername(emailTaskOfficer); //extract name of officer
			idOfficer = db.getUserID(emailTaskOfficer);//extract userID of officer
			//		decisionOfficer =  db.getTaskDecision(Integer.parseInt(historyId));//extract decision of officer
			//		descriptionOfficer =  db.getTaskDesc(Integer.parseInt(historyId));//extract description of office
		 *
		 */

		//txtOfficer = (EditText) rootView.findViewById(R.id.txtOfficer);
		txtHistoryID = (TextView) rootView.findViewById(R.id.history_id_dialog);
		txtFilename = (TextView) rootView.findViewById(R.id.history_filename_dialog);
		txtGPS = (TextView) rootView.findViewById(R.id.history_gps_dialog);
		txtReportDate = (TextView) rootView.findViewById(R.id.history_datereport_dialog);
		txtReportBy = (TextView) rootView.findViewById(R.id.history_reportby_dialog);

		radDecision = (RadioGroup) rootView.findViewById(R.id.radDecision);
		radBtnViewed = (RadioButton) rootView.findViewById(R.id.radBtnViewed);
		radBtnInProcess = (RadioButton) rootView.findViewById(R.id.radBtnInProcess);
		radBtnResolved = (RadioButton) rootView.findViewById(R.id.radBtnResolved);
		radBtnRejected = (RadioButton) rootView.findViewById(R.id.radBtnRejected);

		txtCommComment = (TextView) rootView.findViewById(R.id.history_uploaded_dialog);
		txtOffComment = (EditText) rootView.findViewById(R.id.history_commentOfficer_dialog);
		txtOffName = (TextView) rootView.findViewById(R.id.history_nameofficer_dialog);

		txtHistoryID.setText(historyId);	//extract ID of picture
		txtFilename.setText(filename); 		//extract filename of picture
		txtGPS.setText(gps); 				//extract gps of picture
		txtReportDate.setText(reportDate);  //extract reportdate of picture
		txtReportBy.setText(reportBy);		//extract reportBy of picture
		txtCommComment.setText(photoDesc);  //extract description of picture
		txtOffName.setText(nameOfficer);	//extract name of officer

		//set image
		//new LoadImage().execute("https://storage.googleapis.com/dengue-seaco/"+filename);

		//checkDecision(rootView, decisionOfficer);

		ImageButton imageButtonSave = (ImageButton) rootView.findViewById(R.id.imageButtonSave);
		ImageButton imageButtonBack = (ImageButton) rootView.findViewById(R.id.imageButtonBack);
		ImageButton imageButtonImage = (ImageButton) rootView.findViewById(R.id.imageButtonImage);

		//approve and rejected function
		radDecision.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radBtnViewed:
					valDecision = "1"; //set decision value
					break;

				case R.id.radBtnInProcess:
					// do operations specific to this selection
					valDecision = "2"; //set decision value
					break;
				case R.id.radBtnResolved:
					valDecision = "3"; //set decision value
					break;

				case R.id.radBtnRejected:
					// do operations specific to this selection
					valDecision = "0"; //set decision value
					break;

				}
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

				new LoadImage().execute("https://storage.googleapis.com/dengue-seaco/"+filename);

				dialog.show();
			}
		});


		//save and cancel function
		imageButtonSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (validateSaveTask(getView())) {

					comment = txtOffComment.getText().toString();//get comment of picture

					final ProgressDialog progressDialog = new ProgressDialog(getActivity());
					progressDialog.setIndeterminate(true);
					progressDialog.setMessage("Saving...");
					progressDialog.show();

					new android.os.Handler().postDelayed(
							new Runnable() {
								public void run() {

									/* temporary using before implement server database
									 * db.addTasks(new TaskHistory(Integer.parseInt(historyId), filename, Integer.parseInt(idOfficer), valDecision, comment, getDateTime()));
									 * 
									 */

									//insert task detail
									addTask(historyId, filename, idOfficer, valDecision, comment, progressDialog);

									//fix back to task history after save
									if(section.equals("new")){

										DengueNewTaskFragment dengueNewTaskFragment = new DengueNewTaskFragment();
										FragmentManager fm = getFragmentManager();
										FragmentTransaction fragmentTransaction = fm.beginTransaction();

										//passing value from activity to fragment
										Bundle bundle=new Bundle();
										bundle.putString("userName", nameOfficer);
										bundle.putString("userID", idOfficer);
										dengueNewTaskFragment.setArguments(bundle);

										//remove layout
										layout_relativeDialog.removeAllViews();
										layout_relativeDialog.refreshDrawableState();

										//replace layout
										fragmentTransaction.replace(R.id.layout_relativeDialog, dengueNewTaskFragment);
										fragmentTransaction.commit();

									}else{

										/*DengueHistoryTaskFragment dengueHistoryTaskFragment = new DengueHistoryTaskFragment(nameOfficer, idOfficer);
										FragmentManager fm = getFragmentManager();
										FragmentTransaction fragmentTransaction = fm.beginTransaction();
										fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment);
										fragmentTransaction.commit();*/

										DengueHistoryTaskFragment dengueHistoryTaskFragment= new DengueHistoryTaskFragment();
										FragmentManager fm = getFragmentManager();
										FragmentTransaction fragmentTransaction = fm.beginTransaction();

										//passing value from activity to fragment
										Bundle bundle=new Bundle();
										bundle.putString("userName", nameOfficer);
										bundle.putString("userID", idOfficer);
										dengueHistoryTaskFragment.setArguments(bundle);

										//remove layout
										layout_relativeDialog.removeAllViews();
										layout_relativeDialog.refreshDrawableState();

										//replace layout
										fragmentTransaction.replace(R.id.layout_relativeDialog, dengueHistoryTaskFragment);
										fragmentTransaction.commit();
									}

								}


							}, 3000);
				}
			}

		});

		imageButtonBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(section.equals("new")){

					DengueNewTaskFragment dengueNewTaskFragment = new DengueNewTaskFragment();
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();

					//passing value from activity to fragment
					Bundle bundle=new Bundle();
					bundle.putString("userName", nameOfficer);
					bundle.putString("userID", idOfficer);
					dengueNewTaskFragment.setArguments(bundle);

					//remove layout
					layout_relativeDialog.removeAllViews();
					layout_relativeDialog.refreshDrawableState();

					//replace layout
					fragmentTransaction.replace(R.id.layout_relativeDialog, dengueNewTaskFragment);
					fragmentTransaction.commit();

				}else{

					/*DengueHistoryTaskFragment dengueHistoryTaskFragment = new DengueHistoryTaskFragment(nameOfficer, idOfficer);
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					//				fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment).addToBackStack(null);
					fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment);
					fragmentTransaction.commit();*/

					DengueHistoryTaskFragment dengueHistoryTaskFragment= new DengueHistoryTaskFragment();
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();

					//passing value from activity to fragment
					Bundle bundle=new Bundle();
					bundle.putString("userName", nameOfficer);
					bundle.putString("userID", idOfficer);
					dengueHistoryTaskFragment.setArguments(bundle);

					//remove layout
					layout_relativeDialog.removeAllViews();
					layout_relativeDialog.refreshDrawableState();

					//replace layout
					fragmentTransaction.replace(R.id.layout_relativeDialog, dengueHistoryTaskFragment);
					fragmentTransaction.commit();
				}
			}
		});

		return rootView;
	}

	public boolean validateSaveTask(View rootView){

		boolean valid = true;

		radDecision = (RadioGroup) rootView.findViewById(R.id.radDecision);
		radBtnViewed = (RadioButton) rootView.findViewById(R.id.radBtnViewed);
		radBtnInProcess = (RadioButton) rootView.findViewById(R.id.radBtnInProcess);
		radBtnResolved = (RadioButton) rootView.findViewById(R.id.radBtnResolved);
		radBtnRejected = (RadioButton) rootView.findViewById(R.id.radBtnRejected);

		if(radDecision.getCheckedRadioButtonId()<=0){
			Toast.makeText(getActivity(),"Please select decision",0).show();
			radBtnViewed.setError("Please select one of the decision");
			radBtnInProcess.setError("Please select one of the decision");
			radBtnResolved.setError("Please select one of the decision");
			radBtnRejected.setError("Please select one of the decision");
			valid = false;
		}
		return valid;

	}

	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	//Adding new task
	private void addTask(final String historyId, final String filename, final String idOfficer, final String valDecision, final String comment,  final ProgressDialog progressDialog){

		class AddTask extends AsyncTask<Void,Void,String>{

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);

				Log.d(TAG,"onPostExecute addTask");

				progressDialog.setMessage("Save Successfully");
				progressDialog.dismiss();

			}

			@Override
			protected String doInBackground(Void... v) {

				Log.d(TAG,"doInBackground addTask");

				HashMap<String,String> params = new HashMap<String, String>();
				params.put(Config.KEY_TASK_HISTORYID,historyId);
				params.put(Config.KEY_TASK_FILENAME,filename);
				params.put(Config.KEY_TASK_OFFICERID,idOfficer);
				params.put(Config.KEY_TASK_TASKDECISION,valDecision);
				params.put(Config.KEY_TASK_TASKDESC,comment);
				params.put(Config.KEY_TASK_CLAIMDATE,getDateTime());

				Log.d(TAG,historyId);
				Log.d(TAG,filename);
				Log.d(TAG,idOfficer);
				Log.d(TAG,valDecision);
				Log.d(TAG,comment);
				Log.d(TAG,getDateTime());

				RequestHandler rh = new RequestHandler();
				String res = rh.sendPostRequest(Config.URL_ADD_TASK, params);
				return res;
			}
		}

		AddTask addTask = new AddTask();
		addTask.execute();
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

}
