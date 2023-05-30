package com.jhear.jh10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

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
    private RadioButton  noise_btn0;
    private RadioButton  noise_btn1;
    private RadioButton  noise_btn2;
    private RadioGroup  noise_rg;
    private byte noiselevel = -1;

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
    public void onResume() {
        super.onResume();

        byte[] arr_bytes = ((EqActivity) getActivity()).arr_hadata_displaying;
        if (arr_bytes != null)
            refresh_ui(arr_bytes);
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

        noise_btn0 = rootView.findViewById(R.id.radio_noise0);
        noise_btn1 = rootView.findViewById(R.id.radio_noise1);
        noise_btn2 = rootView.findViewById(R.id.radio_noise2);
        noise_rg = rootView.findViewById(R.id.radiogroup_noise);



        seekBar_wb = rootView.findViewById(R.id.seekBar_widebandgain);
        seekBar_wb.setMax(40);
        seekBar_wb.setMin(0);

        seekBar_wb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                byte[] arr_back_bytes = ((EqActivity) getActivity()).arr_hadata_displaying;

                int  val =seekBar.getProgress();
                 //如何更改tkgain ,我们显示用tkgain[1],但是修改的时候，同时修改tkgain[0..7],因此首先算出delta
                int delta_val = val - arr_back_bytes[37];
                boolean passed_val = true;
                for (int band_idx =0;band_idx <8;band_idx++) {
                    int tmp_newval = arr_back_bytes[36+band_idx] + delta_val;
                    if ((tmp_newval >=0) && (tmp_newval <=40)) {
                        passed_val = true;
                    } else {
                        passed_val = false;
                        break;
                    }
                }
                if (passed_val ==false) {
                    refresh_ui(arr_back_bytes);
                    return;
                }


                //组织一个ble packet ,我们简化点，用长包，也就是100个字节 ,0x24 =36,wdrc.tkgain[0]
                //如果是短包，应该是 0xAA 0X24 8 tkgain0 tkgain1 tkgain2 tkgain3 tkgain4 tkgain5 tkgain6 tkgain7

                for (int band_idx =0;band_idx <8;band_idx++) {
                    int tmp_newval = arr_back_bytes[36+band_idx] + delta_val;
                    arr_back_bytes[36+band_idx] = (byte)tmp_newval;
                }


                ((EqActivity) getActivity()).write_hadata(arr_back_bytes);
            }
        });

        noise_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int val_noise = -1;
                if (noiselevel ==-1) return ;

                switch (i) {
                    case R.id.radio_noise0:
                        val_noise = 0;
                        break;
                    case R.id.radio_noise1:
                        val_noise = 1;
                        break;
                    case R.id.radio_noise2:
                        val_noise = 2;
                        break;
                    default:
                        break;
                }
                if (val_noise >=0 && val_noise != noiselevel) {
                     //现在没有修改Noise level 的功能
                }



            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];


            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];

            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] arr_bytes = new byte[5];

            }
        });



        return rootView;
    }
    public void refresh_ui(byte[] arr_data) {
       // ToastUtils.showShort("Basic Fragment：" + StringUtils.toHex(arr_data, " "));
        //用tkgain[1] 来作为 volume的显示
        // volume :0-40
        byte volume  =arr_data[37];

        if (volume >=0 && volume <=40)
            seekBar_wb.setProgress(volume);
        //1和2 是降噪
      //  noiselevel = arr_data[2];
      //  noise_btn0.setChecked(noiselevel ==0);
       // noise_btn1.setChecked(noiselevel ==1);
        //noise_btn2.setChecked(noiselevel ==2);


    }
}