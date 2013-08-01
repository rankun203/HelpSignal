package com.example.chenjiantest;

import android.app.Activity;
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
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by chenjian on 13-8-1.
 */
public class MyWifiP2p implements WifiP2pManager.PeerListListener,WifiP2pManager.ConnectionInfoListener{
    private MainActivity activity;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private IntentFilter intentFilter = new IntentFilter();
    private boolean isWifiP2pEnabled = false;
    private Collection<WifiP2pDevice> deviceList;

    public void init(MainActivity activity, WifiP2pManager manager) {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        this.activity = activity;
        this.manager = manager;
        channel = manager.initialize(activity.getBaseContext(), activity.getMainLooper(), null);
    }

    public void setWifiP2pEnabled(boolean wifiP2pEnabled) {
        isWifiP2pEnabled = wifiP2pEnabled;
    }

    public WifiP2pManager getManager() {

        return manager;
    }

    public WifiP2pManager.Channel getChannel() {
        return channel;
    }

    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    public boolean isWifiP2pEnabled() {
        return isWifiP2pEnabled;
    }

    public Collection<WifiP2pDevice> getDeviceList() {
        return deviceList;
    }

    /**
     * 手动刷新
     */
    public void refreshPeer() {
        if(isWifiP2pEnabled) {             //判断Wifi DIrect是否已经开启
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(activity, "refresh success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(activity, "refresh fail", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if(null != manager && null != channel) {
                Toast.makeText(activity, "Please enable wifi p2p", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("cj_test_error", "channel or manager is null");
            }
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
            Log.d("eeeeeeeee", "___" + personInfo.getName() + personInfo.getFlag());
        }

        @Override
        protected PersonInfo doInBackground(Object... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8989);
                Socket client = serverSocket.accept();
                InputStream inputStream = client.getInputStream();
                return PersonConfig.readPersonInfo(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        deviceList = peers.getDeviceList();
        Iterator<WifiP2pDevice> iter = deviceList.iterator();
        ArrayList<String> addresses = new ArrayList<String>();
        while(iter.hasNext()) {
            addresses.add(iter.next().deviceAddress);
        }
       Bundle bundle = new Bundle();
        Intent intent = new Intent(activity, SendMsgService.class);
        intent.setAction(SendMsgService.ACTION_SEND_PERSON_INFO);
        intent.putStringArrayListExtra(SendMsgService.EXTRAS_TAG_ADDRESS, addresses);
        intent.putExtra(SendMsgService.EXTRAS_TAG_PORT, 8989);
        activity.startService(intent);
        activity.showPeer();
        //readPersonInfo(getResources().openRawResource(R.raw.person_info));
    }
    public static void enableP2p(WifiP2pManager manager, WifiP2pManager.Channel channel) {
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
    public static void disableP2p(WifiP2pManager manager, WifiP2pManager.Channel channel) {
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
}
