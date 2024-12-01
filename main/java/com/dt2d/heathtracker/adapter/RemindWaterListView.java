package com.dt2d.heathtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.water.RemindWaterActivity;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;

public class RemindWaterListView extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    private ArrayList<Boolean> switchStates;

    public RemindWaterListView(Context context, ArrayList<String> arrayList, ArrayList<Boolean> switchStates) {
        this.context = context;
        this.arrayList = arrayList;
        this.switchStates = switchStates;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_remindwater, null);

        // Ánh xạ view
        Switch aSwitch = view.findViewById(R.id.switchRemindWater);
        TextView txtRemind = view.findViewById(R.id.textViewRemindWater);

        // Gán giá trị
        txtRemind.setText(arrayList.get(i));
        aSwitch.setChecked(switchStates.get(i));

        // Xử lý khi trạng thái của switch thay đổi
        aSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            switchStates.set(i, isChecked);
            if (context instanceof RemindWaterActivity) {
                ((RemindWaterActivity) context).saveData();  // Lưu trạng thái vào SharedPreferences
            }

            if (isChecked) {
                ((RemindWaterActivity) context).setAlarm();
            } else {
                ((RemindWaterActivity) context).cancelAlarm();
            }
        });
        txtRemind.setOnClickListener(v -> showTimePicker(i));
        return view;
    }

    private void showTimePicker(int position) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(((AppCompatActivity) context).getSupportFragmentManager(), "timePicker");

        picker.addOnPositiveButtonClickListener(view -> {
            String selectedTime;
            if (picker.getHour() > 12) {
                selectedTime = String.format("%02d:%02d PM", picker.getHour() - 12, picker.getMinute());
            } else {
                selectedTime = String.format("%02d:%02d AM", picker.getHour(), picker.getMinute());
            }

            // Cập nhật thời gian vào danh sách
            arrayList.set(position, selectedTime); // Cập nhật thời gian cho vị trí hiện tại
            notifyDataSetChanged(); // Cập nhật lại danh sách
        });
    }
}
