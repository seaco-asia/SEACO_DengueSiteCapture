package com.seaco.denguesitecapture.activities;

import java.util.HashMap;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;

public class Signup extends Activity implements OnClickListener {

	private static final String TAG = "Signup";
	private Button  btnCreateAccount, btnBack;
	private MySQLiteHelper db;
	String userName,userEmail,userPassword,userRegtype, actionType;
	View signupLayout;
	public String JSON_STRING;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_page);

		//button create Account, back
		btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		btnCreateAccount.setOnClickListener(this);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);

		btnBack.setOnClickListener(this);

		/* temporary using before implement server database
		 * 
			db = new MySQLiteHelper(this);
			final String[][] users = db.getUserResult();
			Log.d(TAG,"retrieve data users......"+users.length);
		 *	
		 */
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btnCreateAccount:
			createAccount();
			Log.d(TAG,"finish create account");
			break;
		case R.id.btnBack:
			back();
			Log.d(TAG,"finish back");
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//Inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void createAccount(){

		Log.d(TAG,"enter createAccount method");

		actionType = "S";//SIGNUP

		if (!validateSignup()) {
			Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(Signup.this,R.style.AppBaseTheme);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Creating Account...");
		progressDialog.show();

		//textfield signup
		EditText nameEditText = (EditText)findViewById(R.id.signup_name);
		EditText emailEditText = (EditText)findViewById(R.id.signup_email);
		EditText passEditText = (EditText)findViewById(R.id.signup_password);

		userName = nameEditText.getText().toString();
		userEmail = emailEditText.getText().toString();
		userPassword = passEditText.getText().toString();
		userRegtype = "COM";// COM= community, AO= agency officer

		Log.d(TAG, "userName ["+userName+"] and userEmail ["+userEmail+"] and userPassword ["+userPassword+"]");

		// TODO: Implement your own authentication logic here.
		new android.os.Handler().postDelayed(
				new Runnable() {
					public void run() {
						//check user if their email already have at db
						checkUser(userName,userEmail, userPassword, userRegtype, progressDialog);
					}
					//					}
				}, 3000);

	}

	private void back() {
		Intent i = new Intent(Signup.this, Login.class);
		startActivity(i);
	}

	public boolean validateSignup() {

		Log.d(TAG,"enter validate signup method");

		boolean valid = true;

		//textfield signup
		EditText nameEditText = (EditText)findViewById(R.id.signup_name);
		EditText emailEditText = (EditText)findViewById(R.id.signup_email);
		EditText passEditText = (EditText)findViewById(R.id.signup_password);

		userName = nameEditText.getText().toString();
		userEmail = emailEditText.getText().toString();
		userPassword = passEditText.getText().toString();

		Log.d(TAG, "userName ["+userName+"] and userEmail ["+userEmail+"] and userPassword ["+userPassword+"]");

		if (userName.isEmpty() || userName.length() < 3) {
			nameEditText.setError("Please enter at least 3 characters");
			valid = false;
		}else{
			nameEditText.setError(null);
		}

		if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
			emailEditText.setError("Please enter your email");
			valid = false;
		}else{
			emailEditText.setError(null);
		}

		if (userPassword.isEmpty() || userPassword.length() < 4 || userPassword.length() > 10) {
			passEditText.setError("Please enter between 4 and 10 alphanumeric characters");
			valid = false;
		}else{
			passEditText.setError(null);
		}
		return valid;
	}

	public void emptyInputVal(String actionType){

		if(actionType.equalsIgnoreCase("L")){
			//textfield login
			EditText emailLoginEditText = (EditText)findViewById(R.id.login_email);
			EditText passLoginEditText = (EditText)findViewById(R.id.login_password);

			emailLoginEditText.setText("");
			passLoginEditText.setText("");
		}else{
			//textfield signup
			EditText nameSignupEditText = (EditText)findViewById(R.id.signup_name);
			EditText emailSignupEditText = (EditText)findViewById(R.id.signup_email);
			EditText passSignupEditText = (EditText)findViewById(R.id.signup_password);

			nameSignupEditText.setText("");
			emailSignupEditText.setText("");
			passSignupEditText.setText("");
		}
	}

	private void checkUser(final String userName, final String userEmail, final String userPassword, final String userRegtype, final ProgressDialog progressDialog){

		class CheckUser extends AsyncTask<Void,Void,String>{

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);

				Log.d(TAG,"postExecute checkUser");

				JSON_STRING = s;
			
					if(s.contains("success")){
						progressDialog.dismiss();
						Toast.makeText(getBaseContext(), "Signup failed. You ID is already exist.", Toast.LENGTH_LONG).show();
						emptyInputVal(actionType);
					}else{
						
						//add if the data not already in db
						addUser(userName, userEmail, userPassword, userRegtype, progressDialog);
					}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				
				HashMap<String,String> paramUser = new HashMap<String, String>();
				paramUser.put(Config.KEY_USER_EMAIL,userEmail);
				paramUser.put(Config.KEY_USER_PASSWORD,userPassword);
				
				String s = rh.sendPostRequest(Config.URL_CHECK_USER,paramUser);
				Log.d(TAG,"doInBackground ["+s+"]");
				return s;
			}
		}
		CheckUser checkUser = new CheckUser();
		checkUser.execute();
	}

	//Adding new user
	private void addUser(final String userName, final String userEmail, final String userPassword, final String userRegtype, final ProgressDialog progressDialog){

		class AddUser extends AsyncTask<Void,Void,String>{

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);

				Log.d(TAG,"onPostExecute addUser");

				Toast.makeText(getBaseContext(), "Signup successfully", Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
				emptyInputVal(actionType);

				Intent i = new Intent(Signup.this, Login.class);
				startActivity(i);
			}

			@Override
			protected String doInBackground(Void... v) {

				Log.d(TAG,"doInBackground addUser");

				HashMap<String,String> params = new HashMap<String, String>();
				params.put(Config.KEY_USER_NAME,userName);
				params.put(Config.KEY_USER_EMAIL,userEmail);
				params.put(Config.KEY_USER_PASSWORD,userPassword);
				params.put(Config.KEY_USER_Regtype,userRegtype);

				RequestHandler rh = new RequestHandler();
				String res = rh.sendPostRequest(Config.URL_ADD, params);
				return res;
			}
		}

		AddUser addUser = new AddUser();
		addUser.execute();
	}
}
