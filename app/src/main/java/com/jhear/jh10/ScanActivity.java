package com.jhear.jh10;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.wandersnail.ble.Device;
import cn.wandersnail.ble.EasyBLE;
import cn.wandersnail.ble.callback.ScanListener;
import cn.wandersnail.commons.helper.PermissionsRequester;
import cn.wandersnail.commons.util.ToastUtils;
import cn.wandersnail.widget.listview.BaseListAdapter;
import cn.wandersnail.widget.listview.BaseViewHolder;
import cn.wandersnail.widget.listview.PullRefreshLayout;

/**
 * date: 2019/8/4 15:13
 * author: zengfansheng
 */
public class ScanActivity extends BaseActivity  implements View.OnClickListener {
    private ListAdapter listAdapter;
    private PullRefreshLayout refreshLayout;
    private LinearLayout layoutEmpty ;
    private FrameLayout  layout_ears,layout_scanlist;
    private List<Device> devList = new ArrayList<>();
    private PermissionsRequester permissionsRequester;
    private HashMap<String, Integer> map_addr_ears = new HashMap<>();
    private HashMap<String, String> map_addr_notes = new HashMap<>();
    private Button btn_connect,btn_advance,btn_togglescan ;
    private TextView btn_findme;
    private ImageView imgview_0,imgview_1;
    private PopupWindow mPopWindow;
    private boolean exists_l,exists_r;
    private int whichear = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        restoreSharedPreferences(this);
        initViews();
        EasyBLE.getInstance().addScanListener(scanListener);
        initialize();

        toggle_scanlist(false);
    }

    private void toggle_scanlist(boolean isshow) {

        if (isshow) {
            layout_scanlist.setVisibility(View.VISIBLE);
            layout_ears.setVisibility(View.INVISIBLE);
        } else {
            layout_scanlist.setVisibility(View.INVISIBLE);
            layout_ears.setVisibility(View.VISIBLE);
        }
        update_earimage();



    }
    private void update_earimage() {
        exists_l = false;
        exists_r = false;
        for (Device item : devList ) {
            if (item.getAddress().equals(Constants.ha_l_address )) {
                exists_l = true;
            }
            if (item.getAddress().equals(Constants.ha_r_address )) {
                exists_r  = true;
            }
        }
        if ( exists_l)
            imgview_0.setImageResource(R.mipmap.ear_l_green);
        else
            imgview_0.setImageResource(R.mipmap.ear_l_grey);

        if ( exists_r ) {
            imgview_1.setImageResource(R.mipmap.ear_r_green);
        } else
            imgview_1.setImageResource(R.mipmap.ear_r_grey);

    }
    private void initViews() {
        refreshLayout = findViewById(R.id.refreshLayout);
        ListView lv = findViewById(R.id.lv);
        imgview_0 = findViewById(R.id.imageview_0);
        imgview_1 = findViewById(R.id.imageview_1);

        layoutEmpty = findViewById(R.id.layoutEmpty);
        layout_ears = findViewById(R.id.layout_ears);
        layout_scanlist = findViewById(R.id.layout_scanlist);
        btn_connect = findViewById(R.id.button);
        btn_advance = findViewById(R.id.button_advance);
        btn_togglescan = findViewById(R.id.button_togglescan);
        btn_findme = findViewById(R.id.btn_findme);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.rescan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              toggle_scanlist(true);
            }
        });
        btn_togglescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              toggle_scanlist(false);
            }
        });

        btn_findme.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                toggle_scanlist(true);
            }
        });


        btn_connect.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if ((!exists_l) && (!exists_r)) {

                       ToastUtils.showLong("请先选定助听器");

                   } else {
                       saveSharedPreferences(ScanActivity.this);


                       final String[] menuItems = new String[]{"左耳", "右耳", "双耳同时验配"};
                       android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(ScanActivity.this)
                               .setTitle("请选择要验配的助听器")
                               .setSingleChoiceItems(menuItems, whichear, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       if  ((which ==0) && !exists_l) {
                                           ToastUtils.showLong("左耳助听器没有连接");
                                       } else
                                           whichear= which;
                                       if  ((which ==1) && !exists_r) {
                                           ToastUtils.showLong("右耳助听器没有连接");
                                       } else
                                           whichear = which;

                                       if  (which ==2)   {
                                           if ( !exists_l || !exists_r) {
                                               ToastUtils.showLong("只检测到一个助听器，不能验配双耳");
                                           }

                                       } else
                                           whichear = which;

                                   }

                               })
                               .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                       Intent intent = new Intent(ScanActivity.this, EqActivity.class);
                                       intent.putExtra("mode", 0);
                                       intent.putExtra("whichear", whichear);

                                       startActivity(intent);

                                   }

                               })
                               .setNegativeButton("取消", null)
                               .create();
                       alertDialog.show();
                       }

               }
           }
        );
        btn_advance.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if ((Constants.ha_l_address == null) && (Constants.ha_r_address == null) ) {

                       ToastUtils.showLong("请先选定助听器");

                   } else {
                       saveSharedPreferences(ScanActivity.this);
                       Intent intent = new Intent(ScanActivity.this, EqActivity.class);
                       intent.putExtra("mode", 1);
                       startActivity(intent);
                   }
               }
           }
        );


        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        listAdapter = new ListAdapter(this, devList);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {

            Device mDevice = devList.get(position);
            final List<String> menuItems = new ArrayList<>();

            menuItems.add("左耳助听器");
            menuItems.add("右耳助听器");
            menuItems.add("不是我的助听器");


            new AlertDialog.Builder(ScanActivity.this)
                    .setItems(menuItems.toArray(new String[0]), (dialog, which) -> {
                        dialog.dismiss();

                        switch (which) {
                            case 0:
                                map_addr_ears.remove(mDevice.getAddress());
                                if (mDevice.getAddress().equals(Constants.ha_l_address))
                                    Constants.ha_l_address = null;
                                if (mDevice.getAddress().equals(Constants.ha_r_address))
                                    Constants.ha_r_address = null;

                                if (Constants.ha_l_address !=null) {
                                    map_addr_ears.remove( Constants.ha_l_address);
                                }
                                map_addr_ears.put(mDevice.getAddress(), 0);
                                Constants.ha_l_address = mDevice.getAddress();
                                listAdapter.notifyDataSetChanged();




                                break;
                            case 1:
                                map_addr_ears.remove(mDevice.getAddress());
                                if (mDevice.getAddress().equals(Constants.ha_l_address))
                                    Constants.ha_l_address = null;
                                if (mDevice.getAddress().equals(Constants.ha_r_address))
                                    Constants.ha_r_address = null;

                                if (Constants.ha_r_address !=null) {
                                    map_addr_ears.remove( Constants.ha_r_address);
                                }
                                map_addr_ears.put(mDevice.getAddress(), 1);
                                Constants.ha_r_address = mDevice.getAddress();
                                listAdapter.notifyDataSetChanged();
                                break;
                            default:
                                map_addr_ears.remove(mDevice.getAddress());
                                if (mDevice.getAddress().equals(Constants.ha_l_address))
                                    Constants.ha_l_address = null;
                                if (mDevice.getAddress().equals(Constants.ha_r_address))
                                    Constants.ha_r_address = null;


                                listAdapter.notifyDataSetChanged();
                                break;
                        }
                        saveSharedPreferences(ScanActivity.this);
                    })
                    .show();


        });
        refreshLayout.setOnRefreshListener(() -> {
            if (EasyBLE.getInstance().isInitialized()) {
                EasyBLE.getInstance().stopScan();
                doStartScan();
            }
            refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 500);
        });



        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyBLE.getInstance().release();
        System.exit(0);
    }

    private ScanListener scanListener = new ScanListener() {
        @Override
        public void onScanStart() {
            invalidateOptionsMenu();
        }

        @Override
        public void onScanStop() {
            invalidateOptionsMenu();
        }

        @Override
        public void onScanResult(@NonNull Device device, boolean isConnectedBySys) {
            layoutEmpty.setVisibility(View.INVISIBLE);
            listAdapter.add(device);
            update_earimage();
        }

        @Override
        public void onScanError(int errorCode, @NotNull String errorMsg) {
            switch (errorCode) {
                case ScanListener.ERROR_LACK_LOCATION_PERMISSION://缺少定位权限		
                    break;
                case ScanListener.ERROR_LOCATION_SERVICE_CLOSED://位置服务未开启		
                    break;
                case ScanListener.ERROR_SCAN_FAILED://搜索失败
                    ToastUtils.showShort("搜索出错：" + errorCode);
                    break;
            }
        }
    };

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
                doStartScan();
            } else {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EasyBLE.getInstance().isInitialized()) {
            EasyBLE.getInstance().stopScan();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        MenuItem item = menu.findItem(R.id.menuProgress);
        item.setActionView(R.layout.toolbar_indeterminate_progress);
        item.setVisible(EasyBLE.getInstance().isScanning());
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

    }
    private void initialize() {
        //动态申请权限
        permissionsRequester = new PermissionsRequester(this);
        permissionsRequester.setCallback(list -> {

        });
        permissionsRequester.checkAndRequest(getNeedPermissions());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequester.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void doStartScan() {
        listAdapter.clear();

        layoutEmpty.setVisibility(View.VISIBLE);
        EasyBLE.getInstance().stopScanQuietly();
        EasyBLE.getInstance().startScan();
    }
    private SharedPreferences ourPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void saveSharedPreferences(Context context) {

        SharedPreferences preferences = ourPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();


        editor.putString("ha_l_address",Constants.ha_l_address);
        editor.putString("ha_r_address",Constants.ha_r_address);



        editor.apply();
    }
    private void restoreSharedPreferences(Context context) {

        SharedPreferences preferences = ourPreferences(context);
        Constants.ha_l_address  = preferences.getString("ha_l_address",null);
        Constants.ha_r_address  = preferences.getString("ha_r_address",null);

        if (Constants.ha_l_address !=null)
             map_addr_ears.put(Constants.ha_l_address, 0);

        if (Constants.ha_r_address !=null)
            map_addr_ears.put(Constants.ha_r_address, 1);



    }
    private class ListAdapter extends BaseListAdapter<Device> {
        private HashMap<String, Long> updateTimeMap = new HashMap<>();
        private HashMap<String, TextView> rssiViews = new HashMap<>();

        ListAdapter(@NotNull Context context, @NotNull List<Device> list) {
            super(context, list);
        }

        @NotNull
        @Override
        protected BaseViewHolder<Device> createViewHolder(int i) {
            return new BaseViewHolder<Device>() {
                TextView tvName;
                TextView tvAddr;
                TextView tvRssi;
                ImageView iv;

                @Override
                public void onBind(Device device, int i) {
                    rssiViews.put(device.getAddress(), tvRssi);
                    tvName.setText(device.getName().isEmpty() ? "N/A" : device.getName());
                    tvAddr.setText(device.getAddress());
                    tvRssi.setText("" + device.getRssi());

                    Integer earid = -1;
                    if (map_addr_ears.containsKey(device.getAddress()))
                        earid = map_addr_ears.get(device.getAddress());

                    if (earid == 0 || earid == 1) {
                        String earname = (earid == 0) ? "左" : "右";
                        iv.setVisibility(View.VISIBLE);

                        iv.setBackgroundResource((earid == 0) ? R.mipmap.ear_l_green : R.mipmap.ear_r_green);
                        tvName.setText(earname + (device.getName().isEmpty() ? "N/A" : device.getName()));
                    } else {
                        iv.setVisibility(View.INVISIBLE);
                    }
                }

                @NotNull
                @Override
                public View createView() {
                    View view = View.inflate(context, R.layout.item_scan, null);
                    tvName = view.findViewById(R.id.tvName);
                    tvAddr = view.findViewById(R.id.tvAddr);
                    tvRssi = view.findViewById(R.id.tvRssi);
                    iv = view.findViewById(R.id.ivIcon);


                    return view;
                }
            };
        }

        void clear() {
            getItems().clear();
            notifyDataSetChanged();
        }

        void add(Device device) {


            Device dev = null;
            for (Device item : getItems()) {
                if (item.equals(device)) {
                    dev = item;
                    break;
                }
            }
            if (dev == null) {
                updateTimeMap.put(device.getAddress(), System.currentTimeMillis());
                getItems().add(device);
                notifyDataSetChanged();


            } else {
                Long time = updateTimeMap.get(device.getAddress());
                if (time == null || System.currentTimeMillis() - time > 2000) {
                    updateTimeMap.put(device.getAddress(), System.currentTimeMillis());
                    if (dev.getRssi() != device.getRssi()) {
                        dev.setRssi(device.getRssi());
                        final TextView tvRssi = rssiViews.get(device.getAddress());
                        if (tvRssi != null) {
                            tvRssi.setText("" + device.getRssi());
                            tvRssi.setTextColor(Color.BLACK);
                            tvRssi.postDelayed(() -> tvRssi.setTextColor(0xFF909090), 800);
                        }
                    }
                }
            }
        }
    }
}
