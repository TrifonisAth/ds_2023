package com.example.tracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tracker.model.MySegment;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class SegmentsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_segments, container, false);
        try {
            displayResults(view);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    private void displayResults(View view) throws IOException {
        LinearLayout layoutContainer = view.findViewById(R.id.layoutContainer);
        TextView segmentsTitle = (TextView) view.findViewById(R.id.textView);
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        if (activity.getStatistics() == null) {
            segmentsTitle.setText("No Segments to display");
            return;
        } else if (activity.getSegments().isEmpty()) {
            segmentsTitle.setText("No Segments to display");
            return;
        }
        segmentsTitle.setText("Segments: " + activity.getSegments().size());
        for (MySegment segment: activity.getSegments()) {
            LinearLayout segmentLayout = new LinearLayout(getContext());
            TextView segmentName = new TextView(getContext());
            segmentName.setText(segment.getFileName());
            segmentName.setTextSize(20);
            segmentName.setPadding(0, 0, 0, 20);
            segmentLayout.addView(segmentName);
            AtomicInteger j = new AtomicInteger(0);
            segment.getLeaderBoard().forEach(pair -> {
                // use a counter to only display the top 10
                if (j.getAndIncrement() > 10) return;
                LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView name = new TextView(getContext());
                TextView time = new TextView(getContext());
                name.setText("#" + j + "  " + pair.getFirst());
                time.setText(MySegment.strDuration(pair.getSecond()));
                name.setTextSize(18);
                time.setTextSize(16);
                name.setTextColor(Color.BLACK);
                time.setTextColor(Color.BLACK);
                name.setGravity(Gravity.START);
                time.setGravity(Gravity.END);
                name.setPadding(0, 0, 50, 20);
                time.setPadding(50, 0, 0, 0);
                rowLayout.addView(name);
                rowLayout.addView(time);
                segmentLayout.addView(rowLayout);
            });
            segmentLayout.setOrientation(LinearLayout.VERTICAL);
            segmentLayout.setGravity(Gravity.CENTER);
            segmentLayout.setPadding(10, 50, 10, 50);
            layoutContainer.addView(segmentLayout);
        }
    }

}