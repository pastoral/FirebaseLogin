package com.munir.harbingerstudio.firebasepoweredlogin;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;

import com.munir.harbingerstudio.firebasepoweredlogin.BaseActivity;
import com.munir.harbingerstudio.firebasepoweredlogin.R;


/**
 * Created by munirul.hoque on 8/30/2017.
 */

public class Tempactivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
