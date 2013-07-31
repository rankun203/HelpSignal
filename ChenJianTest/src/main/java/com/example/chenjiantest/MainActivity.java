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
import android.provider.Settings;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
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
import java.util.List;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements WifiP2pManager.PeerListListener,WifiP2pManager.ConnectionInfoListener {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private  boolean isWifiP2pEnabled = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void finish() {
        writePersonInfo(new PersonInfo());
        super.finish();
    }

    @SuppressLint("NewApi")
    public void getPeers(View view) {
        if(isWifiP2pEnabled()) {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "refresh success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(MainActivity.this, "refresh fail", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if(null != manager && null != channel) {
                Toast.makeText(this, "Please enable wifi p2p", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("cj_test_error", "channel or manager is null");
            }
        }
   }

    public boolean isWifiP2pEnabled() {
        return isWifiP2pEnabled;
    }

    public void setWifiP2pEnabled(boolean wifiP2pEnabled) {
        isWifiP2pEnabled = wifiP2pEnabled;
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        TextView textView = (TextView) findViewById(R.id.cj_txt_info);
        String s = "";
        if(peers.getDeviceList().size() <= 0) {
            s = "no peers";
        } else {
            s = "search " + peers.getDeviceList().size() + " peers";
            Iterator<WifiP2pDevice> iter = peers.getDeviceList().iterator();
            ArrayList<String> addresses = new ArrayList<String>();
            while(iter.hasNext()) {
                addresses.add(iter.next().deviceAddress);
            }
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, SendMsgService.class);
            intent.setAction(SendMsgService.ACTION_SEND_PERSON_INFO);
            intent.putStringArrayListExtra(SendMsgService.EXTRAS_TAG_ADDRESS, addresses);
            intent.putExtra(SendMsgService.EXTRAS_TAG_PORT, 8989);
            startService(intent);
            //readPersonInfo(getResources().openRawResource(R.raw.person_info));
        }
        textView.setText(s);
    }
    public PersonInfo readPersonInfo(InputStream is) {
        PersonInfo personInfo = null;
        try{
            PersonConfig personConfig = new PersonConfig();
            android.util.Xml.parse(is, Xml.Encoding.UTF_8, personConfig);
            personInfo = personConfig.getPersonInfo();
            new AlertDialog.Builder(this).setMessage("name:" + personInfo.getName() + "  falg:" + personInfo.getFalg())
                    .setPositiveButton("关闭", null).show();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return personInfo;
    }
    public String writePersonInfo(PersonInfo p) {
        StringWriter stringWriter = new StringWriter();
        p.setFalg(10);
        p.setName("XXX");
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            xmlSerializer.setOutput(stringWriter);

            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "person");

            xmlSerializer.startTag(null, "name");
            xmlSerializer.text(p.getName());
            xmlSerializer.endTag(null, "name");

            xmlSerializer.startTag(null, "flag");
            xmlSerializer.text(p.getFalg() + "");
            xmlSerializer.endTag(null, "flag");

            xmlSerializer.endTag(null, "person");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
    public void enableP2p(View view) {
        try {
            Method m = WifiP2pManager.class.getMethod("enableP2p", WifiP2pManager.Channel.class);
            m.invoke(manager, channel);
        } catch (NoSuchMethodException e) {
            Log.e("NoSuchMethod", "No Such Method Exception!");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e("InvocationExcept", "Invocation Exception");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("IllegalAccessExcept", "IllegalAccessException");
            e.printStackTrace();
        }
    }
    public void disableP2p(View view) {
        try {
            Method m = WifiP2pManager.class.getMethod("disableP2p", WifiP2pManager.Channel.class);
            m.invoke(manager, channel);
        } catch (NoSuchMethodException e) {
            Log.e("NoSuchMethod", "No Such Method Exception!");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e("InvocationExcept", "Invocation Exception");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("IllegalAccessExcept", "IllegalAccessException");
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if(info.groupFormed) {
            new MsgAsyncTask().execute();
        }
    }
    public class MsgAsyncTask extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            PersonInfo personInfo = (PersonInfo)o;
            Log.d("eeeeeeeee", "___" + personInfo.getName() + personInfo.getFalg());
        }

        @Override
        protected PersonInfo doInBackground(Object... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8989);
                Socket client = serverSocket.accept();
                InputStream inputStream = client.getInputStream();
                return readPersonInfo(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
