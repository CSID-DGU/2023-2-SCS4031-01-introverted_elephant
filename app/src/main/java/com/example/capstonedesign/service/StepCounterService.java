package com.example.capstonedesign.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.capstonedesign.MainActivity;
import com.example.capstonedesign.R;

public class StepCounterService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int steps = 0;
    private static final String CHANNEL_ID = "StepCounterChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        createNotificationChannel();
        startForeground(1, getNotification());

        return START_STICKY;
    }

    private long lastStepTime = 0;
    private static final long COOL_DOWN_TIME = 200; // 1초 동안은 센서 이벤트를 무시

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastStepTime > COOL_DOWN_TIME) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
                float threshold = 30.0f;  // 임의의 임계값입니다. 조절이 필요할 수 있습니다. (민감도)

                if (magnitude > threshold) {
                    steps++;

                    // MainActivity로 걸음 수 업데이트 브로드캐스트
                    Intent intent = new Intent("com.example.stepcountapp.STEP_UPDATE");
                    intent.putExtra("steps", steps);
                    sendBroadcast(intent);

                    lastStepTime = currentTime; // 현재 시간으로 업데이트

                    // Notification 업데이트 등의 추가 로직이 필요하다면 여기에 추가
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정밀도에 따른 처리 (필요한 경우)
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Step Counter Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Capstone Design")
                .setContentText("만보기 실행 중")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
    }
}
