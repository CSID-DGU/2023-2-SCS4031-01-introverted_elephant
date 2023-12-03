package com.oldcare.capstonedesign.location;

import static com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.oldcare.capstonedesign.NotificationActivity;
import com.oldcare.capstonedesign.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.HashMap;
import java.util.Map;

public class Locationservice extends Service {
    private String uid;
    private int MAX_DOCUMENTS = 30;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.v("LOCATION_UPDATE", latitude + ", " + longitude);

                writeLocationToFirestore(latitude,longitude, uid);
                CheckOutOfSafetyZone(latitude,longitude);
                maintainMaxDocumentCount(uid,"location_data",MAX_DOCUMENTS);
            }
        }
    };
    private Double safe_latitude;
    private Double safe_longitude;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService(Intent intent) {
        if (intent != null) {
            // 인텐트에서 uid 값을 추출
            uid = intent.getStringExtra("uid");
        }
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest.Builder(PRIORITY_BALANCED_POWER_ACCURACY,60*1000).build(); // 1분마다

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
        startForeground(location_Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(location_Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService(intent);
                } else if (action.equals(location_Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void writeLocationToFirestore(double latitude, double longitude, String uid) {
        // Firestore에 저장할 데이터 만들기
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);
        locationData.put("timestamp", FieldValue.serverTimestamp());


        // Firestore에 데이터 쓰기
        db.collection("Users")  // 사용자 위치를 저장하는 컬렉션 이름
                .document(uid)              // 사용자 ID를 문서 이름으로 사용
                .collection("location_data") // "location_data"라는 하위 컬렉션 생성 또는 참조
                .add(locationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Location data written successfully");
                    // 추가 후 최대 개수를 유지하도록 호출
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error writing location data: " + e.getMessage());
                });
        // 안전구역을 벗어났는지 검사
    }

    // 최대 개수를 유지하도록 호출되는 메소드
    private void maintainMaxDocumentCount(String userId, String subCollectionName, int maxDocuments) {
        Log.d("Firestore", "Attempting to maintain max document count");
        db.collection("Users")
                .document(userId)
                .collection(subCollectionName)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int currentDocumentCount = queryDocumentSnapshots.size();
                    if (currentDocumentCount > maxDocuments) {
                        for (int i = 0; i < currentDocumentCount - maxDocuments; i++) {
                            String documentIdToDelete = queryDocumentSnapshots.getDocuments().get(i).getId();
                            deleteDocument(userId, subCollectionName, documentIdToDelete);
                        }
                    }
                    Log.d("Firestore", "Success to maintain max document count");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking document count", e);
                });
    }

    // 문서를 삭제하는 메소드
    private void deleteDocument(String userId, String subCollectionName, String documentId) {
        db.collection("Users")
                .document(userId)
                .collection(subCollectionName)
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Document deleted: " + documentId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting document", e);
                });
    }

    private void CheckOutOfSafetyZone(double lat , double lng) {
        db.collection("Users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.contains("safe_latitude") && documentSnapshot.getDouble("safe_latitude") != 0 && documentSnapshot.getLong("radius") != 0 ) {
                        // 필드가 존재해야하고, 그 값이 초기값이 아닐 때,
                        safe_latitude = documentSnapshot.getDouble("safe_latitude");
                        safe_longitude = documentSnapshot.getDouble("safe_longitude");
                    }
                    if (documentSnapshot.getLong("radius") < Util.getDistance(safe_latitude, safe_longitude, lat, lng)) {
                        // 안전구역과의 거리가 안전구역의 반지름보다 클 경우 : 알림보내기
                        // 알림을 보내는 동작을 수행 (노약자 핸드폰의 Message 문서에 위치 정보 넣기)
                        double outrange = Util.getDistance(safe_latitude, safe_longitude, lat, lng) - documentSnapshot.getLong("radius");
                        Log.d("LocationService", "안전구역을 " + outrange + "M 벗어났습니다");

                        //message 문서 내용
                        Map<String, Object> newUser = new HashMap<>();
                        // 현재 시간을 얻어오기
                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        newUser.put("check", "0");
                        newUser.put("content", "안전구역을 " + outrange + "M 벗어났습니다");
                        int month = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더해줍니다.
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        newUser.put("month", month);
                        newUser.put("day", day);
                        newUser.put("hour", hour);
                        newUser.put("minute", minute);
                        newUser.put("title", "위치 알림");

                        // 컬렉션("users")에 문서 추가
                        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                        String who = preferences.getString("who", "");

                        db.collection("Users").document(who).collection("message")
                                .add(newUser)
                                .addOnSuccessListener(documentReference -> Log.d("LocationService", "알림이 전송되었습니다."));
                    } else
                        Log.d("LocationService", "안전구역 안에 있습니다.");
                });
    }
}




