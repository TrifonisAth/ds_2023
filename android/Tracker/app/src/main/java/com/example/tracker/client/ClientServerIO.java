package com.example.tracker.client;

import com.example.tracker.model.Bundle;
import com.example.tracker.model.Result;
import com.example.tracker.model.Route;
import com.example.tracker.model.Statistics;
import com.example.tracker.model.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class ClientServerIO {
    private final Client client;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final List<Route> files = new ArrayList<>();

    public ClientServerIO(Client client) throws IOException {
        this.client = client;
        boolean connected = false;
        while (!connected) {
            try {
                socket = new Socket(client.getServerIp(), client.getServerPort());
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                connected = true;
            } catch (IOException ignored) {
            }
        }
    }

    public void upload() throws IOException, ClassNotFoundException {
        out.writeObject("/put");
        byte[] buffer;
        BufferedInputStream stream;
        /*
        for (String filename : client.getFilenames()) {
            file = new File(filename);
            stream = new BufferedInputStream(new FileInputStream(file));
            buffer = new byte[(int) file.length()];
            stream.read(buffer, 0, buffer.length);
            files.add(new Route(filename, buffer));
        }
         */
        for (File file : client.getFiles()) {
            stream = new BufferedInputStream(Files.newInputStream(file.toPath()));
            buffer = new byte[(int) file.length()];
            stream.read(buffer, 0, buffer.length);
            files.add(new Route(file.getName(), buffer));
        }
        Bundle bundle = new Bundle(files, client.getUsername());
        out.writeObject(bundle);
        out.flush();
        Result result;
        for (int i = 0; i < files.size(); i++) {
            result = (Result) in.readObject();
            System.out.println(result);
            client.store(new Result(result));
        }
        Statistics statistics = (Statistics) in.readObject();
        client.setStatistics(new Statistics(statistics));
        System.out.println(statistics);
        close();
    }

    public void getRequest() throws IOException, ClassNotFoundException {
        out.writeObject("/get");
        out.writeObject(client.getUsername());
        Object o = in.readObject();
        if (o instanceof User){
            User user = (User) o;
            List<Result> results = user.getResults();
            for (Result result : results) {
                client.store(new Result(result));
            }
            Statistics stats = (Statistics) in.readObject();
            client.setStatistics(new Statistics(stats));
        }
        close();
    }

    private void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

}
