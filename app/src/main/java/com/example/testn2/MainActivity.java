package com.example.testn2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_PERMISSION_CODE = 1;
    public BTControl BTController = new BTControl();
    private Toast mToast;
    private IntentFilter foundFilter;
    public ArrayAdapter adapter1;

    public ArrayList<String> requestList = new ArrayList<>();//权限
    public ArrayList<String> arrayList = new ArrayList<>();//设备
    public ArrayList<String> deviceName = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state){
                case BluetoothAdapter.STATE_OFF:
                    showToast("STATE_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    showToast("STATE_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    showToast("STATE_TURNING_OFF");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    showToast("STATE_TURNING_ON");
                    break;
                default:
                    showToast("UnKnow STATE");
                    unregisterReceiver(this);
                    break;
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.button1);//搜索按钮
        Button button2 = (Button) findViewById(R.id.button2);//
        Switch sw1 = (Switch) findViewById(R.id.switch1);//显示列表

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(receiver, filter);
        //搜索蓝牙的广播
        foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 获取ListView组件
        ListView listView = (ListView) findViewById(R.id.lv1);
        // 实例化ArrayAdapter对象
        adapter1 = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, deviceName);
        // 添加到ListView组件中
        listView.setAdapter(adapter1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CharSequence content = ((TextView) view).getText();
                String con = content.toString();
                String[] conArray = con.split("\n");
                String rightStr = conArray[1].substring(5, conArray[1].length());
                BluetoothDevice device = BTController.find_device(rightStr);
                if (device.getBondState() == 10) {
                    BTController.cancelSearch();
                    String s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：未配对"  + "\n";
                    deviceName.remove(s);
                    device.createBond();
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：已配对"  + "\n";
                    deviceName.add(s);
                    adapter1.notifyDataSetChanged();
                    showToast("配对：" + device.getName());
                }
                else{
                    BTController.cancelSearch();
                    String s2 = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：已配对" + "\n";
                    if(deviceName.contains(s2)) {
                        Intent intent = new Intent(MainActivity.this,ModeChoose.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("deviceaddr",device.getAddress());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
            });


        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BTError();
                if(sw1.isChecked()){
                    BTON();
                    showToast("蓝牙已开启");}
                else{
                    BTController.BTSwitchOff();
                    showToast("蓝牙已关闭");
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
                BTVisible();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
                // 注册广播
                registerReceiver(bluetoothReceiver, foundFilter);
                // 初始化各列表
                arrayList.clear();
                deviceName.clear();
                adapter1.notifyDataSetChanged();
                // 开始搜索
                BTController.findDevice();
            }
        });

    }

    public void BTON(){
        getPermission();
        BTController.BTSwitchOn(this,1);
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void BTVisible(){
        // 获取蓝牙权限
        getPermission();
        // 打开蓝牙可见
        BTController.enableVisibly(this);
    }
    //获取权限
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
            ActivityCompat.requestPermissions(this, requestList.toArray(new String[0]), REQ_PERMISSION_CODE);
        }

    }
    //不支持蓝牙弹窗
    public void BTError(){
        getPermission();
        if(!BTController.isSupportBlueTooth()){
            showToast("设备不支持蓝牙");
        }

    }
    @SuppressLint("MissingPermission")
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                String s;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == 12) {
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：已配对" + "\n";
                }
                else if (device.getBondState() == 10){
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：未配对" +"\n";
                }else{
                    s = "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "连接状态：未知" + "\n";
                }
                if (!deviceName.contains(s)) {
                    deviceName.add(s);//将搜索到的蓝牙名称和地址添加到列表。
                    arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
                    adapter1.notifyDataSetChanged();//更新
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                showToast("搜索结束");
                unregisterReceiver(this);
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                showToast("开始搜索");
            }
        }
    };
    public void showToast(String text){
        if( mToast == null){
            //初始化
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else{
            mToast.setText(text);
        }
        mToast.show();
    }


}