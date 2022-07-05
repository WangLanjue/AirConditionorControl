package com.wlj.airconditionorcontrol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class RoomActivity extends AppCompatActivity {

    public static short [] room_data = new short[100];

    private TextView text_room_tem_data1;
    private TextView text_room_hum_data1;
    private TextView text_room_tvoc_data1;
    private TextView text_room_pm_data1;
    private TextView text_room_ch2o_data1;
    private TextView text_room_co2_data1;
    private View view_room1;

    private TextView text_room_tem_data2;
    private TextView text_room_hum_data2;
    private TextView text_room_tvoc_data2;
    private TextView text_room_pm_data2;
    private TextView text_room_ch2o_data2;
    private TextView text_room_co2_data2;
    private View view_room2;

    private TextView text_room_tem_data3;
    private TextView text_room_hum_data3;
    private TextView text_room_tvoc_data3;
    private TextView text_room_pm_data3;
    private TextView text_room_ch2o_data3;
    private TextView text_room_co2_data3;
    private View view_room3;

    private TextView text_room_tem_data4;
    private TextView text_room_hum_data4;
    private TextView text_room_tvoc_data4;
    private TextView text_room_pm_data4;
    private TextView text_room_ch2o_data4;
    private TextView text_room_co2_data4;
    private View view_room4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        // init();//初始化控件
        // data();//赋值
    }

    private void init(){

        text_room_tem_data1 = findViewById(R.id.text_room_tem_data1);
        text_room_hum_data1 = findViewById(R.id.text_room_hum_data1);
        text_room_tvoc_data1 = findViewById(R.id.text_room_tvoc_data1);
        text_room_pm_data1 = findViewById(R.id.text_room_pm_data1);
        text_room_ch2o_data1 = findViewById(R.id.text_room_ch2o_data1);
        text_room_co2_data1 = findViewById(R.id.text_room_co2_data1);
        view_room1 = findViewById(R.id.view_room1);

        text_room_tem_data2 = findViewById(R.id.text_room_tem_data2);
        text_room_hum_data2 = findViewById(R.id.text_room_hum_data2);
        text_room_tvoc_data2 = findViewById(R.id.text_room_tvoc_data2);
        text_room_pm_data2 = findViewById(R.id.text_room_pm_data2);
        text_room_ch2o_data2 = findViewById(R.id.text_room_ch2o_data2);
        text_room_co2_data2 = findViewById(R.id.text_room_co2_data2);
        view_room2 = findViewById(R.id.view_room2);

        text_room_tem_data3 = findViewById(R.id.text_room_tem_data3);
        text_room_hum_data3 = findViewById(R.id.text_room_hum_data3);
        text_room_tvoc_data3 = findViewById(R.id.text_room_tvoc_data3);
        text_room_pm_data3 = findViewById(R.id.text_room_pm_data3);
        text_room_ch2o_data3 = findViewById(R.id.text_room_ch2o_data3);
        text_room_co2_data3 = findViewById(R.id.text_room_co2_data3);
        view_room3 = findViewById(R.id.view_room3);

        text_room_tem_data4 = findViewById(R.id.text_room_tem_data4);
        text_room_hum_data4 = findViewById(R.id.text_room_hum_data4);
        text_room_tvoc_data4 = findViewById(R.id.text_room_tvoc_data4);
        text_room_pm_data4 = findViewById(R.id.text_room_pm_data4);
        text_room_ch2o_data4 = findViewById(R.id.text_room_ch2o_data4);
        text_room_co2_data4 = findViewById(R.id.text_room_co2_data4);
        view_room4 = findViewById(R.id.view_room4);
    }

    @SuppressLint("SetTextI18n")
    public void data(){
        NumberFormat nf = new DecimalFormat("#.###");
        text_room_tem_data1.setText(room_data[1]/10 + "℃");
        text_room_hum_data1.setText(room_data[2]/10 + "%");
        text_room_tvoc_data1.setText(nf.format(room_data[3]*0.001) + "PPM");
        text_room_pm_data1.setText(nf.format(room_data[4]*0.001) + "mg/m3");
        text_room_ch2o_data1.setText(nf.format(room_data[5]*0.001) + "PPM");
        text_room_co2_data1.setText(room_data[6] + "PPM");

        text_room_tem_data2.setText(room_data[11]/10 + "℃");
        text_room_hum_data2.setText(room_data[12]/10 + "%");
        text_room_tvoc_data2.setText(nf.format(room_data[13]*0.001) + "PPM");
        text_room_pm_data2.setText(nf.format(room_data[14]*0.001) + "mg/m3");
        text_room_ch2o_data2.setText(nf.format(room_data[15]*0.001) + "PPM");
        text_room_co2_data2.setText(room_data[16] + "PPM");

        text_room_tem_data3.setText(room_data[21]/10 + "℃");
        text_room_hum_data3.setText(room_data[22]/10 + "%");
        text_room_tvoc_data3.setText(nf.format(room_data[23]*0.001) + "PPM");
        text_room_pm_data3.setText(nf.format(room_data[24]*0.001) + "mg/m3");
        text_room_ch2o_data3.setText(nf.format(room_data[25]*0.001) + "PPM");
        text_room_co2_data3.setText(room_data[26] + "PPM");

        text_room_tem_data4.setText(room_data[31]/10 + "℃");
        text_room_hum_data4.setText(room_data[32]/10 + "%");
        text_room_tvoc_data4.setText(nf.format(room_data[33]*0.001) + "PPM");
        text_room_pm_data4.setText(nf.format(room_data[34]*0.001) + "mg/m3");
        text_room_ch2o_data4.setText(nf.format(room_data[35]*0.001) + "PPM");
        text_room_co2_data4.setText(room_data[36] + "PPM");

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
}
