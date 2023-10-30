package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstonedesign.old_man.OldMainActivity;

public class oldWarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_warning);

        Button checkButton = findViewById(R.id.checkButton);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭 이벤트 발생 시 oldMainActivity로 이동


                Intent intent = new Intent(oldWarningActivity.this, OldMainActivity.class);
                startActivity(intent);
            }
        });


    }
}