package com.example.capstonedesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MedicineActivity extends AppCompatActivity {

    private String time = "";
    private int hour = 12;
    private int minute = 30;
    private String day = "";
    private int dayCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String title = preferences.getString("title", "");

        TextView memoTitleTextView = findViewById(R.id.memoTitleTextView);
        memoTitleTextView.setText(title);

        timeSettingButton();

        //전송 버튼
        yesButton(title);


    }

    //전송 버튼 함수
    private void yesButton(String title) {
        Button yesButton = findViewById(R.id.medicineYesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                String oldMan = preferences.getString("oldMan", "");

                TextInputEditText memoTextInputEditText = findViewById(R.id.memoTextInputEditText);
                String content = memoTextInputEditText.getText().toString();
                // 앞 30자만 가져오도록 처리
                if (content.length() > 30) {
                    content = content.substring(0, 30);
                }


                //message 문서 내용
                Map<String, Object> newUser = new HashMap<>();
                newUser.put("check", "0");
                newUser.put("content", content);
                newUser.put("day", dayCount);
                newUser.put("hour", hour);
                newUser.put("minute", minute);
//                newUser.put("repeat", 1);
                newUser.put("title", title);

// 컬렉션("users")에 문서 추가
                db.collection("Users").document(oldMan).collection("message")
                        .add(newUser)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(MedicineActivity.this, "알림이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

            }
        });
    }

    private void timeSettingButton() {
        Button timeButton = findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MedicineActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_setting, null);

                NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
                String[] days = new String[]{"오늘", "내일", "모래", "사흘", "나흘"};
                dayPicker.setMinValue(0);
                dayPicker.setMaxValue(days.length - 1);
                dayPicker.setDisplayedValues(days);
                dayPicker.setValue(0); // 기본 선택값

                NumberPicker countdownHourPicker = dialogView.findViewById(R.id.countdownHourPicker);
                countdownHourPicker.setMaxValue(24);
                countdownHourPicker.setMinValue(0);
                countdownHourPicker.setValue(12);

                NumberPicker countdownMinutePicker = dialogView.findViewById(R.id.countdownMinutePicker);
                countdownMinutePicker.setMaxValue(59);
                countdownMinutePicker.setMinValue(0);
                countdownMinutePicker.setValue(30);

                builder.setTitle("시간을 설정해주세요")
                        .setView(dialogView)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hour = countdownHourPicker.getValue();
                                minute = countdownMinutePicker.getValue();
                                day = days[dayPicker.getValue()];
                                // 시간과 분을 문자열로 변환
                                String hourString = String.format("%02d", hour);
                                String minuteString = String.format("%02d", minute);

                                if (day.equals("오늘")) {
                                    dayCount = 0;
                                } else if (day.equals("내일")) {
                                    dayCount = 1;
                                } else if (day.equals("모래")) {
                                    dayCount = 2;
                                } else if (day.equals("사흘")) {
                                    dayCount = 3;
                                } else if (day.equals("나흘")) {
                                    dayCount = 4;
                                }

                                time = day + " " + hourString + "시 " + minuteString + "분";
                                timeButton.setText(time);
                            }

                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

            }
        });
    }
}