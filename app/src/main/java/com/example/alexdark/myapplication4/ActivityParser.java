package com.example.alexdark.myapplication4;

import android.util.Log;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ActivityParser {
    static ArrayList<ActivityItem> parseActivity(DataSet dataSet) {
        ArrayList<ActivityItem> items = new ArrayList<>();
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        String str = "";
        str += dataSet.getDataType().getName();
        str += dataSet.getDataPoints().size();
        Log.e("History", str);
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            ActivityItem item = new ActivityItem(dp.getStartTime(TimeUnit.MILLISECONDS));

            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

                        for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }


            item.activity = dp.getValue(dp.getDataType().getFields().get(0)).asInt();
            item.duration = dp.getValue(dp.getDataType().getFields().get(1)).asInt();
            item.segments = dp.getValue(dp.getDataType().getFields().get(2)).asInt();

//            item.value = (float) dp.getValue(dp.getDataType().getFields().get(0)).asInt();
            items.add(item);
        }
        return items;
    }
}
