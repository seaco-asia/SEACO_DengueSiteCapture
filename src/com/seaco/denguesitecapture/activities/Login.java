package com.seaco.denguesitecapture.activities;

import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.seaco.denguesitecapture.R;
import com.seaco.denguesitecapture.model.MySQLiteHelper;
import com.seaco.denguesitecapture.model.UserDetail;
import com.seaco.denguesitecapture.model.Users;

public class Login extends Activity implements OnClickListener {

	private static final String TAG = "Login";
	Button btnLogin, btnSignup;
	private MySQLiteHelper db;
	String userName,userEmail,userPassword,userRegtype, actionType, userPhoneNo;
	View signupLayout;
	public String JSON_STRING;
	Locale myLocale;
	Spinner spinnerLanguage;
	//Integer selectedLanguage;
	RadioGroup radBtnLanguage, radBtnOnlineMode;
	RadioButton radBtnBahasa, radBtnEnglish, radBtnOn, radBtnOff;
	TextView lblSelectLanguage, lblMode, lblPhoneNo, lblPassword;
	String languageType, radModeType;
	double latitude, longitude;
	EditText editText1,editText2, hintPhoneNo, hintPassword;
	//Switch mySwitch;
	GPSTracker gps;

	//use SharedPreferences to store and retrieve languageType parameter
	SharedPreferences sharedpreferences;
	public static final String mypreference = "mypref";
	public static final String languageTypePref = "languageTypePrefKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);

		//label
		lblSelectLanguage = (TextView)findViewById(R.id.sel_language);
		lblMode = (TextView)findViewById(R.id.sel_mode);
		lblPhoneNo = (TextView)findViewById(R.id.phoneNo);
		lblPassword = (TextView)findViewById(R.id.password);
		
		//hint edit text
		hintPhoneNo = (EditText)findViewById(R.id.login_phoneNo);
		hintPassword = (EditText)findViewById(R.id.login_password);
		
		//use SharedPreferences to store and retrieve languageType parameter
		sharedpreferences = getSharedPreferences(mypreference,
				Context.MODE_PRIVATE);

		languageType = "en"; //set language value
		Log.d(TAG,"languageTypePref ["+languageType+"]");

		radModeType ="Y";//set onlineMode value

		// set radio button
		radBtnLanguage = (RadioGroup)findViewById(R.id.radLanguage);
		radBtnBahasa = (RadioButton) findViewById(R.id.radBahasa);
		radBtnEnglish = (RadioButton)findViewById(R.id.radEnglish);

		radBtnOnlineMode = (RadioGroup)findViewById(R.id.radMode);
		radBtnOn = (RadioButton) findViewById(R.id.radOnlineOn);
		radBtnOff = (RadioButton)findViewById(R.id.radOnlineOff);

		//button signup, login
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnSignup = (Button) findViewById(R.id.btnSignup);

		lblSelectLanguage.setText("Select Language");
		lblPhoneNo.setText("Phone No.");
		lblPassword.setText("Password");
		btnLogin.setText("Login");
		btnSignup.setText("Signup");

		btnLogin.setOnClickListener(this);
		btnSignup.setOnClickListener(this);

		//radio button function
		radBtnLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radBahasa:
					languageType = "ms"; //set language value
					setLocale(languageType);
					break;

				case R.id.radEnglish:
					languageType = "en"; //set language value
					setLocale(languageType);
					break;
				}
			}
		});

		//radio button function
		radBtnOnlineMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radOnlineOn:
					Log.d(TAG,"Test ONN");
					radModeType = "Y";
					break;

				case R.id.radOnlineOff:
					Log.d(TAG,"Test Of");
					radModeType = "N";
					break;
				}
			}
		});

		/* temporary using before implement server database
		 * 
			db = new MySQLiteHelper(this);
			final String[][] users = db.getUserResult();
			Log.d(TAG,"retrieve data users......"+users.length);
		 *
		 */

		//this codes for fix IO Exception while connecting to the url at the connect(). 
		//if (android.os.Build.VERSION.SDK_INT > 9) {
		//StrictMode.ThreadPolicy policy = new     StrictMode.ThreadPolicy.Builder().permitAll().build();
		//StrictMode.setThreadPolicy(policy);
		//}

		//use for offline.
		db = new MySQLiteHelper(this);

		gps = new GPSTracker(this);

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
		i.putExtra("languageType", languageType);
		startActivity(i);
	}

	public void login(){

		Log.d(TAG,"enter login method");

		actionType = "L";//LOGIN

		if (!validateLogin()) {
			Toast.makeText(getBaseContext(), languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_FAIL:Constants.en.LOGIN_FAIL, Toast.LENGTH_LONG).show();
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(Login.this,R.style.AppBaseTheme);
		progressDialog.setIndeterminate(true);
		Log.d(TAG,"LANGUAGE TYPE ["+languageType+"]");
		progressDialog.setMessage(languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_AUTH:Constants.en.LOGIN_AUTH);
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
						//checking if user select online mode, proceed with checking in live DB OTHERWISE checking in local DB
						if (radModeType.equalsIgnoreCase("Y")){
							checkUser(userEmail, userPassword, userPhoneNo, progressDialog);
						}else{
							checkUserinLocalDevice(userEmail, userPassword, userPhoneNo, progressDialog);
						}
						//end checking 
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
			phoneNoEditText.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_AUTH_PHONENO:Constants.en.LOGIN_AUTH_PHONENO);
			valid = false;
		}else{
			phoneNoEditText.setError(null);
		}

		if(userPassword.isEmpty() || userPassword.length() < 4 || userPassword.length() > 10) {
			passEditText.setError(languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_AUTH_PASSWORD:Constants.en.LOGIN_AUTH_PASSWORD);
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
			////EditText emailSignupEditText = (EditText)findViewById(R.id.signup_email);
			EditText phoneSignupEditText = (EditText)findViewById(R.id.signup_phoneNo);
			EditText passSignupEditText = (EditText)findViewById(R.id.signup_password);

			nameSignupEditText.setText("");
			////emailSignupEditText.setText("");
			phoneSignupEditText.setText("");
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

						//save languageType into sharedpreferences to fix languageType param cannot be retrieve after capture photo
						Editor editor = sharedpreferences.edit();
						editor.putString(languageTypePref, languageType);
						editor.commit();

						Toast.makeText(getBaseContext(), languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_AUTH_NOTREGISTERED:Constants.en.LOGIN_AUTH_NOTREGISTERED, Toast.LENGTH_LONG).show();
						emptyInputVal(actionType);
					}else{

						//save languageType into sharedpreferences to fix languageType param cannot be retrieve after capture photo
						Editor editor = sharedpreferences.edit();
						editor.putString(languageTypePref, languageType);
						editor.commit();

						setLocale(languageType);

						//get data from live DB
						Users users = Users.myMethod(s);

						//save to own device
						db.checkUsernInsertInDB(users.getUserId(), users.getUserName(), userEmail, userPassword, users.getUserRegtype(), userPhoneNo, users.getRegDate());
						Toast.makeText(getBaseContext(), languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_SUCCESS:Constants.en.LOGIN_SUCCESS, Toast.LENGTH_LONG).show();

						Intent i = new Intent(Login.this, MainActivity.class);
						i.putExtra("userEmail", userEmail);
						i.putExtra("userRegtype", users.getUserRegtype());
						i.putExtra("userName", users.getUserName());
						i.putExtra("userID", users.getUserId());
						i.putExtra("userPhoneNo", users.getUserPhoneNo());
						i.putExtra("userPassword", userPassword);
						i.putExtra("radModeType", radModeType);
						//i.putExtra("languageType", languageType);
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

	public void checkUserinLocalDevice(final String userEmail, final String userPassword, final String userPhoneNo, final ProgressDialog progressDialog){

		boolean existInDevice = db.checkUserInDB(userPassword, userPhoneNo);

		if(!existInDevice){

			progressDialog.dismiss();

			//save languageType into sharedpreferences to fix languageType param cannot be retrieve after capture photo
			Editor editor = sharedpreferences.edit();
			editor.putString(languageTypePref, languageType);
			editor.commit();

			Toast.makeText(getBaseContext(), languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_AUTH_NOTREGISTERED:Constants.en.LOGIN_AUTH_NOTREGISTERED, Toast.LENGTH_LONG).show();
			emptyInputVal(actionType);

		}else{

			//save languageType into sharedpreferences to fix languageType param cannot be retrieve after capture photo
			Editor editor = sharedpreferences.edit();
			editor.putString(languageTypePref, languageType);
			editor.commit();

			setLocale(languageType);

			//get data from local DB
			UserDetail singleUser = db.getUserDetail(userPassword, userPhoneNo);

			Toast.makeText(getBaseContext(), languageType.equalsIgnoreCase("ms")?Constants.ms.LOGIN_SUCCESS:Constants.en.LOGIN_SUCCESS, Toast.LENGTH_LONG).show();

			Intent i = new Intent(Login.this, MainActivity.class);
			i.putExtra("userEmail", singleUser.getUserEmail());
			i.putExtra("userRegtype", singleUser.getUserRegtype());
			i.putExtra("userName", singleUser.getUserName());
			i.putExtra("userID", singleUser.getUserId());
			i.putExtra("userPhoneNo", singleUser.getUserPhoneNo());
			i.putExtra("userPassword", userPassword);
			i.putExtra("radModeType", radModeType);
			//i.putExtra("languageType", languageType);
			startActivity(i);
			progressDialog.dismiss();
		}
	}

	//localtization to setup language
	public void setLocale(String lang) {

		myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

		if(lang.equalsIgnoreCase("ms")){
			lblSelectLanguage.setText("Pilih Bahasa");
			lblMode.setText("Mod dalam Talian");
			lblPhoneNo.setText("No. Telefon");
			lblPassword.setText("Kata Laluan");
			hintPhoneNo.setHint("No Telefon");
			hintPassword.setHint("Kata Laluan");
			btnLogin.setText("Log Masuk");
			btnSignup.setText("Daftar");
		}
		else{
			lblSelectLanguage.setText("Select Language");
			lblMode.setText("Online Mode");
			lblPhoneNo.setText("Phone No.");
			lblPassword.setText("Password");
			hintPhoneNo.setHint("Phone No");
			hintPassword.setHint("Password");
			btnLogin.setText("Login");
			btnSignup.setText("Signup");
		}

		//Intent refresh = new Intent(this, Login.class);
		//refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		//startActivity(refresh);
	}
}
