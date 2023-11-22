package com.example.capstonedesign.location;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.capstonedesign.R;

import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private ViewGroup mapViewContainer;

    private final String BASE_URL = "https://dapi.kakao.com/";

    private String REST_API_KEY = "KakaoAK "+ "0cf15acd27d8221047379b612be7c6ab"; // REST API 키

    private DatabaseReference mDatabase;

    public long RegionCode_B;
    public List<Institution> agencyList = new ArrayList<>();

    private static final int REQUEST_CODE_OTHER_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        // initMapView();

        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        // 중심점 설정
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        // 줌 레벨 변경
        mapView.setZoomLevel(1, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);

        /*마커 추가*/
        MapPoint MARKER_POINT1 = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        // 마커 아이콘 추가하는 함수
        MapPOIItem marker1 = new MapPOIItem();
        // 클릭 했을 때 나오는 호출 값
        marker1.setItemName("현재 위치");
        // 마커 태그 : 태그로 구분하기 위한 거
        marker1.setTag(0);
        // 좌표를 입력받아 현 위치로 출력
        marker1.setMapPoint(MARKER_POINT1);

        // (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
        marker1.setMarkerType(MapPOIItem.MarkerType.BluePin);

        // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker1.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
        mapView.addPOIItem(marker1);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        geoinfo(longitude,latitude);
        Button nearcenter = findViewById(R.id.nearcenter);
        nearcenter.setOnClickListener(view -> showcenter(agencyList));
        Button centerlist = findViewById(R.id.center_list);
        centerlist.setOnClickListener(view -> movecenterlist(agencyList));
        Button setsafety = findViewById(R.id.setsafetyzone);
        setsafety.setOnClickListener(view -> movesetsaftyzone());

    } // End of onCreate

    protected void onRestart() {
        super.onRestart();

        if (mapViewContainer != null && mapViewContainer.indexOfChild(mapView) != -1) {
            try {
                mapViewContainer.addView(mapView);
                //initMapView();
            } catch (RuntimeException re) {
                Log.e("maperror", "RuntimeException occurred", re);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 다른 액티비티에서 돌아왔을 때 호출되는 콜백 메서드
        if (requestCode == REQUEST_CODE_OTHER_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                // 다른 액티비티에서 OK 결과를 받았을 때 처리할 작업 수행
                // 예: 지도 업데이트 등
                updateMapView();
            } else if (resultCode == RESULT_CANCELED) {
                // 다른 액티비티에서 취소되었거나 오류가 있을 때 처리할 작업 수행
                // 예: 사용자가 뒤로가기 버튼을 눌렀을 때
                handleCancelOperation();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapViewContainer.getChildCount() == 0) {
            mapView = new MapView(this);
            mapViewContainer.addView(mapView);
        }

    }

    private void updateMapView() {
        // 지도 업데이트 등의 작업을 수행
    }

    private void handleCancelOperation() {
        // 사용자가 뒤로가기로 돌아왔을 때 실행되는 코드
        // 예: 취소 메시지 표시, 아무 작업도 수행하지 않기 등
        mapViewContainer.addView(mapView);
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
    }

    private void geoinfo(double lng, double lat) {
        Retrofit retrofit = new Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KakaoAPI_geocoder api = retrofit.create(KakaoAPI_geocoder.class); // 통신 인터페이스를 객체로 생성
        Call<KakaoApiResponse_geocoder> call = api.getRegionInfo(REST_API_KEY, lng,lat); // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(new Callback<KakaoApiResponse_geocoder>() {
            @Override
            public void onResponse(Call<KakaoApiResponse_geocoder> call, Response<KakaoApiResponse_geocoder> response) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                if (response.isSuccessful() && response.body() != null) {
                    // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                    KakaoApiResponse_geocoder kakaoApiResponseGeocoder = response.body();
                    Log.d("Test", "Raw: " + response.raw());

                    // 예시: 결과 데이터 출력
                    List<KakaoApiResponse_geocoder.Document> documents = kakaoApiResponseGeocoder.getDocuments();
                    StringBuilder resultText = new StringBuilder();
                    for (KakaoApiResponse_geocoder.Document document : documents) {
                        if ("B".equals(document.getRegion_type())) {
                            // Rest api 로그 출력 테스트용 코드
                            resultText.append("Code_5digits: " + document.getCode().substring(0,5) + "00000" + "\n");
                            RegionCode_B = Long.parseLong(document.getCode().substring(0,5) + "00000");
                            Log.d("좌표test", "좌표 : " + RegionCode_B);
                            // firebase Query
                            Query locationquery = mDatabase.child("Comtcadministcode").orderByChild("Comtcadministcode").equalTo(RegionCode_B);
                            locationquery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot lawSnapshot : dataSnapshot.getChildren()) {
                                        // "법정동" 아래의 데이터 가져오기
                                        String lawName = lawSnapshot.child("Areaname").getValue(String.class);
                                        long lawCode = lawSnapshot.child("district_code").getValue(long.class);

                                        // 가져온 데이터 출력
                                        Log.d("Firebase", "법정동명: " + lawName);
                                        Log.d("Firebase", "법정동코드: " + lawCode);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    DatabaseError databaseError = null;
                                    Log.e("Firebase", "loadPost:onCancelled", databaseError.toException());
                                }
                            });
                            Query centerfinder = mDatabase.child("WelfareAgency").orderByChild("district_code").equalTo(RegionCode_B);
                            centerfinder.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot lawSnapshot : snapshot.getChildren()) {
                                        Institution agency = lawSnapshot.getValue(Institution.class);
                                        if (agency != null) {
                                            agencyList.add(agency);
                                        }
                                    }
                                    // "복지기관" 아래의 데이터 가져오기
                                    for (Institution agency : agencyList) {
                                        Log.d("Firebase","복지기관 : " + agency .toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    DatabaseError databaseError = null;
                                    Log.e("Firebase.centerfinder", "loadPost:onCancelled", databaseError.toException());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<KakaoApiResponse_geocoder> call, Throwable t) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: " + t.getMessage());
            }
        }); //  api 응답시 행동

    } // End of geoinfo

    public void showcenter(@NonNull List<Institution> Agencylist){
        for (Institution agency : Agencylist) {
            if (Agencylist == null){
                Log.i("buttonwork","데이터가 없습니다.");
            }
            Log.d("buttonwork","복지기관 : " + agency.toString());
        }
        for(Institution data : Agencylist){
            /*마커 추가*/
            MapPOIItem marker = new MapPOIItem();
            // 클릭 했을 때 나오는 호출 값
            marker.setItemName(data.name);
            // 좌표를 입력받아 현 위치로 출력
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude));
            //  (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
            mapView.addPOIItem(marker);
        }
    }

    public void movecenterlist(List<Institution> Agencylist){
        // MapActivity 호출
        Intent intent = new Intent(MapActivity.this, CenterActivity.class);
        intent.putExtra("centers", (Serializable) Agencylist);
        startActivity(intent);
    }

    public void movesetsaftyzone(){
        mapViewContainer.removeView(mapView);
        Intent intent = new Intent(MapActivity.this, SetSafetyZoneActivity.class);
        startActivity(intent);
    }

    private void initMapView() {
        mapView = new MapView(this);
        mapViewContainer = new RelativeLayout(this);
        mapViewContainer.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(mapViewContainer);
        mapViewContainer.addView(mapView);
        // 춤 레벨 변경
        mapView.setZoomLevel(1, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);
    }

} // End of Activity