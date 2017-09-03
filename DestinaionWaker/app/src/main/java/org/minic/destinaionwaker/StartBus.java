package org.minic.destinaionwaker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StartBus extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    double mLatitude, mLongitude;
    float distance;
    NotificationManager nm;
    LatLng mylocation;

    Intent mainintent;
    int id;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) // 알림띄우기위해

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_bus);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_light);
        mapFragment.getMapAsync(this);
        myNoti(); //젤리빈 이상

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //GPS가 켜져있는지 체크
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Toast.makeText(this, "위치 서비스를 켜주세요.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
            finish();
        }

        //마시멜로 이상이면 권한 요청하기
        if (Build.VERSION.SDK_INT >= 23) {
            //권한이 없는 경우
            if (ContextCompat.checkSelfPermission(StartBus.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(StartBus.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartBus.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                finish();
                Toast.makeText(this, "승인하셨습니다.", Toast.LENGTH_SHORT).show();
            }
            //권한이 있는 경우
            else {
                requestMyLocation();
            }
        }
        //마시멜로 아래
        else {
            requestMyLocation();
        }
        requestMyLocation();
    }

    //나의 위치 요청
    public void requestMyLocation() {
        //권한
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}

        //리스너 달기
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener); //밀리초, 거리
    }


    //위치정보 구하기 리스너
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //내 위도 경도
            mLatitude = location.getLatitude();   //위도
            mLongitude = location.getLongitude(); //경도

            //맵생성
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_light);
            //콜백클래스 설정
            mapFragment.getMapAsync(StartBus.this);

            //목적지 근처 도착시
            if (distance < 500.0) {
                Intent intent3 = new Intent(getApplicationContext(), Alarm.class);
                startActivity(intent3);
                closeListenerAndNotiAndFinish();
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {Log.d("gps", "onStatusChanged");}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
        //현위치 보여주는 파란 점
        mMap.setMyLocationEnabled(true);
        //ui셋팅
        UiSettings uisetting=mMap.getUiSettings();
        uisetting.setZoomControlsEnabled(true);  // 아니 이거 대체 왜안되는거야 대체 왜
        uisetting.setMapToolbarEnabled(false);

        //DB정보가져오기
        id = mainintent.getExtras().getInt("id");
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "DESTINATION_SQLITE.db", null, 1);


        //목적지
        double destinationLat = dbHelper.getLatrow(id);
        double destinationLng = dbHelper.getLngrow(id);
        LatLng busDestination = new LatLng(destinationLat, destinationLng);

        //목적지 원그리기
        mMap.addMarker(new MarkerOptions().position(busDestination).title("목적지"));
        CircleOptions circle1KM = new CircleOptions().center(busDestination) //원점
                .radius(500)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#220000FF"));
        mMap.addCircle(circle1KM);


        //내위치
        mylocation = new LatLng(mLatitude, mLongitude);
//         내위치 마커 찍기
//        mMap.addMarker(new MarkerOptions().position(mylocation).title("내위치").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,15.0f));

        //거리구하기
        Location nowLocation = new Location("now");
        nowLocation.setLatitude(mLatitude);
        nowLocation.setLongitude(mLongitude);

        Location destinationLocation = new Location("destination");
        destinationLocation.setLatitude(destinationLat);
        destinationLocation.setLongitude(destinationLng);

        distance =nowLocation.distanceTo(destinationLocation);

        TextView dtl = (TextView) findViewById(R.id.distanceLeft);

        if (mLatitude!=0){
           // Toast.makeText(this, "남은 거리:"+distance+"m", Toast.LENGTH_LONG).show();
            dtl.setText("남은 거리 : "+distance+"m");
        } else{
            dtl.setText("남은 거리 계산중...");
        }

        TextView destn = (TextView) findViewById(R.id.destname);
        destn.setText("목적지 : "+dbHelper.getname(id));

    }

    public void deletebtnListener(View view){   //삭제
        final DBHelper dbHelper = new DBHelper(getApplicationContext(),"DESTINATION_SQLITE.db", null, 1);

        new AlertDialog.Builder(this)
                .setTitle("삭제")
                .setMessage("목적지 거리계산을 중단하고 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 프로세스 종료.
                        dbHelper.delete(id);
                        Toast.makeText(StartBus.this, "목적지가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent();
                        setResult(RESULT_OK, intent2);

                        closeListenerAndNotiAndFinish();
                    }
                })
                .setNegativeButton("아니오", null)
                .show();



    }

    public void startbtnListener(View v){
        Button stbtn= (Button) findViewById(R.id.startbtn);

        if((stbtn.getText()).equals("중지")){
            stbtn.setText("출발");
            Toast.makeText(this, "중지했습니다.", Toast.LENGTH_SHORT).show();

            locationManager.removeUpdates(locationListener);
            nm.cancel(111);

        }else if((stbtn.getText()).equals("출발")){
            startActivityForResult(mainintent,1);
            stbtn.setText("중지");
            Toast.makeText(this, "목적지로 출발합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void updatebtnListener(View v){


        Intent intent4 = new Intent(getApplicationContext(),UpdateDestination.class);
        intent4.putExtra("id",id);
        startActivityForResult(intent4,0);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("뒤로가기")
                        .setMessage("목적지 거리계산을 중단하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 프로세스 종료.
                                closeListenerAndNotiAndFinish();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();

                break;

            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        closeListenerAndNotiAndFinish();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void myNoti(){
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mainintent = getIntent(); //이거 지우면 튕김 전역변수로 했는데도 안됨
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm);
        mBuilder.setTicker("Notification.Builder");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setNumber(10);
        mBuilder.setContentTitle("목적지 이동중...");
        mBuilder.setContentText("목적지 반경 500m내에 진입하면 알람을 울리겠습니다.");
        mBuilder.setOngoing(true);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setPriority(Notification.PRIORITY_MAX);

        nm.notify(111, mBuilder.build());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeListenerAndNotiAndFinish();
    }


    public void closeListenerAndNotiAndFinish(){
        locationManager.removeUpdates(locationListener);
        nm.cancel(111);
        finish();
    }

}