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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MasterStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_start);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText masterStartEditText = findViewById(R.id.masterStartEditText);
        Button masterStartYesButton = findViewById(R.id.masterStartYesButton);
        masterStartYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 확인 버튼을 눌렀을 때 실행할 동작을 여기에 추가
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    // Firestore에서 해당 UID의 사용자 문서 가져오기
                    DocumentReference userRef = db.collection("Users").document(uid);
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {

                                    String masterNumber = masterStartEditText.getText().toString();

                                    // 'who' 필드에 'admin' 값을 설정
                                    userRef.update("who", "admin")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // 'settingNumber' 필드에 randomString 값을 설정
                                                    userRef.update("masterNumber", masterNumber)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(MasterStartActivity.this, "보호자로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(MasterStartActivity.this, LoadingActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // 'settingNumber' 필드 설정 실패 처리
                                                                    Toast.makeText(MasterStartActivity.this, "settingNumber 값을 설정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // 'who' 필드 설정 실패 처리
                                                    Toast.makeText(MasterStartActivity.this, "보호자 설정을 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            }
                        }
                    });
                }

            }
        });




    }
}