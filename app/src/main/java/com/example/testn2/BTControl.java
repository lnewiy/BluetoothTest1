package com.example.testn2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Switch;
@SuppressLint("MissingPermission")
public class BTControl {
    private BluetoothAdapter mAdapter;

    public BTControl() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isSupportBlueTooth() {
        // 若支持蓝牙,则不为null,否则不支持
        return mAdapter != null;
    }



    public void BTSwitchOn(Activity act, int requestcode) {
        if (!mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            act.startActivityForResult(enableBtIntent, requestcode);

        }

    }

    public void BTSwitchOff() {
        if (mAdapter.isEnabled()) {
            mAdapter.disable();
        }
    }

    public boolean findDevice(){
        assert(mAdapter!=null);
        if(mAdapter.isDiscovering()){
            mAdapter.cancelDiscovery();
            return false;
        }else {
            return mAdapter.startDiscovery();
        }
    }

    public void enableVisibly(Context context){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
    }

    public void connect(final BluetoothSocket btSocket){
        try {
            if (btSocket.isConnected()){
                Log.e("testn2", "connect: 已经连接");
                return;
            }
            btSocket.connect();
            if (btSocket.isConnected()){
                Log.e("testn2", "connect: 连接成功");
            }else{
                Log.e("testn2", "connect: 连接失败");
                btSocket.close();

            }
        }catch (Exception e){e.printStackTrace();}
    }

    public BluetoothDevice find_device(String addr){
        return mAdapter.getRemoteDevice(addr);
    }

    public void cancelSearch() {
        mAdapter.cancelDiscovery();
    }


}
