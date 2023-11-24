package com.oldcare.capstonedesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class StepCounterActivity extends AppCompatActivity {
    private TextView tvSteps;
    ArrayList<Integer> jsonList = new ArrayList<>(); // ArrayList 선언
    ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
    BarChart barChart;
    int lastValue;
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

        CardView stepGoal = findViewById(R.id.cardView5);
        stepGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStepGoalDialog();
            }
        });
    }

    private void showStepGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("어르신의 핸드폰에 표시될 일일 걸음 목표를 작성해주세요.");

        // EditText를 생성하고 AlertDialog에 추가
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // 확인 버튼 설정
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String stepGoal = input.getText().toString().trim();
                updateStepGoal(stepGoal);
            }
        });

        // 취소 버튼 설정
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateStepGoal(String stepGoal) {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");
        // Firestore의 Users 컬렉션에서 현재 사용자의 문서를 가져오기
        DocumentReference userRef = db.collection("Users").document(uid);

        // stepGoal 필드를 업데이트
        userRef
                .update("stepGoal", stepGoal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StepCounterActivity.this, "설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StepCounterActivity.this, "설정에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    public void graphInitSetting(){

        Log.e("aaaaa", "문서 가져옴2");

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String oldMan = preferences.getString("oldMan", "");
        // Firestore 쿼리
        db.collection("Users")
                .document(oldMan)
                .collection("steps")
                .limit(7)
                .get()
                .addOnCompleteListener(task -> {
                    Log.e("aaaaa", "문서 가져옴1");

                    if (task.isSuccessful()) {
                        Log.e("aaaaa", "문서 가져옴4");

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            // 문서명에서 끝 두 글자를 labelList에 추가
                            String documentName = document.getId();
                            String label = documentName.substring(Math.max(0, documentName.length() - 2)) + "일";
                            labelList.add(label);

                            Log.e("aaaaa", "문서 가져옴");


                            // step 필드의 값을 jsonList에 추가 (소수점 이하 제거하고 정수로 변경)
                            Long step = document.getLong("step");
                            double stepValue = (step != null) ? step.intValue() : 0;
                            jsonList.add((int) stepValue);
                            tvSteps.setText(String.format("%.0f", stepValue));
                        }

                        Log.e("aaaaa", "문서 가져옴5");

                        TextView totalStepTextView = findViewById(R.id.totalStepTextView);
                        TextView goalTextView = findViewById(R.id.goalTextView);
                        // jsonList에 있는 값들의 평균 계산
                        double sum = 0;
                        for (Number value : jsonList) {
                            sum += value.doubleValue();
                        }
                        double average = sum / jsonList.size();

                        // 평균값을 totalStepTextView에 설정
                        totalStepTextView.setText(String.format("%.0f", average) + " 걸음"); // 소수점 2자리까지 표시

                        // jsonList이 비어있지 않은 경우
                        if (!jsonList.isEmpty()) {
                            lastValue = jsonList.get(jsonList.size() - 1).intValue();

                            // 마지막 값이 10000 이상인 경우 "성공" 아니면 "미달성" 설정
                            String goalResult = (lastValue >= 10000) ? "달성" : "미달성";

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

//        barChart.setRendererLeftYAxis();
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

        TextView rankTextView = findViewById(R.id.rankTextView);

        // count 변수에 있는 숫자
        int count = lastValue; // count 변수를 적절히 초기화하세요
        // Firestore에서 Users 컬렉션의 모든 문서를 가져옴
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    AtomicInteger greaterCountDocuments = new AtomicInteger();
                    greaterCountDocuments.getAndIncrement();

                    for (QueryDocumentSnapshot userDocument : queryDocumentSnapshots) {
                        // 각 유저 문서에서 steps 서브컬렉션의 가장 최근 문서를 가져옴
                        db.collection("Users")
                                .document(userDocument.getId())
                                .collection("steps")
                                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(stepsSnapshots -> {
                                    if (!stepsSnapshots.isEmpty()) {
                                        // steps 서브컬렉션에 최근 문서가 존재하는 경우
                                        QueryDocumentSnapshot stepDocument = (QueryDocumentSnapshot) stepsSnapshots.getDocuments().get(0);
                                        Long stepValue = stepDocument.getLong("step");

                                        if (stepValue != null && stepValue > count) {
                                            // count보다 큰 값이 있는 경우
                                            greaterCountDocuments.getAndIncrement();
                                            Log.d("Firestore", "User " + userDocument.getId() + " has a step count greater than " + count);
                                        }
                                        rankTextView.setText(greaterCountDocuments.toString() + "등");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // 실패 처리
                                    Log.e("Firestore", "Error getting steps document: " + e.getMessage());
                                });
                    }

                    // greaterCountDocuments에는 count보다 큰 값을 가진 문서의 개수가 저장됨
                    Log.d("Firestore", "Total documents with steps greater than " + count + ": " + greaterCountDocuments);
                })
                .addOnFailureListener(e -> {
                    // 실패 처리
                    Log.e("Firestore", "Error getting user documents: " + e.getMessage());
                });



    }

}
