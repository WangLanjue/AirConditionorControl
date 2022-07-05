package com.wlj.airconditionorcontrol.tools;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Button;

import com.wlj.airconditionorcontrol.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class UDPSocket {


    public static int messagetype=0;


    private Context mContext;
    private DatagramSocket client;
    private DatagramPacket receivePacket;

    public static DatagramSocket socket;//PORT:2025;

    static {
        try {
            socket = new  DatagramSocket (3020);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public static String ip = "192.168.1.52";
    private DatagramPacket packet;
    private DatagramPacket send_packet;
    private volatile boolean stopReceiver;
    private static WifiManager.MulticastLock lock;
    public UDPSocket(WifiManager manager) throws SocketException {
        this.lock= manager.createMulticastLock("UDPwifi");
    }

    final static int Starting_add  = 1192;
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

    private ExecutorService mThreadPool;
    private Thread clientThread;

    private byte sendlen;

    private Button text2;
    public static byte [] Data = new byte[2048];
    private byte [] data = new byte[1024];
    private int errorNum=0;

    public UDPSocket() throws SocketException {
    }


    /**
     * 发送CRC
     *
     * @param message
     */
    public static short GetCRC(byte[] message, int lenth) {
        byte[] crc16_h = {
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40
        };

        byte[] crc16_l = {
                (byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04,
                (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8,
                (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC,
                (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12, (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10,
                (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4,
                (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38,
                (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C,
                (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7, (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0,
                (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4,
                (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68,
                (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C,
                (byte) 0xB4, (byte) 0x74, (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0,
                (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54,
                (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98,
                (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D, (byte) 0x4C, (byte) 0x8C,
                (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40
        };

        int crc = 0x0000ffff;
        int ucCRCHi = 0x00ff;
        int ucCRCLo = 0x00ff;
        int iIndex;
        for (int i = 0; i < lenth; ++i) {
            iIndex = (ucCRCLo ^ message[i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_h[iIndex];
            ucCRCHi = crc16_l[iIndex];
        }

        crc = ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
        //高低位互换，输出符合相关工具对Modbus CRC16的运算
        crc = ( (crc & 0xFF00) >> 8) | ( (crc & 0x00FF ) << 8);
        System.out.println(crc);
        return  (short)crc;
    }


    private int COMFrmVrify(final byte [] message,int DataCount)
    {
        int verify,temp;

        if(DataCount < 3)
            return 0;

        //校验码 CRC校验
        verify = GetCRC(message,DataCount - 2 ) ;

        temp =  message[DataCount - 1] | (message[DataCount - 2] << 8);

        if ( verify == temp )
            return 1;

        return 0 ;
    }

    public void itb(int A,int subscript )
    {
        Data[subscript+3]=(byte) (A>>24);
        Data[subscript+2]=(byte) (A>>16);
        Data[subscript+1]=(byte) (A>>8);
        Data[subscript+0]=(byte) A;

        return ;
    }

    public void ctb(char c,int subscript )
    {
        Data[subscript] = (byte) ((c & 0xFF00) >> 8);
        Data[subscript+1] = (byte) (c & 0xFF);
        return ;
    }

    public int getint(int A ){
        int int1=Data[A]&0xff;
        int int2=(Data[A+1]&0xff)<<8;
        int int3=(Data[A+2]&0xff)<<16;
        int int4=(Data[A+3]&0xff)<<24;
        return int1|int2|int3|int4;
    }
    public int getlongint(int A ){
        int int1=Data[A]&0xff;
        int int2=(Data[A+1]&0xff)<<8;
        int int3=(Data[A+2]&0xff)<<16;
        int int4=(Data[A+3]&0xff)<<24;
        int int5=(Data[A+4]&0xff)<<32;
        int int6=(Data[A+5]&0xff)<<40;
        int int7=(Data[A+6]&0xff)<<48;
        int int8=(Data[A+7]&0xff)<<56;
        int int9=(Data[A+8]&0xff)<<64;
        int int10=(Data[A+9]&0xff)<<72;
        return int1|int2|int3|int4|int5|int6|int7|int8|int9|int10;
    }

    public char  getchar(int A ){
        byte [] gchar= new byte[2];
        for(int i=0;i<2;i++)
        {
            gchar[i]=Data[A+i];
        }
        char c = (char) (((gchar[0] & 0xFF) << 8) | (gchar[1] & 0xFF));
        return c;
    }

    public void stb(short c,int subscript) {
        Data[subscript] = (byte) (c& 0xff);
        Data[subscript+1] = (byte) ((c & 0xff00) >> 8);
        return ;
    }

    public int getunshort(int subscript) {
        short C=getshort(subscript);
        int A=C& 0x0FFFF;
        return A;
    }

    public int getunsignedint(int A ){
        int B;
        B=getint(A);
        return B &0XFFFFFF;
    }

    public void getString(int A, byte[] string, int len){
        int i;
        for(i = 0; i < len; i++){
            if(Data[A+i] == 0x00){
                break;
            }
        }
        Arrays.fill(string, (byte)0);
        System.arraycopy(Data, A, string, 0, i);
    }

    public void getidString(int A, byte[] string, int len){
        Arrays.fill(string, (byte)0);
        System.arraycopy(Data, A, string, 0, len);
    }

    public void getByte(int A, byte[] string, int len){
        System.arraycopy(string, 0, Data, A, len);
    }

    public void get00Byte(int A, byte[] string, int len){
        System.arraycopy(string, 0, Data, A, len);
        byte[] a = new byte[]{0x00};
        System.arraycopy(a, 0, Data, A + len, 1);
    }

    public short getshort(int subscript){
        return (short) ((0xff &  Data[subscript]) | (0xff00 & ( Data[subscript+1] << 8)));
    }

    public short getsingleshort(int subscript){
        return (short) (0xff &  Data[subscript]);
    }

    public void CassModbusMasterHandle( final byte [] message)
    {
        short usTemp ;
        short tmp ,sendlen;
        byte BitTemp ,i;
        byte crcFlag;
        byte[] pComInBuf;
        pComInBuf=message;
        System.out.println(message);
        if(((COMFrmVrify(pComInBuf, pComInBuf[2]+3)) == 1))
        {
            pComInBuf[0] = 0 ;
            return ;
        }


        if(pComInBuf[0] !=0)//Mark 2009-7-22
        {
            tmp = (short) (pComInBuf[5]  + (pComInBuf[4] << 8))  ;  //内存地址
            switch( pComInBuf[1] )
            {
                case 0x03: // 上位机发的读数据命令
                    if(messagetype==Edition)
                        System.arraycopy(pComInBuf,3 ,Data ,0, 8);
                    else if(messagetype==LAN_ip)
                        System.arraycopy(pComInBuf,3 ,Data ,8, 24);
                    else if(messagetype==air)
                        System.arraycopy(pComInBuf,3 ,Data ,0376, 2);
                    else if(messagetype==Fan_data)
                        System.arraycopy(pComInBuf,3 ,Data ,0400, 22);
                    else if(messagetype==localwifi_R)
                        System.arraycopy(pComInBuf,3 ,Data ,046, 42);
                    else if(messagetype==lanwifi_R)
                        System.arraycopy(pComInBuf,3 ,Data ,0120, 40);
                    else if(messagetype==control_R)
                        System.arraycopy(pComInBuf,3 ,Data ,128, 2);
                    else if(messagetype==controldata_R)
                        System.arraycopy(pComInBuf,3 ,Data ,0202, 32);
                    else if(messagetype==getroom1)
                        System.arraycopy(pComInBuf,3 ,Data ,0430, 16);
                    else if(messagetype==getroom2)
                        System.arraycopy(pComInBuf,3 ,Data ,0460, 16);
                    else if(messagetype==getroom3)
                        System.arraycopy(pComInBuf,3 ,Data ,0510, 16);
                    else if(messagetype==getroom4)
                        System.arraycopy(pComInBuf,3 ,Data ,0540, 16);
                    else if(messagetype==routerwifi_R)
                        System.arraycopy(pComInBuf,3 ,Data ,0244, 48);
                    else if(messagetype==windconnection)
                        System.arraycopy(pComInBuf,3 ,Data ,776, 2);
                    break;
                case 0x06:	//写命令

                    break;
                case 0x05:

                    break;
                case  0x10:

                    break ;

                default:
                    break;
            }
        }
    }

    public byte [] getMessage(int A ){
        short usTemp ;
        byte [] pComOutBuf = new byte[256];
        messagetype = A;

        if ( messagetype == Edition )
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add >>8) ;
            pComOutBuf[3] = (byte)Starting_add ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 4 ;
            sendlen = 8;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == LAN_ip )
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add +010 )>> 8 ;
            pComOutBuf[3] = (byte)(Starting_add +010 );
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 12 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == air)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0376)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0376) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == Fan_data)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0400)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0400) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 11 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype ==localwifi_R)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+046)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+046) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 21 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == localwifi_W)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+046 )>> 8 ;
            pComOutBuf[3] = (byte)(Starting_add+046) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 21 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data, 38, pComOutBuf ,7, 42);
            sendlen=51;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype ==lanwifi_R)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0130)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0130) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 16 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == lanwifi_W)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0120 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0120 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 20 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,80 , pComOutBuf ,7, 40);
            sendlen =49 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype ==control_R)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0200)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0200) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen- 1] = (byte)usTemp ;
            pComOutBuf[sendlen- 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype ==controldata_R)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0202)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0202) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 16 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == controldata1_W)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0202 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0202 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 4 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,130 , pComOutBuf ,7, 8);
            sendlen =17 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == controldata2_W)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0212 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0212 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 4 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,138 , pComOutBuf ,7, 8);
            sendlen =17 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == controldata3_W)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0222 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0222 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 4 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,146 , pComOutBuf ,7, 8);
            sendlen =17 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == set0)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0200 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0200 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            pComOutBuf[6] = (byte)(pComOutBuf[5] * 2);
            pComOutBuf[7] = 0x00 ;
            pComOutBuf[8] = 0x00 ;
            sendlen =11 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == set1)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0200 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0200 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            pComOutBuf[6] = (byte)(pComOutBuf[5] * 2);
            pComOutBuf[7] = 0x00 ;
            pComOutBuf[8] = 0x01 ;
            sendlen =11 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == set2)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0200 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0200 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            pComOutBuf[6] = (byte)(pComOutBuf[5] * 2);
            pComOutBuf[7] = 0x00 ;
            pComOutBuf[8] = 0x02 ;
            sendlen =11 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == set3)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0200 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0200 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            pComOutBuf[6] = (byte)(pComOutBuf[5] * 2);
            pComOutBuf[7] = 0x00 ;
            pComOutBuf[8] = 0x03 ;
            sendlen =11 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == byhand)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0200 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0200 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            pComOutBuf[6] = 1 ;
            pComOutBuf[7] = 0x00 ;
            sendlen =10 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype == auto)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0200 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0200 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            pComOutBuf[6] = (byte)(pComOutBuf[5] * 2);
            pComOutBuf[7] = 0x01 ;
            pComOutBuf[8] = 0x01 ;
            sendlen =11 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;


        }
        else if( messagetype ==room1)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0430 )>> 8 ;  	//
            pComOutBuf[3] = (byte)(Starting_add+0430);
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,0400 , pComOutBuf ,7, 16);
            sendlen =25 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;
        }
        else if( messagetype ==room2)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0460 )>> 8 ;
            pComOutBuf[3] = (byte)(Starting_add+0460);
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,0400 , pComOutBuf ,7, 16);
            sendlen =25 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;
        }
        else if( messagetype ==room3)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0510 )>> 8 ;
            pComOutBuf[3] = (byte)(Starting_add+0510);
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,0400 , pComOutBuf ,7, 16);
            sendlen =25 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;
        }
        else if( messagetype ==room4)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0540 )>> 8 ;
            pComOutBuf[3] = (byte)(Starting_add+0540);
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data,0400 , pComOutBuf ,7, 16);
            sendlen =25 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;
        }
        else if( messagetype == getroom1)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0430)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0430) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == getroom2)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0460)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0460) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == getroom3)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0510)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0510) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == getroom4)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0540)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0540) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 8 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == storage)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0170 )>> 8 ;
            pComOutBuf[3] = (byte)Starting_add+0170 ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            pComOutBuf[6] = (byte)(pComOutBuf[5] << 1);
            pComOutBuf[7] = (byte)0x55 ;
            pComOutBuf[8] = (byte)0xAA ;
            sendlen =11 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;
        }
        else if( messagetype ==routerwifi_R)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+0244)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+0244) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 24 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype ==routerwifi_W)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x10 ;
            pComOutBuf[2] = (Starting_add+0244 )>> 8 ;
            pComOutBuf[3] = (byte)(Starting_add+0244) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 24 ;
            pComOutBuf[6] =  (byte)(pComOutBuf[5] << 1) ;
            System.arraycopy(Data, 0244, pComOutBuf ,7, 48);
            sendlen=57;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;

        }
        else if( messagetype == windconnection)
        {
            pComOutBuf[0] = 01 ;
            pComOutBuf[1] = 0x03 ;
            pComOutBuf[2] = (Starting_add+776)>> 8 ;
            pComOutBuf[3] =(byte)(Starting_add+776) ;
            pComOutBuf[4] = 0x00 ;
            pComOutBuf[5] = 1 ;
            sendlen = 8 ;
            usTemp = GetCRC(pComOutBuf,sendlen - 2 ) ;
            pComOutBuf[sendlen - 1] = (byte)usTemp ;
            pComOutBuf[sendlen - 2] =  (byte)(usTemp >> 8) ;
        }
        else
        {
            return pComOutBuf;
        }
        return pComOutBuf;
    }

    public void  sendinformatin(int A) throws SocketException, UnknownHostException {
        try {
            socket.getLocalPort();
            InetAddress serverAddress = InetAddress.getByName(ip);//192.168.4.1
            byte [] data = getMessage(A);
            DatagramPacket send_packet = new DatagramPacket(data,sendlen,serverAddress,2025);//PORT:3020
            socket.send(send_packet);
            String result = new String(send_packet.getData(), send_packet.getOffset(),
                    send_packet.getLength());
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("已发送");
        //socket.close();
    }

    public void  upload_sendinformatin(byte[] sendfram)  {
        try {
            socket.getLocalPort();
            InetAddress serverAddress = InetAddress.getByName(ip);
            DatagramPacket send_packet = new DatagramPacket(sendfram, sendfram.length, serverAddress,2024);//PORT:3020
            socket.send(send_packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("已发送");
        //socket.close();
    }

    public void getinfomation(){
        try {
            short usTemp ;
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            System.out.println("abcd");
            socket.setSoTimeout(50);
            socket.receive(packet);
            usTemp = GetCRC(data, data[2] + 3);
            if(((byte)usTemp) == data[(int)data[2] + 5 - 1] && (byte)(usTemp >> 8) == data[(int)data[2] + 5 - 2]){
                MainActivity.isreceivewrong = 0;
            }else{
                MainActivity.isreceivewrong = 1;
            }
            System.out.println("dcba");

            String result = new String(packet.getData(), packet.getOffset(),
                    packet.getLength());
            System.out.println(result);
            CassModbusMasterHandle(data);
        } catch (Exception e) {
            errorNum++;
            if(errorNum>5) {
                MainActivity.iswindcon = 0;
                errorNum=0;
            }
            System.out.println("接收错误2");
            e.printStackTrace();
        }
        MainActivity.iswindcon = 1;
    }

    public void upload_getinfomation(byte[] receivefram){
        try {
            DatagramPacket packet = new DatagramPacket(receivefram, receivefram.length);
            System.out.println("abcd");
            socket.setSoTimeout(100);
            socket.receive(packet);
            System.out.println("dcba");
            String result = new String(packet.getData(), packet.getOffset(),
                    packet.getLength());
            System.out.println(result);
            CassModbusMasterHandle(data);
        } catch (SocketException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void socketclose(){
        socket.close();
        socket.disconnect();
    }

}
