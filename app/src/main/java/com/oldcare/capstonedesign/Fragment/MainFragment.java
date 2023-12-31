package com.oldcare.capstonedesign.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.oldcare.capstonedesign.NotificationActivity;
import com.oldcare.capstonedesign.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("user_preferences", MODE_PRIVATE);

        FloatingActionButton medicineButton = view.findViewById(R.id.medicineButton);
        medicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "투약 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);

            }
        });

        FloatingActionButton foodButton = view.findViewById(R.id.foodButton);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "식사 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton hospitalButton = view.findViewById(R.id.hospitalButton);
        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "병원 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton workButton = view.findViewById(R.id.workButton);
        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "할일 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton exerciseButton = view.findViewById(R.id.exerciseButton);
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "운동 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton institutionButton = view.findViewById(R.id.institutionButton);
        institutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "기관 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton familyButton = view.findViewById(R.id.familyButton);
        familyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "가족 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton carButton = view.findViewById(R.id.carButton);
        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "이동 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton etcButton = view.findViewById(R.id.etcButton);
        etcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "기타 알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        Button warningButton = view.findViewById(R.id.warningButton);
        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 경고 메시지 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("어르신 안부 확인하기");
                builder.setMessage("어르신의 핸드폰으로 안부 확인 알림을 전송하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // SharedPreferences에 값들 저장
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String oldMan = preferences.getString("oldMan", "");

                        // message 문서 내용
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("title", "경고");

                        // 컬렉션("users")에 문서 추가
                        db.collection("Users")
                                .document(oldMan)
                                .collection("message")
                                .document("1") // 원하는 문서 이름 "1"을 설정
                                .set(newUser) // 데이터 설정
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "안부확인 알림이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });




        return view;
        //여기까지 oncreateview
    }
}