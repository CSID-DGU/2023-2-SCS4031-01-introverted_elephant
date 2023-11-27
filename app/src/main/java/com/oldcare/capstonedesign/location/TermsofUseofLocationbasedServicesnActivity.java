package com.oldcare.capstonedesign.location;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;
import com.oldcare.capstonedesign.R;

import java.util.HashMap;
import java.util.Map;

public class TermsofUseofLocationbasedServicesnActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsof_useof_locationbased_servicesn);
        Button consentButton = findViewById(R.id.consentButton);

        db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        uid = preferences.getString("uid", "");

        consentButton.setOnClickListener(v -> {
            showConfirmationDialog(true);  // 동의 선택
        });

        // "거부" 버튼 클릭 시
        Button rejectButton = findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(v -> {
            showConfirmationDialog(false);  // 거부 선택
        });
    }
    // 동의/거부를 선택하는 다이얼로그 표시
    private void showConfirmationDialog(final boolean consent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("동의 여부 확인");
        builder.setMessage("위치 데이터를 사용하여 어르신 경로 확인 및 복지 서비스 제공에 동의하십니까?");
        builder.setPositiveButton("동의", (dialog, which) -> {
            if (consent) {
                // 사용자가 동의한 경우의 처리
                // 위치 데이터 사용 시작 등
                Map<String, Object> updates = new HashMap<>();
                updates.put("agree", 1);
                db.collection("Users")
                        .document(uid)
                        .update(updates)
                        .addOnSuccessListener(aVoid -> {
                            // 업데이트 성공 시 실행할 코드
                            Log.d("Firestore", "업데이트 성공");
                        })
                        .addOnFailureListener(e -> {
                            // 업데이트 실패 시 처리
                            Log.e("Firestore", "업데이트 실패", e);
                        });
                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
            } else {
                // 사용자가 거부한 경우의 처리
                // 특별한 조치나 위치 데이터 사용을 중지하는 등의 처리
                finish(); // 현재 액티비티 종료
            }
        });
        builder.setNegativeButton("거부", (dialog, which) -> {
            // 사용자가 거부한 경우의 처리
            // 특별한 조치나 위치 데이터 사용을 중지하는 등의 처리
            onBackPressed();
        });
        builder.show();
    }
}