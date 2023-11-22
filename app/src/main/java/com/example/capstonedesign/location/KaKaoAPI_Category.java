package com.example.capstonedesign.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
public interface KaKaoAPI_Category {
    @GET("v2/local/search/category.json")
    Call<KakaoApiResponse_Category> getCategory(
            @Header("Authorization") String authorizationHeader,
            @Query("category_group_code") String CategoryGroupCode,
            @Query("x") double longitude,
            @Query("y") double latitude,
            @Query("radius") int radius
    );
}
