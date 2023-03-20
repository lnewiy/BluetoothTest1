package com.example.testn2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.SimpleTimeZone;
import java.util.UUID;

public class BTRW extends AppCompatActivity {

    private final ArrayList<String> requestList = new ArrayList<>();

    private int REQ_Permission_CODE = 1;

    private boolean DEFAULT_STATE = true;

    public View touchArea;
    public Canvas mCanvas;

    public DrawingView mDrawing;

    public TextView showState;
    public Switch toggleState;

    public Button clearPaint;

    private BTclient btclient = new BTclient();

    private Toast mToast;
    public BTControl mController = new BTControl();
    private BluetoothSocket btsocket;

    public static int startX,startY,endX,endY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btrw);

        mCanvas = new Canvas();

        touchArea = findViewById(R.id.hint1);
        showState = (TextView) findViewById(R.id.conntv2);
        toggleState = (Switch) findViewById(R.id.switch2);
        clearPaint = (Button) findViewById(R.id.button3);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        touchArea.setOnTouchListener(handleTouch);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        btclient.connectDevice(mController.find_device(bundle.getString("deviceaddr")));

        btclient.start();
        toggleState.setChecked(DEFAULT_STATE);

        toggleState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(toggleState.isChecked()){
                    btclient.connectDevice(mController.find_device(bundle.getString("deviceaddr")));
                    showState.setText("已连接");
                    showToast("已连接设备");
                }
                else{
                    btDisconnect();
                    showState.setText("已断开");
                    showToast("已断开设备");
                }
            }
        });

        clearPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(startX,startY));
                sendMessageHandle(getFrame(endX,endY));
            }
        });

    }

    public void getParam(int x,int y,int type){
        switch (type) {
            case 1:
                startX=x;
                startY=y;
                break;
            case 0:
                endX=x;
                endY=y;
                break;
            default:
                break;
        }

    }

   private final View.OnTouchListener handleTouch = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {

            int x = (int)event.getX();
            int y = (int) event.getY();

            int y1 = 1152*(int) event.getY()/touchArea.getHeight();//结果归化到1080*1920
            int x1 = 576*(int) event.getX()/touchArea.getWidth();

            if(x1>=576){x1=576;}if(x1<0){x1=0;}
            if(y1>=1152){y1=1152;}if(y1<0){y1=0;}//越界

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendMessageHandle(getFrame(x1,y1));
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
            return false;
        }
    };


    public void getPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            requestList.add(Manifest.permission.BLUETOOTH_SCAN);
            requestList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            requestList.add(Manifest.permission.BLUETOOTH_CONNECT);
            requestList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            requestList.add(Manifest.permission.BLUETOOTH);
        }
        if(requestList.size() != 0){
            ActivityCompat.requestPermissions(this, requestList.toArray(new String[0]), REQ_Permission_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    public class BTclient extends Thread{
        private void connectDevice(BluetoothDevice device){
            try {
                getPermission();
                btsocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                btsocket.connect();
                showToast("蓝牙连接成功");
            } catch (IOException e) {
                e.printStackTrace();
                showToast("蓝牙连接失败");
            }
        }

    }

    public void showToast(String text){
        if( mToast == null){
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else{
            mToast.setText(text);
        }
        mToast.show();
    }

    public void sendMessageHandle(String msg)
    {
        getPermission();
        if (btsocket == null)
        {
            showToast("没有连接");
            return;
        }
        try {
            OutputStream os = btsocket.getOutputStream();
            os.write(msg.getBytes()); //发送出去的值为：msg
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getFrame(int x,int y){
        return "X"+"Y"+x+y+"END"+"\n";
    }

    public void btDisconnect(){
        try{
            btsocket.close();
        }catch(IOException e){
            Log.e("close","无法关闭连接");
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        if (item.getItemId() == android.R.id.home) {
            btDisconnect();
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}