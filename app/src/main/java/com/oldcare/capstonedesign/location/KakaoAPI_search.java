package com.oldcare.capstonedesign.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
public interface KakaoAPI_search {
    @GET("v2/local/search/address.json")
    Call<KakaoApiResponse_search> getKakaoAddress(
            @Header("Authorization") String authorizationHeader,
            @Query("query") String query
    );
}
