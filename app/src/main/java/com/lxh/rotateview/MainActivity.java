package com.lxh.rotateview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Bitmap> list = new ArrayList<Bitmap>();
        list.add(BitmapFactory.decodeResource(getResources(), R.drawable.loading_01));
        list.add(BitmapFactory.decodeResource(getResources(), R.drawable.loading_02));
        list.add(BitmapFactory.decodeResource(getResources(), R.drawable.loading_03));
        list.add(BitmapFactory.decodeResource(getResources(), R.drawable.loading_04));
        list.add(BitmapFactory.decodeResource(getResources(), R.drawable.loading_05));
        list.add(BitmapFactory.decodeResource(getResources(), R.drawable.loading_05));
        RotateView view = (RotateView) findViewById(R.id.rotateView);
        view.setData(list);
        view.start();
    }
}
