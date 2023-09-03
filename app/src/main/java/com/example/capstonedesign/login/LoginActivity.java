package com.example.capstonedesign.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstonedesign.MainActivity;
import com.example.capstonedesign.R;
import com.google.android.gms.common.SignInButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase 인증 객체 가져오기
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // 현재 사용자 확인
        FirebaseUser currentUser = auth.getCurrentUser();

        // 사용자가 로그인한 경우에만 MainActivity로 이동
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

            emailEditText = findViewById(R.id.emailEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            signUpButton = findViewById(R.id.signUpButton);
            signInButton = findViewById(R.id.signInButton);

            // 구글 로그인 버튼 클릭 이벤트 핸들러
            SignInButton googleSignInButton = findViewById(R.id.btn_google_sign_in);
            googleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Firebase 구글 로그인 흐름 시작
                    Intent signInIntent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build();

                    startActivityForResult(signInIntent, RC_SIGN_IN); // RC_SIGN_IN은 상수로 정의해야 합니다.
                }
            });


            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                        if (currentUser != null) {
                                            String userId = currentUser.getUid();

                                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> task) {
                                                    if (task.isSuccessful()) {
                                                        String token = task.getResult();

                                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                                                        userRef.child("userId").setValue(userId);
                                                        userRef.child("username").setValue(email);
                                                        userRef.child("fcmToken").setValue(token);

                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "FCM 토큰을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        // 오류 처리
                                        String errorMessage = task.getException().getMessage();

                                        // Firebase Authentication 오류 메시지를 분석하여 Toast로 표시
                                        if (errorMessage.contains("There is no user record corresponding to this identifier")) {
                                            Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        } else if (errorMessage.contains("The password is invalid")) {
                                            Toast.makeText(LoginActivity.this, "잘못된 비밀번호입니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "로그인에 실패했습니다. 오류: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // 구글 로그인 성공
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // 이제 로그인한 사용자 정보를 활용할 수 있습니다.
                // 예: user.getDisplayName(), user.getEmail() 등

                // 로그인 성공 후 MainActivity로 이동하는 코드를 추가하세요.
                Toast.makeText(this, "구글 아이디로 로그인 하였습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // 구글 로그인 실패 또는 사용자가 취소한 경우
                if (response == null) {
                    Toast.makeText(this, "구글 로그인이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "구글 로그인 실패: " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}