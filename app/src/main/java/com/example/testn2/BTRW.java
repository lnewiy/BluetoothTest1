package com.example.testn2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    public TextView showState;
    public Switch toggleState;

    private BTclient btclient = new BTclient();

    private Toast mToast;
    public BTControl mController = new BTControl();
    private BluetoothSocket btsocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btrw);


        touchArea = (View) findViewById(R.id.hint1);
        showState = (TextView) findViewById(R.id.conntv2);
        toggleState = (Switch) findViewById(R.id.switch2);

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

    }

    private final View.OnTouchListener handleTouch = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {

            int y = 1152*(int) event.getY()/touchArea.getHeight();//结果归化到1080*1920
            int x = 576*(int) event.getX()/touchArea.getWidth();

            if(x>=576){x=576;}if(x<0){x=0;}
            if(y>=1152){y=1152;}if(y<0){y=0;}//越界


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.e("touch", "起始位置x:" + x + ",y:" + y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.e("touch", "当前位置x:" + x + ",y:" + y);
                    sendMessageHandle(getFrame(x,y));
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e("touch", "结束位置x:" + x + ",y:" + y);
                    break;
                default:
                    break;
            }
            return true;

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
    private class BTclient extends Thread{
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
}