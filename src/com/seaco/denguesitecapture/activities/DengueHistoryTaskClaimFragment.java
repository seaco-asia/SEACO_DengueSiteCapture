package com.seaco.denguesitecapture.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.seaco.denguesitecapture.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.Toast;

public class DengueHistoryTaskClaimFragment extends Fragment {

	private static final String TAG = "DengueHistoryTaskClaimFragment";
	String historyId;
	String decisionOfficer;
	private String valDecision;
	RadioButton radBtnViewed, radBtnInProcess, radBtnResolved, radBtnRejected;
	EditText txtOfficer, txtHistoryID, txtFilename, txtComment;
	RadioGroup radDecision;
	String  emailTaskOfficer, nameOfficer, email2, historyFilename, idOfficer, filename, comment, descriptionOfficer, name2, section2;
	ProgressDialog progress;
	private TableLayout tableLayout;

	public DengueHistoryTaskClaimFragment(String email2, String historyID2, String filename2, String name2, String id2, String sections) {
		emailTaskOfficer = email2;
		historyId = historyID2;
		filename = filename2;
		nameOfficer = name2;
		idOfficer = id2;
		section2 = sections;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.historytask_dialog, container, false);
		tableLayout=(TableLayout)rootView.findViewById(R.id.tableLayout);

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

		radBtnViewed = (RadioButton) rootView.findViewById(R.id.radBtnViewed);
		radBtnInProcess = (RadioButton) rootView.findViewById(R.id.radBtnInProcess);
		radBtnResolved = (RadioButton) rootView.findViewById(R.id.radBtnResolved);
		radBtnRejected = (RadioButton) rootView.findViewById(R.id.radBtnRejected);
		
		txtOfficer = (EditText) rootView.findViewById(R.id.txtOfficer);
		txtHistoryID = (EditText) rootView.findViewById(R.id.txtHistoryID);
		txtFilename = (EditText) rootView.findViewById(R.id.txtFilename);
		txtComment = (EditText) rootView.findViewById(R.id.txtComment);
		radDecision = (RadioGroup) rootView.findViewById(R.id.radDecision);

		txtOfficer.setEnabled(false);
		txtHistoryID.setEnabled(false);
		txtFilename.setEnabled(false);

		txtOfficer.setText(nameOfficer);//extract name of officer
		txtHistoryID.setText(historyId);//extract ID of picture
		txtFilename.setText(filename); //extract filename of picture
		//txtComment.setText(descriptionOfficer); //extract description from officer

		//checkDecision(rootView, decisionOfficer);

		Button save = (Button) rootView.findViewById(R.id.btnSave);
		Button cancel = (Button) rootView.findViewById(R.id.btnCancel);

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


		//save and cancel function
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (validateSaveTask(getView())) {

					nameOfficer = txtOfficer.getText().toString();//get name of officer
					historyId = txtHistoryID.getText().toString();//get ID of picture
					filename = txtFilename.getText().toString(); //get filename of picture
					comment = txtComment.getText().toString();//get comment of picture

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
									if(section2.equals("new")){
										DengueNewTaskFragment dengueNewTaskFragment = new DengueNewTaskFragment(emailTaskOfficer, nameOfficer, idOfficer);
										FragmentManager fm = getFragmentManager();
										FragmentTransaction fragmentTransaction = fm.beginTransaction();
										fragmentTransaction.replace(R.id.tableLayout, dengueNewTaskFragment);
										fragmentTransaction.commit();
									}else{
										DengueHistoryTaskFragment dengueHistoryTaskFragment = new DengueHistoryTaskFragment(emailTaskOfficer, nameOfficer, idOfficer);
										FragmentManager fm = getFragmentManager();
										FragmentTransaction fragmentTransaction = fm.beginTransaction();
										fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment);
										fragmentTransaction.commit();
									}

								}


							}, 3000);
				}
			}

		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(section2.equals("new")){
					DengueNewTaskFragment dengueNewTaskFragment = new DengueNewTaskFragment(emailTaskOfficer, nameOfficer, idOfficer);
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					fragmentTransaction.replace(R.id.tableLayout, dengueNewTaskFragment);
					fragmentTransaction.commit();
				}else{
					DengueHistoryTaskFragment dengueHistoryTaskFragment = new DengueHistoryTaskFragment(emailTaskOfficer, nameOfficer, idOfficer);
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					//				fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment).addToBackStack(null);
					fragmentTransaction.replace(R.id.tableLayout, dengueHistoryTaskFragment);
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

}
