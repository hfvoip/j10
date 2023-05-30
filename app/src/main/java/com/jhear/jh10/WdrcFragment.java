package com.jhear.jh10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import cn.wandersnail.commons.util.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WdrcFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WdrcFragment extends Fragment implements OnRangeSeekBarListener   {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RangeSeekBar rangeSeekBar1,rangeSeekBar2,rangeSeekBar3,rangeSeekBar4;
    private SeekBar sb_amp1,sb_cr1,sb_amp2,sb_cr2,sb_amp3,sb_cr3,sb_amp4,sb_cr4;

    private SeekBar sb_ck1_lth,sb_ck1_uth,sb_ck1_limit;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WdrcFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EqFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WdrcFragment newInstance(String param1, String param2) {
        WdrcFragment fragment = new WdrcFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_wdrc, container, false);

        sb_amp1 = rootView.findViewById(R.id.seekBar_cr1_amp);
        sb_cr1 = rootView.findViewById(R.id.seekBar_cr1);
        sb_amp2 = rootView.findViewById(R.id.seekBar_cr2_amp);
        sb_cr2 = rootView.findViewById(R.id.seekBar_cr2);
        sb_amp3 = rootView.findViewById(R.id.seekBar_cr3_amp);
        sb_cr3 = rootView.findViewById(R.id.seekBar_cr3);
        sb_amp4 = rootView.findViewById(R.id.seekBar_cr4_amp);
        sb_cr4 = rootView.findViewById(R.id.seekBar_cr4);

        sb_ck1_lth =  rootView.findViewById(R.id.seekBar_ck1_lth);
        sb_ck1_uth =  rootView.findViewById(R.id.seekBar_ck1_uth);
        sb_ck1_limit =  rootView.findViewById(R.id.seekBar_ck1_limit);


        sb_amp1.setMax(50);
        sb_cr1.setMax(50);
        sb_amp2.setMax(50);
        sb_cr2.setMax(50);
        sb_amp3.setMax(50);
        sb_cr3.setMax(50);
        sb_amp4.setMax(50);
        sb_cr4.setMax(50);

        SeekBar.OnSeekBarChangeListener seekbarlistener = (new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int prog = seekBar.getProgress();
                byte  val = rate_2_byte(prog);
                int nId = seekBar.getId();
                switch (nId) { // this only in case you have multiple Range Seek Bars
                    case R.id.seekBar_cr1_amp:
                        wdrc_writeha((byte) 0x18,  (byte)val);
                        break;
                    case R.id.seekBar_cr1:
                        wdrc_writeha((byte) 0x19,  (byte)val);
                        break;
                    case R.id.seekBar_cr2_amp:
                        wdrc_writeha((byte) 0x1A,  (byte)val);
                        break;
                    case R.id.seekBar_cr2:
                        wdrc_writeha((byte) 0x1B,  (byte)val);
                        break;
                    case R.id.seekBar_cr3_amp:
                        wdrc_writeha((byte) 0x1c,  (byte)val);
                        break;
                    case R.id.seekBar_cr3:
                        wdrc_writeha((byte) 0x1d,  (byte)val);
                        break;
                    case R.id.seekBar_cr4_amp:
                        wdrc_writeha((byte) 0x1e,  (byte)val);
                        break;
                    case R.id.seekBar_cr4:
                        wdrc_writeha((byte) 0x1f,  (byte)val);
                        break;


                };
            }
        });

        SeekBar.OnSeekBarChangeListener ckseekbarlistener = (new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int prog = seekBar.getProgress();

                int nId = seekBar.getId();
                switch (nId) { // this only in case you have multiple Range Seek Bars
                    case R.id.seekBar_ck1_lth:
                        wdrc_writeha((byte) 0x0c,  (byte)prog);
                        break;
                    case R.id.seekBar_ck1_uth:
                        wdrc_writeha((byte) 0x0d,  (byte)prog);
                        break;
                    case R.id.seekBar_ck1_limit:
                        wdrc_writeha((byte) 0x0e,  (byte)prog);
                        break;


                };
            }
        });


        sb_amp1.setOnSeekBarChangeListener(seekbarlistener);
        sb_cr1.setOnSeekBarChangeListener(seekbarlistener);

        sb_amp2.setOnSeekBarChangeListener(seekbarlistener);
        sb_cr2.setOnSeekBarChangeListener(seekbarlistener);
        sb_amp3.setOnSeekBarChangeListener(seekbarlistener);
        sb_cr3.setOnSeekBarChangeListener(seekbarlistener);
        sb_amp4.setOnSeekBarChangeListener(seekbarlistener);
        sb_cr4.setOnSeekBarChangeListener(seekbarlistener);

        sb_ck1_lth.setOnSeekBarChangeListener(ckseekbarlistener);
        sb_ck1_uth.setOnSeekBarChangeListener(ckseekbarlistener);
        sb_ck1_limit.setOnSeekBarChangeListener(ckseekbarlistener);

        rangeSeekBar1 = rootView.findViewById(R.id.rangeSeekBar1);
        rangeSeekBar1.setMax(65);
        rangeSeekBar1.setMinDifference(4); // default is 20
        rangeSeekBar1.setPosProgress(0,15);
        rangeSeekBar1.setPosProgress(1,25);
        rangeSeekBar1.setPosProgress(2,49);
        rangeSeekBar1.setOnRangeSeekBarListener(this);

        rangeSeekBar2 = rootView.findViewById(R.id.rangeSeekBar2);
        rangeSeekBar2.setMax(65);
        rangeSeekBar2.setMinDifference(4); // default is 20
        rangeSeekBar2.setPosProgress(0,15);
        rangeSeekBar2.setPosProgress(1,25);
        rangeSeekBar2.setPosProgress(2,49);
        rangeSeekBar2.setOnRangeSeekBarListener(this);

        rangeSeekBar3 = rootView.findViewById(R.id.rangeSeekBar3);
        rangeSeekBar3.setMax(65);
        rangeSeekBar3.setMinDifference(4); // default is 20
        rangeSeekBar3.setPosProgress(0,15);
        rangeSeekBar3.setPosProgress(1,25);
        rangeSeekBar3.setPosProgress(2,49);
        rangeSeekBar3.setOnRangeSeekBarListener(this);

        rangeSeekBar4 = rootView.findViewById(R.id.rangeSeekBar4);
        rangeSeekBar4.setMax(65);
        rangeSeekBar4.setMinDifference(4); // default is 20
        rangeSeekBar4.setPosProgress(0,15);
        rangeSeekBar4.setPosProgress(1,25);
        rangeSeekBar4.setPosProgress(2,49);
        rangeSeekBar4.setOnRangeSeekBarListener(this);

        return  rootView;
    }
    @Override
    public void onResume() {
        super.onResume();

        byte[] arr_bytes = ((EqActivity) getActivity()).arr_hadata_displaying;
        if (arr_bytes != null)
            refresh_ui(arr_bytes);
    }


    @Override
    public void onRangeValues(RangeSeekBar rangeSeekBar,int pos, int prog) {
        int offset = 0;
        offset = pos -1;  //1=>0,2:1,3:2

        switch (rangeSeekBar.getId()){ // this only in case you have multiple Range Seek Bars
             case R.id.rangeSeekBar1:
                 wdrc_writeha((byte) (0x0c+offset),  (byte)prog);
                  break;
            case R.id.rangeSeekBar2:
                wdrc_writeha((byte) ( 0x0f+offset),  (byte)prog);
                break;
            case R.id.rangeSeekBar3:
                wdrc_writeha((byte) (0x12+offset),  (byte)prog);
                break;
            case R.id.rangeSeekBar4:
                wdrc_writeha((byte) ( 0x15+offset),  (byte)prog);
                break;
         }

    }
    private void wdrc_writeha(byte cmd,byte val) {
        byte[] arr_bytes = new byte[3];
        arr_bytes[0] = cmd;
        arr_bytes[1] =0x55;
        arr_bytes[2] = val;
        ((EqActivity) getActivity()).write_hadata(arr_bytes);
    }
    public void refresh_ui(byte[] arr_data) {
        //23 ，24 是开始放大拐点，23通常会是0
        byte ck1,ck2,ck3;
        ck1 =arr_data[24];
        ck2 = arr_data[26];
        ck3 = arr_data[28];
        rangeSeekBar1.setPosProgress(0,ck1);
        rangeSeekBar1.setPosProgress(1,ck2);
        rangeSeekBar1.setPosProgress(2,ck3);

        sb_ck1_lth.setProgress(ck1);
        sb_ck1_uth.setProgress(ck2);
        sb_ck1_limit.setProgress(ck3);


        ck1 =arr_data[30];
        ck2 = arr_data[32];
        ck3 = arr_data[34];
        rangeSeekBar2.setPosProgress(0,ck1);
        rangeSeekBar2.setPosProgress(1,ck2);
        rangeSeekBar2.setPosProgress(2,ck3);

        ck1 =arr_data[36];
        ck2 = arr_data[38];
        ck3 = arr_data[40];
        rangeSeekBar3.setPosProgress(0,ck1);
        rangeSeekBar3.setPosProgress(1,ck2);
        rangeSeekBar3.setPosProgress(2,ck3);

        ck1 =arr_data[42];
        ck2 = arr_data[44];
        ck3 = arr_data[46];
        rangeSeekBar4.setPosProgress(0,ck1);
        rangeSeekBar4.setPosProgress(1,ck2);
        rangeSeekBar4.setPosProgress(2,ck3);

        int amp1 = byte_2_rate(arr_data[48]);
        int cr1 = byte_2_rate(arr_data[51]);
        sb_amp1.setProgress(amp1);
        sb_cr1.setProgress(cr1);
        int amp2 = byte_2_rate(arr_data[53]);
        int cr2 = byte_2_rate(arr_data[55]);
        sb_amp2.setProgress(amp2);
        sb_cr2.setProgress(cr2);
        int amp3 = byte_2_rate(arr_data[57]);
        int cr3 = byte_2_rate(arr_data[59]);
        sb_amp3.setProgress(amp3);
        sb_cr3.setProgress(cr3);
        int amp4 = byte_2_rate(arr_data[61]);
        int cr4 = byte_2_rate(arr_data[63]);
        sb_amp4.setProgress(amp4);
        sb_cr4.setProgress(cr4);



    }
    public int byte_2_rate(byte val) {
        //头4位, >>> 为无符号右移
      byte highmask = (byte) 0xf0;
      byte lowmask = (byte) 0x0f;

      byte byte1 =  (byte) (( val & highmask) >>>4);
      byte  byte2 =(byte)( val & lowmask);
      //byte2 为各位，<10
      if (byte2 >10) byte2 = 1;
      if (byte1 >10) byte1 = 1;

      return byte1*10+byte2;
    }
    public byte rate_2_byte(int rate) {
        byte byte1 = (byte)(rate /10);
        byte byte2 = (byte)(rate %10);
        byte newbyte = (byte)(byte1 * 16 +byte2);
        return newbyte;



    }

}