package com.oldcare.capstonedesign.start;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.oldcare.capstonedesign.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //어르신 버튼을 눌렀을 때
        ImageButton oldButton = findViewById(R.id.oldButton);
        oldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("어르신으로 시작하시겠습니까?")
                        .setMessage("보호자에 의해 관리를 받을 수 있습니다. 신중히 선택해주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 확인 버튼을 눌렀을 때 실행할 동작을 여기에 추가
                                Intent intent = new Intent(StartActivity.this, OldStartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 취소 버튼을 눌렀을 때 실행할 동작을 여기에 추가
                            }
                        })
                        .show();

            }
        });

        //보호자 버튼을 눌렀을 때
        ImageButton adminButton = findViewById(R.id.adminButton);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("보호자로 시작하시겠습니까?")
                        .setMessage("어르신 계정을 관리할 수 있습니다. 신중히 선택해주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(StartActivity.this, MasterStartActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 취소 버튼을 눌렀을 때 실행할 동작을 여기에 추가
                            }
                        })
                        .show();



            }
        });





    }
}