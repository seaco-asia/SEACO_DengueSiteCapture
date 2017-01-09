//package com.seaco.denguesitecapture.activities;
//
//import android.util.Log;
//import android.widget.RadioGroup;
//
//import com.seaco.denguesitecapture.R;
//
//
//public class CommonRadioValue {
//
//	//Add your variable here
//	private String valType;
//	
//	//Add your radio button method here
//	public String typeStreet(RadioGroup radTypeStreet){
//
//		radTypeStreet.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
//		{
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				switch(checkedId){
//				case R.id.radio2_1:
//					valType = "1"; //set decision value
//					Log.d("CommonRadioValue","1");
//					break;
//
//				case R.id.radio2_2:
//					// do operations specific to this selection
//					valType = "2"; //set decision value
//					Log.d("CommonRadioValue","2");
//					break;
//				case R.id.radio2_3:
//					valType = "3"; //set decision value
//					Log.d("CommonRadioValue","3");
//					break;
//				}
//			}
//			
//		});
//		
//		Log.d("CommonRadioValue",valType);
//		return valType;
//		
//	}
//
//}
//
