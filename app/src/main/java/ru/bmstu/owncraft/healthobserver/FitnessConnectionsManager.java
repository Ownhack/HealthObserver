package ru.bmstu.owncraft.healthobserver;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.bmstu.owncraft.healthobserver.data.Pulse;
import ru.bmstu.owncraft.healthobserver.tracking.Tracker;

public class FitnessConnectionsManager implements Serializable {

    public interface APICallback {
        Call<Void> sendData();
    }

    static private String baseUrl;
    static private Retrofit retrofit;

    static public void initialize(String serverURL) {
        baseUrl = serverURL;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    static public <API> API getApiInstance(Class<API> apiClass) {
        return retrofit.create(apiClass);
    }

    static public void sendData(APICallback api) {
        Log.i("FitnessConnectionsManager", "Sending data begin");

        Call<Void> fitnessCall = api.sendData();
        fitnessCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.i("FitnessConnectionsManager", "Success!");
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("FitnessConnectionsManager", "Failure!");
            }
        });

        Log.i("FitnessConnectionsManager", "Sending data done");
    }
}
