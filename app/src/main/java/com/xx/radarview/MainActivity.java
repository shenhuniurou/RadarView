package com.xx.radarview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadarView radarView = (RadarView) findViewById(R.id.radarView);
        radarView.setVertexPaintColor(getColor(android.R.color.darker_gray));
        radarView.setValuePaintColor(getColor(R.color.colorPrimary));
    }
}
