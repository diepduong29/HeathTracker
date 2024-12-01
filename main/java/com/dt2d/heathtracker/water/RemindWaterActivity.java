package com.dt2d.heathtracker.water;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dt2d.heathtracker.AlarmWater;
import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.adapter.RemindWaterListView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;

public class RemindWaterActivity extends AppCompatActivity {
    MaterialTimePicker picker;
    Calendar calendar;
    ListView lst;
    ArrayList<String> arrayList;
    ArrayList<Boolean> switchStates;
    RemindWaterListView adapter;
    ImageView imgBack, imgAdd;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remind_water);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhxa();

        // Kiểm tra và yêu cầu quyền thông báo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        } else {
            createNotificationChannel();
        }
        // Khởi tạo danh sách
        arrayList = new ArrayList<>();
        switchStates = new ArrayList<>();

        // Nạp dữ liệu từ SharedPreferences
        loadData();

        // Thiết lập adapter
        adapter = new RemindWaterListView(RemindWaterActivity.this, arrayList, switchStates);
        lst.setAdapter(adapter);

        imgAdd.setOnClickListener(view -> showTimePicker());

        imgBack.setOnClickListener(view -> {
            saveData(); // Lưu lại dữ liệu trước khi thoát
            finish();
        });
    }

    private void anhxa() {
        imgBack = findViewById(R.id.backRemind);
        imgAdd = findViewById(R.id.imageViewAdd);
        lst = findViewById(R.id.listViewRemindWater);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("RemindWaterPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Lưu danh sách môn học
        editor.putInt("size", arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            editor.putString("subject_" + i, arrayList.get(i));
            editor.putBoolean("switch_" + i, switchStates.get(i));
        }

        editor.apply(); // Lưu lại dữ liệu
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("RemindWaterPrefs", MODE_PRIVATE);
        int size = sharedPreferences.getInt("size", 0);
        arrayList.clear();
        switchStates.clear();

        for (int i = 0; i < size; i++) {
            arrayList.add(sharedPreferences.getString("subject_" + i, null));
            switchStates.add(sharedPreferences.getBoolean("switch_" + i, false));
        }

        // Cập nhật lại adapter
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void showTimePicker() {
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(), "foxandroid");

        picker.addOnPositiveButtonClickListener(view -> {
            String selectedTime;
            if (picker.getHour() > 12) {
                selectedTime = String.format("%02d:%02d PM", picker.getHour() - 12, picker.getMinute());
            } else {
                selectedTime = String.format("%02d:%02d AM", picker.getHour(), picker.getMinute());
            }

            // Thêm thời gian vào danh sách
            arrayList.add(selectedTime);
            switchStates.add(false); // Trạng thái switch mặc định là tắt
            adapter.notifyDataSetChanged(); // Cập nhật adapter
            saveData(); // Lưu lại dữ liệu
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void cancelAlarm() {
        Intent intent = new Intent(this, AlarmWater.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }

    public void setAlarm() {
        if (calendar == null) {
            Toast.makeText(this, "Please select a time first", Toast.LENGTH_SHORT).show();
            return;
        }

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmWater.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
        Log.d("RemindWaterActivity", "Alarm set at: " + calendar.getTime());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("RemindWaterActivity", "Notification permission granted.");
                createNotificationChannel(); // Tạo channel nếu quyền được cấp
            } else {
                Log.d("RemindWaterActivity", "Notification permission denied.");
            }
        }
    }

}
