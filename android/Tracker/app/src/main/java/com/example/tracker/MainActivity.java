package com.example.tracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tracker.client.Client;
import com.example.tracker.databinding.ActivityMainBinding;
import com.example.tracker.model.MySegment;
import com.example.tracker.model.Result;
import com.example.tracker.model.Statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private final List<Result> results = new ArrayList<>();
    private Statistics statistics;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityCompat.requestPermissions( this,
                new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE

                }, 1
        );

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            }
            else if (item.getItemId() == R.id.routes) {
                replaceFragment(new RoutesFragment());
            }
            else if (item.getItemId() == R.id.stats) {
                replaceFragment(new StatsFragment());
            }
            else if (item.getItemId() == R.id.segments) {
                replaceFragment(new SegmentsFragment());
            }
            else if (item.getItemId() == R.id.upload) {
                replaceFragment(new UploadFragment());
            }


            return true;
        });
        try {
            testConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

    public void showNotification(String title, String content) {
        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            String channelName = "Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_stats_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    private void testConnection() throws IOException {
        new Thread(() -> {
            Client client = new Client("user1", "10.26.61.204", 54321);
            try {
                client.getResultsFromServer();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            appendResults(client.getResults());
            updateStatistics(client.getStatistics());
                // Notifies the user that the results are ready.
            showNotification("Results are ready", "Go to the results page to see them.");
        }).start();
    }

    public List<Result> getResults() {
        return results;
    }

    public void appendResults(List<Result> results) {
        this.results.addAll(results);
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void updateStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public final List<MySegment> getSegments() {
        return statistics.getSegments();
    }

}