package com.dt2d.heathtracker.bmi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dt2d.heathtracker.classes.BMI;
import com.dt2d.heathtracker.sqlite.Database;
import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.adapter.BmiListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BMIHistoryActivity extends AppCompatActivity {
    TextView textView;
    Database database;
    ListView listView;
    ImageView imgBack;
    ArrayList<BMI> arrayList;
    BmiListView bmiAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bmihistory);
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
        bmiAdapter = new BmiListView(this, R.layout.row_historybmi, arrayList);
        listView.setAdapter(bmiAdapter);

        //lay du lieu
        Cursor cursor = database.GetDataBase("SELECT * FROM bmi_table WHERE strftime('%m', timestamp) = strftime('%m', 'now', 'localtime') AND strftime('%Y', timestamp) = strftime('%Y', 'now', 'localtime')");

        while (cursor.moveToNext()){
            arrayList.add(new BMI(cursor.getInt(0), cursor.getFloat(1), cursor.getFloat(2), cursor.getFloat(3), cursor.getString(4)));
        }cursor.close();
        bmiAdapter.notifyDataSetChanged(); //cập nhật lại để RecyclerView hiểu rằng dữ liệu đã thay đổi và vẽ lại các item tương ứng
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
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
                        String selectedDate = i + "-" + formattedMonth;
                        textView.setText(selectedDate);
                        updateListview(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }


    private void updateListview(String string) {
        arrayList = new ArrayList<>();
        bmiAdapter = new BmiListView(this, R.layout.row_historybmi, arrayList);
        listView.setAdapter(bmiAdapter);

        //lay du lieu
        Cursor cursor = database.GetDataBase("SELECT * FROM bmi_table WHERE strftime('%Y-%m', timestamp) = '" + string + "'");
//        arrayList.clear();
        while (cursor.moveToNext()){
            arrayList.add(new BMI(cursor.getInt(0), cursor.getFloat(1), cursor.getFloat(2), cursor.getFloat(3), cursor.getString(4)));
        } cursor.close();
        bmiAdapter.notifyDataSetChanged();
    }


    public void DiaLogDelete(int id){
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this);
        dialogDelete.setMessage("Bạn đồng ý xóa dữ liệu này Không ?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.QueryDataBase("DELETE FROM bmi_table WHERE id = '"+ id +"'");
                Toast.makeText(BMIHistoryActivity.this, "Delete successful!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                showList();
            }
        });
        dialogDelete.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        dialogDelete.show();
    }

    public void DiaLogEdit(float weight, float height, int id ){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_editbmi);
        //ánh xạ
        EditText editWeight = dialog.findViewById(R.id.edittextWeight);
        EditText editHeight = dialog.findViewById(R.id.edittextHeight);
        Button btnedit = dialog.findViewById(R.id.buttonUp);
        Button btneditCancel = dialog.findViewById(R.id.buttonCancel);
        //hiển thị lên form dialog_edit
        editWeight.setText(String.valueOf(weight));
        editHeight.setText(String.valueOf(height));

        //bắt sự kiện cho btnedit
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String w = editWeight.getText().toString();
                String h = editHeight.getText().toString();
                float updateWeight = Float.parseFloat(w);
                float updateHeight = Float.parseFloat(h);
                float bmi = updateWeight / (updateHeight * updateHeight);
                database.QueryDataBase("UPDATE bmi_table SET weight = '"+ updateWeight +"', height = '"+ updateHeight +"', bmi = '"+ bmi +"' WHERE id = '"+ id +"'");
                Toast.makeText(BMIHistoryActivity.this, "Update successful!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                dialog.dismiss();
                showList();
            }
        });

        btneditCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void anhxa(){
        textView = findViewById(R.id.textViewDateHistory);
        imgBack = findViewById(R.id.imageViewBack);
        listView = findViewById(R.id.listViewHistory);
    }
}