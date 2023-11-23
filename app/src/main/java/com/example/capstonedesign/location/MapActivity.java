package com.example.capstonedesign.location;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import com.example.capstonedesign.R;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private DatabaseReference realtimeDatabase;
    private String oldman_uid;
    private double latitude,longitude;
    private TextView textView;
    private String currentlocation;
    private String Latest_time;
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private final String BASE_URL = "https://dapi.kakao.com/";
    private final String REST_API_KEY = "KakaoAK 0cf15acd27d8221047379b612be7c6ab"; // REST API 키
    public long RegionCode_B;
    public List<Institution> agencyList = new ArrayList<>();
    public List<Hospital> HPList = new ArrayList<>();
    public List<LocationData> locationDataList = new ArrayList<>();
    private static final int REQUEST_CODE_OTHER_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate","현재 onCreate 호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        oldman_uid = preferences.getString("oldMan", "");
        firestore = FirebaseFirestore.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance().getReference();
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);

        Button nearcenter = findViewById(R.id.nearcenter);
        nearcenter.setOnClickListener(view -> showcenter(agencyList,HPList));

        Button centerlist = findViewById(R.id.center_list);
        centerlist.setOnClickListener(view -> movecenterlist(agencyList,HPList));

        Button setsafety = findViewById(R.id.setsafetyzone);
        setsafety.setOnClickListener(view -> movesetsaftyzone());

        Button checkpath = findViewById(R.id.check_path);
        checkpath.setOnClickListener(view -> getpath());

        // getlatestlocation() 함수의 결과를 기다리고 완료 후에 다음 함수들을 호출
        getlatestlocation(() -> {
            // getlatestlocation() 함수가 완료된 후에 호출될 부분
            initMapView();
            chksafetyzone();
            MapPoint MARKER_POINT1 = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            MapPOIItem marker1 = new MapPOIItem(); // 마커 아이콘 추가하는 함수
            marker1.setItemName("현재 위치"); // 클릭 했을 때 나오는 호출 값
            if (mapView.findCircleByTag(0) != null)
                mapView.removePOIItem(mapView.findPOIItemByTag(0));
            marker1.setTag(0); // 마커 태그 : 태그로 구분하기 위한 거
            marker1.setMapPoint(MARKER_POINT1); // 좌표를 입력받아 현 위치로 출력
            marker1.setMarkerType(MapPOIItem.MarkerType.CustomImage); //  (클릭 전) 모양 색.
            marker1.setCustomImageResourceId(R.drawable.mapmarker_blue);
            marker1.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // (마커 클릭 후) 모양.
            marker1.setCustomSelectedImageResourceId(R.drawable.mapmarker_red);
            mapView.addPOIItem(marker1); // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
            textView = findViewById(R.id.locationtext);
            geoinfo(longitude, latitude);
            hpinfo(longitude, latitude);
            getAdress(longitude,latitude);

            textView.setOnClickListener(v -> {
                onTextViewClick(v);
            });
        });

    } // End of onCreate

    // XML에서 onClick으로 지정한 메서드
    public void onTextViewClick(View view) {
        // 클릭 시 동작할 내용을 여기에 작성
        getlatestlocation(() -> {
            initMapView();
            chksafetyzone();
            // getlatestlocation() 함수가 완료된 후에 호출될 부분
            MapPoint MARKER_POINT1 = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            MapPOIItem marker1 = new MapPOIItem(); // 마커 아이콘 추가하는 함수
            marker1.setItemName("현재 위치"); // 클릭 했을 때 나오는 호출 값
            if (mapView.findCircleByTag(0) != null)
                mapView.removePOIItem(mapView.findPOIItemByTag(0));
            marker1.setTag(0); // 마커 태그 : 태그로 구분하기 위한 거 태그 0 현재 위치
            marker1.setMapPoint(MARKER_POINT1); // 좌표를 입력받아 현 위치로 출력
            marker1.setMarkerType(MapPOIItem.MarkerType.CustomImage); //  (클릭 전) 모양 색.
            marker1.setCustomImageResourceId(R.drawable.mapmarker_blue);
            marker1.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // (마커 클릭 후) 모양.
            marker1.setCustomSelectedImageResourceId(R.drawable.mapmarker_red);
            mapView.addPOIItem(marker1); // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
            geoinfo(longitude, latitude);
            hpinfo(longitude, latitude);
            getAdress(longitude,latitude);
        });
    }
    private void initMapView() {
        Log.d("initmapView","현재initMapView호출");
        if (mapView == null) mapView = new MapView(this);
        if (mapViewContainer == null) {
            mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
            mapViewContainer.addView(mapView);
        } else if (mapViewContainer.getChildCount() == 0)
            mapViewContainer.addView(mapView);

        mapView.removeAllPOIItems();
        mapView.removeAllPolylines();
        mapView.removeAllCircles();
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true); // 카메라 이동
        mapView.setZoomLevel(1, true); // 춤 레벨 변경
        mapView.zoomIn(true); // 줌 인
        mapView.zoomOut(true); // 줌 아웃

    }

    interface OnLocationDataReadyListener {     // getlatestlocation() 함수의 결과를 기다리기 위한 인터페이스
        void onLocationDataReady();
    }

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
            Log.d("onResume", "현재 onresume호출");
            mapView = new MapView(this);
            if (mapViewContainer == null) {
                mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);
            } else mapViewContainer.addView(mapView);

    }

    private void updateMapView() {
        // 지도 업데이트 등의 작업을 수행
        Log.d("MapActivity : updateMapView","호출됨");
    }

    private void handleCancelOperation() {
        Log.d("handleCancelOperation","현재 handleCancelOperation 호출");
        // 사용자가 뒤로가기로 돌아왔을 때 실행되는 코드
        // 예: 취소 메시지 표시, 아무 작업도 수행하지 않기 등
        if (mapView == null) mapView = new MapView(this);
        if (mapViewContainer == null) {
            mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
            mapViewContainer.addView(mapView);
        } else mapViewContainer.addView(mapView);
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
                    agencyList = new ArrayList<>();
                    for (KakaoApiResponse_geocoder.Document document : documents) {
                        if ("B".equals(document.getRegion_type())) {
                            RegionCode_B = Long.parseLong(document.getCode().substring(0,5) + "00000");
                            Log.d("좌표test", "좌표 : " + RegionCode_B);
                            // firebase Query
                            Query locationquery = realtimeDatabase.child("Comtcadministcode").orderByChild("Comtcadministcode").equalTo(RegionCode_B);
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
                            Query centerfinder = realtimeDatabase.child("WelfareAgency").orderByChild("district_code").equalTo(RegionCode_B);
                            centerfinder.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot lawSnapshot : snapshot.getChildren()) {
                                        Institution agency = lawSnapshot.getValue(Institution.class);
                                        if (agency != null) {
                                            agencyList.add(agency);
                                        }
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

    private void hpinfo(double lng, double lat) {
        Retrofit retrofit = new Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KaKaoAPI_Category api = retrofit.create(KaKaoAPI_Category.class); // 통신 인터페이스를 객체로 생성
        Call<KakaoApiResponse_Category> call = api.getCategory(REST_API_KEY,"HP8",lng,lat,500); // 검색 조건 입력
        call.enqueue(new Callback<KakaoApiResponse_Category>(){

            @Override
            public void onResponse(@NonNull Call<KakaoApiResponse_Category> call, @NonNull Response<KakaoApiResponse_Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    KakaoApiResponse_Category KakaoApiResponse_Category = response.body();
                    Log.d("Test", "Raw: " + response.raw());
                    List<KakaoApiResponse_Category.Document> documents = KakaoApiResponse_Category.getDocuments();
                    HPList = new ArrayList<>();
                    for (KakaoApiResponse_Category.Document document : documents) {
                        if (documents!=null){
                            Hospital hp = new Hospital(document.getPlace_name(), document.getDistance(),
                                    document.getPlace_url(),
                                    document.getCategory_name(),
                                    document.getAddress_name(),
                                    document.getRoad_address_name(),
                                    document.getId(),
                                    document.getPhone(),
                                    document.getCategory_group_code(),
                                    document.getCategory_group_name(),
                                    document.getX(),
                                    document.getY() );
                            HPList.add(hp);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<KakaoApiResponse_Category> call, Throwable t) {
                Log.w("MapActivity", "통신 실패: " + t.getMessage());
            }
        });
    }

    private void getAdress(double lng, double lat) {
        Retrofit retrofit = new Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KaKaoAPI_Address api = retrofit.create(KaKaoAPI_Address.class); // 통신 인터페이스를 객체로 생성
        Call<KakaoApiResponse_Address> call = api.getAddress(REST_API_KEY,lng,lat); // 검색 조건 입력
        call.enqueue(new Callback<KakaoApiResponse_Address>(){

            @Override
            public void onResponse(@NonNull Call<KakaoApiResponse_Address> call, @NonNull Response<KakaoApiResponse_Address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    KakaoApiResponse_Address KakaoApiResponse_Address = response.body();
                    Log.d("Test", "Raw: " + response.raw());
                    List<KakaoApiResponse_Address.Document> documents = KakaoApiResponse_Address.getDocuments();
                    StringBuilder resulttext = new StringBuilder();
                    currentlocation = "";
                    for (KakaoApiResponse_Address.Document document : documents) {
                        if (document.getRoadAddress() == null){
                            resulttext.append("현재 위치: "+ document.getAddress().getAddressName()+"\n" + "누르면 현재 위치로 이동합니다.");
                        } else {
                            resulttext.append("현재 위치: "+ document.getRoadAddress().getAddressName()+"\n" + "누르면 현재 위치로 이동합니다.");
                        }
                    }
                    currentlocation = resulttext.toString();
                    textView.setText(currentlocation);
                }
            }
            @Override
            public void onFailure(Call<KakaoApiResponse_Address> call, Throwable t) {
                Log.w("MapActivity", "통신 실패: " + t.getMessage());
            }
        });
    }

    public void showcenter(@NonNull List<Institution> Agencylist, List<Hospital> HPlist2){
        for(Institution data : Agencylist) {
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(data.name);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker.setCustomImageResourceId(R.drawable.mapmarker_blue);
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker.setCustomSelectedImageResourceId(R.drawable.mapmarker_red);
            marker.setCustomImageAutoscale(false);
            marker.setCustomImageAnchor(0.5f, 1.0f);
            mapView.addPOIItem(marker);
        }

        for(Hospital data : HPlist2) {
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(data.getPlaceName());
            marker.setMapPoint( MapPoint.mapPointWithGeoCoord( Double.parseDouble( data.getY() ), Double.parseDouble( data.getX() ) ) );
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker.setCustomImageResourceId(R.drawable.mapmarker_blue);
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker.setCustomSelectedImageResourceId(R.drawable.mapmarker_red);
            marker.setCustomImageAutoscale(false);
            marker.setCustomImageAnchor(0.5f, 1.0f);
            mapView.addPOIItem(marker);

            for (Hospital hp : HPlist2) {
                if (HPlist2 == null)
                    Log.i("buttonwork","데이터가 없습니다.");
                else
                    Log.d("buttonwork","병원 : " + hp.toString());
            }
        }
    }

    public void movecenterlist(List<Institution> Agencylist, List<Hospital> HPList) {
        // centeractivity 호출
        Intent intent = new Intent(MapActivity.this, CenterActivity.class);
        intent.putExtra("centers", (Serializable) Agencylist);
        intent.putExtra("hospitals", (Serializable) HPList);
        startActivity(intent);
    }

    public void movesetsaftyzone() {
        mapViewContainer.removeView(mapView);
        Intent intent = new Intent(MapActivity.this, SetSafetyZoneActivity.class);
        startActivity(intent);
    }

    private void getlatestlocation(OnLocationDataReadyListener listener) {
        firestore.collection("Users")
                .document(oldman_uid)
                .collection("location_data")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> { // queryDocumentSnapshots에는 최신 문서 하나가 포함됩니다.
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot latestDocument = queryDocumentSnapshots.getDocuments().get(0); // 최신 문서에 대한 작업 수행

                        Map<String, Object> data = latestDocument.getData();
                        latitude = (double) data.get("latitude");
                        longitude = (double) data.get("longitude");
                        Timestamp firestoreTimestamp = (Timestamp) data.get("timestamp");
                        Date javaDate = firestoreTimestamp.toDate(); // Timestamp 값을 Java Date로 변환
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // SimpleDateFormat을 사용하여 원하는 형식으로 포맷팅
                        Latest_time = sdf.format(javaDate);
                        if (listener != null) {
                            listener.onLocationDataReady(); // getlatestlocation() 함수가 완료되었음을 알림
                        }
                    } else {
                        // 문서가 없는 경우에 대한 처리
                        Log.d("Firestore", "No documents found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting documents: " + e.getMessage());
                });
    }

    private void getpath() {
        firestore.collection("Users")
                .document(oldman_uid)
                .collection("location_data")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) //
                .get()
                .addOnSuccessListener(task -> {
                    long currentTimeMillis = System.currentTimeMillis();
                    for (QueryDocumentSnapshot documentSnapshot : task) {
                        // 각 문서에 대한 작업 수행
                        Map<String, Object> data = documentSnapshot.getData();

                        // 데이터 활용
                        double latitude = (double) data.get("latitude");
                        double longitude = (double) data.get("longitude");
                        Timestamp firestoreTimestamp = (Timestamp) data.get("timestamp"); // get Timestamp
                        Date javaDate = firestoreTimestamp.toDate(); // Transform Timestamp to Java Date
                        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 HH시 mm분"); // Formatting Using SimpleDateFormat
                        long timestampMillis = javaDate.getTime();
                        String formattedTimestamp = sdf.format(javaDate);

                        // 현재 시간과의 차이 계산 (milliseconds 단위)
                        long timeDifference = currentTimeMillis - timestampMillis;

//                        // 3시간 이내의 데이터만 리스트에 추가
//                        if (timeDifference <= (3 * 60 * 60 * 1000)) { // 시간 * 분 * 초 * ms milliseconds
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
//                            String formattedTimestamp2 = sdf.format(javaDate);
//                            LocationData locationData2 = new LocationData(latitude, longitude, formattedTimestamp2);
//                            locationDataList.add(locationData2);

                        // LocationData 객체 생성
                        LocationData locationData = new LocationData(latitude, longitude, formattedTimestamp);

                        // Add to List
                        locationDataList.add(locationData);
                        showpath(locationDataList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting documents: " + e.getMessage());
                });
    }

    private void showpath(List<LocationData> locationDataList) {
        MapPolyline path = new MapPolyline();
        MapPoint[] mapPoints = new MapPoint[locationDataList.size()];
        MapPOIItem[] routepoints = new MapPOIItem[locationDataList.size()];

        int idx = 0;
        for (LocationData locationData:locationDataList) {
            mapPoints[idx] = MapPoint.mapPointWithGeoCoord(locationData.getLatitude(),locationData.getLongitude());
            routepoints[idx] = new MapPOIItem();
            routepoints[idx].setItemName(locationData.formattedTimestamp);
            routepoints[idx].setMapPoint(mapPoints[idx]);
            routepoints[idx].setMarkerType(MapPOIItem.MarkerType.CustomImage);
            routepoints[idx].setCustomImageResourceId(R.drawable.route_icon_24px);
            routepoints[idx].setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            routepoints[idx].setCustomSelectedImageResourceId(R.drawable.route_icon_selected_24px);
            routepoints[idx].setCustomImageAutoscale(false);
            routepoints[idx].setCustomImageAnchor(0.5f, 0.5f);
            idx++;
        }

        path.addPoints(mapPoints);
        path.setLineColor(Color.argb(255, 255, 0, 0));
        mapView.addPolyline(path);
        mapView.addPOIItems(routepoints);
        MapPointBounds mapPointBounds = new MapPointBounds(path.getMapPoints());
        int padding = 100; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }

    private void chksafetyzone() {
        // 안전구역을 설정했는지 검사하고 있으면 추가
        firestore.collection("Users")
                .document(oldman_uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.contains("safe_latitude") && documentSnapshot.getDouble("safe_latitude") != 0){
                        // 안전구역 필드가 있고 설정을 한 상태만 원 추가
                        Long longValue = (Long) documentSnapshot.get("radius");
                        int intValue = longValue.intValue();
                        MapCircle safezone = new MapCircle(MapPoint.mapPointWithGeoCoord((Double) documentSnapshot.get("safe_latitude"), (Double) documentSnapshot.get("safe_longitude")),
                                intValue,
                                Color.argb(255, 255, 0, 0),
                                Color.argb(16, 173, 255, 47)
                        );
                        mapView.addCircle(safezone);
                    }
                });
    }

} // End of Activity