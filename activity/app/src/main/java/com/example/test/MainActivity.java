package com.example.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepCountView;
    Button resetButton;

    //현재 걸음 수
    int currentSteps = 0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountView = findViewById(R.id.stepCountView);
        resetButton = findViewById(R.id.resetButton);

        //활동 퍼미선 체크
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        //걸음 센서 연결
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //디바이스에 걸음 센서의 존재 여부 체크
        if(stepCountSensor == null){
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }

        //리셋 버튼 추가 - 리셋 기능
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                currentSteps = 0;
                stepCountView.setText(String.valueOf(currentSteps));
            }
        });
    }

    public void onStart() {
        super.onStart();
        if(stepCountSensor != null){
            sensorManager.registerListener(this, stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0] == 1.0f){
                currentSteps++;
                stepCountView.setText(String.valueOf(currentSteps));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}