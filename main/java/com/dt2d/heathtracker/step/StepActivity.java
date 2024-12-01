package com.dt2d.heathtracker.step;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dt2d.heathtracker.sqlite.Database;
import com.dt2d.heathtracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class StepActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor stepSensor;
    Database database;
    TextView txtStep, txtStepData, txtDistance, txtChart, txtSetUp;
    ImageView imgBack, imgHistory;
    int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_step);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhxa();
        database = new Database(this, "heathTracker.sqlite", null,2);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        backupStep();
        loadStepCountFromDB();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StepActivity.this, ChartStepActivity.class);
                startActivity(intent);
            }
        });

        imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StepActivity.this, StepHistoryActivity.class);
                startActivity(intent);
            }
        });

        txtSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogSetUp();
            }
        });
    }

    private void loadStepCountFromDB() {
        // Load bước chân mới nhất từ database và cập nhật giao diện
        Cursor cursor = database.GetDataBase("SELECT steps FROM steps_table WHERE DATE(timestamp) = DATE('now', 'localtime')");
        if (cursor != null && cursor.moveToFirst()) {
            stepCount = cursor.getInt(0); // Lấy tổng số dòng trong ngày
            txtStepData.setText(stepCount + " Bước"); // Cập nhật số dòng vào TextView
            float s = stepCount * 0.35f;
            txtDistance.setText(s + " m");
        } else {
            txtStepData.setText("0 Bước"); // Nếu không có dữ liệu thì hiển thị 0
        }
        if (cursor != null) {
            cursor.close();
        }
    }


    private void DialogSetUp() {
        Dialog dialog = new Dialog(this);// b1
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //b2
        dialog.setContentView(R.layout.dialog_setup); // Sử dụng bố cục tùy chỉnh từ tệp XML b3

        // Lấy các phần tử từ bố cục để xử lý sự kiện
        EditText editText = dialog.findViewById(R.id.edittextUp);
        Button buttonUp = dialog.findViewById(R.id.buttonUp);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện nhấn nút OK
                String string = editText.getText().toString(); // Lấy dữ liệu người dùng nhập

                if (!string.isEmpty()) {
                    // Cập nhật TextView với giá trị nhập
                    txtStep.setText(string + " Bước");
                    updateStep(string + " Bước");
                    dialog.dismiss(); // Đóng hộp thoại khi hoàn tất
                } else {
                    Toast.makeText(StepActivity.this, "Vui lòng nhập một số hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Đóng hộp thoại khi hoàn tất
            }
        });
        dialog.show(); // Hiển thị hộp thoại
    }

    private void backupStep(){
        // Khôi phục giá trị từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String save = sharedPreferences.getString("setupStep", "2000 Bước"); // Giá trị mặc định là "0ml" nếu không có dữ liệu

        // Cập nhật TextView với giá trị đã lưu
        txtStep.setText(save);
    }

    // Lưu giá trị vào SharedPreferences
    private void updateStep(String data) {
        // Mở SharedPreferences trong chế độ chỉnh sửa
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Lưu chuỗi với khóa "waterAmount"
        editor.putString("setupStep", data);

        // Áp dụng thay đổi
        editor.apply();
    }
    private void anhxa(){
        txtDistance = findViewById(R.id.textViewDistance);
        txtStep = findViewById(R.id.textViewStep);
        txtStepData = findViewById(R.id.textViewStepData);
        imgBack = findViewById(R.id.back);
        imgHistory = findViewById(R.id.historyStep);
        txtChart = findViewById(R.id.chartStep);
        txtSetUp = findViewById(R.id.textViewSetUp);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
            String string = getCurrentTimestamp();

            if (database.QueryDataBase("INSERT INTO steps_table(steps, timestamp) VALUES (" + stepCount + ", '" + string + "')")) {
                Log.d("StepActivity", "Đã thêm bước chân vào database");
            } else {
                Log.e("StepActivity", "Lỗi khi thêm bước chân vào database");
            }

            txtStepData.setText(stepCount + " Bước");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}