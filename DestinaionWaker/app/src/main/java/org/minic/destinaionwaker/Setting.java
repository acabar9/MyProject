package org.minic.destinaionwaker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class Setting extends AppCompatActivity{
    private Spinner spinner;
    Button cancel,save;
    CheckBox btn1,btn2;

    DBHelperSettingManager dbHSM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
//        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
//        setSupportActionBar(toolbar);
        spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayList<String> list = new ArrayList<>();
        String[] list2 = new String[3];
        list2[0] = "기본알람1";
        list2[1] = "준비중";
        list2[2] = "준비중";

        ArrayAdapter spinnerAdapter;
        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list2);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(Setting.this, "선택된 아이템 : " + spinner.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dbHSM = new DBHelperSettingManager(getApplicationContext(), "SETTING_SQLITE.db", null, 1);
        CheckBox repeatChkBx = (CheckBox) findViewById(R.id.checkBox1);

        if(dbHSM.playBellOnVibeMode() ==1){
            repeatChkBx.setChecked(true);
        }else{
            repeatChkBx.setChecked(false);
        }


        repeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isCheckedBox) {

                if (isCheckedBox) {
                    //체크되면 DB에 true 등록
                    dbHSM.updatesetting(1);
                } else{
                    dbHSM.updatesetting(0);
                }
            }
        });
    }


}

