package com.example.capstonedesign;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.capstonedesign.Fragment.CommunityFragment;
import com.example.capstonedesign.Fragment.MainFragment;
import com.example.capstonedesign.Fragment.MyFragment;
import com.example.capstonedesign.start.MasterStartActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MasterLoadingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences preferences;
    BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    FrameLayout main_frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_loading);


        init(); //객체 정의

        //fragment 등록
        SettingListener(); //리스너 등록
        bottomNavigationView.setSelectedItemId(R.id.item_loading_fragment);
        //맨 처음 시작할 탭 설정


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        // Firestore 문서 감시
        db.collection("Users").document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Log.d(TAG, "Current data: " + documentSnapshot.getData());

                            // 'oldMan' 필드의 존재 여부 확인
                            if (documentSnapshot.contains("oldMan")) {
                                // 'oldMan' 필드가 존재하는 경우
                                // 특정 엑티비티로 이동
                                Log.d(TAG, "oldMan 필드가 추가됨");
                                Intent intent = new Intent(MasterLoadingActivity.this, LoadingActivity.class);
                                startActivity(intent);
                                finish(); // 현재 엑티비티를 종료하려면
                            } else {
                                // 'oldMan' 필드가 존재하지 않는 경우
                                // 다른 작업 수행
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });


    }

    //네비게이션 바
    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int itemId = menuItem.getItemId(); // 선택한 메뉴 아이템의 ID를 가져옵니다.

            if (itemId == R.id.item_loading_fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new MasterLoadingFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.item_main_fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new MasterAlarmFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.item_community_fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new MasterCommunityFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.item_my_fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, new MasterSettingFragment())
                        .commit();
                return true;
            }

            // 나머지 경우에 대한 처리를 여기에 추가합니다.

            return false;
        }
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }


    private void init() {
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView = findViewById(R.id.bottomNavi);
    }
}