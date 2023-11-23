package com.example.capstonedesign.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KakaoApiResponse_Address {
    @SerializedName("meta")
    private Meta meta;

    @SerializedName("documents")
    private List<Document> documents;

    public Meta getMeta() {
        return meta;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public static class Meta {
        @SerializedName("total_count")
        private int totalCount;

        public int getTotalCount() {
            return totalCount;
        }
    }

    public static class Document {
        @SerializedName("road_address")
        private RoadAddress roadAddress;

        @SerializedName("address")
        private Address address;

        public RoadAddress getRoadAddress() {
            return roadAddress;
        }

        public Address getAddress() {
            return address;
        }
    }

    public static class RoadAddress {
        @SerializedName("address_name")
        private String addressName;

        @SerializedName("region_1depth_name")
        private String region1DepthName;

        @SerializedName("region_2depth_name")
        private String region2DepthName;

        @SerializedName("region_3depth_name")
        private String region3DepthName;

        @SerializedName("road_name")
        private String roadName;

        @SerializedName("underground_yn")
        private String undergroundYn;

        @SerializedName("main_building_no")
        private String mainBuildingNo;

        @SerializedName("sub_building_no")
        private String subBuildingNo;

        @SerializedName("building_name")
        private String buildingName;

        @SerializedName("zone_no")
        private String zoneNo;

        public String getAddressName() {
            return addressName;
        }

        public String getRegion1DepthName() {
            return region1DepthName;
        }

        public String getRegion2DepthName() {
            return region2DepthName;
        }

        public String getRegion3DepthName() {
            return region3DepthName;
        }

        public String getRoadName() {
            return roadName;
        }

        public String getUndergroundYn() {
            return undergroundYn;
        }

        public String getMainBuildingNo() {
            return mainBuildingNo;
        }

        public String getSubBuildingNo() {
            return subBuildingNo;
        }

        public String getBuildingName() {
            return buildingName;
        }

        public String getZoneNo() {
            return zoneNo;
        }
    }

    public static class Address {
        @SerializedName("address_name")
        private String addressName;

        @SerializedName("region_1depth_name")
        private String region1DepthName;

        @SerializedName("region_2depth_name")
        private String region2DepthName;

        @SerializedName("region_3depth_name")
        private String region3DepthName;

        @SerializedName("mountain_yn")
        private String mountainYn;

        @SerializedName("main_address_no")
        private String mainAddressNo;

        @SerializedName("sub_address_no")
        private String subAddressNo;

        public String getAddressName() {
            return addressName;
        }

        public String getRegion1DepthName() {
            return region1DepthName;
        }

        public String getRegion2DepthName() {
            return region2DepthName;
        }

        public String getRegion3DepthName() {
            return region3DepthName;
        }

        public String getMountainYn() {
            return mountainYn;
        }

        public String getMainAddressNo() {
            return mainAddressNo;
        }

        public String getSubAddressNo() {
            return subAddressNo;
        }
    }
}
