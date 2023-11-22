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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MasterStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_start);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextInputEditText masterStartEditText = findViewById(R.id.masterStartText);
        TextInputEditText masterStartEditText2 = findViewById(R.id.passwordText);

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

                                    String masterNumber =  Objects.requireNonNull(masterStartEditText.getText()).toString();
                                    String masterNumber2 =  Objects.requireNonNull(masterStartEditText2.getText()).toString();


                                    // 'who' 필드에 'admin', 'masterNumber' 필드에 randomString 값을 설정
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("who", "admin");
                                    updates.put("masterNumber", masterNumber);
                                    updates.put("oldNumber", masterNumber2);

                                    userRef.update(updates)
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
                                                    // 업데이트 실패 처리
                                                    Toast.makeText(MasterStartActivity.this, "업데이트를 실패했습니다.", Toast.LENGTH_SHORT).show();
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