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
import java.nio.charset.StandardCharsets;
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

    public Button sendParam;

    private final BTclient btclient = new BTclient();

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
        sendParam = (Button) findViewById(R.id.button3);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //touchArea.setOnTouchListener(handleTouch);


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

        sendParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(startX,startY,0));
                sendMessageHandle(getFrame(endX,endY,1));
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

    public void sendMessageHandle(byte[] data)
    {
        getPermission();
        if (btsocket == null)
        {
            showToast("没有连接");
            return;
        }
        try {
            OutputStream os = btsocket.getOutputStream();
            os.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte[] getFrame(int x,int y,int type){
        //String s;
        byte[] data;


        if(type==0){
           //s = "01"+"00"+x+y+"00"+"\n";
            data = new byte[]{0x01,0x00,(byte)((x>>8)&0xFF),(byte)(x & 0xFF),(byte)((y>>8) & 0xFF),(byte)(y & 0xFF),0x00,0x0A};
        }
        else {
            //s = "01"+"01"+x+y+"00"+"\n";
            data = new byte[]{0x01,0x01,(byte)((x>>8)&0xFF),(byte)(x & 0xFF),(byte)((y>>8) & 0xFF),(byte)(y & 0xFF),0x00,0x0A};
        }

        return data;
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