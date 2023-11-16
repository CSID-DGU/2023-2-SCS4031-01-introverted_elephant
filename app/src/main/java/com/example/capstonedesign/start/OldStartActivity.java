package com.example.capstonedesign.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstonedesign.LoadingActivity;
import com.example.capstonedesign.MainActivity;
import com.example.capstonedesign.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class OldStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_start);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText numberCheckEditText = findViewById(R.id.numberCheckEditText);
        Button numberCheckButton = findViewById(R.id.numberCheckButton);

        // numberCheckButton 클릭 이벤트 핸들러
        numberCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String masterNumber = numberCheckEditText.getText().toString();

                // Firestore에서 "Users" 컬렉션에서 "who" 필드 값이 documentName과 일치하는 문서 검색
                db.collection("Users")
                        .whereEqualTo("masterNumber", masterNumber)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        // 조건에 해당하는 문서가 있을 경우
                                        // 첫 번째 문서의 이름을 가져옴
                                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                        String whoValue = document.getId();

                                        // 현재 로그인한 사용자의 UID 가져오기
                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                        if (currentUser != null) {
                                            String uid = currentUser.getUid();

                                            // Firestore에서 현재 사용자의 UID 문서의 "who" 필드에 값을 설정
                                            db.collection("Users").document(uid).update("who", whoValue)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            db.collection("Users").document(whoValue).update("oldMan", uid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    db.collection("Users").document(whoValue).update("masterNumber", masterNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            Map<String, Object> messageCollection = new HashMap<>();
                                                                            db.collection("Users").document(uid)
                                                                                    .collection("message") // "message" 컬렉션을 "whoValue" 문서 내에 추가
                                                                                    .document("anyDocument") // 빈 문서 추가
                                                                                    .set(messageCollection) // 빈 문서에 데이터 추가
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Toast.makeText(OldStartActivity.this, "설정되었습니다.", Toast.LENGTH_SHORT).show();
                                                                                            Intent intent = new Intent(OldStartActivity.this, LoadingActivity.class);
                                                                                            startActivity(intent);
                                                                                            finish();
                                                                                        }
                                                                                    });

                                                                        }
                                                                    });

                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(OldStartActivity.this, "설정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(OldStartActivity.this, "해당 문서를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(OldStartActivity.this, "문서 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



    }
}