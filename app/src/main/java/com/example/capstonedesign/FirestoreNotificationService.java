package com.example.capstonedesign;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreNotificationService extends Service {

    private static final String TAG = "FirestoreNotification";
    private static final String NOTIFICATION_CHANNEL_ID = "your_notification_channel_id";

    private FirebaseFirestore db;
    private ListenerRegistration registration;

    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase 초기화 및 Firestore 연결 설정
        db = FirebaseFirestore.getInstance();

        // 알림 채널 생성
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Firestore 변경 사항 감지 시작
        startListeningToFirestoreChanges();

        // 서비스를 Foreground Service로 시작
        startForeground(1, getNotification("앱이 백그라운드에서 실행 중입니다."));

        // 서비스가 종료되지 않도록 START_STICKY 반환
        return START_STICKY;
    }

    private Notification getNotification(String contentText) {
        // Foreground Service를 나타내는 알림 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.baseline_chat_24);
        builder.setContentTitle("앱 이름");
        builder.setContentText(contentText);
        // 다른 액티비티로 이동할 수 있는 PendingIntent 설정

        // 반환
        return builder.build();
    }

    @Override
    public void onDestroy() {
        // Firestore 변경 사항 감지 중지
        stopListeningToFirestoreChanges();

        //밑에 코드 두줄 지우면 앱 꺼져있어도 알림 계속 받기 가능, 하지만 플레이스토어 등록 불가
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
        registration = db.collection("Users").document("Q2nzmvWZgRSPZzaaHlXePgprpie2")
                .collection("message") // "message" 서브컬렉션을 감시
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            // Firestore 문서가 추가되면 로컬 알림 표시
                            sendLocalNotification("새로운 문서가 추가되었습니다.");
                        }
                    }
                });
    }

    public void stopListeningToFirestoreChanges() {
        if (registration != null) {
            registration.remove();
        }
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = "Your Channel Name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);

            // 알림 채널을 시스템에 등록
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendLocalNotification(String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder.setSmallIcon(R.drawable.baseline_chat_24);
        notificationBuilder.setContentTitle("메일 알림");
        notificationBuilder.setContentText(message);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }
}
