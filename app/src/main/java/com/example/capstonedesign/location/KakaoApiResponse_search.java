package com.example.capstonedesign.location;

import java.util.List;

public class KakaoApiResponse_search {
    // 카카오 로컬 API 좌표로 주소검색 을 위해 필요한 클래스
    private Meta meta;
    private List<Document> documents;

    public Meta getMeta() {
        return meta;
    }

    public List<Document> getDocuments() { return documents; }

    public static class Meta {
        private int total_count;
        private int pageable_count;
        private boolean is_end;

        public int getTotalCount() {
            return total_count;
        }

        public void setTotalCount(int total_count) {
            this.total_count = total_count;
        }

        public int getPageableCount() {
            return pageable_count;
        }

        public void setPageableCount(int pageable_count) {
            this.pageable_count = pageable_count;
        }

        public boolean isEnd() {
            return is_end;
        }

        public void setEnd(boolean is_end) {
            this.is_end = is_end;
        }
    }

    public static class Document {
        private String address_name;
        private String address_type;
        private double x;
        private double y;
        private Address address;
        private RoadAddress road_address;

        public String getAddressName() {
            return address_name;
        }

        public void setAddressName(String address_name) {
            this.address_name = address_name;
        }

        public String getAddressType() {
            return address_type;
        }

        public void setAddressType(String address_type) {
            this.address_type = address_type;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public RoadAddress getRoadAddress() {
            return road_address;
        }

        public void setRoadAddress(RoadAddress road_address) {
            this.road_address = road_address;
        }
    }

    public static class Address {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String region_3depth_h_name;
        private String h_code;
        private String b_code;
        private String mountain_yn;
        private String main_address_no;
        private String sub_address_no;
        private double x;
        private double y;

        public String getAddressName() {
            return address_name;
        }

        public String getRegion1DepthName() {
            return region_1depth_name;
        }

        public String getRegion2DepthName() {
            return region_2depth_name;
        }

        public String getRegion3DepthName() {
            return region_3depth_name;
        }

        public String getRegion3DepthHName() {
            return region_3depth_h_name;
        }

        public String getHCode() {
            return h_code;
        }

        public String getBCode() {
            return b_code;
        }

        public String getMountainYn() {
            return mountain_yn;
        }

        public String getMainAddressNo() {
            return main_address_no;
        }

        public String getSubAddressNo() {
            return sub_address_no;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    public static class RoadAddress {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String road_name;
        private String underground_yn;
        private String main_building_no;
        private String sub_building_no;
        private String building_name;
        private String zone_no;
        private double x;
        private double y;

        public String getAddressName() {
            return address_name;
        }

        public String getRegion1DepthName() {
            return region_1depth_name;
        }

        public String getRegion2DepthName() {
            return region_2depth_name;
        }

        public String getRegion3DepthName() {
            return region_3depth_name;
        }

        public String getRoadName() {
            return road_name;
        }

        public String getUndergroundYn() {
            return underground_yn;
        }

        public String getMainBuildingNo() {
            return main_building_no;
        }

        public String getSubBuildingNo() {
            return sub_building_no;
        }

        public String getBuildingName() {
            return building_name;
        }

        public String getZoneNo() {
            return zone_no;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}