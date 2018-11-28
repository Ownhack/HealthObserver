package ru.bmstu.owncraft.healthobserver.tracking;

import android.util.Log;

import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

import ru.bmstu.owncraft.healthobserver.data.Pulse;

public class PulseTracker implements Tracker {
    @Override
    public void parseDataSet(DataSet dataSet) {

    }

    @Override
    public void update() {
        Log.e("PulseTracker", "update()");

    }

    @Override
    public String getTitle() {
        return "Pulse data";
    }

    @Override
    public String getData() {
        return "";
    }
}
