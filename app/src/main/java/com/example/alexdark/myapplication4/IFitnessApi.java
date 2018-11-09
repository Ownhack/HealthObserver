package com.example.alexdark.myapplication4;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IFitnessApi {

//    @POST("/data")
//    Call<SimpleItem> createContainer(@Body NetworkManager.FitnessContainer user);

    @POST("/step")
    Call<Void> createStep(@Body SimpleItem step);

    @POST("/pulse")
    Call<Void> createPulse(@Body SimpleItem pulse);

    @POST("/activity")
    Call<Void> createActivity(@Body ActivityItem activity);

    @POST("/nutrition")
    Call<Void> createNutrition(@Body NutritionsItem nutr);

}
