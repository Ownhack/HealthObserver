package ru.bmstu.owncraft.healthobserver.tracking;

import com.google.android.gms.fitness.data.DataSet;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.http.Body;
import ru.bmstu.owncraft.healthobserver.FitnessConnectionsManager;

public interface Tracker extends Serializable {

    String TRAKCER_ID = "HealthObserverTrackerID";

    void parseDataSet(DataSet dataSet);

    void update();

    String getTitle();

    String getData();

    Class<?> getAPI();
}
