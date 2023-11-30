package com.oldcare.capstonedesign.location;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

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
    @Deprecated
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        // 말풍선 클릭 시
        Context context = mapView.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 클립보드 객체 얻기
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        builder.setTitle(mapPOIItem.getItemName());
        String[] itemList = {"주소 복사", "번호 복사", "취소"};
        String[] itemList2 = {"취소"};
        if (mapPOIItem.getUserObject() instanceof Hospital) {
            builder.setItems(itemList, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // 클립데이터 생성
                        ClipData clipData = ClipData.newPlainText("주소 복사", ((Hospital) mapPOIItem.getUserObject()).addressName ); //Test 가 실질적으로 복사되는 Text
                        // 클립보드에 추가
                        clipboardManager.setPrimaryClip(clipData);// 주소 복사
                        break;
                    case 1:
                        // 클립데이터 생성
                        ClipData clipData2 = ClipData.newPlainText("번호 복사", ((Hospital) mapPOIItem.getUserObject()).phone ); //Test 가 실질적으로 복사되는 Text
                        // 클립보드에 추가
                        clipboardManager.setPrimaryClip(clipData2);// 번호 복사
                        break;
                    case 2:
                        dialog.dismiss();   // 대화상자 닫기
                        break;
                }
            });
        } else if (mapPOIItem.getUserObject() instanceof Institution) {
            builder.setItems(itemList, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // 클립데이터 생성
                        ClipData clipData = ClipData.newPlainText("주소 복사", ((Institution) mapPOIItem.getUserObject()).address ); //Test 가 실질적으로 복사되는 Text
                        // 클립보드에 추가
                        clipboardManager.setPrimaryClip(clipData);// 주소 복사
                        break;
                    case 1:
                        // 클립데이터 생성
                        ClipData clipData2 = ClipData.newPlainText("번호 복사", ((Institution) mapPOIItem.getUserObject()).phonenumber ); //Test 가 실질적으로 복사되는 Text
                        // 클립보드에 추가
                        clipboardManager.setPrimaryClip(clipData2);// 번호 복사
                        break;
                    case 2:
                        dialog.dismiss();   // 대화상자 닫기
                        break;
                }
            });
        } else {
            builder.setItems(itemList2, (dialog, which) -> {
                switch (which) {
                    case 0:
                        dialog.dismiss();   // 대화상자 닫기
                        break;
                }
            });
        }
        builder.show();
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
    public MapPOIItem getLastClickedPOIItem() {
        return lastClickedPOIItem;
    }

}