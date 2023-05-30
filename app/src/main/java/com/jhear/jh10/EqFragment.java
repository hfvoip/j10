package com.jhear.jh10;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EqFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EqFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SeekBar  seekBar_eq250,seekBar_eq300,seekBar_eq500,seekBar_eq800,seekBar_eq1000,seekBar_eq2000,seekBar_eq4000,seekBar_eq8000;
    private SeekBar.OnSeekBarChangeListener  seekBarChangeListener;
    public EqFragment() {
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
    public static EqFragment newInstance(String param1, String param2) {
        EqFragment fragment = new EqFragment();
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
    public void onResume() {
        super.onResume();

        byte[] arr_bytes = ((EqActivity) getActivity()).arr_hadataL;
        if (arr_bytes != null)
            refresh_ui(arr_bytes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_eq, container, false);

        seekBar_eq250 = rootView.findViewById(R.id.seekBar_eq250);
        seekBar_eq300 = rootView.findViewById(R.id.seekBar_eq300);
        seekBar_eq800 = rootView.findViewById(R.id.seekBar_eq800);

        seekBar_eq500 = rootView.findViewById(R.id.seekBar_eq500);
        seekBar_eq1000 = rootView.findViewById(R.id.seekBar_eq1000);
        seekBar_eq2000 = rootView.findViewById(R.id.seekBar_eq2000);
        seekBar_eq4000 = rootView.findViewById(R.id.seekBar_eq3000);
        seekBar_eq8000 = rootView.findViewById(R.id.seekBar_eq5000);


        seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            //    ToastUtils.showShort("  tracking  "  );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
             //   ToastUtils.showShort("stop tracking  "  );
                int eqid = get_eqid_seekbar(seekBar);
                if (eqid <0)  return ;
                //用短包,长包就是发送100个字节，也是可以的
                if (true)  {
                    byte[] arr_bytes = new byte[4];
                    arr_bytes[0] =(byte)0xAA;
                    arr_bytes[1] = (byte)(80+eqid);
                    arr_bytes[2] = 0x1;   //1 表示一个字节长度
                    arr_bytes[3] = (byte)seekBar.getProgress();

                    ((EqActivity) getActivity()).write_hadata(arr_bytes);

                } else {
                    //长包
                    //100 个字节，替换eq对应的字节
                }

 }
        };


        seekBar_eq250.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_eq300.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_eq500.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_eq800.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_eq1000.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_eq2000.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_eq4000.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_eq8000.setOnSeekBarChangeListener(seekBarChangeListener);


        return rootView;



    }

    private int get_eqid_seekbar(SeekBar  sbar) {
        int id = sbar.getId();
        if (id == R.id.seekBar_eq250)  return 0;

        if (id == R.id.seekBar_eq300)  return 1;
        if (id == R.id.seekBar_eq500)  return 2;

        if (id == R.id.seekBar_eq800)  return 3;
        if (id == R.id.seekBar_eq1000)  return 4;
        if (id == R.id.seekBar_eq2000)  return 5;
        if (id == R.id.seekBar_eq3000)  return 6;
        if (id == R.id.seekBar_eq5000)  return 7;
        return -1;


    }

    public void refresh_ui(byte[] arr_data) {
       // ToastUtils.showShort("Eq Fragment：" + StringUtils.toHex(arr_data, " "));
        if (arr_data.length <100)  return ;
        int eq250 = arr_data[80];
        int eq300 = arr_data[81];
        int  eq500 =arr_data[82];
        int eq800 = arr_data[83];
        int eq1000 = arr_data[84];
        int eq2000 = arr_data[85];
        int eq3000 = arr_data[86];
        int eq5000 = arr_data[87];

        seekBar_eq250.setMax(40);

        seekBar_eq300.setMax(40);
        seekBar_eq500.setMax(40);

        seekBar_eq800.setMax(40);
        seekBar_eq1000.setMax(40);
        seekBar_eq2000.setMax(40);
        seekBar_eq4000.setMax(40);
        seekBar_eq8000.setMax(40);

        seekBar_eq250.setProgress(eq250);

        seekBar_eq300.setProgress(eq300);

        seekBar_eq500.setProgress(eq500);

        seekBar_eq800.setProgress(eq800);

        seekBar_eq1000.setProgress(eq1000);
        seekBar_eq2000.setProgress(eq2000);
        seekBar_eq4000.setProgress(eq3000);
        seekBar_eq8000.setProgress(eq5000);




    }

}