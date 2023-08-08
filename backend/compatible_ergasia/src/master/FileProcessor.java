package master;

import com.example.tracker.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a seperate thread that is responsible for processing
 * a file of the client. The file gets parsed and chunkified and then
 * the chunks are sent to the workers. The intermediate results are
 * stored in a list and then sent back to the clientHandler.
 */
public class FileProcessor extends Thread {
    private final List<IntermediateResult> intermediateResults = new ArrayList<>();
    private final List<Pair<Integer, List<IntermediateResult>>> clientIres;
    private final List<Waypoint> waypoints;
    private static int activityId = 0;
    private final int id = nextActivityId();

    public FileProcessor(Route file, List<Pair<Integer, List<IntermediateResult>>> clientIres) {
        this.waypoints = new ArrayList<>(Parser.parse(file));
        this.clientIres = clientIres;
    }

    private void map() throws IOException, ClassNotFoundException {
        List<Chunk> chunks = chunkify();
        for (Chunk chunk : chunks) {
            sendChunk(chunk);
        }
        checkSegments();
        // Append the list of intermediate results to the clientHandler.
        Pair<Integer, List<IntermediateResult>> pair = new Pair<>(id, intermediateResults);
        synchronized (clientIres) {
            clientIres.add(pair);
            clientIres.notify();
        }
    }

    private synchronized int nextActivityId() {
        return ++activityId;
    }

    /**
     * Splits the waypoint list into chunks.
     */
    private List<Chunk> chunkify() {
        List<Chunk> chunks = new ArrayList<>();
        int index = 0;
        int chunkIndex = 1;
        int chunkSize = ClientHandler.getChunkSize();
        while (waypoints.size() > index){
            if (chunkSize > waypoints.size() - index)
                chunkSize = waypoints.size() - index;
            List<Waypoint> ls = new ArrayList<>();
            for (int i = 0; i < chunkSize-1; i++) {
                ls.add(waypoints.get(index++));
            }
            // Add the last waypoint to the chunk.
            if (waypoints.size() == index + 1)
                ls.add(waypoints.get(index++));
            else if (waypoints.size() > index + 1)
                ls.add(waypoints.get(index));
            chunks.add(new Chunk(ls, chunkIndex++));
        }
        return chunks;
    }

    private void sendChunk(Chunk chunk) throws IOException, ClassNotFoundException {
        int workerIndex = ClientHandler.nextWorkerIndex();
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        // Find the worker address, and then use the worker port to connect to it.
        boolean sent = false;
        while (!sent) {
            try {
                socket = new Socket(Master.getWorkerAddresses().get(workerIndex), Master.getWorkerPort());
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                sent = true;
            } catch (IOException e) {
                // You could try to send to the same worker instead.
                workerIndex = ClientHandler.nextWorkerIndex();
            }
        }
        out.writeObject(chunk);
        out.flush();
        IntermediateResult iRes = (IntermediateResult) in.readObject();
        intermediateResults.add(new IntermediateResult(iRes));
        in.close();
        out.close();
        socket.close();
    }

    private void checkSegments() {
        for (MySegment segment : Master.getSegments()) {
            if (segment.getWaypoints().size() == 0 || segment.getWaypoints().size() > waypoints.size()) {
                continue;
            }
            int size = segment.getWaypoints().size();
            int index = 0;
            int j = 0;
            for (Waypoint waypoint : waypoints) {
                if (equalsWithinTolerance(waypoint, segment.getWaypoints().get(index))) {
                    index++;
                } else {
                    index = 0;
                }
                if (index == size) {
                    int duration = (int)
                            (waypoint.getTime().toEpochSecond(ZoneOffset.UTC)
                            - waypoints.get(j-size+1).getTime().toEpochSecond(ZoneOffset.UTC));
                    synchronized (Master.getSegments()){
                    segment.append(waypoint.getCreator()+ " @"+ waypoint.getFileName(), duration);
                    }
                    break;
                }
                j++;
            }
        }
    }

    private boolean equalsWithinTolerance(Waypoint a, Waypoint b) {
        // 0.0001 is the tolerance. This is about 10 meters near the equator.
        return Math.abs(a.getLatitude() - b.getLatitude()) < 0.0001 &&
                Math.abs(a.getLongitude() - b.getLongitude()) < 0.0001;
    }

    @Override
    public void run() {
        try {
            map();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
