package com.example.capstonedesign.old_man;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstonedesign.R;
import com.example.capstonedesign.StepCounterActivity;
import com.example.capstonedesign.location.LocationActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class OldMainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable periodicTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_main);

        //설정창 가기
        oldSetting();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");
        CollectionReference collectionReference = db.collection("Users").document(uid).collection("message");

        Button oldMapButton =  findViewById(R.id.oldMapButton);
        oldMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OldMainActivity.this, LocationActivity.class);
                startActivity(intent);
            }
        });

        Button workButton =  findViewById(R.id.workButton);
        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OldMainActivity.this, StepCounterActivity.class);
                startActivity(intent);
            }
        });

        // 색인 조건 설정
        Query query = collectionReference
                .orderBy("day", Query.Direction.ASCENDING)
                .orderBy("hour", Query.Direction.ASCENDING)
                .orderBy("minute", Query.Direction.ASCENDING)
                .limit(3); // 상위 3개 문서만 가져오기

        // Runnable을 정의하여 주기적으로 실행할 작업을 구현합니다.
        periodicTask = new Runnable() {
            @Override
            public void run() {
                // 주기적으로 호출하려는 함수를 여기에 호출
                memoLoad(query);

                // 3초마다 실행하려면 3000 밀리초(3초) 지연 시간을 설정
                handler.postDelayed(this, 2000);
            }
        };

        // 처음에 한 번 호출하고, 그 후 3초마다 실행
        handler.post(periodicTask);

        Button yesButton1 =  findViewById(R.id.yesButton1);
        yesButton1.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                      query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                          // 문서 가져오기 성공 시
                          List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                          if (!documents.isEmpty()) {
                              // 가져온 문서 중 첫 번째 문서 삭제
                              DocumentSnapshot firstDocument = documents.get(0);
                              firstDocument.getReference().delete()
                                      .addOnSuccessListener(aVoid -> {
                                          // 삭제 성공 시 실행할 코드
                                          Toast.makeText(OldMainActivity.this, "확인되었습니다.", Toast.LENGTH_SHORT).show();
                                          Intent intent = new Intent(OldMainActivity.this, OldMainActivity.class);
                                          startActivity(intent);
                                          finish();
                                      });
                          } else {
                              // 가져온 문서가 없을 경우 처리
                              Log.d("Firestore", "문서가 없습니다.");
                          }
                      });

                  }
              }
        );

        Button yesButton2 =  findViewById(R.id.yesButton2);
        yesButton2.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                                                  // 문서 가져오기 성공 시
                                                  List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                                  if (!documents.isEmpty()) {
                                                      // 가져온 문서 중 첫 번째 문서 삭제
                                                      DocumentSnapshot firstDocument = documents.get(1);
                                                      firstDocument.getReference().delete()
                                                              .addOnSuccessListener(aVoid -> {
                                                                  // 삭제 성공 시 실행할 코드
                                                                  Toast.makeText(OldMainActivity.this, "확인되었습니다.", Toast.LENGTH_SHORT).show();
                                                                  Intent intent = new Intent(OldMainActivity.this, OldMainActivity.class);
                                                                  startActivity(intent);
                                                                  finish();
                                                              });
                                                  } else {
                                                      // 가져온 문서가 없을 경우 처리
                                                      Log.d("Firestore", "문서가 없습니다.");
                                                  }
                                              });

                                          }
                                      }
        );

        Button yesButton3 =  findViewById(R.id.yesButton3);
        yesButton3.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                                                  // 문서 가져오기 성공 시
                                                  List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                                  if (!documents.isEmpty()) {
                                                      // 가져온 문서 중 첫 번째 문서 삭제
                                                      DocumentSnapshot firstDocument = documents.get(2);
                                                      firstDocument.getReference().delete()
                                                              .addOnSuccessListener(aVoid -> {
                                                                  // 삭제 성공 시 실행할 코드
                                                                  Toast.makeText(OldMainActivity.this, "확인되었습니다.", Toast.LENGTH_SHORT).show();
                                                                  Intent intent = new Intent(OldMainActivity.this, OldMainActivity.class);
                                                                  startActivity(intent);
                                                                  finish();
                                                              });
                                                  } else {
                                                      // 가져온 문서가 없을 경우 처리
                                                      Log.d("Firestore", "문서가 없습니다.");
                                                  }
                                              });

                                          }
                                      }
        );



//여기까지 onCreate
    }



    //메모 가져오기
    private void memoLoad(Query query) {
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        if (documents.isEmpty()) {
                            // 문서가 없을 경우 처리
                            Log.d("Firestore", "문서가 없습니다.");
                            return;
                        }

                        // 상위 3개의 문서 또는 모든 문서 가져오기
                        List<DocumentSnapshot> resultDocuments;
                        if (documents.size() >= 3) {
                            resultDocuments = documents.subList(0, 3); // 상위 3개만 가져오기
                        } else {
                            resultDocuments = documents; // 모든 문서 가져오기
                        }


                        for (int i = 1; i <= resultDocuments.size(); i++) {
                            // 문서 데이터 사용
                            Long day = resultDocuments.get(i - 1).getLong("day");
                            String dayString = (day != null) ? String.valueOf(day) : "";

                            Long hour = resultDocuments.get(i - 1).getLong("hour");
                            String hourString = (hour != null) ? String.valueOf(hour) : "";

                            Long minute = resultDocuments.get(i - 1).getLong("minute");
                            String minuteString = (minute != null) ? String.valueOf(minute) : "";

                            String title = resultDocuments.get(i - 1).getString("title");
                            String titleString = (title != null) ? title : "";

                            String content = resultDocuments.get(i - 1).getString("content");
                            String contentString = (content != null) ? content : "";


                            String daytime = "";
                            if (dayString.equals("0")) {
                                daytime = "오늘";
                            } else if (dayString.equals("1")) {
                                daytime = "내일";
                            }  else if (dayString.equals("2")) {
                                daytime = "모래";
                            } else if (dayString.equals("3")) {
                                daytime = "사흘";
                            } else if (dayString.equals("4")) {
                                daytime = "나흘";
                            }

                            String time = daytime + " " + hourString + "시 " + minuteString + "분";

                            // 해당 순서의 TextView를 업데이트
                            int titleTextViewId = getResources().getIdentifier("titleTextView" + i, "id", getPackageName());
                            TextView titleTextView = findViewById(titleTextViewId);
                            if (titleTextView != null) {
                                titleTextView.setText(titleString);
                            } else {
                                titleTextView.setText("");
                            }
                            int timeTextViewId = getResources().getIdentifier("timeTextView" + i, "id", getPackageName());
                            TextView timeTextView = findViewById(timeTextViewId);
                            if (timeTextView != null) {
                                timeTextView.setText(time);
                            }else {
                                titleTextView.setText("");
                            }
                            int contentTextViewId = getResources().getIdentifier("contentTextView" + i, "id", getPackageName());
                            TextView contentTextView = findViewById(contentTextViewId);
                            if (contentTextView != null) {
                                contentTextView.setText(contentString);
                            }else {
                                titleTextView.setText("");
                            }
                            int yesButtonId = getResources().getIdentifier("yesButton" + i, "id", getPackageName());
                            Button yesButton = findViewById(yesButtonId);
                            if (hourString.equals("")) {
                                yesButton.setVisibility(View.INVISIBLE);
                            }else {
                                yesButton.setVisibility(View.VISIBLE);
                            }
                            int backgroundTextViewId = getResources().getIdentifier("backgroundTextView" + i, "id", getPackageName());
                            TextView backgroundTextView = findViewById(backgroundTextViewId);
                            if (hourString.equals("")) {
                                backgroundTextView.setVisibility(View.INVISIBLE);
                            }else {
                                backgroundTextView.setVisibility(View.VISIBLE);
                            }


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 오류 처리
                        Log.e("Firestore", "데이터 가져오기 실패", e);
                    }
                });
    }


    //설정창 가는 함수
    private void oldSetting() {
        Button oldSettingButton = findViewById(R.id.oldSettingButton);
        oldSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // OldSettingActivity를 시작
                Intent intent = new Intent(OldMainActivity.this, OldSettingActivity.class);
                startActivity(intent);
            }
        });
    }


}