package ru.bmstu.owncraft.healthobserver.tracking;

import android.support.v7.widget.RecyclerView;

import com.google.android.gms.fitness.data.DataSet;

import java.io.Serializable;
import java.util.List;

public interface Tracker extends Serializable {

    public static final String TRAKCER_ID = "HealthObserverTrackerID";

    void parseDataSet(DataSet dataSet);

    void update();

    String getTitle();

    String getData();
}
