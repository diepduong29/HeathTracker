package com.dt2d.heathtracker.water;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class DrinkWaterActivity extends AppCompatActivity {
    Database database;
    ImageView imgBack, imgRemind, imgHistory, imgAdd, imgDel, imgUp;
    TextView txtChart, txtWater, txtSumWater, txtCountWater, txtWaterLast, txtUpGoalWater, txtGoalWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_drink_water);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhxa();
        backupWaterAmount();
        backupWaterGoal();

        database = new Database(this, "heathTracker.sqlite", null,2);

        updateTextView();
        updateCountTextView();
        waterLastEntry();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrinkWaterActivity.this, WaterHistoryActivity.class);
                startActivity(intent);
            }
        });

        imgRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrinkWaterActivity.this, RemindWaterActivity.class);
                startActivity(intent);
            }
        });

        txtChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrinkWaterActivity.this, ChartWaterActivity.class);
                startActivity(intent);
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWater();
                updateTextView();
                updateCountTextView();
                waterLastEntry();
            }
        });

        imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLastEntry();
                updateTextView();
                updateCountTextView();
                waterLastEntry();
            }
        });

        imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAmountWater();
            }
        });

        txtUpGoalWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSumWater();
            }
        });
    }

    // Lưu giá trị vào SharedPreferences
    private void updateWaterAmount(String data) {
        // Mở SharedPreferences trong chế độ chỉnh sửa
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Lưu chuỗi với khóa "waterAmount"
        editor.putString("waterAmount", data);

        // Áp dụng thay đổi
        editor.apply();
    }

    private void updateWaterGoal(String data) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("waterSum", data);

        editor.apply();
    }

    // Khôi phục giá trị từ SharedPreferences
    private void backupWaterGoal(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedWaterGoal = sharedPreferences.getString("waterSum", "2000 ml"); // Giá trị mặc định là "0ml" nếu không có dữ liệu

        // Cập nhật TextView với giá trị đã lưu
        txtGoalWater.setText(savedWaterGoal);
    }

    private void backupWaterAmount(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedWaterAmount = sharedPreferences.getString("waterAmount", "200 ml");

        txtWater.setText(savedWaterAmount);
    }

    private void DialogAmountWater() {
        Dialog dialog = new Dialog(this);// b1
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //b2
        dialog.setContentView(R.layout.dialog_amountwater); // Sử dụng bố cục tùy chỉnh từ tệp XML b3

        EditText editText = dialog.findViewById(R.id.edittextUpWater);
        Button buttonUp = dialog.findViewById(R.id.buttonUpWater);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancelUpWater);

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện nhấn nút OK
                String string = editText.getText().toString(); // Lấy dữ liệu người dùng nhập

                if (!string.isEmpty()) {
                    // Cập nhật TextView với giá trị nhập
                    txtWater.setText(string + " ml");
                    updateWaterAmount(string + " ml");
                    dialog.dismiss();
                } else {
                    Toast.makeText(DrinkWaterActivity.this, "Vui lòng nhập một số hợp lệ!", Toast.LENGTH_SHORT).show();
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

    private void DialogSumWater() {
        Dialog dialog = new Dialog(this);// b1
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //b2
        dialog.setContentView(R.layout.dialog_sumwater); // Sử dụng bố cục tùy chỉnh từ tệp XML b3

        EditText editText = dialog.findViewById(R.id.edittextUpSumWater);
        Button buttonUp = dialog.findViewById(R.id.buttonUpSumWater);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancelUpSumWater);

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện nhấn nút OK
                String string = editText.getText().toString(); // Lấy dữ liệu người dùng nhập

                if (!string.isEmpty()) {
                    // Cập nhật TextView với giá trị nhập
                    txtGoalWater.setText(string + " ml");
                    updateWaterGoal(string + " ml");
                    dialog.dismiss();
                } else {
                    Toast.makeText(DrinkWaterActivity.this, "Vui lòng nhập một số hợp lệ!", Toast.LENGTH_SHORT).show();
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

    private void addWater(){
        String text = txtWater.getText().toString();
        // Loại bỏ "ml" và giữ lại phần số
        String number = text.replace("ml", "").trim();
        // Chuyển đổi giá trị đã lọc thành số nguyên
        int amount = Integer.parseInt(number);
        String time = getCurrentTimestamp();
        String insertWaterQuery = "INSERT INTO water_table(amount, timestamp) VALUES (" + amount + ", '" + time + "')";

        database.QueryDataBase(insertWaterQuery);
    }

    private void deleteLastEntry() {
        // Truy vấn id cuối cùng trong ngày
        Cursor cursor = database.GetDataBase("SELECT id FROM water_table WHERE DATE(timestamp) = DATE('now', 'localtime') ORDER BY id DESC LIMIT 1");
        if (cursor.moveToFirst()) {
            int idToDelete = cursor.getInt(0);
            database.QueryDataBase("DELETE FROM water_table WHERE id = " + idToDelete);

        } else {
            Toast.makeText(this, "Không có bản ghi nào để xóa trong ngày hôm nay.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void waterLastEntry() {
        Cursor cursor = database.GetDataBase("SELECT amount FROM water_table WHERE DATE(timestamp) = DATE('now', 'localtime') ORDER BY id DESC LIMIT 1");
        if (cursor.moveToFirst()) {
            int waterLast = cursor.getInt(0);
            txtWaterLast.setText(waterLast + " ml");
        }else {
            txtWaterLast.setText("--ml");
        }
        cursor.close();
    }

    private void updateTextView() {
        Cursor cursor = database.GetDataBase("SELECT SUM(amount) FROM water_table WHERE DATE(timestamp) = DATE('now', 'localtime')");
        if (cursor.moveToFirst()) {
            int totalWater = cursor.getInt(0); // Lấy tổng lượng nước uống
            txtSumWater.setText(totalWater + " ml");
        } else {
            txtSumWater.setText("0ml");
        }
        cursor.close();
    }

    // Hàm lấy thời gian hiện tại dưới dạng chuỗi
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void updateCountTextView() {
        Cursor cursor = database.GetDataBase("SELECT COUNT(*) FROM water_table WHERE DATE(timestamp) = DATE('now', 'localtime')");
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0); // Lấy tổng số dòng trong ngày
            txtCountWater.setText(count + " Cốc");
        } else {
            txtCountWater.setText("0 Cốc");
        }
        cursor.close();
    }

    private void anhxa() {
        imgBack = findViewById(R.id.back);
        imgHistory = findViewById(R.id.history);
        imgRemind = findViewById(R.id.remind);
        txtChart = findViewById(R.id.chart);
        imgAdd = findViewById(R.id.addWater);
        txtWater = findViewById(R.id.dataWater);
        txtSumWater = findViewById(R.id.watertextView);
        imgDel = findViewById(R.id.delWater);
        txtCountWater = findViewById(R.id.countTextView);
        txtWaterLast = findViewById(R.id.waterLastView);
        imgUp = findViewById(R.id.upWater);
        txtGoalWater = findViewById(R.id.textViewGoalWater);
        txtUpGoalWater = findViewById(R.id.textViewUpGoalWater);
    }
}
