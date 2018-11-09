package com.example.alexdark.myapplication4;

import android.util.Log;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public  class StepParser {
    static ArrayList<SimpleItem> parseSteps(DataSet dataSet) {
        ArrayList<SimpleItem> items = new ArrayList<SimpleItem>();
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        String str = "";
        str+=dataSet.getDataType().getName();
        str+=dataSet.getDataPoints().size();
        Log.e("History", str);
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            SimpleItem item = new SimpleItem(dp.getStartTime(TimeUnit.MILLISECONDS),1);

            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            item.value = (float) dp.getValue(dp.getDataType().getFields().get(0)).asInt();
            items.add(item);
        }
        return items;
    }
}
