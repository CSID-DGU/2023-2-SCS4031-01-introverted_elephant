package com.oldcare.capstonedesign.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.oldcare.capstonedesign.R;
import com.oldcare.capstonedesign.login.LoginActivity;
import com.oldcare.capstonedesign.old_man.OldWarningActivity;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

public class FirestoreNotificationService extends Service {

    private static final String TAG = "FirestoreNotification";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "default_notification_channel";
    private static final String CUSTOM_NOTIFICATION_CHANNEL_ID = "custom_notification_channel";
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase 초기화 및 Firestore 연결 설정
        db = FirebaseFirestore.getInstance();

        // 알림 채널 생성
        createDefaultNotificationChannel();
        createCustomNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Firestore 변경 사항 감지 시작
        startListeningToFirestoreChanges();

        // 서비스를 Foreground Service로 시작
        startForeground(2, getNotification2("앱이 백그라운드에서 실행 중입니다."));
        startForeground(1, getNotification1("앱이 백그라운드에서 실행 중입니다."));

        // 서비스가 종료되지 않도록 START_STICKY 반환
        return START_STICKY;
    }

    private Notification getNotification1(String contentText) {
        // Foreground Service를 나타내는 알림 생성
        // 이거 지워도 잘 되는지 확인!!!!!!!!!
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.old_person);
        builder.setContentTitle("어르신을 부탁해");
        builder.setContentText(contentText);
        // 다른 액티비티로 이동할 수 있는 PendingIntent 설정

        // 반환
        return builder.build();
    }

    private Notification getNotification2(String contentText) {
        // Foreground Service를 나타내는 알림 생성
        // 이거 지워도 잘 되는지 확인!!!!!!!!!
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CUSTOM_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.old_person);
        builder.setContentTitle("어르신을 부탁해");
        builder.setContentText(contentText);
        // 다른 액티비티로 이동할 수 있는 PendingIntent 설정

        // 반환
        return builder.build();
    }

    @Override
    public void onDestroy() {
        // Firestore 변경 사항 감지 중지
        stopListeningToFirestoreChanges();

        // Foreground Service 중지
        stopForeground(true);
        // 서비스 종료
        stopSelf();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startListeningToFirestoreChanges() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");

        registration = db.collection("Users").document(uid)
                .collection("message")
                .addSnapshotListener(MetadataChanges.INCLUDE, (queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // 오류 처리
                        return;
                    }

                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String titleValue = dc.getDocument().getString("title");

                            if (titleValue != null) {
                                if (titleValue.equals("경고")) {
                                    sendCustomLocalNotification("보호자께서 안부 확인 알림을 보냈습니다. 알림을 클릭해주세요.");
                                    Log.d("121212", "경고알림도착");
                                } else {
                                    Log.d("121212", "일반알림도착");
                                    sendDefaultLocalNotification("새로운 알림이 도착했습니다.");
                                }
                            } else {
                                Log.d("121212", "titleValue가 null입니다.");
                            }
                        }
                    }

                });
    }


    public void stopListeningToFirestoreChanges() {
        if (registration != null) {
            registration.remove();
        }
    }

    // 기본 알림 채널 생성
    private void createDefaultNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = "Default Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 사용자 지정 알림 채널 생성
    private void createCustomNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = "Custom Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CUSTOM_NOTIFICATION_CHANNEL_ID, channelName, importance);
            // 알림음 설정 (res/raw 디렉토리에 있는 galaxy.mp3 사용)
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.galaxy);
            channel.setSound(soundUri, null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 기본 알림 보내기
    private void sendDefaultLocalNotification(String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID);

        // 진동 패턴 설정
        long[] pattern = {0, 1000, 1000, 1000};
        notificationBuilder.setVibrate(pattern);

        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder.setSmallIcon(R.drawable.old_person);
        notificationBuilder.setContentTitle("알림 도착");
        notificationBuilder.setContentText(message);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    // 사용자 지정 알림 보내기
    private void sendCustomLocalNotification(String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CUSTOM_NOTIFICATION_CHANNEL_ID);

        // 진동 패턴 설정
        long[] pattern = {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        notificationBuilder.setVibrate(pattern);

        Intent intent = new Intent(this, OldWarningActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder.setSmallIcon(R.drawable.old_person);
        notificationBuilder.setContentTitle("안부 확인 알림 도착");
        notificationBuilder.setContentText(message);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notificationBuilder.build());
    }
}
