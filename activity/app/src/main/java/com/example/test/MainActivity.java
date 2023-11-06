package com.example.test;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.StepCounterService;

public class MainActivity extends AppCompatActivity {

    private TextView tvSteps;
    private Button btnStartService, btnStopService;
    private StepReceiver stepReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSteps = findViewById(R.id.tvSteps);
        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        stepReceiver = new StepReceiver();

        // 이 예제에서는 걸음 수를 실시간으로 업데이트하지 않았습니다.
        // BroadcastReceiver나 다른 메커니즘을 사용하여 실시간으로 걸음 수를 업데이트할 수 있습니다.

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startStepCounterService();
                }
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStepCounterService();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(stepReceiver, new IntentFilter("com.example.stepcountapp.STEP_UPDATE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(stepReceiver);
    }

    private class StepReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int steps = intent.getIntExtra("steps", 0);
            tvSteps.setText("Steps: " + steps);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startStepCounterService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        startForegroundService(serviceIntent);
        Log.d("MainActivity", "Service started");

    }

    private void stopStepCounterService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        stopService(serviceIntent);
    }
}