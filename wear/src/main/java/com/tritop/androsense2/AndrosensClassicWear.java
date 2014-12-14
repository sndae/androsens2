package com.tritop.androsense2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AndrosensClassicWear extends Activity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    final static String SENSOR_KEY_NAME = "SENSOR_NAME";
    final static String SENSOR_KEY_VENDOR = "SENSOR_VENDOR";
    final static String SENSOR_KEY_VERSION = "SENSOR_VERSION";
    final static String SENSOR_KEY_POWER = "SENSOR_POWER";
    final static String SENSOR_KEY_MAXRANGE = "SENSOR_MAXRANGE";
    final static String SENSOR_KEY_RESOLUTION = "SENSOR_RESOLUTION";


    List<Map<String,String>> itemList = new ArrayList<Map<String,String>>();
    SimpleAdapter mListAdapter;

    SensorManager mSensorManager;

    int[] boundViews = new int[]{R.id.textView_SensorName,R.id.textView_SensorVendor,R.id.textView_SensorVersion,R.id.textView_SensorMaxRange,R.id.textView_SensorResolution,R.id.textView_SensorPower};
    String[] boundRows = new String[]{SENSOR_KEY_NAME,SENSOR_KEY_VENDOR,SENSOR_KEY_VERSION,SENSOR_KEY_MAXRANGE,SENSOR_KEY_RESOLUTION,SENSOR_KEY_POWER};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tritop.androsense2.R.layout.activity_androsens_classic_wear);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getAvailableSensors();
        mListAdapter = new SimpleAdapter(this, itemList, R.layout.list_item_layout, boundRows, boundViews);
        final WatchViewStub stub = (WatchViewStub) findViewById(com.tritop.androsense2.R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (ListView) stub.findViewById(R.id.listView_watch);
                mListView.setAdapter(mListAdapter);
                mListView.setOnItemClickListener(AndrosensClassicWear.this);
            }
        });
    }

    private void getAvailableSensors(){
        itemList.clear();
        itemList.addAll(parseAvailableSensors(mSensorManager.getSensorList(Sensor.TYPE_ALL)));
        ((AndrosensApp) this.getApplication()).setSensorManager(mSensorManager);
        ((AndrosensApp) getApplication()).setSensorList(((AndrosensApp) getApplication()).getSensorManager().getSensorList(Sensor.TYPE_ALL));

    }
    private List<Map<String,String>> parseAvailableSensors(List<Sensor> sList){
        List<Map<String,String>> mList = new Vector<Map<String,String>>();
        HashMap<String,String> mSensorInfo;
        for(Sensor sensor: sList){
            mSensorInfo = new HashMap<String,String>();
            mSensorInfo.put(SENSOR_KEY_NAME, sensor.getName());
            mSensorInfo.put(SENSOR_KEY_VENDOR, sensor.getVendor());
            mSensorInfo.put(SENSOR_KEY_VERSION, SensorInfo.getStringType(this, sensor.getType())+" v"+String.valueOf(sensor.getVersion()));
            mSensorInfo.put(SENSOR_KEY_POWER, SensorInfo.formatSensorfloat(sensor.getPower(),4)+" mA");
            mSensorInfo.put(SENSOR_KEY_MAXRANGE, SensorInfo.formatSensorfloat(sensor.getMaximumRange(),3)+" max");
            mSensorInfo.put(SENSOR_KEY_RESOLUTION,SensorInfo.formatSensorfloat(sensor.getResolution(),4)+" res");
            mList.add(mSensorInfo);
        }
        return mList;


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent startIntent = new Intent(this, SensorActivity.class);
        startIntent.putExtra("SENSORINDEX",position);
        startActivity(startIntent);
    }
}
