package com.example.tracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class MySegment implements Serializable {
    private final List<Waypoint> waypoints;
    private final String name;
    private final List<Pair<String, Integer>> matchingActivities = new ArrayList<>();

public MySegment(String name, List<Waypoint> waypoints) {
        this.waypoints = new ArrayList<>(waypoints);
        this.name = name;
    }

    public int getMatchingActivities(){
        return matchingActivities.size();
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void append(String s, int duration) {
        matchingActivities.add(new Pair<>(s, duration));
    }

    public String getFileName() {
        return name;
    }

    public static String strDuration(int duration){
        return duration/3600+"h "+(duration%3600)/60+"m "+duration%60+"s";
    }

    public Stream<Pair<String, Integer>> getLeaderBoard() {
        return matchingActivities.stream().sorted(Comparator.comparingInt(Pair::getSecond));
    }
}
