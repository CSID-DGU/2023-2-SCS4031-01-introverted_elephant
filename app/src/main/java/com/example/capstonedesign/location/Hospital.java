package com.example.capstonedesign.location;

import java.io.Serializable;

public class Hospital implements Serializable {
    String placeName;
    String distance;
    String placeUrl;
    String categoryName;
    String addressName;
    String roadAddressName;
    String id;
    String phone;
    String categoryGroupCode;
    String categoryGroupName;
    String x;
    String y;

    // 생성자, getter, setter 등을 필요에 따라 추가

    public Hospital() {
        // Default constructor required for calls to DataSnapshot.getValue(Hospital.class)
    }

    public Hospital(String placeName, String distance, String placeUrl, String categoryName, String addressName,
                    String roadAddressName, String id, String phone, String categoryGroupCode, String categoryGroupName,
                    String x, String y) {
        this.placeName = placeName;
        this.distance = distance;
        this.placeUrl = placeUrl;
        this.categoryName = categoryName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.id = id;
        this.phone = phone;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryGroupName = categoryGroupName;
        this.x = x;
        this.y = y;
    }

    // Getter and Setter methods

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public void setPlaceUrl(String placeUrl) {
        this.placeUrl = placeUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getRoadAddressName() {
        return roadAddressName;
    }

    public void setRoadAddressName(String roadAddressName) {
        this.roadAddressName = roadAddressName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategoryGroupCode() {
        return categoryGroupCode;
    }

    public void setCategoryGroupCode(String categoryGroupCode) {
        this.categoryGroupCode = categoryGroupCode;
    }

    public String getCategoryGroupName() {
        return categoryGroupName;
    }

    public void setCategoryGroupName(String categoryGroupName) {
        this.categoryGroupName = categoryGroupName;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "placeName='" + placeName + '\'' +
                ", distance='" + distance + '\'' +
                ", placeUrl='" + placeUrl + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", addressName='" + addressName + '\'' +
                ", roadAddressName='" + roadAddressName + '\'' +
                ", id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", categoryGroupCode='" + categoryGroupCode + '\'' +
                ", categoryGroupName='" + categoryGroupName + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                '}';
    }
}
