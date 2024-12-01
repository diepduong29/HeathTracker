package com.dt2d.heathtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.classes.Step;


import java.util.List;

public class StepListView extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Step> stepList;

    public StepListView(Context context, int layout, List<Step> stepList) {
        this.context = context;
        this.layout = layout;
        this.stepList = stepList;
    }

    @Override
    public int getCount() {
        return stepList.size();
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
        TextView txtStep = view.findViewById(R.id.textViewStepRow);
        TextView txtDate = view.findViewById(R.id.textViewDateRow);


        //gán giá trị
        Step step = stepList.get(i);

        txtStep.setText(step.getSobuoc() + " Bước");
        //txtStep.setText(String.valueOf(step.getSobuoc())); hoặc như này
        txtDate.setText((step.getNgaytao()));

        return view;
    }
}
