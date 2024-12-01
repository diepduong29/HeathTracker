package com.dt2d.heathtracker.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dt2d.heathtracker.R;
import com.dt2d.heathtracker.bmi.BMIActivity;
import com.dt2d.heathtracker.step.StepActivity;
import com.dt2d.heathtracker.water.DrinkWaterActivity;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout layoutWaterReminder, layoutBmi, layoutStepCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhxa();

        // Thiết lập sự kiện click cho từng ConstraintLayout
        layoutWaterReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang activity nhắc nhở uống nước
                Intent intent = new Intent(MainActivity.this, DrinkWaterActivity.class);
                startActivity(intent);
            }
        });

        layoutBmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BMIActivity.class);
                startActivity(intent);
            }
        });

        layoutStepCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StepActivity.class);
                startActivity(intent);
            }
        });
    }

    private void anhxa(){
        layoutWaterReminder = findViewById(R.id.water);
        layoutBmi = findViewById(R.id.bmi);
        layoutStepCounter = findViewById(R.id.step);
    }
}