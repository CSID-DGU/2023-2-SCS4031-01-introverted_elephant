package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstonedesign.service.FirestoreNotificationService;
import com.example.capstonedesign.service.FirestoreNotificationService2;
import com.example.capstonedesign.service.StepCounterService;

public class OldServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_service);

        Intent intent1 = getIntent();
        if(intent1 != null) {
            // 데이터가 있는지 확인 후 추출
            String receivedVariable = intent1.getStringExtra("key");
            if(receivedVariable.equals("first")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(OldServiceActivity.this, FirestoreNotificationService.class);
                    startService(serviceIntent);
                    Intent serviceIntent1 = new Intent(OldServiceActivity.this, StepCounterService.class);
                    startService(serviceIntent1);
                }
                Toast.makeText(OldServiceActivity.this, "보호자와 연결되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OldServiceActivity.this, LoadingActivity.class);
                startActivity(intent);
                finish();
            }
        }



        Button alarmOnButton = findViewById(R.id.alarmOnButton);
        alarmOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(OldServiceActivity.this, FirestoreNotificationService.class);
                    startService(serviceIntent);
                }

            }
        });

        Button alarmOffButton = findViewById(R.id.alarmOffButton);
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(OldServiceActivity.this, FirestoreNotificationService.class);
                stopService(serviceIntent);
            }
        });

        Button stepOnButton = findViewById(R.id.stepOnButton);
        stepOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent serviceIntent1 = new Intent(OldServiceActivity.this, StepCounterService.class);
                    startService(serviceIntent1);
                }

            }
        });

        Button stepOffButton = findViewById(R.id.stepOffButton);
        stepOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent1 = new Intent(OldServiceActivity.this, StepCounterService.class);
                stopService(serviceIntent1);
            }
        });


        Button alarmButton = findViewById(R.id.alarmButton);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        });




    }
}