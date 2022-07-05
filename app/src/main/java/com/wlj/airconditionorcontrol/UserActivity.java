package com.wlj.airconditionorcontrol;


import static com.wlj.airconditionorcontrol.MainActivity.wifisocket;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wlj.airconditionorcontrol.tools.DeviceFragment;
import com.wlj.airconditionorcontrol.tools.IntentUtils;

public class UserActivity extends AppCompatActivity {

    public static int isuserlogin = 1;//0为登录，1为未登录
    public static String[] device = new String[5];//绑定设备
    public static String titleuserid = "";
    public static int i_device = 0;
    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;
    public static DeviceFragment DeviceFragment;

    private ImageView user_imageview_return;
    private ImageView user_imageview_add;
    private Button user_button_cancel;
    private TextView user_textview_userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //获取Fragment事务

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        init();
        for(int i = 0; i < 5; i++){
            device[i] = wifisocket.getdevicestring(i);
        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        DeviceFragment = new DeviceFragment();
        fragmentTransaction.replace(R.id.fragment_device, DeviceFragment);
        fragmentTransaction.commit();

        titleuserid = MainActivity.myPreferences.getString("titleuserid", "");
        user_textview_userid.setText(titleuserid);

        user_imageview_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                final EditText editText = new EditText(UserActivity.this);
                builder.setTitle("绑定设备");
                builder.setView(editText);
                builder.setPositiveButton("确定",null);
                builder.setNegativeButton("取消",null);
                final AlertDialog dialog = builder.create();
                dialog.show();
                //确定按钮
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //设置UDPSocket的IP地址
                        int i = 0;

                        if(!"".equals(editText.getText())) {
                            if(editText.getText().toString().length() != 10){
                                Toast.makeText(UserActivity.this, "设备ID须为10位，请重新输入", Toast.LENGTH_SHORT).show();
                            }else {
                                if (i_device < 5) {
                                    wifisocket.setInstallationid(editText.getText().toString());
                                    wifisocket.setUserid(titleuserid);
                                    MainActivity.isbinddevice = 1;
                                    while (i < 50) {
                                        if (MainActivity.isservercon == 0) {
                                            if (MainActivity.isbinddevicesuccess == 0) {
                                                Toast.makeText(UserActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                                                i_device++;
                                                break;
                                            } else if (MainActivity.isbinddevicesuccess == 1) {
                                                Toast.makeText(UserActivity.this, "绑定失败，请重试", Toast.LENGTH_SHORT).show();
                                                break;
                                            } else if (MainActivity.isbinddevicesuccess == 2) {
                                                Toast.makeText(UserActivity.this, "设备已被其他用户绑定，请重试", Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                        } else if (MainActivity.isservercon == 1) {
                                            Toast.makeText(UserActivity.this, "服务器未连接", Toast.LENGTH_SHORT).show();
                                            MainActivity.isbinddevicesuccess = 3;
                                            break;
                                        }
                                        i++;
                                    }
                                    if (i == 50) {
                                        Toast.makeText(UserActivity.this, "绑定出错，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(UserActivity.this, "已绑定五台设备", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(UserActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //取消按钮
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(UserActivity.this, "取消绑定", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        /*
        user_button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isuserlogin = 1;
                LoginActivity.isremchecked = 1;
                MainActivity.editor.putInt("isuserlogin", isuserlogin);
                MainActivity.editor.putString("titleuserid", "");
                MainActivity.editor.commit();
                LoginActivity.login_psw_str = "";
                LoginActivity.login_userid_str = "";
                com.example.airquality.tools.wifisocket.setUserid("");
                com.example.airquality.tools.wifisocket.setPassword("");
                IntentUtils.SetIntent(UserActivity.this, LoginActivity.class);
            }
        });
        */
    }

    public static void setTitleuserid(String Userid){
        titleuserid = Userid;
        MainActivity.editor.putString("titleuserid", titleuserid);
        MainActivity.editor.commit();
    }

    public static String getTitleuserid(){
        return titleuserid;
    }

    private void init(){
        // user_imageview_return = findViewById(R.id.user_imageview_return);
        user_button_cancel =findViewById(R.id.user_button_cancel);
        user_textview_userid = findViewById(R.id.user_textview_userid);
        user_imageview_add = findViewById(R.id.user_imageview_add);
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