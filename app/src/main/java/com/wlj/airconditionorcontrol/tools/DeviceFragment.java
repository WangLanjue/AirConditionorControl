package com.wlj.airconditionorcontrol.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.wlj.airconditionorcontrol.R;
import com.wlj.airconditionorcontrol.UserActivity;

public class DeviceFragment extends Fragment {
    private View view;
    private ListView listView;
    private String[] DeviceNumber = new String[5];

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        //加载布局文件
        view = inflater.inflate(R.layout.fragment_device, container, false);

        DeviceNumber = UserActivity.device;

        init();

        return view;
    }

    private void init() {
        // 获取ListView（listView）实例
        listView = view.findViewById(R.id.devicefragmeng_listview);

        if (DeviceNumber != null) {
            // 为listView设置adapter
            listView.setAdapter(new MyAdapter());
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int i) {
            return DeviceNumber[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // 加载listView每一项的布局
            view = View.inflate(getActivity(), R.layout.device_item_layout, null);

            TextView item_textview_title = view.findViewById(R.id.item_textview_title);
            TextView item_textview_devicenumber = view.findViewById(R.id.item_textview_devicenumber);

            item_textview_title.setText("设备" + (i + 1));
            item_textview_devicenumber.setText(DeviceNumber[i].replace(",", "").replace("[", "").replace("]", ""));


            return view;
        }
    }
}
