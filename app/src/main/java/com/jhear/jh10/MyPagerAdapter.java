package com.jhear.jh10;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;



public class MyPagerAdapter   extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private List<String>  titleList;

    public MyPagerAdapter(FragmentManager fm,   List<Fragment> fragmentList,List<String> titleList )  {
        super(fm);

        this.fragmentList = fragmentList;
        this.titleList = titleList;

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
    @Override
    public int getCount() {
        return fragmentList.size();
    }
    /**
     * //此方法用来显示tab上的名字
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
       List<String> list_title = new ArrayList<>();
       list_title = this.titleList;
      // list_title.add("智能设置");
       // list_title.add("高级设置");
       // list_title.add("产品手册");
     //   list_title.add("放大压缩");
     //   list_title.add("TBD");
        return list_title.get(position);

    }
}
