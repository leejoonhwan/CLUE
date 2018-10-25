package com.example.a1002732.clue.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaService extends IntentService {

    static DatagramSocket socket;
    static InetAddress serverAddr;
    public static final String sIP = "localhost";


    //사용할 통신 포트
    public static final int sPORT = 7000;
    public static final int myPORT = 8000;

    private Runnable r;
    private Thread udpReceiver;
    private Thread udpSender;


    public MediaService() {
        super("MediaService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("이준환", "MediaService: onHandleIntent");
        Log.d("이준환", "MediaService: onCreate");

        try {

            socket = new DatagramSocket(myPORT);
            serverAddr = InetAddress.getByName(sIP);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        r = new UDPReceive();
        udpReceiver = new Thread(r);
        udpReceiver.start();

        /*r = new UDPSender("call");
        udpSender = new Thread(r);
        udpSender.start();*/
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class UDPSender implements Runnable {

        String msg = "";
        public UDPSender(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                Log.d("이준환", "run: ======== UDPSender =======");
                InetAddress serverAddr = InetAddress.getByName(sIP);

                byte[] buf = (msg).getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, sPORT);
                socket.send(packet);

            } catch (Exception e) {
                Log.d("이준환", "UDPSender: "+e.getMessage());
            }
        }
    }

    class UDPReceive implements Runnable {
        public Bitmap byteArrayToBitmap(byte[] byteArray) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bitmap;
        }


        @Override
        public void run() {
            Log.d("이준환", "run: ======== UDPReceive =======");

            DataOutputStream dos = null;
            ByteArrayOutputStream bos = null;

            int readNum = 0;

            while (true) {

                try {
                    byte[] buf = new byte[512];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, sPORT);
                    socket.receive(packet);
                    // 받은 내용을 문자열로 변환

                    String str = new String(packet.getData()).trim();

                    if (str.equals("start")) {

                        // 시작한다는 신호 받으면 파일 하나 만듬

                        bos = new ByteArrayOutputStream();


                        // 파일에 기록할 수 있는 객체 생성
                        //dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

                    } else if (str.equals("end")) {
                        readNum = 0;
                        System.out.println("전송완료");

                        byte[] bytes = bos.toByteArray();

                        // Bitmap bitmap = byteArrayToBitmap(bytes);

                        long time = System.currentTimeMillis();
                        SimpleDateFormat dayTime = new SimpleDateFormat("yyyymmddhhmmss");
                        String dateStr = dayTime.format(new Date(time));


                        try {
                            FileOutputStream fileOuputStream = new FileOutputStream("/Users/1002732/Desktop/UDP/client/" + dateStr + ".jpg");
                            fileOuputStream.write(bytes);
                            fileOuputStream.close();

                            System.out.println("Done");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        dos.close();
                        break;
                    } else {

                        bos.write(packet.getData(), 0, packet.getData().length);
                        readNum++;
                        //dos.write(str.getBytes(), 0, str.getBytes().length);
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}