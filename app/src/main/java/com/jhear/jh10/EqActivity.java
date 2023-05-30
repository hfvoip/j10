package com.jhear.jh10;

import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.wandersnail.adapter.tree.Node;
import cn.wandersnail.ble.Connection;
import cn.wandersnail.ble.ConnectionConfiguration;
import cn.wandersnail.ble.Device;
import cn.wandersnail.ble.EasyBLE;
import cn.wandersnail.ble.EasyBLEBuilder;
import cn.wandersnail.ble.Request;
import cn.wandersnail.ble.RequestBuilder;
import cn.wandersnail.ble.RequestBuilderFactory;
import cn.wandersnail.ble.RequestType;
import cn.wandersnail.ble.WriteCharacteristicBuilder;
import cn.wandersnail.ble.WriteOptions;
import cn.wandersnail.ble.callback.MtuChangeCallback;
import cn.wandersnail.ble.callback.NotificationChangeCallback;
import cn.wandersnail.ble.callback.ReadCharacteristicCallback;
import cn.wandersnail.ble.callback.WriteCharacteristicCallback;
import cn.wandersnail.commons.observer.Observe;
import cn.wandersnail.commons.poster.RunOn;
import cn.wandersnail.commons.poster.Tag;
import cn.wandersnail.commons.poster.ThreadMode;
import cn.wandersnail.commons.util.StringUtils;
import cn.wandersnail.commons.util.ToastUtils;

/**
 * date: 2019/8/2 23:33
 * author: zengfansheng
 */
public class EqActivity extends BaseActivity {
    private Device deviceL;
    private Device deviceR;

    public byte[] arr_hadataL;
    public byte[] arr_hadataR;
    public byte[]  arr_hadata_displaying;
    private int mode=0;

    private FrameLayout layoutConnecting;
    private AVLoadingIndicatorView loadingIndicator;
    private ImageView ivDisconnected;
    private List<Item> itemList = new ArrayList<>();

    private Connection connectionL, connectionR;
    private NoScrollViewPager  viewpager;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList;
    private List<String> list_Title;
    private int whichear = 0;
    private int env_idx = 0;



    private void assignViews() {

        layoutConnecting = findViewById(R.id.layoutConnecting);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        ivDisconnected = findViewById(R.id.ivDisconnected);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String ha_l_address = Constants.ha_l_address;
        String ha_r_address = Constants.ha_r_address;

        arr_hadataL =  null;
        arr_hadataR = null;
        arr_hadata_displaying = null;



        setContentView(R.layout.activity_eq);
        assignViews();
        initViews();
        //连接配置，举个例随意配置两项
        ConnectionConfiguration config = new ConnectionConfiguration();
        config.setConnectTimeoutMillis(10000);
        config.setRequestTimeoutMillis(1000);
        config.setAutoReconnect(false);
        if (ha_l_address != null)
            deviceL = new Device(EasyBLE.getInstance().getBluetoothAdapter().getRemoteDevice(ha_l_address));
        if (ha_r_address != null)
            deviceR = new Device(EasyBLE.getInstance().getBluetoothAdapter().getRemoteDevice(ha_r_address));

//        connection = EasyBLE.getInstance().connect(device, config, observer);//回调监听连接状态，设置此回调不影响观察者接收连接状态消息
        if (  whichear == 0 || whichear ==2) {
            if (deviceL != null) {

                connectionL = EasyBLE.getInstance().connect(deviceL, config);//观察者监听连接状态
                connectionL.setBluetoothGattCallback(new BluetoothGattCallback() {

                });
            }
        }
        if (  whichear == 1 || whichear ==2) {
            if (deviceR != null) {
                connectionR = EasyBLE.getInstance().connect(deviceR, config);//观察者监听连接状态
                connectionR.setBluetoothGattCallback(new BluetoothGattCallback() {


                });

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放连接
        if (deviceL != null)
            EasyBLE.getInstance().releaseConnection(deviceL);
        if (deviceR != null)
            EasyBLE.getInstance().releaseConnection(deviceR);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        int is_connected_ha = 0;

        if (deviceL != null && !deviceL.isDisconnected())
            is_connected_ha ++;

        if (deviceR != null && !deviceR.isDisconnected())
            is_connected_ha ++;

        if (is_connected_ha >0) {
            menu.findItem(R.id.menuDisconnect).setVisible(true);
            menu.findItem(R.id.menuConnect).setVisible(false);
            menu.findItem(R.id.menuSave).setVisible(true);
            menu.findItem(R.id.menuCancel).setVisible(true);
            menu.findItem(R.id.menuSaveIDS).setVisible(true);
            menu.findItem(R.id.menuLoadIDS).setVisible(true);

        } else {
            menu.findItem(R.id.menuDisconnect).setVisible(false);
            menu.findItem(R.id.menuConnect).setVisible(true);
            menu.findItem(R.id.menuSave).setVisible(false);
            menu.findItem(R.id.menuCancel).setVisible(false);
            menu.findItem(R.id.menuSaveIDS).setVisible(false);
            menu.findItem(R.id.menuLoadIDS).setVisible(false);

        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDisconnect:
                if (deviceL !=null)
                    EasyBLE.getInstance().disconnectConnection(deviceL);
                if (deviceR !=null)
                    EasyBLE.getInstance().disconnectConnection(deviceR);


                break;
            case R.id.menuConnect:
                if (deviceL !=null)
                    EasyBLE.getInstance().getConnection(deviceL).reconnect();
                if (deviceR !=null)
                    EasyBLE.getInstance().getConnection(deviceR).reconnect();

                break;
            case android.R.id.home :
                onBackPressed();
                break;
            case R.id.menuSave:
                byte[] arr_bytes = new byte[3];
                arr_bytes[0] = (byte)0x20;
                arr_bytes[1] =(byte)0x55;
                arr_bytes[2]=(byte)(0x0a+env_idx);
                write_hadata(arr_bytes);

                String filename = "j10_L_env"+env_idx+".dat";
                if(whichear == 0 || whichear ==2)
                    saveTosdcard(filename,arr_hadataL);
                filename = "j10_R_env"+env_idx+".dat";
                if (whichear ==1 || whichear ==2)
                    saveTosdcard(filename,arr_hadataR);
                break;
            case R.id.menuSaveIDS:
                /*

                */
                ToastUtils.showLong("DEMO未实现，导出是为了把参数给远程验配师或其他用户查看和进一步验配");
                break;
            case R.id.menuLoadIDS:
                ToastUtils.showLong("DEMO未实现，导入是为了把验配师验配的参数导入到助听器");
                //read_fromenv(0);
                break;

        }
        return super.onOptionsItemSelected(item);
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

        switch (device.getConnectionState()) {
            case SCANNING_FOR_RECONNECTION:
                ivDisconnected.setVisibility(View.INVISIBLE);
                break;
            case CONNECTING:
                layoutConnecting.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.VISIBLE);
                ivDisconnected.setVisibility(View.INVISIBLE);
                break;
            case DISCONNECTED:
                layoutConnecting.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.INVISIBLE);
                ivDisconnected.setVisibility(View.VISIBLE);
                break;
            case SERVICE_DISCOVERED:
                Connection connection = EasyBLE.getInstance().getConnection(device);
                layoutConnecting.setVisibility(View.INVISIBLE);
                loadingIndicator.setVisibility(View.INVISIBLE);
                itemList.clear();
                int id = 0;
                List<BluetoothGattService> services = connection.getGatt().getServices();
                for (BluetoothGattService service : services) {
                    int pid = id;
                    Item item = new Item(pid, 0, 0);
                    item.isService = true;
                    item.service = service;
                    itemList.add(item);
                    id++;
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        Item i = new Item(id++, pid, 1);
                        i.service = service;
                        i.characteristic = characteristic;
                        itemList.add(i);
                    }
                }

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

                //
                //   readCharacteristic(connection, UUID.fromString(Constants.service_uuid_battery),UUID.fromString(Constants.char_uuid_battery));
             //   setNotification(connection, UUID.fromString(Constants.service_uuid_eq), UUID.fromString(Constants.char_uuid_eq));
                readCharacteristic(connection, UUID.fromString(Constants.service_uuid_eq), UUID.fromString(Constants.char_uuid_eq));
                break;
        }
        invalidateOptionsMenu();
    }


    /**
     * 使用{@link Observe}确定要接收消息，方法在{@link EasyBLEBuilder#setMethodDefaultThreadMode(ThreadMode)}指定的线程执行
     */
    @Observe
    @Override
    public void onNotificationChanged(@NonNull Request request, boolean isEnabled) {
        Log.d("EasyBLE", "主线程：" + (Looper.getMainLooper() == Looper.myLooper()) + ", 通知/Indication：" + (isEnabled ? "开启" : "关闭"));
        if (request.getType() == RequestType.SET_NOTIFICATION) {
            if (isEnabled) {
                ToastUtils.showShort("通知开启了");
            } else {
                ToastUtils.showShort("通知关闭了");
            }
        } else {
            if (isEnabled) {
                ToastUtils.showShort("Indication开启了");
            } else {
                ToastUtils.showShort("Indication关闭了");
            }
        }
    }
    @Observe
    @Override
    public void onCharacteristicChanged(@NonNull Device device, @NonNull UUID service, @NonNull UUID characteristic,
                                              @NonNull byte[] value) {

        ToastUtils.showShort("onCharacteristicChanged：" + StringUtils.toHex(value, " "));
    }


    /**
     * 如果{@link EasyBLEBuilder#setObserveAnnotationRequired(boolean)}设置为false时，无论加不加{@link Observe}注解都会收到消息。
     * 设置为true时，必须加{@link Observe}才会收到消息。
     * 默认为false，方法默认执行线程在{@link EasyBLEBuilder#setMethodDefaultThreadMode(ThreadMode)}指定
     */
    @Override
    public void onCharacteristicWrite(@NonNull Request request, @NonNull byte[] value) {
        Log.d("EasyBLE", "主线程：" + (Looper.getMainLooper() == Looper.myLooper()) + ", 成功写入：" + StringUtils.toHex(value, " "));
        ToastUtils.showShort("成功写入到助听器" );
        Device device = request.getDevice();
        UUID service_uuid = request.getService();
        UUID char_uuid  = request.getCharacteristic();
        Connection connection = EasyBLE.getInstance().getConnection(device);
        readCharacteristic(connection,service_uuid,char_uuid);


    }

    private void initViews() {
        tabLayout = findViewById(R.id.tablayout);
        viewpager = findViewById(R.id.viewpager);


        Bundle bundle=getIntent().getExtras();
        mode = 0;
        if (bundle !=null ) {
            mode = bundle.getInt("mode");
            whichear = bundle.getInt("whichear");
        }
        fragmentList = new ArrayList<>();
        list_Title = new ArrayList<>();

        if (mode == 1) {

            fragmentList.add(new MemoryFragment());
            list_Title.add(getString(R.string.tab_memory));

            fragmentList.add(new EqFragment());
            fragmentList.add(new WdrcFragment());
            list_Title.add(getString(R.string.tab_eq));
            list_Title.add(getString(R.string.tab_wdrc));
          //  fragmentList.add(new HelpFragment());
           // list_Title.add(getString(R.string.tab_help));
        } else {
            fragmentList.add(new MemoryFragment());
            list_Title.add(getString(R.string.tab_memory));
            fragmentList.add(new BasicFragment());
            list_Title.add(getString(R.string.tab_basic));
            fragmentList.add(new EqFragment());
            list_Title.add(getString(R.string.tab_eq));

          //  fragmentList.add(new HelpFragment());
         //   list_Title.add(getString(R.string.tab_help));
        }


        viewpager.setAdapter(
                new MyPagerAdapter(getSupportFragmentManager(),fragmentList, list_Title
                       ));


        tabLayout.setupWithViewPager(viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int idx = position;
                if (mode ==0) {
                    if (idx ==0)  {
                        ( (MemoryFragment)fragmentList.get(idx)).onResume();
                    }
                    if (idx ==1)  {
                        ( (BasicFragment)fragmentList.get(idx)).onResume();
                    }
                    if (idx ==2)
                        ((EqFragment)fragmentList.get(idx)).onResume();
                }
                if (mode ==1) {
                    if (idx ==0)  {
                        ( (MemoryFragment)fragmentList.get(idx)).onResume();
                    }
                    if (idx==1)
                        ((BasicFragment)fragmentList.get(idx)).onResume();
                    //本来有个wdrc fragment ,我们不在app中做了
                    if (idx ==2)
                        ((EqFragment)fragmentList.get(idx)).onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




        Button btn_left = findViewById(R.id.button_ear0);
        Button btn_right= findViewById(R.id.button_ear1);


        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("left clicked "  );
            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("right clicked "  );
            }
        });



    }
    public void refresh_ui(byte[] arr_data) {
        arr_hadata_displaying = arr_data;

        int idx = viewpager.getCurrentItem();
        if (mode ==0) {
            if (idx ==0)  {
                ( (MemoryFragment)fragmentList.get(idx)).refresh_ui(arr_data);
            }
            if (idx ==1)  {
                ( (BasicFragment)fragmentList.get(idx)).refresh_ui(arr_data);
            }
        }
        if (mode ==1) {
            if (idx==1)
                ((EqFragment)fragmentList.get(idx)).refresh_ui(arr_data);
            if (idx ==2)
                ((WdrcFragment)fragmentList.get(idx)).refresh_ui(arr_data);
        }


    }

    private void writeCharacteristic(Connection conn, UUID service_uuid, UUID char_uuid,byte[] bytesdata) {
        Log.d("EasyBLE", "开始写入");

        WriteCharacteristicBuilder builder = new RequestBuilderFactory().getWriteCharacteristicBuilder(service_uuid,
                char_uuid, bytesdata);
        //根据需要设置写入配置
        if (conn != null) {
            int writeType = conn.hasProperty(service_uuid, char_uuid,
                    BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) ?
                    BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE : BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
            builder.setWriteOptions(new WriteOptions.Builder()
                    .setPackageSize(conn.getMtu() - 3)
                    .setPackageWriteDelayMillis(5)
                    .setRequestWriteDelayMillis(10)
                    .setWaitWriteResult(true)
                    .setWriteType(writeType)
                    .build());
            //不设置回调，使用观察者模式接收结果
             builder.build().execute(conn);
            //我们还是用回调
            builder.setCallback(new WriteCharacteristicCallback() {
                //注解可以指定回调线程
                @RunOn(ThreadMode.BACKGROUND)
                @Override
                public void onCharacteristicWrite(@NonNull Request request, @NonNull byte[] value) {
                    Log.d("EasyBLE", "主线程：" + (Looper.getMainLooper() == Looper.myLooper()) + ", 成功写入：" + StringUtils.toHex(value, " "));
                    ToastUtils.showShort("成功写入：" + StringUtils.toHex(value, " ")+",再次执行读操作");
                    readCharacteristic(conn,service_uuid,char_uuid);
                }

                //不使用注解指定线程的话，使用构建器设置的默认线程
                @Override
                public void onRequestFailed(@NonNull Request request, int failType, @Nullable Object value) {

                }
            });

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

                refresh_ui(value);

            }

            //不使用注解指定线程的话，使用构建器设置的默认线程
            @Override
            public void onRequestFailed(@NonNull Request request, int failType, @Nullable Object value) {

            }
        });
        if (conn != null)
            builder.build().execute(conn);


    }

    private void setNotification(Connection conn, UUID service_uuid, UUID char_uuid) {
        if (conn != null) {
            Connection tmp_conn = conn;
            boolean isEnabled = tmp_conn.isNotificationOrIndicationEnabled(service_uuid, char_uuid);
            RequestBuilder<NotificationChangeCallback> builder = new RequestBuilderFactory().getSetNotificationBuilder(
                    service_uuid, char_uuid, !isEnabled);
            //不设置回调，使用观察者模式接收结果
            builder.build().execute(tmp_conn);
        }


    }

    public void write_hadata(byte[] data) {


          if (connectionL != null) {
              if ((whichear ==0 || whichear ==2))
              writeCharacteristic(connectionL, UUID.fromString(Constants.service_uuid_eq), UUID.fromString(Constants.char_uuid_eq), data);
          }
            if (connectionR != null) {
                if ((whichear ==1 || whichear ==2))
                writeCharacteristic(connectionR, UUID.fromString(Constants.service_uuid_eq), UUID.fromString(Constants.char_uuid_eq), data);
            }
    }
    public void saveTosdcard(String filename, byte[] arr_data) {
        //文件输出流
        FileOutputStream fos = null;


        //设置文件路径 ，第一个参数是文件保存的路径，null放在根目录下，第二个参数是文件名
        File file = new File(getExternalFilesDir(null), filename);

        try {
            fos = new FileOutputStream(file);
            fos.write( arr_data);
            ToastUtils.showLong("保存到SD卡成功,你可以把这个文件分享给验配师，让验配师帮你远程验配");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    // 关闭输入流
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    public void read_fromenv(int env_idx) {

        set_current_env(env_idx);

        return;
        /*
        String filename = "j10_L_env"+env_idx+".dat";
        if(whichear == 0 || whichear ==2)
            readFromsdcard(filename,arr_hadataL);
        filename = "j10_R_env"+env_idx+".dat";
        if (whichear ==1 || whichear ==2)
            readFromsdcard(filename,arr_hadataR);

         */
    }
    public void readFromsdcard(String filename, byte[] arr_data) {
        //文件输出流
        FileInputStream fos = null;


        //设置文件路径 ，第一个参数是文件保存的路径，null放在根目录下，第二个参数是文件名
        File file = new File(getExternalFilesDir(null), filename);

        try {
            fos = new FileInputStream(file);
            fos.read( arr_data);


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    // 关闭输入流
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void set_current_env(int val) {
        env_idx = val;
    }
    public int  get_current_env( ) {
        return env_idx ;
    }




    private class Item extends Node<Item> {
        boolean isService;
        BluetoothGattService service;
        BluetoothGattCharacteristic characteristic;
        boolean hasNotifyProperty;
        boolean hasWriteProperty;
        boolean hasReadProperty;

        Item(int id, int pId, int level) {
            super(id, pId, level);
        }
    }
}
