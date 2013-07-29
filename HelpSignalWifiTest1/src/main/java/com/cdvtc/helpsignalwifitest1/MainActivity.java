package com.cdvtc.helpsignalwifitest1;

import android.app.*;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.cdvtc.helpsignalwifitest1.R;

/**
 * Created by mindfine on 13-7-29.
 */
public class MainActivity extends Activity {

    private WifiP2pManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//set testActivity.

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    }

    public void getMsg(View view) {

        WifiManager mng = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        int a = mng.getWifiState();
        if (mng.getWifiState() == 1) {
            mng.setWifiEnabled(true);
            EditText msgText = ((EditText)findViewById(R.id.test_wifiStateText));
            msgText.setText("Wifi已打开\r\n" + msgText.getText());
        } else {
            mng.setWifiEnabled(false);
            EditText msgText = ((EditText)findViewById(R.id.test_wifiStateText));
            msgText.setText("Wifi已关闭\r\n" + msgText.getText());
        }


    }

    public void showText(String txt){
        EditText msg = (EditText)findViewById(R.id.test_wifiStateText);
        msg.setText(txt);
    }
}
