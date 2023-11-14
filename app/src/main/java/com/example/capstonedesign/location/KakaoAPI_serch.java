package com.example.capstonedesign.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoAPI_serch {
    @GET("v2/local/search/address.json")
    Call<KakaoApiResponse_search> getKakaoAddress(
            @Header("Authorization") String apiKey,
            @Query("query") String query
    );
}
