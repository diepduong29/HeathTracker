package com.dt2d.heathtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.classes.Water;

import java.util.List;

public class WaterListView extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Water> waterList;

    public WaterListView(Context context, int layout, List<Water> waterList) {
        this.context = context;
        this.layout = layout;
        this.waterList = waterList;
    }

    @Override
    public int getCount() {
        return waterList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        // ánh xạ view
        TextView txtWater = view.findViewById(R.id.textViewWater);
        TextView txtDate = view.findViewById(R.id.textViewDate);

        //gán giá trị
        Water water = waterList.get(i);
        txtWater.setText(water.getLuongnuoc() + " ml");
        txtDate.setText((water.getNgaytao()));

        return view;
    }
}
