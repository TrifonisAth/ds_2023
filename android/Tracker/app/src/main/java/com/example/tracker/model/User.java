package com.example.tracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private final String name;
    private double totalDistance;
    private double totalDuration;
    private double totalElevationGain;
    private final List<Result> routes;

    public User(String name) {
        this.name = name;
        this.totalDistance = 0;
        this.totalDuration = 0;
        this.totalElevationGain = 0;
        this.routes = new ArrayList<>();
    }

    public User(User other){
        this.name = other.name;
        this.totalDistance = other.totalDistance;
        this.totalDuration = other.totalDuration;
        this.totalElevationGain = other.totalElevationGain;
        this.routes = new ArrayList<>(other.routes);
    }

    public void addRoute(Result res) {
        this.totalDistance += res.getDistance();
        this.totalDuration += res.getDuration();
        this.totalElevationGain += res.getElevationGain();
        routes.add(new Result(res));
    }

    public String getName() {
        return name;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public double getTotalElevationGain() {
        return totalElevationGain;
    }

    public int getRoutesCompleted() {
        return routes.size();
    }

    public List<Result> getResults() {
        return routes;
    }

}
