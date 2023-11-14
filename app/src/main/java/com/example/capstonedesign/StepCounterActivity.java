package com.example.capstonedesign;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.capstonedesign.R;
import com.example.capstonedesign.service.StepCounterService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StepCounterActivity extends AppCompatActivity {
    private TextView tvSteps;
    ArrayList<Integer> jsonList = new ArrayList<>(); // ArrayList 선언
    ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
    BarChart barChart;
    TextView minuteTextview;
    private String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        tvSteps = findViewById(R.id.tvSteps);


        barChart = (BarChart) findViewById(R.id.fragment_bluetooth_chat_barchart);
        graphInitSetting();       //그래프 기본 세팅

        BarChartGraph(labelList, jsonList);
        barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
//        barChart.getAxisRight().setAxisMaxValue(500);
//        barChart.getAxisLeft().setAxisMaxValue(500);
    }




    public void graphInitSetting(){

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String oldMan = preferences.getString("oldMan", "");
        // Firestore 쿼리
        db.collection("Users")
                .document(oldMan)
                .collection("steps")
                .limit(7)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 문서명에서 끝 두 글자를 labelList에 추가
                            String documentName = document.getId();
                            String label = documentName.substring(Math.max(0, documentName.length() - 2)) + "일";
                            labelList.add(label);

                            // step 필드의 값을 jsonList에 추가 (소수점 이하 제거하고 정수로 변경)
                            Long step = document.getLong("step");
                            double stepValue = (step != null) ? step.intValue() : 0;
                            jsonList.add((int) stepValue);
                            tvSteps.setText("오늘 걸음 수 : " + String.format("%.0f", stepValue)+ " 걸음");
                        }

                        TextView totalStepTextView = findViewById(R.id.totalStepTextView);
                        TextView goalTextView = findViewById(R.id.goalTextView);
                        // jsonList에 있는 값들의 평균 계산
                        double sum = 0;
                        for (Number value : jsonList) {
                            sum += value.doubleValue();
                        }
                        double average = sum / jsonList.size();

                        // 평균값을 totalStepTextView에 설정
                        totalStepTextView.setText("일주일 평균 걸음 수 : " + String.format("%.0f", average) + " 걸음"); // 소수점 2자리까지 표시

                        // jsonList이 비어있지 않은 경우
                        if (!jsonList.isEmpty()) {
                            int lastValue = jsonList.get(jsonList.size() - 1).intValue();

                            // 마지막 값이 10000 이상인 경우 "성공" 아니면 "미달성" 설정
                            String goalResult = (lastValue >= 10000) ? "하루 만 보 걷기 : 달성" : "하루 만 보 걷기 : 미달성";

                            // goalTextView에 결과 설정
                            goalTextView.setText(goalResult);
                        } else {
                            // jsonList가 비어있는 경우에 대한 처리
                            goalTextView.setText("데이터 없음");
                        }



                        // 문서가 7개 미만이면 나머지 자리에 "x"와 0 추가
                        int remaining = 7 - labelList.size();
                        for (int i = 0; i < remaining; i++) {
                            labelList.add("-");
                            jsonList.add(0);
                        }



                        BarChartGraph(labelList, jsonList);
                        barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
                        barChart.setAutoScaleMinMaxEnabled(true);

                        // 여기에서 labelList와 jsonList를 사용할 수 있습니다.
                        // 예를 들어, 그래프에 데이터를 설정하는 등의 용도로 활용할 수 있습니다.
                    } else {
                        // Firestore에서 문서 가져오기 실패
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("Firestore", "Error getting documents: " + exception.getMessage());
                        }
                    }
                });




        //barChart.setRendererLeftYAxis();
//        barChart.setMaxVisibleValueCount(50);
//        barChart.setTop(50);
//        barChart.setBottom(0);
//        barChart.getAxisRight().setAxisMaxValue(80);
//        barChart.getAxisLeft().setAxisMaxValue(500);
    }

    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {
        // BarChart 메소드


        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry((Integer) valList.get(i), i));
        }

        BarDataSet depenses = new BarDataSet(entries, "일일 걸음 수"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        barChart.setDescription(" ");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        BarData data = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS); //

        barChart.setData(data);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();
    }

}
