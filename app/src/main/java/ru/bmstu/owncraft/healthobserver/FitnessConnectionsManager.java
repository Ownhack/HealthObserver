package ru.bmstu.owncraft.healthobserver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class FitnessConnectionsManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private View view;

    FitnessConnectionsManager(View view) {
        this.view = view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("History API", "onConnected");

        // Connection success
        Snackbar.make(view, "Connection succeeded", Snackbar.LENGTH_LONG)
                .setAction("ConnectionSuccess", null).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("HistoryAPI", "onConnectionSuspended");

        Snackbar.make(view, "Connection suspended", Snackbar.LENGTH_LONG)
                .setAction("ConnectionSuspended", null).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");

        Snackbar.make(view, "Connection failed", Snackbar.LENGTH_LONG)
                .setAction("ConnectionFailed", null).show();
    }
}
