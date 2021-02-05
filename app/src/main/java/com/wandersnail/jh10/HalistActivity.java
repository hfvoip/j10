package com.wandersnail.jh10;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.wandersnail.ble.Connection;
import cn.wandersnail.ble.ConnectionConfiguration;
import cn.wandersnail.ble.Device;
import cn.wandersnail.ble.EasyBLE;
import cn.wandersnail.ble.Request;
import cn.wandersnail.ble.RequestBuilder;
import cn.wandersnail.ble.RequestBuilderFactory;
import cn.wandersnail.ble.callback.MtuChangeCallback;
import cn.wandersnail.ble.callback.ReadCharacteristicCallback;
import cn.wandersnail.ble.callback.ScanListener;
import cn.wandersnail.commons.helper.PermissionsRequester;
import cn.wandersnail.commons.observer.Observe;
import cn.wandersnail.commons.poster.RunOn;
import cn.wandersnail.commons.poster.Tag;
import cn.wandersnail.commons.poster.ThreadMode;
import cn.wandersnail.commons.util.StringUtils;
import cn.wandersnail.commons.util.ToastUtils;
import cn.wandersnail.widget.listview.BaseListAdapter;
import cn.wandersnail.widget.listview.BaseViewHolder;
import cn.wandersnail.widget.listview.PullRefreshLayout;

/**
 * date: 2019/8/4 15:13
 * author: zengfansheng
 */
public class HalistActivity extends BaseActivity {


    private Device deviceL;
    private Device deviceR;

    public byte[] arr_hadataL;
    public byte[] arr_hadataR;



    private Connection connectionL, connectionR;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halist);
        restoreSharedPreferences(this);
        initViews();

        initialize();
    }

    private void initViews() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyBLE.getInstance().release();
        System.exit(0);
    }



    //需要进行检测的权限
    private List<String> getNeedPermissions() {
        List<String> list = new ArrayList<>();
        if (getApplicationInfo().targetSdkVersion >= 29) {//target sdk版本在29以上的需要精确定位权限才能搜索到蓝牙设备
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Main", "onResume");
        if (EasyBLE.getInstance().isInitialized()) {
            if (EasyBLE.getInstance().isBluetoothOn()) {

            } else {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        MenuItem item = menu.findItem(R.id.menuProgress);
        item.setActionView(R.layout.toolbar_indeterminate_progress);
        item.setVisible(EasyBLE.getInstance().isScanning());
        return super.onCreateOptionsMenu(menu);
    }

    private void initialize() {
        ConnectionConfiguration config = new ConnectionConfiguration();
        config.setConnectTimeoutMillis(10000);
        config.setRequestTimeoutMillis(1000);
        config.setAutoReconnect(false);
        String ha_l_address = Constants.ha_l_address;
        String ha_r_address = Constants.ha_r_address;
        if (ha_l_address != null)
            deviceL = new Device(EasyBLE.getInstance().getBluetoothAdapter().getRemoteDevice(ha_l_address));
        if (ha_r_address != null)
            deviceR = new Device(EasyBLE.getInstance().getBluetoothAdapter().getRemoteDevice(ha_l_address));

 
        if (deviceL != null) {
            //connection = EasyBLE.getInstance().connect(device, config);//观察者监听连接状态
            connectionL = EasyBLE.getInstance().connect(deviceL, config);//观察者监听连接状态
            connectionL.setBluetoothGattCallback(new BluetoothGattCallback() {
                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    Log.d("EasyBLE", "原始写入数据：" + StringUtils.toHex(characteristic.getValue()));
                }
            });
        }
        if (deviceR != null) {
            connectionR = EasyBLE.getInstance().connect(deviceR, config);//观察者监听连接状态
            connectionR.setBluetoothGattCallback(new BluetoothGattCallback() {
                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    Log.d("EasyBLE", "原始写入数据：" + StringUtils.toHex(characteristic.getValue()));
                }
            });

        }

    }
    /**
     * 使用{@link Observe}确定要接收消息，{@link RunOn}指定在主线程执行方法，设置{@link Tag}防混淆后找不到方法
     */
    @Tag("onConnectionStateChanged")
    @Observe
    @RunOn(ThreadMode.MAIN)
    @Override
    public void onConnectionStateChanged(@NonNull Device device) {
        Log.d("EasyBLE", "主线程：" + (Looper.getMainLooper() == Looper.myLooper()) + ", 连接状态：" + device.getConnectionState());
        ToastUtils.showShort(" 连接状态：" + device.getConnectionState());

        switch (device.getConnectionState()) {
            case SCANNING_FOR_RECONNECTION:

                break;
            case CONNECTING:

                break;
            case DISCONNECTED:

                break;
            case SERVICE_DISCOVERED:
                Connection connection = EasyBLE.getInstance().getConnection(device);

                //设置MTU

                RequestBuilder<MtuChangeCallback> builder = new RequestBuilderFactory().getChangeMtuBuilder(503);
                Request request = builder.setCallback(new MtuChangeCallback() {
                    @Override
                    public void onMtuChanged(@NonNull Request request, int mtu) {
                        Log.d("EasyBLE", "MTU修改成功，新值：" + mtu);
                    }

                    @Override
                    public void onRequestFailed(@NonNull Request request, int failType, @Nullable Object value) {

                    }
                }).build();
                connection.execute(request);

                readCharacteristic(connection, UUID.fromString(Constants.service_uuid_eq), UUID.fromString(Constants.char_uuid_eq));


                break;
        }




    }
    private void readCharacteristic(Connection conn, UUID service_uuid, UUID char_uuid) {
        RequestBuilder<ReadCharacteristicCallback> builder = new RequestBuilderFactory().getReadCharacteristicBuilder(service_uuid,
                char_uuid);
        builder.setTag(UUID.randomUUID().toString());
        builder.setPriority(Integer.MAX_VALUE);//设置请求优先级
        //设置了回调则观察者不会收到此次请求的结果消息
        builder.setCallback(new ReadCharacteristicCallback() {
            //注解可以指定回调线程
            @RunOn(ThreadMode.BACKGROUND)
            @Override
            public void onCharacteristicRead(@NonNull Request request, @NonNull byte[] value) {
                Log.d("EasyBLE", "主线程：" + (Looper.getMainLooper() == Looper.myLooper()) + ", 读取到特征值：" + StringUtils.toHex(value, " "));
                //   ToastUtils.showShort("读取到特征值：" + StringUtils.toHex(value, " "));

                if (conn == connectionL)
                    arr_hadataL =  value.clone();
                if (conn == connectionR)
                    arr_hadataR =  value.clone();



            }

            //不使用注解指定线程的话，使用构建器设置的默认线程
            @Override
            public void onRequestFailed(@NonNull Request request, int failType, @Nullable Object value) {

            }
        });
        if (conn != null)
            builder.build().execute(conn);


    }


    private SharedPreferences ourPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    private void restoreSharedPreferences(Context context) {

        SharedPreferences preferences = ourPreferences(context);
        Constants.ha_l_address  = preferences.getString("ha_l_address",null);
        Constants.ha_r_address  = preferences.getString("ha_r_address",null);



    }

}
