package com.wandersnail.jh10;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import cn.wandersnail.commons.util.StringUtils;
import cn.wandersnail.commons.util.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BasicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private AppCompatSpinner cm_noise;
    private SeekBar  seekBar_wb ;

    public BasicFragment() {
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
    public static BasicFragment newInstance(String param1, String param2) {
        BasicFragment fragment = new BasicFragment();
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
        View rootView;
        rootView =  inflater.inflate(R.layout.fragment_basic, container, false);

        btn1 = rootView.findViewById(R.id.btn_1);
        btn2 = rootView.findViewById(R.id.btn_2);
        btn3 = rootView.findViewById(R.id.btn_3);
        btn4 = rootView.findViewById(R.id.btn_4);

        seekBar_wb = rootView.findViewById(R.id.seekBar_widebandgain);

        seekBar_wb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                byte[] arr_bytes = new byte[5];
                arr_bytes[0] = 0x00;
                arr_bytes[1] =0x01;
                arr_bytes[2] =0x01;
                arr_bytes[3] =0x05;
                arr_bytes[4] =0x01;

                ((EqActivity) getActivity()).write_hadata(arr_bytes);
            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];
                arr_bytes[0] = 0x00;
                arr_bytes[1] =0x01;
                arr_bytes[2] =0x01;
                arr_bytes[3] =0x01;
                arr_bytes[4] =0x01;

                ((EqActivity) getActivity()).write_hadata(arr_bytes);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];
                arr_bytes[0] = 0x00;
                arr_bytes[1] =0x01;
                arr_bytes[2] =0x01;
                arr_bytes[3] =0x01;
                arr_bytes[4] =0x01;

                ((EqActivity) getActivity()).write_hadata(arr_bytes);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];
                arr_bytes[0] = 0x00;
                arr_bytes[1] =0x01;
                arr_bytes[2] =0x01;
                arr_bytes[3] =0x01;
                arr_bytes[4] =0x01;

                ((EqActivity) getActivity()).write_hadata(arr_bytes);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];
                arr_bytes[0] = 0x00;
                arr_bytes[1] =0x01;
                arr_bytes[2] =0x01;
                arr_bytes[3] =0x01;
                arr_bytes[4] =0x01;

                ((EqActivity) getActivity()).write_hadata(arr_bytes);
            }
        });



        return rootView;
    }
    public void refresh_ui(byte[] arr_data) {
       // ToastUtils.showShort("Basic Fragment：" + StringUtils.toHex(arr_data, " "));
    }
}