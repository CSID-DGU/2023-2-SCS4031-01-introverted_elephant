package com.oldcare.capstonedesign.location;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.oldcare.capstonedesign.R;
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

public class MapActivity_Oldman extends AppCompatActivity {

    private MapView mapView;
    private double latitude,longitude;
    private final String BASE_URL = "https://dapi.kakao.com/";
    private DatabaseReference mDatabase;
    private final String REST_API_KEY = "KakaoAK "+ "0cf15acd27d8221047379b612be7c6ab"; // REST API 키
    // private  String RESR_API_KEY = "KakaoAK "+ BuildConfig.RESTAPIKEY; API키 숨기는 코드 잦은 오류로 임시 주석처리
    public long RegionCode_B;
    public List<Institution> agencyList = new ArrayList<>();
    public List<Hospital> HPList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_map);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
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
        marker1.setItemName("현재 위치"); // 클릭 했을 때 나오는 호출 값
        marker1.setMapPoint(MARKER_POINT1); // 좌표를 입력받아 현 위치로 출력
        marker1.setMarkerType(MapPOIItem.MarkerType.CustomImage); //  (클릭 전) 모양 색.
        marker1.setCustomImageResourceId(R.drawable.mapmarker_blue);
        marker1.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // (마커 클릭 후) 모양.
        marker1.setCustomSelectedImageResourceId(R.drawable.mapmarker_red);
        marker1.setCustomImageAutoscale(false);    // 커스텀 마커 이미지 크기 자동 조정
        marker1.setCustomImageAnchor(0.5f, 1.0f);    // 마커 이미지 기준점
        mapView.addPOIItem(marker1); // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)

        mDatabase = FirebaseDatabase.getInstance().getReference();
        geoinfo(longitude,latitude);
        hpinfo(longitude,latitude);

        Button nearcenter = findViewById(R.id.nearcenter);
        nearcenter.setOnClickListener(view -> showcenter(agencyList,HPList));

        Button centerlist = findViewById(R.id.center_list);
        centerlist.setOnClickListener(view -> movecenterlist(agencyList,HPList));

    } // End of onCreate

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
            public void onResponse(@NonNull Call<KakaoApiResponse_geocoder> call, @NonNull Response<KakaoApiResponse_geocoder> response) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                if (response.isSuccessful() && response.body() != null) {
                    KakaoApiResponse_geocoder kakaoApiResponseGeocoder = response.body();
                    // 확인용 로그 출력
                    Log.d("Test", "Raw: " + response.raw());
                    // documents 에 내용이 담겨 있음
                    List<KakaoApiResponse_geocoder.Document> documents = kakaoApiResponseGeocoder.getDocuments();
                    for (KakaoApiResponse_geocoder.Document document : documents) {
                        if ("B".equals(document.getRegion_type())) {
                            RegionCode_B = Long.parseLong(document.getCode().substring(0,5) + "00000");
                            // firedatabase query
                            Query centerfinder = mDatabase.child("WelfareAgency").orderByChild("district_code").equalTo(RegionCode_B);
                            centerfinder.addValueEventListener(new ValueEventListener() {
                                // 쿼리로부터 값을 얻어오는 함수
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot lawSnapshot : snapshot.getChildren()) {
                                        Institution agency = lawSnapshot.getValue(Institution.class);
                                        if (agency != null) agencyList.add(agency);
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
            public void onFailure(@NonNull Call<KakaoApiResponse_geocoder> call, @NonNull Throwable t) {
                // 통신 실패
                Log.w("MapActivity", "통신 실패: " + t.getMessage());
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

    public void showcenter(@NonNull List<Institution> Agencylist, List<Hospital> HPlist2){
        for (Institution agency : Agencylist) {
            if (Agencylist == null)
                Log.i("buttonwork","데이터가 없습니다.");
            else
                Log.d("buttonwork","복지기관 : " + agency.toString());
        }
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

        for (Hospital hp : HPlist2) {
            if (HPlist2 == null)
                Log.i("buttonwork","데이터가 없습니다.");
            else
                Log.d("buttonwork","병원 : " + hp.toString());
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
        }
    }

    public void movecenterlist(List<Institution> Agencylist, List<Hospital> HPList){
        // centeractivity 호출
        Intent intent = new Intent(MapActivity_Oldman.this, CenterActivity.class);
        intent.putExtra("centers", (Serializable) Agencylist);
        intent.putExtra("hospitals", (Serializable) HPList);
        startActivity(intent);
    }

} // End of Activity