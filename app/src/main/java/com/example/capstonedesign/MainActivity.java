package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로그아웃
        setupSignOutButton();
    }

    //로그아웃 함수
    private void setupSignOutButton() {
        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase에서 로그아웃
                FirebaseAuth.getInstance().signOut();

                // 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티를 종료하여 뒤로 가기 버튼으로 돌아갈 수 없게 함
            }
        });
    }



}
