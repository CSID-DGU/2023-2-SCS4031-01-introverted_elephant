package com.oldcare.capstonedesign.location;

import com.oldcare.capstonedesign.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MyMapViewEventListener implements MapView.MapViewEventListener {

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        // 한번 클릭했을 때
        MapPOIItem marker = new MapPOIItem(); // 마커 아이콘 추가하는 함수
        marker.setItemName("클릭한 위치"); // 클릭 했을 때 나오는 호출 값
        if (mapView.findPOIItemByTag(1) != null)
            mapView.removePOIItem(mapView.findPOIItemByTag(1));
        marker.setTag(1); // 마커 태그 : 태그로 구분하기 위한 거 태그 1 클릭한 위치
        marker.setMapPoint(mapPoint); // 좌표를 입력받아 현 위치로 출력
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); //  (클릭 전) 모양 색.
        marker.setCustomImageResourceId(R.drawable.mapmarker_blue);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // (마커 클릭 후) 모양.
        marker.setCustomSelectedImageResourceId(R.drawable.mapmarker_red);
        marker.setCustomImageAutoscale(false);
        marker.setCustomImageAnchor(0.5f, 1.0f);
        mapView.addPOIItem(marker); // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        // 두번 클릭 했을 때

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
