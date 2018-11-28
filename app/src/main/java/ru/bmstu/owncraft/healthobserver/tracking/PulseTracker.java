package ru.bmstu.owncraft.healthobserver.tracking;

import android.util.Log;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.bmstu.owncraft.healthobserver.FitnessConnectionsManager;
import ru.bmstu.owncraft.healthobserver.data.Pulse;

public class PulseTracker implements Tracker {

    public interface API {
        @POST("/pulse")
        Call<Void> sendData(@Body Pulse pule);
    }

    private List<Pulse> pulses = new ArrayList<>();

    @Override
    public void parseDataSet(DataSet dataSet) {
        Log.e("PulseTracker", "parseDataSet() begin");

        for(DataPoint dataPoint : dataSet.getDataPoints()) {
            Pulse pulse = new Pulse();

            pulse.startTime   = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
            pulse.endTime     = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
            pulse.average_bpm = dataPoint.getValue(dataPoint.getDataType().getFields().get(0)).asFloat();
            pulse.max_bpm     = dataPoint.getValue(dataPoint.getDataType().getFields().get(1)).asFloat();
            pulse.min_bpm     = dataPoint.getValue(dataPoint.getDataType().getFields().get(2)).asFloat();

            pulses.add(pulse);
        }

        Log.e("PulseTracker", "parseDataSet() end");

    }

    @Override
    public void update() {
        Log.i("PulseTracker", "update() begin");

        Log.i("PulseTracker", "getting api");
        final API api = FitnessConnectionsManager.getApiInstance(API.class);

        for (final Pulse pulse : pulses) {
            FitnessConnectionsManager.sendData(new FitnessConnectionsManager.APICallback() {
                @Override
                public Call<Void> sendData() {
                    return api.sendData(pulse);
                }
            });
        }

        Log.i("PulseTracker", "update() end");
    }

    @Override
    public String getTitle() {
        return "Pulse data";
    }

    @Override
    public String getData() {
        StringBuilder builder = new StringBuilder();

        for (Pulse pulse : pulses) {
            builder.append("---------------\n");

            builder.append(" start time: ").append(pulse.startTime).append('\n');
            builder.append("   end time: ").append(pulse.endTime).append('\n');
            builder.append("average bpm: ").append(pulse.average_bpm).append('\n');
            builder.append("    min bpm: ").append(pulse.min_bpm).append('\n');
            builder.append("    max bpm: ").append(pulse.max_bpm).append('\n');

            builder.append("---------------\n\n");
        }

        return builder.toString();
    }

    @Override
    public Class<?> getAPI() {
        return API.class;
    }
}
