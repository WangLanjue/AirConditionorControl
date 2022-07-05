package com.wlj.airconditionorcontrol;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.wlj.airconditionorcontrol.tools.pageradapter;

import java.util.ArrayList;


//保存数据
public class DataActivity extends AppCompatActivity {

    private ViewPager vpager;
    private ArrayList<View> aList;
    private ArrayList<String> sList;
    public static pageradapter mAdapter;

    int prePosition = 0;

    public static byte[] write_ch2o = new byte[2];
    public static byte[] write_co2 = new byte[2];
    public static byte[] write_tvoc = new byte[2];
    public static byte[] write_pm = new byte[2];

    public static int write_ch2o_len = 0;
    public static int write_co2_len = 0;
    public static int write_tvoc_len = 0;
    public static int write_pm_len = 0;

    public static EditText text_data_tvoc_data1;
    public static EditText text_data_pm_data1;
    public static EditText text_data_ch2o_data1;
    public static EditText text_data_co2_data1;
    private EditText text_data_TVOC_backlash_data1;
    private EditText text_data_pm_backlash_data1;
    private EditText text_data_ch2o_backlash_data1;
    private EditText text_data_co2_backlash_data1;

    public static EditText text_data_tvoc_data2;
    public static EditText text_data_pm_data2;
    public static EditText text_data_ch2o_data2;
    public static EditText text_data_co2_data2;
    private EditText text_data_TVOC_backlash_data2;
    private EditText text_data_pm_backlash_data2;
    private EditText text_data_ch2o_backlash_data2;
    private EditText text_data_co2_backlash_data2;


    public static EditText text_data_tvoc_data3;
    public static EditText text_data_pm_data3;
    public static EditText text_data_ch2o_data3;
    public static EditText text_data_co2_data3;
    private EditText text_data_TVOC_backlash_data3;
    private EditText text_data_pm_backlash_data3;
    private EditText text_data_ch2o_backlash_data3;
    private EditText text_data_co2_backlash_data3;


    public static short [] system_data = new short[100];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        if(MainActivity.iswindcon > 1) {
            Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
        }
        vpager = findViewById(R.id.vpager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        aList = new ArrayList<View>();
        LayoutInflater li = getLayoutInflater();
        aList.add(li.inflate(R.layout.activity_data1,null,false));
        aList.add(li.inflate(R.layout.activity_data2,null,false));
        aList.add(li.inflate(R.layout.activity_data3, null, false));

        sList = new ArrayList<String>();
        sList.add("一挡");
        sList.add("二挡");
        sList.add("三挡");
        mAdapter = new pageradapter(aList,sList);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int i) {

                switch (i) {
                    case 0:
                        init(0);
                        setData(0);
                        setClick(0);
                        break;
                    case 1:
                        init(1);
                        setData(1);
                        setClick(1);
                        break;
                    case 2:
                        init(2);
                        setData(2);
                        setClick(2);
                        break;
                    default:

                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MainActivity.page=MainActivity.MAIN_PAGE;
                finish();
                break;
        }
        return true;
    }

    private void init(int i){
        if(i == 0){
            text_data_tvoc_data1 = findViewById(R.id.text_data_tvoc_data1);
            text_data_pm_data1 = findViewById(R.id.text_data_pm_data1);
            text_data_ch2o_data1 = findViewById(R.id.text_data_ch2o_data1);
            text_data_co2_data1 = findViewById(R.id.text_data_co2_data1);
            text_data_TVOC_backlash_data1 = findViewById(R.id.text_data_TVOC_backlash_data1);
            text_data_pm_backlash_data1 = findViewById(R.id.text_data_pm_backlash_data1);
            text_data_ch2o_backlash_data1 = findViewById(R.id.text_data_ch2o_backlash_data1);
            text_data_co2_backlash_data1 = findViewById(R.id.text_data_co2_backlash_data1);
        } else if(i == 1){
            text_data_tvoc_data2 = findViewById(R.id.text_data_tvoc_data2);
            text_data_pm_data2 = findViewById(R.id.text_data_pm_data2);
            text_data_ch2o_data2 = findViewById(R.id.text_data_ch2o_data2);
            text_data_co2_data2 = findViewById(R.id.text_data_co2_data2);
            text_data_TVOC_backlash_data2 = findViewById(R.id.text_data_TVOC_backlash_data2);
            text_data_pm_backlash_data2 = findViewById(R.id.text_data_pm_backlash_data2);
            text_data_ch2o_backlash_data2 = findViewById(R.id.text_data_ch2o_backlash_data2);
            text_data_co2_backlash_data2 = findViewById(R.id.text_data_co2_backlash_data2);
        } else if(i == 2){
            text_data_tvoc_data3 = findViewById(R.id.text_data_tvoc_data3);
            text_data_pm_data3 = findViewById(R.id.text_data_pm_data3);
            text_data_ch2o_data3 = findViewById(R.id.text_data_ch2o_data3);
            text_data_co2_data3 = findViewById(R.id.text_data_co2_data3);
            text_data_TVOC_backlash_data3 = findViewById(R.id.text_data_TVOC_backlash_data3);
            text_data_pm_backlash_data3 = findViewById(R.id.text_data_pm_backlash_data3);
            text_data_ch2o_backlash_data3 = findViewById(R.id.text_data_ch2o_backlash_data3);
            text_data_co2_backlash_data3 = findViewById(R.id.text_data_co2_backlash_data3);
        }
    }

    private void setData(int i){
        if(i == 0){
            text_data_tvoc_data1.setText(system_data[3] * 0.001 + "PPM");
            text_data_pm_data1.setText(system_data[4] * 0.001 + "mg/m3");
            text_data_ch2o_data1.setText(system_data[1] * 0.001 + "PPM");
            text_data_co2_data1.setText(system_data[2] + "PPM");
            text_data_TVOC_backlash_data1.setText(String.valueOf(system_data[7]));
            text_data_pm_backlash_data1.setText(String.valueOf(system_data[8]));
            text_data_ch2o_backlash_data1.setText(String.valueOf(system_data[5]));
            text_data_co2_backlash_data1.setText(String.valueOf(system_data[6]));
        } else if(i == 1){
            text_data_tvoc_data2.setText(system_data[13] * 0.001 + "PPM");
            text_data_pm_data2.setText(system_data[14] * 0.001 + "mg/m3");
            text_data_ch2o_data2.setText(system_data[11] * 0.001 + "PPM");
            text_data_co2_data2.setText(system_data[12] + "PPM");
            text_data_TVOC_backlash_data2.setText(String.valueOf(system_data[17]));
            text_data_pm_backlash_data2.setText(String.valueOf(system_data[18]));
            text_data_ch2o_backlash_data2.setText(String.valueOf(system_data[15]));
            text_data_co2_backlash_data2.setText(String.valueOf(system_data[16]));
        } else if(i == 2){
            text_data_tvoc_data3.setText(system_data[23] * 0.001 + "PPM");
            text_data_pm_data3.setText(system_data[24] * 0.001 + "mg/m3");
            text_data_ch2o_data3.setText(system_data[21] * 0.001 + "PPM");
            text_data_co2_data3.setText(system_data[22] + "PPM");
            text_data_TVOC_backlash_data3.setText(String.valueOf(system_data[27]));
            text_data_pm_backlash_data3.setText(String.valueOf(system_data[28]));
            text_data_ch2o_backlash_data3.setText(String.valueOf(system_data[25]));
            text_data_co2_backlash_data3.setText(String.valueOf(system_data[26]));
        }
    }

    private void setClick(int i){
        if(i == 0){
            aList.get(0).findViewById(R.id.button_data_refresh1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        setData(0);
                        Toast.makeText(DataActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            aList.get(0).findViewById(R.id.button_data_save1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        write_ch2o = text_data_ch2o_data1.getText().toString().replace("[", "").getBytes();
                        write_ch2o_len = text_data_ch2o_data1.getText().length();

                        write_co2 = text_data_co2_data1.getText().toString().replace("[", "").getBytes();
                        write_co2_len = text_data_co2_data1.getText().length();

                        write_tvoc = text_data_tvoc_data1.getText().toString().replace("[", "").getBytes();
                        write_tvoc_len = text_data_tvoc_data1.getText().length();

                        write_pm = text_data_pm_data1.getText().toString().replace("[", "").getBytes();
                        write_pm_len = text_data_pm_data1.getText().length();
                        MainActivity.isdataclick = 11;
                        Toast.makeText(DataActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            aList.get(0).findViewById(R.id.button_data_send1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        MainActivity.isconclick = 3;
                        Toast.makeText(DataActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if(i == 1){
            aList.get(1).findViewById(R.id.button_data_refresh2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        setData(1);
                        Toast.makeText(DataActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            aList.get(1).findViewById(R.id.button_data_save2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        write_ch2o = text_data_ch2o_data2.getText().toString().replace("[", "").getBytes();
                        write_ch2o_len = text_data_ch2o_data2.getText().length();

                        write_co2 = text_data_co2_data2.getText().toString().replace("[", "").getBytes();
                        write_co2_len = text_data_co2_data2.getText().length();

                        write_tvoc = text_data_tvoc_data2.getText().toString().replace("[", "").getBytes();
                        write_tvoc_len = text_data_tvoc_data2.getText().length();

                        write_pm = text_data_pm_data2.getText().toString().replace("[", "").getBytes();
                        write_pm_len = text_data_pm_data2.getText().length();
                        MainActivity.isdataclick = 21;
                        Toast.makeText(DataActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            aList.get(1).findViewById(R.id.button_data_send2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        MainActivity.isconclick = 3;
                        Toast.makeText(DataActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if(i == 2){
            aList.get(2).findViewById(R.id.button_data_refresh3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        setData(2);
                        Toast.makeText(DataActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            aList.get(2).findViewById(R.id.button_data_save3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        write_ch2o = text_data_ch2o_data3.getText().toString().replace("[", "").getBytes();
                        write_ch2o_len = text_data_ch2o_data3.getText().length();

                        write_co2 = text_data_co2_data3.getText().toString().replace("[", "").getBytes();
                        write_co2_len = text_data_co2_data3.getText().length();

                        write_tvoc = text_data_tvoc_data3.getText().toString().replace("[", "").getBytes();
                        write_tvoc_len = text_data_tvoc_data3.getText().length();

                        write_pm = text_data_pm_data3.getText().toString().replace("[", "").getBytes();
                        write_pm_len = text_data_pm_data3.getText().length();
                        MainActivity.isdataclick = 31;
                        Toast.makeText(DataActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            aList.get(2).findViewById(R.id.button_data_send3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.iswindcon > 1){
                        Toast.makeText(DataActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                    }else {
                        MainActivity.isconclick = 3;
                        Toast.makeText(DataActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

