package com.oldcare.capstonedesign.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.oldcare.capstonedesign.LoadingActivity;
import com.oldcare.capstonedesign.R;
import com.google.android.gms.common.SignInButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private TextInputEditText emailText;
    private TextInputEditText passwordText;
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

        // 사용자가 로그인이 된 경우에만 MainActivity로 이동
        if (currentUser != null) {
            Intent intent = new Intent(this, LoadingActivity.class);
            startActivity(intent);
            finish();
        } else {

            emailText = findViewById(R.id.emailText);
            passwordText = findViewById(R.id.passwordText);
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

            //회원가입 버튼
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();

                }
            });

            //로그인 버튼
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = Objects.requireNonNull(emailText.getText()).toString();
                    String password = Objects.requireNonNull(passwordText.getText()).toString();


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

                                                        Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
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

        //onCreate
    }

    //구글 로그인
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("Users").document(currentUser.getUid());

                    // 사용자 정보를 가져오기
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // 이미 사용자 정보가 Firestore에 있음
                                    // MainActivity로 이동
                                    Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Firestore에 사용자 정보가 없음
                                    // 정보를 저장하고 MainActivity로 이동
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("nickname", "긴급 상황에 확인할 수 있도록 중요한 정보들을 모두 적어주세요. 작성한 정보는 어르신의 핸드폰에 표시 됩니다. ex) 어르신 혈액형 : AB");
                                    //userInfo.put("추가정보", "추가정보");


                                    userRef.set(userInfo, SetOptions.merge())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // 사용자 정보 저장 성공
                                                        Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        // 사용자 정보 저장 실패 처리
                                                        // 오류 메시지를 표시하거나 다른 조치를 취할 수 있음
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // Firestore에서 정보 가져오기 실패 처리
                                // 오류 메시지를 표시하거나 다른 조치를 취할 수 있음
                            }
                        }
                    });
                }
            }
        }
    }


}
