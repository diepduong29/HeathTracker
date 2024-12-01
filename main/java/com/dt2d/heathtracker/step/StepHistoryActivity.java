package com.dt2d.heathtracker.step;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dt2d.heathtracker.sqlite.Database;
import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.classes.Step;
import com.dt2d.heathtracker.adapter.StepListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StepHistoryActivity extends AppCompatActivity {

    TextView textView;
    Database database;
    ListView listView;
    ImageView imgBack;
    ArrayList<Step> arrayList;
    StepListView stepAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_step_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhxa();

        database = new Database(this, "heathTracker.sqlite", null, 2);
        showList();
        textView.setText(getCurrentTimestamp());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }
    private void showList(){
        arrayList = new ArrayList<>();
        stepAdapter = new StepListView(this, R.layout.row_historystep, arrayList);
        listView.setAdapter(stepAdapter);

        //lay du lieu
        Cursor cursor = database.GetDataBase("SELECT * FROM steps_table WHERE strftime('%m', timestamp) = strftime('%m', 'now', 'localtime') AND strftime('%Y', timestamp) = strftime('%Y', 'now', 'localtime')");

        while (cursor.moveToNext()){
            arrayList.add(new Step(cursor.getInt(0), cursor.getInt(1), cursor.getString(2)));
        }cursor.close();
        stepAdapter.notifyDataSetChanged();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showDatePickerDialog() {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String formattedMonth = String.format("%02d", i1 + 1);
                        String formattedDay = String.format("%02d", i2);
                        String selectedDate = i + "-" + formattedMonth + "-" + formattedDay;
                        textView.setText(selectedDate);
                        updateListview(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateListview(String string) {
        arrayList = new ArrayList<>();
        stepAdapter = new StepListView(this, R.layout.row_historystep, arrayList);
        listView.setAdapter(stepAdapter);

        //lay du lieu
        Cursor cursor = database.GetDataBase("SELECT * FROM steps_table WHERE DATE(timestamp) = DATE('"+ string +"')");
//        arrayList.clear();
        while (cursor.moveToNext()){
            arrayList.add(new Step(cursor.getInt(0), cursor.getInt(1), cursor.getString(2)));
        } cursor.close();
        stepAdapter.notifyDataSetChanged();
    }
    private void anhxa(){
        textView = findViewById(R.id.textViewDateHistoryStep);
        imgBack = findViewById(R.id.imageViewBackHistory);
        listView = findViewById(R.id.listViewHistory);
    }
}