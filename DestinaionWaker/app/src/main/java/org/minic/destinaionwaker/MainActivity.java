package org.minic.destinaionwaker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> arGeneral;
    ArrayList<Integer> a;
    ArrayAdapter<String> adapter1;
    DBHelper dbHelper;
    ListView lv;
    private long pressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slidemenu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewDestination.class);
                startActivityForResult(intent,0);
            }
        });
        dbHelper = new DBHelper(getApplicationContext(),"DESTINATION_SQLITE.db", null, 1);

        //////////////////////////////////////////////////////////////////////////////////////////////////

//        txresult.setText(dbHelper.getResult());  // DB에 있는 데이터 조회하는거(혹시몰라 남겨놓음. 안씀.)
        loadlist();



/////////////////////////////////////////////////////////////////////////////////////////////////////////////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            if(pressedTime == 0){
                Toast.makeText(MainActivity.this,"한번 더 누르면 종료됩니다",Toast.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);
                if(seconds > 2000){
                    pressedTime = 0;
                } else {
                    finish();
                }
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingintent = new Intent(getApplicationContext(),Setting.class);
            startActivity(settingintent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent settingintent = new Intent(getApplicationContext(),Setting.class);
            startActivity(settingintent);
        } else if (id == R.id.nav_share) {
            Intent devintent = new Intent(Intent.ACTION_VIEW);
            devintent.setData(Uri.parse("https://play.google.com/store/apps/details?id=org.minic.destinaionwaker"));
            startActivity(devintent);
        } else if (id == R.id.nav_send) {
            Intent devintent = new Intent(Intent.ACTION_VIEW);
            devintent.setData(Uri.parse("http://open.kakao.com/o/sVL5cNz"));
            startActivity(devintent);
        }else if (id == R.id.nav_explain) {
            Toast.makeText(this, "위치 서비스를 이용해 지정한 목적지 주변에서 알람을 울려주는 앱입니다. 하단 원형 추가버튼으로 목적지를 추가하세요.",Toast.LENGTH_LONG).show();
            Toast.makeText(this, "위치 서비스를 이용해 지정한 목적지 주변에서 알람을 울려주는 앱입니다. 하단 원형 추가버튼으로 목적지를 추가하세요.",Toast.LENGTH_LONG).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    AdapterView.OnItemClickListener gListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(getApplicationContext(), "목적지로 출발합니다.", Toast.LENGTH_SHORT).show();
            Intent mainintent = new Intent(getApplicationContext(),StartBus.class);

            mainintent.putExtra("id",a.get(position));
//            Toast.makeText(MainActivity.this,a.get(position)+"", Toast.LENGTH_SHORT).show();
            startActivityForResult(mainintent,1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadlist();
    }

    public void loadlist(){                     //리스트 불러오는 메소드 만듦
        dbHelper.getResult();               //총갯수 세기 위해
        //리스트 폼
        arGeneral = new ArrayList<String>();
        a= new ArrayList<Integer>();
        for (int i=0,j=0; i<=dbHelper.maxrow; i++ ){
            if (dbHelper.getResultrow(i).equals("")){
//                arGeneral.add("삭제됨");
            }else{
                arGeneral.add(dbHelper.getname(i));
                a.add(i);
            }
        }
//        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arGeneral);
        adapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_item_my, arGeneral);
        lv= (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter1);
        lv.setOnItemClickListener(gListener);

    }



}