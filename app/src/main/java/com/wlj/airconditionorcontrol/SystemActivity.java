package com.wlj.airconditionorcontrol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class SystemActivity extends AppCompatActivity {

    private TextView text_system_equipmentid_data;
    private TextView text_system_engine_data;
    private TextView text_system_app_data;
    private TextView text_system_ladder_tvoc_data;
    private TextView text_system_ladder_pm_data;
    private TextView text_system_ladder_ch2o_data;
    private TextView text_system_ladder_co2_data;
    private Button button_system_ladder_tvoc_data_refresh;
    private Button button_system_ladder_pm_data_refresh;
    private Button button_system_ladder_ch2o_data_refresh;
    private Button button_system_ladder_co2_data_refresh;
    private Button button_system_update;

    public static String engine_data;
    public static String ladder_data;
    public static byte[] equipmentid = new byte[10];

    public static int isdonwload = 0;

    public static int downloadprocess = 0;

    public static final int UPDATE_PROCESS_RESET = 0;
    public static final int UPDATE_PROCESS_SartDownload = 1;
    public static final int UPDATE_PROCESS_AuthenticationRequest = 2;
    public static final int UPDATE_PROCESS_RebootDevice = 3;
    public static final int UPDATE_PROCESS_HandShake = 4;
    public static final int UPDATE_PROCESS_DownloadData = 5;
    public static final int UPDATE_PROCESS_SUCCESS = 6;
    public static final int UPDATE_PROCESS_FAIL = 7;
    public static final int UPDATE_PROCESS_SartDownload_FAIL = 8;
    public static final int UPDATE_PROCESS_AuthenticationRequest_FAIL = 9;
    public static final int UPDATE_PROCESS_RebootDevice_FAIL = 10;
    public static final int UPDATE_PROCESS_HandShake_FAIL = 11;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//???????????????????????????????????????
        getSupportActionBar().setHomeButtonEnabled(true); //?????????????????????

        init();
        progressDialog = new ProgressDialog(this);
        text_system_equipmentid_data.setText(Arrays.toString(equipmentid).replace(",", "").replace("[", "").replace("]", ""));
        text_system_engine_data.setText(engine_data);
        text_system_ladder_tvoc_data.setText(ladder_data);
        text_system_ladder_pm_data.setText(ladder_data);
        text_system_ladder_ch2o_data.setText(ladder_data);
        text_system_ladder_co2_data.setText(ladder_data);

        button_system_ladder_tvoc_data_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SystemActivity.this, "?????????TVOC???????????????", Toast.LENGTH_SHORT).show();
            }
        });

        button_system_ladder_pm_data_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SystemActivity.this, "?????????PM2.5???????????????", Toast.LENGTH_SHORT).show();
            }
        });

        button_system_ladder_ch2o_data_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SystemActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });

        button_system_ladder_co2_data_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SystemActivity.this, "?????????Co2???????????????", Toast.LENGTH_SHORT).show();
            }
        });

        button_system_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowProgressDialog();
                isdonwload = 1;
            }
        });

    }

    private void ShowProgressDialog(){
        progressDialog.setTitle("????????????");
        progressDialog.setMessage("????????????");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//????????????
        progressDialog.setMax(100);//?????????????????????
        progressDialog.setProgress(0);//???????????????,???????????????????????????
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        if(MainActivity.iswindcon == 5){
            progressDialog.setMessage("??????????????????");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setButton("??????",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // TODO ???????????????????????????
                            progressDialog.dismiss();//???????????????

                        }
                    });
        }

        progressDialog.onStart();
        progressDialog.show();
    }

    private void init() {
        text_system_equipmentid_data = findViewById(R.id.text_system_equipmentid_data);
        text_system_engine_data = findViewById(R.id.text_system_engine_data);
        text_system_app_data = findViewById(R.id.text_system_app_data);

        text_system_ladder_tvoc_data = findViewById(R.id.text_system_ladder_tvoc_data);
        text_system_ladder_pm_data = findViewById(R.id.text_system_ladder_pm_data);
        text_system_ladder_ch2o_data = findViewById(R.id.text_system_ladder_ch2o_data);
        text_system_ladder_co2_data = findViewById(R.id.text_system_ladder_co2_data);

        button_system_ladder_tvoc_data_refresh = findViewById(R.id.button_system_ladder_tvoc_data_refresh);
        button_system_ladder_pm_data_refresh = findViewById(R.id.button_system_ladder_pm_data_refresh);
        button_system_ladder_ch2o_data_refresh = findViewById(R.id.button_system_ladder_ch2o_data_refresh);
        button_system_ladder_co2_data_refresh = findViewById(R.id.button_system_ladder_co2_data_refresh);

        button_system_update = findViewById(R.id.button_system_update);
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
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
