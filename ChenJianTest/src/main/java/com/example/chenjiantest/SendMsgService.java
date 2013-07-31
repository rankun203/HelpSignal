package com.example.chenjiantest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenjian on 13-7-30.
 */
public class SendMsgService extends IntentService {
    public static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_PERSON_INFO = "com.example.chenjiantest.SEND_PERSON_INFO";
    public static final String EXTRAS_PERSON_NAME = "person_name";
    public static final String EXTRAS_PERSON_FLAG = "person_flag";
    public static final String EXTRAS_TAG_ADDRESS = "target_address";
    public static final String EXTRAS_TAG_PORT = "target_port";


    public SendMsgService(String name) {
        super(name);
    }
    public SendMsgService() {
        super("SendMsgService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if(ACTION_SEND_PERSON_INFO.equals(intent.getAction())) {
            sendPersonInfo(intent);
        }
    }
    private void sendPersonInfo(Intent intent) {
        List<String> addresses = intent.getStringArrayListExtra(EXTRAS_TAG_ADDRESS);
        Iterator<String> host = addresses.iterator();
        int port = intent.getIntExtra(EXTRAS_TAG_PORT, -1);
        if(-1 != port) {
            Log.d("dddddddddddd", "______" + addresses.size() + "port:" + port);
            Socket socket = new Socket();
            OutputStream outputStream;
            try {
                socket.bind(null);
                while(host.hasNext()) {
                    socket.connect(new InetSocketAddress(host.next(), port), SOCKET_TIMEOUT);
                    outputStream = socket.getOutputStream();
                    copyFile(getResources().openRawResource(R.raw.person_info), outputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public static void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
    }
}
