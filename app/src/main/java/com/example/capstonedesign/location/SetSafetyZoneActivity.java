package com.example.capstonedesign.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstonedesign.BuildConfig;
import com.example.capstonedesign.R;


import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetSafetyZoneActivity extends AppCompatActivity {
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private final String BASE_URL = "https://dapi.kakao.com/";
    private String REST_API_KEY = "KakaoAK " + BuildConfig.RESTAPIKEY; // REST API 키

    private List<KakaoApiResponse_search.Document> documents;

    private MyPOIItemEventListener poiItemEventListener;
    private double last_latitude;
    private double last_longitude;
    private int distance = -1;
    private MapCircle safezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_safety_zone);

        initMapView();
        poiItemEventListener = new MyPOIItemEventListener();
        mapView.setPOIItemEventListener(poiItemEventListener);

        Button searchbutton = findViewById(R.id.searchbutton);
        EditText editText = findViewById(R.id.editTextText);

        searchbutton.setOnClickListener(view -> {
            String searchText = editText.getText().toString();
            if (searchText != null) {
                doSearch(searchText);
            } else {
                // searchText가 null인 경우
                showToast("주소를 입력하세요");
            }
        });

        Button dist_insert = findViewById(R.id.dist_insert);
        EditText dist = findViewById(R.id.editTextNumber);
        dist_insert.setOnClickListener(view -> getnumber(Integer.parseInt(dist.getText().toString())));

        Button setzone = findViewById(R.id.setzone);
        setzone.setOnClickListener(view -> {
            MapPOIItem lastClickedPOIItem = poiItemEventListener.getLastClickedPOIItem();
            if (lastClickedPOIItem != null) {
                setZone(lastClickedPOIItem, distance);
            } else {
                // searchText가 null인 경우
                showToast("지도에서 마커를 클릭해야 합니다.");
            }
        });

        Button okbutton = findViewById(R.id.okbutton);
        okbutton.setOnClickListener(view -> okbutton());


    } // end of oncreate

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initMapView() {
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        // 춤 레벨 변경
        mapView.setZoomLevel(1, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);
        // mapView.setMapCenterPoint();
    }


    @Override
    public void finish() {
        mapViewContainer.removeView(mapView);
        super.finish();
    }

    public void doSearch(String query) {
        Retrofit retrofit = new Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KakaoAPI_search api = retrofit.create(KakaoAPI_search.class); // 통신 인터페이스를 객체로 생성
        Call<KakaoApiResponse_search> call = api.getKakaoAddress(REST_API_KEY,query); // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(new Callback<KakaoApiResponse_search>(){

            @Override
            public void onResponse(Call<KakaoApiResponse_search> call, Response<KakaoApiResponse_search> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                    KakaoApiResponse_search KakaoApiResponse_search = response.body();
                    Log.d("Test", "Raw: " + response.raw());

                    documents = KakaoApiResponse_search.getDocuments();
                    for (KakaoApiResponse_search.Document document : documents) {
                        // 첫 검색결과의 좌표로 지도 화면을 바꾸고
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(documents.get(0).getY(),documents.get(0).getX()),true);
                        // 검색결과 들 마커표시
                        MapPOIItem marker = new MapPOIItem();
                        // 클릭 했을 때 나오는 호출 값
                        if (document.getRoadAddress() != null){
                            marker.setItemName(document.getRoadAddress().getAddressName());
                        } else {
                            marker.setItemName(document.getAddress().getAddressName());
                        }
                        // 좌표를 입력받아 출력
                        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(),document.getX()));
                        // (클릭 전) 기본으로 제공하는 BluePin 마커 모양의 색.
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                        // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                        // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
                        mapView.addPOIItem(marker);
                    }
                }
            }

            @Override
            public void onFailure(Call<KakaoApiResponse_search> call, Throwable t) {
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
        }

        int defaultRadius = 100;
        int finalRadius = (radius > 0) ? radius : defaultRadius;

        safezone = new MapCircle(clickedPOIItem.getMapPoint(),
                finalRadius,
                Color.argb(255, 255, 0, 0),
                Color.argb(16, 173, 255, 47)
        );
        mapView.addCircle(safezone);
    }

    public void getnumber(int num){
        distance = num;
    }

    public void okbutton() {
        Intent resultIntent = new Intent(this, MapActivity.class);
//        if (last_latitude != null && last_longitude != null){
//            resultIntent.putExtra("safe_latitude", last_latitude);
//            resultIntent.putExtra("safe_longitude", last_longitude);
//        }
        resultIntent.putExtra("distance", distance);
        setResult(Activity.RESULT_OK, resultIntent);
        mapViewContainer.removeView(mapView);
        finish();
    }

} // end of Activity