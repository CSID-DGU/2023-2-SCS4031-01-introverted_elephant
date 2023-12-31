package com.oldcare.capstonedesign.old_man;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oldcare.capstonedesign.OldServiceActivity;
import com.oldcare.capstonedesign.R;
import com.oldcare.capstonedesign.WarningActivity;
import com.oldcare.capstonedesign.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class OldSettingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_setting);

        // view 변수를 현재 액티비티의 레이아웃으로 초기화
        View view = findViewById(android.R.id.content);

        signOut(view);

        //회원 탈퇴 버튼 클릭 시 회원 탈퇴
        deleteID(view);

        notification(view);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String who = preferences.getString("who", "");
        TextView information = findViewById(R.id.informationTextView);
        TextView masterNumberTextView = findViewById(R.id.masterNumberTextView);
        TextView oldNumberTextView = findViewById(R.id.oldNumberTextView);

        // who는 문서 ID로 가정
        db.collection("Users").document(who).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        String masterNumber = documentSnapshot.getString("masterNumber");
                        String oldNumber = documentSnapshot.getString("oldNumber");

                        if (nickname != null) {
                            // "nickname" 필드의 값이 존재하는 경우 처리
                            information.setText(nickname.toString());
                        }
                        if (masterNumber != null) {
                            // "nickname" 필드의 값이 존재하는 경우 처리
                            masterNumberTextView.setText(masterNumber.toString());
                        }
                        if (oldNumber != null) {
                            // "nickname" 필드의 값이 존재하는 경우 처리
                            oldNumberTextView.setText(oldNumber.toString());
                        }
                    }
                });



        //이용 약관
        Chip warningChip = view.findViewById(R.id.oldWarningChip);
        warningChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OldSettingActivity.this, WarningActivity.class);
                startActivity(intent);
            }
        });


    }

    private void notification(View view) {
        Chip finishChip = view.findViewById(R.id.finishChip);
        finishChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OldSettingActivity.this, OldServiceActivity.class);
                intent.putExtra("key", "second");
                startActivity(intent);
                finish();
            }
        });
    }


    //로그아웃 함수
    private void signOut(@NonNull View view) {
        // 로그아웃 버튼을 찾아서 클릭 리스너를 설정합니다.
        Chip oldSignOutChip = view.findViewById(R.id.oldSignOutChip);

        oldSignOutChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase에서 로그아웃
                FirebaseAuth.getInstance().signOut();

                // 로그인 화면으로 이동
                Intent intent = new Intent(OldSettingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // 현재 액티비티를 종료하여 뒤로 가기 버튼으로 돌아갈 수 없게 함

            }
        });
    }

    //회원 탈퇴 버튼 함수
    private void deleteID(View view) {
        mAuth = FirebaseAuth.getInstance();
        Chip oldDeleteIDChip = view.findViewById(R.id.oldDeleteIDChip);

        oldDeleteIDChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    // 회원 탈퇴 함수
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // getActivity() 대신 this 사용
        builder.setMessage("계정을 삭제하시겠습니까? 삭제 후에는 복구할 수 없습니다.")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자가 예를 선택한 경우, 계정 삭제 작업 수행
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            currentUser.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // 탈퇴 성공
                                                mAuth.signOut(); // 로그아웃 처리
                                                Toast.makeText(OldSettingActivity.this, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                                                // 로그인 화면으로 이동
                                                Intent intent = new Intent(OldSettingActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish(); // 현재 액티비티를 종료하여 뒤로 가기 버튼으로 돌아갈 수 없게 함
                                            } else {
                                                // 탈퇴 실패
                                                // 오류 처리를 수행하거나 사용자에게 알림을 제공
                                                if (task.getException().getMessage().contains("requires recent authentication")) {
                                                    // "This operation is sensitive and requires recent authentication" 오류 발생 시
                                                    Toast.makeText(OldSettingActivity.this, "로그인이 오래되었습니다. 재 로그인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // 다른 오류인 경우
                                                    Toast.makeText(OldSettingActivity.this, "계정 삭제 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자가 아니오를 선택한 경우, 아무 작업도 수행하지 않음
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }



}