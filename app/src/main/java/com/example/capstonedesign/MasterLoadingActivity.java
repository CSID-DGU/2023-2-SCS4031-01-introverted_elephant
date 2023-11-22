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
import com.example.capstonedesign.start.MasterStartActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Firestore에서 문서 가져오기
        db.collection("Users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // 문서가 존재하는 경우
                        if (documentSnapshot.exists()) {
                            // 'oldMan' 필드의 존재 여부 확인
                            if (documentSnapshot.contains("oldMan")) {
                                // 'oldMan' 필드가 존재하는 경우
                                // 특정 엑티비티로 이동
                                Intent intent = new Intent(MasterLoadingActivity.this, LoadingActivity.class);
                                startActivity(intent);
                                finish(); // 현재 엑티비티를 종료하려면
                            } else {
                                // 'oldMan' 필드가 존재하지 않는 경우
                                // 다른 작업 수행
                            }
                        } else {
                            // 문서가 존재하지 않는 경우
                            // 다른 작업 수행
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 실패 처리
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
                setContentView(R.layout.fragment_master_loading);
                return true;
            } else if (itemId == R.id.item_main_fragment) {
                setContentView(R.layout.fragment_master_alarm);
                return true;
            } else if (itemId == R.id.item_community_fragment) {
                setContentView(R.layout.fragment_master_community);
                return true;
            } else if (itemId == R.id.item_my_fragment) {
                setContentView(R.layout.fragment_master_setting);
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