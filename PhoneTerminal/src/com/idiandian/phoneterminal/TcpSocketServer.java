package com.idiandian.phoneterminal;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpSocketServer {

    private static final int TCP_PORT = 9999;// 端口监听
    private ServerSocket mServer = null;
    private ExecutorService mExecutorService = null;// 线程池

    public TcpSocketServer() {
        try {
            mServer = new ServerSocket(TCP_PORT);
            mExecutorService = Executors.newCachedThreadPool();// 创建一个线程池
            Log.i("test", "tcp socket Server Start...");
            Socket client = null;
            while (true) {
                client = mServer.accept();
                mExecutorService.execute(new SocketService(client));// 开启一个客户端线程.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                mServer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public class SocketService implements Runnable {

        private Socket mClient = null;

        private DataInputStream mStreamIn = null;

        private DataOutputStream mStreamOut;

        public SocketService(Socket socket) throws IOException {
            mClient = socket;
            open();
        }

        private void open() throws IOException {
            // TODO Auto-generated method stub
            mStreamIn = new DataInputStream(new BufferedInputStream(mClient.getInputStream()));
            mStreamOut = new DataOutputStream(new BufferedOutputStream(mClient.getOutputStream()));
        }

        private void close() throws IOException {
            Log.i("test", "close client socket");
            if (mClient != null) {
                mClient.close();
            }
            if (mStreamOut != null) {
                mStreamOut.close();
            }
            if (mStreamIn != null) {
                mStreamIn.close();
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                String msg;
                while (true) {
                    if ((msg = mStreamIn.readLine()) != null) {
                        if (msg.equals("exit")) {
                            Log.i("test", "sssssssssss");
                            mStreamIn.close();
                            msg = "user:" + mClient.getInetAddress();
                            close();
                            sendmsg(msg);
                            break;
                        } else if (msg.equals("hello")) {
                            msg = mStreamIn.readLine();
                            msg = mClient.getInetAddress() + " : " + msg;
                            sendmsg(msg);
                        } else {
                            Log.i("test", "===== unknown msg");
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("server error");
                ex.printStackTrace();
            }
        }

        /**
         * 发送消息给所有客户端
         */
        public void sendmsg(String msg) {
            Log.i("test", "send [" + msg);
            byte[] buffer = msg.getBytes();
            if (buffer != null) {
                try {
                    mStreamOut.write(buffer);
                    mStreamOut.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
