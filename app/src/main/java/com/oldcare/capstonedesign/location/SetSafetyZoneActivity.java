package com.oldcare.capstonedesign.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oldcare.capstonedesign.R;
import com.google.firebase.firestore.FirebaseFirestore;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetSafetyZoneActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private String oldman_uid;
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private List<KakaoApiResponse_search.Document> documents;
    private MyPOIItemEventListener poiItemEventListener = new MyPOIItemEventListener();
    private MyMapViewEventListener MyMapViewEventListener = new MyMapViewEventListener();
    private double last_latitude;
    private double last_longitude;
    private int distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_safety_zone);
        firestore = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        oldman_uid = preferences.getString("oldMan", "");
        initMapView();
        mapView.setPOIItemEventListener(poiItemEventListener);
        mapView.setMapViewEventListener(MyMapViewEventListener);
        mapView.setCalloutBalloonAdapter(new MapActivity.CustomBalloonAdapter(getLayoutInflater()));  // 커스텀 말풍선 등록

        Button searchbutton = findViewById(R.id.searchbutton);
        EditText editText = findViewById(R.id.editTextText);



        searchbutton.setOnClickListener(view -> {
            String searchText = editText.getText().toString();
            if (TextUtils.isEmpty(searchText)) showToast("도로명 주소를 입력해주세요");
            else doSearch(searchText);
        });

        Button dist_insert = findViewById(R.id.dist_insert);
        EditText dist = findViewById(R.id.editTextNumber);
        dist_insert.setOnClickListener(view -> {
            String distString = dist.getText().toString().trim(); // 문자열 앞뒤 공백 제거

            if (!distString.isEmpty()) {
                try {
                    int distValue = Integer.parseInt(distString);
                    // distValue를 사용한 작업 수행
                    getnumber(distValue);
                    showToast(distValue +"m 입력하였습니다.");
                } catch (NumberFormatException e) {
                    // 정수로 변환할 수 없는 경우에 대한 예외 처리
                    e.printStackTrace(); // 또는 적절한 오류 처리를 추가
                }
            } else {
                // 빈 문자열에 대한 처리 또는 오류 메시지 출력
                showToast("숫자를 입력해주세요");
            }
        });

        Button setzone = findViewById(R.id.setzone);
        setzone.setOnClickListener(view -> {
            MapPOIItem lastClickedPOIItem = poiItemEventListener.getLastClickedPOIItem();
            if (lastClickedPOIItem != null) {
                setZone(lastClickedPOIItem, distance);
                showToast("구역이 설정되었습니다.");
            } else {
                // searchText가 null인 경우
                showToast("지도에서 마커를 클릭해야 합니다.");
            }
        });

        Button okbutton = findViewById(R.id.okbutton);
        okbutton.setOnClickListener(view -> okbutton());

        Button resetbutton = findViewById(R.id.resetzone);
        resetbutton.setOnClickListener(view -> resetzone());



    } // end of oncreate

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initMapView() {
        Log.d("SetSafetyZoneActivity : initmapView","현재initMapView호출");
        if (mapView == null) mapView = new MapView(this);
        if (mapViewContainer == null) {
            mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
            mapViewContainer.addView(mapView);
        } else if (mapViewContainer.getChildCount() == 0)
            mapViewContainer.addView(mapView);
        mapView.removeAllPOIItems();
        mapView.removeAllPolylines();
        mapView.removeAllCircles();
        mapView.setZoomLevel(1, true); // 춤 레벨 변경
        mapView.zoomIn(true); // 줌 인
        mapView.zoomOut(true); // 줌 아웃
    }

    @Override
    public void finish() {
        mapViewContainer.removeView(mapView);
        super.finish();
    }

    public void doSearch(String query) {
        String BASE_URL = "https://dapi.kakao.com/";
        Retrofit retrofit = new Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KakaoAPI_search api = retrofit.create(KakaoAPI_search.class); // 통신 인터페이스를 객체로 생성
        // REST API 키
        String REST_API_KEY = "KakaoAK 0cf15acd27d8221047379b612be7c6ab";
        Call<KakaoApiResponse_search> call = api.getKakaoAddress(REST_API_KEY,query); // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(new Callback<KakaoApiResponse_search>(){
            @Override
            public void onResponse(@NonNull Call<KakaoApiResponse_search> call, @NonNull Response<KakaoApiResponse_search> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                    KakaoApiResponse_search KakaoApiResponse_search = response.body();
                    Log.d("Test", "Raw: " + response.raw());

                    documents = KakaoApiResponse_search.getDocuments();
                    for (KakaoApiResponse_search.Document document : documents) {
                        // 첫 검색결과의 좌표로 지도 화면을 바꾸고
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(documents.get(0).getY(),documents.get(0).getX()),true);
                        MapPOIItem marker = new MapPOIItem(); // 검색결과 들 마커표시
                        if (document.getRoadAddress() != null){ // 클릭 했을 때 나오는 호출 값
                            marker.setItemName(document.getRoadAddress().getAddressName());
                        } else {
                            marker.setItemName(document.getAddress().getAddressName());
                        }
                        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(),document.getX())); // 좌표를 입력받아 출력
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); //  (클릭 전) 모양 색.
                        marker.setCustomImageResourceId(R.drawable.mapmarker_blue);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // (마커 클릭 후) 모양.
                        marker.setCustomSelectedImageResourceId(R.drawable.mapmarker_red);
                        marker.setCustomImageAutoscale(false);
                        marker.setCustomImageAnchor(0.5f, 1.0f);
                        mapView.addPOIItem(marker);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<KakaoApiResponse_search> call, @NonNull Throwable t) {
                // 통신 실패
                Log.w("SetSafetyZoneActivity", "통신 실패: " + t.getMessage());
            }
        }); // api 검색 응답시 행동
    } // end of do search

    public void setZone(MapPOIItem clickedPOIItem, int radius) {
        // 외부에서 필드에 접근
        // 좌표를 얻어오는 것
        if (clickedPOIItem != null) {
            MapPoint.GeoCoordinate point = clickedPOIItem.getMapPoint().getMapPointGeoCoord();
            last_latitude = point.latitude;
            last_longitude = point.longitude;
            int defaultRadius = 100;
            int finalRadius = (radius > 0) ? radius : defaultRadius;
            MapCircle safezone = new MapCircle(clickedPOIItem.getMapPoint(),
                    finalRadius,
                    Color.argb(255, 255, 0, 0),
                    Color.argb(16, 173, 255, 47)
            );
            if (mapView.findCircleByTag(2) != null)
                mapView.removeCircle(mapView.findCircleByTag(2));
            safezone.setTag(2);
            mapView.addCircle(safezone);
        } else {
            showToast("지도에서 마커를 선택한 뒤 눌러주세요");
        }
    }

    public void getnumber(int num){
        distance = num;
    }

    public void okbutton() {
        Intent resultIntent = new Intent(this, MapActivity.class);
        if (last_latitude != 0 && last_longitude != 0){
            Map<String, Object> updates = new HashMap<>();
            updates.put("safe_latitude", last_latitude);
            updates.put("safe_longitude", last_longitude);
            updates.put("radius", distance);
            resultIntent.putExtra("safe_latitude", last_latitude);
            resultIntent.putExtra("safe_longitude", last_longitude);
            firestore.collection("Users")
                    .document(oldman_uid)
                    .update(updates);
        }
        resultIntent.putExtra("distance", distance);
        setResult(Activity.RESULT_OK, resultIntent);
        mapViewContainer.removeView(mapView);
        finish();
    }

    public void resetzone() {
        last_latitude = 0;
        last_longitude = 0;
        distance = 0;
        mapView.removeAllCircles();
        mapView.removeAllPOIItems();
        firestore.collection("Users")
                .document(oldman_uid)
                .update(
                        "radius", 0,
                        "safe_latitude", 0,
                        "safe_longitude", 0
                );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewContainer.removeView(mapView);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapViewContainer.removeView(mapView);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewContainer.removeView(mapView);
        finish();
    }
} // end of Activity