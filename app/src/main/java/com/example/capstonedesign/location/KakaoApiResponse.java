package com.example.capstonedesign.location;

import java.util.List;

public class KakaoApiResponse {
    private Meta meta;
    private List<Document> documents;

    public Meta getMeta() {
        return meta;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public static class Meta {
        private int total_count;

        public int getTotal_count() {
            return total_count;
        }
    }

    public static class Document {
        private String region_type;
        private String code;
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String region_4depth_name;
        private double x;
        private double y;

        public String getRegion_type() {
            return region_type;
        }

        public String getCode() {
            return code;
        }

        public String getAddress_name() {
            return address_name;
        }

        public String getRegion_1depth_name() {
            return region_1depth_name;
        }

        public String getRegion_2depth_name() {
            return region_2depth_name;
        }

        public String getRegion_3depth_name() {
            return region_3depth_name;
        }

        public String getRegion_4depth_name() {
            return region_4depth_name;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
