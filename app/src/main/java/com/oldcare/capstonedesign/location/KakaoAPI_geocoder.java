package com.oldcare.capstonedesign.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoAPI_geocoder {
    @GET("v2/local/geo/coord2regioncode.json")
    Call<KakaoApiResponse_geocoder> getRegionInfo(
            @Header("Authorization") String apiKey,
            @Query("x") double longitude,
            @Query("y") double latitude
    );
}