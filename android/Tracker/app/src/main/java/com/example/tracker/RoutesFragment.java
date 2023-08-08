package com.example.tracker;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.tracker.model.Result;

import java.io.IOException;
import java.util.Arrays;

public class RoutesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routes, container, false);
        try {
            displayResults(view);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    private void displayResults(View view) throws IOException {
        LinearLayout layoutContainer = view.findViewById(R.id.layoutContainer);
        TextView routes = (TextView) view.findViewById(R.id.textView);
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        if (activity.getResults().size() == 0) {
            routes.setText("No routes to display");
            return;
        }
        routes.setText("Completed Routes: " + activity.getResults().size());
        for (Result res : activity.getResults()) {
            String key = res.getFilename();
            LinearLayout routeLayout = new LinearLayout(getContext());
            LinearLayout iconLayout = new LinearLayout(getContext());
            LinearLayout textLayout = new LinearLayout(getContext());
            for (LinearLayout l: Arrays.asList(routeLayout, iconLayout, textLayout)) {
                l.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
            }
            routeLayout.setOrientation(LinearLayout.HORIZONTAL);
            iconLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setOrientation(LinearLayout.VERTICAL);

            // Set layout weights for iconLayout and textLayout
            LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.3f);
            iconLayout.setLayoutParams(iconLayoutParams);
            textLayout.setLayoutParams(textLayoutParams);

            TextView routeName = new TextView(getContext());
            routeName.setText(key);
            routeName.setTextSize(17);
            routeName.setTextColor(Color.BLACK);
            routeName.setGravity(Gravity.CENTER);

            TextView distance = new TextView(getContext());
            TextView duration = new TextView(getContext());
            TextView elevation = new TextView(getContext());
            TextView averageSpeed = new TextView(getContext());
            distance.setText("distance: " + res.strDistance());
            duration.setText("duration: "+ res.strDuration());
            elevation.setText("elevetionGain: "+ res.strElevationGain());
            averageSpeed.setText("avg speed: "+ res.strAvgSpeed());

            for (TextView i : Arrays.asList(distance, duration, elevation, averageSpeed)) {
                i.setTextSize(15);
                i.setTextColor(Color.BLACK);
                i.setPadding(0, 8, 0, 8);
                textLayout.addView(i);
            }

            ImageView image = new ImageView(getContext());
            image.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            // create icon.
            Resources resource = getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resource, R.drawable.ic_stats_foreground, null);
            image.setImageDrawable(drawable);

            iconLayout.setGravity(Gravity.START);
            iconLayout.addView(routeName);
            iconLayout.addView(image);
            routeLayout.setGravity(Gravity.END);
            routeLayout.addView(iconLayout);
            routeLayout.addView(textLayout);
            routeLayout.setPadding(0, 20, 0, 20);
            layoutContainer.addView(routeLayout);
        }
    }

}