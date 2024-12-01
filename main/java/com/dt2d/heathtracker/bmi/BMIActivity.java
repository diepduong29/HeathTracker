package com.dt2d.heathtracker.bmi;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

public class BMIActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 1;
    Database database;
    ImageView imgAdd, imgBack, imgHistory, imgRemind;
    TextView txtChart, txtDate, txtWeight, txtHeight, txtBmi, txtTestBmi;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            showBmi();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView( R.layout.activity_bmiactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhxa();
        database = new Database(this, "heathTracker.sqlite", null,2);

        showBmi();
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAddBmi();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(BMIActivity.this, BMIHistoryActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(BMIActivity.this, BMIHistoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        txtChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BMIActivity.this, ChartBmiActivity.class);
                startActivity(intent);
            }
        });
    }


    private void showBmi(){
        Cursor cursor = database.GetDataBase("SELECT * FROM bmi_table ORDER BY id DESC LIMIT 1");
        if (cursor.moveToFirst()) {
            // Nếu có dữ liệu
            float weight = cursor.getFloat(1);
            float height = cursor.getFloat(2);
            float bmi = cursor.getFloat(3);
            String time = cursor.getString(4);

            // Cập nhật vào TextView
            String string = chisobmi(bmi);
            txtTestBmi.setText(string);
            txtBmi.setText(bmi + " Bmi");
            txtWeight.setText(weight + " kg");
            txtHeight.setText(height + " m");
            txtDate.setText(time);
        } else {
            txtTestBmi.setText("BMI");
            txtBmi.setText(" Bmi");
            txtWeight.setText(" kg");
            txtHeight.setText(" m");
            txtDate.setText(" Ngày");
            DialogAddBmi();
        }
        cursor.close();
    }


    // Hàm lấy thời gian hiện tại dưới dạng chuỗi
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void DialogAddBmi() {
        Dialog dialog = new Dialog(this);// b1
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //b2
        dialog.setContentView(R.layout.dialog_addbmi); // Sử dụng bố cục tùy chỉnh từ tệp XML b3

        EditText editW = dialog.findViewById(R.id.etWeight);
        EditText editH = dialog.findViewById(R.id.etHeight);
        Button buttonAdd = dialog.findViewById(R.id.addBmiButton);
        Button buttonCancel = dialog.findViewById(R.id.cancelButton);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu người dùng nhập
                String stringW = editW.getText().toString();
                String stringH = editH.getText().toString();

                if (!stringW.isEmpty() && !stringH.isEmpty()) {
                    float weight = Float.parseFloat(stringW);
                    float height = Float.parseFloat(stringH);
                    float bmi = weight / (height * height);
                    float bmiRounded = Math.round(bmi * 100) / 100f;
                    String time = getCurrentTimestamp();

                    // Lưu dữ liệu vào cơ sở dữ liệu
                    String insert = "INSERT INTO bmi_table(weight, height ,bmi, timestamp) VALUES (" + weight + "," + height + "," + bmiRounded + ", '" + time + "')";
                    database.QueryDataBase(insert);

                    dialog.dismiss();
                    showBmi();
                } else {
                    Toast.makeText(BMIActivity.this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
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


    private void anhxa(){
        imgAdd = findViewById(R.id.addBmi);
        imgBack = findViewById(R.id.back);
        imgHistory = findViewById(R.id.historyBmi);
        imgRemind = findViewById(R.id.remindBmi);
        txtChart = findViewById(R.id.chartBmi);
        txtBmi = findViewById(R.id.textViewBmi);
        txtDate = findViewById(R.id.textViewDate);
        txtWeight = findViewById(R.id.textViewWeight);
        txtHeight = findViewById(R.id.textViewHeight);
        txtTestBmi = findViewById(R.id.bmitextView);
    }
}