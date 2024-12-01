package com.dt2d.heathtracker.step;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dt2d.heathtracker.sqlite.Database;
import com.dt2d.heathtracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ChartStepActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    BarChart chart;
    Database database;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart_step);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhxa();
        database = new Database(this, "heathTracker.sqlite", null, 2);
        loadChartData();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonDay){
                    updateChartData("day");
                } else if (checkedId == R.id.radioButtonWeek) {
                    updateChartData("week");
                } else if (checkedId == R.id.radioButtonMonth) {
                    updateChartData("month");
                } else if (checkedId == R.id.radioButton6Month) {
                    updateChartData("6month");
                } else if (checkedId == R.id.radioButtonYear) {
                    updateChartData("year");
                }
            }
        });
    }
    private void loadChartData() {
        ArrayList<BarEntry> steps = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        Cursor cursor = database.GetDataBase("SELECT steps, DATE(timestamp) FROM steps_table WHERE DATE(timestamp) = DATE('now', 'localtime') GROUP BY DATE(timestamp)");
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                int step = cursor.getInt(0);
                String date = cursor.getString(1);

                steps.add(new BarEntry(index, step));
                dates.add(date);
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        updateChart(steps, dates);
    }

    private void updateChartData(String timePeriod) {
        String query = "";
        switch (timePeriod) {
            case "day":
                query = "SELECT steps, DATE(timestamp) FROM steps_table " +
                        "WHERE DATE(timestamp) = DATE('now', 'localtime') " +
                        "GROUP BY DATE(timestamp)";
                break;
            case "week":
                query = "SELECT steps, DATE(timestamp) FROM steps_table " +
                        "WHERE DATE(timestamp) >= DATE('now', 'localtime', '-6 day') " +
                        "GROUP BY DATE(timestamp)";
                break;
            case "month":
                query = "SELECT steps, strftime('%Y-%m', timestamp) FROM steps_table " +
                        "WHERE strftime('%Y-%m', timestamp) = strftime('%Y-%m', 'now', 'localtime') " +
                        "GROUP BY strftime('%Y-%m', timestamp)";
                break;
            case "6month":
                query = "SELECT steps, strftime('%Y-%m', timestamp) FROM steps_table " +
                        "WHERE strftime('%Y-%m', timestamp) >= strftime('%Y-%m', 'now', '-6 months', 'localtime') " +
                        "GROUP BY strftime('%Y-%m', timestamp)";
                break;
            case "year":
                query = "SELECT steps, strftime('%Y', timestamp) FROM steps_table " +
                        "WHERE strftime('%Y', timestamp) = strftime('%Y', 'now', 'localtime') " +
                        "GROUP BY strftime('%Y', timestamp)";
                break;
        }

        // Thực hiện truy vấn và cập nhật biểu đồ
        Cursor cursor = database.GetDataBase(query);
        ArrayList<BarEntry> steps = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                int step = cursor.getInt(0);
                String date = cursor.getString(1);

                steps.add(new BarEntry(index, step));
                dates.add(date);
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Cập nhật dữ liệu vào biểu đồ
        updateChart(steps, dates);
    }


    private void updateChart(ArrayList<BarEntry> steps, ArrayList<String> dates) {
        BarDataSet dataSet = new BarDataSet(steps, "Số bước chân");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        // Cấu hình biểu đồ
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(2000f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.animateY(1000);

        // Cấu hình trục X để hiển thị dưới cùng
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);

        chart.invalidate();  // Refresh biểu đồ
    }

    private void anhxa(){
        imgBack = findViewById(R.id.backChart);
        chart = findViewById(R.id.chart);
        radioGroup = findViewById(R.id.radioGroup);
    }
}