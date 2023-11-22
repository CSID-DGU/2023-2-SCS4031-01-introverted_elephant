package com.example.capstonedesign.location;

import java.io.Serializable;
// RealtimeDatabase에서 기관정보를 받아오기 위한 클래스
public class Institution implements Serializable {
    String name;
    String address;
    String phonenumber;
    double latitude;
    double longitude;
    long district_code;

    public Institution(){}
    public Institution(String name, String address, String phoneNumber, double latitude, double longitude, long districtCode) {
        this.name = name;
        this.address = address;
        this.phonenumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.district_code = districtCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDistrict_code() {
        return district_code;
    }

    public void setDistrict_code(long district_code) {
        this.district_code = district_code;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "Institution{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", district_code=" + district_code +
                '}';
    }
}
