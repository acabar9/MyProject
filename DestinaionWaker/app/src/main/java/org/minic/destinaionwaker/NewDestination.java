package org.minic.destinaionwaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.GoogleMap.OnMapClickListener;

public class NewDestination extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener{
    private GoogleMap mMap;
    TextView lattext,lngtext;
    EditText edtname;

    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST;
    PlacePicker.IntentBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_destination);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        lattext = (TextView) findViewById(R.id.latText);
        lngtext = (TextView) findViewById(R.id.lngText);
        edtname = (EditText) findViewById(R.id.destname);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        PLACE_PICKER_REQUEST = 1;
        builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng seoul = new LatLng(37.52487, 126.92723);
        mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in seoul"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,15.0f));

        /////////////////////////////////////////
        mMap.setOnMapClickListener(new OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                mymapPick(point);
            }
        });
        /////////////////////////////////////////

    }
    public void addbtnlistener(View v){
        if ((lattext.getText().toString()).equals("경도")) {
            Toast.makeText(this, "목적지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else{
            if((edtname.getText().toString().equals(""))){
                Toast.makeText(this, "이름을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
            }else{
                DBHelper dbHelper = new DBHelper(getApplicationContext(), "DESTINATION_SQLITE.db", null, 1);
                dbHelper.insert(Double.parseDouble(lattext.getText().toString()), Double.parseDouble(lngtext.getText().toString()), edtname.getText().toString());
                Toast.makeText(getApplicationContext(), "새 목적지가 생성되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void cancelbtnlistener(View v){
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void mymapPick(LatLng point){
        mMap.clear(); //전에 찍은거 없애기
        LatLng addmark1 = new LatLng(point.latitude, point.longitude);
        mMap.addMarker(new MarkerOptions().position(addmark1).title("목적지"));
        lattext.setText(point.latitude+"");
        lngtext.setText(point.longitude+"");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String placename=place.getName().toString();
                if (placename.contains("\"")|placename.contains("\'")){
                    //검색이 아닌 좌표로 찾았을 시, 위도와 경도로 표현되는데 그대로 DB 저장하면 오류나서 뺐음.
                }else{
                    edtname.setText(placename);
                }
                mymapPick(place.getLatLng());
            }
        }
    }

    public void searchListener(View v) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }
}