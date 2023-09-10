package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class MedicineActivity extends AppCompatActivity {

    private String time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);



        Button timeButton = findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MedicineActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_setting, null);

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
                                int hour = countdownHourPicker.getValue();
                                int minute = countdownMinutePicker.getValue();

                                // 시간과 분을 문자열로 변환
                                String hourString = String.format("%02d", hour);
                                String minuteString = String.format("%02d", minute);

                                time = hourString + "시 " + minuteString + "분";
                                timeButton.setText(time);
                            }

                        })
                        .setNegativeButton("취소", null)
                        .show();

            }
        });




    }
}