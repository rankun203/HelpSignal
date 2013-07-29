package com.cdvtc.helpsignal;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Switch;



public class MainActivity extends Activity {
    private Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void checked(Switch mSwitch) {
        mSwitch.setText(R.string.role);
        mSwitch.setTextOff(getResources().getText(R.string.survivor));
        mSwitch.setTextOn(getResources().getText(R.string.searcher));

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选中时 do some thing
                } else {
                    //非选中时 do some thing
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mySwitch = (Switch) menu.findItem(R.id.menu_switch).getActionView();
        checked(mySwitch);
        return true;
    }
    
}
