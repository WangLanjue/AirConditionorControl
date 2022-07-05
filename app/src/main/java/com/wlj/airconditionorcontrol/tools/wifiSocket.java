package com.wlj.airconditionorcontrol.tools;

import android.net.wifi.WifiManager;

import com.wlj.airconditionorcontrol.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class wifiSocket {

    private static int sendlen;//发送的数据帧长度

    public static int control_module;//空调控制模式  0=档位手动，1=档位自动
    public static int gears = 1;//空调档位  控制档位，0-3档。

    public static int sendmessagetype=0;//发送消息的类型
    //sendmessagetype 数值为1时从服务器读取空调最新消息，为2时向服务器注册信息，为3时读取用户绑定设备为4时手机app调节档位，为5时用户登录；
    public static byte  state ;//确认操作为绑定解绑或读取 当该数据为0时 为读取，1时为解绑，2为绑定
    public static byte Registration = 4;//当app端发起注册请求时，服务器返回的注册状态
    public static byte getinstallationdateruslt;//绑定 返回值当回复“绑定”和“解除绑定”请求时成功为0，失败为1，已绑定为2；当读取时为数据的数量
    public static byte Forwarding; //转发状态 0 转发成功 1 用户不存在 2 设备不存在 3 用户无权限（没有绑定这台设备）4 异常


    private static byte  control ;//发送的空调控制信息
    public static byte [] Data = new byte[2048];
    public static byte [] userid = new byte[20];//用户id
    public static byte [] installationid = new byte[10];//设备id
    public static byte [] password = new byte[20];//用户密码
    public static byte [] username = new byte[15];//用户姓名
    public static byte [] phonenumber = new byte[15];//手机号
    public static byte [] useraddress= new byte[150];//地址

    public static byte [] getairdate = new byte[24];//读取空调最新消息时，返回的数据
    public static byte [] getinstallationdate= new byte[1024];//读取用户绑定数据时 返回的设备数量，设备上限为5，每168个字节存储一个设备的数据
    public static byte [] loginreturn = new byte[1];//服务器返回的登录情况

    private static byte [] getzero = new byte[30];//用于数组清零

    final static int wifisocket_airdata = 1;//读取风机数据
    final static int wifisocket_rigister = 2;//发送注册数据
    final static int wifisocket_device = 3;//绑定/设备信息
    final static int wifisocket_gearposition = 4;//更改风机档位
    final static int wifisocket_login = 5;//用户登录

    private static WifiManager.MulticastLock lock;

    public  short outtemp ;
    public  short outhum ;
    public  short formaldehyde ;
    public  short co2  ;
    public  short tvoc ;
    public  short temp ;
    public  short hum ;
    public  short pm25 ;




    public static void clear(int A) {
        int i;
        for( i= 0;i<30;i++)
            getzero[i]=0x00;
        System.arraycopy(getzero, 0,userid , 0, userid.length);
        if(A==1)
            System.arraycopy(getzero, 0,installationid , 0, installationid.length);
        if(A==2)
        {
            System.arraycopy(getzero, 0,password , 0, password.length);
            System.arraycopy(getzero, 0,username , 0, username.length);
            System.arraycopy(getzero, 0,phonenumber , 0, phonenumber.length);
            for( i= 0;i<useraddress.length;i++)
                useraddress[i]=0x00;
        }
        if(A==3)
        {
            System.arraycopy(getzero, 0,installationid , 0, installationid.length);
            state=0x00;
        }
        if(A==4)
            System.arraycopy(getzero, 0,installationid , 0, installationid.length);
        return;
    }

    public static void setUserid(String Userid){
        userid = Userid.getBytes();

    }

    public static void setPassword(String Password){
        password = Password.getBytes();

    }

    public void setUsername(String Username){
        username = Username.getBytes();
    }

    public void setUseraddress(String Useraddress){
        useraddress = Useraddress.getBytes();
    }

    public static void setInstallationid(String Installationid){
        installationid = Installationid.getBytes();

    }

    public void setPhonenumber(String Phonenumber){
        phonenumber = Phonenumber.getBytes();
    }

    public void get00Byte(int A, byte[] string, int len){
        System.arraycopy(string, 0, Data, A, len);
        byte[] a = new byte[]{0x00};
        System.arraycopy(a, 0, Data, A + len, 1);
    }

    public static byte [] getMessage(int A){
        sendmessagetype=A;
        byte [] pComOutBuf = new byte[1024];

        Arrays.fill(pComOutBuf, (byte)0x00);
        if ( sendmessagetype == wifisocket_airdata )//从服务器读取最新空气数据
        {
            pComOutBuf[0] = 0x55 ;
            pComOutBuf[1] = (byte)0xAA ;
            pComOutBuf[2] = 0x02;
            System.arraycopy(installationid, 0, pComOutBuf, 3, installationid.length);
            System.arraycopy(userid, 0, pComOutBuf, 13,3);

            sendlen =34;
        }
        if ( sendmessagetype == wifisocket_rigister )//向服务器注册信息
        {
            pComOutBuf[0] = 0x55 ;
            pComOutBuf[1] = (byte)0xAA ;
            pComOutBuf[2] = 0x01;
            System.arraycopy(userid, 0, pComOutBuf, 3, userid.length);
            System.arraycopy(password, 0,pComOutBuf , 23, password.length);
            System.arraycopy(username, 0,pComOutBuf , 43, username.length);
            System.arraycopy(phonenumber, 0,pComOutBuf , 58, phonenumber.length);
            System.arraycopy(useraddress, 0,pComOutBuf , 73, useraddress.length);
            sendlen =223;
        }
        if ( sendmessagetype == wifisocket_device )//读取/解绑/绑定设备信息
        {
            pComOutBuf[0] = 0x55 ;
            pComOutBuf[1] = (byte)0xAA ;
            pComOutBuf[2] = 0x04;
            pComOutBuf[3] = state;
            System.arraycopy(userid, 0, pComOutBuf, 4, userid.length);
            System.arraycopy(installationid, 0, pComOutBuf, 24, installationid.length);
            sendlen =34;
        }
        if ( sendmessagetype == wifisocket_gearposition )//更改档位
        {
            if(control_module==1)
                control=0x40;
            else {
                if(gears == 0)
                    control = 0x00;
                if(gears==1)
                    control=0x10;
                if(gears==2)
                    control=0x20;
                if(gears==3)
                    control=0x30;
            }
            pComOutBuf[0] = 0x55 ;
            pComOutBuf[1] = (byte)0xAA ;
            pComOutBuf[2] = 0x03;
            System.arraycopy(installationid, 0, pComOutBuf, 3, installationid.length);
            pComOutBuf[13]=0x05;
            pComOutBuf[14]=0x28;
            pComOutBuf[15]=0x00;
            pComOutBuf[16]=0x02;
            pComOutBuf[17]=0x02;
            pComOutBuf[18]=(byte)control_module;
            pComOutBuf[19]=(byte)gears;
            String s = new String(userid);
            System.out.println(s);
            System.arraycopy(userid, 0, pComOutBuf, 20, userid.length);
            sendlen =40;
        }
        if ( sendmessagetype == wifisocket_login )//用户登录
        {
            pComOutBuf[0] = 0x55 ;
            pComOutBuf[1] = (byte)0xAA ;
            pComOutBuf[2] = 0x05;
            System.arraycopy(userid, 0, pComOutBuf, 3, userid.length);
            System.arraycopy(password, 0, pComOutBuf, 23, password.length);
            sendlen =43;
        }
        return pComOutBuf;
    }

    public short getshort(int subscript){
        return (short) ((0xff &  getairdate[subscript]) | (0xff00 & ( getairdate[subscript+1] << 8)));
    }

    public static String getdevicestring(int i){
        byte[] device = new byte[10];
        System.arraycopy(getinstallationdate, i*168, device, 0, 10);
        return Arrays.toString(device);
    }

    public short getsingleshort(int subscript){
        return (short) (0xff &  getairdate[subscript]);
    }

    public void CassModbusMasterHandle( final byte [] message)
    {
        if((message[0]==0x55)&&(message[1]==(byte)0xAA))
        {
            if (message[2]==0x02)
            {


                outtemp = (short)((( message[5]& 0x00FF) << 8) | (0x00FF &  message[6]));
                outhum = (short)((( message[9]& 0x00FF) << 8) | (0x00FF &  message[10]));
                formaldehyde = (short)((( message[13]& 0x00FF) << 8) | (0x00FF &  message[14]));
                co2 = (short)((( message[17]& 0x00FF) << 8) | (0x00FF &  message[18]));
                tvoc = (short)((( message[21]& 0x00FF) << 8) | (0x00FF &  message[22]));
                temp = (short)((( message[25]& 0x00FF) << 8) | (0x00FF &  message[26]));
                hum = (short)((( message[29]& 0x00FF) << 8) | (0x00FF &  message[30]));
                pm25 = (short)((( message[33]& 0x00FF) << 8) | (0x00FF &  message[34]));

            }
            if(message[2]==0x01)
            {
                Registration=message[3];
            }
            if(message[2]==0x04)//读取、解绑、绑定设备
            {

                getinstallationdateruslt = message[4];
                if((int)message[3]==0)
                {
                    System.arraycopy(message, 6,  getinstallationdate, 0,  840);
                }

            }
            if(message[2]==0x03)
            {
                Forwarding=message[3];
            }
            if(message[2]==0x05)//登录
            {
                System.arraycopy(message, 3,loginreturn, 0, 1);
            }
        }
        else
            return;
    }
    public void  sendinformatin(int A) {
        try {
            UDPSocket.socket.getLocalPort();
            InetAddress serverAddress = InetAddress.getByName("112.124.202.8");
            byte [] data = getMessage(A);
            System.out.println(Arrays.toString(data));
            data=getMessage(A);
            DatagramPacket send_packet = new DatagramPacket(data, sendlen, serverAddress,3022);//T:3020
            UDPSocket.socket.send(send_packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getinfomation(){
        try {
            byte data[] = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            UDPSocket.socket.setSoTimeout(100);
            UDPSocket.socket.receive(packet);
            System.out.println("注册返回结果" + Arrays.toString(data));
            MainActivity.testservercon = 0;
            CassModbusMasterHandle(data);

        } catch (SocketException e) {
            e.printStackTrace();
        }catch (IOException e) {
            MainActivity.testservercon = 1;
            e.printStackTrace();
        }

    }


    public void socketclose(){
        UDPSocket.socket.close();
        UDPSocket.socket.disconnect();
    }


}
