package com.example.hotelreservationsystem_java;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.Body;

public interface ApiInterface {

    // API's endpoints
    @GET("/allHotels")
    public void getHotelsLists(Callback<List<HotelListData>> callback);

    @POST("/reservecode")
    public void postData(@Body ApiRequestBody requestBody, Callback<HotelRespond> callback);

}

