package com.seaco.denguesitecapture.activities;

import java.util.HashMap;

import org.json.JSONException;

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
import com.seaco.denguesitecapture.model.Users;

public class Login extends Activity implements OnClickListener {

	private static final String TAG = "Login";
	private Button btnLogin, btnSignup;
	private MySQLiteHelper db;
	String userName,userEmail,userPassword,userRegtype, actionType, userPhoneNo;
	View signupLayout;
	public String JSON_STRING;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);

		//button signup, login
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnSignup = (Button) findViewById(R.id.btnSignup);

		btnLogin.setOnClickListener(this);
		btnSignup.setOnClickListener(this);

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
		case R.id.btnLogin:
			login();
			Log.d(TAG,"finish login");
			break;
		case R.id.btnSignup:
			signup();
			Log.d("TAG","finish signup");
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//Inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void signup(){
		Intent i = new Intent(Login.this, Signup.class);
		startActivity(i);
	}

	public void login(){

		Log.d(TAG,"enter login method");

		actionType = "L";//LOGIN

		if (!validateLogin()) {
			Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(Login.this,R.style.AppBaseTheme);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Please Wait. Your authentication is in progress");
		progressDialog.show();

		//textfield login
		//EditText emailEditText = (EditText)findViewById(R.id.login_email);
		EditText phoneNoEditText = (EditText)findViewById(R.id.login_phoneNo);
		EditText passEditText = (EditText)findViewById(R.id.login_password);

		//userEmail = emailEditText.getText().toString();
		userPhoneNo = phoneNoEditText.getText().toString();
		userPassword = passEditText.getText().toString();

		Log.d(TAG, "userEmail ["+userEmail +"] and userPassword ["+userPassword+"] and userPhoneNo ["+userPhoneNo+"]" );

		// TODO: Implement your own authentication logic here.
		new android.os.Handler().postDelayed(
				new Runnable() {
					public void run() {
						//check user if their email already have at db
						//checkUser(userEmail, userPassword, progressDialog);
						checkUser(userEmail, userPassword, userPhoneNo, progressDialog);
					}
					//}
				}, 3000);
	}


	public boolean validateLogin(){

		Log.d(TAG,"enter validate login method");

		boolean valid = true;

		//textfield login
		//EditText emailEditText = (EditText)findViewById(R.id.login_email);
		EditText phoneNoEditText = (EditText)findViewById(R.id.login_phoneNo);
		EditText passEditText = (EditText)findViewById(R.id.login_password);

		//userEmail = emailEditText.getText().toString();
		userPhoneNo = phoneNoEditText.getText().toString();
		userPassword = passEditText.getText().toString();

		Log.d(TAG, "userEmail ["+userEmail +"] and userPassword ["+userPassword+"] and userPhoneNo ["+userPhoneNo+"]" );
		
		/*if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
			emailEditText.setError("Please enter your email");
			valid = false;
		}else{
			emailEditText.setError(null);
		}*/
		
		if (userPhoneNo.isEmpty()) {
			phoneNoEditText.setError("Please enter your telephone number");
			valid = false;
		}else{
			phoneNoEditText.setError(null);
		}

		if(userPassword.isEmpty() || userPassword.length() < 4 || userPassword.length() > 10) {
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
			//EditText emailLoginEditText = (EditText)findViewById(R.id.login_email);
			EditText phoneNoEditText = (EditText)findViewById(R.id.login_phoneNo);
			EditText passLoginEditText = (EditText)findViewById(R.id.login_password);

			//emailLoginEditText.setText("");
			phoneNoEditText.setText("");
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

	//private void checkUser(final String userEmail, final String userPassword, final ProgressDialog progressDialog){
	private void checkUser(final String userEmail, final String userPassword, final String userPhoneNo, final ProgressDialog progressDialog){
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

				try {
					if(s.contains("failure")){
						progressDialog.dismiss();
						Toast.makeText(getBaseContext(), "Please signup. You are not register yet.", Toast.LENGTH_LONG).show();
						emptyInputVal(actionType);
					}else{
						//add if the data not already in db
						
						Users users = Users.myMethod(s);
						Toast.makeText(getBaseContext(), "Successfully login", Toast.LENGTH_LONG).show();
						Intent i = new Intent(Login.this, MainActivity.class);
						i.putExtra("userEmail", userEmail);
						i.putExtra("userRegtype", users.getUserRegtype());
						i.putExtra("userName", users.getUserName());
						i.putExtra("userID", users.getUserId());
						i.putExtra("userPhoneNo", users.getUserPhoneNo());
						startActivity(i);
						progressDialog.dismiss();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				HashMap<String,String> paramUser = new HashMap<String, String>();
				//paramUser.put(Config.KEY_USER_EMAIL,userEmail);
				paramUser.put(Config.KEY_USER_PHONENO,userPhoneNo);
				paramUser.put(Config.KEY_USER_PASSWORD,userPassword);
				
				String s = rh.sendPostRequest(Config.URL_CHECK_USER,paramUser);
				Log.d(TAG,"doInBackground ["+s+"]");
				return s;
			}
		}
		CheckUser checkUser = new CheckUser();
		checkUser.execute();
	}
}
