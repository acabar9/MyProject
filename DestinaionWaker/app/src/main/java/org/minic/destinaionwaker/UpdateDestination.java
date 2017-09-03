package org.minic.destinaionwaker;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class UpdateDestination extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView lattext,lngtext;
    EditText edtname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_destination);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        lattext = (TextView) findViewById(R.id.latText_update);
        lngtext = (TextView) findViewById(R.id.lngText_update);
        edtname = (EditText) findViewById(R.id.destname_update);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng seoul = new LatLng(37.52487, 126.92723);
        mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in seoul"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,15.0f));

        /////////////////////////////////////////
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {

                mMap.clear(); //전에 찍은거 없애기
                LatLng addmark1 = new LatLng(point.latitude, point.longitude);
                mMap.addMarker(new MarkerOptions().position(addmark1).title("목적지"));
                lattext.setText(point.latitude+"");
                lngtext.setText(point.longitude+"");

            }
        });
        /////////////////////////////////////////

    }
    public void addbtnlistener_update(View v){
        if ((lattext.getText().toString()).equals("경도")) {
            Toast.makeText(this, "목적지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else{
            if((edtname.getText().toString().equals(""))){
                Toast.makeText(this, "이름을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = getIntent();
                int id = intent.getExtras().getInt("id");

                DBHelper dbHelper = new DBHelper(getApplicationContext(), "DESTINATION_SQLITE.db", null, 1);
                dbHelper.update(Double.parseDouble(lattext.getText().toString()), Double.parseDouble(lngtext.getText().toString()), id, edtname.getText().toString());
                finish();
            }
        }
    }

    public void cancelbtnlistener_update(View v){
        finish();
    }

    public void searchListener_update(View v) {
        EditText getaddr = (EditText) findViewById(R.id.etGetAddress_update);
        findGeoPoint(this,getaddr.getText().toString());
    }


    public Location findGeoPoint(Context mcontext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정
        Toast.makeText(mcontext, "주소를 검색했습니다. 원하는 위치가 아닐 경우 자세한 단서를 붙여 입력하십시오. \nex) ㅇㅇ동 ㅇㅇ아파트" , Toast.LENGTH_LONG).show();

        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                loc.setLatitude(lat);
                loc.setLongitude(lon);

                mMap.clear(); //전에 찍은거 없애기
                LatLng addmark1 = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(addmark1).title("목적지"));
                lattext.setText(lat+"");
                lngtext.setText(lon+"");
                LatLng seoul = new LatLng(lat, lon);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,15.0f));
            }
        }else {
            Toast.makeText(mcontext, "일시적 네트워크 에러입니다. 잠시후 다시 검색해주세요." , Toast.LENGTH_LONG).show();
        }

        return loc;
    }

}
