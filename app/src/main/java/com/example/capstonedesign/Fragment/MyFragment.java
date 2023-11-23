package com.example.capstonedesign.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstonedesign.MasterServiceActivity;
import com.example.capstonedesign.R;
import com.example.capstonedesign.login.LoginActivity;
import com.example.capstonedesign.old_man.OldSettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyFragment extends Fragment {

    private Button deleteIDChip;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences preferences;
    private TextView nicknameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        //로그아웃 버튼 클릭 시 로그아웃
        signOut(view);

        //회원 탈퇴 버튼 클릭 시 회원 탈퇴
        deleteID(view);

        //닉네임 관련 함수
        nicknameEvent(view);


        //이용 약관
        Chip signOutChip = view.findViewById(R.id.warningChip);
        signOutChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //백그라운드 설정
        Chip stopServiceChip = view.findViewById(R.id.stopServiceChip);
        stopServiceChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MasterServiceActivity.class);
                intent.putExtra("key", "second");
                startActivity(intent);
            }
        });


        return view;
        //여기까지 oncreateview
    }

    //닉네임 불러오고 변경하는 함수
    private void nicknameEvent(View view) {
        //닉네임 불러오기
        TextView nicknameTextView = view.findViewById(R.id.nicknameTextView);
        Context context = getActivity();
        SharedPreferences preferences = context.getSharedPreferences("user_preferences", MODE_PRIVATE);
        String nickName = preferences.getString("nickName", "임시 닉네임");
        nicknameTextView.setText(nickName);

        Chip nicknameChangeChip = view.findViewById(R.id.nicknameChangeChip);
        nicknameChangeChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getActivity());
                editText.setHint("혈액형과 같이 중요한 정보를 작성해주세요. 어르신의 핸드폰에 표시됩니다.");

                // AlertDialog 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("어르신 정보 변경");
                builder.setView(editText);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 입력된 텍스트를 사용하여 원하는 작업 수행
                        String enteredText = editText.getText().toString();

                        // 현재 사용자 가져오기
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            // 파이어스토어 인스턴스 가져오기
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            // 현재 사용자의 UID 가져오기
                            String uid = currentUser.getUid();
                            // Users 컬렉션에서 현재 사용자의 UID로 된 문서 업데이트
                            DocumentReference userRef = db.collection("Users").document(uid);
                            // 업데이트할 데이터 생성
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("nickname", enteredText);

                            // 문서 업데이트
                            userRef.update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 업데이트 성공 시 작업 수행
                                            SharedPreferences preferences = getActivity().getSharedPreferences("user_preferences", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("nickName", enteredText);
                                            editor.apply();

                                            Toast.makeText(getActivity(), "어르신 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                                            nicknameTextView.setText(enteredText);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
                builder.setNegativeButton("취소", null);

                // 다이얼로그 표시
                builder.create().show();
            }
        });
    }

    //로그아웃 함수
    private void signOut(@NonNull View view) {
        // 로그아웃 버튼을 찾아서 클릭 리스너를 설정합니다.
        Chip signOutChip = view.findViewById(R.id.signOutChip);

        signOutChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase에서 로그아웃
                FirebaseAuth.getInstance().signOut();

                // 로그인 화면으로 이동
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish(); // 현재 액티비티를 종료하여 뒤로 가기 버튼으로 돌아갈 수 없게 함
            }
        });
    }

    //회원 탈퇴 버튼 함수
    private void deleteID(View view) {
        mAuth = FirebaseAuth.getInstance();
        deleteIDChip = view.findViewById(R.id.deleteIDChip);

        deleteIDChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    //회원 탈퇴 함수
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                                Toast.makeText(getActivity(), "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                                                // 로그인 화면으로 이동
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                startActivity(intent);
                                                getActivity().finish(); // 현재 액티비티를 종료하여 뒤로 가기 버튼으로 돌아갈 수 없게 함
                                            } else {
                                                // 탈퇴 실패
                                                // 오류 처리를 수행하거나 사용자에게 알림을 제공
                                                if (task.getException().getMessage().contains("requires recent authentication")) {
                                                    // "This operation is sensitive and requires recent authentication" 오류 발생 시
                                                    Toast.makeText(getActivity(), "로그인이 오래되었습니다. 재 로그인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // 다른 오류인 경우
                                                    Toast.makeText(getActivity(), "계정 삭제 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
