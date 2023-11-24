package com.oldcare.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.oldcare.capstonedesign.service.FirestoreNotificationService2;

public class MasterServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_service);

        Intent intent1 = getIntent();
        if(intent1 != null) {
            // 데이터가 있는지 확인 후 추출
            String receivedVariable = intent1.getStringExtra("key");
            if(receivedVariable.equals("first")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(MasterServiceActivity.this, FirestoreNotificationService2.class);
                    startService(serviceIntent);
                }
                Toast.makeText(MasterServiceActivity.this, "어르신과 연결되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MasterServiceActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }



        Button alarmOnButton = findViewById(R.id.alarmOnButton);
        alarmOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(MasterServiceActivity.this, FirestoreNotificationService2.class);
                    startService(serviceIntent);
                }

            }
        });

        Button alarmOffButton = findViewById(R.id.alarmOffButton);
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MasterServiceActivity.this, FirestoreNotificationService2.class);
                stopService(serviceIntent);
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