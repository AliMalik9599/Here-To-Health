

import exceptions.InsertionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Iterator;

public final class StreetSearcher {
    
    // useful for marking distance to nodes
    private static final double MAX_DISTANCE = 1e18;

    // Global variables
    private static Map<String, Vertex<String>> vertices = new HashMap<>();
    private static SparseGraph<String, String> graph = new SparseGraph<>();

    // Silencing checkstyle
    private StreetSearcher() {}

    /**
     * Get path between two points.
     */
    private static List<Edge<String>> getPath(Vertex<String> end,
                                              Vertex<String> start) {
        if (graph.label(end) != null) {
            List<Edge<String>> path = new ArrayList<>();

            Vertex<String> cur = end;
            Edge<String> road;
            while (cur != start) {
                road = (Edge<String>) graph.label(cur);  // unchecked cast 
                path.add(road);
                cur = graph.from(road);
            }
            return path;
        }
        return null;
    }

    /**
     * Print the path found.
     */
    private static void printPath(List<Edge<String>> path,
                                  double totalDistance) {
        if (path == null) {
            System.out.println("No path found");
            return;
        }

        System.out.println("Total Distance: " + totalDistance);
        for (int i = path.size() - 1; i >= 0; i--) {
            System.out.println(path.get(i).get() + " "
                               + graph.label(path.get(i)));
        }
    }


    /**
     * Using dijkstras algorithm to find the shortest path between
     * two nodes.
     */
    private static void findShortestPath(String startName, String endName) {
        Vertex<String> start = vertices.get(startName);
        Vertex<String> end = vertices.get(endName);

        double totalDist = -1;

        //min PQ to hold the vertices based on distance for order.
        PriorityQueue<Vertex<String>> minPQ =
                new PriorityQueue<Vertex<String>>();

        Iterable<Vertex<String>> make = graph.vertices();
        Iterator<Vertex<String>> it = make.iterator();

        while (it.hasNext()) {
            Vertex<String> check = it.next();
            String s = graph.getData(check);
            //start node should be at start of PQ.
            if (s.equals(startName)) {
                graph.putDist(check, 0);
                minPQ.add(check);
            } else {
                graph.putDist(check, MAX_DISTANCE);
                minPQ.add(check);
            }

        }

        while (!minPQ.isEmpty()) {
            Vertex<String> cur = minPQ.poll();
            //break loop since have reached end.
            if (cur.equals(end)) {
                break;
            }
            Iterable<Edge<String>> build = graph.outgoing(cur);
            Iterator<Edge<String>> its = build.iterator();

            //Traverse through all nodes connected to this.
            while (its.hasNext()) {
                Edge<String> e = its.next();
                Vertex<String> to = graph.to(e);
                if (minPQ.contains(to)) {
                    double d = graph.getDist(cur) + (double) graph.label(e);
                    //If a shorter path to the node is found through cur
                    //than its current distance.
                    if (d < graph.getDist(to)) {
                        //change label and dist and update in PQ
                        graph.label(to, e);
                        graph.putDist(to, d);
                        minPQ.remove(to);
                        minPQ.add(to);
                    }
                }
            }
        }

        totalDist = graph.getDist(end);
        // These method calls will create and print the path for you
        List<Edge<String>> path = getPath(end, start);
        printPath(path, totalDist);
    }


    /**
     * Add an end point to the network if it is
     * a new end point.
     */
    private static Vertex<String> addLocation(String name) {
        if (!vertices.containsKey(name)) {
            Vertex<String> v = graph.insert(name);
            vertices.put(name, v);
            return v;
        }
        return vertices.get(name);
    }


    /**
     * Method to load a data from a file
     * and insert it into the graph.
     */
    private static int loadNetwork(String fileName)
            throws FileNotFoundException {

        int numRoads = 0;

        // Read in from file fileName
        Scanner input = new Scanner(new FileInputStream(new File(fileName)));


        while (input.hasNext()) {

            // Parse the line in to <end1> <end2> <road-distance> <road-name>
            String[] tokens = input.nextLine().split(" ");
            String fromName = tokens[0];
            String toName = tokens[1];
            double roadDistance = Double.parseDouble(tokens[2]);
            String roadName = tokens[3];

            // Get the from and to endpoints, adding if necessary
            Vertex<String> from = addLocation(fromName);
            Vertex<String> to =  addLocation(toName);

            // Add the road to the network
            try {

                Edge<String> road = graph.insert(from, to, roadName);
                Edge<String> backwardsRoad = graph.insert(to, from, roadName);
                numRoads += 2;

                // Label each road with it's weight
                graph.label(road, roadDistance);
                graph.label(backwardsRoad, roadDistance);

            } catch (InsertionException ignored) {
                // Nothing to do.
            }
        }

        return numRoads;
    }


    /**
     * Method to check that the endpoint of
     * a path call is in the graph.
     */
    private static void checkValidEndpoint(String endpointName) {
        if (!vertices.containsKey(endpointName)) {
            throw new IllegalArgumentException(endpointName);
        }
    }

    /**
     * Main method.
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: " +
                    "StreetSearcher <map_name> <start_coords> <end_coords>");
            return;
        }

        String fileName  = args[0];
        String startName = args[1];
        String endName   = args[2];

        try {

            int numRoads = loadNetwork(fileName);
            System.out.println("Network Loaded!");
            System.out.println("Loaded " + numRoads + " roads");
            System.out.println("Loaded " + vertices.size() + " endpoints");

            checkValidEndpoint(startName);
            checkValidEndpoint(endName);

        } catch (FileNotFoundException e) {
            System.err.println("Could not find file " + fileName);
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Endpoint: " + e.getMessage());
            return;
        }

        findShortestPath(startName, endName);
    }
}
