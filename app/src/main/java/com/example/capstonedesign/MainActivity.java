package com.example.capstonedesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
        SettingListener(); //리스너 등록

        userInformation(); //사용자 정보

        //맨 처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.item_main_fragment);

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
                                    // Firestore에서 닉네임 필드 가져오기
                                    String userNickname = Objects.requireNonNull(document.getString("nickname"));

                                    // SharedPreferences에 닉네임 저장
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("nickName", userNickname);
                                    editor.apply();
                                } else {
                                    // 문서가 없거나 문서 내에 'nickname' 필드가 없을 경우 처리
                                    Toast.makeText(MainActivity.this, "사용자 닉네임을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Firestore에서 데이터를 가져오지 못한 경우 처리
                                Toast.makeText(MainActivity.this, "Firestore에서 사용자 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
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
