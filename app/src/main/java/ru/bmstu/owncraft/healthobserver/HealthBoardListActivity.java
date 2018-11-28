package ru.bmstu.owncraft.healthobserver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import java.util.ArrayList;
import java.util.List;

import ru.bmstu.owncraft.healthobserver.tracking.PulseTracker;
import ru.bmstu.owncraft.healthobserver.tracking.Tracker;

/**
 * An activity representing a list of HealthBoards. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link HealthBoardDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class HealthBoardListActivity extends AppCompatActivity {

    private GoogleApiClient           googleApiClient;
    private FitnessConnectionsManager fitnessConnectionsManager;

    private View recyclerView;

    private List<Tracker> trackers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        fitnessConnectionsManager = new FitnessConnectionsManager(recyclerView);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addConnectionCallbacks(fitnessConnectionsManager)
                .enableAutoManage(this, 0, fitnessConnectionsManager)
                .build();
    }

    private void setupTrackers() {
        trackers = new ArrayList<>();

        trackers.add(new PulseTracker());
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
