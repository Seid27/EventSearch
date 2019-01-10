package com.example.yd3843oa.eventsearch;


import com.google.gson.JsonObject;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    String BASE_URL = "https://api.spotify.com/v1/me/top/";
    @GET("artists")
    Call<JsonObject> getTopArtists(@Header("Authorization ") String auth, @Query("limit") String limit);


    String SEARCH_URL = "https://api.spotify.com/v1/";
    @GET("search")
    Call<JsonObject> getSearchItem(@Header("Authorization ") String auth, @Query("q") String query,
                                   @Query("type") String type);

    //getting an image for a URL
    @GET
    Call<ResponseBody> fetchCaptcha(@Url String url);

    String ROOT_URL = "https://app.ticketmaster.com/discovery/v2/";

    @GET("events.json")
    Call<JsonObject> getEvents(@Query("apikey") String apiKey, @Query("size") String size,
                               @Query("classificationName") String classificationName, @Query("attractionId") String attractionId,
                               @Query("startDateTime") String startDateTime, @Query("sort") String dateSort);
//    @Query("endDateTime") String endDateTime

//    @Query("startDateTime") String startDateTime, @Query("endDateTime") String endDateTime

    //@Query("size") String size, @Query("classificationName") String classificationName
    //@Query("attractionId") String attractionId

    @GET("attractions.json")
    Call<JsonObject> getAttractions(@Query("apikey") String apiKey , @Query("keyword") String keyword,
                               @Query("classificationName") String classificationName);
}
