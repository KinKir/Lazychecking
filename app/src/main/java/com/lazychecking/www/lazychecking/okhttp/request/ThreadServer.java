package com.lazychecking.www.lazychecking.okhttp.request;

import android.util.Log;

import com.lazychecking.www.lazychecking.network.ServerIP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ThreadServer extends Thread {
        public volatile boolean exit = false;
        private File file;
    private ServerIP mIP=ServerIP.getInstance();
        public  ThreadServer(File file){
            this.file=file;
           // Log.i("info2", "cs: ");
        }
        @Override
        public void run() {
            while (!exit) {
                try {
                Log.i("info2", "run0: ");
                Socket s = new Socket(mIP.getIp(), 6667);
                Log.i("info2", "file.length: "+file.length());
                    long filelength=file.length();
                    long total=filelength+16;
                    Log.i("info2", "total: "+total);
                    Log.i("info2", "filename.length: "+file.getName().length());
                    DataOutputStream  dos = new DataOutputStream(s.getOutputStream());
                    dos.writeLong(total);
                    dos.flush();
                    dos.writeLong(file.getName().length());
                    dos.flush();
                   // dos.writeChars(file.getName());
                    //dos.flush();
                    // 封装图片,图片只能使用字节流，为了高效，用缓冲字节流
                BufferedInputStream bi = new BufferedInputStream(new FileInputStream(
                        file));

                // 把通道中的字节流包装成缓冲字节流
                BufferedOutputStream bo = new BufferedOutputStream(s.getOutputStream());
                Log.i("info2", "run2: ");
                // 接收图片,并发送给服务器
                byte[] bys = new byte[1024];
                int len = 0;// 读取的实际长度，没有数据时，为-1
                while ((len = bi.read(bys)) != -1) {
                    Log.i("info2", "run: "+len);
                    bo.write(bys, 0, len);
                    bo.flush();
                }
                Log.i("info2", "run3: ");
                // 提醒服务器已经读取完毕，终止
                //s.shutdownOutput();

                // 接收反馈
                InputStream in = s.getInputStream();
                byte[] by = new byte[1024];
                //肯定有内容的，就不判断了
                int len1 = in.read(by);
                String str = new String(by, 0, len1);
                System.out.println(str);

                // 释放资源
                bi.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
    }