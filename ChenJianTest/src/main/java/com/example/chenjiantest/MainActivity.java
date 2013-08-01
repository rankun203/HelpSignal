package com.example.chenjiantest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
@SuppressLint("NewApi")
public class MainActivity extends Activity {
    private BroadcastReceiver receiver = null;
    private MyWifiP2p myWifiP2p = new MyWifiP2p();
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(myWifiP2p);
        registerReceiver(receiver, myWifiP2p.getIntentFilter());
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWifiP2p.init(this, (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE));
    }
    @Override
    protected void onDestroy() {
        PersonConfig.writePersonInfo(new PersonInfo());
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    public void getPeers(View view) {
         myWifiP2p.refreshPeer();
    }
    public void showPeer() {
        Log.d("showPeer", "_________________________________");
        TextView textView = (TextView) findViewById(R.id.cj_txt_info);
        String s = "";
        if(myWifiP2p.getDeviceList().size() <= 0) {
            s = "no peers";
        } else {
            s = "search " + myWifiP2p.getDeviceList().size() + " peers\n";
            Iterator<WifiP2pDevice> iter = myWifiP2p.getDeviceList().iterator();
            while(iter.hasNext())
                s += "  " + iter.next().deviceName + "  \n";
        }
        textView.setText(s);
    }
    public void enableP2p(View view) {
        MyWifiP2p.enableP2p(myWifiP2p.getManager(), myWifiP2p.getChannel());
    }
    public void disableP2p(View view) {
        MyWifiP2p.disableP2p(myWifiP2p.getManager(), myWifiP2p.getChannel());
    }
}
