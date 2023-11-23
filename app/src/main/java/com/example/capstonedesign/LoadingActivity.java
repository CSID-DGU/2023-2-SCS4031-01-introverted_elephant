package com.example.capstonedesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.capstonedesign.old_man.OldMainActivity;
import com.example.capstonedesign.service.FirestoreNotificationService;
import com.example.capstonedesign.service.FirestoreNotificationService2;
import com.example.capstonedesign.start.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoadingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        userInformation();

    }

    private void userInformation() {
        // Firebase 인증 및 Firestore 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // SharedPreferences 초기화
        preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);

        // 현재 로그인한 사용자 가져오기
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // 사용자의 UID 가져오기
            String uid = currentUser.getUid();

            // Firestore에서 해당 UID의 사용자 닉네임 가져오기
            db.collection("Users").document(uid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    if (document.contains("who")) {
                                        // 'who' 필드가 있는 경우

                                        String who = Objects.requireNonNull(document.getString("who"));

                                        //admin이 아니면 노약자 페이지로
                                        if (!who.equals("admin")) {

                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("uid", uid);
                                            editor.putString("who", who);
                                            editor.apply();

                                            Intent intent = new Intent(LoadingActivity.this, OldMainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            //알림 서비스 킴
//                                            Intent serviceIntent = new Intent(LoadingActivity.this, FirestoreNotificationService2.class);
//                                            startService(serviceIntent);

                                            // 보호자 필드들 가져오기
                                            String userNickname = Objects.requireNonNull(document.getString("nickname"));
                                            String oldMan = document.getString("oldMan");
                                            if (oldMan == null) {
                                                oldMan = "";
                                            }

                                            // SharedPreferences에 값들 저장
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("nickName", userNickname);
                                            editor.putString("who", who);
                                            editor.putString("oldMan", oldMan);
                                            editor.putString("uid", uid);
                                            editor.apply();

                                            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } else {
                                        // 'who' 필드가 없는 경우 초기 화면으로
                                        Intent intent = new Intent(LoadingActivity.this, StartActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }


                                }
                            }
                        }
                    });


        }
    }




}