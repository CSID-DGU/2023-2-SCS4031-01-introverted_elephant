package com.example.capstonedesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.capstonedesign.Fragment.CommunityFragment;
import com.example.capstonedesign.Fragment.MainFragment;
import com.example.capstonedesign.Fragment.MyFragment;
import com.example.capstonedesign.old_man.OldMainActivity;
import com.example.capstonedesign.service.FirestoreNotificationService;
import com.example.capstonedesign.service.FirestoreNotificationService2;
import com.example.capstonedesign.start.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    FrameLayout main_frame;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); //객체 정의

        userInformation(); //사용자 정보

        //맨 처음 시작할 탭 설정

//onCreate
    }

    //사용자 정보 가져오기
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
                                            //알림 서비스 킴
                                            Intent serviceIntent = new Intent(MainActivity.this, FirestoreNotificationService.class);
                                            startService(serviceIntent);

                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("uid", uid);
                                            editor.putString("who", who);
                                            editor.apply();

                                            Intent intent = new Intent(MainActivity.this, OldMainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            //알림 서비스 킴
                                            Intent serviceIntent = new Intent(MainActivity.this, FirestoreNotificationService2.class);
                                            startService(serviceIntent);

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

                                        }

                                    } else {
                                        // 'who' 필드가 없는 경우 초기 화면으로
                                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }



                                    //fragment 등록
                                    SettingListener(); //리스너 등록
                                    bottomNavigationView.setSelectedItemId(R.id.item_main_fragment);

                                }
                            }
                        }
                    });


        }
    }


    //네비게이션 바
    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int itemId = menuItem.getItemId(); // 선택한 메뉴 아이템의 ID를 가져옵니다.

            if (itemId == R.id.item_main_fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new MainFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.item_community_fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new CommunityFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.item_my_fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new MyFragment())
                        .commit();
                return true;
            }
            // 나머지 경우에 대한 처리를 여기에 추가합니다.

            return false;
        }
    }

    private void init() {
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView = findViewById(R.id.bottomNavi);
    }
    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }





}
