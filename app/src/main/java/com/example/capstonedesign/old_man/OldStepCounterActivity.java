package com.example.capstonedesign.old_man;

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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OldStepCounterActivity extends AppCompatActivity {
    private TextView tvSteps;
    private StepReceiver stepReceiver;
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
        setContentView(R.layout.activity_old_step_counter);

        tvSteps = findViewById(R.id.tvSteps);

        stepReceiver = new StepReceiver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startStepCounterService();
        }


        barChart = (BarChart) findViewById(R.id.fragment_bluetooth_chat_barchart);
        graphInitSetting();       //그래프 기본 세팅

        BarChartGraph(labelList, jsonList);
        barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
//        barChart.getAxisRight().setAxisMaxValue(500);
//        barChart.getAxisLeft().setAxisMaxValue(500);

        masterGoal();


        // count 변수에 있는 숫자
        int count = 33; // count 변수를 적절히 초기화하세요
        TextView rankTextView = findViewById(R.id.rankTextView);

// Firestore에서 Users 컬렉션의 모든 문서를 가져옴
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    AtomicInteger greaterCountDocuments = new AtomicInteger();

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

    private void masterGoal() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String who = preferences.getString("who", "");
        TextView masterGoal = findViewById(R.id.masterGoalTextView);
        db.collection("Users")
                .document(who)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 문서가 존재하는 경우
                        String stepGoal = documentSnapshot.getString("stepGoal");
                        if (stepGoal != null) {
                            // stepGoal 값이 존재하는 경우
                            // stepGoal 값 사용
                            masterGoal.setText(stepGoal);
                            Log.d("Firestore", "stepGoal: " + stepGoal);
                        } else {
                            // stepGoal 값이 존재하지 않는 경우
                            Log.d("Firestore", "stepGoal is not available");
                        }
                    } else {
                        // 문서가 존재하지 않는 경우
                        Log.d("Firestore", "Document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    // 실패 처리
                    Log.e("Firestore", "Error getting document: " + e.getMessage());
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(stepReceiver, new IntentFilter("com.example.stepcountapp.STEP_UPDATE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(stepReceiver);
    }

    private class StepReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            double steps = intent.getIntExtra("steps", 0);
            tvSteps.setText(String.format("%.0f", steps));

            SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            String uid = preferences.getString("uid", "");

            // 컬렉션, 문서, 필드 생성 및 업데이트
            DocumentReference userStepsRef = db.collection("Users").document(uid).collection("steps").document(currentDate);

                userStepsRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // 이미 해당 날짜의 문서가 존재하면 업데이트
                            userStepsRef.update("step", steps)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 업데이트 성공
                                            System.out.println("Document updated successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            // 업데이트 실패
                                            System.out.println("Error updating document: " + e);
                                        }
                                    });
                        } else {
                            // 해당 날짜의 문서가 없으면 새로운 문서 생성
                            Map<String, Object> data = new HashMap<>();
                            data.put("step", steps);

                            userStepsRef.set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 저장 성공
                                            System.out.println("Document saved successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            // 저장 실패
                                            System.out.println("Error saving document: " + e);
                                        }
                                    });
                        }
                    }
                });
            }

        }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startStepCounterService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        startService(serviceIntent);
        Log.d("StepCounterActivity", "Service started");
    }

    private void stopStepCounterService() {

    }

    public void graphInitSetting(){

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");

        db.collection("Users")
                .document(uid)
                .collection("steps")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        int documentCount = task1.getResult().size();
                        if (documentCount >= 8) {
                            // 문서가 8개 이상인 경우, 첫 번째 문서 삭제
                            List<DocumentSnapshot> documents = task1.getResult().getDocuments();
                            DocumentSnapshot firstDocument = documents.get(0);
                            firstDocument.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // 삭제 성공
                                        Log.d("Firestore", "First document deleted successfully");
                                        // Firestore 쿼리
                                        db.collection("Users")
                                                .document(uid)
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
                                                            tvSteps.setText(String.format("%.0f", stepValue));
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
                                                        if (!Double.isNaN(average)) {
                                                            totalStepTextView.setText(String.format("%.0f", average) + " 걸음");
                                                        } else {
                                                            totalStepTextView.setText("0 걸음");
                                                        }

                                                        // jsonList이 비어있지 않은 경우
                                                        if (!jsonList.isEmpty()) {
                                                            int lastValue = jsonList.get(jsonList.size() - 1).intValue();

                                                            // 마지막 값이 10000 이상인 경우 "성공" 아니면 "미달성" 설정
                                                            String goalResult = (lastValue >= 10000) ? "달성" : "미달성";

                                                            // goalTextView에 결과 설정
                                                            goalTextView.setText(goalResult);
                                                        } else {
                                                            // jsonList가 비어있는 경우에 대한 처리
                                                            goalTextView.setText("미달성");
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

                                    })
                                    .addOnFailureListener(e -> {
                                        // 삭제 실패
                                        Log.e("Firestore", "Error deleting first document: " + e.getMessage());
                                    });
                        } else {

                            db.collection("Users")
                                    .document(uid)
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
                                                tvSteps.setText(String.format("%.0f", stepValue));
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
                                            if (!Double.isNaN(average)) {
                                                totalStepTextView.setText(String.format("%.0f", average) + " 걸음");
                                            } else {
                                                totalStepTextView.setText("0 걸음");
                                            }

                                            // jsonList이 비어있지 않은 경우
                                            if (!jsonList.isEmpty()) {
                                                int lastValue = jsonList.get(jsonList.size() - 1).intValue();

                                                // 마지막 값이 10000 이상인 경우 "성공" 아니면 "미달성" 설정
                                                String goalResult = (lastValue >= 10000) ? "달성" : "미달성";

                                                // goalTextView에 결과 설정
                                                goalTextView.setText(goalResult);
                                            } else {
                                                // jsonList가 비어있는 경우에 대한 처리
                                                goalTextView.setText("미달성");
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

                        }
                    } else {
                        // Firestore에서 문서 가져오기 실패
                        Exception exception = task1.getException();
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
