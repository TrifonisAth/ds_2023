package master;

import com.example.tracker.model.MySegment;
import com.example.tracker.model.Route;
import com.example.tracker.model.UsersData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Master {
    private static final List<MySegment> segments = new ArrayList<>();
    private static final UsersData usersData = new UsersData(segments);
    private static List<String> workerAddresses;
    private final ServerSocket clientSocket;
    private static int workerPort;

    public Master(List<String> workerAddresses, int workerPort, int clientPort, int chunkSize) throws IOException {
        Master.workerAddresses = new ArrayList<>(workerAddresses);
        Master.workerPort = workerPort;
        ClientHandler.setChunkSize(chunkSize);
        this.clientSocket = new ServerSocket(clientPort);
        System.out.println("Master-> Initialized on port " + clientPort);
    }

    private void openServer() {
        while (true) {
            try {
                Socket clientConnection = clientSocket.accept();
                new ClientHandler(clientConnection).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<MySegment> getSegments() {
        return segments;
    }

    private void parseSegments() throws IOException {
        byte[] buffer;
        BufferedInputStream stream;
        File[] directory = new File("resources/segments").listFiles();
        if (directory == null) {
            System.out.println("Master-> No segments found.");
            return;
        }
        for (File file : directory) {
            System.out.println("Master-> Parsing segment: " + file.getName());
            stream = new BufferedInputStream(Files.newInputStream(file.toPath()));
            buffer = new byte[(int) file.length()];
            stream.read(buffer, 0, buffer.length);
            Route route = new Route(file.getName(), buffer);
            synchronized (segments){
                this.segments.add(new MySegment(file.getName(), new ArrayList<>(Parser.parse(route))));
            }
        }
    }

    public static int getWorkerPort() {
        return workerPort;
    }

    public static UsersData getUsersData() {
        return usersData;
    }

    public static List<String> getWorkerAddresses() {
        return workerAddresses;
    }

    public static void main(String[] args) throws IOException {
        // Create list of worker addresses.
        List<String> workerAddresses = new ArrayList<>();
        /*
            Use add method to add the IP addresses of the workers.
            For example:
            workerAddresses.add("192.168.2.11");
            workerAddresses.add("192.168.2.12");
         */
        workerAddresses.add("127.0.0.1");   // set according to Worker's IP.
        // Create master.
        Master master = new Master(workerAddresses, 12345, 54321, 12);
        master.parseSegments();
        // Open server.
        master.openServer();
    }
}
