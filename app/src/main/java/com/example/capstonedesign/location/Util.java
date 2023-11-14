package com.example.capstonedesign.location;

public class Util {
    // 좌표간 거리계산 공식
    public static String getDistanceAsText(double distance) {
        if (distance < 1000) {
            return String.format("%dm", (int)distance);
        } else {
            return String.format("%.1fkm", (distance / 1000));
        }
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 != lat2 || lon1 != lon2) {
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = dist * 1000;
            return (dist);
        } else {
            return 0;
        }
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
