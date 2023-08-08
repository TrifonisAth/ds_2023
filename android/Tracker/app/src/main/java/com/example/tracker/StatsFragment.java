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

import com.example.tracker.model.Statistics;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.Arrays;

public class StatsFragment extends Fragment {

    LinearLayout layoutContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        layoutContainer = view.findViewById(R.id.layoutContainer);
        displayStats(view);
        return view;
    }

    private void displayStats(View view) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        if (activity.getStatistics() == null) {
            displayNoData(view);
            return;
        }
        BarChart chartActivities = new BarChart(getContext());
        BarChart chartDistance = new BarChart(getContext());
        BarChart chartDuration = new BarChart(getContext());
        BarChart chartElevetion = new BarChart(getContext());
        BarChart chartSpeed = new BarChart(getContext());
        Statistics stats = activity.getStatistics();

        BarEntry userActivities = new BarEntry(0, stats.getUserRoutesCompleted());
        BarEntry userDistance = new BarEntry(0, (float) stats.getUserDistance());
        BarEntry userDuration = new BarEntry(0, (float) stats.getUserDuration()/60);
        BarEntry userElevation = new BarEntry(0, (float) stats.getUserElevationGain());
        BarEntry userSpeed = new BarEntry(0, (float) stats.getUserDistance() / (float) (stats.getUserDuration()/3600));

        BarEntry avgActivities = new BarEntry(1, (float) stats.getTotalRoutes() / (stats.getExistingUsers() - 1));
        BarEntry avgDistance = new BarEntry(1, (float) stats.getTotalDistance() / (stats.getExistingUsers() - 1));
        BarEntry avgDuration = new BarEntry(1, (float) stats.getTotalDuration() / 60 / (stats.getExistingUsers() - 1) );
        BarEntry avgElevation = new BarEntry(1, (float) stats.getTotalElevationGain() / (stats.getExistingUsers() - 1));
        BarEntry avgSpeed = new BarEntry(1, (float) stats.getTotalDistance() / (float) (stats.getTotalDuration()/3600));

        int[] colors = new int[]{Color.YELLOW, Color.GREEN};
        // Set data for each chart.
        BarDataSet dataSetActivities = new BarDataSet(Arrays.asList(userActivities, avgActivities), "Activities Completed");
        dataSetActivities.setStackLabels(new String[]{"User Activities", "Avg Other Users"});
        dataSetActivities.setColors(Color.MAGENTA);
        BarData barData = new BarData(dataSetActivities);
        chartActivities.setData(barData);
        chartActivities.getAxisLeft().setAxisMinimum(0);

        BarDataSet dataSetDistance = new BarDataSet(Arrays.asList(userDistance, avgDistance), "Distance (km)");
        dataSetDistance.setColors(Color.BLUE);
        barData = new BarData(dataSetDistance);
        chartDistance.setData(barData);
        chartDistance.getAxisLeft().setAxisMinimum(0);

        BarDataSet dataSetDuration = new BarDataSet(Arrays.asList(userDuration, avgDuration), "Duration (min)");
        dataSetDuration.setColors(Color.RED);
        barData = new BarData(dataSetDuration);
        chartDuration.setData(barData);
        chartDuration.getAxisLeft().setAxisMinimum(0);

        BarDataSet dataSetElevation = new BarDataSet(Arrays.asList(userElevation, avgElevation), "Elevation (m)");
        dataSetElevation.setColors(Color.CYAN);
        barData = new BarData(dataSetElevation);
        chartElevetion.setData(barData);
        chartElevetion.getAxisLeft().setAxisMinimum(0);

        BarDataSet dataSetSpeed = new BarDataSet(Arrays.asList(userSpeed, avgSpeed), "Speed (km/h)");
        dataSetSpeed.setColors(Color.GRAY);
        barData = new BarData(dataSetSpeed);
        chartSpeed.setData(barData);
        chartSpeed.getAxisLeft().setAxisMinimum(0);
        // set width of bars
        for (BarDataSet dataSet: Arrays.asList(dataSetActivities, dataSetDistance, dataSetDuration, dataSetElevation, dataSetSpeed)) {
            dataSet.setBarBorderWidth(0.5f);
            dataSet.setBarBorderColor(Color.BLACK);
            // Make the labels bigger.
            dataSet.setValueTextSize(20);
        }

        for (BarChart chart: Arrays.asList(chartActivities, chartDistance, chartDuration, chartElevetion, chartSpeed)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    800
            );
            params.setMargins(0, 100, 0, 50);
            chart.setLayoutParams(params);
            chart.setMinimumHeight(270);
            // Remove the description label.
            chart.getDescription().setEnabled(false);
            chart.getXAxis().setDrawLabels(false);
            chart.getAxisRight().setDrawLabels(false);
            chart.setPinchZoom(false);
            chart.setDoubleTapToZoomEnabled(false);

            layoutContainer.addView(chart);
        }

    }

    private void displayNoData(View view){
        LinearLayout layoutContainer = view.findViewById(R.id.layoutContainer);
        TextView noData = new TextView(getContext());
        noData.setText("No data to display");
        noData.setTextSize(20);
        // center text
        noData.setGravity(Gravity.CENTER);
        layoutContainer.addView(noData);
    }
}