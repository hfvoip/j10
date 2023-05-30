package com.jhear.jh10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import cn.wandersnail.commons.util.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoryFragment extends Fragment  implements View.OnClickListener{

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

    public MemoryFragment() {
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
    public static MemoryFragment newInstance(String param1, String param2) {
        MemoryFragment fragment = new MemoryFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_memory, container, false);

        btn1 = rootView.findViewById(R.id.btn_memory1);
        btn2 = rootView.findViewById(R.id.btn_memory2);
        btn3 = rootView.findViewById(R.id.btn_memory3);
        btn4 = rootView.findViewById(R.id.btn_memory4);


        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);

        btn1.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        btn1.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        return rootView;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        int env_idx = 0;
        btn1.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        btn2.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        btn3.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        btn4.setBackgroundColor(getResources().getColor(R.color.light_overlay));

        v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        int  old_env_idx =  ((EqActivity) getActivity()).get_current_env();


        switch (id) {
            case R.id.btn_memory1:
                env_idx = 0;
                break;
            case R.id.btn_memory2:
                env_idx = 1;
                break;
            case R.id.btn_memory3:
                env_idx = 2;
                break;
            case R.id.btn_memory4:
                env_idx = 3;
                break;
        }


        //这里用短的指令，0xaa 00 00 <mem_idx>
        byte[] arr_bytes = new byte[4];
        arr_bytes[0] = (byte)0xAA;
        arr_bytes[1] = 0;
        arr_bytes[2]   =0;
        arr_bytes[3] = (byte)(env_idx );

        ((EqActivity) getActivity()).write_hadata(arr_bytes);
        //这句是什么，我们write 完以后，最好重新read 一次
        //2023-5-30 经过测试，已经有read 操作了（在debug 输出里可以看到)
        //((EqActivity) getActivity()).read_fromenv(env_idx);



    }
    public void refresh_ui(byte[] arr_data) {
        byte ck1=0;
        int env_idx  = 0;
        ck1 =arr_data[67];
        int mem_idx =(int)(ck1 - 0x10);
        if ((mem_idx >=0) && (mem_idx <4)) {
            env_idx = mem_idx ;
        }
        btn1.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        btn2.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        btn3.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        btn4.setBackgroundColor(getResources().getColor(R.color.light_overlay));
        if (env_idx == 0)
             btn1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        if (env_idx == 1)
            btn1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        if (env_idx == 2)
            btn1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        if (env_idx == 3)
            btn1.setBackgroundColor(getResources().getColor(R.color.colorAccent));


    }
}