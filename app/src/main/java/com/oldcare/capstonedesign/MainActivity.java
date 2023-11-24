package com.oldcare.capstonedesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.oldcare.capstonedesign.Fragment.CommunityFragment;
import com.oldcare.capstonedesign.Fragment.MainFragment;
import com.oldcare.capstonedesign.Fragment.MyFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    FrameLayout main_frame;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");

        // Users 컬렉션의 uid 문서의 message 컬렉션에 대한 참조
        CollectionReference messageCollectionRef = db.collection("Users").document(uid).collection("message");

// message 컬렉션에서 문서 가져오기
        messageCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 문서가 존재하는지 확인
                if (!task.getResult().isEmpty()) {
                    // 문서가 존재하면 모든 문서를 삭제
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection("Users").document(uid).collection("message").document(document.getId()).delete();
                    }
                }
                // 문서가 없거나 message 컬렉션이 없는 경우
                // 여기에 해당 경우에 대한 로직을 추가할 수 있습니다.
            } else {
                // Firestore에서 문서 가져오기 실패
                Exception exception = task.getException();
                if (exception != null) {
                    // 오류 처리
                }
            }
        });
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
