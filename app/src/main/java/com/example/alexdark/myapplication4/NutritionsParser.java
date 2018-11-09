package com.example.alexdark.myapplication4;

import android.util.Log;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class NutritionsParser {
    static ArrayList<NutritionsItem> parseNutrition(DataSet dataSet) {
        ArrayList<NutritionsItem> items = new ArrayList<>();
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        String str = "";
        str += dataSet.getDataType().getName();
        str += dataSet.getDataPoints().size();
        Log.e("History", str);
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            NutritionsItem item = new NutritionsItem(dp.getStartTime(TimeUnit.MILLISECONDS));

            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

            for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }

            Field meal = dp.getDataType().getFields().get(0);
            item.mealType = dp.getValue(dp.getDataType().getFields().get(1)).asInt();

            Value mv =  dp.getValue(meal);
            try{}catch (Exception ex ){}
            try{item.calcium = mv.getKeyValue("calcium");}catch (Exception ex ){}
            try{item.calories = mv.getKeyValue("calories");}catch (Exception ex ){}
                try{item.carbsTotal = mv.getKeyValue("carbs.total");}catch (Exception ex ){}
                    try{item.cholesterol = mv.getKeyValue("cholesterol");}catch (Exception ex ){}
                        try{item.dietaryFiber = mv.getKeyValue("dietary_fiber");}catch (Exception ex ){}
                            try{item.fatMonounsaturated = mv.getKeyValue("fat.monounsaturated");}catch (Exception ex ){}
                                try{item.fatPolyunsaturated = mv.getKeyValue("fat.polyunsaturated");}catch (Exception ex ){}
                                    try{item.fatSaturated = mv.getKeyValue("fat.saturated");}catch (Exception ex ){}
                                        try{item.fatTotal = mv.getKeyValue("fat.total");}catch (Exception ex ){}
                                            try{item.fatTrans = mv.getKeyValue("fat.trans");}catch (Exception ex ){}
                                                try{item.iron = mv.getKeyValue("iron");}catch (Exception ex ){}
                                                    try{item.potassium = mv.getKeyValue("potassium");}catch (Exception ex ){}
                                                        try{item.protein = mv.getKeyValue("protein");}catch (Exception ex ){}
                                                            try{item.sodium = mv.getKeyValue("sodium");}catch (Exception ex ){}
                                                                try{item.sugar = mv.getKeyValue("sugar");}catch (Exception ex ){}
                                                                    try{item.vitamin_c = mv.getKeyValue("vitamin_c");}catch (Exception ex ){}
            items.add(item);
        }
        return items;
    }
}

