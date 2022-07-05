package com.wlj.airconditionorcontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    public static String local_wifi_ip1;
    public static String local_wifi_ip2;
    public static String air_wifi_ip;
    public static byte[] read_local_wifi_id = new byte[16];
    public static byte[] read_local_wifi_password = new byte[16];
    public static byte[] read_router_wifi_id = new byte[16];
    public static byte[] read_router_wifi_password = new byte[16];
    public static byte[] write_local_wifi_id = new byte[16];
    public static byte[] write_local_wifi_password = new byte[16];
    public static byte[] write_router_wifi_id = new byte[16];
    public static byte[] write_router_wifi_password = new byte[16];
    public static short local_wifi_channel;
    public static int isconclicksuccess = 0;

    private String local_router_wifi_id;
    private String local_router_wifi_password;
    private int local_wifi = 1;
    private int router_wifi = 2;

    private int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    public static int write_local_wifi_id_len = 0;
    public static int write_local_wifi_password_len = 0;
    public static int write_router_wifi_id_len = 0;
    public static int write_router_wifi_password_len = 0;

    public static EditText text_wifi_ip_data;
    public static EditText text_local_wifi_name_data;
    public static EditText text_local_wifi_password_data;
    public static EditText text_local_wifi_channel_data;
    public static  EditText text_air_ip_data;
    public static EditText text_air_port1_data;
    public static  EditText text_air_port2_data;
    public static  EditText text_router_wifi_name_data;
    public static EditText text_router_wifi_password_data;
    public static EditText text_timeflag_data;
    public static  EditText text_time_data;
    public static Button button_con_refresh;
    public static Button button_con_save;
    public static Button button_con_send;

    View view;
    ListView listView;
    List<String> wifilist = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        init();



        text_router_wifi_name_data.setInputType(InputType.TYPE_NULL);
        text_local_wifi_name_data.setInputType(InputType.TYPE_NULL);

        button_con_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                if(MainActivity.iswindcon > 1){
                    Toast.makeText(ConnectActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                }else {
                    MainActivity.isconclick = 0;
                    try {
                        String s_local_wifi_id = new String(read_local_wifi_id, "UTF-8");
                        String s_local_wifi_password = new String(read_local_wifi_password, "UTF-8");
                        String s_router_wifi_id = new String(read_router_wifi_id, "UTF-8");
                        String s_router_wifi_password = new String(read_router_wifi_password, "UTF-8");
                        text_wifi_ip_data.setText(local_wifi_ip2);
                        text_air_ip_data.setText(air_wifi_ip);
                        text_local_wifi_name_data.setText(s_local_wifi_id);
                        text_local_wifi_password_data.setText(s_local_wifi_password);
                        text_router_wifi_name_data.setText(s_router_wifi_id);
                        text_router_wifi_password_data.setText(s_router_wifi_password);
                        text_local_wifi_channel_data.setText(String.valueOf(local_wifi_channel));
                        Toast.makeText(ConnectActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                        isconclicksuccess = 0;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        button_con_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                if(MainActivity.iswindcon > 1){
                    Toast.makeText(ConnectActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ConnectActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    MainActivity.isconclick = 0;
                    write_local_wifi_id = Arrays.toString(new Editable[]{text_local_wifi_name_data.getText()}).replace("[","").getBytes();
                    write_local_wifi_id_len = text_local_wifi_name_data.getText().length();

                    write_local_wifi_password = Arrays.toString(new Editable[]{text_local_wifi_password_data.getText()}).replace("[","").getBytes();
                    write_local_wifi_password_len = text_local_wifi_password_data.getText().length();

                    write_router_wifi_id = Arrays.toString(new Editable[]{text_router_wifi_name_data.getText()}).replace("[","").getBytes();
                    write_router_wifi_id_len = text_router_wifi_name_data.getText().length();

                    write_router_wifi_password = Arrays.toString(new Editable[]{text_router_wifi_password_data.getText()}).replace("[","").getBytes();
                    write_router_wifi_password_len = text_router_wifi_password_data.getText().length();
                    MainActivity.saveflag = true;

                }


            }
        });

        button_con_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                if(MainActivity.iswindcon > 1){
                    Toast.makeText(ConnectActivity.this, "未连接手持器", Toast.LENGTH_SHORT).show();
                }else {
                    MainActivity.isconclick = 2;
                    MainActivity.xiugaiflag=true;
                    Toast.makeText(ConnectActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(ConnectActivity.this, "检测连接中，请勿关闭", Toast.LENGTH_SHORT).show();
//                    while(i < 99){
//                        if(isconclicksuccess == 20) {
//                            Toast.makeText(ConnectActivity.this, "修改成功，请重启手持器", Toast.LENGTH_SHORT).show();
//                            isconclicksuccess = 0;
//                            break;
//                        } else if(isconclicksuccess == 21){
//                            Toast.makeText(ConnectActivity.this, "检测风机未连接，修改失败", Toast.LENGTH_SHORT).show();
//                            isconclicksuccess = 0;
//                            break;
//                        } else {
//                            i++;
//                        }
//                    }
//                    if(i == 99){
//                        Toast.makeText(ConnectActivity.this, "修改失败，请检测风机与手持器连接情况", Toast.LENGTH_SHORT).show();

                }
            }
        });

        text_local_wifi_name_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListDialog(local_wifi);
            }
        });

        text_router_wifi_name_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListDialog(router_wifi);
            }
        });
    }

    public static void setIsconclicksuccess(int success){
        isconclicksuccess = success;
    }

    private void init(){
        text_wifi_ip_data = findViewById(R.id.text_wifi_ip_data);
        text_local_wifi_name_data = findViewById(R.id.text_local_wifi_name_data);
        text_local_wifi_password_data = findViewById(R.id.text_local_wifi_password_data);
        text_local_wifi_channel_data = findViewById(R.id.text_local_wifi_channel_data);
        text_air_ip_data = findViewById(R.id.text_air_ip_data);
        text_air_port1_data = findViewById(R.id.text_air_port1_data);
        text_air_port2_data = findViewById(R.id.text_air_port2_data);
        text_router_wifi_name_data = findViewById(R.id.text_router_wifi_name_data);
        text_router_wifi_password_data = findViewById(R.id.text_router_wifi_password_data);

        button_con_refresh = findViewById(R.id.button_con_refresh);
        button_con_save = findViewById(R.id.button_con_save);
        button_con_send = findViewById(R.id.button_con_send);
    }

    @SuppressLint("InflateParams")
    private void showListDialog(int i){
        registerPermission();//动态获取权限
        view = getLayoutInflater().inflate(R.layout.connectactivity_listview, null);
        listView = view.findViewById(R.id.connect_listview);
        adapter = new ArrayAdapter<String>(ConnectActivity.this, android.R.layout.simple_list_item_1, wifilist);
        listView.setAdapter(adapter);
        if(i == local_wifi){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    text_local_wifi_name_data.setText(wifilist.get(position));
                }
            });
        } else if(i == router_wifi){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    text_router_wifi_name_data.setText(wifilist.get(position));
                }
            });
        }
        AlertDialog.Builder listDialog = new AlertDialog.Builder(ConnectActivity.this);
        listDialog.setView(view);
        listDialog.setTitle("WIFI列表");
        listDialog.show();
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

    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public String getWIFISSID(Activity activity) {
        String ssid="unknown id";

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O||Build.VERSION.SDK_INT==Build.VERSION_CODES.P) {

            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT==Build.VERSION_CODES.O_MR1){

            ConnectivityManager connManager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo()!=null){
                    return networkInfo.getExtraInfo().replace("\"","");
                }
            }
        }
        return ssid;
    }

    public List<String> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        List<String> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult.SSID);
                    }
                }
            }
        }
        return wifiList;
    }

    private void registerPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

        } else {
            wifilist = getWifiList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            wifilist = getWifiList();
        }
    }
}
