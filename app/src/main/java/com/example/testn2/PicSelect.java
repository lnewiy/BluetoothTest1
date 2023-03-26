package com.example.testn2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class PicSelect extends AppCompatActivity {

    public Button pic1,pic2,pic3,pic4,pic5,pic6;

    public TextView showState;

    public Switch toggleState;

    private final ArrayList<String> requestList = new ArrayList<>();

    private int REQ_Permission_CODE = 1;

    private boolean DEFAULT_STATE = true;

    private final PicSelect.BTclient btclient = new PicSelect.BTclient();

    private Toast mToast;
    public BTControl mController = new BTControl();
    private BluetoothSocket btsocket;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.btselect);

        pic1 = findViewById(R.id.pic1);
        pic2 = findViewById(R.id.pic2);
        pic3 = findViewById(R.id.pic3);
        pic4 = findViewById(R.id.pic4);
        pic5 = findViewById(R.id.pic5);
        pic6 = findViewById(R.id.pic6);

        showState = (TextView) findViewById(R.id.picState1);
        toggleState = (Switch) findViewById(R.id.picSw1);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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


    public void ButtonActivity(){

        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(1));
            }
        });

        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(2));
            }
        });

        pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(3));
            }
        });

        pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(4));
            }
        });

        pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(5));
            }
        });

        pic6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageHandle(getFrame(6));
            }
        });
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


    public String getFrame(int code){
        return "02"+"00"+code+"00"+"\n";
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
