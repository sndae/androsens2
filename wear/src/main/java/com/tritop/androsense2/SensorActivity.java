/*
    Copyright (C) 2013-2014 Christian Schneider
    christian.d.schneider@googlemail.com
    
    This file is part of Androsens classic.

    Androsens classic is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Androsens classic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Androsens classic.  If not, see <http://www.gnu.org/licenses/>.
*/


package com.tritop.androsense2;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SensorActivity extends Activity implements SensorEventListener {

    private static final String LOGTAG = "WEAR_SENSORSACTIVITY";

    private Sensor mSensor;
	private TextView mSensorName;
	private TextView mSensorAccuracy;
	private TextView mSensorType;
	private TextView mSensorVendor;
	private TextView mSensorVersion;
	private TextView mSensorPower;
	
	private TextView mSensorValueX;
	private TextView mSensorValueY;
	private TextView mSensorValueZ;
	
	private LinearLayout llYvalues;
	private LinearLayout llZvalues;
	
	private ProgressBar mProgressX;
	private ProgressBar mProgressY;
	private ProgressBar mProgressZ;
	
	private float mConversionMulti=1.0f;

    WatchViewStub stub;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int index=0;
        super.onCreate(savedInstanceState);
        setContentView(com.tritop.androsense2.R.layout.activity_sensor_wear);
        Intent intent = getIntent();
        if (intent != null) {
            index = intent.getIntExtra("SENSORINDEX", 0);
        }
        mSensor = ((AndrosensApp) this.getApplication()).getSensor(index);
        stub = (WatchViewStub) findViewById(com.tritop.androsense2.R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                bindViews(stub);
                getSensorInfo();
                chooseProgressMaximum(mSensor.getMaximumRange());
            }
        });
	}

	
	
	private void bindViews(WatchViewStub stub){
		mSensorName = (TextView) stub.findViewById(R.id.activity_sensor_textView_sensorName);
		mSensorAccuracy = (TextView) stub.findViewById(R.id.activity_sensor_textView_sensorAccuracy);

        //mSensorType = (TextView) stub.findViewById(R.id.activity_sensor_textView_typeValue);
		//mSensorVendor = (TextView) stub.findViewById(R.id.activity_sensor_textView_vendorValue);
		//mSensorVersion = (TextView) stub.findViewById(R.id.activity_sensor_textView_versionValue);
		//mSensorPower = (TextView) stub.findViewById(R.id.activity_sensor_textView_powerValue);
		
		llYvalues = (LinearLayout) stub.findViewById(R.id.activity_sensor_LL_Y);
		llZvalues = (LinearLayout) stub.findViewById(R.id.activity_sensor_LL_Z);
		
		mProgressX  =  (ProgressBar) stub.findViewById(R.id.activity_sensor_progressBarX);
		mProgressY =  (ProgressBar) stub.findViewById(R.id.activity_sensor_progressBarY);
	    mProgressZ =  (ProgressBar) stub.findViewById(R.id.activity_sensor_progressBarZ);
	    
	    mProgressX.setIndeterminate(false);
	    mProgressY.setIndeterminate(false);
	    mProgressZ.setIndeterminate(false);
	    mSensorValueX = (TextView) stub.findViewById(R.id.activity_sensor_textView_xvalue);
		mSensorValueY = (TextView) stub.findViewById(R.id.activity_sensor_textView_yvalue);
		mSensorValueZ = (TextView) stub.findViewById(R.id.activity_sensor_textView_zvalue);
	}
	
	private void getSensorInfo(){
		mSensorName.setText(mSensor.getName());
		/*
		mSensorType.setText(this.getResources().getString(R.string.activity_sensor_sensor_type_title)+" "+SensorInfo.getStringType(this, mSensor.getType()));
		mSensorVendor.setText(this.getResources().getString(R.string.activity_sensor_sensor_vendor_title)+" "+mSensor.getVendor());
		mSensorVersion.setText(this.getResources().getString(R.string.activity_sensor_sensor_version_title)+" "+mSensor.getVersion());
		mSensorPower.setText(this.getResources().getString(R.string.activity_sensor_sensor_power_title)+" "+SensorInfo.formatSensorfloat(mSensor.getPower(),4)+" mA");
		*/
	}
	
	private void chooseProgressMaximum(float sensorMax){
		if(sensorMax > 100){
			setProgressBarMax((int) sensorMax);
		}
		else {
			mConversionMulti = 100 / sensorMax;
			setProgressBarMax((int) (sensorMax*mConversionMulti));
		}
	}
	
	private void setProgressBarMax(int maximum){
		mProgressX.setMax(maximum);
		mProgressY.setMax(maximum);
		mProgressZ.setMax(maximum);
	}
	
	private int getSensorDelayPref(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	    int delay = Integer.valueOf(settings.getString("sensorDelay", "3"));
	    switch(delay){
	    	case SensorManager.SENSOR_DELAY_FASTEST:return SensorManager.SENSOR_DELAY_FASTEST; 
	    	case SensorManager.SENSOR_DELAY_GAME:   return SensorManager.SENSOR_DELAY_GAME; 
	    	case SensorManager.SENSOR_DELAY_UI:     return SensorManager.SENSOR_DELAY_UI; 
	    	case SensorManager.SENSOR_DELAY_NORMAL: return SensorManager.SENSOR_DELAY_NORMAL; 
	    	default: return SensorManager.SENSOR_DELAY_NORMAL;
	    }
	}
	
	
	@Override
	protected void onPause() {
		((AndrosensApp) this.getApplication()).getSensorManager().unregisterListener(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		((AndrosensApp) this.getApplication()).getSensorManager().registerListener(this, mSensor, getSensorDelayPref());
	}

	
	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(mSensorAccuracy!=null) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    mSensorAccuracy.setText("ACCURACY HIGH");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    mSensorAccuracy.setText("ACCURACY MEDIUM");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    mSensorAccuracy.setText("ACCURACY LOW");
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    mSensorAccuracy.setText("STATUS UNRELIABLE");
                    break;
                default:
                    mSensorAccuracy.setText("unknow accuracy");
                    break;
            }
        }
        else {
            Log.e(LOGTAG, "No view for output accuracy="+accuracy);
        }
	}

	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
        if(llYvalues ==null || llZvalues == null) {
           return ;
        }
		if(SensorInfo.getSignificantValues(mSensor.getType()) >1){
                llYvalues.setVisibility(View.VISIBLE);
                llZvalues.setVisibility(View.VISIBLE);

			mSensorValueX.setText(SensorInfo.formatSensorfloat(event.values[0],3));
			mSensorValueY.setText(SensorInfo.formatSensorfloat(event.values[1],3));
			mSensorValueZ.setText(SensorInfo.formatSensorfloat(event.values[2],3));
			
			mProgressX.setProgress((int)(Math.abs(event.values[0])*mConversionMulti));
			mProgressY.setProgress((int)(Math.abs(event.values[1])*mConversionMulti));
			mProgressZ.setProgress((int)(Math.abs(event.values[2])*mConversionMulti));
		    
		}
		else {

            llYvalues.setVisibility(View.GONE);
            llZvalues.setVisibility(View.GONE);

			mSensorValueX.setText(SensorInfo.formatSensorfloat(event.values[0],3));
			mProgressX.setProgress((int)(Math.abs(event.values[0])*mConversionMulti));
		}
		
	}

	
}
