package com.seaco.denguesitecapture.activities;

import com.seaco.denguesitecapture.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

	private static int SPLASH_TIME_OUT = 3000;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			public void run(){
				//Intent i = new Intent(SplashScreen.this, MainActivity.class);
				Intent i = new Intent(SplashScreen.this, Login.class);
				//Intent i = new Intent(SplashScreen.this, TabTest.class);
				//Test Jason
				//Intent i = new Intent(SplashScreen.this, TestJason.class);
				startActivity(i);

				finish();
			}

		}, SPLASH_TIME_OUT);
	}

}
