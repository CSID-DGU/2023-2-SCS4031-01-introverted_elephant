package com.example.capstonedesign.old_man;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstonedesign.R;

public class OldMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_main);

        //설정창 가기
        oldSetting();

//여기까지 onCreate
    }



    //설정창 가는 함수
    private void oldSetting() {
        Button oldSettingButton = findViewById(R.id.oldSettingButton);
        oldSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // OldSettingActivity를 시작
                Intent intent = new Intent(OldMainActivity.this, OldSettingActivity.class);
                startActivity(intent);
            }
        });
    }


}