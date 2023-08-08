package com.example.tracker.client;

import android.os.Environment;

import com.example.tracker.model.Result;
import com.example.tracker.model.Statistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Client which in our case is
 * an android device. The client is responsible for
 * sending the routes to the server and receiving the
 * results. The client is also responsible for storing
 * the results and the statistics of the user.
 */
public class Client {
    private Statistics statistics;
    private final String SERVER_IP;
    private final int SERVER_PORT;
    private final String username;
    private final List<Result> results = new ArrayList<>();
    private final List<File> files = new ArrayList<>();

    public Client(String username, String address, int port, List<File> files) {
        this.username = username;
        this.SERVER_IP = address;
        this.SERVER_PORT = port;
        this.files.addAll(files);
    }

    public Client(String name, String address, int port) {
        this.username = name;
        this.SERVER_IP = address;
        this.SERVER_PORT = port;
    }

    /**
     * The ClientServerIOThread is calling this method to store
     * the result of a route inside the Client's main thread code.
     */
    public void store(Result result) {
        results.add(new Result(result));
    }

    /**
     * The ClientServerIOThread is calling this method to set
     * the statistics of the user inside the Client's main thread code.
     */
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public List<Result> getResults() {
        return results;
    }

    public String getUsername() {
        return username;
    }

//    public List<String> getFilenames() {
//        return filenames;
//    }

    public List<File> getFiles(){
        return this.files;
    }

    /**
     * This method is called by the Client's main thread code
     * to send the files to the server, by creating a new thread
     * which will handle the communication with the server.
     */
    public void sendFiles() throws IOException, ClassNotFoundException {
        ClientServerIO cl = new ClientServerIO(this);
        cl.upload();
    }

    public void getResultsFromServer() throws IOException, ClassNotFoundException {
        ClientServerIO cl = new ClientServerIO(this);
        cl.getRequest();
    }

    public String getServerIp() {
        return SERVER_IP;
    }

    public int getServerPort() {
        return SERVER_PORT;
    }

}
