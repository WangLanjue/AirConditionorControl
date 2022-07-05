package com.wlj.airconditionorcontrol.tools;

import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_AuthenticationRequest;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_AuthenticationRequest_FAIL;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_DownloadData;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_FAIL;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_HandShake;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_HandShake_FAIL;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_RESET;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_RebootDevice;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_RebootDevice_FAIL;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_SUCCESS;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_SartDownload;
import static com.wlj.airconditionorcontrol.SystemActivity.UPDATE_PROCESS_SartDownload_FAIL;
import static com.wlj.airconditionorcontrol.SystemActivity.progressDialog;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.wlj.airconditionorcontrol.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class upLoad {
        private static boolean isAddCode = true;//是否加上密匙
        private byte []password = new byte[16];//AES加密密码
        private static int REPEAT_TIMES = 100;
        private int SendBufSize = 512;
        private int flashSize = 25 * 1024;
        private int downAddress;//0x8008013
        private AES AES = new AES();
        private md5 MD5 = new md5();

        public static int downloadstep = 0;
        public static int process;
        public static int Maximum;


        //private UDPSocket upload_udpSocket;

        //private uploadUser uploadUser;


        String filePath = "/storage/emulated/0/Download/DeviceStatusInfo.bin";
        String filePath_ladder = "/storage/emulated/0/Download/Handheld.bin";
        private static int StartDownload_outlength = 0;
        private static int AuthenticationRequest_outlength = 0;
        private static int AuthenticationRequest_cipherlength = 0;
        private static int RebootDevice_outlength = 0;
        private static int HandShake_outlength = 0;

        private static byte[] StartDownload_output = new byte[16];
        private static byte[] AuthenticationRequest_cipherText = new byte[16];
        private static byte[] RebootDevice_cipherText = new byte[16];
        private static byte[] HandShake_cipherText = new byte[16];
        private static byte[] tinput = new byte[16];
        private static char[] tinput_char = new char[16];


        @SuppressLint("HandlerLeak")
        public static Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case UPDATE_PROCESS_SartDownload://更新UI
                        progressDialog.setMessage("正在发送下载请求...");
                        break;
                    case UPDATE_PROCESS_AuthenticationRequest://更新UI
                        progressDialog.setMessage("正在进行身份认证...");
                        break;
                    case UPDATE_PROCESS_RebootDevice://更新UI
                        progressDialog.setMessage("开始重启...");
                        break;
                    case UPDATE_PROCESS_HandShake://更新UI
                        progressDialog.setMessage("正在握手...");
                        break;
                    case UPDATE_PROCESS_DownloadData://更新UI
                        double a = div(process, Maximum, 2);
                        progressDialog.setProgress( (int)(a * 100));
                        progressDialog.setMessage("已下载文件：" + process + "/" + Maximum + "块");
                        break;
                    case UPDATE_PROCESS_SUCCESS://更新UI
                        progressDialog.setProgress(100);
                        progressDialog.setMessage("下载成功");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();//关闭对话框
                        break;
                    case UPDATE_PROCESS_FAIL://更新UI
                        progressDialog.setProgress(0);
                        progressDialog.setMessage("下载失败,请重试");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();//关闭对话框
                        break;
                    case UPDATE_PROCESS_SartDownload_FAIL://更新UI
                        progressDialog.setMessage("发送下载请求失败，请重试");
                        break;
                    case UPDATE_PROCESS_AuthenticationRequest_FAIL://更新UI
                        progressDialog.setMessage("身份认证失败，请重试");
                        break;
                    case UPDATE_PROCESS_RebootDevice_FAIL://更新UI
                        progressDialog.setMessage("重启失败，请重试");
                        break;
                    case UPDATE_PROCESS_HandShake_FAIL://更新UI
                        progressDialog.setMessage("握手失败，请重试");
                        break;
                    case UPDATE_PROCESS_RESET://更新UI
                        progressDialog.setProgress(0);
                        break;

                    default:
                        break;
                }

            }
        };

        public static double div(double v1, double v2, int scale) {
            if (scale < 0) {
                throw new IllegalArgumentException(
                        "The scale must be a positive integer or zero");
            }
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }


        //安全下载
        public void Download_Safe(String filePath, int down) throws Exception {
            System.out.println("开始下载");
            process = 0;
            byte code = 0x00;//密匙
            byte[] input = new byte[16];
            byte[] output = new byte[16];
            byte[] sendbuf = new byte[512];
            String blockDownloadResponse = null;
            if(down == 0){//梯形图
                downAddress = 0x8038013;
            }else{
                downAddress = 0x8038013;
            }
            Message message0 = new Message();
            Message message1 = new Message();
            Message message2 = new Message();
            Message message3 = new Message();
            Message message4 = new Message();
            Message message5 = new Message();
            Message message6 = new Message();
            Message message7 = new Message();
            Message message8 = new Message();
            Message message9 = new Message();
            Message message10 = new Message();
            message0.what = UPDATE_PROCESS_RESET;
            handler.sendMessage(message0);
            //发送下载请求
            if(StartDownload()){
                System.out.println("发送下载请求");
                message1.what = UPDATE_PROCESS_SartDownload;
                handler.sendMessage(message1);
                Thread.sleep(100);
                //身份认证
                if (AuthenticationRequest()){
                    System.out.println("身份认证");
                    message2.what = UPDATE_PROCESS_AuthenticationRequest;
                    handler.sendMessage(message2);
                    Thread.sleep(100);
                    //等待重启结束
                    if (RebootDevice()){
                        System.out.println("等待机器重启");
                        message3.what = UPDATE_PROCESS_RebootDevice;
                        handler.sendMessage(message3);
                        Thread.sleep(100);
                        //握手
                        if (HandShake()){
                            System.out.println("发送握手请求");
                            message4.what = UPDATE_PROCESS_HandShake;
                            handler.sendMessage(message4);
                            File file = new File(filePath);
                            //发送文件信息
                            if(file.exists()){
                                System.out.println("判断文件是否存在");
                                FileInputStream inputStream = new FileInputStream(file);
                                //FileOutputStream outputStream = new FileOutputStream(file);//暂定
                                //文件长度最多64位
                                long fileLength = file.length();
                                downloadstep = (int) (fileLength / 512);
                                //下载程序块数
                                //   int blockNum = (int) fileLength / (SendBufSize + (isAddCode ? 1 : 0));
                                int blockNum = (int) fileLength / (SendBufSize + (isAddCode ? 1 : 0));
                                int remainBytes = (int) fileLength % (SendBufSize + (isAddCode ? 1 : 0));
                                int totalBlockNum = blockNum;

                                if (remainBytes != 0){
                                    totalBlockNum = totalBlockNum + 1;
                                }
                                Maximum = totalBlockNum;
//                            if(fileLength > flashSize)
//                            {
//                                inputStream.close();
//                                return;
//                            }
                                //              //发送文件信息帧
                                //                  Thread.sleep(100);
                                if (SendFileInfo(518, totalBlockNum))
                                {
                                    //发送块
                                    Thread.sleep(100);
                                    for(int i = 0; i < blockNum; i++){
                                        inputStream.read(sendbuf, 0 , SendBufSize);
                                        if (isAddCode){
                                            code = (byte) inputStream.read();
                                        }

                                        int repeayCount = REPEAT_TIMES;
                                        blockDownloadResponse = "OTHER";
                                        while(!("SUCCESS".equals(blockDownloadResponse)) && repeayCount-- >= 0){
                                            blockDownloadResponse = DownloadBlockSafe(sendbuf, SendBufSize, downAddress, isAddCode, code);
                                            if(!("SUCCESS".equals(blockDownloadResponse))){
                                                if("DECIPHER_FALL".equals(blockDownloadResponse)){
                                                    System.out.println("解密失败");
                                                }else if("FLASH_ERROR".equals(blockDownloadResponse)){
                                                    System.out.println("写flash失败");
                                                }else if("TIME_OUT".equals(blockDownloadResponse)){
                                                    System.out.println("超时");
                                                }else if("ERASE_ERROR".equals(blockDownloadResponse)){
                                                    System.out.println("擦除失败");
                                                }else if("ADDRESS_ERROR".equals(blockDownloadResponse)){
                                                    System.out.println("地址错误");
                                                }else if("OTHER".equals(blockDownloadResponse)){
                                                    System.out.println("其他");
                                                }
                                                Thread.sleep((50));
                                            }
                                        }
                                        if(!("SUCCESS".equals(blockDownloadResponse))){
                                            System.out.println("" + i + "块数据帧错误");
                                            break;
                                        }else{
                                            process++;
                                            message5.what = UPDATE_PROCESS_DownloadData;
                                            handler.sendMessage(message5);
                                            message5 = new Message();
                                            System.out.println("" + i + "块数据下载成功");
                                            downAddress += SendBufSize;
                                        }
                                        Thread.sleep(250);
                                    }
                                    //发送剩余字节（最后一块）
                                    assert blockDownloadResponse != null;
                                    if("SUCCESS".equals(blockDownloadResponse) || totalBlockNum == 1){
                                        if(remainBytes != 0){
                                            byte[] remainDatas = new byte[518];
                                            inputStream.read(remainDatas, 0, remainBytes);
                                            System.arraycopy(remainDatas, 0, sendbuf, 0, remainBytes);
                                            for(int i = remainBytes; i < SendBufSize; i++){
                                                sendbuf[i] = (byte)0xff;
                                            }
                                            int repeatCount = 3;
                                            blockDownloadResponse = "OTHER";
                                            while(!("SUCCESS".equals(blockDownloadResponse)) && repeatCount-- >= 0){
                                                blockDownloadResponse = DownloadBlockSafe(sendbuf, SendBufSize, downAddress, isAddCode, code);
                                                if (!("SUCCESS".equals(blockDownloadResponse))) {
                                                    Thread.sleep(10);
                                                }
                                            }
                                            if(!("SUCCESS".equals(blockDownloadResponse))){
                                                System.out.println("最后一个数据帧错误");
                                            }
                                        }
                                    }
                                    if("SUCCESS".equals(blockDownloadResponse)){
                                        System.out.println("下载成功");
                                        message6.what = UPDATE_PROCESS_SUCCESS;
                                        handler.sendMessage(message6);
                                    }else{
                                        System.out.println("下载失败");
                                        message6.what = UPDATE_PROCESS_FAIL;
                                        handler.sendMessage(message6);
                                    }
                                }
                            }else{
                                System.out.println("文件不存在");
                            }
                        }else{
                            System.out.println("握手失败");
                            message10.what = UPDATE_PROCESS_HandShake_FAIL;
                            handler.sendMessage(message10);
                        }
                    }else{
                        System.out.println("机器重启失败");
                        message9.what = UPDATE_PROCESS_RebootDevice_FAIL;
                        handler.sendMessage(message9);
                    }
                }else{
                    System.out.println("身份认证失败");
                    message8.what = UPDATE_PROCESS_AuthenticationRequest_FAIL;
                    handler.sendMessage(message8);
                }
            }else{
                System.out.println("发送下载请求失败");
                message7.what = UPDATE_PROCESS_SartDownload_FAIL;
                handler.sendMessage(message7);
            }

        }

        //启动下载请求
        private boolean StartDownload() throws Exception {
            Boolean downloadType = false;
            byte[] input = new byte[16];
            byte[] sendfram = new byte[20];
            byte[] receivefram = new byte[1024];
            int length = 0;
            int retryTimes = REPEAT_TIMES;
            short usTemp = 0;
            while (!downloadType && retryTimes-- >= 0){
                //构造发送帧
                GetCipherTimeText(new byte[]{(byte)0x55, (byte)0xAA, (byte)0x55, (byte)0xAA}, 4, 1);//output在加密函数中赋值
                sendfram[0] = (byte)0xE0;
                sendfram[1] = (byte)StartDownload_outlength;
                System.arraycopy(StartDownload_output, 0, sendfram, 2 , 16);
                usTemp = UDPSocket.GetCRC(sendfram,sendfram.length-2);
                sendfram[sendfram.length - 1] = (byte)usTemp ;
                sendfram[sendfram.length - 2] =  (byte)(usTemp >> 8) ;
                MainActivity.UIudpsocket.upload_sendinformatin(sendfram);
                Thread.sleep(50);
                MainActivity.UIudpsocket.upload_getinfomation(receivefram);
                if((receivefram[0] & 0xff) == 0xF0){
                    //获取密文长度
                    int len = receivefram[1];
                    System.arraycopy(receivefram, 2, input, 0, len);
                    //解密
                    byte[] output = AES.Decrypt(input);
                    //byte[] a = com.example.airquality.tools.AES.Encrypt(output);
                    if((output[0] & 0xff) == 0xAA){
                        downloadType = true;
                    }else{
                        downloadType = false;
                    }
                }
            }
            return downloadType;
        }

        private boolean AuthenticationRequest() throws Exception {
            Boolean result = false;
            byte[] sendfram = new byte[100];
            byte[] receivefram = new byte[1024];
            int length = 0;
            int retryTimes = REPEAT_TIMES;
            short usTemp = 0;
            while (!result && retryTimes-- >= 0){
                GetCipherTimeText(new byte[]{(byte)0x66, (byte)0xBB, (byte)0x66, (byte)0xBB}, 4, 2);
                //构造发送帧
                sendfram[0] = (byte) 0xE1;
                sendfram[1] = (byte) AuthenticationRequest_outlength;
                //sendfram.length = 4 + length;
                System.arraycopy(AuthenticationRequest_cipherText, 0 , sendfram, 2 , AuthenticationRequest_outlength);
                //sendfram.length待定
                usTemp = UDPSocket.GetCRC(sendfram,sendfram.length-2);
                sendfram[sendfram.length - 1] = (byte)usTemp ;
                sendfram[sendfram.length - 2] =  (byte)(usTemp >> 8) ;
                MainActivity.UIudpsocket.upload_sendinformatin(sendfram);
                Thread.sleep(50);
                MainActivity.UIudpsocket.upload_getinfomation(receivefram);
                if((receivefram[0] & 0xff) == 0xF1){
                    //获取随机数长度
                    AuthenticationRequest_outlength = receivefram[1];
                    byte[] random = new byte[4];
                    System.arraycopy(receivefram, 2, random, 0, AuthenticationRequest_outlength);
                    //构造发送帧
                    //byte[] random = new byte[]{0,0,0,0};
                    AuthenticationRequest_outlength = 4;
                    byte[] md5 = GetCipherTimeText(random , AuthenticationRequest_outlength, 3);
                    int md5Length = md5.length;
                    //构造帧
                    sendfram[0] = (byte) 0xE2;
                    sendfram[1] = (byte) AuthenticationRequest_cipherlength;
                    sendfram[2] = (byte) md5Length;
                    System.arraycopy(AuthenticationRequest_cipherText, 0 , sendfram, 3, AuthenticationRequest_cipherlength);
                    System.arraycopy(md5, 0 , sendfram, 3 + AuthenticationRequest_cipherlength, md5Length);
                    length = 5 + AuthenticationRequest_cipherlength + md5Length;
                    usTemp = UDPSocket.GetCRC(sendfram,length-2);
                    sendfram[length - 1] = (byte)usTemp ;
                    sendfram[length - 2] =  (byte)(usTemp >> 8) ;
                    MainActivity.UIudpsocket.upload_sendinformatin(sendfram);
                    Thread.sleep(20);
                    MainActivity.UIudpsocket.upload_getinfomation(receivefram);
                    if((receivefram[0] & 0xff) == 0xF2){
                        length = receivefram[1];
                        byte[] input = new byte[16];
                        //获取密文
                        System.arraycopy(receivefram, 2, input, 0, length);
                        //解密
                        byte[] output = AES.Decrypt(input);
                        if((output[0] & 0xff) == 0xAA){
                            result = true;
                        }
                    }
                }
            }
            return result;
        }

        //重启设备
        private Boolean RebootDevice() throws Exception {
            boolean rtnValue = false;
            byte[] sendfram = new byte[18];
            byte[] receivefram = new byte[1024];
            int outLength = 0;
            int retryTimes = REPEAT_TIMES;
            while (!rtnValue && retryTimes-- >= 0){
                //重启设备
                GetCipherTimeText(new byte[]{(byte) 0x88, (byte) 0xDD, (byte) 0x88, (byte) 0xDD}, 4, 4);
                sendfram[0] = (byte) 0xE3;
                sendfram[1] = (byte) RebootDevice_outlength;
                System.arraycopy(RebootDevice_cipherText, 0, sendfram, 2, RebootDevice_outlength);
                MainActivity.UIudpsocket.upload_sendinformatin(sendfram);
                Thread.sleep(50);
                MainActivity.UIudpsocket.upload_getinfomation(receivefram);
                if((receivefram[0] & 0xff) == 0xF3){
                    rtnValue =true;
                }
            }
            return rtnValue;
        }

        //握手
        private Boolean HandShake() throws Exception {
            boolean rtnValue = false;
            byte[] sendfram = new byte[20];
            byte[] receivefram = new byte[1024];
            int outLength = 0;
            int retryTimes = REPEAT_TIMES;
            short usTemp = 0;
            while (!rtnValue && retryTimes-- >= 0){

                //重启设备
                GetCipherTimeText(new byte[]{(byte) 0x77, (byte) 0xCC, (byte) 0x77, (byte) 0xCC}, 4, 5);
                sendfram[0] = (byte) 0xE4;
                sendfram[1] = (byte) HandShake_outlength;
                System.arraycopy(HandShake_cipherText, 0, sendfram, 2, HandShake_outlength);
                usTemp = UDPSocket.GetCRC(sendfram,sendfram.length-2);
                sendfram[sendfram.length - 1] = (byte)usTemp ;
                sendfram[sendfram.length - 2] =  (byte)(usTemp >> 8) ;
                MainActivity.UIudpsocket.upload_sendinformatin(sendfram);
                Thread.sleep(50);
                MainActivity.UIudpsocket.upload_getinfomation(receivefram);
                if((receivefram[0] & 0xff) == 0xF4){
                    rtnValue =true;
                }
            }
            return rtnValue;
        }

        //发送文件信息
        private Boolean SendFileInfo(int blockSize, int blockNum) throws SocketException, UnknownHostException, InterruptedException {
            boolean rtnValue = false;
            int retryTimes = REPEAT_TIMES;
            byte[] sendfram = new byte[100];
            byte[] receivefram = new byte[1024];
            while (!rtnValue && retryTimes-- >= 0){
                //发送文件信息帧 E5	加密文件块大小	加密文件块数	签名
                sendfram[0] = (byte) 0xE5;
                sendfram[1] = (byte)(blockSize & 0xFF);
                sendfram[2] = (byte)((blockSize >> 8) & 0xFF);
                sendfram[3] = (byte)(blockNum & 0xFF);
                sendfram[4] = (byte)((blockNum >> 8) & 0xFF);
                byte[] tmp = new byte[4];
                System.arraycopy(sendfram, 1 , tmp, 0, 4);
                byte[] md5 = MD5.getmd5(tmp);
                System.arraycopy(md5, 0 , sendfram, 5, md5.length);
                MainActivity.UIudpsocket.upload_sendinformatin(sendfram);
                Thread.sleep(50);
                MainActivity.UIudpsocket.upload_getinfomation(receivefram);
                if ((receivefram[0] & 0xff) == 0xF5 && (receivefram[1] & 0xff) == 0xAA){
                    rtnValue = true;
                }
            }
            return rtnValue;
        }

        //获取加密数据
        private byte[] GetCipherTimeText(byte[] code, int length, int i) throws Exception {
            //byte[] input = new byte[16];
            System.arraycopy(code, 0, tinput,0, length);
            byte []tmp = {1};
            System.arraycopy(tmp, 0, tinput, length, tmp.length);
            if(i == 1){
                Arrays.fill(StartDownload_output, (byte)0);
                //tinput, StartDownload_output交换位置测试（context：StartDownload_output， key:tinput）
                //以下同上
                StartDownload_output = AES.Encrypt(tinput);
                StartDownload_outlength = StartDownload_output.length;
//            return com.example.airquality.tools.MD5.getmd5(tinput);//md5错误
            }else if(i == 2){
                Arrays.fill(AuthenticationRequest_cipherText, (byte)0);
                AuthenticationRequest_cipherText = AES.Encrypt(tinput);
                AuthenticationRequest_outlength = AuthenticationRequest_cipherText.length;
//            return com.example.airquality.tools.MD5.getmd5(tinput);
            }else if(i == 3){
                AuthenticationRequest_cipherText = AES.Encrypt(tinput);
                AuthenticationRequest_cipherlength = AuthenticationRequest_cipherText.length;
                return MD5.getmd5(tinput);
            } else if(i == 4){
                Arrays.fill(RebootDevice_cipherText, (byte)0);
                RebootDevice_cipherText = AES.Encrypt(tinput);
                RebootDevice_outlength = RebootDevice_cipherText.length;
//            return com.example.airquality.tools.MD5.getmd5(tinput);
            }else if(i == 5){
                Arrays.fill(HandShake_cipherText, (byte)0);
                HandShake_cipherText = AES.Encrypt(tinput);
                HandShake_outlength = HandShake_cipherText.length;
//            return com.example.airquality.tools.MD5.getmd5(tinput);
            }

            return null;
        }


        //long型数据转换为byte数组
        private byte[] getBytes(long s, boolean bBigEnding) {
            byte[] buf = new byte[8];
            if (bBigEnding) {
                for (int i = buf.length - 1; i >= 0; i--) {
                    buf[i] = (byte) (s & 0x00000000000000ff);
                    s >>= 8;
                }
            }else {
                for (int i = 0; i < buf.length; i++) {
                    buf[i] = (byte) (s & 0x00000000000000ff);
                    s >>= 8;
                }
            }
            return buf;
        }

        private String HasReceiveFrame(int ms) throws InterruptedException {
            String rtnValue = "time_out";
            boolean hasResponse = false;
            int repeatTimes = ms / 20;
            byte[] receivefram = new byte[1024];

            while(!hasResponse && repeatTimes-- > 0){
                //读取端口数据
                if(repeatTimes-- > 0){
                    MainActivity.UIudpsocket.upload_getinfomation(receivefram);
                    hasResponse = true;
                    if((receivefram[0] & 0xff) == 0xF6){
                        if((receivefram[5] & 0xff) == 0x80){
                            rtnValue = "SUCCESS";
                        }else if((receivefram[5] & 0xff) == 0x81){
                            rtnValue = "DECIPHER_FAIL";
                        }else if((receivefram[5] & 0xff) == 0x82){
                            rtnValue = "FLASH_ERROR";
                        }else if((receivefram[5] & 0xff) == 0x83){
                            rtnValue = "TIME_OUT";
                        }else if((receivefram[5] & 0xff) == 0x84){
                            rtnValue = "ERASE_ERROR";
                        }else if((receivefram[5] & 0xff) == 0x85){
                            rtnValue = "ADDRESS_ERROR";
                        }else{
                            rtnValue = "OTHER";
                        }
                    }
                }
                if(!hasResponse){
                    Thread.sleep(100);
                }
            }
            return rtnValue;
        }

        private String DownloadBlockSafe(byte[] block, int blockLength, int downloadAddress, boolean addCode, byte code) throws InterruptedException, SocketException, UnknownHostException {
            // 求出校验和
            int retryTimes = 10;
            int blockLen = 0;
            int sendframelen = 0;
            byte sum = 0;
            byte[] bAddress = new byte[4];
            byte[] codeWrite = new byte[1];
            byte[] sendBlock = new byte[blockLength + 6];
            byte[] sendframe = new byte[1000];

            retryTimes = 100;
            for (int i = 0; i < 4; i++) {
                sum += (byte)((downloadAddress >> ((3 - i) * 8)) & 0xff);
                sendBlock[blockLen++] = (byte)((downloadAddress >> ((3 - i) * 8)) & 0xff);
            }
            for (int i = 0; i < blockLength; i++) {
                sum += block[i];
                sendBlock[blockLen++] = block[i];
            }
            if (addCode) {
                sum += code;
            }
            sum = (byte)(0x100 - sum);
            sendBlock[blockLen++] = code;
            sendBlock[blockLen++] = sum;
            byte[] md5 = MD5.getmd5(sendBlock);
            sendframe[0] = (byte)0xE6;
            sendframe[1] = (byte)((blockLength + 6)&0xff);
            sendframe[2] = (byte)((blockLength + 6)>>8 & 0xff);
            sendframe[3] = (byte)(md5.length);
            System.arraycopy(sendBlock, 0, sendframe, 4, blockLen);
            System.arraycopy(md5, 0, sendframe, 4+blockLen, md5.length);
            sendframelen = 4 + blockLen + md5.length;
            MainActivity.UIudpsocket.upload_sendinformatin(sendframe);
            Thread.sleep(250);

            String rtnValue = HasReceiveFrame(2000);
            return rtnValue;
        }
}
