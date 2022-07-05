package com.wlj.airconditionorcontrol;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.wlj.airconditionorcontrol.Animation.progressbar;
import com.wlj.airconditionorcontrol.tools.IntentUtils;
import com.wlj.airconditionorcontrol.tools.UDPSocket;
import com.wlj.airconditionorcontrol.tools.upLoad;
import com.wlj.airconditionorcontrol.tools.wifiSocket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int EXTERNAL_STORAGE_REQ_CODE = 1;

    //界面控件
    private progressbar percentView;//自定义控件：进度条
    private TextView tvProgress;//控件：空气质量（优良中差）
    private TextView textview_tem;//文字控件：手持器温度值
    private TextView textview_humidity;//文字控件：手持器湿度值
    private TextView textview_ch2o_data;//文字控件：手持器甲醛值
    private TextView textview_co2_data;//文字控件：手持器CO2值
    private TextView textview_pm_data;//文字控件：手持器PM2.5值
    private TextView textview_tvoc_data;//文字控件：手持器TVOC值
    private TextView textview_wind_data;//文字控件：风机档位值
    private TextView text_out_tem;//文字控件：户外温度值
    private TextView text_out_hum;//文字控件：户外湿度值
    private Switch switch_wind_mode;//开关控件：风机模式
    private ImageView imagevwind_0;//图形控件：0档图片
    private ImageView imagevwind_1;//图形控件：1档图片
    private ImageView imagevwind_2;//图形控件：2档图片
    private ImageView imagevwind_3;//图形控件：3档图片
    private ImageView image_nav_header;
    private ImageView imageview_windcon;
    private ImageView imageview_servercon;
    private View view_up;
    private Toolbar toolbar;

    //显示数据
    private short progress = 0;//进度数值
    private short tem_data = 0;//手持器温度数值
    private short humidity_data = 0;//手持器湿度数值
    private short out_tem_data = 0;//户外温度数值
    private short out_humidity_data = 0;//户外湿度数值
    private short co2_data = 0;//手持器CO2数值
    private short ch2o_data = 0;//手持器甲醛数值
    private short tvoc_data = 0;//手持器TVOC数值
    private short pm_data = 0;//手持器PM2.5数值
    private short wind_data = 0;//风机档位
    private short wind_mode = 0;//风机模式
    private short[] air_data = new short[3];//空气质量（0为优，3为差）,airdate[0]airdate[1]互为更新数据和历史数据，

    public static SharedPreferences myPreferences;
    public static SharedPreferences.Editor editor;



    //套接字
    public static UDPSocket UIudpsocket;
    public static wifiSocket wifisocket = new wifiSocket();

    static {
        try {
            UIudpsocket = new UDPSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static upLoad upload = new upLoad();

    private volatile boolean stopReceiver;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private static final int PhonePort = 3020;
    private byte receBuf[] = new byte[1024];

    //输入输出控制
    final static int Edition = 1;
    final static int LAN_ip = 2;
    final static int air = 3;
    final static int Fan_data = 4;
    final static int localwifi_R = 5;
    final static int localwifi_W = 6;
    final static int lanwifi_R = 7;
    final static int lanwifi_W = 8;
    final static int control_R = 9;
    final static int controldata_R = 10;
    final static int controldata1_W = 11;
    final static int set0 = 12;
    final static int set1 = 13;
    final static int set2 = 14;
    final static int set3 = 15;
    final static int byhand = 16;
    final static int auto = 17;
    final static int room1=18;
    final static int room2=19;
    final static int room3=20;
    final static int room4=21;
    final static int getroom1=22;
    final static int getroom2=23;
    final static int getroom3=24;
    final static int getroom4=25;
    final static int storage=26;
    final static int routerwifi_R = 27;
    final static int routerwifi_W = 28;
    final static int controldata2_W = 29;
    final static int controldata3_W = 30;
    final static int windconnection = 31;//判断手持器与风机是否连接

    final static int wifisocket_airdata = 1;//读取风机数据
    final static int wifisocket_rigister = 2;//发送注册数据
    final static int wifisocket_device = 3;//绑定设备信息
    final static int wifisocket_gearposition = 4;//更改风机档位
    final static int wifisocket_login = 5;//用户登录
    //对应事件判断
    private int ischoice = 0;//判断是否选择房间保存
    private int isclicked = 5;//判断是否修改风机档位
    private int isroom = 0;//判断修改至哪个房间
    private int iscon = 999;//判断联网，为0时连接成功
    public static int iswindcon = 0;//判断设备连接情况，0为连接成功
    public static int isservercon = 0;//判断云服务器连接情况，0为连接成功
    public static int isconclick = 999;//判断连接界面按钮点击情况
    public static int isdataclick = 0;//判断数据界面按钮点击情况
    public static int isbinddevice = 2;//判断是否点击绑定设备
    public static int isreaddevice = 2;//判断是否读取绑定设备
    public static int isunbinddevice = 2;//判断是否解绑设备
    public static int isbinddevicesuccess = 2;//判断是否绑定设备
    public static int isreceivewrong = 0;//判断套接字是否接收错误
    public static int initairgrade = 0;//初始化空气质量指数
    public static int init_i = 0;//初始化
    public static int ishand = 0;//判断是否修改手动自动
    public static int control_thread = 0;//判断线程操作
    public static int air_grade_i = 0;//判断空气质量导入参数
    public static int testservercon = 0;//测试服务器是否连接
    public static short iswindconnection = 3;//判断风机与手持器是否连接
    public static int isloginsuccess = 3;//0为成功，1为失败，2为测试
    public static int conmode = 0;
    public static String userid = "";

    //Handle控制标志位
    public static final int UPDATE_DATA = 1;  //更新空气数据
    public static final int UPDATE_WINDDATA = 2;   //更新风力数据
    public static final int UPDATE_WIFI_WINDDATA = 3;  //通过服务器更新数据
    public static final int UPDATE_CONNECT = 4;     //wifi连接
    public static final int UPDATE_SYSTEM = 5;         //系统更新

    public static final int INIT_PAGE = 0;
    public static final int MAIN_PAGE = 1;
    public static final int  ROOM_PAGE= 2;
    public static final int DATA_PAGE = 3;
    public static final int CONNECT_PAGE = 4;
    public static final int SYSTEM_PAGE = 5;
    public static final int USER_PAGE = 6;
    public static final int REG_PAGE = 7;
    public static final int LOGIN_PAGE = 8;

    public static boolean levelflag=false;
    public static boolean saveflag=false;
    public static boolean xiugaiflag=false;
    public static boolean connectflag=true;

    public static int page=0;
    public int isclickedWrite =0;

    thread_main thread_main = new thread_main();



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_DATA://更新UI
                    air_data[0] = (short) myPreferences.getInt("air_data0", 0);
                    air_data[1] = (short) myPreferences.getInt("air_data1", 0);
                    tem_data = (short) myPreferences.getInt("tem_data", 0);
                    humidity_data = (short) myPreferences.getInt("humidity_data", 0);
                    out_tem_data = (short) myPreferences.getInt("out_tem_data", 0);
                    out_humidity_data = (short) myPreferences.getInt("out_humidity_data", 0);
                    ch2o_data = (short) myPreferences.getInt("ch2o_data", 0);
                    co2_data = (short) myPreferences.getInt("co2_data", 0);
                    pm_data = (short) myPreferences.getInt("pm_data", 0);
                    tvoc_data = (short) myPreferences.getInt("tvoc_data", 0);


                    //设置优良中差进度条

                    if(initairgrade == 0 || (air_data[0] != air_data[1])){
                        if (air_data[2] == 0) {
                            progress = 20;
                            percentView.setProgress(progress);//初始化进度
                            tvProgress.setText("优");
                            view_up.setBackgroundColor(Color.rgb(74, 204, 117));
                            percentView.setBackgroundColor(Color.rgb(74, 204, 117));
                            toolbar.setBackgroundColor(Color.rgb(74, 204, 117));
                        } else if (air_data[2] == 1) {
                            progress = 50;
                            tvProgress.setText("良");
                            percentView.setProgress(progress);//初始化进度
                            view_up.setBackgroundColor(Color.rgb(53, 151, 208));
                            percentView.setBackgroundColor(Color.rgb(53, 151, 208));
                            toolbar.setBackgroundColor(Color.rgb(53, 151, 208));
                        } else if (air_data[2] == 2) {
                            progress = 85;
                            tvProgress.setText("中");
                            percentView.setProgress(progress);//初始化进度
                            view_up.setBackgroundColor(Color.rgb(244, 164, 96));
                            percentView.setBackgroundColor(Color.rgb(244, 164, 96));
                            toolbar.setBackgroundColor(Color.rgb(244, 164, 96));
                        } else if (air_data[2] == 3) {
                            progress = 95;
                            tvProgress.setText("差");
                            percentView.setProgress(progress);//初始化进度
                            view_up.setBackgroundColor(Color.rgb(220, 20, 60));
                            percentView.setBackgroundColor(Color.rgb(220, 20, 60));
                            toolbar.setBackgroundColor(Color.rgb(220, 20, 60));
                        }
                    }
                    initairgrade++;
                    if(initairgrade > 100){
                        initairgrade = 1;
                    }

                    //风机连接情况
                    if (iswindcon ==1 ) {
                        imageview_windcon.setImageResource(R.drawable.wind_white);
                    } else {
                        imageview_windcon.setImageResource(R.drawable.wind_gray);
                        initData();
                    }

                    //服务器连接情况
                    if (isservercon == 1) {
                        imageview_servercon.setImageResource(R.drawable.server);
                    } else if (isservercon == 0) {
                        imageview_servercon.setImageResource(R.drawable.server_gray);
                    }


                    //参数设置
                    textview_tem.setText(tem_data / 10 + "℃");
                    textview_humidity.setText(humidity_data / 10 + "%");
                    text_out_tem.setText("户外温度：" + out_tem_data / 10 + "℃");//户外
                    text_out_hum.setText("户外湿度：" + out_humidity_data / 10 + "%");
                    NumberFormat nf = new DecimalFormat("#.###");
                    textview_ch2o_data.setText(nf.format(ch2o_data * 0.001));
                    textview_co2_data.setText(String.valueOf(co2_data));
                    textview_pm_data.setText(String.valueOf(nf.format(pm_data * 0.001)));
                    textview_tvoc_data.setText(String.valueOf(nf.format(tvoc_data * 0.001)));


                    break;

                case UPDATE_WINDDATA:
                    //档位设置
                    if(isreceivewrong == 1){
                        isreceivewrong = 0;
                    }
                    if (isclicked%4 == 0) {
                        imagevwind_0.setImageDrawable(getResources().getDrawable(R.drawable.wind_off));
                        imagevwind_1.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_2.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_3.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                    } else if (isclicked%4 == 1) {
                        imagevwind_0.setImageDrawable(getResources().getDrawable(R.drawable.wind_off_uncheck));
                        imagevwind_1.setImageDrawable(getResources().getDrawable(R.drawable.wind));
                        imagevwind_2.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_3.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                    } else if (isclicked%4 == 2) {
                        imagevwind_0.setImageDrawable(getResources().getDrawable(R.drawable.wind_off_uncheck));
                        imagevwind_1.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_2.setImageDrawable(getResources().getDrawable(R.drawable.wind));
                        imagevwind_3.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                    } else if (isclicked%4 == 3) {
                        imagevwind_0.setImageDrawable(getResources().getDrawable(R.drawable.wind_off_uncheck));
                        imagevwind_1.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_2.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_3.setImageDrawable(getResources().getDrawable(R.drawable.wind));
                    }else if (isclicked%4 == 4) {
                        imagevwind_0.setImageDrawable(getResources().getDrawable(R.drawable.wind_off_uncheck));
                        imagevwind_1.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_2.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                        imagevwind_3.setImageDrawable(getResources().getDrawable(R.drawable.wind_uncheck));
                    }

                    if(isclicked>=4) {
                        switch_wind_mode.setChecked(true);
                        textview_wind_data.setText("当前风机挡位为自动"+(isclicked-4)+"挡");
                    }
                    else {
                        switch_wind_mode.setChecked(false);
                        textview_wind_data.setText("当前风机挡位为：" + isclicked + "挡");
                    }
                    break;

                case UPDATE_WIFI_WINDDATA:

                    if(wifisocket.Forwarding == 1){
                        Toast.makeText(MainActivity.this, "用户不存在，请重新登录", Toast.LENGTH_SHORT).show();
                    }else if(wifisocket.Forwarding == 2){
                        Toast.makeText(MainActivity.this, "设备不存在，请重新输入正确的设备号", Toast.LENGTH_SHORT).show();
                    }else if(wifisocket.Forwarding == 3){
                        Toast.makeText(MainActivity.this, "用户无权限，请先绑定当前设备号", Toast.LENGTH_SHORT).show();
                    }else if(wifisocket.Forwarding == 4){
                        Toast.makeText(MainActivity.this, "服务器异常，请重试", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UPDATE_CONNECT:

                    try {
                        String s_local_wifi_id = new String(ConnectActivity.read_local_wifi_id, "UTF-8");
                        String s_local_wifi_password = new String(ConnectActivity.read_local_wifi_password, "UTF-8");
                        String s_router_wifi_id = new String(ConnectActivity.read_router_wifi_id, "UTF-8");
                        String s_router_wifi_password = new String(ConnectActivity.read_router_wifi_password, "UTF-8");
                        ConnectActivity.text_wifi_ip_data.setText(ConnectActivity.local_wifi_ip2);
                        ConnectActivity.text_air_ip_data.setText(ConnectActivity.air_wifi_ip);
                        ConnectActivity.text_local_wifi_name_data.setText(s_local_wifi_id);
                        ConnectActivity.text_local_wifi_password_data.setText(s_local_wifi_password);
                        ConnectActivity. text_router_wifi_name_data.setText(s_router_wifi_id);
                        ConnectActivity.text_router_wifi_password_data.setText(s_router_wifi_password);
                        ConnectActivity. text_local_wifi_channel_data.setText(String.valueOf(ConnectActivity.local_wifi_channel));


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }



                    break;

                case UPDATE_SYSTEM:


                    break;

                default:
                    break;
            }

        }
    };

    public void initData(){

        editor.putInt("humidity_data", 0);
        editor.putInt("tem_data", 0);
        editor.putInt("out_tem_data", 0);
        editor.putInt("out_humidity_data", 0);
        editor.putInt("ch2o_data", 0);
        editor.putInt("co2_data", 0);
        editor.putInt("pm_data", 0);
        editor.putInt("tvoc_data", 0);
        editor.putInt("air_data0",0);
        editor.putInt("air_data1",0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolBar);


        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQ_CODE);
        }
        init();
        myPreferences = getSharedPreferences("myTable", MODE_PRIVATE);
        editor = myPreferences.edit();
        UDPSocket.ip = myPreferences.getString("ip", "");
        wifisocket.userid = myPreferences.getString("userid", "").getBytes();
        wifisocket.password = myPreferences.getString("userpsw", "").getBytes();
        initairgrade = 0;




        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        initData();
        thread_main.start();
    }

    protected void onRestart() {
        super.onRestart();
        initairgrade = 0;
        Message message = new Message();
        message.what = UPDATE_DATA;
        handler.sendMessage(message);
    }

    protected void onResume(){
        super.onResume();
        initairgrade = 0;
        Message message = new Message();
        message.what = UPDATE_DATA;
        handler.sendMessage(message);
    }

    private void init(){
        //温度，湿度动态赋值
        textview_tem = findViewById(R.id.textView_tem);//手持器温度
        textview_humidity = findViewById(R.id.textView_humidity);//手持器湿度
        text_out_tem = findViewById(R.id.text_out_tem);//户外温度
        text_out_hum = findViewById(R.id.text_out_hum);//户外湿度

        //co2,tvoc,pm,ch2o动态赋值
        textview_ch2o_data = findViewById(R.id.textView_ch2o_data);//手持器甲醛
        textview_co2_data = findViewById(R.id.textView_co2_data);//手持器CO2
        textview_pm_data = findViewById(R.id.textView_pm_data);//手持器pm2.5
        textview_tvoc_data = findViewById(R.id.textView_tvoc_data);//手持器TVOC

        //风机状态、档位动态赋值
        textview_wind_data = findViewById(R.id.textView_wind_data);//风机档位
        switch_wind_mode = findViewById(R.id.switch_wind_mode);//风机模式
        imagevwind_0 = findViewById(R.id.imagewind_0);//0档图片
        imagevwind_1 = findViewById(R.id.imagewind_1);//1档图片
        imagevwind_2 = findViewById(R.id.imagewind_2);//2档图片
        imagevwind_3 = findViewById(R.id.imagewind_3);//3档图片

        //进度条设置
        view_up = findViewById(R.id.view3);
        percentView = findViewById(R.id.progressbar);
        tvProgress = findViewById(R.id.tv_progress);

        //登录图标
        image_nav_header = findViewById(R.id.image_nav_header);

        //连接状态标志
        imageview_windcon = findViewById(R.id.imageview_windcon);
        imageview_servercon = findViewById(R.id.imageview_servercon);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String intToIp(int i) {

        return  (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 24 ) & 0xFF)
                ;
    }

    private String antiintToIp(int i) {

        return  ((i >> 16 ) & 0xFF) + "." +
                ((i >> 24 ) & 0xFF) + "." +
                (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF)
                ;
    }

    public boolean downloadFile(String ip, int port, String username,
                                String password, String localPath, String serverPath) {

        boolean success = false;
        FTPClient ftpClient = new FTPClient();

        try {
            //设置编码格式，防止中文乱码
            ftpClient.setControlEncoding("UTF-8");
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            //设置缓存
            //ftpClient.setBufferSize(1024);
            //Toast.makeText(MainActivity.this, "开始连接", Toast.LENGTH_SHORT).show();
            ftpClient.connect(ip, port);
            //Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            ftpClient.login(username, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                System.out.println("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
                ftpClient.disconnect(); //未连接到FTP，用户名或密码错误
                return false;
            }
            // 判断服务器文件是否存在  
            FTPFile[] files = ftpClient.listFiles(serverPath);
            if (files.length == 0) {
                System.out.println("服务器文件不存在");
                return false;
            }else{
                System.out.println("创建本地文件目录");
            }
            //创建本地文件目录
            //localPath = Environment.getExternalStorageDirectory() + File.separator + files[0].getName();
            localPath = localPath + files[0].getName();
            long serverSize = files[0].getSize(); // 获取远程文件的长度  

            File localFile = new File(localPath);
            localFile.setWritable(true);
            long localSize = 0;
            //判断本地文件是否存在
            if (localFile.exists()) {
                localFile.delete();
            }/*else{
                //localFile.createNewFile();
                localFile.mkdirs();
            }*/
            // 进度  
            long step = serverSize / 100;
            long process = 0;
            long currentSize = 0;

            // 开始准备下载文件  
            //ftpClient.enterLocalActiveMode();
            //ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            FileOutputStream out = new FileOutputStream(localFile,true);
            ftpClient.setRestartOffset(localSize); //设置从哪里开始下，就是断点下载 

            InputStream input = ftpClient.retrieveFileStream(serverPath);



            byte[] b = new byte[1024];
            int length = 0;
            while ((length = input.read(b)) >0) {
                out.write(b, 0, length);
                currentSize = currentSize + length;
                /*if (currentSize / step != process) {
                    process = currentSize / step;
                    System.out.println( "下载进度：" + process + "%");
                }*/
            }

            out.flush();
            out.close();
            input.close();
            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉  
            if (ftpClient.completePendingCommand()) {
                System.out.println("文件下载成功");
                success = true;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_righttop, menu);
        return true;
    }

    //右上角点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final EditText editText = new EditText(MainActivity.this);
            builder.setTitle("设置ip");
            builder.setMessage("当前IP：" + myPreferences.getString("ip", ""));
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
                    if(!"".equals(editText.getText())) {
                        UDPSocket.ip = editText.getText().toString();
                        editor.putString("ip", editText.getText().toString());
                        editor.commit();
                        Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //取消按钮
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "取消修改", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            return false;
        } else if(id == R.id.action_settings_installationid) {//设置设备ID
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final EditText editText = new EditText(MainActivity.this);
            builder.setTitle("设置设备ID");
            builder.setMessage("当前ID：" + myPreferences.getString("installationid", ""));
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
                    if(!"".equals(editText.getText())) {
                        if(editText.getText().toString().length() != 10){
                            Toast.makeText(MainActivity.this, "设备ID须为10位", Toast.LENGTH_SHORT).show();
                        } else{
                            wifisocket.installationid = editText.getText().toString().getBytes();
                            editor.putString("installationid", editText.getText().toString());
                            editor.commit();
                            Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //取消按钮
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "取消修改", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            return false;
        } else if(id == R.id.action_settings_room){//修改房间
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("将当前数据保存到");
            final String[] aroom = {"房间一","房间二","房间三","房间四"};
            //选择挡位
            builder.setSingleChoiceItems(aroom, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "保存到：" + aroom[i], Toast.LENGTH_SHORT).show();
                    if(i == 0){
                        isroom = 0;
                    }else if(i == 1){
                        isroom = 1;
                    }else if(i == 2){
                        isroom = 2;
                    }else if(i == 3){
                        isroom = 3;
                    }
                    ischoice = 1;
                }
            });
            //实例化AlertDialog
            builder.setPositiveButton("确定",null);
            builder.setNegativeButton("取消",null);
            final AlertDialog dialog = builder.create();
            dialog.show();
            //确定按钮
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ischoice == 1) {
                        Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        ischoice = 2;
                        dialog.dismiss();
                    }else{
                        Toast.makeText(MainActivity.this, "未选择房间", Toast.LENGTH_SHORT).show();
                        ischoice = 0;
                    }
                }
            });
            //取消按钮
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "取消修改", Toast.LENGTH_SHORT).show();
                    ischoice = 0;
                    dialog.dismiss();
                }
            });

            return false;
        } else if(id == R.id.action_settings_mode){//修改房间
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("切换模式");
            final String[] aroom = {"本地模式","云模式"};

            //选择挡位
            builder.setSingleChoiceItems(aroom, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "切换为：" + aroom[i], Toast.LENGTH_SHORT).show();
                    if(i == 0){
                        conmode = 0;//本地
                    }else if(i == 1){
                        conmode = 1;
                    }
                    ischoice = 1;
                }
            });
            //实例化AlertDialog
            builder.setPositiveButton("确定",null);
            builder.setNegativeButton("取消",null);
            final AlertDialog dialog = builder.create();
            dialog.show();
            //确定按钮
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ischoice == 1) {
                        Toast.makeText(MainActivity.this, "切换成功", Toast.LENGTH_SHORT).show();
                        ischoice = 2;
                        dialog.dismiss();
                    }else{
                        Toast.makeText(MainActivity.this, "未选择模式", Toast.LENGTH_SHORT).show();
                        ischoice = 0;
                    }
                }
            });
            //取消按钮
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "取消切换", Toast.LENGTH_SHORT).show();
                    ischoice = 0;
                    dialog.dismiss();
                }
            });

            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    //左侧菜单
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_room) {
            page=ROOM_PAGE;
            IntentUtils.SetIntent(MainActivity.this, RoomActivity.class);
        } else if (id == R.id.nav_data_setting) {
            page=DATA_PAGE;
            IntentUtils.SetIntent(MainActivity.this, DataActivity.class);
        } else if (id == R.id.nav_con_setting) {
            page=CONNECT_PAGE;
            IntentUtils.SetIntent(MainActivity.this, ConnectActivity.class);
        }else if(id == R.id.nav_system_setting){
            page=SYSTEM_PAGE;
            IntentUtils.SetIntent(MainActivity.this, SystemActivity.class);
        }else if(id == R.id.nav_user_setting){
            UserActivity.isuserlogin = myPreferences.getInt("isuserlogin", 0);
            if(UserActivity.isuserlogin == 0){
                page=LOGIN_PAGE;
                IntentUtils.SetIntent(MainActivity.this, UserActivity.class);
            } else if(UserActivity.isuserlogin == 1){
                page=USER_PAGE;
                //IntentUtils.SetIntent(MainActivity.this, LoginActivity.class);
            }

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //测试网络是否连通
    public static int ping() {
        try {
            String host = "112.124.202.8";
            Process p = Runtime.getRuntime().exec("ping -c 3"+host);
            int ret = p.waitFor();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static boolean isNetWorkAvailable(Context context){
        boolean isAvailable = false ;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isAvailable()){
            isAvailable = true;
        }
        return isAvailable;
    }

    class Control extends Thread {
        private thread_main t;
        public Control(thread_main t) {
            this.t = t;
        }
        public void run() {
            while(true) {
                if(control_thread == 0) {  //r=0运行
                    t.setSleep(false);
                } else if(control_thread == 1) {  //r=1暂停
                    t.setSleep(true);
                } else if(control_thread == 2) {  //r=2 停止
                    t.setStop(true);
                }
            }
        }
    }

    class thread_main extends Thread{
        ConnectivityManager cm;
        NetworkInfo networkInfo;
        private boolean isStop = false;
        private boolean isSleep = false;
        @Override
        public void run() {
            Looper.prepare();
            while (!isStop) {
                try {
                    if (isSleep){
                        Thread.sleep(1);
                    }else {
                        try {
                            Thread.sleep(500);
                            //初始化
                            if(page==INIT_PAGE){
                                page=MAIN_PAGE;
                            }
                            //环境参数
                            if (page == MAIN_PAGE) {
                                connectflag=true;
                                if(levelflag){

                                    if(isclickedWrite==4)
                                        UIudpsocket.sendinformatin(auto);
                                    else
                                        UIudpsocket.sendinformatin(set0+isclickedWrite);
                                    Thread.sleep(100);
                                    UIudpsocket.getinfomation();
                                    isclicked = isclickedWrite;
                                    levelflag = false;

                                }
                                else {
                                    if (conmode == 0) {
                                        UIudpsocket.sendinformatin(Fan_data);
                                        System.out.println("获取温度数值");
                                        Thread.sleep(100);
                                        UIudpsocket.getinfomation();

                                        System.out.println("接收温度数值");
                                        ch2o_data = UIudpsocket.getshort(260);//手持器ch2o数值
                                        co2_data = UIudpsocket.getshort(262);//手持器CO2数值
                                        tvoc_data = UIudpsocket.getshort(264);//手持器tvoc数值
                                        pm_data = UIudpsocket.getshort(270);//手持器pm2.5数值
                                        tem_data = UIudpsocket.getshort(266);//手持器温度数值
                                        humidity_data = UIudpsocket.getshort(268);//手持器湿度数值
                                        out_tem_data = UIudpsocket.getshort(256);//户外温度数值
                                        out_humidity_data = UIudpsocket.getshort(258);//户外湿度数值
                                    } else {
                                        wifisocket.sendinformatin(wifisocket_airdata);
                                        System.out.println("发送成功");
                                        Thread.sleep(500);
                                        wifisocket.getinfomation();

                                        System.out.println("接收成功");
                                        ch2o_data = wifisocket.formaldehyde;//手持器ch2o数值
                                        co2_data = wifisocket.co2;//手持器CO2数值
                                        tvoc_data = wifisocket.tvoc;//手持器tvoc数值
                                        pm_data = wifisocket.pm25;//手持器pm2.5数值
                                        tem_data = wifisocket.temp;//手持器温度数值
                                        humidity_data = wifisocket.hum;//手持器湿度数值
                                        out_tem_data = wifisocket.outtemp;//户外温度数值
                                        out_humidity_data = wifisocket.outhum;//户外湿度数值
                                    }

                                    if (humidity_data > 0 && humidity_data < 1000)
                                        editor.putInt("humidity_data", humidity_data);
                                    if (tem_data > 0 && tem_data < 1000)
                                        editor.putInt("tem_data", tem_data);
                                    if (out_tem_data > 0 && out_tem_data < 1000)
                                        editor.putInt("out_tem_data", out_tem_data);
                                    if (out_humidity_data > 0 && out_humidity_data < 1000)
                                        editor.putInt("out_humidity_data", out_humidity_data);
                                    if (ch2o_data > 0 && ch2o_data < 1000)
                                        editor.putInt("ch2o_data", ch2o_data);
                                    if (co2_data > 0 && co2_data < 10000)
                                        editor.putInt("co2_data", co2_data);
                                    if (pm_data > 0 && pm_data < 1000)
                                        editor.putInt("pm_data", pm_data);
                                    if (tvoc_data > 0 && tvoc_data < 1000)
                                        editor.putInt("tvoc_data", tvoc_data);
                                    editor.commit();
                                    Message message = new Message();
                                    message.what = UPDATE_DATA;
                                    handler.sendMessage(message);


                                    // Prioritize();
                                    Thread.sleep(500);

                                    //空气质量
                                    UIudpsocket.sendinformatin(air);
                                    System.out.println("获取空气质量");
                                    Thread.sleep(100);
                                    UIudpsocket.getinfomation();
                                    //  Thread.sleep(2000);
                                    System.out.println("接收空气质量");
                                    air_data[2] = UIudpsocket.getshort(254);//空气质量数值
                                    if (air_grade_i == 0) {
                                        editor.putInt("air_data0", air_data[2]);
                                        air_grade_i = 1;
                                    } else if (air_grade_i == 1) {
                                        editor.putInt("air_data1", air_data[2]);
                                        air_grade_i = 0;
                                    }

                                    Thread.sleep(500);


                                    //风机模式与档位
                                    if(!levelflag) {
                                        UIudpsocket.sendinformatin(control_R);
                                        System.out.println("获取风机档位与模式");
                                        Thread.sleep(100);
                                        UIudpsocket.getinfomation();
                                        System.out.println("接收风机档位与模式");
                                        wind_mode = UIudpsocket.getsingleshort(128);//风机模式
                                        wind_data = UIudpsocket.getsingleshort(129);//风机档位数值
                                        if (wind_mode == 1) {

                                            isclicked = 4+wind_data;
                                        } else
                                            isclicked = wind_data;


                                        message = new Message();
                                        message.what = UPDATE_WINDDATA;
                                        handler.sendMessage(message);

                                    }


                                    Thread.sleep(500);
                                }
                                // Prioritize();
                            }


                            //系统参数
                            if (page == SYSTEM_PAGE) {
                                UIudpsocket.sendinformatin(Edition);
                                Thread.sleep(100);
                                System.out.println("获取引擎版本");
                                UIudpsocket.getinfomation();
                                System.out.println("接收引擎版本");
                                SystemActivity.engine_data = antiintToIp(UIudpsocket.getint(4));//引擎版本
                                SystemActivity.ladder_data = antiintToIp(UIudpsocket.getint(0));//梯形图版本
                                //Thread.sleep(250);
                                //   Prioritize();
                                Message message = new Message();
                                message.what = UPDATE_SYSTEM;
                                handler.sendMessage(message);
                            }

                            //绑定设备号获取
                            if (page == USER_PAGE) {
                                wifisocket.setUserid(UserActivity.getTitleuserid());
                                wifisocket.state = 0x00;
                                wifisocket.sendinformatin(wifisocket_device);
                                Thread.sleep(100);
                                wifisocket.getinfomation();
                                for (int i = 0; i < 5; i++) {
                                    UserActivity.device[i] = wifisocket.getdevicestring(i);
                                }
                                UserActivity.i_device = (byte)wifisocket.getinstallationdateruslt;

                                // Prioritize();
                            }

                            //连接参数
                            if (page == CONNECT_PAGE) {

                                UIudpsocket.sendinformatin(LAN_ip);
                                System.out.println("获取连接参数1");
                                Thread.sleep(100);
                                UIudpsocket.getinfomation();
                                UIudpsocket.getidString(22, SystemActivity.equipmentid, 10);
                                Thread.sleep(500);
                                //Thread.sleep(250);
                                UIudpsocket.sendinformatin(localwifi_R);
                                System.out.println("获取连接参数2");
                                Thread.sleep(500);
                                UIudpsocket.getinfomation();
                                Thread.sleep(100);
                                //Thread.sleep(250);
                                UIudpsocket.sendinformatin(routerwifi_R);
                                System.out.println("获取路由器名称");
                                Thread.sleep(100);
                                UIudpsocket.getinfomation();
                                Thread.sleep(500);
                                //Thread.sleep(250);
                                ConnectActivity.local_wifi_ip1 = intToIp(UIudpsocket.getint(8));
                                ConnectActivity.local_wifi_ip2 = intToIp(UIudpsocket.getint(12));
                                ConnectActivity.air_wifi_ip = intToIp(UIudpsocket.getint(72));
                                ConnectActivity.local_wifi_channel = UIudpsocket.getshort(70);
                                UIudpsocket.getString(38, ConnectActivity.read_local_wifi_id, 16);//本地wifi名称
                                UIudpsocket.getString(54, ConnectActivity.read_local_wifi_password, 16);
                                UIudpsocket.getString(0244, ConnectActivity.read_router_wifi_id, 16);//路由器wifi名称
                                System.out.println("路由器wifi:" + Arrays.toString(ConnectActivity.read_router_wifi_id));
                                UIudpsocket.getString(188, ConnectActivity.read_router_wifi_password, 16);

                                Message message = new Message();
                                message.what = UPDATE_CONNECT;
                                handler.sendMessage(message);



                            }


                            //房间数据
                            //房间一
                            if (page == ROOM_PAGE){
                                UIudpsocket.sendinformatin(getroom1);
                                System.out.println("获取房间1");
                                Thread.sleep(100);
                                UIudpsocket.getinfomation();
                                System.out.println("接收房间1");

                                RoomActivity.room_data[1] = UIudpsocket.getshort(290);//房间一温度
                                RoomActivity.room_data[2] = UIudpsocket.getshort(292);//房间一湿度
                                RoomActivity.room_data[3] = UIudpsocket.getshort(288);//房间一TVOC
                                RoomActivity.room_data[4] = UIudpsocket.getshort(294);//房间一PM2.5
                                RoomActivity.room_data[5] = UIudpsocket.getshort(284);//房间一甲醛
                                RoomActivity.room_data[6] = UIudpsocket.getshort(286);//房间一Co2

                                //   Prioritize();


                                //房间二
                                UIudpsocket.sendinformatin(getroom2);
                                System.out.println("获取房间2");
                                Thread.sleep(100);
                                UIudpsocket.getinfomation();
                                System.out.println("接收房间2");
                                //Thread.sleep(250);
                                RoomActivity.room_data[11] = UIudpsocket.getshort(314);//房间二温度
                                RoomActivity.room_data[12] = UIudpsocket.getshort(316);//房间二湿度
                                RoomActivity.room_data[13] = UIudpsocket.getshort(312);//房间二TVOC
                                RoomActivity.room_data[14] = UIudpsocket.getshort(318);//房间二PM2.5
                                RoomActivity.room_data[15] = UIudpsocket.getshort(308);//房间二甲醛
                                RoomActivity.room_data[16] = UIudpsocket.getshort(310);//房间二Co2

                                //   Prioritize();

                                //房间三
                                UIudpsocket.sendinformatin(getroom3);
                                System.out.println("获取房间3");
                                UIudpsocket.getinfomation();
                                System.out.println("接收房间3");
                                //Thread.sleep(250);
                                RoomActivity.room_data[21] = UIudpsocket.getshort(338);//房间三温度
                                RoomActivity.room_data[22] = UIudpsocket.getshort(340);//房间三湿度
                                RoomActivity.room_data[23] = UIudpsocket.getshort(336);//房间三TVOC
                                RoomActivity.room_data[24] = UIudpsocket.getshort(342);//房间三PM2.5
                                RoomActivity.room_data[25] = UIudpsocket.getshort(332);//房间三甲醛
                                RoomActivity.room_data[26] = UIudpsocket.getshort(334);//房间三Co2

                                //  Prioritize();

                                //房间四
                                UIudpsocket.sendinformatin(getroom4);
                                System.out.println("获取房间4");
                                UIudpsocket.getinfomation();
                                System.out.println("接收房间4");
                                //Thread.sleep(250);
                                RoomActivity.room_data[31] = UIudpsocket.getshort(362);//房间四温度
                                RoomActivity.room_data[32] = UIudpsocket.getshort(364);//房间四湿度
                                RoomActivity.room_data[33] = UIudpsocket.getshort(360);//房间四TVOC
                                RoomActivity.room_data[34] = UIudpsocket.getshort(366);//房间四PM2.5
                                RoomActivity.room_data[35] = UIudpsocket.getshort(356);//房间四甲醛
                                RoomActivity.room_data[36] = UIudpsocket.getshort(358);//房间四Co2

                                //  Prioritize();
                            }


                            //阈值参数
                            if(page==DATA_PAGE) {
                                UIudpsocket.sendinformatin(controldata_R);
                                System.out.println("获取阈值");
                                Thread.sleep(100);
                                UIudpsocket.getinfomation();
                                System.out.println("接收阈值");
                                //Thread.sleep(250);
                                DataActivity.system_data[1] = UIudpsocket.getshort(130);
                                DataActivity.system_data[2] = UIudpsocket.getshort(132);
                                DataActivity.system_data[3] = UIudpsocket.getshort(134);
                                DataActivity.system_data[4] = UIudpsocket.getshort(136);
                                DataActivity.system_data[5] = UIudpsocket.getshort(154);
                                DataActivity.system_data[6] = UIudpsocket.getshort(156);
                                DataActivity.system_data[7] = UIudpsocket.getshort(158);
                                DataActivity.system_data[8] = UIudpsocket.getshort(160);

                                DataActivity.system_data[11] = UIudpsocket.getshort(138);
                                DataActivity.system_data[12] = UIudpsocket.getshort(140);
                                DataActivity.system_data[13] = UIudpsocket.getshort(142);
                                DataActivity.system_data[14] = UIudpsocket.getshort(144);
                                DataActivity.system_data[15] = UIudpsocket.getshort(154);
                                DataActivity.system_data[16] = UIudpsocket.getshort(156);
                                DataActivity.system_data[17] = UIudpsocket.getshort(158);
                                DataActivity.system_data[18] = UIudpsocket.getshort(160);

                                DataActivity.system_data[21] = UIudpsocket.getshort(146);
                                DataActivity.system_data[22] = UIudpsocket.getshort(148);
                                DataActivity.system_data[23] = UIudpsocket.getshort(150);
                                DataActivity.system_data[24] = UIudpsocket.getshort(152);
                                DataActivity.system_data[25] = UIudpsocket.getshort(154);
                                DataActivity.system_data[26] = UIudpsocket.getshort(156);
                                DataActivity.system_data[27] = UIudpsocket.getshort(158);
                                DataActivity.system_data[28] = UIudpsocket.getshort(160);

                                //Prioritize();
                            }

                            //Thread.sleep(100);


                        } catch (IOException e) {
                            System.out.println("2");
                            e.printStackTrace();
                        }
                    }
                    System.out.println("完成一次读取");

                    //  Thread.sleep(50);
                    if(control_thread==1) Thread.sleep(1000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                    System.out.println("InterruptedException");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception");
                }
            }
        }

        public void setStop(boolean isstop){
            this.isStop = isstop;
        }

        public void setSleep(boolean issleep){
            this.isSleep = issleep;
        }
    }


    public static void setUserid(String Userid){
        userid = Userid;
    }


    public void onButton1Click (View view){
        // Toast.makeText(MainActivity.this,"you have clicked Button3",Toast.LENGTH_SHORT).show();
        isclickedWrite = 1;
        isclicked = 1;
        levelflag=true;
        Message message = new Message();
        message.what = UPDATE_WINDDATA;
        handler.sendMessage(message);



    }
    public void onButton2Click (View view){
        //  Toast.makeText(MainActivity.this,"you have clicked Button3",Toast.LENGTH_SHORT).show();
        isclickedWrite = 2;
        isclicked=2;
        levelflag=true;
        Message message = new Message();
        message.what = UPDATE_WINDDATA;
        handler.sendMessage(message);


    }
    public void onButton3Click (View view){
        //Toast.makeText(MainActivity.this,"you have clicked Button3",Toast.LENGTH_SHORT).show();
        isclickedWrite = 3;
        isclicked = 3;
        levelflag=true;
        Message message = new Message();
        message.what = UPDATE_WINDDATA;
        handler.sendMessage(message);



    }

    public void onButton0Click (View view){
        //Toast.makeTextMainActivity.this,"you have clicked Button3",Toast.LENGTH_SHORT).show();
        isclickedWrite = 0;
        isclicked = 0;
        levelflag=true;
        Message message = new Message();
        message.what = UPDATE_WINDDATA;
        handler.sendMessage(message);

    }
    public void onSwtichClick (View view){

        if(isclicked == 4) {
            isclickedWrite = 0;
            isclicked = 0;
        }
        else{
            isclickedWrite = 4;
            isclicked = isclicked+4;
        }
        levelflag=true;
        Message message = new Message();
        message.what = UPDATE_WINDDATA;
        handler.sendMessage(message);


    }


}