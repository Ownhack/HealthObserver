package ru.bmstu.owncraft.healthobserver.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.bmstu.owncraft.healthobserver.R;
import ru.bmstu.owncraft.healthobserver.tracking.Tracker;

public class HealthBoardDetailFragment<DataType> extends Fragment {

    private Tracker tracker;

    public HealthBoardDetailFragment() {
        tracker = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(Tracker.TRAKCER_ID)) {
            tracker = (Tracker) getArguments().getSerializable(Tracker.TRAKCER_ID);
        }

        if (tracker != null) {
            Activity activity = getActivity();
            assert activity != null;

            CollapsingToolbarLayout layout = activity.findViewById(R.id.toolbar_layout);
            if (layout != null) {
                layout.setTitle(tracker.getTitle());
            }

            NestedScrollView scrollView = activity.findViewById(R.id.healthboard_detail_container);
            if(scrollView != null) {
                TextView textView = new TextView(activity);
                textView.setText(tracker.getData());
                textView.setPadding(5, 5, 5, 5);

                scrollView.addView(textView);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void update(List<DataType> dataList) {

    }
}
