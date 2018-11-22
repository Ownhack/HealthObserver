package com.example.alexdark.myapplication4;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private Button mButtonViewToday;
    private Button mConBut;


    private GoogleApiClient mGoogleApiClient;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //Немного магический код, но он нужен)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //создание клиента (логин) из важного, вы тут указываете скоупы данных которые вам нужны
        //если нужно что-то еще смотрите в инструкции на типы данных и в каких скоупах они лежат
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        networkManager = new NetworkManager();
    }

    private void initViews() {
        //настройка кнопок
        mButtonViewToday = findViewById(R.id.btn_view_today);
        mButtonViewToday.setOnClickListener(this);
        mButtonViewToday.setText("GetData");
        mConBut = findViewById(R.id.resig);
        mConBut.setText("wait for con");
    }

    public void onConnected(@Nullable Bundle bundle) {
        //обработка события "успешное подключение к облаку"
        Log.e("HistoryAPI", "onConnected");
        mConBut.setBackgroundColor(10);
        mConBut.setText("Con OK");
    }

    // запрос данных типа д1 д2 за последнюю неделю
    //возвращает набор дадасетов которые пришли
    private ArrayList<DataSet> displayLastWeekData(GoogleApiClient client, DataType d1, DataType d2) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        ArrayList<DataSet> sets = new ArrayList<DataSet>();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(d1, d2)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(client, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    sets.add(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                sets.add(dataSet);
            }
        }
        return sets;
    }

    // пример записи данных в облако гугл фита
    private void writeActivity(GoogleApiClient client){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        // Set a range of the run, using a start time of 10 minutes before this moment,
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -10);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource runningDataSource = new DataSource.Builder()
                .setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_SPEED)
                .setName("-running speed")
                .setType(DataSource.TYPE_RAW)
                .build();

        float runSpeedMps = 10;
        // Create a data set of the running speeds to include in the session.
            DataSet runningDataSet = DataSet.create(runningDataSource);
        runningDataSet.add(
                runningDataSet.createDataPoint()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .setFloatValues(runSpeedMps)
        );
        Fitness.HistoryApi.insertData(client,runningDataSet);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }


    @Override
    public void onClick(View v) {
        // обработка гажатия на кнопку
        switch(v.getId()) {
            case R.id.btn_view_today: {
                //запросы данных
                new WeekStepTask(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA ).execute();
                new WeekNutritionTask(DataType.TYPE_NUTRITION, DataType.AGGREGATE_NUTRITION_SUMMARY ).execute();
                new WeekPulseTask(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY ).execute();
                new WeekActivityTask(DataType.TYPE_ACTIVITY_SEGMENT,DataType.AGGREGATE_ACTIVITY_SUMMARY ).execute();

                // запись данныъх в облако
                //new WriteActivityTask().execute();
                break;
            }
        }
    }

    //общий класс для асинхронной задачи
    private class Task extends AsyncTask<Void, Void, Void> {

        DataType d1;
        DataType d2;

        public Task( DataType d1, DataType d2) {
            this.d1 = d1;
            this.d2 = d2;
        }

        protected Void doInBackground(Void... params) {
            return null;
        }
    }

    //асинхронная реализация запроса шагов
    private class WeekStepTask extends Task {

        public WeekStepTask(DataType d1, DataType d2) {
            super(d1, d2);
        }

        protected Void doInBackground(Void... params) {

            //обновляем набор датасетов в нетворк менеджере
            networkManager.stepSet = displayLastWeekData(mGoogleApiClient,d1,d2);
            //запускаем функцию отправки шагов
            networkManager.sendSteps();
            return null;
        }
    }

    private class WeekPulseTask extends Task {

        public WeekPulseTask(DataType d1, DataType d2) {
            super(d1, d2);
        }

        protected Void doInBackground(Void... params) {
            networkManager.pulseSet = displayLastWeekData(mGoogleApiClient,d1,d2);
            networkManager.sendPulse();
            return null;
        }
    }

    private class WeekActivityTask extends Task {

        public WeekActivityTask(DataType d1, DataType d2) {
            super(d1, d2);
        }

        protected Void doInBackground(Void... params) {

            //обновляем набор датасетов в нетворк менеджере
            networkManager.activitySet = displayLastWeekData(mGoogleApiClient,d1,d2);
            //запускаем функцию отправки шагов
            networkManager.sendActivity();
            return null;
        }
    }

    private class WeekNutritionTask extends Task {

        public WeekNutritionTask(DataType d1, DataType d2) {
            super(d1, d2);
        }

        protected Void doInBackground(Void... params) {

            //обновляем набор датасетов в нетворк менеджере
            networkManager.nutritionsSet = displayLastWeekData(mGoogleApiClient,d1,d2);
            //запускаем функцию отправки шагов
            networkManager.sendNutrition();
            return null;
        }
    }


    //Асинхронная таска для записи данных в облако
    private class WriteActivityTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
           writeActivity(mGoogleApiClient);
            return null;
        }
    }

}







