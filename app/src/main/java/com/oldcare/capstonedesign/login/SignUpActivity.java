package com.oldcare.capstonedesign.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.oldcare.capstonedesign.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextInputEditText signUpEmailEditText = findViewById(R.id.emailText);
        TextInputEditText signUpPasswordEditText = findViewById(R.id.passwordText);
        TextInputEditText signUpPasswordCheckEditText = findViewById(R.id.repeatPasswordText);
        Button realSignUpButton = findViewById(R.id.realSignUpButton);

        realSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(signUpEmailEditText.getText()).toString();
                String password = Objects.requireNonNull(signUpPasswordEditText.getText()).toString();
                String passwordCheck = Objects.requireNonNull(signUpPasswordCheckEditText.getText()).toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordCheck) ) {
                    Toast.makeText(SignUpActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 회원가입 성공
                                    Toast.makeText(SignUpActivity.this, "회원가입에 성공했습니다. 자동으로 로그인됩니다.", Toast.LENGTH_LONG).show();
                                    // Firebase Firestore에 사용자 정보 저장
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (currentUser != null) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference userRef = db.collection("Users").document(currentUser.getUid());

                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("nickname", "긴급 상황에 확인할 수 있도록 중요한 정보들을 모두 적어주세요. 작성한 정보는 어르신의 핸드폰에 표시 됩니다. ex) 어르신 혈액형 : AB");
                                        userInfo.put("safe_latitude", 0);
                                        userInfo.put("safe_longitude", 0);
                                        userInfo.put("radius", 0);
                                        userInfo.put("agree", 0);

                                        userRef.set(userInfo, SetOptions.merge()); // SetOptions.merge()를 사용하여 문서를 덮어쓰지 않고 업데이트
                                    }

                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // 회원가입 실패, 오류 메시지 출력
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(SignUpActivity.this, "회원가입에 실패했습니다. 이메일 양식에 맞는 지 확인해주세요." + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}