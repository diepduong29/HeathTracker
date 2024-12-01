package com.dt2d.heathtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt2d.heathtracker.classes.BMI;
import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.bmi.BMIHistoryActivity;

import java.util.List;

public class BmiListView extends BaseAdapter {
    private BMIHistoryActivity context;
    private int layout;
    private List<BMI> bmiList;

    public BmiListView(BMIHistoryActivity context, int layout, List<BMI> bmiList) {
        this.context = context;
        this.layout = layout;
        this.bmiList = bmiList;
    }

    @Override
    public int getCount() {
        return bmiList.size();
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
        TextView txtString = view.findViewById(R.id.rowtextView);
        TextView txtBmi = view.findViewById(R.id.rowtextViewBmi);
        TextView txtWeight = view.findViewById(R.id.rowtextViewWeight);
        TextView txtHeight = view.findViewById(R.id.rowtextViewHeight);
        TextView txtDate = view.findViewById(R.id.rowtextViewDate);
        ImageView imgDel = view.findViewById(R.id.imageViewDel);
        ImageView imgUp = view.findViewById(R.id.imageViewUp);

        //gán giá trị
        BMI bmi = bmiList.get(i);
        float wh = bmi.getCannang() / (bmi.getChieucao() * bmi.getChieucao());

        txtString.setText(chisobmi(wh));
        txtBmi.setText(bmi.getBmi() + " bmi");
        txtWeight.setText(bmi.getCannang() + " kg");
        txtHeight.setText(bmi.getChieucao() + " m");
        txtDate.setText((bmi.getNgaytao()));

        // sự kiện
        imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.DiaLogDelete(bmi.getId());
            }
        });

        imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.DiaLogEdit(bmi.getCannang(), bmi.getChieucao(), bmi.getId());
            }
        });
        return view;
    }

    private String chisobmi(float f) {
        String string = "";
        if (f > 30f) {
            string = "Béo phì";
        } else if (f >= 25.0f && f <= 29.9f) { // Chỉnh sửa điều kiện ở đây
            string = "Thừa cân";
        } else if (f >= 18.5f && f <= 24.9f) {
            string = "Bình thường";
        } else {
            string = "Thiếu cân";
        }
        return string;
    }
}
