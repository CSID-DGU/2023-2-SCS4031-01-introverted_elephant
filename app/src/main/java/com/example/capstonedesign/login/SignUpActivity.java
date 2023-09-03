package com.example.capstonedesign.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstonedesign.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText signUpEmailEditText = findViewById(R.id.signUpEmailEditText);
        EditText signUpPasswordEditText = findViewById(R.id.signUpPasswordEditText);
        EditText signUpPasswordCheckEditText = findViewById(R.id.signUpPasswordCheckEditText);
        Button realSignUpButton = findViewById(R.id.realSignUpButton);

        realSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signUpEmailEditText.getText().toString();
                String password = signUpPasswordEditText.getText().toString();
                String passwordCheck = signUpPasswordCheckEditText.getText().toString();

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
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);

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