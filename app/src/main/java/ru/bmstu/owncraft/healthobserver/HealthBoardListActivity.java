package ru.bmstu.owncraft.healthobserver;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.bmstu.owncraft.healthobserver.tracking.PulseTracker;
import ru.bmstu.owncraft.healthobserver.tracking.Tracker;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * An activity representing a list of HealthBoards. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link HealthBoardDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class HealthBoardListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private class LastWeekPulseLoader extends AsyncTask<Void, Void, Void> {

        private GoogleApiClient client;
        private Tracker tracker;
        private List<DataSet> dataSets = null;

        public LastWeekPulseLoader(GoogleApiClient client, Tracker tracker) {
            this.client = client;
            this.tracker = tracker;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dataSets = loadLastWeekData(client, DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY);

            for (DataSet dataSet : dataSets) {
                tracker.parseDataSet(dataSet);
            }

            return null;
        }
    }

    private GoogleApiClient           googleApiClient;
    private String serverURL = "http://192.168.20.1:5055";

    private View recyclerView;

    private List<Tracker> trackers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FitnessConnectionsManager.initialize(serverURL);

        setupTrackers();

        setupUI();

        requestAccountInfo();
        setupGoogleAPI();
    }

    private void setupUI() {
        setContentView(R.layout.activity_healthboard_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recyclerView = findViewById(R.id.healthboard_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void requestAccountInfo() {
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    private void setupGoogleAPI() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();
    }

    private void setupTrackers() {
        trackers = new ArrayList<>();

        trackers.add(new PulseTracker());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Snackbar.make(recyclerView, "Connection succeeded", Snackbar.LENGTH_LONG)
                .setAction("ConnectionOK", null).show();

        for(Tracker tracker : trackers) {
            new LastWeekPulseLoader(googleApiClient, tracker).execute();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(recyclerView, "Connection suspended", Snackbar.LENGTH_LONG)
                .setAction("ConnectionSUSP", null).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(recyclerView, "Connection failed", Snackbar.LENGTH_LONG)
                .setAction("ConnectionFAIL", null).show();
    }

    private ArrayList<DataSet> loadLastWeekData(GoogleApiClient client, DataType first_type, DataType second_type) {
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
                .aggregate(first_type, second_type)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(client, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                sets.addAll(dataSets);
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            sets.addAll(dataReadResult.getDataSets());
        }
        return sets;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(trackers));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Tracker> trackers;

        SimpleItemRecyclerViewAdapter(List<Tracker> trackers) {
            this.trackers = trackers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.healthboard_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            Log.e("MY:", Integer.toString(position));

            if(position <= getItemCount()) {
                holder.mIdView.setText(trackers.get(position).getTitle());

                final int final_position = position;

                holder.itemView.setTag(trackers.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, HealthBoardDetailActivity.class);
                        intent.putExtra(Tracker.TRAKCER_ID, trackers.get(final_position));

                        context.startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return trackers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
//            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView      = view.findViewById(R.id.id_text);
//                mContentView = view.findViewById(R.id.content);
            }
        }
    }
}
