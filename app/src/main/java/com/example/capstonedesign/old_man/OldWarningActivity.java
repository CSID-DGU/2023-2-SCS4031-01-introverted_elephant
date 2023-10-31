package com.example.capstonedesign.old_man;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.capstonedesign.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OldWarningActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_warning);
        db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");

        Button checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭 이벤트 발생 시 oldMainActivity로 이동
                Log.d("121212", "클릭 성공");

// Firestore 쿼리를 사용하여 문서를 "timestamp" 필드로 정렬
                db.collection("Users")
                        .document(uid)
                        .collection("message")
//                        .orderBy("timestamp", Query.Direction.DESCENDING) // "timestamp" 필드를 내림차순으로 정렬
                        .limit(1) // 최근 1개 문서만 가져옴
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                // 최근 문서를 가져옴
                                DocumentSnapshot latestDocument = queryDocumentSnapshots.getDocuments().get(0);
                                Log.d("121212", "문서 가져옴");

                                // 문서를 삭제
                                String documentId = latestDocument.getId();
                                db.collection("Users")
                                        .document(uid)
                                        .collection("message")
                                        .document(documentId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // 삭제 성공
                                            SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                                            String who = preferences.getString("who", "");

                                            Map<String, Object> newDocument = new HashMap<>();
                                            newDocument.put("title", "확인");

                                            db.collection("Users").document(who)
                                                    .collection("message")
                                                    .add(newDocument)
                                                    .addOnSuccessListener(documentReference -> {
                                                        // 문서 추가 성공
                                                        Log.d("Firestore", "New document added with ID: " + documentReference.getId());
                                                        Intent intent = new Intent(OldWarningActivity.this, OldMainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        Log.d("121212", "삭제 성공");
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // 문서 추가 실패
                                                        Log.e("Firestore", "Error adding document: " + e.getMessage());
                                                    });

                                        })
                                        .addOnFailureListener(e -> {
                                            // 삭제 실패
                                            Log.d("121212", "삭제 실패");
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // 쿼리 실패
                            Log.d("121212", "쿼리 실패");
                        });


            }
        });


    }
}