package com.example.tracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.tracker.client.Client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadFragment extends Fragment {
    String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/routes";
    File directory = new File(directoryPath);
    File[] files = directory.listFiles();
    List<File> selectedFiles = new ArrayList<>();
    Button btnTestConnection;
    LinearLayout layoutContainer;
    TextView textSelectedFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        layoutContainer = view.findViewById(R.id.layoutContainer);
        btnTestConnection = (Button) view.findViewById(R.id.btnTestConnection);
        textSelectedFiles = (TextView) view.findViewById(R.id.selectedAmount);
        textSelectedFiles.setText("Selected: " + selectedFiles.size() + " files");

        btnTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    testConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if (files != null){
            for (File file: files){
                addFileItem(file);
            }
        }
        return view;
    }


    private void testConnection() throws IOException {
        if (selectedFiles.isEmpty()) {
            return;
        }
        new Thread(() -> {
            try {
                Client client = new Client("user1", "10.26.61.204", 54321, selectedFiles);
                client.sendFiles();
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null){
                    mainActivity.appendResults(client.getResults());
                    mainActivity.updateStatistics(client.getStatistics());
                    // Notifies the user that the results are ready.
                    showNotification();

                    mainActivity.replaceFragment(new RoutesFragment());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private boolean toggleFile(File file) {
        if (selectedFiles.contains(file)) {
            selectedFiles.remove(file);
            return false;
        } else {
            selectedFiles.add(file);
            return true;
        }
    }

    private void addFileItem(File file) {
        // Create a new TextView for the file name
        TextView textView = new TextView(getContext());
        textView.setText(file.getName());
        textView.setTextSize(20); // Set the text size to 20sp
        textView.setTextColor(Color.BLACK); // Set the text color to black
        textView.setPadding(220, 20, 16, 20);

        // Set the layout parameters
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);

        // Check if the file is in the selectedFiles list
        boolean isSelected = selectedFiles.contains(file);
        // Apply different visual style based on the selection state
        if (isSelected) {
            textView.setBackgroundColor(Color.YELLOW);
        } else {
            textView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Add click listener to toggle file selection
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = toggleFile(file);
                updateFileItemStyle(textView, isSelected);
                textSelectedFiles.setText("Selected: " + selectedFiles.size() + " files");
            }
        });

        // Add the TextView to the fileContainerLayout
        layoutContainer.addView(textView);
    }

    private void updateFileItemStyle(TextView textView, boolean isSelected) {
        // Apply different visual style based on the selection state
        Color color = Color.valueOf(isSelected ? Color.YELLOW : Color.TRANSPARENT);
        textView.setBackgroundColor(color.toArgb());
    }

    // Method to show the notification
    private void showNotification() {
        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            String channelName = "Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "channel_id")
                .setSmallIcon(R.drawable.ic_stats_foreground)
                .setContentTitle("Results Ready")
                .setContentText("The results are now available.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

}