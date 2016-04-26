package com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrary;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import android.os.Handler;
import android.support.v7.widget.Toolbar;


/**
 * Created by ben on 2016/1/8.
 */
public class MapActivity extends AppCompatActivity {
    private final String TAG = "=== Map Demo ==>";
    private Long startTime;
    private Handler handler = new Handler();
    Toolbar toolbar;
    /**高第一*/
    final LatLng NKFUST = new LatLng(22.755493,120.335235);


    /** Map */
    private GoogleMap mMap;
    private TextView txtOutput;
    private Marker markerMe;

    /** 記錄軌跡 */
    private ArrayList<LatLng> traceOfMe;

    /** GPS */
    private LocationManager locationMgr;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TypedValue typedValueColorPrimaryDark = new TypedValue();
        MapActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueColorPrimaryDark, true);
        final int colorPrimaryDark = typedValueColorPrimaryDark.data;
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(colorPrimaryDark);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        initView();
        initMap();
        if (initLocationProvider()) {
            whereAmI();
        }else{
            txtOutput.setText("請開啟定位！");
        }
    }


    @Override
    protected void onStop() {
        locationMgr.removeUpdates(locationListener);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();

    }

    private void initView(){
        txtOutput = (TextView) findViewById(R.id.txtOutput);
    }


    /************************************************
     *
     * 						Map部份
     *
     ***********************************************/
    /**
     * Map初始化
     * 建立3個標記
     */
    private void initMap(){
        if (mMap == null) {
            mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

            if (mMap != null) {
                //設定地圖類型
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                //Marker1
                MarkerOptions markerOpt = new MarkerOptions();
                markerOpt.position(NKFUST);
                markerOpt.title("NKFUST");
                markerOpt.snippet("國立雄第一科技大學");
                markerOpt.draggable(false);
                markerOpt.visible(true);
                markerOpt.anchor(0.5f, 0.5f);//設為圖片中心
                markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(markerOpt);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NKFUST, 15));

            }
        }
    }

    /**
     * 按鈕:放大地圖
     * @param v
     */
    public void zoomInOnClick(View v){
        //zoom in
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * 按鈕:縮小地圖
     * @param v
     */
    public void zoomToOnClick(View v){
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 3000, null);
    }

    public void timecal(View v){
        //取得目前時間
        startTime = System.currentTimeMillis();
        //設定定時要執行的方法
        handler.removeCallbacks(updateTimer);
        //設定Delay的時間
        handler.postDelayed(updateTimer, 1000);
        Timer timer = new Timer();
        timer.schedule(new SimpleTask(), 10000, 10000);
    }

    public void timestop(View v){
        // stoptime = timer.purge();
        handler.removeCallbacks(updateTimer);

    }
    //固定要執行的方法
    private Runnable updateTimer = new Runnable() {
        public void run() {
            final TextView time = (TextView) findViewById(R.id.textView);
            Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數
            Long minius = (spentTime/1000)/60;
            //計算目前已過秒數
            Long seconds = (spentTime/1000) % 60;
            time.setText("計時: "+minius+":"+seconds);
            handler.postDelayed(this, 1000);

        }
    };

    /************************************************
     *
     * 						GPS部份
     *
     ***********************************************/
    /**
     * GPS初始化，取得可用的位置提供器
     * @return
     */
    private boolean initLocationProvider() {
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //選擇使用GPS提供器
        if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }
        return false;
    }

    /**
     * 執行"我"在哪裡
     * 1.建立位置改變偵聽器
     * 2.預先顯示上次的已知位置
     */
    private void whereAmI(){

        //取得上次已知的位置
        Location location = locationMgr.getLastKnownLocation(provider);
        updateWithNewLocation(location);

        //GPS Listener
        locationMgr.addGpsStatusListener(gpsListener);

        //Location Listener
        long minTime = 1000;//ms
        float minDist = 1.0f;//meter
        locationMgr.requestLocationUpdates(provider, minTime, minDist, locationListener);
    }

    /**
     * 顯示"我"在哪裡
     * @param lat
     * @param lng
     */
    private void showMarkerMe(double lat, double lng){
        if (markerMe != null) {
            markerMe.remove();
        }

        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(new LatLng(lat, lng));
        markerOpt.title("我在這裡");
        markerMe = mMap.addMarker(markerOpt);


    }

    private void cameraFocusOnMe(double lat, double lng){
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(16)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    private void trackToMe(double lat, double lng){
        if (traceOfMe == null) {
            traceOfMe = new ArrayList<LatLng>();
        }
        traceOfMe.add(new LatLng(lat, lng));

        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : traceOfMe) {
            polylineOpt.add(latlng);
        }

        polylineOpt.color(Color.RED);

        Polyline line = mMap.addPolyline(polylineOpt);
        line.setWidth(13);
    }

    /**
     * 更新並顯示新位置
     * @param location
     */
    private void updateWithNewLocation(Location location) {
        String where = "";
        if (location != null) {
            //經度
            double lng = location.getLongitude();
            //緯度
            double lat = location.getLatitude();
            //速度
            speed = Math.round(location.getSpeed());

            //時間
            long time = location.getTime();
            String timeString = getTimeString(time);

            where = "經度: " + lng +
                    "\n緯度: " + lat +
                    "\n速度: " + speed +" km/hr"+
                    "\n時間: " + timeString+
                    "\n卡路里:"+c +" kcals";

            //標記"我"
            showMarkerMe(lat, lng);
            cameraFocusOnMe(lat, lng);
            trackToMe(lat, lng);

        }else{
            where = "無法定位";
        }

        //位置改變顯示
        txtOutput.setText(where);

    }
    public double c=0;
    public int speed;

    public class SimpleTask extends TimerTask {
        public void run() {
            int sp = speed;

            switch (sp) {
                case 4:
                    c += 0.5;
                    break;
                case 5:
                    c += 0.5;
                    break;
                case 6:
                    c += 1;
                    break;
                case 7:
                    c += 1.17;
                    break;
                case 8:
                    c += 1.67;
                    break;
                case 9:
                    c += 1.83;
                    break;
                case 10:
                    c += 2.17;
                    break;
                case 11:
                    c += 2.33;
                    break;
                case 12:
                    c += 2.67;
                    break;
                case 13:
                    c += 2.83;
                    break;
                case 14:
                    c += 3;
                    break;
                case 15:
                    c += 3.17;
                    break;
                case 16:
                    c += 3.5;
                    break;
                case 17:
                    c += 3.83;
                    break;

            }
        }
    }

    GpsStatus.Listener gpsListener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "GPS_EVENT_STARTED");
                    Toast.makeText(MapActivity.this, "GPS開始定位", Toast.LENGTH_SHORT).show();
                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "GPS_EVENT_STOPPED");
                    Toast.makeText(MapActivity.this, "GPS停止", Toast.LENGTH_SHORT).show();
                    break;

                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "GPS_EVENT_FIRST_FIX");
                    Toast.makeText(MapActivity.this, "第一次定位", Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };

    LocationListener locationListener = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.v(TAG, "Status Changed: Out of Service");
                    Toast.makeText(MapActivity.this, "不能使用",
                            Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.v(TAG, "Status Changed: Temporarily Unavailable");
                    Toast.makeText(MapActivity.this, "暫時不能使用",
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    private String getTimeString(long timeInMilliseconds){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeInMilliseconds);
    }

}
