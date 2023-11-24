package com.oldcare.capstonedesign.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KaKaoAPI_Address {
    @GET("v2/local/geo/coord2address.json")
    Call<KakaoApiResponse_Address> getAddress(
            @Header("Authorization") String authorizationHeader,
            @Query("x") double longitude,
            @Query("y") double latitude
    );
}
