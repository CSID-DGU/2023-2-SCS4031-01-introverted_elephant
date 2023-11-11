package com.example.capstonedesign;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StepCounterActivity extends AppCompatActivity {
    private TextView tvSteps;
    private StepReceiver stepReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        tvSteps = findViewById(R.id.tvSteps);
        Button btnStartService = findViewById(R.id.btnStartService);
        Button btnStopService = findViewById(R.id.btnStopService);
        stepReceiver = new StepReceiver();

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
            Log.d("StepCounterActivity", "Received steps update: " + steps);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startStepCounterService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        startService(serviceIntent);
        Log.d("StepCounterActivity", "Service started");
    }

    private void stopStepCounterService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        stopService(serviceIntent);
        Log.d("StepCounterActivity", "Service stopped");
    }
}
