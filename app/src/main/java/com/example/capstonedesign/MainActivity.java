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

        //fragment 등록
        SettingListener(); //리스너 등록
        bottomNavigationView.setSelectedItemId(R.id.item_main_fragment);
        //맨 처음 시작할 탭 설정

//onCreate
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

    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }


    private void init() {
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView = findViewById(R.id.bottomNavi);
    }



}
