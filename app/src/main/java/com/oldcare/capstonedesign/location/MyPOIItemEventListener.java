package com.oldcare.capstonedesign.location;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MyPOIItemEventListener implements MapView.POIItemEventListener {
    private MapPOIItem lastClickedPOIItem; // 추가된 필드
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem poiItem) {
        // POI 아이템이 선택되었을 때 실행되는 코드
        // 추가된 필드에 클릭한 POI 아이템 저장
        lastClickedPOIItem = poiItem;
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }


    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
    public MapPOIItem getLastClickedPOIItem() {
        return lastClickedPOIItem;
    }

}