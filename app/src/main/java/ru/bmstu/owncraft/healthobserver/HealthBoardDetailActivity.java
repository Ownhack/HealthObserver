package ru.bmstu.owncraft.healthobserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import ru.bmstu.owncraft.healthobserver.fragment.HealthBoardDetailFragment;
import ru.bmstu.owncraft.healthobserver.tracking.Tracker;

/**
 * An activity representing a single HealthBoard detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link HealthBoardListActivity}.
 */
public class HealthBoardDetailActivity extends AppCompatActivity {

    private Tracker tracker;

    HealthBoardDetailActivity() {
        tracker = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthboard_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.sync_info);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tracker != null) {
                    Snackbar.make(view, "Syncing data", Snackbar.LENGTH_LONG)
                            .setAction("SyncingData", null).show();

                    tracker.update();

                    Snackbar.make(view, "Syncing done", Snackbar.LENGTH_LONG)
                            .setAction("SyncingDataDone", null).show();
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            tracker = (Tracker) getIntent().getSerializableExtra(Tracker.TRAKCER_ID);
            Bundle arguments = new Bundle();
            arguments.putSerializable(Tracker.TRAKCER_ID, tracker);



            HealthBoardDetailFragment fragment = new HealthBoardDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.healthboard_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, HealthBoardListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
